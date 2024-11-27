package com.restonic4.forgotten.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class HearthPulseManager {
    private static final List<HearthPulse> hearths = new ArrayList<>();

    public static HearthPulse createOrReset(int index) {
        HearthPulse hearth = null;

        for (int i = hearths.size() - 1; i >= 0; i--) {
            if (hearths.get(i).getIndex() == index) {
                hearth = hearths.get(i);
                hearth.reset();

                break;
            }
        }

        if (hearth == null) {
            hearth = new HearthPulse(index);
            hearths.add(hearth);
        }

        return hearth;
    }

    public static HearthPulse create(int index) {
        HearthPulse hearth = null;

        for (int i = hearths.size() - 1; i >= 0; i--) {
            if (hearths.get(i).getIndex() == index) {
                hearth = hearths.get(i);

                break;
            }
        }

        if (hearth == null) {
            hearth = new HearthPulse(index);
            hearths.add(hearth);
        }

        return hearth;
    }

    public static void render(GuiGraphics guiGraphics, int currentHearthIndex, int x, int y, int u, int v) {
        for (int i = hearths.size() - 1; i >= 0; i--) {
            if (hearths.get(i).getIndex() == currentHearthIndex) {
                hearths.get(i).render(guiGraphics, x, y, u, v);
            }
        }
    }

    public static void reset() {
        if (!hearths.isEmpty()) {
            hearths.clear();
        }
    }
}
