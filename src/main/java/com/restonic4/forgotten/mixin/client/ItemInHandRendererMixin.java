package com.restonic4.forgotten.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.restonic4.forgotten.client.CachedClientData;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    private void renderArmWithItem(
            AbstractClientPlayer abstractClientPlayer, float f, float g, InteractionHand interactionHand, float h, ItemStack itemStack, float i, PoseStack poseStack, MultiBufferSource multiBufferSource, int j, CallbackInfo ci
    ) {
        long currentTime = System.currentTimeMillis();
        if (currentTime >= CachedClientData.chainsCutsceneStartTime && currentTime <= CachedClientData.chainsCutsceneEndTime) {
            ci.cancel();
        }
    }
}
