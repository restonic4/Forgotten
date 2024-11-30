package com.restonic4.forgotten.networking.packets;

import com.restonic4.forgotten.client.CachedClientData;
import com.restonic4.forgotten.client.rendering.*;
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
import team.lodestar.lodestone.systems.screenshake.PositionedScreenshakeInstance;
import team.lodestar.lodestone.systems.screenshake.ScreenshakeInstance;

import java.awt.*;

public class MainRitualPacket {
    private static final float MAX_DISTANCE = 5000;

    public static void receive(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf friendlyByteBuf, PacketSender packetSender) {
        if (minecraft.player == null || minecraft.level == null) {
            return;
        }

        Vec3 receivedCenter = new Vec3(0, 0, 0);
        Vec3 playerPos = minecraft.player.position();

        Vec3 directionToCenter = receivedCenter.subtract(playerPos);
        double distanceToCenter = directionToCenter.length();

        if (distanceToCenter > MAX_DISTANCE) {
            directionToCenter = directionToCenter.normalize().scale(MAX_DISTANCE);
        }

        Vec3 beamCenter = playerPos.add(directionToCenter);

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
        spawnEnergyOrb(minecraft, beamCenter.toVector3f().setComponent(1, 2000), 100, 15, 4, beamColor);

        new Thread(() -> {
            Vector3f playerPosVec = minecraft.player.position().toVector3f();
            float distance = playerPosVec.distance(beamCenter.toVector3f());
            float maxBeamShakeDistance = 500;

            if (distance < maxBeamShakeDistance) {
                ScreenshakeInstance beamShake = new PositionedScreenshakeInstance(30 * 20, beamCenter, 100, maxBeamShakeDistance).setEasing(Easing.CUBIC_IN, Easing.QUAD_IN_OUT).setIntensity(0.0f, 0.75f, 0.0f);
                ScreenshakeHandler.addScreenshake(beamShake);
            }

            try {
                Thread.sleep(14000);
            } catch (Exception ignored) {}

            spawnEnergyOrb(minecraft, beamCenter.toVector3f().setComponent(1, 2000), 400, 2, 0, beamColor);

            try {
                Thread.sleep(1000);
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

            try {
                Thread.sleep(1000);
            } catch (Exception ignored) {}

            ScreenshakeInstance orbShake = new ScreenshakeInstance(4 * 20).setEasing(Easing.CUBIC_IN, Easing.QUAD_IN_OUT).setIntensity(0.75f, 0.8f, 0.6f);
            ScreenshakeHandler.addScreenshake(orbShake);

            minecraft.execute(() -> {
                if (minecraft.player == null || minecraft.level == null) {
                    return;
                }

                BlockPos blockPos = minecraft.player.blockPosition();

                minecraft.level.playLocalSound(blockPos.getX(), blockPos.getY(), blockPos.getZ(), ForgottenSounds.EXPLOSION, SoundSource.AMBIENT, 1, 1, false);
            });

            try {
                Thread.sleep(1000);
            } catch (Exception ignored) {}

            minecraft.execute(() -> {
                if (minecraft.player == null || minecraft.level == null) {
                    return;
                }

                BlockPos blockPos = minecraft.player.blockPosition();

                minecraft.level.playLocalSound(blockPos.getX(), blockPos.getY(), blockPos.getZ(), ForgottenSounds.WAVE_EXPLOSION, SoundSource.AMBIENT, 1, 1, false);
            });

            ScreenshakeInstance waveShake = new ScreenshakeInstance(20 * 20).setEasing(Easing.CUBIC_IN, Easing.QUAD_IN_OUT).setIntensity(0.6f, 0.65f, 0.45f);
            ScreenshakeHandler.addScreenshake(waveShake);
        }).start();
    }

    private static void spawnSkyWave(Minecraft minecraft, Vector3f position, Color color, float lifetime, boolean shouldPlaySound) {
        minecraft.execute(() -> {
            SkyWaveEffectManager.create()
                    .lifetime(lifetime)
                    .setPosition(position)
                    .height(2000)
                    .color(color)
                    .offsetActionBeforeHead(0.15f)
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
                        .addLayer(4, 2020, new Color(beamR, beamG, beamB, 1))
                        .addLayer(8, 2020, new Color(beamR, beamG, beamB, 0.75f))
                        .addLayer(12, 2020, new Color(beamR, beamG, beamB, 0.5f))
                        .addLayer(16, 2020, new Color(beamR, beamG, beamB, 0.25f))
                        .addLayer(20, 2020, new Color(beamR, beamG, beamB, 0.1f));

                BlockPos blockPos = minecraft.player.blockPosition();
                minecraft.level.playLocalSound(blockPos.getX(), blockPos.getY(), blockPos.getZ(), ForgottenSounds.EXPLOSION, SoundSource.AMBIENT, 1, 1, false);
            }
        });
    }

    private static void spawnEnergyOrb(Minecraft minecraft, Vector3f center, float radius, float lifetime, float timeBetweenFades, Color color) {
        minecraft.execute(() -> {
            float beamR = MathHelper.getNormalizedColorR(color);
            float beamG = MathHelper.getNormalizedColorG(color);
            float beamB = MathHelper.getNormalizedColorB(color);

            if (minecraft.level != null && minecraft.player != null) {
                EnergyOrbEffectManager.create()
                        .lifetime(lifetime)
                        .setPosition(center)
                        .timeBetweenFades(timeBetweenFades)
                        .setFadeInAnimation(EnergyOrbEffect.EASED_SCALE_IN)
                        .setFadeOutAnimation(EnergyOrbEffect.EASED_SCALE_OUT)
                        .addLayer(radius, new Color(beamR, beamG, beamB, 1))
                        .addLayer(radius + 100, new Color(beamR, beamG, beamB, 0.75f))
                        .addLayer(radius + 200, new Color(beamR, beamG, beamB, 0.5f))
                        .addLayer(radius + 300, new Color(beamR, beamG, beamB, 0.25f));
            }
        });

    }
}
