package com.restonic4.forgotten.registries.client;

import net.minecraft.client.renderer.texture.TextureAtlas;
import team.lodestar.lodestone.registry.client.LodestoneRenderTypeRegistry;
import team.lodestar.lodestone.registry.client.LodestoneShaderRegistry;
import team.lodestar.lodestone.systems.particle.render_types.LodestoneWorldParticleRenderType;

public class ForgottenLodestoneWorldParticleRenderType {
    public static LodestoneWorldParticleRenderType LUMITRANSPARENT_NO_FOG = new LodestoneWorldParticleRenderType(
            ForgottenRenderTypes.LUMITRANSPARENT_NO_FOG,
            LodestoneShaderRegistry.PARTICLE,
            TextureAtlas.LOCATION_PARTICLES,
            LodestoneRenderTypeRegistry.TRANSPARENT_FUNCTION
    );
}
