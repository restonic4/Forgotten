package com.restonic4.forgotten.entity.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.client.RenderingHelper;
import com.restonic4.forgotten.entity.common.BlockGeoEntity;
import com.restonic4.forgotten.registries.client.CustomRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import team.lodestar.lodestone.handlers.RenderHandler;
import team.lodestar.lodestone.registry.client.LodestoneRenderTypeRegistry;
import team.lodestar.lodestone.registry.common.particle.LodestoneParticleRegistry;
import team.lodestar.lodestone.systems.rendering.VFXBuilders;

import java.awt.*;

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
    public void render(BlockGeoEntity entity, float f, float g, PoseStack poseStack, MultiBufferSource vertexConsumerProvider, int i) {
        if(entity.isBaby()) {
            poseStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            poseStack.scale(1f, 1f, 1f);
        }

        RenderType renderType = LodestoneRenderTypeRegistry.ADDITIVE_TEXTURE.applyAndCache(CustomRenderTypes.BEAM_THINGY_TEXTURE);

        Vec3 startPos = entity.position();
        Vec3 endPos = new Vec3(0, 10, 0);

        RenderingHelper.renderBeamFromEntity(poseStack, startPos, endPos, 5);

        super.render(entity, f, g, poseStack, vertexConsumerProvider, i);
    }
}
