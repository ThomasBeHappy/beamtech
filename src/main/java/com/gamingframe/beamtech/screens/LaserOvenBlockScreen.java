package com.gamingframe.beamtech.screens;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class LaserOvenBlockScreen extends CottonInventoryScreen<LaserOvenGUI> {
    public LaserOvenBlockScreen(LaserOvenGUI description, PlayerEntity player, Text title) {
        super(description, player, title);
    }
}
