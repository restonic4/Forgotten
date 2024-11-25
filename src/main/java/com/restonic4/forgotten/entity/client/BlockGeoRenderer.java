package com.restonic4.forgotten.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.entity.common.BlockGeoEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class BlockGeoRenderer extends MobRenderer<BlockGeoEntity, BlockGeo<BlockGeoEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Forgotten.MOD_ID, "textures/entity/block_geo.png");

    public BlockGeoRenderer(EntityRendererProvider.Context context) {
        super(context, new BlockGeo<>(context.bakeLayer(BlockGeoLayers.BLOCK_GEO)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(BlockGeoEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(BlockGeoEntity mobEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
        if(mobEntity.isBaby()) {
            matrixStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            matrixStack.scale(1f, 1f, 1f);
        }

        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
