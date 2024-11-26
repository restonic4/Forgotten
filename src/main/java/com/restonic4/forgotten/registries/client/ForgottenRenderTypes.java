package com.restonic4.forgotten.registries.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.restonic4.forgotten.Forgotten;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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

public class ForgottenRenderTypes {
    public static LodestoneRenderType RENDER_TYPE_CUSTOM = LodestoneRenderTypeRegistry.applyUniformChanges(
            LodestoneRenderTypeRegistry.createGenericRenderType("render_type_custom", PARTICLE, QUADS, LodestoneRenderTypeRegistry.builder()
                    .setShaderState(ForgottenShaderHolders.COOL_PARTICLE)
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
}
