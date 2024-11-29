package com.restonic4.forgotten.registries.client;

import com.restonic4.forgotten.entity.client.*;
import com.restonic4.forgotten.registries.common.ForgottenEntities;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class ForgottenEntityRenderers {
    public static void register() {
        EntityRendererRegistry.register(ForgottenEntities.BLOCK_GEO, BlockGeoRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(BlockGeoLayers.BLOCK_GEO, BlockGeo::getTexturedModelData);

        EntityRendererRegistry.register(ForgottenEntities.SMALL_CORE, SmallCoreRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(BlockGeoLayers.BLOCK_GEO, SmallCoreModel::getTexturedModelData);
    }
}
