package com.gamingframe.beamtech.block;

import com.gamingframe.beamtech.BeamTech;
import com.gamingframe.beamtech.block.custom.LaserBlock;
import com.gamingframe.beamtech.block.custom.LaserCombinerBlock;
import com.gamingframe.beamtech.block.custom.MirrorBlock;
import com.gamingframe.beamtech.block.custom.multiblock.LaserOven.TitaniumPlatedObsidian.TitaniumPlatedObsidianComponentBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {

    public static final Block LASER_EMITTER = registerBlock("laser_emitter",
            new LaserBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque()));

    public static final Block MIRROR_BLOCK = registerBlock("mirror",
            new MirrorBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque()));

    public static final Block LASER_COMBINER_BLOCK = registerBlock("laser_combiner",
            new LaserCombinerBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque()));

    public static final Block TITANIUM_PLATED_OBSIDIAN_BLOCK = registerBlock("titanium_plated_obsidian",
            new TitaniumPlatedObsidianComponentBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque()));

    public static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(BeamTech.MOD_ID, name), block);
    }

    public static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, Identifier.of(BeamTech.MOD_ID, name), new BlockItem(block, new Item.Settings()));
    }


    public static void registerModBlocks() {
        BeamTech.LOGGER.info("Registering Blocks for " + BeamTech.MOD_ID);
    }

}
