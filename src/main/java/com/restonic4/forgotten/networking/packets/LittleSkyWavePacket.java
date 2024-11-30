package com.restonic4.forgotten.networking.packets;

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

public class LittleSkyWavePacket {
    public static void receive(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf friendlyByteBuf, PacketSender packetSender) {
        if (minecraft.player == null || minecraft.level == null) {
            return;
        }

        Vec3 beamCenter = friendlyByteBuf.readBlockPos().getCenter();
        Vec3 playerPos = minecraft.player.position();

        Color beamColor = new Color(0.3f, 1, 1, 1);

        spawnSkyWave(minecraft, beamCenter.toVector3f(), beamColor, 4);
    }

    private static void spawnSkyWave(Minecraft minecraft, Vector3f position, Color color, float lifetime) {
        minecraft.execute(() -> {
            SkyWaveEffectManager.create()
                    .lifetime(lifetime)
                    .setPosition(position)
                    .height(2000)
                    .color(color);
        });
    }
}
