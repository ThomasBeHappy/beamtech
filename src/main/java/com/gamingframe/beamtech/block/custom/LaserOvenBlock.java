package com.gamingframe.beamtech.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

// TODO: Make an oven that when powered by a laser cooks items.
public class LaserOvenBlock extends BlockEntity {

    public LaserOvenBlock(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}
