package com.gamingframe.beamtech.entities;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class BlackHoleRenderer extends EntityRenderer<BlackHoleEntity> {
    public BlackHoleRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTexture(BlackHoleEntity entity) {
        return null;
    }

    @Override
    public void render(BlackHoleEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        // TODO: SHADER BULLSHIT
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }
}
