package com.restonic4.forgotten.client;

import com.restonic4.forgotten.networking.PacketManager;
import com.restonic4.forgotten.registries.common.ForgottenItems;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class ClientItemInteractions {
    public static final float REACH_DISTANCE = 5;
    public static final float HITBOX_INCREASE = 0.5f;

    public static final Minecraft minecraft = Minecraft.getInstance();

    public static boolean onPlayerInteractClient(Level level, Player player, boolean rightClick) {
        HitResult result = getEntityItem(minecraft.player);

        if (result != null && result.getType() == HitResult.Type.ENTITY) {
            ItemEntity entity = (ItemEntity) ((EntityHitResult) result).getEntity();

            if (entity.getItem().is(ForgottenItems.PLAYER_SOUL) && level.isClientSide && entity != null) {
                player.swing(InteractionHand.MAIN_HAND);

                FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();
                friendlyByteBuf.writeUUID(entity.getUUID());
                friendlyByteBuf.writeBoolean(rightClick);
                ClientPlayNetworking.send(PacketManager.INTERACTED_ITEM, friendlyByteBuf);

                return true;
            }
        }

        return false;
    }

    public static boolean onPlayerInteract(Player player) {
        return onPlayerInteractClient(player.level(), player, true);
    }

    public static HitResult getEntityItem(Player player) {
        double distance = REACH_DISTANCE;
        float partialTicks = minecraft.getDeltaFrameTime();

        Vec3 position = player.getEyePosition(partialTicks);
        Vec3 view = player.getViewVector(partialTicks);

        if (minecraft.hitResult != null && minecraft.hitResult.getType() != HitResult.Type.MISS) {
            distance = Math.min(minecraft.hitResult.getLocation().distanceTo(position), distance);
        }

        return getEntityItem(player, position, position.add(view.x * distance, view.y * distance, view.z * distance));
    }

    public static HitResult getEntityItem(Player player, Vec3 position, Vec3 look) {
        Vec3 include = look.subtract(position);
        List<Entity> list = player.level().getEntities(player, player.getBoundingBox().expandTowards(include.x, include.y, include.z));

        for (int i = 0; i < list.size(); ++i) {
            Entity entity = (Entity) list.get(i);

            if (entity instanceof ItemEntity) {
                AABB aabb = entity.getBoundingBox().inflate(HITBOX_INCREASE);
                Optional<Vec3> vec = aabb.clip(position, look);

                if (vec.isPresent()) {
                    return new EntityHitResult(entity, vec.get());
                } else if (aabb.contains(position)) {
                    return new EntityHitResult(entity);
                }
            }
        }

        return null;
    }
}
