package com.restonic4.forgotten.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.restonic4.forgotten.registries.client.CustomRenderTypes;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import team.lodestar.lodestone.handlers.RenderHandler;
import team.lodestar.lodestone.registry.client.LodestoneRenderTypeRegistry;
import team.lodestar.lodestone.systems.rendering.VFXBuilders;

import java.awt.*;

public class RenderingHelper {
    public static void renderBeamFromEntity(PoseStack poseStack, Vec3 startPos, Vec3 endPos, float width) {
        renderBeamFromEntity(
                poseStack,
                LodestoneRenderTypeRegistry.ADDITIVE_TEXTURE.applyAndCache(CustomRenderTypes.BEAM_THINGY_TEXTURE),
                RenderHandler.LATE_DELAYED_RENDER,
                startPos, endPos, width
        );
    }

    public static void renderBeamFromEntity(PoseStack poseStack, RenderType renderType, RenderHandler.LodestoneRenderLayer renderLayer, Vec3 startPos, Vec3 endPos, float width) {
        VFXBuilders.WorldVFXBuilder builder = VFXBuilders.createWorld();

        // Weird fix to the beam, as Sammy told me
        // This fixes the beam looking at the camera correctly
        poseStack.pushPose();
        poseStack.translate(-startPos.x, -startPos.y, -startPos.z);

        builder.replaceBufferSource(renderLayer.getTarget())
                .setRenderType(renderType)
                .setColor(new Color(255, 255, 255, 255))
                .setAlpha(1.0f);

        poseStack.pushPose();

        builder.renderBeam(poseStack.last().pose(), startPos, endPos, width);

        poseStack.popPose();

        // Recovers the original poseStack before the weird fix
        poseStack.popPose();
    }

    public static void renderBeam(PoseStack poseStack, Camera camera, Vec3 startPos, Vec3 endPos, float width) {
        renderBeam(
                poseStack,
                camera,
                LodestoneRenderTypeRegistry.ADDITIVE_TEXTURE.applyAndCache(CustomRenderTypes.BEAM_THINGY_TEXTURE),
                RenderHandler.LATE_DELAYED_RENDER,
                startPos, endPos, width
        );
    }

    public static void renderBeam(PoseStack poseStack, Camera camera, RenderType renderType, RenderHandler.LodestoneRenderLayer renderLayer, Vec3 startPos, Vec3 endPos, float width) {
        poseStack.pushPose();
        Vec3 weirdOffset = new Vec3(
                startPos.x - camera.getPosition().x,
                startPos.y - camera.getPosition().y,
                startPos.z - camera.getPosition().z
        );
        poseStack.translate(weirdOffset.x, weirdOffset.y, weirdOffset.z);

        renderBeamFromEntity(poseStack, renderType, renderLayer, startPos, endPos, width);

        poseStack.popPose();
    }
}
