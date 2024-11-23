package com.restonic4.forgotten.networking.packets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.restonic4.forgotten.client.DeathUtils;
import com.restonic4.forgotten.registries.ForgottenSounds;
import com.restonic4.forgotten.registries.client.CustomRenderTypes;
import com.restonic4.forgotten.util.CircleGenerator;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import team.lodestar.lodestone.handlers.RenderHandler;
import team.lodestar.lodestone.handlers.ScreenshakeHandler;
import team.lodestar.lodestone.registry.client.LodestoneRenderTypeRegistry;
import team.lodestar.lodestone.registry.client.LodestoneShaderRegistry;
import team.lodestar.lodestone.registry.common.particle.LodestoneParticleRegistry;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;
import team.lodestar.lodestone.systems.particle.data.spin.SpinParticleData;
import team.lodestar.lodestone.systems.particle.render_types.LodestoneWorldParticleRenderType;
import team.lodestar.lodestone.systems.particle.world.LodestoneWorldParticle;
import team.lodestar.lodestone.systems.particle.world.type.LodestoneWorldParticleType;
import team.lodestar.lodestone.systems.screenshake.PositionedScreenshakeInstance;
import team.lodestar.lodestone.systems.screenshake.ScreenshakeInstance;

import java.awt.*;
import java.util.List;

public class BeamPacket {
    public static void receive(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf friendlyByteBuf, PacketSender packetSender) {
        minecraft.execute(() -> {
            if (minecraft.level != null && minecraft.player != null) {
                Vec3 targetPoint = new Vec3(0, 0, 0);
                Vec3 targetPointRing = targetPoint.add(0, 200, 0);

                ScreenshakeInstance roarScreenShake = new PositionedScreenshakeInstance(260, targetPoint, 1000000f, 2000000f, Easing.CIRC_OUT).setIntensity(0.0f, 2, 0.0f);
                //ScreenshakeHandler.addScreenshake(roarScreenShake);

                spawnBeam(minecraft.player.level(),  targetPoint, 120);
                spawnBeam(minecraft.player.level(),  targetPoint, 140);
                spawnBeam(minecraft.player.level(),  targetPoint, 180);
                spawnBeam(minecraft.player.level(),  targetPoint, 200);
                spawnBeam(minecraft.player.level(),  targetPoint, 260);

                float radius = 20;
                int precision = 200;

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
                            .setRenderType(CustomRenderTypes.particleType)
                            .enableForcedSpawn()
                            .spawn(minecraft.level, targetPointRing.x, targetPointRing.y, targetPointRing.z);
                }
            }
        });
    }

    public static void spawnBeam(Level level, Vec3 pos, int duration) {
        for (int i = 0; i <= 400; i++) {
            Color startingColor = new Color(255, 179, 0);
            Color endingColor = new Color(91, 10, 146);

            float sizeFactor = (float) (2.5f / Math.pow(i + 1, 0.5));
            float verticalFactor = Math.max(0.5f, ((float) Math.pow(0.9, i)));

            WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
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
                    .spawn(level, pos.x, pos.y + i * verticalFactor, pos.z);
        }
    }
}
