package com.restonic4.forgotten.networking;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ServerItemInteractions {
    public static void playerPickup(ItemEntity entity, Player player) {
        if (!entity.level().isClientSide()) {
            player.getServer().execute(() -> {
                ItemStack itemStack = entity.getItem();
                Item item = itemStack.getItem();
                int count = itemStack.getCount();

                if (entity.pickupDelay == 0 && (entity.target == null || entity.target.equals(player.getUUID())) && player.getInventory().add(itemStack)) {
                    player.take(entity, count);
                    if (itemStack.isEmpty()) {
                        entity.discard();
                        itemStack.setCount(count);
                    }

                    player.awardStat(Stats.ITEM_PICKED_UP.get(item), count);
                    player.onItemPickup(entity);
                }
            });
        }
    }

    public static InteractionResult interact(ItemEntity item, Player player, InteractionHand hand) {
        playerPickup(item, player);
        return InteractionResult.CONSUME;
    }
}
