package com.restonic4.forgotten.commdands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.networking.PacketManager;
import com.restonic4.forgotten.saving.SaveManager;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class TestMainRitual {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("test_main_ritual")
                        .requires(source -> source.hasPermission(2))
                        .executes(TestMainRitual::execute)
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        try {
            for (ServerPlayer serverPlayer : source.getServer().getPlayerList().getPlayers()) {
                SaveManager saveManager = SaveManager.getInstance(source.getServer());

                if (!saveManager.containsKey("center")) {
                    source.sendSystemMessage(Component.literal("The mod has not been initialized"));
                    return 1;
                }

                FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();
                friendlyByteBuf.writeBlockPos(saveManager.get("center", BlockPos.class).offset(0, -8, 0));
                ServerPlayNetworking.send(serverPlayer, PacketManager.MAIN_RITUAL, friendlyByteBuf);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 1;
    }
}
