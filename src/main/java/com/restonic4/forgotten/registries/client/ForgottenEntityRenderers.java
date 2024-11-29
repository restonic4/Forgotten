package com.restonic4.forgotten.registries.client;

import com.restonic4.forgotten.entity.client.*;
import com.restonic4.forgotten.registries.common.ForgottenEntities;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import team.lodestar.lodestone.registry.common.particle.LodestoneParticleRegistry;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;
import team.lodestar.lodestone.systems.particle.render_types.LodestoneWorldParticleRenderType;

public class ForgottenEntityRenderers {
    public static void register() {
        EntityRendererRegistry.register(ForgottenEntities.SMALL_CORE, SmallCoreRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(SmallCoreLayers.SMALL_CORE, SmallCoreModel::getTexturedModelData);
    }
}
