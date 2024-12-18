package com.restonic4.forgotten.networking.packets;

import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.client.ClientItemInteractions;
import com.restonic4.forgotten.item.PlayerSoul;
import com.restonic4.forgotten.networking.ServerItemInteractions;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerPacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class InteractedItemPacket {
    public static void receive(MinecraftServer server, Player player, ServerPacketListener serverPacketListener, FriendlyByteBuf friendlyByteBuf, PacketSender packetSender) {
        UUID entityUUID = friendlyByteBuf.readUUID();
        boolean rightClick = friendlyByteBuf.readBoolean();

        Entity item = ((ServerLevel) player.level()).getEntity(entityUUID);
        if (item != null && item instanceof ItemEntity && item.isAlive() && isSoul(item) && !Forgotten.isVanishLoadedAndVanished((ServerPlayer) player)) {
            ServerItemInteractions.interact((ItemEntity) item, player, InteractionHand.MAIN_HAND);
            player.swing(InteractionHand.MAIN_HAND);
        }
    }

    public static boolean isSoul(Entity entity) {
        return entity instanceof ItemEntity itemEntity && itemEntity.getItem().getItem() instanceof PlayerSoul playerSoul;
    }
}
