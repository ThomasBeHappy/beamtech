package com.gamingframe.beamtech.interfaces;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface IEmitter {
    public int getPower();

    public void shootLaser(World world, Vec3d startPos, Vec3d direction, int remainingRange);

    public void renderLaser(World world, Vec3d startPos, Vec3d endPos);
}
