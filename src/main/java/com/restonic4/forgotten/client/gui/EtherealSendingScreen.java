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
    }
}
