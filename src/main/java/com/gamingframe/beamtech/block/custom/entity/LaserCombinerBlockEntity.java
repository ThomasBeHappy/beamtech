package com.gamingframe.beamtech.block.custom.entity;

import com.gamingframe.beamtech.BeamTech;
import com.gamingframe.beamtech.block.ModBlockEntities;
import com.gamingframe.beamtech.block.custom.LaserBlock;
import com.gamingframe.beamtech.block.custom.MirrorBlock;
import com.gamingframe.beamtech.interfaces.EmitterInventory;
import com.gamingframe.beamtech.interfaces.IEmitter;
import com.gamingframe.beamtech.interfaces.ILaserInteractable;
import com.gamingframe.beamtech.item.ModItems;
import com.gamingframe.beamtech.raycasting.LaserRayCastContext;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import team.lodestar.lodestone.registry.common.particle.*;
import team.lodestar.lodestone.systems.easing.*;
import team.lodestar.lodestone.systems.particle.builder.*;
import team.lodestar.lodestone.systems.particle.data.*;
import team.lodestar.lodestone.systems.particle.data.color.*;
import team.lodestar.lodestone.systems.particle.data.spin.*;

import java.util.ArrayList;
import java.util.List;
import java.awt.Color;
import java.util.concurrent.atomic.AtomicReference;

public class LaserCombinerBlockEntity extends BlockEntity implements IEmitter, ILaserInteractable, EmitterInventory {
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private static final int MAX_RANGE = 50; // Max range for the laser
    private static final int MAX_REFLECTIONS = 100;
    private int currentReflections = 0;
    public List<IEmitter> emitters = new ArrayList<>();
    public int power = 0;
    public ILaserInteractable registeredLaserInteractable;

    public LaserCombinerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LASER_COMBINER_BLOCK_ENTITY, pos, state);
    }

    public static void tick(World world1, BlockPos pos, BlockState state1, LaserCombinerBlockEntity be) {
        be.power = 0;

        for (int i = 0; i < be.emitters.size(); i++) {

            be.power += be.emitters.get(i).getPower();
        }


        if (be.emitters.size() >= 2) {
            Direction direction = state1.get(LaserBlock.FACING);
            Vec3d startPos = Vec3d.ofCenter(pos).add(Vec3d.of(direction.getVector()).multiply(0.5));
            be.shootLaser(be.world, startPos, Vec3d.of(direction.getVector()), MAX_RANGE, be);
        }else if (be.registeredLaserInteractable != null){
            be.registeredLaserInteractable.onNoLongerHit(be);
            be.registeredLaserInteractable = null;
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

        ItemStack toDrop = getStack(0);
        if (!toDrop.isOf(Items.AIR)) {
            ItemScatterer.spawn(world, this.pos, getItems());
        }

        super.markRemoved();
    }

    @Override
    public int getPower() {
        return power;
    }

    Vec3d lastHitMirror;

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

        // Calculate the distance to the hit position
        double distance = startPos.distanceTo(hitResult.getPos());

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

        if (world.isClient) {
            // Render the laser up to the hit point
            renderLaser(world, startPos, hitResult.getPos());
        }

        if (world.getBlockEntity(hitPos) instanceof ILaserInteractable laserCombinerBlock) {
            registeredLaserInteractable = laserCombinerBlock;
            laserCombinerBlock.onHit(this, direction);
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
        }else if (power >= 10 && !hitState.isAir()) {
            if (world.getTime() % 20 != 0) return;
            vaporizeBlocks(world, hitPos, hitResult.getPos(), direction, power/10);
        }
    }

    @Override
    public void renderLaser(World world, Vec3d startPos, Vec3d endPos) {
        double distance = startPos.distanceTo(endPos);
        Vec3d direction = endPos.subtract(startPos).normalize();

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
                .setLifetime(10)
                .enableNoClip();

        for (double i = 0; i < distance; i += 0.05) {
            Vec3d currentPos = startPos.add(direction.multiply(i));
            builderOuter.spawn(world, currentPos.x, currentPos.y, currentPos.z);
            builderCenter.spawn(world, currentPos.x, currentPos.y, currentPos.z);
        }

    }

    public void vaporizeBlocks(World world, BlockPos centerPos, Vec3d hitPos, Vec3d direction, int radius) {

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx * dx + dy * dy + dz * dz <= radius * radius) {
                        Vec3d offset = new Vec3d(dx, dy, dz);
                        Vec3d normalizedDirection = direction.normalize();

                        // Project the offset onto the direction vector
                        double dotProduct = offset.dotProduct(normalizedDirection);
                        Vec3d projection = normalizedDirection.multiply(dotProduct);

                        // Get the perpendicular component
                        Vec3d alignedOffset = offset.subtract(projection);

                        BlockPos targetPos = centerPos.add((int) alignedOffset.x, (int) alignedOffset.y, (int) alignedOffset.z);

                        // Ensure targetPos is within bounds
                        if (world.isInBuildLimit(targetPos)) {
                            if (!world.isClient) {
                                world.removeBlock(targetPos, false);
                            }

                            Color startingColor = new Color(50, 50, 50);

                            if (world.isClient) {
                                var smoke = WorldParticleBuilder.create(LodestoneParticleRegistry.SMOKE_PARTICLE).setScaleData(GenericParticleData.create(0.10f * (power / 4f) / 1.5f).build())
                                        .setTransparencyData(GenericParticleData.create(0.8f).build())
                                        .setColorData(ColorParticleData.create(startingColor).build())
                                        .setSpinData(SpinParticleData.create(0.2f, 0.4f).setSpinOffset((world.getTime() * 0.2f) % 6.28f).setEasing(Easing.QUARTIC_IN).build())
                                        .setLifetime(40)
                                        .addMotion(0,0.2,0)
                                        .enableNoClip();

                                smoke.spawn(world, targetPos.getX(), targetPos.getY(), targetPos.getZ());
                            }
                        }
                    }
                }
            }
        }

        world.playSound(null, centerPos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 1f, 1f);
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
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, items);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
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
                ServerPlayNetworking.send(serverPlayerEntity, new Identifier(BeamTech.MOD_ID, "combiner_update_laser_lens"), buf);
            });
        }
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public void onHit(IEmitter emitter, Vec3d direction) {
        if (!emitters.contains(emitter)) {
            emitters.add(emitter);
        }
    }

    @Override
    public void onNoLongerHit(IEmitter emitter) {
        emitters.remove(emitter);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    // TODO: make more lenses for the combiner to use.
    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return EmitterInventory.super.isValid(slot, stack);
    }
}
