package com.restonic4.forgotten.mixin;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PlayerHeadItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public class MobMixin {
    // Prevents mobs for picking heads

    @Inject(method = "pickUpItem", at = @At("HEAD"), cancellable = true)
    protected void pickUpItem(ItemEntity itemEntity, CallbackInfo ci) {
        ItemStack itemStack = itemEntity.getItem();
        if (itemStack.getItem() instanceof PlayerHeadItem) {
            ci.cancel();
        }
    }

    @Inject(method = "equipItemIfPossible", at = @At("HEAD"), cancellable = true)
    public void equipItemIfPossible(ItemStack itemStack, CallbackInfoReturnable<ItemStack> cir) {
        if (itemStack.getItem() instanceof PlayerHeadItem) {
            cir.setReturnValue(ItemStack.EMPTY);
            cir.cancel();
        }
    }
}
