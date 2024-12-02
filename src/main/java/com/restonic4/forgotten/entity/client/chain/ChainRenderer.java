package com.restonic4.forgotten.entity.client.chain;

import com.mojang.blaze3d.vertex.PoseStack;
import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.entity.common.ChainEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ChainRenderer extends MobRenderer<ChainEntity, ChainModel<ChainEntity>> {
    private static final ResourceLocation NORMAL_TEXTURE = new ResourceLocation(Forgotten.MOD_ID, "textures/entity/chain.png");

    public ChainRenderer(EntityRendererProvider.Context context) {
        super(context, new ChainModel<>(context.bakeLayer(ChainLayers.CHAIN)), 0.5f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ChainEntity entity) {
        return NORMAL_TEXTURE;
    }

    @Override
    public void render(ChainEntity entity, float f, float g, PoseStack poseStack, MultiBufferSource vertexConsumerProvider, int i) {
        super.render(entity, f, g, poseStack, vertexConsumerProvider, i);
    }
}
