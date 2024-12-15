package com.restonic4.forgotten.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class CheaterScreen extends Screen {
    private final Component title;
    private final Component message;
    private final Component close;

    public CheaterScreen(String mods) {
        super(Component.literal("You cheater!"));
        this.title = Component.translatable("gui.forgotten.incompatibility.title.cheater");
        this.message = Component.translatable("gui.forgotten.incompatibility.message.cheater").append(" -> ").append(mods);
        this.close = Component.translatable("gui.forgotten.incompatibility.close");
    }

    protected void init() {
        super.init();

        int buttonWidth = 200;
        int buttonHeight = 20;
        int horizontalCenter = (this.width - buttonWidth) / 2;
        int verticalMargin = 20;

        int buttonY = this.height - buttonHeight - verticalMargin;

        this.addRenderableWidget(Button.builder(close, (button) -> {
            this.minecraft.close();
        }).bounds(horizontalCenter, buttonY, buttonWidth, buttonHeight).build());
    }

    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        guiGraphics.fillGradient(0, 0, this.width, this.height, -12574688, -11530224);

        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 90, 16777215);

        int messageWidth = this.width - 40;
        int lineHeight = this.font.lineHeight;
        int messageY = 110;

        for (FormattedCharSequence line : this.font.split(this.message, messageWidth)) {
            guiGraphics.drawCenteredString(this.font, line, this.width / 2, messageY, 16777215);
            messageY += lineHeight;
        }

        super.render(guiGraphics, i, j, f);
    }

    public boolean shouldCloseOnEsc() {
        return false;
    }
}
