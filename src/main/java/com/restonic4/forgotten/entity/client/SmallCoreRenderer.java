package com.restonic4.forgotten.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.entity.common.BlockGeoEntity;
import com.restonic4.forgotten.entity.common.SmallCoreEntity;
import com.restonic4.forgotten.util.helpers.RenderingHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class SmallCoreRenderer extends MobRenderer<SmallCoreEntity, SmallCoreModel<SmallCoreEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Forgotten.MOD_ID, "textures/entity/small_core.png");

    public SmallCoreRenderer(EntityRendererProvider.Context context) {
        super(context, new SmallCoreModel<>(context.bakeLayer(SmallCoreLayers.SMALL_CORE)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(BlockGeoEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(SmallCoreEntity entity, float f, float g, PoseStack poseStack, MultiBufferSource vertexConsumerProvider, int i) {
        super.render(entity, f, g, poseStack, vertexConsumerProvider, i);
    }
}
