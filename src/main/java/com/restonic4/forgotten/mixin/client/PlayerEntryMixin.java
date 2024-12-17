package com.restonic4.forgotten.mixin.client;

import com.restonic4.forgotten.client.gui.EtherealSendingScreen;
import com.restonic4.forgotten.networking.packets.SendBookPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.social.PlayerEntry;
import net.minecraft.client.gui.screens.social.SocialInteractionsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@Mixin(PlayerEntry.class)
public class PlayerEntryMixin {
    @Shadow @Final private Minecraft minecraft;

    @Shadow @Final private String playerName;
    @Shadow @Final private List<AbstractWidget> children;
    @Unique private Button sendButton;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0, shift = At.Shift.AFTER))
    public void PlayerEntry(Minecraft minecraft, SocialInteractionsScreen socialInteractionsScreen, UUID uUID, String string, Supplier supplier, boolean bl, CallbackInfo ci) {
        this.sendButton = Button.builder(Component.translatable("gui.forgotten.ethereal_sending_screen.send"), this::onSendPress)
                .bounds(0, 0, 80, 20)
                .build();

        this.sendButton.setTooltip(Tooltip.create(Component.translatable("gui.forgotten.ethereal_sending_screen.send.tooltip")));
        this.sendButton.setTooltipDelay(10);

        this.children.add(this.sendButton);
    }

    @Inject(method = "render", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I",
            shift = At.Shift.AFTER
    ), cancellable = true)
    public void render(GuiGraphics guiGraphics, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f, CallbackInfo ci) {
        if (this.minecraft.screen instanceof EtherealSendingScreen) {
            if (this.sendButton != null) {
                this.sendButton.setX(k + (l - this.sendButton.getWidth() - 4));
                this.sendButton.setY(j + (m - this.sendButton.getHeight()) / 2);
                this.sendButton.render(guiGraphics, n, o, f);
            }

            ci.cancel();
        }
    }

    @Unique
    private void onSendPress(Button button) {
        System.out.println("Send button pressed to " + this.playerName);

        this.minecraft.setScreen(null);

        SendBookPacket.sendToServer(this.playerName);
    }
}
