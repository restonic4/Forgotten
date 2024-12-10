package com.restonic4.forgotten.registries.common;

import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.block.AltarBlock;
import com.restonic4.forgotten.block.AltarBlockEntity;
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import com.restonic4.forgotten.block.AltarBlockEntityRenderer;

import java.util.ArrayList;
import java.util.List;

import static team.lodestar.lodestone.LodestoneLib.LODESTONE;

public class ForgottenBlocks {
    private static final List<Item> blockItems = new ArrayList<>();

    public static final Block ALTAR = registerBlock("altar",
            new AltarBlock(FabricBlockSettings.copyOf(Blocks.BEDROCK).dropsNothing().nonOpaque()));

    public static final BlockEntityType<AltarBlockEntity> ALTAR_BLOCK_ENTITY = registerBlockEntity(
            "altar_block_entity",
            FabricBlockEntityTypeBuilder.create(AltarBlockEntity::new, ForgottenBlocks.ALTAR).build()
    );

    public static void registerClient() {
        BlockEntityRenderers.register(ALTAR_BLOCK_ENTITY, AltarBlockEntityRenderer::new);
    }

    public static void registerCommon() {

    }

    public static <T extends BlockEntityType<?>> T registerBlockEntity(String path, T blockEntityType) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(Forgotten.MOD_ID, path), blockEntityType);
    }

    private static Block registerBlock(String name, Block block) {
        blockItems.add(registerBlockItem(name, block));
        return Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(Forgotten.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Forgotten.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }

    public static void onCreativeTabRegistration(CreativeModeTab.Output output) {
        for (Item item : blockItems) {
            output.accept(item);
        }
    }
}
