package com.gamingframe.beamtech.interfaces;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

public interface ILaserCraftingRecipe {

    public int getMinPowerNeeded();
    public int getTimeNeeded();
    public Item getIngredient();
    public Item getOutput();
}
