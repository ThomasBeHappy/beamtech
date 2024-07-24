package com.gamingframe.beamtech.item;

import com.gamingframe.beamtech.BeamTech;
import com.gamingframe.beamtech.entities.ModEntities;
import com.gamingframe.beamtech.item.lenses.FocalLens;
import com.gamingframe.beamtech.item.lenses.RangeLens;
import com.gamingframe.beamtech.item.tools.LaserGun;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item IRON_ALLOY = registerItem("alloy_iron",new Item(new Item.Settings()));
    public static final Item FOCAL_LENS = registerItem("focal_lens",new FocalLens(new Item.Settings().maxCount(1)));
    public static final Item RANGE_LENS = registerItem("range_lens",new RangeLens(new Item.Settings().maxCount(1)));
    public static final Item LENS = registerItem("lens",new Item(new Item.Settings().maxCount(64)));
    public static final Item MIRROR_INGREDIENT = registerItem("mirror_ingredient",new Item(new Item.Settings().maxCount(64)));
    public static final Item SUPERTREATED_GLASS = registerItem("super_treated_glass",new Item(new Item.Settings().maxCount(64)));
    public static final Item EMITTER = registerItem("emitter",new Item(new Item.Settings().maxCount(64)));
    public static final Item CAPACITOR = registerItem("capacitor",new Item(new Item.Settings().maxCount(64)));
    public static final Item LASER_GUN = registerItem("laser_gun",new LaserGun(new Item.Settings().maxCount(1).maxDamage(101)));

    public static final Item CLEANBOT_SPAWN_EGG = registerItem("cleanbot_spawn_egg",
            new SpawnEggItem(ModEntities.CLEANBOT, 0x4a5351, 0xe25340, new FabricItemSettings()));

    private static void addItemsToIngredientTabItemGroup(FabricItemGroupEntries entries) {
        entries.add(IRON_ALLOY);
    }

    public static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(BeamTech.MOD_ID, name), item);
    }

    public static void registerModItems() {
        BeamTech.LOGGER.info("Registering Mod Items for " + BeamTech.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ModItems::addItemsToIngredientTabItemGroup);
    }


}
