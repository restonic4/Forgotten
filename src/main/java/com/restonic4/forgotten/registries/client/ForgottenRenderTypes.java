package com.restonic4.forgotten.registries.client;

import net.minecraft.client.renderer.texture.TextureAtlas;
import team.lodestar.lodestone.registry.client.LodestoneRenderTypeRegistry;
import team.lodestar.lodestone.registry.client.LodestoneShaderRegistry;
import team.lodestar.lodestone.systems.particle.render_types.LodestoneWorldParticleRenderType;
import team.lodestar.lodestone.systems.rendering.LodestoneRenderType;
import team.lodestar.lodestone.systems.rendering.StateShards;
import team.lodestar.lodestone.systems.rendering.rendeertype.ShaderUniformHandler;

import static com.mojang.blaze3d.vertex.DefaultVertexFormat.PARTICLE;
import static com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS;

public class ForgottenRenderTypes {
    public static LodestoneRenderType LUMITRANSPARENT_NO_FOG = LodestoneRenderTypeRegistry.applyUniformChanges(
            LodestoneRenderTypeRegistry.createGenericRenderType("lumitransparent_no_fog", PARTICLE, QUADS, LodestoneRenderTypeRegistry.builder()
                    .setShaderState(ForgottenShaderHolders.LUMITRANSPARENT_NO_FOG_PARTICLE)
                    .setTransparencyState(StateShards.NORMAL_TRANSPARENCY)
                    .setTextureState(TextureAtlas.LOCATION_PARTICLES)
                    .setCullState(LodestoneRenderTypeRegistry.NO_CULL)
            ),
            ShaderUniformHandler.LUMITRANSPARENT
    );
}
