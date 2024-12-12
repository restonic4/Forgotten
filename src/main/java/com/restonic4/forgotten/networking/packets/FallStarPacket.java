package com.restonic4.forgotten.networking.packets;

import com.restonic4.forgotten.client.rendering.ClientShootingStarManager;
import com.restonic4.forgotten.networking.PacketManager;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class FallStarPacket {
    public static final int ANIMATION_TIME = 1500;

    public static void receive(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf friendlyByteBuf, PacketSender packetSender) {
        BlockPos collisionPoint = friendlyByteBuf.readBlockPos();

        long startAnim = System.currentTimeMillis();
        long endAnim = startAnim + ANIMATION_TIME;

        ClientShootingStarManager.loadShootingStar(startAnim, endAnim, collisionPoint);
    }

    public static void sendToClient(ServerPlayer serverPlayer, BlockPos collisionPoint) {
        FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();

        friendlyByteBuf.writeBlockPos(collisionPoint);

        ServerPlayNetworking.send(serverPlayer, PacketManager.FALL_STAR, friendlyByteBuf);
    }
}
