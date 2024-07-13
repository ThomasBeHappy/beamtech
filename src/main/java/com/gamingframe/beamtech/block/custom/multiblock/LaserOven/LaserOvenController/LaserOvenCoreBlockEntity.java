package com.gamingframe.beamtech.block.custom.multiblock.LaserOven.LaserOvenController;


import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import team.lodestar.lodestone.systems.multiblock.MultiBlockCoreEntity;
import team.lodestar.lodestone.systems.multiblock.MultiBlockStructure;

public class LaserOvenCoreBlockEntity extends MultiBlockCoreEntity {

    public LaserOvenCoreBlockEntity(BlockEntityType<?> type, MultiBlockStructure structure, BlockPos pos, BlockState state) {
        super(type, structure, pos, state);
    }
}
