package com.restonic4.forgotten.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.registries.client.ForgottenMaterials;
import com.restonic4.forgotten.registries.common.ForgottenItems;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.EnchantTableRenderer;
import net.minecraft.client.renderer.blockentity.LecternRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LecternRenderer.class)
public class LecternRenderMixin {
    @Shadow @Final private BookModel bookModel;

    @Inject(method = "render(Lnet/minecraft/world/level/block/entity/LecternBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V", at = @At("HEAD"), cancellable = true)
    public void render(LecternBlockEntity lecternBlockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, CallbackInfo ci) {
        BlockState blockState = lecternBlockEntity.getBlockState();
        if ((Boolean)blockState.getValue(LecternBlock.HAS_BOOK) && lecternBlockEntity.getBook().is(ForgottenItems.ETHEREAL_WRITTEN_BOOK)) {
            poseStack.pushPose();
            poseStack.translate(0.5F, 1.0625F, 0.5F);
            float g = ((Direction)blockState.getValue(LecternBlock.FACING)).getClockWise().toYRot();
            poseStack.mulPose(Axis.YP.rotationDegrees(-g));
            poseStack.mulPose(Axis.ZP.rotationDegrees(67.5F));
            poseStack.translate(0.0F, -0.125F, 0.0F);
            this.bookModel.setupAnim(0.0F, 0.1F, 0.9F, 1.2F);
            VertexConsumer vertexConsumer = ForgottenMaterials.ETHEREAL_BOOK_LOCATION.buffer(multiBufferSource, RenderType::entitySolid);
            this.bookModel.render(poseStack, vertexConsumer, i, j, 1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.popPose();

            ci.cancel();
        }
    }
}
