package com.restonic4.forgotten.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class EnergyOrbEffectManager {
    private static final List<EnergyOrbEffect> orbs = new ArrayList<>();

    public static EnergyOrbEffect create() {
        EnergyOrbEffect orbEffect = new EnergyOrbEffect();
        orbs.add(orbEffect);

        return orbEffect;
    }

    public static void render(PoseStack poseStack, Matrix4f matrix4f, Camera camera) {
        for (int i = orbs.size() - 1; i >= 0; i--) {
            if (orbs.get(i).isFinished()) {
                orbs.get(i).cleanup();
                orbs.remove(i);
            } else {
                orbs.get(i).render(poseStack, matrix4f, camera);
            }
        }
    }
}
