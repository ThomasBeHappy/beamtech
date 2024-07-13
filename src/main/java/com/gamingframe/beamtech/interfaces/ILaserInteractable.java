package com.gamingframe.beamtech.interfaces;

import net.minecraft.util.math.Vec3d;

public interface ILaserInteractable {
    public void onHit(IEmitter emitter, Vec3d direction);

    public void onNoLongerHit(IEmitter emitter);
}
