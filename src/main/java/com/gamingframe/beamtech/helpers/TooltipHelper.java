package com.gamingframe.beamtech.helpers;

import com.gamingframe.beamtech.mixin.KeybindingAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TooltipHelper {

    public static void appendTooltip(ItemStack stack, List<Text> tooltip) {
        InputUtil.Key boundSneakKey = ((KeybindingAccessor) MinecraftClient.getInstance().options.sneakKey).getBoundKey();

        if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), boundSneakKey.getCode())) {
            tooltip.add(Text.translatable(stack.getItem().getTranslationKey() + ".desc").formatted(Formatting.GRAY));
        }else {
            tooltip.add(Text.translatable("tooltip.beamtech.hold_crouch_desc",
                            Text.translatable(MinecraftClient.getInstance().options.sneakKey.getBoundKeyTranslationKey()).formatted(Formatting.GOLD))
                    .formatted(Formatting.GRAY));
        }
    }
}
