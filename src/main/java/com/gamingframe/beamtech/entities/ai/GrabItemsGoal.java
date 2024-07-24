package com.gamingframe.beamtech.entities.ai;

import com.gamingframe.beamtech.BeamTech;
import com.gamingframe.beamtech.entities.custom.CleanbotEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Predicate;

public class GrabItemsGoal extends Goal {

    protected CleanbotEntity mob;
    protected ItemEntity item;
    protected Predicate<ItemEntity> entityPredicate = (entity) -> entity.isAlive() && entity.isOnGround();


    public GrabItemsGoal(CleanbotEntity mob) {
        this.mob = mob;
    }

    @Override
    public boolean canStart() {
        List<ItemEntity> itemList = this.mob.getWorld().getEntitiesByClass(ItemEntity.class, this.mob.getBoundingBox().expand(7, 7, 7), entityPredicate);
        if (!itemList.isEmpty() && this.mob.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()) {
            item = itemList.get(0);
            return true;
        }
        return false;
    }

    @Override
    public void start() {
        this.mob.setCleaning(true);
        Path path = this.mob.getNavigation().findPathTo(item, 0);
        this.mob.getNavigation().startMovingAlong(path, 1.5f);
    }

    @Override
    public void tick() {
        List<ItemEntity> itemList = this.mob.getWorld().getEntitiesByClass(ItemEntity.class, this.mob.getBoundingBox().expand(7, 7, 7), entityPredicate);
        if (!itemList.isEmpty() && this.mob.getNavigation().isIdle()) {
            Path path = this.mob.getNavigation().findPathTo(itemList.get(0), 0);
            this.mob.getNavigation().startMovingAlong(path, 1.5f);
        }
    }

    @Override
    public void stop() {
        this.mob.setCleaning(false);
        this.mob.getNavigation().stop();
    }
}
