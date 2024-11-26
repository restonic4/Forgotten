package com.restonic4.forgotten.registries.common;

import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.entity.common.BlockGeoEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ForgottenEntities {
    public static final EntityType<BlockGeoEntity> BLOCK_GEO = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            new ResourceLocation(Forgotten.MOD_ID, "block_geo"),
            FabricEntityTypeBuilder.create(MobCategory.CREATURE, BlockGeoEntity::new)
                    .dimensions(EntityDimensions.fixed(1f, 1f))
                    .build()
    );

    public static void register() {
        FabricDefaultAttributeRegistry.register(BLOCK_GEO, BlockGeoEntity.createAttributes());
    }
}
