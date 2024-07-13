package com.gamingframe.beamtech.block.custom.entity;

import com.gamingframe.beamtech.block.ModBlockEntities;
import com.gamingframe.beamtech.block.custom.LaserBlock;
import com.gamingframe.beamtech.block.custom.MirrorBlock;
import com.gamingframe.beamtech.interfaces.IEmitter;
import com.gamingframe.beamtech.interfaces.ILaserInteractable;
import com.gamingframe.beamtech.raycasting.LaserRayCastContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import team.lodestar.lodestone.registry.common.particle.*;
import team.lodestar.lodestone.systems.easing.*;
import team.lodestar.lodestone.systems.particle.builder.*;
import team.lodestar.lodestone.systems.particle.data.*;
import team.lodestar.lodestone.systems.particle.data.color.*;
import team.lodestar.lodestone.systems.particle.data.spin.*;

import java.util.ArrayList;
import java.util.List;
import java.awt.Color;

public class LaserCombinerBlockEntity extends BlockEntity implements IEmitter, ILaserInteractable {
    private static final int MAX_RANGE = 50; // Max range for the laser
    public List<IEmitter> emitters = new ArrayList<>();
    public int power = 0;
    public ILaserInteractable registeredLaserInteractable;
    private static final int MAX_REFLECTIONS = 100;
    private int currentReflections = 0;

