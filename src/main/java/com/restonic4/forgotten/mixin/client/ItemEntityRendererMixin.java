package com.restonic4.forgotten.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.restonic4.forgotten.client.rendering.ClientShootingStarManager;
import com.restonic4.forgotten.item.EtherealFragment;
import com.restonic4.forgotten.item.InvincibleItem;
import com.restonic4.forgotten.item.PlayerSoul;
import com.restonic4.forgotten.registries.common.ForgottenItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntityRenderer.class)
public abstract class ItemEntityRendererMixin extends EntityRenderer<ItemEntity> {
    protected ItemEntityRendererMixin(EntityRendererProvider.Context context) {
        super(context);

        this.shadowRadius = 0.25F;
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V", shift = At.Shift.AFTER, ordinal = 0))
    private void onRender(ItemEntity itemEntity, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        if (shouldScaleItem(itemEntity)) {
            poseStack.scale(2.0F, 2.0F, 2.0F);
        }

        if (shouldRenderOrbs(itemEntity)) {
            ClientShootingStarManager.addEtherealFragment(itemEntity);
        }
    }

    @Unique
    private boolean shouldBeVisible(ItemEntity itemEntity) {
        return itemEntity.getItem().getItem() instanceof InvincibleItem;
    }

    @Unique
    private boolean shouldScaleItem(ItemEntity itemEntity) {
        return itemEntity.getItem().is(ForgottenItems.PLAYER_SOUL) || itemEntity.getItem().is(ForgottenItems.ETHEREAL_SHARD);
    }

    @Unique
    private boolean shouldRenderOrbs(ItemEntity itemEntity) {
        return itemEntity.getItem().is(ForgottenItems.ETHEREAL_SHARD);
    }
}
