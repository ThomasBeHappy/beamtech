package com.gamingframe.beamtech;

import com.gamingframe.beamtech.block.ModBlockEntities;
import com.gamingframe.beamtech.block.custom.entity.LaserBlockEntity;
import com.gamingframe.beamtech.block.custom.entity.LaserCombinerBlockEntity;
import com.gamingframe.beamtech.block.custom.entity.renderer.LaserBlockEntityRenderer;
import com.gamingframe.beamtech.block.custom.entity.renderer.LaserCombinerEntityRenderer;
import com.gamingframe.beamtech.entities.client.BlackHoleRenderer;
import com.gamingframe.beamtech.entities.ModEntities;
import com.gamingframe.beamtech.entities.client.CleanbotRenderer;
import com.gamingframe.beamtech.screens.EmitterBlockScreen;
import com.gamingframe.beamtech.screens.EmitterGUI;
import com.gamingframe.beamtech.screens.ModGUIs;
import com.gamingframe.beamtech.shader.MirrorReflectionPostProcessor;
import com.gamingframe.beamtech.worldevents.events.SingularityEvent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import team.lodestar.lodestone.handlers.ScreenshakeHandler;
import team.lodestar.lodestone.registry.common.particle.LodestoneParticleRegistry;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;
import team.lodestar.lodestone.systems.particle.data.spin.SpinParticleData;
import team.lodestar.lodestone.systems.postprocess.PostProcessHandler;
import team.lodestar.lodestone.systems.screenshake.PositionedScreenshakeInstance;
import team.lodestar.lodestone.systems.screenshake.ScreenshakeInstance;

import java.awt.*;

@Environment(EnvType.CLIENT)
public class BeamTechClient implements ClientModInitializer {
    public int timeLeft = 120;
    public boolean showTimer = false;

    @Override
    public void onInitializeClient() {
        HandledScreens.<EmitterGUI, EmitterBlockScreen>register(ModGUIs.EMITTER_GUI, (gui, inventory, title) -> new EmitterBlockScreen(gui, inventory.player, title));
        ClientPlayNetworking.registerGlobalReceiver(new Identifier(BeamTech.MOD_ID, "update_laser_lens"), (client, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            ItemStack itemStack = buf.readItemStack();

            BlockEntity blockEntity = client.world.getBlockEntity(pos);
            if (blockEntity instanceof LaserBlockEntity laserBlockEntity) {
                laserBlockEntity.syncInventory(itemStack);
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(new Identifier(BeamTech.MOD_ID, "combiner_update_laser_lens"), (client, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            ItemStack itemStack = buf.readItemStack();

            BlockEntity blockEntity = client.world.getBlockEntity(pos);
            if (blockEntity instanceof LaserCombinerBlockEntity laserBlockEntity) {
                laserBlockEntity.syncInventory(itemStack);
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(new Identifier(BeamTech.MOD_ID, "singularity_particles"), (client, handler, buf, responseSender) -> {

            var ticksPassed = buf.readInt();
            if (buf.readBoolean()) {
                SingularityEvent.spawnCreationParticles(client.world, new BlockPos(0,0,0), 1, ticksPassed * 4);
            }else {
                SingularityEvent.spawnParticlesInSphere(client.world, new BlockPos(0,0,0), 4, ticksPassed * 4);
            }

            ScreenshakeInstance blackHoleShake = new PositionedScreenshakeInstance(40, client.player.getPos(), 30, 200f, Easing.LINEAR).setIntensity(0.1f, 0.2f, 0.25f);
            ScreenshakeHandler.addScreenshake(blackHoleShake);

        });

        ClientPlayNetworking.registerGlobalReceiver(new Identifier(BeamTech.MOD_ID, "singularity_time_tick"), (client, handler, buf, responseSender) -> {
            showTimer = buf.readBoolean();
            timeLeft = buf.readInt();
        });

        ClientPlayNetworking.registerGlobalReceiver(new Identifier(BeamTech.MOD_ID, "spawn_laser_particles"), ((client, handler, buf, responseSender) -> {
            Vec3d startPos = new Vec3d(buf.readVector3f());
            Vec3d endPos = new Vec3d(buf.readVector3f());
            World world = client.world;

            double distance = startPos.distanceTo(endPos);
            Vec3d direction = endPos.subtract(startPos).normalize();

            Color startingColor = new Color(255, 0, 0);
            Color centerColor = new Color(255, 142, 142);

            var builderOuter = WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE).setScaleData(GenericParticleData.create(0.25f, 0).build())
                    .setTransparencyData(GenericParticleData.create(0.75f, 0).build())
                    .setColorData(ColorParticleData.create(startingColor).build())
                    .setSpinData(SpinParticleData.create(0.2f, 0.4f).setSpinOffset((world.getTime() * 0.2f) % 6.28f).setEasing(Easing.QUARTIC_IN).build())
                    .setLifetime(3)
                    .enableNoClip();

            var builderCenter = WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE).setScaleData(GenericParticleData.create(0.20f, 0).build())
                    .setTransparencyData(GenericParticleData.create(1f, 0).build())
                    .setColorData(ColorParticleData.create(centerColor).build())
                    .setSpinData(SpinParticleData.create(0.2f, 0.4f).setSpinOffset((world.getTime() * 0.2f) % 6.28f).setEasing(Easing.QUARTIC_IN).build())
                    .setLifetime(3)
                    .enableNoClip();

            for (double i = 0; i < distance; i += 0.05) {
                Vec3d currentPos = startPos.add(direction.multiply(i));
                builderOuter.spawn(world, currentPos.x, currentPos.y, currentPos.z);
                builderCenter.spawn(world, currentPos.x, currentPos.y, currentPos.z);
            }

        }));

        HudRenderCallback.EVENT.register((context, tickDeltaManager) -> {
            if (showTimer) {
                var renderer = MinecraftClient.getInstance().textRenderer;
                var x = renderer.getWidth(timeLeft + "s");
                int color;
                if (timeLeft % 2 == 0) {
                    color = 0xFFFFFFFF;
                } else {
                    color = 0xFF0000;
                }
                context.drawText(MinecraftClient.getInstance().textRenderer, timeLeft + "s", context.getScaledWindowWidth() / 2 - x / 2, 40 - renderer.fontHeight - 10, color, false);
            }
        });

        BlockEntityRendererFactories.register(ModBlockEntities.LASER_EMITTER_BLOCK_ENTITY, LaserBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.LASER_COMBINER_BLOCK_ENTITY, LaserCombinerEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.BLACK_HOLE, BlackHoleRenderer::new);

        EntityRendererRegistry.register(ModEntities.CLEANBOT, CleanbotRenderer::new);

        PostProcessHandler.addInstance(MirrorReflectionPostProcessor.INSTANCE);
    }
}
