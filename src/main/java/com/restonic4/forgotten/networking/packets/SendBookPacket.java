package com.restonic4.forgotten.networking.packets;

import com.restonic4.forgotten.item.PlayerSoul;
import com.restonic4.forgotten.networking.PacketManager;
import com.restonic4.forgotten.networking.ServerItemInteractions;
import com.restonic4.forgotten.registries.common.ForgottenItems;
import com.restonic4.forgotten.util.helpers.SimpleEffectHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerPacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class SendBookPacket {
    public static void receive(MinecraftServer server, Player player, ServerPacketListener serverPacketListener, FriendlyByteBuf friendlyByteBuf, PacketSender packetSender) {
        String targetName = friendlyByteBuf.readUtf();

        ServerPlayer targetPlayer = server.getPlayerList().getPlayerByName(targetName);

        if (targetPlayer != null) {
            ItemStack etherealBook = null;

            if (player.getItemInHand(InteractionHand.MAIN_HAND).is(ForgottenItems.ETHEREAL_WRITTEN_BOOK)) {
                etherealBook = player.getItemInHand(InteractionHand.MAIN_HAND);
            } else if(player.getItemInHand(InteractionHand.OFF_HAND).is(ForgottenItems.ETHEREAL_WRITTEN_BOOK)) {
                etherealBook = player.getItemInHand(InteractionHand.OFF_HAND);
            }

            if (etherealBook == null || etherealBook.getCount() <= 0) {
                System.out.println("Book not found on original player or empty ItemStack");
                // tell the original player, where did book go? ):
                return;
            }

            ItemStack bookToGive = etherealBook.split(1);
            boolean addedToInventory = targetPlayer.addItem(bookToGive);

            if (!addedToInventory) {
                System.out.println("Inventory full, spawning item in the world.");

                server.execute(() -> {
                    float speed = 0.3F;

                    float g = Mth.sin(targetPlayer.getXRot() * (float) (Math.PI / 180.0));
                    float h = Mth.cos(targetPlayer.getXRot() * (float) (Math.PI / 180.0));
                    float i = Mth.sin(targetPlayer.getYRot() * (float) (Math.PI / 180.0));
                    float j = Mth.cos(targetPlayer.getYRot() * (float) (Math.PI / 180.0));
                    float k = targetPlayer.random.nextFloat() * (float) (Math.PI * 2);
                    float l = 0.02F * targetPlayer.random.nextFloat();

                    ItemEntity itemEntity = new ItemEntity(
                            targetPlayer.serverLevel(),
                            targetPlayer.getX(),
                            targetPlayer.getY(),
                            targetPlayer.getZ(),
                            bookToGive,
                            0, 0, 0
                    );

                    itemEntity.setDeltaMovement(
                            (double)(-i * h * speed) + Math.cos((double)k) * (double)l,
                            (double)(-g * speed + 0.1F + (targetPlayer.random.nextFloat() - targetPlayer.random.nextFloat()) * 0.1F),
                            (double)(j * h * speed) + Math.sin((double)k) * (double)l
                    );

                    targetPlayer.serverLevel().addFreshEntity(itemEntity);
                });
            }

            server.execute(() -> {
                SimpleEffectHelper.invalidHeadPlacement((ServerLevel) player.level(), new BlockPos(player.blockPosition()).offset(0, 1, 0));
                SimpleEffectHelper.invalidHeadPlacement(targetPlayer.serverLevel(), new BlockPos(targetPlayer.blockPosition()).offset(0, 1, 0));
            });
        } else {
            System.out.println("Player not found on server");
            // tell the original player about the issue
        }
    }

    public static void sendToServer(String targetPlayerName) {
        FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();
        friendlyByteBuf.writeUtf(targetPlayerName);
        ClientPlayNetworking.send(PacketManager.SEND_BOOK, friendlyByteBuf);
    }
}
