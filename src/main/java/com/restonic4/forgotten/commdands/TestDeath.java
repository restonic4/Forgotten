package com.restonic4.forgotten.commdands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.restonic4.forgotten.networking.PacketManager;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class TestDeath {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("test_death")
                        .requires(source -> source.hasPermission(2))
                        .executes(TestDeath::execute)
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        for (ServerPlayer serverPlayer : source.getServer().getPlayerList().getPlayers()) {
            FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();
            friendlyByteBuf.writeBoolean(true);
            ServerPlayNetworking.send(serverPlayer, PacketManager.DEATH, friendlyByteBuf);
        }

        return 1;
    }
}
