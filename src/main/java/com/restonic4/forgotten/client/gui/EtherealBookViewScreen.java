package com.restonic4.forgotten.client.gui;

import com.restonic4.forgotten.Forgotten;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.Iterator;

public class EtherealBookViewScreen extends BookViewScreen {
    public static final ResourceLocation TEXTURE = new ResourceLocation(Forgotten.MOD_ID, "textures/gui/ethereal_book.png");

    public EtherealBookViewScreen(WrittenBookAccess writtenBookAccess) {
        super(writtenBookAccess);
    }

    //TODO: Evitar tener esta clase ya que crea porblemas, hacer que se abra la normal y simplementer hacerle un mixin para uqe cambie la textura en el blit(), asi tambien deberie funcionar el lectern

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        this.renderBackground(guiGraphics);
        int k = (this.width - 192) / 2;
        int l = 2;
        guiGraphics.blit(TEXTURE, k, 2, 0, 0, 192, 192);
        if (super.cachedPage != super.currentPage) {
            FormattedText formattedText = super.bookAccess.getPage(super.currentPage);
            super.cachedPageComponents = this.font.split(formattedText, 114);
            super.pageMsg = Component.translatable("book.pageIndicator", super.currentPage + 1, Math.max(this.getNumPages(), 1));
        }

        super.cachedPage = super.currentPage;
        int m = this.font.width(super.pageMsg);
        guiGraphics.drawString(this.font, super.pageMsg, k - m + 192 - 44, 18, 0, false);
        int n = Math.min(128 / 9, this.cachedPageComponents.size());

        for (int o = 0; o < n; o++) {
            FormattedCharSequence formattedCharSequence = (FormattedCharSequence)super.cachedPageComponents.get(o);
            guiGraphics.drawString(this.font, formattedCharSequence, k + 36, 32 + o * 9, 0, false);
        }

        Style style = this.getClickedComponentStyleAt((double)i, (double)j);
        if (style != null) {
            guiGraphics.renderComponentHoverEffect(this.font, style, i, j);
        }

        Iterator var5 = this.renderables.iterator();

        while(var5.hasNext()) {
            Renderable renderable = (Renderable)var5.next();
            renderable.render(guiGraphics, i, j, f);
        }
    }
}
