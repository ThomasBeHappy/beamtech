package com.gamingframe.beamtech.entities;

import com.gamingframe.beamtech.BeamTech;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<BlackHoleEntity> BLACK_HOLE = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(BeamTech.MOD_ID, "black_hole"),
            FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, BlackHoleEntity::new)
                    .dimensions(EntityDimensions.fixed(1,1)).build());

    public static void initializeEntities() {

    }
}
