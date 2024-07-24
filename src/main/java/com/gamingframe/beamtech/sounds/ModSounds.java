package com.gamingframe.beamtech.sounds;

import com.gamingframe.beamtech.BeamTech;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {

    public static final SoundEvent LASER_GUN_FIRING = registerSoundEvent("laser_gun_firing");
    public static final SoundEvent FACILITY_MUSIC = registerSoundEvent("facility_music");
    public static final SoundEvent SINGULARITY_SPAWNING = registerSoundEvent("singularity_spawning");
    public static final SoundEvent CLEANBOT_STEP = registerSoundEvent("cleanbot_step");
    public static final SoundEvent CLEANBOT_HURT = registerSoundEvent("cleanbot_hurt");
    public static final SoundEvent CLEANBOT_DEATH = registerSoundEvent("cleanbot_death");
    public static final SoundEvent CLEANBOT_AMBIENT = registerSoundEvent("cleanbot_ambient");

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = new Identifier(BeamTech.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerSounds() {
        BeamTech.LOGGER.info("registering sounds for {}", BeamTech.MOD_ID);
    }
}
