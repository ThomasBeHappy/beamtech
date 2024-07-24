package com.gamingframe.beamtech.entities.ai;

import com.gamingframe.beamtech.BeamTech;
import com.gamingframe.beamtech.entities.custom.CleanbotEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class DepositItemsGoal extends Goal {

    protected CleanbotEntity mob;
    protected Block depositBlock = Blocks.HOPPER;
    protected BlockPos depositBlockPos;

    public DepositItemsGoal(CleanbotEntity mob) {
        this.mob = mob;
    }

    @Override
    public boolean canStart() {
        if (!this.mob.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()) {
            depositBlockPos = locateClosestDepositBlock(this.mob.getWorld(), this.mob, depositBlock, 25);
            BeamTech.LOGGER.info("blockpos = {}", depositBlockPos);
            return depositBlockPos != null;
        }
        return false;
    }

    @Override
    public void start() {
        this.mob.setDepositing(true);
        this.mob.getNavigation().startMovingTo(depositBlockPos.getX(), depositBlockPos.getY(), depositBlockPos.getZ(), 1.5f);
    }

    @Override
    public void stop() {
        this.mob.setDepositing(false);
        this.mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        ItemStack itemStack = this.mob.getEquippedStack(EquipmentSlot.MAINHAND);
        if (!itemStack.isEmpty() && this.mob.getPos().isInRange(depositBlockPos.toCenterPos(), 2)) {
            spitOutItemTowardsBlock(itemStack, depositBlockPos);
            this.mob.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }
        if (this.mob.getNavigation().isIdle()) {
            this.mob.getNavigation().startMovingTo(depositBlockPos.getX(), depositBlockPos.getY(), depositBlockPos.getZ(), 1.5f);
        }
    }

    protected BlockPos locateClosestDepositBlock(World world, Entity entity, Block block, int range) {
        BlockPos blockPos = entity.getBlockPos();

        return BlockPos.findClosest(blockPos, range, range, (pos) ->
                world.getBlockState(pos).getBlock() == block).orElse(null);
    }

    private void spitOutItemTowardsBlock(ItemStack stack, BlockPos blockPos) {
        if (!stack.isEmpty()) {
            double d = this.mob.getEyeY() - 0.3;
            ItemEntity itemEntity = new ItemEntity(this.mob.getWorld(), this.mob.getX(), d, this.mob.getZ(), stack);
            itemEntity.setPickupDelay(40);
            itemEntity.setThrower(this.mob.getUuid());
            itemEntity.setVelocity((blockPos.getX() + 0.5 - this.mob.getX()) / 5, (blockPos.getY() + 0.5 - this.mob.getY()) / 5, (blockPos.getZ() + 0.5 - this.mob.getZ()) / 5);
            this.mob.getWorld().spawnEntity(itemEntity);
        }
    }
}
