package com.restonic4.forgotten.mixin.client;

import com.restonic4.forgotten.client.DeathUtils;
import com.restonic4.forgotten.client.ItemInteractions;
import com.restonic4.forgotten.item.PlayerSoul;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    public void setScreen(Screen screen, CallbackInfo ci) {
        if (DeathUtils.isDeath() && screen instanceof EffectRenderingInventoryScreen) {
            ci.cancel();
        }
    }

    @Inject(method = "startUseItem()V", at = @At("TAIL"), require = 1)
    public void rightClickEmpty(CallbackInfo ci) {
        HitResult result = ((Minecraft) (Object) this).hitResult;
        if (result == null || result.getType() == HitResult.Type.MISS) {
            ItemInteractions.onPlayerInteractClient(((Minecraft) (Object) this).level, ((Minecraft) (Object) this).player, true);
        }
    }

    @Inject(method = "startUseItem()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/InteractionHand;values()[Lnet/minecraft/world/InteractionHand;"),
            cancellable = true, require = 1)
    public void rightClickEarly(CallbackInfo ci) {
        if (ItemInteractions.onPlayerInteract(((Minecraft) (Object) this).player)) {
            ci.cancel();
        }
    }
}
