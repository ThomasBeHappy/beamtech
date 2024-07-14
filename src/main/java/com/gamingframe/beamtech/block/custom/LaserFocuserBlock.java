package com.gamingframe.beamtech.block.custom;

import com.gamingframe.beamtech.block.ModBlockEntities;
import com.gamingframe.beamtech.block.custom.entity.LaserBlockEntity;
import com.gamingframe.beamtech.block.custom.entity.LaserCombinerBlockEntity;
import com.gamingframe.beamtech.block.custom.entity.LaserFocuserBlockEntity;
import com.gamingframe.beamtech.block.custom.entity.LaserOvenBlockEntity;
import com.gamingframe.beamtech.item.ModItems;
import com.gamingframe.beamtech.recipes.Recipes;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Recipe;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

// TODO: This block will focus a laser it receives onto itself to create cool items. Will an inventory
//  (avoid UI interface, prefer using right click to give the items to the block)
public class LaserFocuserBlock extends BlockWithEntity {
    public static final DirectionProperty FACING = Properties.FACING;

    public LaserFocuserBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }
}
