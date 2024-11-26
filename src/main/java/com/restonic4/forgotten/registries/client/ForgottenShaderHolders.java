package com.restonic4.forgotten.registries.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.datafixers.util.Pair;
import com.restonic4.forgotten.Forgotten;
import net.minecraft.resources.ResourceLocation;
import team.lodestar.lodestone.events.LodestoneShaderRegistrationEvent;
import team.lodestar.lodestone.registry.client.LodestoneShaderRegistry;
import team.lodestar.lodestone.systems.rendering.shader.ShaderHolder;

public class ForgottenShaderHolders {
    public static ShaderHolder COOL_PARTICLE = new ShaderHolder(new ResourceLocation(Forgotten.MOD_ID, "particle/cool_particle"), DefaultVertexFormat.PARTICLE, "LumiTransparency", "DepthFade");
    public static ShaderHolder WAVE_SHADER = new ShaderHolder(new ResourceLocation(Forgotten.MOD_ID, "program/wave"), DefaultVertexFormat.POSITION, "Time", "Alpha", "BeamCenter", "PlayerPos");
    public static ShaderHolder QUAD_SHADER = new ShaderHolder(new ResourceLocation(Forgotten.MOD_ID, "program/quad"), DefaultVertexFormat.POSITION);

    public static void register() {
        LodestoneShaderRegistrationEvent.EVENT.register((provider, shaderList) -> {
            shaderList.add(Pair.of(COOL_PARTICLE.createInstance(provider), LodestoneShaderRegistry.getConsumer()));
            shaderList.add(Pair.of(WAVE_SHADER.createInstance(provider), LodestoneShaderRegistry.getConsumer()));
            shaderList.add(Pair.of(QUAD_SHADER.createInstance(provider), LodestoneShaderRegistry.getConsumer()));
        });
    }
}
