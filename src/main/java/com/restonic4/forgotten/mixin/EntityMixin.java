package com.restonic4.forgotten.mixin;

import com.restonic4.forgotten.client.rendering.ClientShootingStarManager;
import com.restonic4.forgotten.item.EtherealFragment;
import com.restonic4.forgotten.item.InvincibleItem;
import com.restonic4.forgotten.item.PlayerSoul;
import com.restonic4.forgotten.networking.PacketManager;
import com.restonic4.forgotten.registries.common.ForgottenItems;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.item.ItemEntity;
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

        if (shouldNotDie(current)) {
            ci.cancel();
        }
    }

    @Inject(method = "kill", at = @At("HEAD"), cancellable = true)
    public void kill(CallbackInfo ci) {
        Entity current = (Entity) (Object) this;

        if (shouldNotDie(current)) {
            ci.cancel();
        }
    }

    @Inject(method = "remove", at = @At("HEAD"), cancellable = true)
    public void remove(Entity.RemovalReason removalReason, CallbackInfo ci) {
        Entity current = (Entity) (Object) this;

        if (shouldNotDie(current)) {
            ci.cancel();
        }
    }

    @Inject(method = "onBelowWorld", at = @At("HEAD"), cancellable = true)
    public void onBelowWorld(CallbackInfo ci) {
        Entity current = (Entity) (Object) this;

        if (shouldNotDie(current)) {
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

        if (isSoul(current)) {
            dimensions = customItemDimensions;
        }
    }

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    public void shouldRender(double d, double e, double f, CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;

        if (isFragment(entity)) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Unique
    public boolean shouldNotDie(Entity entity) {
        return entity instanceof ItemEntity itemEntity && (itemEntity.getItem().getItem() instanceof InvincibleItem || (itemEntity.getItem().is(ForgottenItems.ETHEREAL_WRITTEN_BOOK)));
    }

    @Unique
    public boolean isSoul(Entity entity) {
        return entity instanceof ItemEntity itemEntity && itemEntity.getItem().getItem() instanceof PlayerSoul;
    }

    @Unique
    public boolean isFragment(Entity entity) {
        return entity instanceof ItemEntity itemEntity && itemEntity.getItem().getItem() instanceof EtherealFragment;
    }
}
