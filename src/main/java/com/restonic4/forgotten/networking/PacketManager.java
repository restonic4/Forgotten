package com.restonic4.forgotten.networking;

import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.networking.packets.DeathPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.resources.ResourceLocation;

public class PacketManager {
    public static final ResourceLocation DEATH = new ResourceLocation(Forgotten.MOD_ID, "death");

    public static void registerServerToClient() {
        ClientPlayNetworking.registerGlobalReceiver(DEATH, DeathPacket::receive);
    }
}
