package com.restonic4.forgotten.mixin;

import com.restonic4.forgotten.item.PlayerSoul;
import com.restonic4.forgotten.networking.PacketManager;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "discard", at = @At("HEAD"), cancellable = true)
    public void discard(CallbackInfo ci) {
        Entity current = (Entity) (Object) this;

        if (shouldCancel(current)) {
            ci.cancel();
        }
    }

    @Inject(method = "kill", at = @At("HEAD"), cancellable = true)
    public void kill(CallbackInfo ci) {
        Entity current = (Entity) (Object) this;

        if (shouldCancel(current)) {
            ci.cancel();
        }
    }

    @Inject(method = "remove", at = @At("HEAD"), cancellable = true)
    public void remove(Entity.RemovalReason removalReason, CallbackInfo ci) {
        Entity current = (Entity) (Object) this;

        if (shouldCancel(current)) {
            ci.cancel();
        }
    }

    @Inject(method = "onBelowWorld", at = @At("HEAD"), cancellable = true)
    public void onBelowWorld(CallbackInfo ci) {
        Entity current = (Entity) (Object) this;

        if (shouldCancel(current)) {
            Vec3 tpZone = new Vec3(current.position().x, 2000, current.position().z);

            if (!current.level().isClientSide()) {
                MinecraftServer server = current.getServer();

                if (server != null) {
                    for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
                        FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();
                        friendlyByteBuf.writeBlockPos(new BlockPos((int) tpZone.x, (int) tpZone.y, (int) tpZone.z));
                        ServerPlayNetworking.send(serverPlayer, PacketManager.LITTLE_SKY_WAVE, friendlyByteBuf);
                    }
                }

                current.teleportTo(tpZone.x, tpZone.y, tpZone.z);
            }

            ci.cancel();
        }
    }

    @Shadow private EntityDimensions dimensions;

    @Unique private EntityDimensions customItemDimensions = new EntityDimensions(0.75f, 1f, false);

    @Inject(method = "makeBoundingBox", at = @At("HEAD"), cancellable = true)
    protected void makeBoundingBox(CallbackInfoReturnable<AABB> cir) {
        Entity current = (Entity) (Object) this;

        if (shouldCancel(current)) {
            dimensions = customItemDimensions;
        }
    }

    @Unique
    public boolean shouldCancel(Entity entity) {
        return entity instanceof ItemEntity itemEntity && itemEntity.getItem().getItem() instanceof PlayerSoul playerSoul;
    }
}
