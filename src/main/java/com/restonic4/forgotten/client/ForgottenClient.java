package com.restonic4.forgotten.client;

import com.restonic4.forgotten.client.gui.IrisScreen;
import com.restonic4.forgotten.networking.PacketManager;
import com.restonic4.forgotten.registries.client.CustomRenderTypes;
import com.restonic4.forgotten.registries.client.ForgottenEntityRenderers;
import com.restonic4.forgotten.util.CircleGenerator;
import com.restonic4.forgotten.util.LodestoneVars;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;
import team.lodestar.lodestone.registry.common.particle.LodestoneParticleRegistry;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;
import team.lodestar.lodestone.systems.particle.data.spin.SpinParticleData;

import java.awt.*;
import java.util.List;

public class ForgottenClient implements ClientModInitializer {
    /**
     * Runs the mod initializer on the client environment.
     */
    private boolean configured = false;
    private long lastTimeSpawned = System.currentTimeMillis();

    @Override
    public void onInitializeClient() {
        PacketManager.registerServerToClient();
        CustomRenderTypes.init();
        ForgottenEntityRenderers.register();

        ClientPlayConnectionEvents.DISCONNECT.register((clientPacketListener, minecraft) -> {
            DeathUtils.setDeathValue(false);
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (FabricLoader.getInstance().isModLoaded("iris") && (Minecraft.getInstance().screen == null || !(Minecraft.getInstance().screen instanceof IrisScreen))) {
                Minecraft.getInstance().forceSetScreen(new IrisScreen());
            }

            if (!configured && Minecraft.getInstance().getWindow().getWindow() != 0) {
                configured = true;
                configureWindow();
            }

            if (System.currentTimeMillis() > lastTimeSpawned + 3000 && Minecraft.getInstance().level != null) {
                lastTimeSpawned = System.currentTimeMillis();

                spawnParticles(Minecraft.getInstance());
            }
        });
    }

    private void spawnParticles(Minecraft minecraft) {
        Vec3 targetPointRing = new Vec3(0, 10, 0);

        float radius = 20;
        int precision = 100;

        String string = CustomRenderTypes.COOL_PARTICLE.getInstance().get().getName();
        //ResourceLocation thing = new ResourceLocation("shaders/core/" + string + ".json");

        List<CircleGenerator.CirclePoint> circle = CircleGenerator.generateCircle(radius, precision);

        Color startingColor = new Color(255, 179, 0);
        Color endingColor = new Color(91, 10, 146);

        for (int i = 0; i < circle.size(); i++) {
            CircleGenerator.CirclePoint point = circle.get(i);

            WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                    .setScaleData(GenericParticleData.create(2, 14).build())
                    .setTransparencyData(GenericParticleData.create(1, 0f).build())
                    .setColorData(ColorParticleData.create(startingColor, endingColor).setCoefficient(1.4f).setEasing(Easing.BOUNCE_IN_OUT).build())
                    .setSpinData(SpinParticleData.create(0.2f, 0.4f).setSpinOffset((minecraft.level.getGameTime() * 0.2f) % 6.28f).setEasing(Easing.QUARTIC_IN).build())
                    .setLifetime(300)
                    .addMotion(-point.toCenter.x, 0, -point.toCenter.y)
                    .enableNoClip()
                    .setRenderType(LodestoneVars.renderType)
                    .setRenderTarget(LodestoneVars.renderTarget)
                    .enableForcedSpawn()
                    .spawn(minecraft.level, targetPointRing.x, targetPointRing.y, targetPointRing.z);
        }
    }

    private void configureWindow() {
        Minecraft client = Minecraft.getInstance();
        long windowHandle = client.getWindow().getWindow();

        GLFW.glfwSetWindowSize(windowHandle, 300, 200);
        GLFW.glfwSetWindowPos(windowHandle, 400, 500);
    }
}
