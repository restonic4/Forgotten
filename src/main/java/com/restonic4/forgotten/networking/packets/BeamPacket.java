package com.restonic4.forgotten.networking.packets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.restonic4.forgotten.client.rendering.BeamEffect;
import com.restonic4.forgotten.client.rendering.BeamEffectManager;
import com.restonic4.forgotten.client.rendering.SkyWaveEffectManager;
import com.restonic4.forgotten.registries.common.ForgottenSounds;
import com.restonic4.forgotten.registries.client.ForgottenShaderHolders;
import com.restonic4.forgotten.util.EasingSystem;
import com.restonic4.forgotten.util.helpers.CircleGenerator;
import com.restonic4.forgotten.util.helpers.MathHelper;
import com.restonic4.forgotten.util.trash.TestingVars;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
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
        if (minecraft.player == null || minecraft.level == null) {
            return;
        }

        Vec3 beamCenter = new Vec3(0, 0, 0);
        Color beamColor = new Color(0.3f, 1, 1, 1);

        spawnSkyWave(minecraft, beamCenter.toVector3f(), beamColor);

        spawnBeam(minecraft, beamCenter.toVector3f(), beamColor);
    }

    private static void spawnSkyWave(Minecraft minecraft, Vector3f position, Color color) {
        minecraft.execute(() -> {
            SkyWaveEffectManager.create()
                    .lifetime(10)
                    .setPosition(position)
                    .height(1000)
                    .color(color)
                    .actionExecutedBeforeAbovePlayerHead(() -> {
                        ScreenshakeInstance roarScreenShake = new ScreenshakeInstance(5 * 20).setEasing(Easing.QUAD_IN_OUT).setIntensity(0.0f, 0.75f, 0.0f);
                        ScreenshakeHandler.addScreenshake(roarScreenShake);
                    })
                    .actionExecutedAbovePlayerHead(() -> {
                        minecraft.execute(() -> {
                            if (minecraft.player == null || minecraft.level == null) {
                                return;
                            }

                            BlockPos blockPos = minecraft.player.blockPosition();

                            minecraft.level.playLocalSound(blockPos.getX(), blockPos.getY(), blockPos.getZ(), ForgottenSounds.WAVE, SoundSource.AMBIENT, 1, 1, false);
                        });
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
                        .addLayer(2, 1020, new Color(beamR, beamG, beamB, 1))
                        .addLayer(4, 1020, new Color(beamR, beamG, beamB, 0.75f))
                        .addLayer(6, 1020, new Color(beamR, beamG, beamB, 0.5f))
                        .addLayer(8, 1020, new Color(beamR, beamG, beamB, 0.25f));

                BlockPos blockPos = minecraft.player.blockPosition();

                minecraft.level.playLocalSound(blockPos.getX(), blockPos.getY(), blockPos.getZ(), ForgottenSounds.EXPLOSION, SoundSource.AMBIENT, 1, 1, false);
            }
        });
    }
}
