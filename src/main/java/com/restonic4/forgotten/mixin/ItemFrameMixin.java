package com.restonic4.forgotten.mixin;

import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.util.helpers.SimpleEffectHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PlayerHeadItem;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemFrame.class)
public class ItemFrameMixin {
    // Preventing heads to be in item frames, at least for now

    @Inject(method = "setItem(Lnet/minecraft/world/item/ItemStack;Z)V", at = @At("HEAD"), cancellable = true)
    public void setItem(ItemStack itemStack, boolean bl, CallbackInfo ci) {
        if (itemStack.getItem() instanceof PlayerHeadItem) {
            ci.cancel();
        }
    }

    @Inject(
            method = "interact",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/decoration/ItemFrame;setItem(Lnet/minecraft/world/item/ItemStack;)V",
                    ordinal = 0
            ),
            cancellable = true
    )
    private void preventPlayerHeadPlacement(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (itemStack.getItem() instanceof PlayerHeadItem) {
            if (!player.level().isClientSide()) {
                ItemFrame current = (ItemFrame) (Object) this;
                SimpleEffectHelper.invalidHeadPlacement((ServerLevel) player.level(), current.blockPosition());
            }

            cir.setReturnValue(InteractionResult.FAIL);
            cir.cancel();
        }
    }

    // Prevents ghost killing entity
    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    public void hurt(DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> cir) {
        ItemFrame current = (ItemFrame) (Object) this;

        if (!current.level().isClientSide() && damageSource.getEntity() instanceof ServerPlayer serverPlayer && Forgotten.isVanishLoadedAndVanished(serverPlayer)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
