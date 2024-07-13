package com.gamingframe.beamtech.block.custom;

import com.gamingframe.beamtech.BeamTech;
import com.gamingframe.beamtech.classes.MirrorRotation;
import com.gamingframe.beamtech.mixin.KeybindingAccessor;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import com.gamingframe.beamtech.classes.Point;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MirrorBlock extends HorizontalFacingBlock {
    public static final EnumProperty<MirrorRotation> ROTATION = EnumProperty.of("rotation", MirrorRotation.class);
    public static final BooleanProperty POWERED = BooleanProperty.of("powered");

    public MirrorBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(ROTATION, MirrorRotation.ROTATE_NE).with(POWERED, false));
    }


    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        if (!world.isClient) {
            boolean isPowered = world.isReceivingRedstonePower(pos);
            boolean wasPowered = state.get(POWERED);
            if (isPowered && !wasPowered) {
                // Handle redstone input
                System.out.println("Redstone power received!");
                world.setBlockState(pos, state.with(POWERED, true), 3);
            } else if (!isPowered && wasPowered) {
                world.setBlockState(pos, state.with(POWERED, false), 3);
            }
        }
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
    }


    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ROTATION, POWERED);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(ROTATION, MirrorRotation.ROTATE_NE);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        world.scheduleBlockTick(pos, this, 1);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            MirrorRotation currentRotation = state.get(ROTATION);
            MirrorRotation newRotation = currentRotation.next();
            BeamTech.LOGGER.info("Used mirror, new rotation {} with normal {}", newRotation, newRotation.normal);
            world.setBlockState(pos, state.with(ROTATION, newRotation));
        }
        return ActionResult.SUCCESS;
    }

    public Vec3d reflectLaser(BlockState mirrorState, Vec3d incomingDirection) {
        MirrorRotation rotation = mirrorState.get(ROTATION);

        return MirrorRotation.calculateReflection(incomingDirection, rotation);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {

        InputUtil.Key boundSneakKey = ((KeybindingAccessor) MinecraftClient.getInstance().options.sneakKey).getBoundKey();

        if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), boundSneakKey.getCode())) {
            tooltip.add(Text.translatable("item.beamtech.mirror.desc").formatted(Formatting.GRAY));
        }else {
            tooltip.add(Text.translatable("tooltip.beamtech.hold_crouch_desc",
                            Text.translatable(MinecraftClient.getInstance().options.sneakKey.getBoundKeyTranslationKey()).formatted(Formatting.GOLD))
                    .formatted(Formatting.GRAY));
        }

        super.appendTooltip(stack, world, tooltip, options);
    }

    public int getNewStartPos(BlockState mirrorState, BlockPos mirrorPos, Vec3d hitPos, Vec3d direction, AtomicReference<Vec3d> newPos) {
        MirrorRotation rotation = mirrorState.get(ROTATION);
        Vec3d mirrorCenterPos = mirrorPos.toCenterPos();

        Vec3d offset = hitPos.subtract(mirrorCenterPos).multiply(16);
        Point hitOnMirror =  new Point((int) offset.x, (int) offset.z);

        BeamTech.LOGGER.info("Offset {} HitOnMirror {} with Direction {}", offset, hitOnMirror, direction);

        Point intersect = rotation.CalculateIntersect(new Point(direction.x * 16,direction.z * 16), hitOnMirror);

        if (intersect == null) {
            return 0;
        }else if (intersect.x == 9 && intersect.y == 9) {
            return -1;
        }

        newPos.set(new Vec3d(intersect.x / 16, 0, intersect.y / 16).add(mirrorCenterPos));
        return 1;
    }
}

