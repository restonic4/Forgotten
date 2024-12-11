package com.restonic4.forgotten.networking.packets;

import com.restonic4.forgotten.client.rendering.ClientShootingStarManager;
import com.restonic4.forgotten.networking.PacketManager;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class SpawnStarPacket {
    public static void receive(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf friendlyByteBuf, PacketSender packetSender) {
        long spawnAnimationStart = System.currentTimeMillis();
        long spawnAnimationEnd = spawnAnimationStart + 20000;

        float size = friendlyByteBuf.readFloat();
        float rotation = friendlyByteBuf.readFloat();
        float posX = friendlyByteBuf.readFloat();
        float posY = friendlyByteBuf.readFloat();
        float posZ = friendlyByteBuf.readFloat();

        ClientShootingStarManager.loadStarDataFromServer(spawnAnimationStart, spawnAnimationEnd, size, rotation, posX, posY, posZ);
    }

    public static void sendToClient(ServerPlayer serverPlayer, float size, float rotation, float x, float y, float z) {
        FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();

        friendlyByteBuf.writeFloat(size);
        friendlyByteBuf.writeFloat(rotation);
        friendlyByteBuf.writeFloat(x);
        friendlyByteBuf.writeFloat(y);
        friendlyByteBuf.writeFloat(z);

        ServerPlayNetworking.send(serverPlayer, PacketManager.SPAWN_STAR, friendlyByteBuf);
    }
}
