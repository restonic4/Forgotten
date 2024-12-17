package com.restonic4.forgotten.registries.client;

import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.mixin.client.LecternRenderMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.nio.file.Path;

public class ForgottenMaterials {
    private static final ResourceLocation ETHEREAL_BOOK_LOCATION_TEXTURE = new ResourceLocation(Forgotten.MOD_ID, "entity/ethereal_book");
    public static final Material ETHEREAL_BOOK_LOCATION = new Material(TextureAtlas.LOCATION_BLOCKS, ETHEREAL_BOOK_LOCATION_TEXTURE);
    //public static final Material ETHEREAL_BOOK_LOCATION = registerToAtlas(ETHEREAL_BOOK_LOCATION_TEXTURE);

    public static void register() {

    }

    public static void downloadAtlas() {
        new Thread(() -> {
            boolean downloaded = false;
            while (!downloaded) {
                TextureManager textureManager = Minecraft.getInstance().getTextureManager();
                if (textureManager != null) {
                    downloaded = true;
                    textureManager.dumpAllSheets(Path.of("C:/Users/Marcos/Downloads/generated_atlas"));
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception ignored) {}
                }
            }
        }).start();
    }
}
