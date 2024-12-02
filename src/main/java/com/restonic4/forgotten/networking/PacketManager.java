package com.restonic4.forgotten.networking;

import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.networking.packets.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;

public class PacketManager {
    public static final ResourceLocation DEATH = new ResourceLocation(Forgotten.MOD_ID, "death");
    public static final ResourceLocation BEAM = new ResourceLocation(Forgotten.MOD_ID, "beam");
    public static final ResourceLocation LITTLE_SKY_WAVE = new ResourceLocation(Forgotten.MOD_ID, "little_sky_wave");
    public static final ResourceLocation MAIN_RITUAL = new ResourceLocation(Forgotten.MOD_ID, "main_ritual");
    public static final ResourceLocation INTERACTED_ITEM = new ResourceLocation(Forgotten.MOD_ID, "interacted_item");
    public static final ResourceLocation CHAIN_STATE = new ResourceLocation(Forgotten.MOD_ID, "chain_state");

    public static void registerServerToClient() {
        ClientPlayNetworking.registerGlobalReceiver(DEATH, DeathPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(BEAM, BeamPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(LITTLE_SKY_WAVE, LittleSkyWavePacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(MAIN_RITUAL, MainRitualPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(CHAIN_STATE, ChainStatePacket::receive);
    }

    public static void registerClientToServer() {
        ServerPlayNetworking.registerGlobalReceiver(INTERACTED_ITEM, InteractedItemPacket::receive);
    }
}
