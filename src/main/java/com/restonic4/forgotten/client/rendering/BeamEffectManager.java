package com.restonic4.forgotten.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class BeamEffectManager {
    private static final List<BeamEffect> beams = new ArrayList<>();

    public static BeamEffect create() {
        BeamEffect beamEffect = new BeamEffect();
        beams.add(beamEffect);

        return beamEffect;
    }

    public static void render(PoseStack poseStack, Matrix4f matrix4f, Camera camera) {
        for (int i = beams.size() - 1; i >= 0; i--) {
            if (beams.get(i).isFinished()) {
                beams.get(i).cleanup();
                beams.remove(i);
            } else {
                beams.get(i).render(poseStack, matrix4f, camera);
            }
        }
    }
}
