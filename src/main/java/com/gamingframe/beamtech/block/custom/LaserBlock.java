package com.gamingframe.beamtech.block.custom;

import com.gamingframe.beamtech.BeamTech;
import com.gamingframe.beamtech.block.ModBlockEntities;
import com.gamingframe.beamtech.block.custom.entity.LaserBlockEntity;
import com.gamingframe.beamtech.item.ModItems;
import com.gamingframe.beamtech.mixin.KeybindingAccessor;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.text.Normalizer;
import java.util.List;

public class LaserBlock extends BlockWithEntity   {
    public static final DirectionProperty FACING = Properties.FACING;

    public LaserBlock(Settings settings) {
        super(settings);
        BeamTech.LOGGER.info("Creating Block Laser Emitter for {} ", BeamTech.MOD_ID);
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
        Direction dir = state.get(FACING);
        return switch (dir) {
            default -> VoxelShapes.fullCube();
        };
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
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type != ModBlockEntities.LASER_EMITTER_BLOCK_ENTITY) return null;
        return (world1, pos, state1, be) -> LaserBlockEntity.tick(world1, pos, state1, (LaserBlockEntity) be);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        BeamTech.LOGGER.info("Creating Block Entity at {} for {} ", pos, BeamTech.MOD_ID);
        return new LaserBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack playerStack = player.getStackInHand(hand);

        BlockEntity entity = world.getBlockEntity(pos);

        if (entity instanceof LaserBlockEntity be) {
            ItemStack beStack = be.getStack(0);

            Item playerItem = playerStack.getItem();
            if (beStack.getItem() != Items.AIR) {
                if (playerItem == ModItems.FOCAL_LENS || playerItem == ModItems.RANGE_LENS) {
                    player.setStackInHand(hand, beStack);
                    be.setStack(0, playerStack);
                } else {
                    if (playerItem == Items.AIR) {
                        player.setStackInHand(hand, beStack);
                    }else {
                        // TODO: This ignores if your inventory is full, if its full it disapears in the void!
                        player.giveItemStack(beStack);
                    }
                    be.setStack(0, Items.AIR.getDefaultStack());
                }
            } else {
                if (playerItem == ModItems.FOCAL_LENS || playerItem == ModItems.RANGE_LENS) {
                    player.setStackInHand(hand, Items.AIR.getDefaultStack());
                    be.setStack(0, playerStack);
                }
            }
        }

//        player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
        return ActionResult.SUCCESS;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {

        InputUtil.Key boundSneakKey = ((KeybindingAccessor)MinecraftClient.getInstance().options.sneakKey).getBoundKey();

        if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), boundSneakKey.getCode())) {
            tooltip.add(Text.translatable("item.beamtech.laser_block.desc").formatted(Formatting.GRAY));
        }else {
            tooltip.add(Text.translatable("tooltip.beamtech.hold_crouch_desc",
                    Text.translatable(MinecraftClient.getInstance().options.sneakKey.getBoundKeyTranslationKey()).formatted(Formatting.GOLD))
                    .formatted(Formatting.GRAY));
        }

        super.appendTooltip(stack, world, tooltip, options);
    }
}
