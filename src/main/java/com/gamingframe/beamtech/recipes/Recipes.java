package com.gamingframe.beamtech.recipes;

import com.gamingframe.beamtech.interfaces.ILaserCraftingRecipe;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

public class Recipes {

    public static final ILaserCraftingRecipe SUPER_TREATED_GLASS_RECIPE = new SuperTreatedGlassRecipe();

    public static final List<ILaserCraftingRecipe> RECIPES = new ArrayList<>();

    public static void registerRecipe(ILaserCraftingRecipe recipe) {
        RECIPES.add(recipe);
    }

    public static boolean isValidRecipe(Item item) {
        return RECIPES.stream().anyMatch((recipe) -> recipe.getIngredient() == item);
    }

    public static ILaserCraftingRecipe getRecipe(Item item) {
        return RECIPES.stream().filter((recipe) -> recipe.getIngredient() == item).findFirst().get();
    }

    public static void registerRecipes() {
        registerRecipe(SUPER_TREATED_GLASS_RECIPE);
    }
}
