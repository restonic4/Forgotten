package com.restonic4.forgotten.networking.packets;

import com.restonic4.forgotten.registries.common.ForgottenSounds;
import com.restonic4.forgotten.registries.client.ForgottenShaderHolders;
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
                Vec3 targetPoint = new Vec3(0, 0, 0);

                spawnBeam(minecraft.player.level(),  targetPoint, 120);
                spawnBeam(minecraft.player.level(),  targetPoint, 140);
                spawnBeam(minecraft.player.level(),  targetPoint, 180);
                spawnBeam(minecraft.player.level(),  targetPoint, 200);
                spawnBeam(minecraft.player.level(),  targetPoint, 260);

                spawnCircle(minecraft, targetPoint.add(0, 200, 0), 20, 200, 0.25f);
                spawnCircle(minecraft, targetPoint.add(0, 400, 0), 20, 200, 2);

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

    public static void spawnCircle(Minecraft minecraft, Vec3 targetPointRing, float radius, int precision, float speed) {
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
                    .addMotion(-point.toCenter.x * speed, 0, -point.toCenter.y * speed)
                    .enableNoClip()
                    .setRenderType(TestingVars.renderType)
                    //.setRenderType(CustomRenderTypes.particleType)
                    .enableForcedSpawn()
                    .spawn(minecraft.level, targetPointRing.x, targetPointRing.y, targetPointRing.z);
        }
    }

    public static void spawnBeam(Level level, Vec3 pos, int duration) {
        for (int i = 0; i <= 400; i++) {
            Color startingColor = new Color(255, 179, 0);
            Color endingColor = new Color(91, 10, 146);

            float sizeFactor = (float) (2.5f / Math.pow(i + 1, 0.5));
            float verticalFactor = Math.max(0.5f, ((float) Math.pow(0.9, i)));

            /*WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                    .setScaleData(GenericParticleData.create(sizeFactor, Math.max(sizeFactor, 0.2f)).build())
                    .setTransparencyData(GenericParticleData.create(1, 0f).build())
                    .setColorData(ColorParticleData.create(startingColor, endingColor).setCoefficient(1.4f).setEasing(Easing.BOUNCE_IN_OUT).build())
                    .setSpinData(SpinParticleData.create(0.2f, 0.4f).setSpinOffset((level.getGameTime() * 0.2f) % 6.28f).setEasing(Easing.QUARTIC_IN).build())
                    .setLifetime(duration)
                    .addMotion(0, 0.01f, 0)
                    .setRenderType(CustomRenderTypes.particleType)
                    .enableForcedSpawn()
                    .enableNoClip()
                    .setForceSpawn(true)
                    .spawn(level, pos.x, pos.y + i * verticalFactor, pos.z);*/

        }
    }



    public static float calculateScale(Vector3f distance, float maxDistance, float maxValue) {
        // Calcula la longitud del vector en X y Z.
        float lengthXZ = (float) Math.sqrt(distance.x * distance.x + distance.z * distance.z);

        // Asegúrate de que la distancia no exceda el máximo permitido.
        lengthXZ = Math.min(lengthXZ, maxDistance);

        // Escala el valor de 0 a maxValue en función de la distancia.
        return (lengthXZ / maxDistance) * maxValue;
    }
}
