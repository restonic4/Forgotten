package com.restonic4.forgotten.registries.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.restonic4.forgotten.Forgotten;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import team.lodestar.lodestone.LodestoneLib;
import team.lodestar.lodestone.events.LodestoneShaderRegistrationEvent;
import team.lodestar.lodestone.registry.client.LodestoneRenderTypeRegistry;
import team.lodestar.lodestone.registry.client.LodestoneShaderRegistry;
import team.lodestar.lodestone.systems.particle.render_types.LodestoneWorldParticleRenderType;
import team.lodestar.lodestone.systems.rendering.LodestoneRenderType;
import team.lodestar.lodestone.systems.rendering.StateShards;
import team.lodestar.lodestone.systems.rendering.rendeertype.RenderTypeProvider;
import team.lodestar.lodestone.systems.rendering.rendeertype.RenderTypeToken;
import team.lodestar.lodestone.systems.rendering.rendeertype.ShaderUniformHandler;
import team.lodestar.lodestone.systems.rendering.shader.ExtendedShaderInstance;
import team.lodestar.lodestone.systems.rendering.shader.ShaderHolder;
import com.mojang.datafixers.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.mojang.blaze3d.vertex.DefaultVertexFormat.PARTICLE;
import static com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS;
import static team.lodestar.lodestone.LodestoneLib.lodestonePath;

public class CustomRenderTypes extends RenderStateShard {
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

    public static ShaderHolder COOL_PARTICLE = new ShaderHolder(new ResourceLocation(Forgotten.MOD_ID, "particle/cool_particle"), DefaultVertexFormat.PARTICLE, "LumiTransparency", "DepthFade");

    //public static final LodestoneRenderType RENDER_TYPE_CUSTOM = LodestoneRenderTypeRegistry.copyWithUniformChanges("forgotten:render_type_custom", LodestoneRenderTypeRegistry.TRANSPARENT_PARTICLE, ShaderUniformHandler.LUMITRANSPARENT);

    public static LodestoneRenderType RENDER_TYPE_CUSTOM = LodestoneRenderTypeRegistry.applyUniformChanges(
            LodestoneRenderTypeRegistry.createGenericRenderType("render_type_custom", PARTICLE, QUADS, LodestoneRenderTypeRegistry.builder()
                    .setShaderState(COOL_PARTICLE)
                    .setTransparencyState(StateShards.NORMAL_TRANSPARENCY)
                    .setTextureState(TextureAtlas.LOCATION_PARTICLES)
                    .setCullState(LodestoneRenderTypeRegistry.NO_CULL)
            ),
            ShaderUniformHandler.LUMITRANSPARENT
    );

    public static LodestoneWorldParticleRenderType particleType = new LodestoneWorldParticleRenderType(
            RENDER_TYPE_CUSTOM,
            COOL_PARTICLE,
            TextureAtlas.LOCATION_PARTICLES,
            LodestoneRenderTypeRegistry.TRANSPARENT_FUNCTION
    );

    public CustomRenderTypes(String pName, Runnable pSetupState, Runnable pClearState) {
        super(pName, pSetupState, pClearState);
    }

    public static void init() {
        LodestoneShaderRegistrationEvent.EVENT.register((provider, shaderList) -> {
            shaderList.add(Pair.of(COOL_PARTICLE.createInstance(provider), LodestoneShaderRegistry.getConsumer()));
        });
    }
}
