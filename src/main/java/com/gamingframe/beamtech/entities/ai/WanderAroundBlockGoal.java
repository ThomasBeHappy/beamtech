package com.gamingframe.beamtech.entities.ai;

import com.gamingframe.beamtech.BeamTech;
import com.gamingframe.beamtech.entities.custom.CleanbotEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WanderAroundBlockGoal extends Goal {

    protected BlockPos wanderBlockPos;
    protected Block wanderBlock;
    protected final double speed;
    protected final CleanbotEntity mob;
    protected final float probability;
    protected final int detectRange = 25;
    protected final int wanderRange = 15;

    public WanderAroundBlockGoal(CleanbotEntity mob, double speed, Block block, float probability) {
        this.speed = speed;
        this.mob = mob;
        this.wanderBlock = block;
        this.probability = probability;
    }

    @Override
    public boolean canStart() {
        BlockPos blockPos = locateClosestWanderBlock(this.mob.getWorld(), this.mob, this.wanderBlock, this.detectRange);
        if (blockPos != null && this.mob.getRandom().nextFloat() >= this.probability && this.mob.getNavigation().isIdle() && !this.mob.isCleaning() && !this.mob.isDepositing()) {
            this.wanderBlockPos = blockPos;
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldContinue() {
        BlockPos blockPos = locateClosestWanderBlock(this.mob.getWorld(), this.mob, this.wanderBlock, this.detectRange);
        if (blockPos != null && !this.mob.getNavigation().isIdle()) {
            this.wanderBlockPos = blockPos;
            return true;
        }
        return false;
    }

    @Override
    public void start() {
        BlockPos block = getRandomTargetAroundBlock(wanderBlockPos, this.wanderRange);
        this.mob.getNavigation().startMovingTo(block.getX(), block.getY(), block.getZ(), this.speed);
    }

    @Override
    public void stop() {
        this.mob.getNavigation().stop();
    }

    public BlockPos getRandomTargetAroundBlock(BlockPos block, int radius) {
        int targetX = (int)(block.getX() + ((Math.random() - 0.5) * radius));
        int targetY = (int)(block.getY() + ((Math.random() - 0.5) * radius));
        int targetZ = (int)(block.getZ() + ((Math.random() - 0.5) * radius));

        return new BlockPos(targetX, targetY, targetZ);
    }

    protected BlockPos locateClosestWanderBlock(World world, Entity entity, Block block, int range) {
        BlockPos blockPos = entity.getBlockPos();

        return BlockPos.findClosest(blockPos, range, range, (pos) ->
                world.getBlockState(pos).getBlock() == block).orElse(null);
    }
}
