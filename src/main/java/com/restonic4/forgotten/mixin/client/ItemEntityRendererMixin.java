package com.restonic4.forgotten.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.math.Axis;
import com.restonic4.forgotten.item.EtherealFragment;
import com.restonic4.forgotten.item.InvincibleItem;
import com.restonic4.forgotten.item.PlayerSoul;
import com.restonic4.forgotten.util.MainMatrixStorage;
import com.restonic4.forgotten.util.helpers.RenderingHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.ibm.icu.text.PluralRules.Operand.f;

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

        if (shouldBeVisible(itemEntity)) {

        }

        if (shouldRenderOrbs(itemEntity)) {
            Vector3f position = itemEntity.position().toVector3f();

            RenderSystem.setShaderColor(0.988f, 0.996f, 0.996f, 0.05f);

            RenderingHelper.renderSphere(poseStack, MainMatrixStorage.getCurrentMatrix(), Minecraft.getInstance().gameRenderer.getMainCamera(), position, 2);
            RenderingHelper.renderSphere(poseStack, MainMatrixStorage.getCurrentMatrix(), Minecraft.getInstance().gameRenderer.getMainCamera(), position, 4);
            RenderingHelper.renderSphere(poseStack, MainMatrixStorage.getCurrentMatrix(), Minecraft.getInstance().gameRenderer.getMainCamera(), position, 8);
            RenderingHelper.renderSphere(poseStack, MainMatrixStorage.getCurrentMatrix(), Minecraft.getInstance().gameRenderer.getMainCamera(), position, 16);

            RenderSystem.setShaderColor(1, 1, 1, 1);
        }
    }

    @Unique
    private boolean shouldBeVisible(ItemEntity itemEntity) {
        return itemEntity.getItem().getItem() instanceof InvincibleItem;
    }

    @Unique
    private boolean shouldScaleItem(ItemEntity itemEntity) {
        return itemEntity.getItem().getItem() instanceof PlayerSoul playerSoul;
    }

    @Unique
    private boolean shouldRenderOrbs(ItemEntity itemEntity) {
        return itemEntity.getItem().getItem() instanceof EtherealFragment;
    }
}
