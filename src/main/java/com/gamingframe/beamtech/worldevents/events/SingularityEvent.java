package com.gamingframe.beamtech.worldevents.events;


import com.gamingframe.beamtech.BeamTech;
import com.gamingframe.beamtech.entities.BlackHoleEntity;
import com.gamingframe.beamtech.entities.ModEntities;
import com.gamingframe.beamtech.sounds.ModSounds;
import com.gamingframe.beamtech.worldevents.ModWorldEvents;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;
import team.lodestar.lodestone.systems.worldevent.WorldEventInstance;
import team.lodestar.lodestone.systems.worldevent.WorldEventType;

public class SingularityEvent extends WorldEventInstance {
    public SingularityEvent() {
        super(ModWorldEvents.SINGULARITY_EVENT);
    }


    public int ticksPassed = 1;


    @Override
    public void tick(World level) {
        if (ticksPassed == 0) {
            end(level);
            return;
        }

    }

    @Override
    public void start(World level) {
        super.start(level);
        BeamTech.LOGGER.info("Spawning and playing sound in level {}", level);
        level.playSound(null,0,0,0, ModSounds.SINGULARITY_SPAWNING, SoundCategory.AMBIENT, 1, 1);
        level.playSound(null,0,0,0, ModSounds.FACILITY_MUSIC, SoundCategory.MUSIC, 1, 1);
        Entity black_hole = ModEntities.BLACK_HOLE.create(level);
        black_hole.setPos(0,0,0);
        level.spawnEntity(black_hole);

    }
}
