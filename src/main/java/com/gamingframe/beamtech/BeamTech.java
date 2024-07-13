package com.gamingframe.beamtech;

import com.gamingframe.beamtech.block.ModBlockEntities;
import com.gamingframe.beamtech.block.ModBlocks;
import com.gamingframe.beamtech.item.ModItemGroups;
import com.gamingframe.beamtech.item.ModItems;
import com.gamingframe.beamtech.sounds.ModSounds;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeamTech implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "beamtech";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		ModItemGroups.registerItemGroups();

		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModBlockEntities.registerBlockEntities();
		ModSounds.registerSounds();

		LOGGER.info("Hello Fabric world!");
	}
}