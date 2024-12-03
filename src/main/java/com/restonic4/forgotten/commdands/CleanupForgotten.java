package com.restonic4.forgotten.commdands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.restonic4.forgotten.entity.common.ChainEntity;
import com.restonic4.forgotten.entity.common.SmallCoreEntity;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

public class CleanupForgotten {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("cleanup_forgotten")
                        .requires(source -> source.hasPermission(2)).executes(CleanupForgotten::execute)
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();

        ServerLevel serverLevel = source.getLevel();

        Iterable<Entity> entities = serverLevel.getAllEntities();

        for (Entity entity : entities) {
            if (entity instanceof ChainEntity chain) {
                chain.discard();
            }

            if (entity instanceof SmallCoreEntity smallCoreEntity) {
                smallCoreEntity.discard();
            }
        }

        return 1;
    }
}
