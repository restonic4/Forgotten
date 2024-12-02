package com.restonic4.forgotten.registries.client;

import com.restonic4.forgotten.entity.client.chain.ChainLayers;
import com.restonic4.forgotten.entity.client.chain.ChainModel;
import com.restonic4.forgotten.entity.client.chain.ChainRenderer;
import com.restonic4.forgotten.entity.client.small_core.SmallCoreLayers;
import com.restonic4.forgotten.entity.client.small_core.SmallCoreModel;
import com.restonic4.forgotten.entity.client.small_core.SmallCoreRenderer;
import com.restonic4.forgotten.registries.common.ForgottenEntities;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class ForgottenEntityRenderers {
    public static void register() {
        EntityRendererRegistry.register(ForgottenEntities.SMALL_CORE, SmallCoreRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(SmallCoreLayers.SMALL_CORE, SmallCoreModel::getTexturedModelData);

        EntityRendererRegistry.register(ForgottenEntities.CHAIN, ChainRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ChainLayers.CHAIN, ChainModel::getTexturedModelData);
    }
}
