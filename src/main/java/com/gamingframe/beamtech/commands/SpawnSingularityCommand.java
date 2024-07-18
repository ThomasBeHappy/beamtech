package com.gamingframe.beamtech.commands;

import com.gamingframe.beamtech.world.dimension.ModDimensions;
import com.gamingframe.beamtech.worldevents.events.SingularityEvent;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import team.lodestar.lodestone.handlers.WorldEventHandler;

public class SpawnSingularityCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("spawnsingularity")
                .executes(context -> {
                    WorldEventHandler.addWorldEvent(context.getSource().getWorld(), new SingularityEvent());
                    return 1;
                })
        );
    }
}
