package com.gamingframe.beamtech.block.custom;

import com.gamingframe.beamtech.BeamTech;
import com.gamingframe.beamtech.block.ModBlockEntities;
import com.gamingframe.beamtech.block.custom.entity.LaserBlockEntity;
import com.gamingframe.beamtech.block.custom.entity.LaserCombinerBlockEntity;
import com.gamingframe.beamtech.interfaces.IEmitter;
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
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LaserCombinerBlock extends BlockWithEntity {
    public LaserCombinerBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    public static final DirectionProperty FACING = Properties.FACING;

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        // With inheriting from BlockWithEntity this defaults to INVISIBLE, so we need to change that!
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        if (ctx.getPlayer().isSneaking()) {
            return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection());
        }
        return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type != ModBlockEntities.LASER_COMBINER_BLOCK_ENTITY) return null;
        return (world1, pos, state1, be) -> LaserCombinerBlockEntity.tick(world1, pos, state1, (LaserCombinerBlockEntity) be);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        BeamTech.LOGGER.info("Creating Block Entity at {} for {} ", pos, BeamTech.MOD_ID);
        return new LaserCombinerBlockEntity(pos, state);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {

        InputUtil.Key boundSneakKey = ((KeybindingAccessor) MinecraftClient.getInstance().options.sneakKey).getBoundKey();

        if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), boundSneakKey.getCode())) {
            tooltip.add(Text.translatable("item.beamtech.laser_combiner.desc").formatted(Formatting.GRAY));
        }else {
            tooltip.add(Text.translatable("tooltip.beamtech.hold_crouch_desc",
                            Text.translatable(MinecraftClient.getInstance().options.sneakKey.getBoundKeyTranslationKey()).formatted(Formatting.GOLD))
                    .formatted(Formatting.GRAY));
        }

        super.appendTooltip(stack, world, tooltip, options);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack playerStack = player.getStackInHand(hand);

        BlockEntity entity = world.getBlockEntity(pos);

        if (entity instanceof LaserCombinerBlockEntity be) {
            ItemStack beStack = be.getStack(0);

            Item playerItem = playerStack.getItem();
            if (beStack.getItem() != Items.AIR) {
                // TODO: Make this use the .isValid method in the block entity.
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
                // TODO: Make this use the .isValid method in the block entity.
                if (playerItem == ModItems.FOCAL_LENS || playerItem == ModItems.RANGE_LENS) {
                    player.setStackInHand(hand, Items.AIR.getDefaultStack());
                    be.setStack(0, playerStack);
                }
            }
        }

//        player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
        return ActionResult.SUCCESS;
    }

}
