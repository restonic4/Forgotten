package com.restonic4.forgotten.networking.packets;

import com.restonic4.forgotten.client.DeathUtils;
import com.restonic4.forgotten.registries.common.ForgottenSounds;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
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

public class BlockCancellationEffectsPacket {
    public static void receive(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf friendlyByteBuf, PacketSender packetSender) {
        BlockPos blockPos = friendlyByteBuf.readBlockPos();
        int timeToRecover = friendlyByteBuf.readInt();

        int timeToRecoverInTicks = (int) ((timeToRecover / 1000f) * 20);

        Vec3 center = new Vec3(blockPos.getCenter().toVector3f());

        minecraft.execute(() -> {
            WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                    .setLifetime(timeToRecoverInTicks + 40)
                    .setScaleData(GenericParticleData.create(0.01f, 0.5f).setEasing(Easing.QUAD_IN_OUT).build())
                    .setTransparencyData(GenericParticleData.create(0.01f, 1, 0.01f).setEasing(Easing.QUAD_IN_OUT).build())
                    .setColorData(ColorParticleData.create(234, 190, 63).build())
                    .setRandomOffset(0)
                    .enableForcedSpawn()
                    .repeatSurroundBlock(minecraft.level, blockPos, 8);

            WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                    .setLifeDelay(timeToRecoverInTicks - 40)
                    .setLifetime(20 * 4)
                    .setScaleData(GenericParticleData.create(0.01f, 1f).setEasing(Easing.QUAD_IN_OUT).build())
                    .setTransparencyData(GenericParticleData.create(1, 0.01f).setEasing(Easing.QUAD_IN_OUT).build())
                    .setColorData(ColorParticleData.create(234, 190, 63).build())
                    .setRandomOffset(0)
                    .enableForcedSpawn()
                    .repeatSurroundBlock(minecraft.level, blockPos, 10);
        });
    }
}
