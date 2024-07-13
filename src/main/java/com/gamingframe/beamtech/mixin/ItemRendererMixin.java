package com.gamingframe.beamtech.mixin;

import com.gamingframe.beamtech.BeamTech;
import com.gamingframe.beamtech.item.ModItems;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @ModifyVariable(method = "renderItem", at = @At(value = "HEAD"), argsOnly = true)
    public BakedModel useLaserWeaponModel(BakedModel value, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (stack.isOf(ModItems.LASER_GUN) && renderMode != ModelTransformationMode.GUI) {
            return ((ItemRendererAccessor) this).getModels().getModelManager().getModel(new ModelIdentifier(BeamTech.MOD_ID, "laser_weapon_held", "inventory"));
        }
        return value;
    }
}
