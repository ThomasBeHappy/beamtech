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


    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return VoxelShapes.fullCube();
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LaserFocuserBlockEntity(pos, state);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof LaserFocuserBlockEntity) {
                ItemScatterer.spawn(world, pos, (LaserFocuserBlockEntity)blockEntity);
                world.updateComparators(pos,this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack playerStack = player.getStackInHand(hand);

        BlockEntity entity = world.getBlockEntity(pos);

        if (entity instanceof LaserFocuserBlockEntity be) {
            ItemStack beStack = be.getStack(0);

            Item playerItem = playerStack.getItem();
            if (beStack.getItem() != Items.AIR) {
                if (Recipes.isValidRecipe(playerItem)) {
                    player.setStackInHand(hand, beStack);
                    be.setStack(0, playerStack);
                } else {
                    if (playerItem == Items.AIR) {
                        player.setStackInHand(hand, beStack);
                    }else {
                        if (!player.giveItemStack(beStack)) {
                            Vec3d playerPos = player.getPos();
                            ItemScatterer.spawn(world, playerPos.x, playerPos.y, playerPos.z, beStack);
                        }
                    }
                    be.setStack(0, Items.AIR.getDefaultStack());
                }
            } else {
                if (Recipes.isValidRecipe(playerItem)) {
                    player.setStackInHand(hand, Items.AIR.getDefaultStack());
                    be.setStack(0, playerStack);
                }
            }
        }

//        player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
        return ActionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type != ModBlockEntities.LASER_FOCUSER_BLOCK_ENTITY) return null;
        return (world1, pos, state1, be) -> ((LaserFocuserBlockEntity)be).tick(world1, pos, state1);
    }
}
