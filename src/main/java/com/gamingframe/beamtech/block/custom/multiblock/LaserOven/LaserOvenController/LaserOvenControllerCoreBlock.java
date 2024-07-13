package com.gamingframe.beamtech.block.custom.multiblock.LaserOven.LaserOvenController;

import com.gamingframe.beamtech.block.ModBlocks;
import net.minecraft.block.entity.BlockEntityType;
import team.lodestar.lodestone.systems.block.WaterLoggedEntityBlock;
import team.lodestar.lodestone.systems.multiblock.MultiBlockStructure;

import java.util.function.Supplier;

public class LaserOvenControllerCoreBlock <T extends LaserOvenCoreBlockEntity> extends WaterLoggedEntityBlock<T> {

    public static final Supplier<MultiBlockStructure> STRUCTURE = () -> (MultiBlockStructure.of(
            new MultiBlockStructure.StructurePiece(0, 1, 0, ModBlocks.TITANIUM_PLATED_OBSIDIAN_BLOCK.getDefaultState())));

    public LaserOvenControllerCoreBlock(Settings properties, Supplier<BlockEntityType<T>> type) {
        super(properties);
        setBlockEntity(type);
    }
}
