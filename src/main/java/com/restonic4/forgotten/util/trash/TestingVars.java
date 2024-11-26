package com.restonic4.forgotten.util.trash;

import com.restonic4.forgotten.registries.client.ForgottenLodestoneWorldParticleRenderType;
import com.restonic4.forgotten.registries.client.ForgottenRenderTypes;
import net.minecraft.client.particle.ParticleRenderType;
import team.lodestar.lodestone.handlers.RenderHandler;

public class TestingVars {
    public static ParticleRenderType renderType = ForgottenLodestoneWorldParticleRenderType.LUMITRANSPARENT_NO_FOG;
    public static RenderHandler.LodestoneRenderLayer renderTarget = RenderHandler.DELAYED_RENDER;
    public static float FAR_PLANE = 10000;

    public static float ROTX = 0;
    public static float ROTY = 0;
    public static float ROTZ = 0;
}
