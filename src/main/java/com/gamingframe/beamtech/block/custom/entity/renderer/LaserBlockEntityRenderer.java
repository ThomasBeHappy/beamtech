package com.gamingframe.beamtech.block.custom.entity.renderer;

import com.gamingframe.beamtech.block.custom.LaserBlock;
import com.gamingframe.beamtech.block.custom.entity.LaserBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import team.lodestar.lodestone.handlers.RenderHandler;
import team.lodestar.lodestone.systems.rendering.VFXBuilders;

import java.awt.*;
import java.util.Map;

public class LaserBlockEntityRenderer implements BlockEntityRenderer<LaserBlockEntity> {
    public LaserBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(LaserBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

//        VFXBuilders.WorldVFXBuilder builder = VFXBuilders.createWorld();
//        builder.replaceBufferSource(RenderHandler.LATE_DELAYED_RENDER.getTarget())
//            .setRenderType(RenderLayer.getDebugQuads())
//            .setColor(Color.red)
//            .setAlpha(0.8f);
//
//        for (var entry : entity.rayCastPositions) {
//
//            builder.renderQuad(matrices);
//        }

        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        ItemStack stack = entity.getStack(0);

        Direction facing = entity.getCachedState().get(LaserBlock.FACING);
        Vec3d itemPos = new Vec3d(0.5, 0.5, 0.5).add(Vec3d.of(facing.getVector()).multiply(0.2));

        matrices.push();
        matrices.translate(itemPos.x, itemPos.y, itemPos.z);
        matrices.scale(0.9f, 0.9f, 0.9f);

        switch (facing) {
            case EAST:
            case WEST:
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
                break;
            case UP:
            case DOWN:
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
                break;
        }

        itemRenderer.renderItem(stack, ModelTransformationMode.GUI, getLightLevel(entity.getWorld(), entity.getPos()), OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), 1);
        matrices.pop();
    }

    private int getLightLevel(World world, BlockPos pos) {
        int bLight = world.getLightLevel(LightType.BLOCK, pos);
        int sLight = world.getLightLevel(LightType.SKY, pos);
        return LightmapTextureManager.pack(bLight, sLight);
    }
}
