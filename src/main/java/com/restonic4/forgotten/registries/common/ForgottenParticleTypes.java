package com.restonic4.forgotten.registries.common;

import com.restonic4.forgotten.Forgotten;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import team.lodestar.lodestone.systems.particle.world.type.LodestoneWorldParticleType;

public class ForgottenParticleTypes {
    public static final LodestoneWorldParticleType WISP_PARTICLE = new LodestoneWorldParticleType();

    public static void registerClient() {
        ParticleFactoryRegistry.getInstance().register(WISP_PARTICLE, LodestoneWorldParticleType.Factory::new);
    }

    public static void registerCommon() {
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, new ResourceLocation(Forgotten.MOD_ID, "wisp"), WISP_PARTICLE);
    }
}
