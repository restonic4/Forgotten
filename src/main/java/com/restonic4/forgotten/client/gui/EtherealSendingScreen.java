package com.restonic4.forgotten.client.gui;

import net.minecraft.client.gui.screens.social.SocialInteractionsScreen;

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
