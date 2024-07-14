package com.gamingframe.beamtech.recipes;

import com.gamingframe.beamtech.interfaces.ILaserCraftingRecipe;
import com.gamingframe.beamtech.item.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class SuperTreatedGlassRecipe implements ILaserCraftingRecipe {

    @Override
    public int getMinPowerNeeded() {
        return 5;
    }

    @Override
    public int getTimeNeeded() {
        return 10;
    }

    @Override
    public Item getIngredient() {
        return Items.SAND;
    }

    @Override
    public Item getOutput() {
        return ModItems.SUPERTREATED_GLASS;
    }
}
