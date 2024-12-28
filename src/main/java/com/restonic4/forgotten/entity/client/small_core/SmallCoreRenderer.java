package com.restonic4.forgotten.entity.client.small_core;

import com.mojang.blaze3d.vertex.PoseStack;
import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.entity.common.SmallCoreEntity;
import com.restonic4.forgotten.util.helpers.RandomUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.effective.core.world.RenderedHypnotizingEntities;

public class SmallCoreRenderer extends MobRenderer<SmallCoreEntity, SmallCoreModel<SmallCoreEntity>> {
    private static final ResourceLocation NORMAL_TEXTURE = new ResourceLocation(Forgotten.MOD_ID, "textures/entity/small_core.png");
    private static final ResourceLocation DAMAGED_TEXTURE = new ResourceLocation(Forgotten.MOD_ID, "textures/entity/small_core_damaged.png");
    private static final ResourceLocation VERY_DAMAGED_TEXTURE = new ResourceLocation(Forgotten.MOD_ID, "textures/entity/small_core_very_damaged.png");

    public SmallCoreRenderer(EntityRendererProvider.Context context) {
        super(context, new SmallCoreModel<>(context.bakeLayer(SmallCoreLayers.SMALL_CORE)), 0.5f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SmallCoreEntity entity) {
        float health = entity.getHealth();

        if (health >= 3) {
            return NORMAL_TEXTURE;
        }

        if (health >= 2) {
            return DAMAGED_TEXTURE;
        }

        return VERY_DAMAGED_TEXTURE;
    }

    @Override
    public void render(SmallCoreEntity entity, float f, float g, PoseStack poseStack, MultiBufferSource vertexConsumerProvider, int i) {
        poseStack.scale(1.25f, 1.25f, 1.25f);

        if (entity.hurtTime > 0) {
            float range = 0.02f;
            poseStack.translate(RandomUtil.randomBetween(-range, range), RandomUtil.randomBetween(-range, range), RandomUtil.randomBetween(-range, range));
        }

        if (!entity.done) {
            super.render(entity, f, g, poseStack, vertexConsumerProvider, i);
        }
    }

    @Override
    protected void setupRotations(SmallCoreEntity livingEntity, PoseStack poseStack, float f, float g, float h) {
        if (Math.sqrt(Minecraft.getInstance().player.position().distanceToSqr(livingEntity.position())) < 20.0) {
            RenderedHypnotizingEntities.GLOWSQUIDS.add(livingEntity);
        }

        super.setupRotations(livingEntity, poseStack, f, g, h);
    }
}
