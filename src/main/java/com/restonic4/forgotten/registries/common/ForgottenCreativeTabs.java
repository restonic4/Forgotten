package com.restonic4.forgotten.registries.common;

import com.restonic4.forgotten.Forgotten;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ForgottenCreativeTabs {
    public static final CreativeModeTab FORGOTTEN = Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            new ResourceLocation(Forgotten.MOD_ID, "forgotten"),
            FabricItemGroup.builder()
                    .title(Component.translatable("creative_mode_tab.forgotten"))
                    .icon(() -> new ItemStack(ForgottenItems.PLAYER_SOUL))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ForgottenItems.PLAYER_SOUL);
                        ForgottenBlocks.onCreativeTabRegistration(output);
                    })
                    .build()
    );

    public static void register() {

    }
}
