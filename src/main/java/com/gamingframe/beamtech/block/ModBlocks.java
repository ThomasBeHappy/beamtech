package com.gamingframe.beamtech.block;

import com.gamingframe.beamtech.BeamTech;
import com.gamingframe.beamtech.block.custom.*;
import com.gamingframe.beamtech.block.decorative.*;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
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

    public static final Block LASER_OVEN_BLOCK = registerBlock("laser_oven",
            new LaserOvenBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque()));

    public static final Block LASER_FOCUSER_BLOCK = registerBlock("laser_focuser",
            new LaserFocuserBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque()));

    public static final Block LASER_SENSOR_BLOCK = registerBlock("laser_sensor",
            new LaserSensorBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque()));

    //region METAL MESH BLOCKS -------------------------------------------------------------------
    public static final Block METAL_MESH_PLATING_BLOCK = registerBlock("metal_mesh_plating",
            new Block(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).strength(-1.0F, 3600000.0F)));

    public static final Block METAL_MESH_WALLS_BLOCK = registerBlock("metal_mesh_walls",
            new Block(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).strength(-1.0F, 3600000.0F)));

    public static final Block METAL_MESH_CEILING_BLOCK = registerBlock("metal_mesh_ceiling",
            new Block(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).strength(-1.0F, 3600000.0F)));

    public static final Block METAL_MESH_DOOR_BLOCK = registerBlock("metal_mesh_door",
            new DoorBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK), BlockSetType.IRON));
    //endregion ----------------------------------------------------------------------------------

    //region PIPES -------------------------------------------------------------------------------
    public static final Block PIPE = registerBlock("pipe",
            new PipeBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque()));
    public static final Block PIPE_ANGLED = registerBlock("pipe_angled",
            new PipeAngledBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque()));
    public static final Block PIPE_ANGLED_HORIZONTAL = registerBlock("pipe_angled_horizontal",
            new PipeAngledHorizontalBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque()));
    public static final Block PIPE_ANGLED_UP = registerBlock("pipe_angled_up",
            new PipeAngledUpBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque()));
    public static final Block PIPE_T = registerBlock("pipe_t",
            new PipeTBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque()));
    public static final Block PIPE_T_UP = registerBlock("pipe_t_up",
            new PipeTUpBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque()));
    public static final Block PIPE_UP = registerBlock("pipe_up",
            new PipeUp(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque()));
    //endregion -----------------------------------------------------------------------------------

    public static final Block FLUORESCENT_BULB_BLOCK = registerBlock("fluorescent_bulb",
            new PipeUp(FabricBlockSettings.copyOf(Blocks.GLOWSTONE).nonOpaque().luminance(state -> 15)));


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
