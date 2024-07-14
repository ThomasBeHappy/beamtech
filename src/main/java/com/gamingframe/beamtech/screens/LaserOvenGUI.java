package com.gamingframe.beamtech.screens;

import com.gamingframe.beamtech.BeamTech;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WBar;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import io.github.cottonmc.cotton.gui.widget.data.Texture;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.Identifier;

public class LaserOvenGUI extends SyncedGuiDescription {
    private static final int INVENTORY_SIZE = 2;
    private static final int PROPERTY_COUNT = 2;

    public LaserOvenGUI(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(ModGUIs.LASER_OVEN_GUI, syncId, playerInventory, getBlockInventory(context, INVENTORY_SIZE), getBlockPropertyDelegate(context, PROPERTY_COUNT));

        WGridPanel root = new WGridPanel();
        setRootPanel(root);
        root.setSize(150, 150);
        root.setInsets(Insets.ROOT_PANEL);

        WItemSlot inputSlot = WItemSlot.of(blockInventory, 0);
        root.add(inputSlot, 2, 1);

        WItemSlot outputSlot = WItemSlot.of(blockInventory, 0);
        root.add(outputSlot, 6, 1);

        //WBar progressBar = new WBar(Identifier.of(BeamTech.MOD_ID, "progress_bar_tex"), Identifier.of(BeamTech.MOD_ID, "progress_bar"), 3, 5, WBar.Direction.RIGHT);
        //progressBar.

        root.add(this.createPlayerInventoryPanel(), 0, 3);
        root.validate(this);
    }
}
