package com.restonic4.forgotten.networking;

import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.networking.packets.BeamPacket;
import com.restonic4.forgotten.networking.packets.DeathPacket;
import com.restonic4.forgotten.networking.packets.MainRitualPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.resources.ResourceLocation;

public class PacketManager {
    public static final ResourceLocation DEATH = new ResourceLocation(Forgotten.MOD_ID, "death");
    public static final ResourceLocation BEAM = new ResourceLocation(Forgotten.MOD_ID, "beam");
    public static final ResourceLocation MAIN_RITUAL = new ResourceLocation(Forgotten.MOD_ID, "main_ritual");

    public static void registerServerToClient() {
        ClientPlayNetworking.registerGlobalReceiver(DEATH, DeathPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(BEAM, BeamPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(MAIN_RITUAL, MainRitualPacket::receive);
    }
}
