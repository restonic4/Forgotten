package com.restonic4.forgotten.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.restonic4.forgotten.registries.common.ForgottenItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class AltarBlockEntityRenderer implements BlockEntityRenderer<AltarBlockEntity> {
    private final ItemRenderer itemRenderer;
    private final EntityRenderDispatcher entityRenderer;

    public AltarBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        Minecraft minecraft = Minecraft.getInstance();
        this.itemRenderer = minecraft.getItemRenderer();
        this.entityRenderer = minecraft.getEntityRenderDispatcher();
    }

    @Override
    public void render(AltarBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemStack itemStack = blockEntity.getStoredItem();

        if (!itemStack.isEmpty()) {
            poseStack.pushPose();

            poseStack.translate(0.5, 1.1, 0.5);

            if (itemStack.is(ForgottenItems.PLAYER_SOUL) || itemStack.is(ForgottenItems.ETHEREAL_SHARD)) {
                poseStack.scale(1f, 1f, 1f);
                packedLight = 0xF000F0;
            } else {
                poseStack.scale(0.5f, 0.5f, 0.5f);
            }

            int scale = (int) (2 * 360f);
            long time = blockEntity.getLevel().getGameTime();
            float angle = (Math.floorMod(time, (long) scale) + partialTicks) / (float) scale;
            Quaternionf rotation = Axis.YP.rotation((float) (angle * Math.PI * 10));

            poseStack.mulPose(rotation);


            this.itemRenderer.renderStatic(itemStack, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, bufferSource, blockEntity.getLevel(), 0);

            poseStack.popPose();
        }
    }
}
