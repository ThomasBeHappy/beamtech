package com.gamingframe.beamtech.world.dimension;

import com.gamingframe.beamtech.BeamTech;
import com.gamingframe.beamtech.world.dimension.beamtechdimension.VoidChunkGenerator;
import net.minecraft.registry.*;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

import java.util.OptionalLong;

public class ModDimensions {
    public static final RegistryKey<DimensionOptions> BEAMTECH_KEY =
            RegistryKey.of(RegistryKeys.DIMENSION, new Identifier(BeamTech.MOD_ID, "beamtechdim"));

    public static final RegistryKey<World> BEAMTECH_LEVEL_KEY =
            RegistryKey.of(RegistryKeys.WORLD, new Identifier(BeamTech.MOD_ID, "beamtechdim"));

    public static final RegistryKey<DimensionType> BEAMTECH_DIM_TYPE =
            RegistryKey.of(RegistryKeys.DIMENSION_TYPE, new Identifier(BeamTech.MOD_ID, "beamtechdim_type"));

    public static void registerDimensions() {
        Registry.register(Registries.CHUNK_GENERATOR, new Identifier(BeamTech.MOD_ID, "void_chunk_generator"), VoidChunkGenerator.CODEC);
    }
}
