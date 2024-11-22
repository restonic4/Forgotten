package com.restonic4.forgotten.util;

import net.minecraft.client.particle.ParticleRenderType;
import team.lodestar.lodestone.handlers.RenderHandler;
import team.lodestar.lodestone.systems.particle.render_types.LodestoneWorldParticleRenderType;

public class LodestoneCommandVars {
    public static ParticleRenderType renderType = LodestoneWorldParticleRenderType.ADDITIVE;
    public static RenderHandler.LodestoneRenderLayer renderTarget = RenderHandler.DELAYED_RENDER;
}
