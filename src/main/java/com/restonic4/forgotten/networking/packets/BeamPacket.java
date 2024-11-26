package com.restonic4.forgotten.networking.packets;

import com.restonic4.forgotten.client.rendering.BeamEffectManager;
import com.restonic4.forgotten.registries.common.ForgottenSounds;
import com.restonic4.forgotten.registries.client.ForgottenShaderHolders;
import com.restonic4.forgotten.util.EasingSystem;
import com.restonic4.forgotten.util.helpers.CircleGenerator;
import com.restonic4.forgotten.util.trash.TestingVars;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import team.lodestar.lodestone.handlers.ScreenshakeHandler;
import team.lodestar.lodestone.registry.common.particle.LodestoneParticleRegistry;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;
import team.lodestar.lodestone.systems.particle.data.spin.SpinParticleData;
import team.lodestar.lodestone.systems.screenshake.ScreenshakeInstance;

import java.awt.*;
import java.util.List;

public class BeamPacket {
    public static void receive(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf friendlyByteBuf, PacketSender packetSender) {
        if (Minecraft.getInstance().player == null) {
            return;
        }

        float maxDistance = 1000;

        Vec3 beamCenter = new Vec3(0, 0, 0);
        Vec3 playerPosition = Minecraft.getInstance().player.position();

        Vec3 distance = beamCenter.subtract(playerPosition);
        float secondsToWait = Math.max(4, calculateScale(distance.toVector3f(), maxDistance, 9));

        new Thread(() -> {
            int time = 0;
            float velocity = 0.01f;

            Minecraft.getInstance().execute(() -> {
                ForgottenShaderHolders.SKY_WAVE.getInstance().get().safeGetUniform("BeamCenter").set(new float[] {(float) beamCenter.x, (float) beamCenter.y, (float) beamCenter.z});
                ForgottenShaderHolders.SKY_WAVE.getInstance().get().safeGetUniform("Time").set(0);
                ForgottenShaderHolders.SKY_WAVE.getInstance().get().safeGetUniform("Alpha").set(1f);
            });

            while (time < Integer.MAX_VALUE && time >= 0) {
                int finalTime = time;
                Minecraft.getInstance().execute(() -> {
                    ForgottenShaderHolders.SKY_WAVE.getInstance().get().safeGetUniform("Time").set(finalTime);
                });

                time += Math.max((int) (time * velocity), 1);

                try {
                    Thread.sleep(10);
                } catch (Exception ignored) {}
            }

            float alpha = 1;
            while (alpha > 0) {
                float finalAlpha = alpha;
                Minecraft.getInstance().execute(() -> {
                    ForgottenShaderHolders.SKY_WAVE.getInstance().get().safeGetUniform("Alpha").set(Math.max(finalAlpha, 0));
                });

                alpha -= 0.01f;

                try {
                    Thread.sleep(10);
                } catch (Exception ignored) {}
            }
        }).start();

        minecraft.execute(() -> {
            if (minecraft.level != null && minecraft.player != null) {
                float beamR = 0.3f;
                float beamG = 1;
                float beamB = 1;

                BeamEffectManager.create()
                        .lifetime(10)
                        .setPosition(beamCenter.toVector3f())
                        .timeBetweenFades(4f)
                        .setFadeInAnimation((animationContext, progress) -> {
                            float easedProgress = EasingSystem.getEasedValue(
                                    progress, 0f, 1f, EasingSystem.EasingType.BACK_OUT
                            );

                            animationContext.setWidth(animationContext.getWidth() * easedProgress);
                        })
                        .setFadeOutAnimation((animationContext, progress) -> {
                            float easedProgress = EasingSystem.getEasedValue(
                                    progress, 1f, 0f, EasingSystem.EasingType.BACK_IN
                            );

                            animationContext.setWidth(animationContext.getWidth() * easedProgress);
                        })
                        .addLayer(2, 1020, new Color(beamR, beamG, beamB, 1))
                        .addLayer(4, 1020, new Color(beamR, beamG, beamB, 0.75f))
                        .addLayer(6, 1020, new Color(beamR, beamG, beamB, 0.5f))
                        .addLayer(8, 1020, new Color(beamR, beamG, beamB, 0.25f));

                BlockPos blockPos = minecraft.player.blockPosition();

                minecraft.level.playLocalSound(blockPos.getX(), blockPos.getY(), blockPos.getZ(), ForgottenSounds.EXPLOSION, SoundSource.AMBIENT, 1, 1, false);
            }
        });

        float waitSecond = 1.5f;
        float waitFirst = secondsToWait - waitSecond;

        try {
            Thread.sleep((long) (waitFirst * 1000));
        } catch (Exception ignored) {}

        ScreenshakeInstance roarScreenShake = new ScreenshakeInstance(5 * 20).setEasing(Easing.QUAD_IN_OUT).setIntensity(0.0f, 0.75f, 0.0f);
        ScreenshakeHandler.addScreenshake(roarScreenShake);

        try {
            Thread.sleep((long) (waitSecond * 1000));
        } catch (Exception ignored) {}

        minecraft.execute(() -> {
            if (minecraft.player == null || minecraft.level == null) {
                return;
            }

            BlockPos blockPos = minecraft.player.blockPosition();

            minecraft.level.playLocalSound(blockPos.getX(), blockPos.getY(), blockPos.getZ(), ForgottenSounds.WAVE, SoundSource.AMBIENT, 1, 1, false);
        });
    }




    public static float calculateScale(Vector3f distance, float maxDistance, float maxValue) {
        float lengthXZ = (float) Math.sqrt(distance.x * distance.x + distance.z * distance.z);

        lengthXZ = Math.min(lengthXZ, maxDistance);

        return (lengthXZ / maxDistance) * maxValue;
    }
}
