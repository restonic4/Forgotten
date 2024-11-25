package com.restonic4.forgotten.registries.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.restonic4.forgotten.Forgotten;
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import team.lodestar.lodestone.LodestoneLib;
import team.lodestar.lodestone.events.LodestoneShaderRegistrationEvent;
import team.lodestar.lodestone.registry.client.LodestoneRenderTypeRegistry;
import team.lodestar.lodestone.registry.client.LodestoneShaderRegistry;
import team.lodestar.lodestone.systems.particle.render_types.LodestoneWorldParticleRenderType;
import team.lodestar.lodestone.systems.particle.world.type.LodestoneWorldParticleType;
import team.lodestar.lodestone.systems.rendering.LodestoneRenderType;
import team.lodestar.lodestone.systems.rendering.StateShards;
import team.lodestar.lodestone.systems.rendering.rendeertype.RenderTypeToken;
import team.lodestar.lodestone.systems.rendering.rendeertype.ShaderUniformHandler;
import team.lodestar.lodestone.systems.rendering.shader.ShaderHolder;
import com.mojang.datafixers.util.Pair;

import static com.mojang.blaze3d.vertex.DefaultVertexFormat.PARTICLE;
import static com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS;

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

    public static final RenderTypeToken BEAM_THINGY_TEXTURE = RenderTypeToken.createToken(new ResourceLocation(Forgotten.MOD_ID, "textures/vfx/concentrated_trail.png"));

    public static LazyRegistrar<ParticleType<?>> PARTICLES = LazyRegistrar.create(BuiltInRegistries.PARTICLE_TYPE, Forgotten.MOD_ID);

    //public static final RegistryObject<LodestoneWorldParticleType> WISP_PARTICLE = PARTICLES.register("wisp", LodestoneWorldParticleType::new);

    public static ShaderHolder COOL_PARTICLE = new ShaderHolder(new ResourceLocation(Forgotten.MOD_ID, "particle/cool_particle"), DefaultVertexFormat.PARTICLE, "LumiTransparency", "DepthFade");
    public static ShaderHolder WAVE_SHADER = new ShaderHolder(new ResourceLocation(Forgotten.MOD_ID, "program/wave"), DefaultVertexFormat.POSITION, "Time", "Alpha", "BeamCenter", "PlayerPos");

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
            //COOL_PARTICLE,
            LodestoneShaderRegistry.PARTICLE,
            TextureAtlas.LOCATION_PARTICLES,
            LodestoneRenderTypeRegistry.TRANSPARENT_FUNCTION
    );

    public CustomRenderTypes(String pName, Runnable pSetupState, Runnable pClearState) {
        super(pName, pSetupState, pClearState);
    }

    public static void init() {
        LodestoneShaderRegistrationEvent.EVENT.register((provider, shaderList) -> {
            shaderList.add(Pair.of(COOL_PARTICLE.createInstance(provider), LodestoneShaderRegistry.getConsumer()));
            shaderList.add(Pair.of(WAVE_SHADER.createInstance(provider), LodestoneShaderRegistry.getConsumer()));
        });
    }

    public static final LodestoneWorldParticleType WISP_PARTICLE = new LodestoneWorldParticleType();

    public static void registerCParticles() {
        ParticleFactoryRegistry.getInstance().register(WISP_PARTICLE, LodestoneWorldParticleType.Factory::new);
    }

    public static void registerSParticles() {
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, new ResourceLocation(Forgotten.MOD_ID, "wisp"), WISP_PARTICLE);
    }
}
