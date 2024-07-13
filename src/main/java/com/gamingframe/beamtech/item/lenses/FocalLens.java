package com.gamingframe.beamtech.item.lenses;

import com.gamingframe.beamtech.mixin.KeybindingAccessor;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FocalLens extends Item {
    public FocalLens(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        InputUtil.Key boundSneakKey = ((KeybindingAccessor) MinecraftClient.getInstance().options.sneakKey).getBoundKey();

        if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), boundSneakKey.getCode())) {
            tooltip.add(Text.translatable("item.beamtech.focal_lens.desc").formatted(Formatting.GRAY));
        }else {
            tooltip.add(Text.translatable("tooltip.beamtech.hold_crouch_desc",
                            Text.translatable(MinecraftClient.getInstance().options.sneakKey.getBoundKeyTranslationKey()).formatted(Formatting.GOLD))
                    .formatted(Formatting.GRAY));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }
}
