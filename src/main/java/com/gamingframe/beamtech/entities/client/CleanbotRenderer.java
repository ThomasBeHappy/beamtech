package com.gamingframe.beamtech.entities.client;

import com.gamingframe.beamtech.BeamTech;
import com.gamingframe.beamtech.entities.custom.CleanbotEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CleanbotRenderer extends GeoEntityRenderer<CleanbotEntity> {
    public CleanbotRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new CleanbotModel());
    }

    @Override
    public Identifier getTextureLocation(CleanbotEntity animatable) {
        return new Identifier(BeamTech.MOD_ID, "textures/entity/cleanbot.png");
    }

    @Override
    public void render(CleanbotEntity entity, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.4f,0.4f,0.4f);
        }
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
