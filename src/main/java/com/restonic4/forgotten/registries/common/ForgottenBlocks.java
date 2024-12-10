package com.restonic4.forgotten.registries.common;

import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.block.AltarBlock;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public class ForgottenBlocks {
    private static final List<Item> blockItems = new ArrayList<>();

    public static final Block ALTAR = registerBlock("altar",
            new AltarBlock(FabricBlockSettings.copyOf(Blocks.COBBLESTONE).nonOpaque()));

    public static void register() {

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
