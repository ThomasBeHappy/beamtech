package com.gamingframe.beamtech.block.custom.entity;

import com.gamingframe.beamtech.BeamTech;
import com.gamingframe.beamtech.block.ModBlockEntities;
import com.gamingframe.beamtech.effects.FlashBlindnessEffect;
import com.gamingframe.beamtech.effects.ModEffects;
import com.gamingframe.beamtech.interfaces.IEmitter;
import com.gamingframe.beamtech.interfaces.ILaserCraftingRecipe;
import com.gamingframe.beamtech.interfaces.ILaserInteractable;
import com.gamingframe.beamtech.interfaces.ImplementedInventory;
import com.gamingframe.beamtech.recipes.Recipes;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LaserFocuserBlockEntity extends BlockEntity implements ImplementedInventory, ILaserInteractable {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private IEmitter emitter;
    protected final PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = 100;

    public LaserFocuserBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LASER_FOCUSER_BLOCK_ENTITY, pos, state);
        propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> LaserFocuserBlockEntity.this.progress;
                    case 1 -> LaserFocuserBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> LaserFocuserBlockEntity.this.progress = value;
                    case 1 -> LaserFocuserBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int size() {
                return 2;
            }
        };
    }

    @Override
    public void onHit(IEmitter emitter, Vec3d direction) {
        this.emitter = emitter;
    }

    @Override
    public void onNoLongerHit(IEmitter emitter) {
        this.emitter = null;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt("laser_oven_block.progress", progress);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        progress = nbt.getInt("laser_oven_block.progress");
    }

    private void resetProgress() {
        this.progress = 0;
    }

    private boolean hasCraftingFinished() {
        return progress >= maxProgress;
    }

    private void increaseCraftProgress() {
        progress++;
    }

    // TODO: Atm this crafts regardless of power (client side) which causes desync issues. Needs to be fixed
    public void tick(World world, BlockPos pos, BlockState state) {
        for (PlayerEntity entity : world.getPlayers()) {
            // TODO: check if player is wearing goggles
            if (entity.getPos().distanceTo(pos.toCenterPos()) < 5) {
                entity.addStatusEffect(new StatusEffectInstance(
                        ModEffects.FLASH_BLINDNESS_EFFECT,
                        6000,
                        0));
            }
        }

        if (this.emitter == null || this.getStack(0).isOf(Items.AIR)) return;

        ItemStack item = this.getStack(0);

        if (Recipes.isValidRecipe(item.getItem())) {

            ILaserCraftingRecipe recipe = Recipes.getRecipe(item.getItem());

            if (recipe.getMinPowerNeeded() < emitter.getPower())  {
                resetProgress();
                return;
            }

            maxProgress = recipe.getTimeNeeded();

            if (!hasCraftingFinished()) {
                increaseCraftProgress();
            }else {
                Item output = recipe.getOutput();
                ItemStack outputStack = new ItemStack(output, item.getCount());

                this.setStack(0, outputStack);
                resetProgress();
            }
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public void markDirty() {
        if (world.isClient) return;

        world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        world.getServer().getWorld(world.getRegistryKey()).getChunkManager().markForUpdate(this.getPos());
    }
}
