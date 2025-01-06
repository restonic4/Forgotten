package com.restonic4.forgotten.client;

import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.client.gui.IrisScreen;
import com.restonic4.forgotten.compatibility.exordium.Overrides;
import com.restonic4.forgotten.networking.PacketManager;
import com.restonic4.forgotten.registries.client.ForgottenEntityRenderers;
import com.restonic4.forgotten.registries.client.ForgottenMaterials;
import com.restonic4.forgotten.registries.client.ForgottenShaderHolders;
import com.restonic4.forgotten.registries.common.ForgottenBlocks;
import com.restonic4.forgotten.registries.common.ForgottenParticleTypes;
import com.restonic4.forgotten.saving.SaveManager;
import com.restonic4.forgotten.util.ModCheck;
import com.restonic4.forgotten.util.helpers.CircleGenerator;
import com.restonic4.forgotten.util.helpers.RenderingHelper;
import com.restonic4.forgotten.util.trash.TestingVars;
import com.restonic4.under_control.api.whitelist.WhitelistAPI;
import com.restonic4.under_control.client.gui.FatalErrorScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;
import team.lodestar.lodestone.systems.particle.data.spin.SpinParticleData;

import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ForgottenClient implements ClientModInitializer {
    /**
     * Runs the mod initializer on the client environment.
     */
    private boolean configured = false;
    private long lastTimeSpawned = System.currentTimeMillis();
    public static long currentTime = System.currentTimeMillis();
    private int status = -1;

    int ticksLeft = 0;
    int tickSaveCounter = 0;

    @Override
    public void onInitializeClient() {
        SaveManager.getClientInstance(Minecraft.getInstance()).loadFromFile();

        PacketManager.registerServerToClient();
        ForgottenShaderHolders.register();
        ForgottenParticleTypes.registerClient();
        ForgottenEntityRenderers.register();
        ForgottenBlocks.registerClient();
        ForgottenMaterials.register();
        ModCheck.check();

        WhitelistAPI.registerWhitelist(Forgotten.MOD_ID, "earlyAccess", (UUID uuid) -> {
            try {
                HttpClient client = HttpClient.newHttpClient();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://chaotic-loom.com/api/testers"))
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    String responseBody = response.body();
                    List<String> testers = Arrays.asList(responseBody.replace("[", "").replace("]", "").replace("\"", "").split(","));

                    return testers.contains(uuid.toString());
                } else {
                    System.out.println("Error: " + response.statusCode());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        });

        ClientPlayConnectionEvents.DISCONNECT.register((clientPacketListener, minecraft) -> {
            DeathUtils.setDeathValue(false);
            SaveManager.getClientInstance(minecraft).saveToFile();
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            tickSaveCounter++;
            if (tickSaveCounter >= 3000) {
                SaveManager.getClientInstance(client).saveToFile();
                tickSaveCounter = 0;
            }

            if (FabricLoader.getInstance().isModLoaded("iris") && (Minecraft.getInstance().screen == null || !(Minecraft.getInstance().screen instanceof IrisScreen))) {
                Minecraft.getInstance().forceSetScreen(new IrisScreen());
            }

            if (FabricLoader.getInstance().isModLoaded("exordium")) {
                Overrides.override();
            }

            if (status == -1) {
                status = (WhitelistAPI.isAllowed(Forgotten.MOD_ID, "earlyAccess", client.getUser().getGameProfile().getId())) ? 1 : 0;
            }

            if (status == 0) {
                Minecraft.getInstance().forceSetScreen(new FatalErrorScreen(Component.literal("Access denied!"), Component.literal("Forgotten is in early access, how the fuck did you got the mod?")));
            }

            if (System.currentTimeMillis() > lastTimeSpawned + 3000 && Minecraft.getInstance().level != null) {
                lastTimeSpawned = System.currentTimeMillis();

                //spawnParticles(Minecraft.getInstance());
            }
        });
    }

    private void spawnParticles(Minecraft minecraft) {
        Vec3 targetPointRing = new Vec3(0, 10, 0);

        float radius = 20;
        int precision = 100;

        String string = ForgottenShaderHolders.LUMITRANSPARENT_NO_FOG_PARTICLE.getInstance().get().getName();
        //ResourceLocation thing = new ResourceLocation("shaders/core/" + string + ".json");

        List<CircleGenerator.CirclePoint> circle = CircleGenerator.generateCircle(radius, precision);

        Color startingColor = new Color(255, 179, 0);
        Color endingColor = new Color(91, 10, 146);

        for (int i = 0; i < circle.size(); i++) {
            CircleGenerator.CirclePoint point = circle.get(i);

            WorldParticleBuilder.create(ForgottenParticleTypes.WISP_PARTICLE)
                    .setScaleData(GenericParticleData.create(2, 14).build())
                    .setTransparencyData(GenericParticleData.create(1, 0f).build())
                    .setColorData(ColorParticleData.create(startingColor, endingColor).setCoefficient(1.4f).setEasing(Easing.BOUNCE_IN_OUT).build())
                    .setSpinData(SpinParticleData.create(0.2f, 0.4f).setSpinOffset((minecraft.level.getGameTime() * 0.2f) % 6.28f).setEasing(Easing.QUARTIC_IN).build())
                    .setLifetime(300)
                    .addMotion(-point.toCenter.x, 0, -point.toCenter.y)
                    .enableNoClip()
                    .setRenderType(TestingVars.renderType)
                    .setRenderTarget(TestingVars.renderTarget)
                    .enableForcedSpawn()
                    .spawn(minecraft.level, targetPointRing.x, targetPointRing.y, targetPointRing.z);

            /*WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
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
                    .spawn(minecraft.level, targetPointRing.x, targetPointRing.y + 20, targetPointRing.z);

            WorldParticleBuilder.create(LodestoneParticleRegistry.SMOKE_PARTICLE)
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
                    .spawn(minecraft.level, targetPointRing.x, targetPointRing.y + 40, targetPointRing.z);

            WorldParticleBuilder.create(LodestoneParticleRegistry.SPARK_PARTICLE)
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
                    .spawn(minecraft.level, targetPointRing.x, targetPointRing.y + 60, targetPointRing.z);*/
        }
    }

    private void configureWindow() {
        Minecraft client = Minecraft.getInstance();
        long windowHandle = client.getWindow().getWindow();

        GLFW.glfwSetWindowSize(windowHandle, 300, 200);
        GLFW.glfwSetWindowPos(windowHandle, 400, 500);
    }
}
