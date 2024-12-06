package com.restonic4.forgotten.entity.client.core;

import com.mojang.blaze3d.vertex.PoseStack;
import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.client.CachedClientData;
import com.restonic4.forgotten.entity.client.small_core.SmallCoreLayers;
import com.restonic4.forgotten.entity.client.small_core.SmallCoreModel;
import com.restonic4.forgotten.entity.common.CoreEntity;
import com.restonic4.forgotten.entity.common.SmallCoreEntity;
import com.restonic4.forgotten.registries.common.ForgottenSounds;
import com.restonic4.forgotten.util.EasingSystem;
import com.restonic4.forgotten.util.ServerCache;
import com.restonic4.forgotten.util.helpers.MathHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.NotNull;
import team.lodestar.lodestone.handlers.ScreenshakeHandler;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.screenshake.PositionedScreenshakeInstance;
import team.lodestar.lodestone.systems.screenshake.ScreenshakeInstance;

public class CoreRenderer extends MobRenderer<CoreEntity, CoreModel<CoreEntity>> {
    private boolean alreadyDidScreenShake = false;

    private static final ResourceLocation NORMAL_TEXTURE = new ResourceLocation(Forgotten.MOD_ID, "textures/entity/core.png");
    private static final ResourceLocation DAMAGED_TEXTURE = new ResourceLocation(Forgotten.MOD_ID, "textures/entity/core_damaged.png");
    private static final ResourceLocation VERY_DAMAGED_TEXTURE = new ResourceLocation(Forgotten.MOD_ID, "textures/entity/core_very_damaged.png");

    public CoreRenderer(EntityRendererProvider.Context context) {
        super(context, new CoreModel<>(context.bakeLayer(CoreLayers.CORE)), 0.5f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull CoreEntity entity) {
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
    public void render(CoreEntity entity, float f, float g, PoseStack poseStack, MultiBufferSource vertexConsumerProvider, int i) {
        if (CachedClientData.groundTimeEnd != 0) {
            float easedProgress = EasingSystem.getEasedValue(CachedClientData.groundTimeStart, CachedClientData.groundTimeEnd, 1, 0, EasingSystem.EasingType.CUBIC_IN);

            if (easedProgress > 0) {
                poseStack.translate(0, 21 * easedProgress, 0);
            }

            if (easedProgress == 0 && !alreadyDidScreenShake) {
                alreadyDidScreenShake = true;

                ScreenshakeInstance beamShake = new PositionedScreenshakeInstance(4 * 20, entity.position(), 100, 200).setEasing(Easing.QUAD_IN_OUT).setIntensity(1, 0);
                ScreenshakeHandler.addScreenshake(beamShake);

                entity.level().playLocalSound(entity.position().x, entity.position().y, entity.position().z, ForgottenSounds.BRICK_EXPLOSION, SoundSource.BLOCKS, 2, 1, false);
            }
        }

        poseStack.scale(1.5f, 1.5f, 1.5f);

        super.render(entity, f, g, poseStack, vertexConsumerProvider, i);
    }
}
