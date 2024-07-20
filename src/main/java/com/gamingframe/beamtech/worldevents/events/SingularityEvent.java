package com.gamingframe.beamtech.worldevents.events;


import com.gamingframe.beamtech.BeamTech;
import com.gamingframe.beamtech.entities.BlackHoleEntity;
import com.gamingframe.beamtech.entities.ModEntities;
import com.gamingframe.beamtech.sounds.ModSounds;
import com.gamingframe.beamtech.worldevents.ModWorldEvents;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
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
import team.lodestar.lodestone.systems.screenshake.PositionedScreenshakeInstance;
import team.lodestar.lodestone.systems.screenshake.ScreenshakeInstance;
import team.lodestar.lodestone.systems.worldevent.WorldEventInstance;
import team.lodestar.lodestone.systems.worldevent.WorldEventType;

import java.awt.*;
import java.util.Random;

public class SingularityEvent extends WorldEventInstance {
    public SingularityEvent() {
        super(ModWorldEvents.SINGULARITY_EVENT);
    }


    public int ticksPassed = 1;
    public int spawnblackHoleTicks = 420;

    @Override
    public void tick(World level) {
        if (ticksPassed == 0) {
            end(level);
            return;
        }

        if (ticksPassed < spawnblackHoleTicks) {
            if (!level.isClient && ticksPassed % 10 == 0) {

                BeamTech.LOGGER.info("Spawning Particles and Screenshake");
                PlayerLookup.all(level.getServer()).forEach(serverPlayerEntity -> {
                    if (serverPlayerEntity.getWorld() == level) {
                        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                        buf.writeInt(ticksPassed);
                        buf.writeBoolean(false); // is not the creation effect
                        ServerPlayNetworking.send(serverPlayerEntity, new Identifier(BeamTech.MOD_ID, "singularity_particles"), buf);
                    }
                });
            }
        }

        if (ticksPassed == spawnblackHoleTicks) {
            Entity black_hole = ModEntities.BLACK_HOLE.create(level);
            black_hole.setPos(0,0,0);
            level.spawnEntity(black_hole);
            level.playSound(null,0,0,0, ModSounds.FACILITY_MUSIC, SoundCategory.MUSIC, 1, 1);
            PlayerLookup.all(level.getServer()).forEach(serverPlayerEntity -> {
                if (serverPlayerEntity.getWorld() == level) {
                    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                    buf.writeInt(ticksPassed);
                    buf.writeBoolean(true); // is the creation effect
                    ServerPlayNetworking.send(serverPlayerEntity, new Identifier(BeamTech.MOD_ID, "singularity_particles"), buf);
                }
            });
        }

        if (ticksPassed >= spawnblackHoleTicks) {
            if (!level.isClient && ticksPassed % 20 == 0) {
                PlayerLookup.all(level.getServer()).forEach(serverPlayerEntity -> {
                    if (serverPlayerEntity.getWorld() == level) {
                        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                        buf.writeBoolean(true); // enable timer
                        buf.writeInt(timeLeft);
                        ServerPlayNetworking.send(serverPlayerEntity, new Identifier(BeamTech.MOD_ID, "singularity_time_tick"), buf);
                    }
                });
                timeLeft--;
            }
        }


        ticksPassed++;

    }

    public int timeLeft = 120;

    public static void spawnParticlesInSphere(World level, BlockPos center, double radius, int particleCount) {
        Random random = new Random();

        for (int i = 0; i < particleCount; i++) {
            double theta = 2 * Math.PI * random.nextDouble();  // Random angle for the circle
            double phi = Math.acos(2 * random.nextDouble() - 1);  // Random angle for the sphere
            double r = radius * Math.cbrt(random.nextDouble());  // Random distance from the center

            double xOffset = r * Math.sin(phi) * Math.cos(theta);
            double yOffset = r * Math.sin(phi) * Math.sin(theta);
            double zOffset = r * Math.cos(phi);

            Vec3d particlePos = new Vec3d(center.getX() + xOffset, center.getY() + yOffset, center.getZ() + zOffset);

            // Calculate motion towards the center
            Vec3d motion = new Vec3d(center.getX() - particlePos.x, center.getY() - particlePos.y, center.getZ() - particlePos.z).normalize().multiply(0.1);

            Color startingColor = new Color(0, 0, 0);
            Color endingColor = new Color(255, 255, 255);
            WorldParticleBuilder.create(LodestoneParticleRegistry.EXTRUDING_SPARK_PARTICLE)
                    .setScaleData(GenericParticleData.create(0.05f, 0).build())
                    .setTransparencyData(GenericParticleData.create(1).build())
                    .setColorData(ColorParticleData.create(startingColor, endingColor).setCoefficient(1.4f).setEasing(Easing.BOUNCE_IN_OUT).build())
                    .setLifetime(20)
                    .addMotion(motion.x, motion.y, motion.z) // Apply the motion towards the center
                    .enableNoClip()
                    .spawn(level, particlePos.x, particlePos.y, particlePos.z);
        }
    }

    public static void spawnCreationParticles(World level, BlockPos center, double radius, int particleCount) {
        Random random = new Random();

        for (int i = 0; i < particleCount; i++) {
            double theta = 2 * Math.PI * random.nextDouble();  // Random angle for the circle
            double r = radius * Math.cbrt(random.nextDouble());  // Random distance from the center

            double xOffset = r * Math.cos(theta);
            double zOffset = r * Math.sin(theta);

            Vec3d particlePos = new Vec3d(center.getX() + xOffset, center.getY(), center.getZ() + zOffset);

            Vec3d motion = new Vec3d(center.getX() + particlePos.x, center.getY() + particlePos.y, center.getZ() + particlePos.z).normalize().multiply(0.3);

            Color startingColor = new Color(28, 28, 28);
            Color endingColor = new Color(166, 166, 166);
            WorldParticleBuilder.create(LodestoneParticleRegistry.EXTRUDING_SPARK_PARTICLE)
                    .setScaleData(GenericParticleData.create(0.1f, 0).build())
                    .setTransparencyData(GenericParticleData.create(1).build())
                    .setColorData(ColorParticleData.create(startingColor, endingColor).setCoefficient(1.4f).setEasing(Easing.BOUNCE_IN_OUT).build())
                    .setLifetime(200)
                    .addMotion(motion.x, motion.y, motion.z)
                    .enableNoClip()
                    .spawn(level, particlePos.x, particlePos.y, particlePos.z);

            Color startingColor2 = new Color(166, 166, 166);
            Color endingColor2 = new Color(28, 28, 28);
            WorldParticleBuilder.create(LodestoneParticleRegistry.EXTRUDING_SPARK_PARTICLE)
                    .setScaleData(GenericParticleData.create(0.9f, 0).build())
                    .setTransparencyData(GenericParticleData.create(1).build())
                    .setColorData(ColorParticleData.create(startingColor2, endingColor2).setCoefficient(1.4f).setEasing(Easing.BOUNCE_IN_OUT).build())
                    .setLifetime(200)
                    .addMotion(motion.x * 0.9, motion.y * 0.9, motion.z * 0.9)
                    .enableNoClip()
                    .spawn(level, particlePos.x, particlePos.y, particlePos.z);
        }
    }


    @Override
    public void start(World level) {
        super.start(level);
        BeamTech.LOGGER.info("Spawning and playing sound in level {}", level);
        level.playSound(null,0,0,0, ModSounds.SINGULARITY_SPAWNING, SoundCategory.AMBIENT, 1, 1);
    }
}
