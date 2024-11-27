package com.restonic4.forgotten.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class SkyWaveEffectManager {
    private static final List<SkyWaveEffect> waves = new ArrayList<>();

    public static SkyWaveEffect create() {
        SkyWaveEffect waveEffect = new SkyWaveEffect();
        waves.add(waveEffect);

        return waveEffect;
    }

    public static void render(PoseStack poseStack, Matrix4f matrix4f) {
        for (int i = waves.size() - 1; i >= 0; i--) {
            if (waves.get(i).isFinished()) {
                waves.remove(i);
            } else {
                waves.get(i).render(poseStack, matrix4f);
            }
        }
    }
}
