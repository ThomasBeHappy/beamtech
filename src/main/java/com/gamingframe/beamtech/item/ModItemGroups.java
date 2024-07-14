package com.gamingframe.beamtech.item;

import com.gamingframe.beamtech.BeamTech;
import com.gamingframe.beamtech.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class ModItemGroups {
    public static final ItemGroup BEAMTECH_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(BeamTech.MOD_ID, "beamtech"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.beamtech"))
                    .icon(() -> new ItemStack(ModBlocks.LASER_EMITTER)).entries((displayContext, entries) -> {
                        entries.add(ModItems.IRON_ALLOY);
                        entries.add(ModItems.FOCAL_LENS);
                        entries.add(ModItems.RANGE_LENS);
                        entries.add(ModItems.LENS);
                        entries.add(ModItems.CAPACITOR);
                        entries.add(ModItems.LASER_GUN);
                        entries.add(ModItems.EMITTER);
                        entries.add(ModItems.MIRROR_INGREDIENT);
                        entries.add(ModItems.SUPERTREATED_GLASS);
                        entries.add(ModBlocks.LASER_EMITTER);
                        entries.add(ModBlocks.LASER_COMBINER_BLOCK);
                        entries.add(ModBlocks.MIRROR_BLOCK);
                        entries.add(ModBlocks.LASER_OVEN_BLOCK);
                        entries.add(ModBlocks.METAL_MESH_DOOR_BLOCK);
                    }).build());

    public static final ItemGroup DEV_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(BeamTech.MOD_ID, "beamtech_dev"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.beamtech"))
                    .icon(() -> new ItemStack(ModBlocks.LASER_EMITTER)).entries((displayContext, entries) -> {
                        entries.add(ModBlocks.PIPE);
                        entries.add(ModBlocks.PIPE_T);
                        entries.add(ModBlocks.PIPE_UP);
                        entries.add(ModBlocks.PIPE_ANGLED);
                        entries.add(ModBlocks.PIPE_T_UP);
                        entries.add(ModBlocks.PIPE_ANGLED_UP);
                        entries.add(ModBlocks.PIPE_ANGLED_HORIZONTAL);
                        entries.add(ModBlocks.FLUORESCENT_BULB_BLOCK);
                    }).build());


    public static void registerItemGroups() {
        BeamTech.LOGGER.info("Registering Item Groups for " + BeamTech.MOD_ID);
    }
}
