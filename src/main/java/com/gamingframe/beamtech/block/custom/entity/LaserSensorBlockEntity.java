package com.gamingframe.beamtech.block.custom.entity;

import com.gamingframe.beamtech.block.ModBlockEntities;
import com.gamingframe.beamtech.block.custom.LaserSensorBlock;
import com.gamingframe.beamtech.interfaces.IEmitter;
import com.gamingframe.beamtech.interfaces.ILaserInteractable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class LaserSensorBlockEntity extends BlockEntity implements ILaserInteractable {
    public LaserSensorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LASER_SENSOR_BLOCK_ENTITY, pos, state);
    }

    IEmitter emitter;

    @Override
    public void onHit(IEmitter emitter, Vec3d direction) {
        if (this.emitter == emitter) return;
        this.emitter = emitter;
        world.setBlockState(pos, getCachedState().with(LaserSensorBlock.POWERED, true));
    }

    @Override
    public void onNoLongerHit(IEmitter emitter) {
        if (this.emitter != emitter) return;
        this.emitter = null;
        world.setBlockState(pos, getCachedState().with(LaserSensorBlock.POWERED, false));
    }
}
