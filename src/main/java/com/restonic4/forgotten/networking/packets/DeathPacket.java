package com.restonic4.forgotten.networking.packets;

import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.client.DeathUtils;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class DeathPacket {
    public static void receive(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf friendlyByteBuf, PacketSender packetSender) {
        boolean shouldHaveDeathShader = friendlyByteBuf.readBoolean();

        DeathUtils.setDeathValue(shouldHaveDeathShader);

        minecraft.execute(() -> {
            if (minecraft.level != null && minecraft.player != null) {
                minecraft.level.playLocalSound(minecraft.player.blockPosition(), Forgotten.DEATH_SOUND, SoundSource.PLAYERS, 1, 1, false);
                minecraft.level.playLocalSound(minecraft.player.blockPosition(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 1, 1, false);
            }
        });
    }
}
