package com.gamingframe.beamtech.block.custom.entity;

import com.gamingframe.beamtech.BeamTech;
import com.gamingframe.beamtech.block.ModBlockEntities;
import com.gamingframe.beamtech.block.custom.LaserBlock;
import com.gamingframe.beamtech.block.custom.LaserCombinerBlock;
import com.gamingframe.beamtech.block.custom.MirrorBlock;
import com.gamingframe.beamtech.classes.LaserPositions;
import com.gamingframe.beamtech.interfaces.EmitterInventory;
import com.gamingframe.beamtech.interfaces.IEmitter;
import com.gamingframe.beamtech.interfaces.ILaserInteractable;
import com.gamingframe.beamtech.item.ModItems;
import com.gamingframe.beamtech.raycasting.LaserRayCastContext;
import com.gamingframe.beamtech.screens.EmitterGUI;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import team.lodestar.lodestone.registry.common.particle.LodestoneParticleRegistry;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;
import team.lodestar.lodestone.systems.particle.data.spin.SpinParticleData;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.awt.*;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class LaserBlockEntity extends BlockEntity implements IEmitter, NamedScreenHandlerFactory, EmitterInventory {
    private int max_range = 50; // Max range for the laser
    private int power = 5;
    private ILaserInteractable registeredLaserInteractable;
    private static final int MAX_REFLECTIONS = 100;
    private int currentReflections = 0;
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);
    public final SimpleEnergyStorage energyStorage = new SimpleEnergyStorage(100000, 10, 0) {
        @Override
        protected void onFinalCommit() {
            markDirty();
        }
    };
    public List<LaserPositions> rayCastPositions;
    Vec3d lastHitMirror;


    public LaserBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LASER_EMITTER_BLOCK_ENTITY, pos, state);
        BeamTech.LOGGER.info("Created Block Entity at {} for {} ", pos, BeamTech.MOD_ID);
        energyStorage.amount = 100000;
    }

    public static void tick(World world, BlockPos pos, BlockState state, LaserBlockEntity be) {
        if (be.world != null && be.world.isReceivingRedstonePower(be.pos)) {
            if (be.energyStorage.amount >= 100) {
                be.energyStorage.amount -= 100;
                be.markDirty();
            }else {
                if (be.registeredLaserInteractable != null) {
                    be.registeredLaserInteractable.onNoLongerHit(be);
                    be.registeredLaserInteractable = null;
                }
                return;
            }
            Direction direction = state.get(LaserBlock.FACING);
            Vec3d startPos = Vec3d.ofCenter(pos).add(Vec3d.of(direction.getVector()).multiply(0.20));
            ItemStack item = be.getStack(0);
            if (item.isOf(ModItems.FOCAL_LENS)) {
                //BeamTech.LOGGER.info("FOCAL BITCH");
                be.power = 10;
                be.max_range = 25;
            }else if (item.isOf(ModItems.RANGE_LENS)) {
               // BeamTech.LOGGER.info("RANGE BITCH");
                be.power = 2;
                be.max_range = 100;
            }else {
                be.power = 5;
                be.max_range = 50;
            }
            be.shootLaser(be.world, startPos, Vec3d.of(direction.getVector()), be.max_range, be);
        }else {
            if (be.registeredLaserInteractable != null) {
                be.registeredLaserInteractable.onNoLongerHit(be);
                be.registeredLaserInteractable = null;
            }
        }
        be.lastHitMirror = null;
        be.currentReflections = 0;
    }

    @Override
    public void markRemoved() {
        if (registeredLaserInteractable != null) {
            registeredLaserInteractable.onNoLongerHit(this);
            registeredLaserInteractable = null;
        }
        super.markRemoved();
    }


    @Override
    public void shootLaser(World world, Vec3d startPos, Vec3d direction, int remainingRange, BlockEntity source) {
        if (remainingRange <= 0) return;

        Vec3d endPos = startPos.add(direction.multiply(remainingRange));

        BlockHitResult hitResult = world.raycast(new LaserRayCastContext(
                startPos,
                endPos,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                source, lastHitMirror));

        BlockPos hitPos = hitResult.getBlockPos();
        BlockState hitState = world.getBlockState(hitPos);


        if (!world.isClient) {
            if (world.getTime() % 10 == 0) {
                Box box = new Box(startPos, hitResult.getPos()).expand(0.5);

                List<Entity> entities = world.getOtherEntities(null, box);

                for (Entity entity : entities) {
                    Box entityBox = entity.getBoundingBox().expand(0.3);

                    if (entityBox.raycast(startPos, endPos).isPresent()) {
                        if (entity instanceof ItemEntity item) {
                            ItemStack stack = item.getStack();
                            if (stack.getItem() == Items.SAND) {
                                ItemStack superTreatedGlass = new ItemStack(ModItems.SUPERTREATED_GLASS, stack.getCount());
                                item.setStack(superTreatedGlass);
                            }
                        } else {
                            entity.damage(entity.getDamageSources().generic(), (float) power / 2);
                        }
                    }
                }
            }
        }

        // Calculate the distance to the hit position
        double distance = startPos.distanceTo(hitResult.getPos());

        if (world.isClient) {
            renderLaser(world, startPos, hitResult.getPos());
        }
        // Render the laser up to the hit point

        if (world.getBlockEntity(hitPos) instanceof ILaserInteractable be) {
            registeredLaserInteractable = be;
            registeredLaserInteractable.onHit(this, direction);
        }else if (hitState.getBlock() instanceof MirrorBlock mirror) {
            if (currentReflections >= MAX_REFLECTIONS) {
                return;
            }

            lastHitMirror = hitPos.toCenterPos();

            Vec3d newDirection = mirror.reflectLaser(hitState, direction);

            AtomicReference<Vec3d> newPos = new AtomicReference<Vec3d>();

            int result = mirror.getNewStartPos(hitState, hitPos, hitResult.getPos(), direction, newPos);

            if (result == 0) {
                shootLaser(world, hitResult.getPos(), direction, remainingRange - (int)distance, null);
                return;
            }else if (result == -1) {
                return;
            }

            if (world.isClient) {
                renderLaser(world, hitResult.getPos(), newPos.get());
            }

            if (newDirection.distanceTo(direction) <= 0.1) {
                return;
            }

            currentReflections++;
            // Emit laser in the new direction, subtracting the distance already traveled
            shootLaser(world, newPos.get(), newDirection, remainingRange - (int)distance, null);
        }else if (registeredLaserInteractable != null) {
            registeredLaserInteractable.onNoLongerHit(this);
            registeredLaserInteractable = null;
        }
    }

    @Override
    public void renderLaser(World world, Vec3d startPos, Vec3d endPos) {
        double distance = startPos.distanceTo(endPos);
        Vec3d direction = endPos.subtract(startPos).normalize();
//        if (world.isClient()) {
//            BeamTech.LOGGER.info("Client: power: {} at location {} with end {}", power, startPos, endPos);
//        }else {
//            BeamTech.LOGGER.info("Server: power: {} at location {} with end {}", power, startPos, endPos);
//        }

        Color startingColor = new Color(255, 0, 0);
        Color centerColor = new Color(255, 142, 142);

        var builderOuter = WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE).setScaleData(GenericParticleData.create(0.10f * (power / 4f) / 1.5f).build())
                .setTransparencyData(GenericParticleData.create(0.75f).build())
                .setColorData(ColorParticleData.create(startingColor).build())
                .setSpinData(SpinParticleData.create(0.2f, 0.4f).setSpinOffset((world.getTime() * 0.2f) % 6.28f).setEasing(Easing.QUARTIC_IN).build())
                .setLifetime(10)
                .enableNoClip();

        var builderCenter = WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE).setScaleData(GenericParticleData.create(0.05f * (power / 4f) / 1.5f).build())
                .setTransparencyData(GenericParticleData.create(1f).build())
                .setColorData(ColorParticleData.create(centerColor).build())
                .setSpinData(SpinParticleData.create(0.2f, 0.4f).setSpinOffset((world.getTime() * 0.2f) % 6.28f).setEasing(Easing.QUARTIC_IN).build())
                .setLifetime(8)
                .enableNoClip();

        for (double i = 0; i < distance; i += 0.05) {
            Vec3d currentPos = startPos.add(direction.multiply(i));
            builderOuter.spawn(world, currentPos.x, currentPos.y, currentPos.z);
            builderCenter.spawn(world, currentPos.x, currentPos.y, currentPos.z);
        }
    }

    public void syncInventory(ItemStack itemStack) {
        if (this.world.isClient) {
            this.setStack(0, itemStack);
        }
    }


    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public int getPower() {
        return power;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inventory, PlayerEntity player) {
        return new EmitterGUI(syncId, inventory, ScreenHandlerContext.create(world, pos));
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, items);
        energyStorage.amount = nbt.getLong("energy");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putLong("energy", energyStorage.amount);
        Inventories.writeNbt(nbt, items);
        super.writeNbt(nbt);
    }

    @Override
    public void markDirty() {
        world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        if (!this.world.isClient) {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeBlockPos(this.pos);
            buf.writeItemStack(this.getStack(0));

            PlayerLookup.tracking(this).forEach((serverPlayerEntity) -> {
                ServerPlayNetworking.send(serverPlayerEntity, new Identifier(BeamTech.MOD_ID, "update_laser_lens"), buf);
            });
        }
    }

        @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}
