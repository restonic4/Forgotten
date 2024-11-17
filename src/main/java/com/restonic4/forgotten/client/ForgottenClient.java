package com.restonic4.forgotten.client;

import com.restonic4.forgotten.networking.PacketManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class ForgottenClient implements ClientModInitializer {
    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {
        PacketManager.registerServerToClient();

        ClientPlayConnectionEvents.DISCONNECT.register((clientPacketListener, minecraft) -> {
            DeathUtils.setDeathValue(false);
        });
    }
}
