package com.gamingframe.beamtech.commands;

import com.gamingframe.beamtech.world.dimension.ModDimensions;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class TeleportCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("teleportdimension")
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    ServerWorld targetWorld = context.getSource().getServer().getWorld(ModDimensions.BEAMTECH_LEVEL_KEY);
                    if (player != null && targetWorld != null) {
                        player.teleport(targetWorld, 0,0,0, player.getYaw(), player.getPitch());
                        return 1;
                    }
                    return 0;
                })
        );
    }
}
