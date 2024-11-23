package com.restonic4.forgotten.util;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import team.lodestar.lodestone.LodestoneLib;
import team.lodestar.lodestone.handlers.RenderHandler;
import team.lodestar.lodestone.registry.client.LodestoneRenderTypeRegistry;
import team.lodestar.lodestone.registry.client.LodestoneShaderRegistry;
import team.lodestar.lodestone.systems.particle.render_types.LodestoneWorldParticleRenderType;
import team.lodestar.lodestone.systems.rendering.LodestoneRenderType;
import team.lodestar.lodestone.systems.rendering.StateShards;
import team.lodestar.lodestone.systems.rendering.rendeertype.RenderTypeProvider;
import team.lodestar.lodestone.systems.rendering.rendeertype.RenderTypeToken;

public class LodestoneVars {
    public static ParticleRenderType renderType = LodestoneWorldParticleRenderType.ADDITIVE;
    public static RenderHandler.LodestoneRenderLayer renderTarget = RenderHandler.DELAYED_RENDER;
    public static float FAR_PLANE = 10000;
}
