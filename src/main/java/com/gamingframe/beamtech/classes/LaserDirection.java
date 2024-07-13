package com.gamingframe.beamtech.classes;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

public enum LaserDirection implements StringIdentifiable {
    NORTH(new Vec3i(0, 0, -1)),
    SOUTH(new Vec3i(0, 0, 1)),
    NORTH_WEST(new Vec3i(-1, 0, -1)),
    SOUTH_WEST(new Vec3i(-1, 0, 1)),
    WEST(new Vec3i(-1, 0, 0)),
    NORTH_EAST(new Vec3i(1, 0, -1)),
    SOUTH_EAST(new Vec3i(1, 0, 1)),
    EAST(new Vec3i(1, 0, 0));

    public Vec3i direction;

    LaserDirection(Vec3i direction) {
        this.direction = direction;
    }

    @Override
    public String asString() {
        return this.name().toLowerCase();
    }
}
