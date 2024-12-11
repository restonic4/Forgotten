package com.restonic4.forgotten.commdands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.networking.PacketManager;
import com.restonic4.forgotten.networking.packets.SpawnStarPacket;
import com.restonic4.forgotten.saving.JsonDataManager;
import com.restonic4.forgotten.saving.StarData;
import com.restonic4.forgotten.util.StarSpawner;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

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

        StarSpawner.spawn(source.getServer());

        return 1;
    }
}