    public LaserCombinerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LASER_COMBINER_BLOCK_ENTITY, pos, state);
    }

    public static void tick(World world1, BlockPos pos, BlockState state1, LaserCombinerBlockEntity be) {
        be.power = 0;

        for (int i = 0; i < be.emitters.size(); i++) {

            be.power += be.emitters.get(i).getPower();
        }


        if (be.emitters.size() >= 2) {
            Direction direction = state1.get(LaserBlock.FACING);
            Vec3d startPos = Vec3d.ofCenter(pos).add(Vec3d.of(direction.getVector()).multiply(0.5));
            be.shootLaser(be.world, startPos, Vec3d.of(direction.getVector()), MAX_RANGE);
        }else if (be.registeredLaserInteractable != null){
            be.registeredLaserInteractable.onNoLongerHit(be);
            be.registeredLaserInteractable = null;
        }
        be.currentReflections = 0;
    }

    @Override
    public void markRemoved() {
        if (registeredLaserInteractable != null) {
            registeredLaserInteractable.onNoLongerHit(this);
            registeredLaserInteractable = null;
        }
        super.markRemoved();
    }

    @Override
    public int getPower() {
        return power;
    }

    Vec3d lastHitMirror;

    @Override
    public void shootLaser(World world, Vec3d startPos, Vec3d direction, int remainingRange) {
        if (remainingRange <= 0) return;

        Vec3d endPos = startPos.add(direction.multiply(remainingRange));

        BlockHitResult hitResult = world.raycast(new LaserRayCastContext(
                startPos,
                endPos,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                this, lastHitMirror));

        BlockPos hitPos = hitResult.getBlockPos();
        BlockState hitState = world.getBlockState(hitPos);

        // Calculate the distance to the hit position
        double distance = startPos.distanceTo(hitResult.getPos());

        // Render the laser up to the hit point
        renderLaser(world, startPos, hitResult.getPos());

        if (world.getBlockEntity(hitPos) instanceof ILaserInteractable laserCombinerBlock) {
            registeredLaserInteractable = laserCombinerBlock;
            laserCombinerBlock.onHit(this, direction);
        }else if (hitState.getBlock() instanceof MirrorBlock mirror) {
            if (currentReflections >= MAX_REFLECTIONS) {
                return;
            }

            lastHitMirror = hitPos.toCenterPos();

            Vec3d newDirection = mirror.reflectLaser(hitState, direction);

            Vec3d newStartPos = hitPos.toCenterPos().add(newDirection.multiply(0.6));

            currentReflections++;
            // Emit laser in the new direction, subtracting the distance already traveled
            shootLaser(world, newStartPos, newDirection, remainingRange - (int)distance);
        }else if (power >= 10 && !hitState.isAir()) {
            if (world.getTime() % 20 != 0) return;
            vaporizeBlocks(world, hitPos, hitResult.getPos(), direction, power/10);
        }
    }

    @Override
    public void renderLaser(World world, Vec3d startPos, Vec3d endPos) {
        double distance = startPos.distanceTo(endPos);
        Vec3d direction = endPos.subtract(startPos).normalize();

        Color startingColor = new Color(255, 0, 0);
        Color centerColor = new Color(255, 142, 142);

        var builderOuter = WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE).setScaleData(GenericParticleData.create(0.10f * (power / 4f) / 1.5f).build())
                .setTransparencyData(GenericParticleData.create(0.75f).build())
                .setColorData(ColorParticleData.create(startingColor).build())
                .setSpinData(SpinParticleData.create(0.2f, 0.4f).setSpinOffset((world.getTime() * 0.2f) % 6.28f).setEasing(Easing.QUARTIC_IN).build())
                .setLifetime(10)
                .enableNoClip();

        var builderCenter = WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE).setScaleData(GenericParticleData.create(0.05f * (power / 4f) / 1.5f).build())
                .setTransparencyData(GenericParticleData.create(1f).build())
                .setColorData(ColorParticleData.create(centerColor).build())
                .setSpinData(SpinParticleData.create(0.2f, 0.4f).setSpinOffset((world.getTime() * 0.2f) % 6.28f).setEasing(Easing.QUARTIC_IN).build())
                .setLifetime(10)
                .enableNoClip();

        for (double i = 0; i < distance; i += 0.05) {
            Vec3d currentPos = startPos.add(direction.multiply(i));
            builderOuter.spawn(world, currentPos.x, currentPos.y, currentPos.z);
            builderCenter.spawn(world, currentPos.x, currentPos.y, currentPos.z);
        }

    }

    public void vaporizeBlocks(World world, BlockPos centerPos, Vec3d hitPos, Vec3d direction, int radius) {
        // Normalize the direction vector
//        Vec3d normalizedDirection = Vec3d.of(direction.getVector()).normalize();

//        // Determine the plane normal vector based on the direction vector
//        Vec3d planeNormal;
//        if (Math.abs(normalizedDirection.x) > Math.abs(normalizedDirection.y) && Math.abs(normalizedDirection.x) > Math.abs(normalizedDirection.z)) {
//            planeNormal = new Vec3d(1, 0, 0);
//        } else if (Math.abs(normalizedDirection.y) > Math.abs(normalizedDirection.z)) {
//            planeNormal = new Vec3d(0, 1, 0);
//        } else {
//            planeNormal = new Vec3d(0, 0, 1);
//        }
        // Loop through all blocks in a cube around the center position
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos currentPos = centerPos.add(x, y, z);
                    if (world.getBlockState(currentPos).isAir()) continue;

                    // Calculate the distance from the current block position to the plane
                    Vec3d currentVec = new Vec3d(currentPos.getX() - hitPos.x, currentPos.getY() - hitPos.y, currentPos.getZ() - hitPos.z);
                    double distanceToPlane = currentVec.dotProduct(direction);

                    // Project the point onto the plane
                    Vec3d projectedVec = currentVec.subtract(direction.multiply(distanceToPlane));

                    // Calculate the distance from the projected point to the center
                    double distance = projectedVec.length();

                    // Check if the current block position is within the specified radius on the plane
                    if (Math.abs(distanceToPlane) < 1e-6 && distance <= radius) {
                        // Remove the block at the current position

                        world.removeBlock(currentPos, false);

                        Color startingColor = new Color(50, 50, 50);
                        var smoke = WorldParticleBuilder.create(LodestoneParticleRegistry.SMOKE_PARTICLE).setScaleData(GenericParticleData.create(0.10f * (power / 4f) / 1.5f).build())
                                .setTransparencyData(GenericParticleData.create(0.8f).build())
                                .setColorData(ColorParticleData.create(startingColor).build())
                                .setSpinData(SpinParticleData.create(0.2f, 0.4f).setSpinOffset((world.getTime() * 0.2f) % 6.28f).setEasing(Easing.QUARTIC_IN).build())
                                .setLifetime(40)
                                .addMotion(0,0.2,0)
                                .enableNoClip();

                        smoke.spawn(world,currentPos.getX(), currentPos.getY(), currentPos.getZ());
                    }
                }
            }
        }

        world.playSound(null, centerPos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 1f, 1f);


    }

    @Override
    public void onHit(IEmitter emitter, Vec3d direction) {
        if (!emitters.contains(emitter)) {
            emitters.add(emitter);
        }
    }

    @Override
    public void onNoLongerHit(IEmitter emitter) {
        emitters.remove(emitter);
    }
}
