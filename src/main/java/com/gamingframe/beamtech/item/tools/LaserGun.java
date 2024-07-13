package com.gamingframe.beamtech.item.tools;

import com.gamingframe.beamtech.BeamTech;
import com.gamingframe.beamtech.helpers.TooltipHelper;
import com.gamingframe.beamtech.item.ModItems;
import com.gamingframe.beamtech.mixin.KeybindingAccessor;
import com.gamingframe.beamtech.raycasting.LaserRayCastContext;
import com.gamingframe.beamtech.sounds.ModSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import team.lodestar.lodestone.handlers.ScreenshakeHandler;
import team.lodestar.lodestone.registry.common.particle.LodestoneParticleRegistry;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;
import team.lodestar.lodestone.systems.particle.data.spin.SpinParticleData;
import team.lodestar.lodestone.systems.screenshake.PositionedScreenshakeInstance;
import team.lodestar.lodestone.systems.screenshake.ScreenshakeInstance;
import team.reborn.energy.api.base.SimpleEnergyItem;

import java.awt.*;
import java.util.List;

// TODO: Make an item that shoots lasers, dare I say more?
public class LaserGun extends Item implements SimpleEnergyItem {
    public LaserGun(Settings settings) {
        super(settings);
    }


    @Override
    public void setStoredEnergy(ItemStack stack, long newAmount) {
        SimpleEnergyItem.super.setStoredEnergy(stack, newAmount);
        stack.setDamage(Math.min(Math.max((int) (newAmount * 100 / getEnergyCapacity(stack)), 0), 100));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
//        if (world.isClient) {
//            return TypedActionResult.pass(user.getStackInHand(hand));
//        }
        ItemStack stack = user.getStackInHand(hand);
        NbtCompound nbt = stack.getNbt();

        if (nbt == null) nbt = new NbtCompound();

        if (!nbt.getBoolean("inUse") && !nbt.getBoolean("firing")) {
            nbt.putBoolean("inUse", true); // LETS FIRE DA LASERRRRRRRRRR
            stack.setNbt(nbt);
            user.getItemCooldownManager().set(this, 600);
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS,
                    120,
                    10));
        }

        return TypedActionResult.pass(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        NbtCompound nbt = stack.getNbt();
        if (nbt == null) return;

        if (nbt.getBoolean("inUse") && !nbt.getBoolean("firing")) {
            // LETS GO
            nbt.putBoolean("firing", true);
            nbt.putInt("timePassed", 0);
            stack.setNbt(nbt);
        }

        if (nbt.getBoolean("firing")) {

            int ticks = nbt.getInt("timePassed");

            if (ticks == 0) {
                // play sound, begin effect for gathering
                world.playSound(null, entity.getBlockPos(), ModSounds.LASER_GUN_FIRING, SoundCategory.BLOCKS, 1,1);
            }else {
                if (ticks >= 60) {
                    Vec3d eyePos = entity.getEyePos();
                    Vec3d lookVec = entity.getRotationVec(1.0F); // Get the direction the player is looking at

                    BlockHitResult result = world.raycast(
                            new LaserRayCastContext(eyePos, eyePos.add(lookVec.multiply(100)),
                                    RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, null, null));

                    // Calculate the right vector by taking the cross product of the look vector and the up vector (0, 1, 0)
                    Vec3d rightVec = lookVec.crossProduct(new Vec3d(0, 1, 0)).normalize();

                    // Define an offset magnitude
                    double offsetMagnitude = 0.5; // Adjust this value as needed

                    // Calculate the offset vector
                    Vec3d offset = rightVec.multiply(offsetMagnitude);

                    // Apply the offset to the eye position
                    Vec3d offsetEyePos = eyePos.add(offset);

                    renderLaser(world, offsetEyePos, result.getPos());

                    ScreenshakeInstance laserScreenShake = new PositionedScreenshakeInstance(35, entity.getPos(), 30f, 45f, Easing.CIRC_IN_OUT).setIntensity(0.3f, 0.5f, 0f);
                    ScreenshakeHandler.addScreenshake(laserScreenShake);

                    Box box = new Box(entity.getEyePos(), result.getPos());

                    List<Entity> entities = world.getOtherEntities(null, box);

                    for (Entity entity2 : entities) {
                        Box entityBox = entity2.getBoundingBox().expand(0.3);

                        if (entityBox.raycast(entity.getEyePos(), result.getPos()).isPresent()) {
                            entity2.damage(entity2.getDamageSources().generic(), (float) 10);
                        }
                    }
                }
            }

            if (ticks >= 120) {
                nbt.putBoolean("firing", false);
                nbt.putBoolean("inUse", false);
                nbt.putInt("timePassed", 0);
                long energy = getStoredEnergy(stack);
                setStoredEnergy(stack, energy - 50000);
            }

            ticks = ticks + 1;
            nbt.putInt("timePassed", ticks);
            stack.setNbt(nbt);
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 120;
    }


    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {

        return stack;
    }

    public void renderLaser(World world, Vec3d startPos, Vec3d endPos) {
        double distance = startPos.distanceTo(endPos);
        Vec3d direction = endPos.subtract(startPos).normalize();

        Color startingColor = new Color(255, 0, 0);
        Color centerColor = new Color(255, 142, 142);

        var builderOuter = WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE).setScaleData(GenericParticleData.create(0.25f, 0).build())
                .setTransparencyData(GenericParticleData.create(0.75f, 0).build())
                .setColorData(ColorParticleData.create(startingColor).build())
                .setSpinData(SpinParticleData.create(0.2f, 0.4f).setSpinOffset((world.getTime() * 0.2f) % 6.28f).setEasing(Easing.QUARTIC_IN).build())
                .setLifetime(3)
                .enableNoClip();

        var builderCenter = WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE).setScaleData(GenericParticleData.create(0.20f, 0).build())
                .setTransparencyData(GenericParticleData.create(1f, 0).build())
                .setColorData(ColorParticleData.create(centerColor).build())
                .setSpinData(SpinParticleData.create(0.2f, 0.4f).setSpinOffset((world.getTime() * 0.2f) % 6.28f).setEasing(Easing.QUARTIC_IN).build())
                .setLifetime(3)
                .enableNoClip();

        for (double i = 0; i < distance; i += 0.05) {
            Vec3d currentPos = startPos.add(direction.multiply(i));
            builderOuter.spawn(world, currentPos.x, currentPos.y, currentPos.z);
            builderCenter.spawn(world, currentPos.x, currentPos.y, currentPos.z);
        }
    }


    @Override
    public long getEnergyCapacity(ItemStack stack) {
        return 100000;
    }

    @Override
    public long getEnergyMaxInput(ItemStack stack) {
        return 5000;
    }

    @Override
    public long getEnergyMaxOutput(ItemStack stack) {
        return 5000;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        TooltipHelper.appendTooltip(stack, tooltip);
        super.appendTooltip(stack, world, tooltip, context);
    }
}
