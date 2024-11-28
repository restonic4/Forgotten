package com.restonic4.forgotten.networking.packets;

import com.restonic4.forgotten.client.CachedClientData;
import com.restonic4.forgotten.client.rendering.BeamEffect;
import com.restonic4.forgotten.client.rendering.BeamEffectManager;
import com.restonic4.forgotten.client.rendering.SkyWaveEffectManager;
import com.restonic4.forgotten.registries.common.ForgottenSounds;
import com.restonic4.forgotten.util.helpers.MathHelper;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import team.lodestar.lodestone.handlers.ScreenshakeHandler;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.screenshake.ScreenshakeInstance;

import java.awt.*;

public class MainRitualPacket {
    public static void receive(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf friendlyByteBuf, PacketSender packetSender) {
        if (minecraft.player == null || minecraft.level == null) {
            return;
        }

        Vec3 beamCenter = new Vec3(0, 0, 0);
        //Color beamColor = new Color(1, 0.169f, 0.169f, 1);

        //Color beamColor = new Color(1, 0, 0.31f, 1);
        Color beamColor = new Color(1, 0, 0.227f, 1);

        minecraft.execute(() -> {
            if (minecraft.player == null || minecraft.level == null) {
                return;
            }

            BlockPos blockPos = minecraft.player.blockPosition();

            minecraft.level.playLocalSound(blockPos.getX(), blockPos.getY(), blockPos.getZ(), ForgottenSounds.MAIN_RITUAL_BACKGROUND, SoundSource.AMBIENT, 1, 1, false);
            minecraft.level.playLocalSound(beamCenter.x, beamCenter.y, beamCenter.z, ForgottenSounds.BEAM, SoundSource.AMBIENT, 1, 1, false);
            minecraft.level.playLocalSound(blockPos.getX(), blockPos.getY(), blockPos.getZ(), ForgottenSounds.BEAM_STEREO, SoundSource.AMBIENT, 0.25f, 1, false);
        });

        spawnBeam(minecraft, beamCenter.toVector3f(), 30, beamColor);

        new Thread(() -> {
            try {
                Thread.sleep(15000);
            } catch (Exception ignored) {}

            spawnSkyWave(minecraft, beamCenter.toVector3f(), beamColor, 10, true);

            for (int i = 0; i < 10; i++) {
                spawnSkyWave(minecraft, beamCenter.toVector3f().sub(0, 10, 0), beamColor, 12 + 2 * i, false);
            }

            CachedClientData.hearthsShakeAnimationStartTime = System.currentTimeMillis();
            CachedClientData.hearthsShakeAnimationEndTime = CachedClientData.hearthsShakeAnimationStartTime + 5000;

            CachedClientData.hearthsRitualAnimationStartTime = CachedClientData.hearthsShakeAnimationEndTime;
            CachedClientData.hearthsRitualAnimationEndTime = CachedClientData.hearthsRitualAnimationStartTime + 10000;

            CachedClientData.hearthsRitualFinishAnimationStartTime = CachedClientData.hearthsRitualAnimationEndTime;
            CachedClientData.hearthsRitualFinishAnimationEndTime = CachedClientData.hearthsRitualFinishAnimationStartTime + 2000;

            CachedClientData.hardcoreStartTime = CachedClientData.hearthsRitualFinishAnimationEndTime;
        }).start();
    }

    private static void spawnSkyWave(Minecraft minecraft, Vector3f position, Color color, float lifetime, boolean shouldPlaySound) {
        minecraft.execute(() -> {
            SkyWaveEffectManager.create()
                    .lifetime(lifetime)
                    .setPosition(position)
                    .height(1000)
                    .color(color)
                    .offsetActionBeforeHead(0.15f)
                    .actionExecutedBeforeAbovePlayerHead(() -> {
                        if (shouldPlaySound) {
                            ScreenshakeInstance roarScreenShake = new ScreenshakeInstance(15 * 20).setEasing(Easing.CUBIC_IN, Easing.QUAD_IN_OUT).setIntensity(0.0f, 0.75f, 0.0f);
                            ScreenshakeHandler.addScreenshake(roarScreenShake);
                        }
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

    private static void spawnBeam(Minecraft minecraft, Vector3f center, float lifetime, Color color) {
        minecraft.execute(() -> {
            if (minecraft.level != null && minecraft.player != null) {
                float beamR = MathHelper.getNormalizedColorR(color);
                float beamG = MathHelper.getNormalizedColorG(color);
                float beamB = MathHelper.getNormalizedColorB(color);

                BeamEffectManager.create()
                        .lifetime(lifetime)
                        .setPosition(center)
                        .timeBetweenFades(4f)
                        .setFadeInAnimation(BeamEffect.EASED_SCALE_IN)
                        .setFadeOutAnimation(BeamEffect.EASED_SCALE_OUT)
                        .addLayer(4, 1020, new Color(beamR, beamG, beamB, 1))
                        .addLayer(8, 1020, new Color(beamR, beamG, beamB, 0.75f))
                        .addLayer(12, 1020, new Color(beamR, beamG, beamB, 0.5f))
                        .addLayer(16, 1020, new Color(beamR, beamG, beamB, 0.25f))
                        .addLayer(20, 1020, new Color(beamR, beamG, beamB, 0.1f));

                BlockPos blockPos = minecraft.player.blockPosition();

                minecraft.level.playLocalSound(blockPos.getX(), blockPos.getY(), blockPos.getZ(), ForgottenSounds.EXPLOSION, SoundSource.AMBIENT, 1, 1, false);
            }
        });
    }
}
