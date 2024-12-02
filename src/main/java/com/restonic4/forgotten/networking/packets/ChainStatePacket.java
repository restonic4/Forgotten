package com.restonic4.forgotten.networking.packets;

import com.restonic4.forgotten.client.rendering.BeamEffect;
import com.restonic4.forgotten.client.rendering.BeamEffectManager;
import com.restonic4.forgotten.client.rendering.SkyWaveEffectManager;
import com.restonic4.forgotten.entity.common.ChainEntity;
import com.restonic4.forgotten.registries.common.ForgottenSounds;
import com.restonic4.forgotten.util.helpers.MathHelper;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.EntityGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import team.lodestar.lodestone.handlers.ScreenshakeHandler;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.screenshake.ScreenshakeInstance;

import java.awt.*;
import java.util.UUID;

public class ChainStatePacket {
    public static void receive(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf friendlyByteBuf, PacketSender packetSender) {
        if (minecraft.player == null || minecraft.level == null) {
            return;
        }

        UUID entityUUID = friendlyByteBuf.readUUID();
        boolean isAlt = friendlyByteBuf.readBoolean();
        boolean isVertical = friendlyByteBuf.readBoolean();

        Entity entity = getEntityByUUID(minecraft, entityUUID);

        if (entity != null) {
            ChainEntity chain = (ChainEntity) entity;

            chain.setAlt(isAlt);
            chain.setVertical(isVertical);
        }
    }

    private static Entity getEntityByUUID(Minecraft minecraft, UUID uuid) {
        LevelEntityGetter<Entity> entityGetter = minecraft.level.getEntities();
        Iterable<Entity> iterable = entityGetter.getAll();

        for (Entity entity : iterable) {
            if (entity.getUUID() == uuid) {
                return entity;
            }
        }

        return null;
    }
}
