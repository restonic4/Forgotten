package com.restonic4.forgotten.saving;

import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.entity.common.ChainEntity;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.resources.ResourceLocation;

public class Components implements EntityComponentInitializer {
    public static final ComponentKey<ChainStateComponent> CHAIN_STATE =
            ComponentRegistry.getOrCreate(new ResourceLocation(Forgotten.MOD_ID, "chain_state"), ChainStateComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.beginRegistration(ChainEntity.class, CHAIN_STATE).respawnStrategy(RespawnCopyStrategy.ALWAYS_COPY).end(ChainStateComponent::new);
    }
}
