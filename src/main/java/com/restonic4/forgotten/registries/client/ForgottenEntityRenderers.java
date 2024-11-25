package com.restonic4.forgotten.registries.client;

import com.restonic4.forgotten.entity.client.BlockGeo;
import com.restonic4.forgotten.entity.client.BlockGeoLayers;
import com.restonic4.forgotten.entity.client.BlockGeoRenderer;
import com.restonic4.forgotten.entity.common.BlockGeoEntity;
import com.restonic4.forgotten.registries.ForgottenEntities;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class ForgottenEntityRenderers {
    public static void register() {
        EntityRendererRegistry.register(ForgottenEntities.BLOCK_GEO, BlockGeoRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(BlockGeoLayers.BLOCK_GEO, BlockGeo::getTexturedModelData);
    }
}
