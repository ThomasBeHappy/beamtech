package com.gamingframe.beamtech.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.book.RecipeCategory;
import com.gamingframe.beamtech.item.ModItems;

import java.util.List;
import java.util.function.Consumer;

public class ModRecipeProvider extends FabricRecipeProvider {
//    private static final List<ItemConvertible> CRAB_SMELTABLES = List.of(Moditems.CRAB);

    public ModRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
//        offerSmelting(exporter, CRAB_SMELTABLES, RecipeCategory.FOOD, Moditems.COOKED_CRAB,
//                0.7f, 200, "crab");
    }
}
