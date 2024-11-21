package com.restonic4.forgotten.client;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.restonic4.forgotten.networking.PacketManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;

public class ForgottenClient implements ClientModInitializer {
    /**
     * Runs the mod initializer on the client environment.
     */
    private boolean configured = false;

    @Override
    public void onInitializeClient() {
        PacketManager.registerServerToClient();

        ClientPlayConnectionEvents.DISCONNECT.register((clientPacketListener, minecraft) -> {
            DeathUtils.setDeathValue(false);
        });

        /*ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!configured && Minecraft.getInstance().getWindow().getWindow() != 0) {
                configured = true;
                configureWindow();
            }
        });*/
    }

    private void configureWindow() {
        Minecraft client = Minecraft.getInstance();
        long windowHandle = client.getWindow().getWindow();

        GLFW.glfwSetWindowSize(windowHandle, 300, 200);
        GLFW.glfwSetWindowPos(windowHandle, 400, 500);
    }
}
