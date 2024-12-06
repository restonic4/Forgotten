package com.restonic4.forgotten.networking.packets;

import com.restonic4.forgotten.client.CachedClientData;
import com.restonic4.forgotten.client.DeathUtils;
import com.restonic4.forgotten.registries.common.ForgottenSounds;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class HardcorePacket {
    public static void receive(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf friendlyByteBuf, PacketSender packetSender) {
        boolean isHardcore = friendlyByteBuf.readBoolean();
        CachedClientData.hardcoreStartTime = (isHardcore) ? 0 : -1;
    }
}
