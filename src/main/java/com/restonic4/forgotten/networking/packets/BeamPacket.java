package com.restonic4.forgotten.networking.packets;

import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.client.CachedClientData;
import com.restonic4.forgotten.client.rendering.BeamEffect;
import com.restonic4.forgotten.client.rendering.BeamEffectManager;
import com.restonic4.forgotten.client.rendering.SkyWaveEffectManager;
import com.restonic4.forgotten.networking.PacketManager;
import com.restonic4.forgotten.registries.common.ForgottenSounds;
import com.restonic4.forgotten.saving.JsonDataManager;
import com.restonic4.forgotten.util.helpers.MathHelper;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import team.lodestar.lodestone.handlers.ScreenshakeHandler;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.screenshake.ScreenshakeInstance;

import java.awt.*;

public class BeamPacket {
    private static final float MAX_DISTANCE = 5000;

    public static void receive(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf friendlyByteBuf, PacketSender packetSender) {
        if (minecraft.player == null || minecraft.level == null) {
            return;
        }

        Vec3 receivedCenter = friendlyByteBuf.readBlockPos().getCenter();
        Vec3 playerPos = minecraft.player.position();

        Vec3 directionToCenter = receivedCenter.subtract(playerPos);
        double distanceToCenter = directionToCenter.length();

        if (distanceToCenter > MAX_DISTANCE) {
            directionToCenter = directionToCenter.normalize().scale(MAX_DISTANCE);
        }

        Vec3 beamCenter = playerPos.add(directionToCenter);

        Color beamColor = new Color(0.3f, 1, 1, 1);

        spawnSkyWave(minecraft, beamCenter.toVector3f(), beamColor, 10, true);

        spawnBeam(minecraft, beamCenter.toVector3f(), beamColor);
    }

    private static void spawnSkyWave(Minecraft minecraft, Vector3f position, Color color, float lifetime, boolean shouldPlaySound) {
        minecraft.execute(() -> {
            SkyWaveEffectManager.create()
                    .lifetime(lifetime)
                    .setPosition(position)
                    .height(2000)
                    .color(color)
                    .offsetActionBeforeHead(0.15f)
                    .actionExecutedBeforeAbovePlayerHead(() -> {
                        ScreenshakeInstance roarScreenShake = new ScreenshakeInstance(5 * 20).setEasing(Easing.QUAD_IN_OUT).setIntensity(0.0f, 0.75f, 0.0f);
                        ScreenshakeHandler.addScreenshake(roarScreenShake);
                    })
                    .actionExecutedAbovePlayerHead(() -> {
                        if (shouldPlaySound) {
                            minecraft.execute(() -> {
                                if (minecraft.player == null || minecraft.level == null) {
                                    return;
                                }

                                BlockPos blockPos = minecraft.player.blockPosition();

                                minecraft.level.playLocalSound(blockPos.getX(), blockPos.getY(), blockPos.getZ(), ForgottenSounds.WAVE, SoundSource.AMBIENT, 1, 1, false);
                            });
                        }
                    });
        });
    }

    private static void spawnBeam(Minecraft minecraft, Vector3f center, Color color) {
        minecraft.execute(() -> {
            if (minecraft.level != null && minecraft.player != null) {
                float beamR = MathHelper.getNormalizedColorR(color);
                float beamG = MathHelper.getNormalizedColorG(color);
                float beamB = MathHelper.getNormalizedColorB(color);

                BeamEffectManager.create()
                        .lifetime(10)
                        .setPosition(center)
                        .timeBetweenFades(4f)
                        .setFadeInAnimation(BeamEffect.EASED_SCALE_IN)
                        .setFadeOutAnimation(BeamEffect.EASED_SCALE_OUT)
                        .addLayer(2, 2020, new Color(beamR, beamG, beamB, 1))
                        .addLayer(4, 2020, new Color(beamR, beamG, beamB, 0.75f))
                        .addLayer(6, 2020, new Color(beamR, beamG, beamB, 0.5f))
                        .addLayer(8, 2020, new Color(beamR, beamG, beamB, 0.25f));

                BlockPos blockPos = minecraft.player.blockPosition();

                minecraft.level.playLocalSound(blockPos.getX(), blockPos.getY(), blockPos.getZ(), ForgottenSounds.EXPLOSION, SoundSource.AMBIENT, 1, 1, false);
            }
        });
    }

    public static void sendToClient(ServerPlayer serverPlayer) {
        JsonDataManager dataManager = Forgotten.getDataManager();

        if (!dataManager.contains("center")) {
            return;
        }

        FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();
        friendlyByteBuf.writeBlockPos(dataManager.getBlockPos("center").offset(0, -8, 0));
        ServerPlayNetworking.send(serverPlayer, PacketManager.BEAM, friendlyByteBuf);
    }
}
