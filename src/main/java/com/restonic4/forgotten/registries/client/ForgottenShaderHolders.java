package com.restonic4.forgotten.registries.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.datafixers.util.Pair;
import com.restonic4.forgotten.Forgotten;
import net.minecraft.resources.ResourceLocation;
import team.lodestar.lodestone.events.LodestoneShaderRegistrationEvent;
import team.lodestar.lodestone.registry.client.LodestoneShaderRegistry;
import team.lodestar.lodestone.systems.rendering.shader.ShaderHolder;

public class ForgottenShaderHolders {
    public static ShaderHolder LUMITRANSPARENT_NO_FOG_PARTICLE = new ShaderHolder(new ResourceLocation(Forgotten.MOD_ID, "particle/lumitransparent_no_fog_particle"), DefaultVertexFormat.PARTICLE, "LumiTransparency", "DepthFade");
    public static ShaderHolder SKY_WAVE = new ShaderHolder(new ResourceLocation(Forgotten.MOD_ID, "program/sky_wave"), DefaultVertexFormat.POSITION, "Time", "Alpha", "BeamCenter", "PlayerPos");
    public static ShaderHolder SIMPLE_COLOR = new ShaderHolder(new ResourceLocation(Forgotten.MOD_ID, "program/simple_color"), DefaultVertexFormat.POSITION);

    public static void register() {
        LodestoneShaderRegistrationEvent.EVENT.register((provider, shaderList) -> {
            shaderList.add(Pair.of(LUMITRANSPARENT_NO_FOG_PARTICLE.createInstance(provider), LodestoneShaderRegistry.getConsumer()));
            shaderList.add(Pair.of(SKY_WAVE.createInstance(provider), LodestoneShaderRegistry.getConsumer()));
            shaderList.add(Pair.of(SIMPLE_COLOR.createInstance(provider), LodestoneShaderRegistry.getConsumer()));
        });
    }
}
