package com.restonic4.forgotten.util.trash;

import team.lodestar.lodestone.systems.particle.screen.ScreenParticleHolder;

public class OldCodeThatCouldBeUsefulAtSomePoint {
    public static final ScreenParticleHolder SCREEN_PARTICLES = new ScreenParticleHolder();

    //public static ResourceLocation TEXTURE = new ResourceLocation(LodestoneLib.LODESTONE, "textures/particle/wisp.png");

    /*public static RenderTypeProvider MY_PROVIDER = new RenderTypeProvider(token ->
            LodestoneRenderTypeRegistry.createGenericRenderType("my_provider", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, LodestoneRenderTypeRegistry.builder()
                    .setShaderState(LodestoneShaderRegistry.LODESTONE_TEXTURE)
                    .setTransparencyState(StateShards.ADDITIVE_TRANSPARENCY)
                    .setLightmapState(LodestoneRenderTypeRegistry.LIGHTMAP)
                    .setCullState(LodestoneRenderTypeRegistry.CULL)
                    .setTextureState(token.get())
            )
    );*/

    //public static LodestoneRenderType RENDER_TYPE_CUSTOM = MY_PROVIDER.apply(RenderTypeToken.createToken(TextureAtlas.LOCATION_PARTICLES));

    /*public static final Runnable WEIRD = () -> {
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
    };


    public static LodestoneRenderType RENDER_TYPE_CUSTOM = LodestoneRenderTypeRegistry.createGenericRenderType("render_type_custom", PARTICLE, QUADS, LodestoneRenderTypeRegistry.builder()
            .setShaderState(LodestoneShaderRegistry.PARTICLE)
            .setTransparencyState(StateShards.NORMAL_TRANSPARENCY)
            .setTextureState(TextureAtlas.LOCATION_PARTICLES)
            .setCullState(LodestoneRenderTypeRegistry.NO_CULL)
    );*/

    /*public static RenderType MY_RENDERTYPE = create("my_rendertype", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.TRIANGLES, 2097152, true, true,
            RenderType.CompositeState.builder()
                    .setShaderState(ShaderRegistry.MY_SHADER.getShard())
                    .setCullState(NO_CULL)
                    .createCompositeState(true)
    );*/

    //public static final RegistryObject<LodestoneWorldParticleType> WISP_PARTICLE = PARTICLES.register("wisp", LodestoneWorldParticleType::new);

    //public static final LodestoneRenderType RENDER_TYPE_CUSTOM = LodestoneRenderTypeRegistry.copyWithUniformChanges("forgotten:render_type_custom", LodestoneRenderTypeRegistry.TRANSPARENT_PARTICLE, ShaderUniformHandler.LUMITRANSPARENT);
}
