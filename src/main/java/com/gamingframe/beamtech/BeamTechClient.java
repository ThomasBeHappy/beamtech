package com.gamingframe.beamtech;

import com.gamingframe.beamtech.block.ModBlockEntities;
import com.gamingframe.beamtech.block.custom.entity.LaserBlockEntity;
import com.gamingframe.beamtech.block.custom.entity.renderer.LaserBlockEntityRenderer;
import com.gamingframe.beamtech.screens.EmitterBlockScreen;
import com.gamingframe.beamtech.screens.EmitterGUI;
import com.gamingframe.beamtech.screens.ModGUIs;
import com.gamingframe.beamtech.shader.MirrorReflectionPostProcessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import team.lodestar.lodestone.systems.postprocess.PostProcessHandler;

@Environment(EnvType.CLIENT)
public class BeamTechClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HandledScreens.<EmitterGUI, EmitterBlockScreen>register(ModGUIs.EMITTER_GUI, (gui, inventory, title) -> new EmitterBlockScreen(gui, inventory.player, title));
        ClientPlayNetworking.registerGlobalReceiver(new Identifier(BeamTech.MOD_ID, "update_laser_lens"), (client, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            ItemStack itemStack = buf.readItemStack();

            BlockEntity blockEntity = client.world.getBlockEntity(pos);
            if (blockEntity instanceof LaserBlockEntity laserBlockEntity) {
                laserBlockEntity.syncInventory(itemStack);
            }
        });

        BlockEntityRendererFactories.register(ModBlockEntities.LASER_EMITTER_BLOCK_ENTITY, LaserBlockEntityRenderer::new);

        PostProcessHandler.addInstance(MirrorReflectionPostProcessor.INSTANCE);
    }
}
