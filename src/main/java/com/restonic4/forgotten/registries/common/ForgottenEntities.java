package com.restonic4.forgotten.registries.common;

import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.entity.common.ChainEntity;
import com.restonic4.forgotten.entity.common.SmallCoreEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ForgottenEntities {
    public static final EntityType<SmallCoreEntity> SMALL_CORE = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            new ResourceLocation(Forgotten.MOD_ID, "small_core"),
            FabricEntityTypeBuilder.create(MobCategory.CREATURE, SmallCoreEntity::new)
                    .dimensions(EntityDimensions.fixed(1f, 1f))
                    .fireImmune()
                    .build()
    );

    public static final EntityType<ChainEntity> CHAIN = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            new ResourceLocation(Forgotten.MOD_ID, "chain"),
            FabricEntityTypeBuilder.create(MobCategory.CREATURE, ChainEntity::new)
                    .dimensions(EntityDimensions.fixed(1f, 1f))
                    .fireImmune()
                    .build()
    );

    public static void register() {
        FabricDefaultAttributeRegistry.register(SMALL_CORE, SmallCoreEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(CHAIN, ChainEntity.createAttributes());
    }
}
