package com.restonic4.forgotten.commdands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.restonic4.forgotten.util.ServerShootingStarManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class TestShootStar {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("test_star_shoot")
                        .requires(source -> source.hasPermission(2))
                        .executes(TestShootStar::execute)
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        ServerShootingStarManager.shootStar(source.getServer());

        return 1;
    }
}
