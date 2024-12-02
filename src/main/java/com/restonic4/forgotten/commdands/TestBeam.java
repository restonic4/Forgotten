package com.restonic4.forgotten.commdands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.networking.PacketManager;
import com.restonic4.forgotten.saving.JsonDataManager;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import team.lodestar.lodestone.handlers.ScreenshakeHandler;
import team.lodestar.lodestone.registry.common.particle.LodestoneParticleRegistry;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;
import team.lodestar.lodestone.systems.particle.data.spin.SpinParticleData;
import team.lodestar.lodestone.systems.screenshake.PositionedScreenshakeInstance;
import team.lodestar.lodestone.systems.screenshake.ScreenshakeInstance;

import java.awt.*;

public class TestBeam {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("test_beam")
                        .requires(source -> source.hasPermission(2))
                        .executes(TestBeam::execute)
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        for (ServerPlayer serverPlayer : source.getServer().getPlayerList().getPlayers()) {
            JsonDataManager dataManager = Forgotten.getDataManager();

            if (!dataManager.contains("center")) {
                source.sendSystemMessage(Component.literal("The mod has not been initialized"));
                return 1;
            }

            FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();
            friendlyByteBuf.writeBlockPos(dataManager.getBlockPos("center").offset(0, -8, 0));
            ServerPlayNetworking.send(serverPlayer, PacketManager.BEAM, friendlyByteBuf);
        }

        return 1;
    }
}
