package com.gamingframe.beamtech.block;

import com.gamingframe.beamtech.BeamTech;
import com.gamingframe.beamtech.block.custom.entity.LaserBlockEntity;
import com.gamingframe.beamtech.block.custom.entity.LaserCombinerBlockEntity;
import com.gamingframe.beamtech.block.custom.entity.LaserFocuserBlockEntity;
import com.gamingframe.beamtech.block.custom.entity.LaserOvenBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import team.reborn.energy.api.EnergyStorage;

public class ModBlockEntities {

    public static final BlockEntityType<LaserBlockEntity> LASER_EMITTER_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(BeamTech.MOD_ID, "laser_emitter_block_entity"),
            BlockEntityType.Builder.create(LaserBlockEntity::new, ModBlocks.LASER_EMITTER).build(null)
    );

    public static final BlockEntityType<LaserCombinerBlockEntity> LASER_COMBINER_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(BeamTech.MOD_ID, "laser_combiner_block_entity"),
            BlockEntityType.Builder.create(LaserCombinerBlockEntity::new, ModBlocks.LASER_COMBINER_BLOCK).build(null)
    );

    public static final BlockEntityType<LaserOvenBlockEntity> LASER_OVEN_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(BeamTech.MOD_ID, "laser_oven_block_entity"),
            BlockEntityType.Builder.create(LaserOvenBlockEntity::new, ModBlocks.LASER_OVEN_BLOCK).build(null)
    );

    public static final BlockEntityType<LaserFocuserBlockEntity> LASER_FOCUSER_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(BeamTech.MOD_ID, "laser_focuser_block_entity"),
            BlockEntityType.Builder.create(LaserFocuserBlockEntity::new, ModBlocks.LASER_FOCUSER_BLOCK).build(null)
    );


    public static void registerBlockEntities() {
        BeamTech.LOGGER.info("Registering Blocks Entities for " + BeamTech.MOD_ID);
        EnergyStorage.SIDED.registerForBlockEntity((myBlockEntity, direction) -> myBlockEntity.energyStorage, LASER_EMITTER_BLOCK_ENTITY);

    }


}
