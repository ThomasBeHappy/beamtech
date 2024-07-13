package com.gamingframe.beamtech.screens;

import com.gamingframe.beamtech.BeamTech;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;

public class EmitterGUI extends SyncedGuiDescription {
    private static final int INVENTORY_SIZE = 1;

    public EmitterGUI(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(ModGUIs.EMITTER_GUI, syncId, playerInventory, getBlockInventory(context, INVENTORY_SIZE), getBlockPropertyDelegate(context));

        WGridPanel root = new WGridPanel();
        setRootPanel(root);
        root.setSize(150, 150);
        root.setInsets(Insets.ROOT_PANEL);

        WItemSlot itemSlot = WItemSlot.of(blockInventory, 0);
        root.add(itemSlot, 4, 1);

        root.add(this.createPlayerInventoryPanel(), 0, 3);

        root.validate(this);
    }
}
