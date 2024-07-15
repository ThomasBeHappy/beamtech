package com.gamingframe.beamtech.effects;

import com.gamingframe.beamtech.BeamTech;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEffects {

    public static final StatusEffect FLASH_BLINDNESS_EFFECT = Registry.register(Registries.STATUS_EFFECT,
            Identifier.of(BeamTech.MOD_ID, "flash_blindness"), new FlashBlindnessEffect());

    public static void registerEffects() {

    }

}
