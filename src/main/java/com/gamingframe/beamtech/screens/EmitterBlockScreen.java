package com.gamingframe.beamtech.screens;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class EmitterBlockScreen extends CottonInventoryScreen<EmitterGUI> {
    public EmitterBlockScreen(EmitterGUI description, PlayerEntity player, Text title) {
        super(description, player, title);
    }
}
