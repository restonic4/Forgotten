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
import com.restonic4.forgotten.util.helpers.RandomUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.effective.core.world.RenderedHypnotizingEntities;
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

                spawnParticles(entity);

                spawnParticles(entity.level(), entity.position().add(0, 0, 1));
                spawnParticles(entity.level(), entity.position().add(1, 0, 1));
                spawnParticles(entity.level(), entity.position().add(1, 0, 0));
                spawnParticles(entity.level(), entity.position().add(1, 0, -1));
                spawnParticles(entity.level(), entity.position().add(0, 0, -1));
                spawnParticles(entity.level(), entity.position().add(-1, 0, -1));
                spawnParticles(entity.level(), entity.position().add(-1, 0, 0));
                spawnParticles(entity.level(), entity.position().add(-1, 0, 1));
            }
        }

        poseStack.scale(3.01f, 3.01f, 3.01f);

        if (entity.hurtTime > 0) {
            float range = 0.02f;
            poseStack.translate(RandomUtil.randomBetween(-range, range), RandomUtil.randomBetween(-range, range), RandomUtil.randomBetween(-range, range));
        }

        if (entity.getHealth() == 2) {
            float range = 0.005f;
            poseStack.translate(RandomUtil.randomBetween(-range, range), RandomUtil.randomBetween(-range, range), RandomUtil.randomBetween(-range, range));
        } else if (entity.getHealth() == 1) {
            float range = 0.01f;
            poseStack.translate(RandomUtil.randomBetween(-range, range), RandomUtil.randomBetween(-range, range), RandomUtil.randomBetween(-range, range));
        } else if (entity.getHealth() == 0) {
            float range = 0.025f;
            poseStack.translate(RandomUtil.randomBetween(-range, range), RandomUtil.randomBetween(-range, range), RandomUtil.randomBetween(-range, range));
        }

        super.render(entity, f, g, poseStack, vertexConsumerProvider, i);
    }

    private void spawnParticles(Level level, Vec3 origin) {
        int particleCount = 100;

        BlockPos blockBelow = new BlockPos((int) origin.x, (int) origin.y, (int) origin.z).below();
        BlockState blockStateBelow = level.getBlockState(blockBelow);

        for (int i = 0; i < particleCount; i++) {
            double theta = Math.random() * 2 * Math.PI;
            double phi = Math.acos(2 * Math.random() - 1);
            double r = Math.random() * 0.5;

            double dx = r * Math.sin(phi) * Math.cos(theta);
            double dy = r * Math.sin(phi) * Math.sin(theta);
            double dz = r * Math.cos(phi);

            double speedMultiplier = 1;
            double vx = speedMultiplier * dx;
            double vy = speedMultiplier * dy + 0.1;
            double vz = speedMultiplier * dz;

            level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockStateBelow),
                    origin.x + dx, origin.y + dy, origin.z + dz,
                    vx, vy, vz);
        }
    }

    private void spawnParticles(Entity entity) {
        BlockPos blockBelow = entity.blockPosition().below();
        BlockState blockStateBelow = entity.level().getBlockState(blockBelow);

        int particleCount = 100;
        Level level = entity.level();
        Vec3 origin = entity.position();

        for (int i = 0; i < particleCount; i++) {
            double theta = Math.random() * 2 * Math.PI;
            double phi = Math.acos(2 * Math.random() - 1);
            double r = Math.random() * 0.5;

            double dx = r * Math.sin(phi) * Math.cos(theta);
            double dy = r * Math.sin(phi) * Math.sin(theta);
            double dz = r * Math.cos(phi);

            double speedMultiplier = 1;
            double vx = speedMultiplier * dx;
            double vy = speedMultiplier * dy + 0.1;
            double vz = speedMultiplier * dz;

            level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockStateBelow),
                    origin.x + dx, origin.y + dy, origin.z + dz,
                    vx, vy, vz);
        }
    }

}
