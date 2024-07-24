package com.gamingframe.beamtech.entities.client;

import com.gamingframe.beamtech.BeamTech;
import com.gamingframe.beamtech.entities.custom.CleanbotEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class CleanbotModel extends GeoModel<CleanbotEntity> {
    @Override
    public Identifier getModelResource(CleanbotEntity animatable) {
        return new Identifier(BeamTech.MOD_ID, "geo/cleanbot.geo.json");
    }

    @Override
    public Identifier getTextureResource(CleanbotEntity animatable) {
        return new Identifier(BeamTech.MOD_ID, "textures/entity/cleanbot.png");
    }

    @Override
    public Identifier getAnimationResource(CleanbotEntity animatable) {
        return new Identifier(BeamTech.MOD_ID, "animations/cleanbot.animation.json");
    }
}
