package com.gamingframe.beamtech.entities.custom;

import com.gamingframe.beamtech.BeamTech;
import com.gamingframe.beamtech.entities.ai.DepositItemsGoal;
import com.gamingframe.beamtech.entities.ai.GrabItemsGoal;
import com.gamingframe.beamtech.entities.ai.WanderAroundBlockGoal;
import com.gamingframe.beamtech.sounds.ModSounds;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class CleanbotEntity extends PathAwareEntity implements GeoEntity, Angerable {

    @Nullable
    private UUID angryAt;

    private static final TrackedData<Integer> ANGER_TIME =
            DataTracker.registerData(CleanbotEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public static final TrackedData<Boolean> CLEANING =
            DataTracker.registerData(CleanbotEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public static final TrackedData<Boolean> DEPOSITING =
            DataTracker.registerData(CleanbotEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.cleanbot.walk");

    public final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public CleanbotEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.setCanPickUpLoot(true);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::walkAnimController));
    }

    private <E extends CleanbotEntity> PlayState walkAnimController(final AnimationState<E> event) {
        if (event.isMoving()) {
            event.setControllerSpeed(this.isAttacking() || this.isDepositing() || this.isCleaning() ? 1.5f : 1.0f);
            return event.setAndContinue(WALK_ANIM);
        }

        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    public static DefaultAttributeContainer.Builder createCleanbotAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2f)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 64)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, -10);

    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.5, false));
        this.goalSelector.add(2, new DepositItemsGoal(this));
        this.goalSelector.add(3, new GrabItemsGoal(this));
        this.goalSelector.add(4, new WanderAroundBlockGoal(this, 1.0, Blocks.GOLD_BLOCK, 0.9f));
        this.targetSelector.add(1, (new RevengeGoal(this, new Class[0])).setGroupRevenge(new Class[0]));
        this.targetSelector.add(2, new ActiveTargetGoal(this, PlayerEntity.class, 10, true, false, entity -> shouldAngerAt((LivingEntity) entity)));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ANGER_TIME, 0);
        this.dataTracker.startTracking(CLEANING, false);
        this.dataTracker.startTracking(DEPOSITING, false);
    }

    public boolean isCleaning() {
        return this.dataTracker.get(CLEANING);
    }

    public void setCleaning(boolean setCleaning) {
        this.dataTracker.set(CLEANING, setCleaning);
    }

    public boolean isDepositing() {
        return this.dataTracker.get(DEPOSITING);
    }

    public void setDepositing(boolean setdepositing) {
        this.dataTracker.set(DEPOSITING, setdepositing);
    }

    public int getAngerTime() {
        return this.dataTracker.get(ANGER_TIME);
    }

    public void setAngerTime(int angerTime) {
        this.dataTracker.set(ANGER_TIME, angerTime);
    }

    public void chooseRandomAngerTime() {
        int angertime = (int)(Math.random() * 19 + 20);
        this.setAngerTime(angertime);
    }

    @Nullable
    public UUID getAngryAt() {
        return this.angryAt;
    }

    public void setAngryAt(@Nullable UUID angryAt) {
        this.angryAt = angryAt;
    }

    @Override
    public boolean canEquip(ItemStack stack) {
        EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(stack);
        if (!this.getEquippedStack(equipmentSlot).isEmpty()) {
            return false;
        } else {
            return equipmentSlot == EquipmentSlot.MAINHAND && super.canEquip(stack);
        }
    }

    @Override
    protected void loot(ItemEntity item) {
        if (this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()) {
            ItemStack itemStack = item.getStack();
            if (this.canPickupItem(itemStack)) {
                this.triggerItemPickedUpByEntityCriteria(item);
                this.equipStack(EquipmentSlot.MAINHAND, itemStack);
                this.updateDropChances(EquipmentSlot.MAINHAND);
                this.sendPickup(item, itemStack.getCount());
                item.discard();
            }
        }

    }

    /* sounds */

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.CLEANBOT_AMBIENT;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.CLEANBOT_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.CLEANBOT_DEATH;
    }
}
