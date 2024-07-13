package com.gamingframe.beamtech.screens;

import com.gamingframe.beamtech.BeamTech;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModGUIs {
    public static final ScreenHandlerType<EmitterGUI> EMITTER_GUI = Registry.register(Registries.SCREEN_HANDLER, Identifier.of(BeamTech.MOD_ID, "emitter_screen"),
            new ScreenHandlerType<>((syncId, inventory) -> new EmitterGUI(syncId, inventory, ScreenHandlerContext.EMPTY),
                    FeatureFlags.VANILLA_FEATURES));


    public static void InitializeGUIs() {
        BeamTech.LOGGER.info("Initializing Screens for {}", BeamTech.MOD_ID);
    }
}
