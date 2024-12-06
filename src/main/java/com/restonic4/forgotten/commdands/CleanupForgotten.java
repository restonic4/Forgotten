package com.restonic4.forgotten.commdands;

import com.google.common.collect.Iterables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.restonic4.forgotten.entity.common.ChainEntity;
import com.restonic4.forgotten.entity.common.CoreEntity;
import com.restonic4.forgotten.entity.common.SmallCoreEntity;
import com.restonic4.forgotten.util.ServerCache;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class CleanupForgotten {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("cleanup_forgotten")
                        .requires(source -> source.hasPermission(2)).executes(CleanupForgotten::execute)
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();

        try {
            for (ChainEntity entity : ServerCache.chains) {
                entity.discard();
            }

            for (SmallCoreEntity entity : ServerCache.cores) {
                entity.discard();
            }

            for (CoreEntity entity : ServerCache.coresMain) {
                entity.discard();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

        ServerCache.coresMain.clear();
        ServerCache.cores.clear();
        ServerCache.chains.clear();

        return 1;
    }
}
