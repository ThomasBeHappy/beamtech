package com.gamingframe.beamtech.raycasting;

import com.gamingframe.beamtech.block.custom.MirrorBlock;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;

public class LaserRayCastContext extends RaycastContext {
    private final BlockEntity source;
    private final Vec3d lastHitMirror;

    public LaserRayCastContext(Vec3d start, Vec3d end, ShapeType shapeType, FluidHandling fluidHandling, BlockEntity source, Vec3d lastHitMirror) {
        super(start, end, shapeType, fluidHandling, null);
        this.source = source;
        this.lastHitMirror = lastHitMirror;
    }

    @Override
    public VoxelShape getBlockShape(BlockState state, BlockView world, BlockPos pos) {
        if (state.isAir() || state.isOf(Blocks.WATER) || state.getBlock() instanceof TransparentBlock
                || (state.getBlock() instanceof PaneBlock && state.getSoundGroup() == BlockSoundGroup.GLASS)
                || (source != null && world.getBlockEntity(pos) == source)
                || (lastHitMirror != null && pos.toCenterPos().distanceTo(lastHitMirror) < 0.2)) {
            return VoxelShapes.empty();
        }
        return state.getCollisionShape(world, pos);
    }
}
