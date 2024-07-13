package com.gamingframe.beamtech.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import com.gamingframe.beamtech.item.ModItems;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
//        itemModelGenerator.register(ModItems.FocalLens, Models.GENERATED);
//        itemModelGenerator.register(ModItems.RangeLens, Models.GENERATED);
//        itemModelGenerator.register(ModItems.IRON_ALLOY, Models.GENERATED);
//        itemModelGenerator.register(Moditems.RUBY, Models.GENERATED);
//        itemModelGenerator.register(Moditems.RAW_RUBY, Models.GENERATED);
//        itemModelGenerator.register(Moditems.CRAB, Models.GENERATED);
//        itemModelGenerator.register(Moditems.COOKED_CRAB, Models.GENERATED);
//        itemModelGenerator.register(Moditems.CRAB_RAVE_MUSIC_DISC, Models.GENERATED);
//
//        itemModelGenerator.register(Moditems.CRAB_SPAWN_EGG,
//                new Model(Optional.of(new Identifier("item/template_spawn_egg")), Optional.empty()));
    }
}
