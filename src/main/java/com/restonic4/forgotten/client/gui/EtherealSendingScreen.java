package com.restonic4.forgotten.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.social.PlayerEntry;
import net.minecraft.client.gui.screens.social.SocialInteractionsScreen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;

public class EtherealSendingScreen extends SocialInteractionsScreen {
    public EtherealSendingScreen() {
        super();
    }

    @Override
    protected void init() {
        super.init();

        this.removeWidget(super.allButton);
        this.removeWidget(super.hiddenButton);
        this.removeWidget(super.blockedButton);

        for (PlayerEntry playerEntry : super.socialInteractionsPlayerList.players) {
            this.addRenderableWidget(Button.builder(
                                    Component.literal("Send to " + playerEntry.getPlayerName()),
                                    button -> this.onSendButtonClicked(playerEntry)
                            )
                            .bounds(this.marginX() + 50, 100 + (super.socialInteractionsPlayerList.players.indexOf(playerEntry) * 25), 150, 20)
                            .build()
            );
        }

        /*this.addRenderableWidget(Button.builder(Component.literal("Send"), button -> this.onSendButtonClicked())
                .bounds(this.marginX() + 50, 100, 100, 20)
                .build());*/
    }

    private void onSendButtonClicked(PlayerEntry playerEntry) {
        Minecraft.getInstance().player.sendSystemMessage(Component.literal("Send button clicked! " + playerEntry.getPlayerName()));
    }
}
