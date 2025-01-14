package com.restonic4.forgotten.commdands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.restonic4.forgotten.util.ServerShootingStarManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class TestStarSpawn {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("test_star_spawn")
                        .requires(source -> source.hasPermission(2))
                        .executes(TestStarSpawn::execute)
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        ServerShootingStarManager.spawn(source.getServer());

        return 1;
    }
}
