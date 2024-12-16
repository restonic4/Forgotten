package com.restonic4.forgotten.mixin.client;

import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.item.EtherealWrittenBook;
import com.restonic4.forgotten.item.EtherealWrittenBookAccess;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BookViewScreen.class)
public class BookViewScreenMixin {
    @Shadow public BookViewScreen.BookAccess bookAccess;
    @Unique private final ResourceLocation ETHEREAL_BOOK_TEXTURE = new ResourceLocation(Forgotten.MOD_ID, "textures/gui/ethereal_book.png");

    @Redirect(
            method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"
            )
    )
    private void redirectBlit(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int u, int v, int width, int height) {
        if (isEtherealBook()) {
            texture = ETHEREAL_BOOK_TEXTURE;
        }

        guiGraphics.blit(texture, x, y, u, v, width, height);
    }

    @Unique
    private boolean isEtherealBook() {
        return this.bookAccess instanceof EtherealWrittenBookAccess;
    }
}
