package com.restonic4.forgotten.util;

import com.restonic4.forgotten.client.gui.EtherealSendingScreen;
import net.minecraft.client.Minecraft;

public class GuiHelper {
    public static void openEtherealBook() {
        Minecraft.getInstance().setScreen(new EtherealSendingScreen());
    }
}
