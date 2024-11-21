package com.restonic4.forgotten.networking.packets;

import com.restonic4.forgotten.client.DeathUtils;
import com.restonic4.forgotten.registries.ForgottenSounds;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import team.lodestar.lodestone.registry.common.particle.LodestoneParticleRegistry;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;
import team.lodestar.lodestone.systems.particle.data.spin.SpinParticleData;
import team.lodestar.lodestone.systems.particle.world.LodestoneWorldParticle;
import team.lodestar.lodestone.systems.particle.world.type.LodestoneWorldParticleType;

import java.awt.*;

public class BeamPacket {
    public static void receive(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf friendlyByteBuf, PacketSender packetSender) {
        minecraft.execute(() -> {
            if (minecraft.level != null && minecraft.player != null) {
                spawnBeam(minecraft.player.level(),  new Vec3(0, 0, 0));
            }
        });
    }

    public static void spawnExampleParticles(Level level, Vec3 pos, LodestoneWorldParticleType particle) {
        Color startingColor = new Color(100, 0, 100);
        Color endingColor = new Color(0, 100, 200);
        WorldParticleBuilder.create(particle)
                .setScaleData(GenericParticleData.create(2.5f, 0).build())
                .setTransparencyData(GenericParticleData.create(1, 0.5f).build())
                .setColorData(ColorParticleData.create(startingColor, endingColor).setCoefficient(1.4f).setEasing(Easing.BOUNCE_IN_OUT).build())
                .setSpinData(SpinParticleData.create(0.2f, 0.4f).setSpinOffset((level.getGameTime() * 0.2f) % 6.28f).setEasing(Easing.QUARTIC_IN).build())
                .setLifetime(100)
                .addMotion(0, 0.01f, 0)
                .enableNoClip()
                .spawn(level, pos.x, pos.y, pos.z);
    }

    public static void spawnBeam(Level level, Vec3 pos) {
        for (int i = 0; i <= 25; i++) {
            Color startingColor = new Color(255, 179, 0);
            Color endingColor = new Color(91, 10, 146);

            float sizeFactor = (float) (2.5f / Math.pow(i + 1, 0.7));
            float verticalFactor = (float) Math.pow(0.9, i);

            WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                    .setScaleData(GenericParticleData.create(sizeFactor, 0).build())
                    .setTransparencyData(GenericParticleData.create(1, 0.5f).build())
                    .setColorData(ColorParticleData.create(startingColor, endingColor).setCoefficient(1.4f).setEasing(Easing.BOUNCE_IN_OUT).build())
                    .setSpinData(SpinParticleData.create(0.2f, 0.4f).setSpinOffset((level.getGameTime() * 0.2f) % 6.28f).setEasing(Easing.QUARTIC_IN).build())
                    .setLifetime(60)
                    .addMotion(0, 0.01f, 0)
                    .enableNoClip()
                    .spawn(level, pos.x, pos.y + i * verticalFactor, pos.z);
        }
    }
}
