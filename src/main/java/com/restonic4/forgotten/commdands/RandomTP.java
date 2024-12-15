package com.restonic4.forgotten.commdands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.restonic4.forgotten.util.RandomPlayerSpawnerManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class RandomTP {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("randomspawn")
                        .requires(source -> source.hasPermission(2))
                        .executes(RandomTP::execute)
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
            RandomPlayerSpawnerManager.forceSpawnRandomly(player);
        }

        return 1;
    }
}
