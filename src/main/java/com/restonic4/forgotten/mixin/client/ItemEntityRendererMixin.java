package com.restonic4.forgotten.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.math.Axis;
import com.restonic4.forgotten.item.EtherealFragment;
import com.restonic4.forgotten.item.PlayerSoul;
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

        if (shouldRenderOrbs(itemEntity)) {
            Matrix4f matrix4f = getMatrix(Minecraft.getInstance().gameRenderer.getMainCamera(), f, Minecraft.getInstance().gameRenderer);

            Vector3f position = itemEntity.position().toVector3f();

            RenderSystem.setShaderColor(0.988f, 0.996f, 0.996f, 0.05f);

            RenderingHelper.renderSphere(poseStack, matrix4f, Minecraft.getInstance().gameRenderer.getMainCamera(), position, 2);
            RenderingHelper.renderSphere(poseStack, matrix4f, Minecraft.getInstance().gameRenderer.getMainCamera(), position, 4);
            RenderingHelper.renderSphere(poseStack, matrix4f, Minecraft.getInstance().gameRenderer.getMainCamera(), position, 8);
            RenderingHelper.renderSphere(poseStack, matrix4f, Minecraft.getInstance().gameRenderer.getMainCamera(), position, 16);

            RenderSystem.setShaderColor(1, 1, 1, 1);
        }
    }

    @Unique
    private Matrix4f getMatrix(Camera camera, float idkTheFuckIsThis, GameRenderer gameRenderer) {
        PoseStack poseStack2 = new PoseStack();
        double d = gameRenderer.getFov(camera, idkTheFuckIsThis, true);
        poseStack2.mulPoseMatrix(gameRenderer.getProjectionMatrix(d));
        gameRenderer.bobHurt(poseStack2, idkTheFuckIsThis);
        if (Minecraft.getInstance().options.bobView().get()) {
            gameRenderer.bobView(poseStack2, idkTheFuckIsThis);
        }

        float g = Minecraft.getInstance().options.screenEffectScale().get().floatValue();
        float h = Mth.lerp(idkTheFuckIsThis, Minecraft.getInstance().player.oSpinningEffectIntensity, Minecraft.getInstance().player.spinningEffectIntensity) * g * g;
        if (h > 0.0F) {
            int i = Minecraft.getInstance().player.hasEffect(MobEffects.CONFUSION) ? 7 : 20;
            float j = 5.0F / (h * h + 5.0F) - h * 0.04F;
            j *= j;
            Axis axis = Axis.of(new Vector3f(0.0F, Mth.SQRT_OF_TWO / 2.0F, Mth.SQRT_OF_TWO / 2.0F));
            poseStack2.mulPose(axis.rotationDegrees(((float)gameRenderer.tick + idkTheFuckIsThis) * (float)i));
            poseStack2.scale(1.0F / j, 1.0F, 1.0F);
            float k = -((float)gameRenderer.tick + idkTheFuckIsThis) * (float)i;
            poseStack2.mulPose(axis.rotationDegrees(k));
        }

        Matrix4f matrix4f = poseStack2.last().pose();
        RenderSystem.setProjectionMatrix(matrix4f, VertexSorting.DISTANCE_TO_ORIGIN);

        return matrix4f;
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
