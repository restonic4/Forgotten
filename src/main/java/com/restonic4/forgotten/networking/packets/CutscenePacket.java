package com.restonic4.forgotten.networking.packets;

import com.restonic4.forgotten.client.CachedClientData;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;

public class CutscenePacket {
    public static void receive(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf friendlyByteBuf, PacketSender packetSender) {
        CachedClientData.chainsCutsceneStartTime = System.currentTimeMillis();
        CachedClientData.chainsCutsceneEndTime = CachedClientData.chainsCutsceneStartTime + 5000;

        if (friendlyByteBuf.readBoolean()) {
            CachedClientData.chainsCutsceneEndTime -= 1500;
        }
    }
}
