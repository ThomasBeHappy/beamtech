package com.gamingframe.beamtech.block.custom.entity;

import com.gamingframe.beamtech.interfaces.IEmitter;
import com.gamingframe.beamtech.interfaces.ILaserInteractable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class LaserOvenBlockEntity extends BlockEntity implements ILaserInteractable {
    public LaserOvenBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void onHit(IEmitter emitter, Vec3d direction) {

    }

    @Override
    public void onNoLongerHit(IEmitter emitter) {

    }
}
