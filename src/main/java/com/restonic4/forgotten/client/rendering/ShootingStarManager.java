package com.restonic4.forgotten.client.rendering;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.util.EasingSystem;
import com.restonic4.forgotten.util.helpers.MathHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.Random;

public class ShootingStarManager {
    public static final ResourceLocation STAR_LOCATION = new ResourceLocation(Forgotten.MOD_ID, "textures/environment/star.png");

    private static long spawnAnimationStart = 0;
    private static long spawnAnimationEnd = 0;

    private static float size = 30.0f;
    private static float rotation = 0.0f;
    private static float posX = 0.0f;
    private static float posY = 100.0f;
    private static float posZ = 0.0f;

    public static void loadStarDataFromServer(long spawnAnimationStart, long spawnAnimationEnd, float size, float rotation, float x, float y, float z) {
        ShootingStarManager.spawnAnimationStart = spawnAnimationStart;
        ShootingStarManager.spawnAnimationEnd = spawnAnimationEnd;

        ShootingStarManager.size = size;
        ShootingStarManager.rotation = rotation;
        ShootingStarManager.posX = x;
        ShootingStarManager.posY = y;
        ShootingStarManager.posZ = z;
    }

    public static void render(PoseStack poseStack, Matrix4f matrix4f, Camera camera, float rainAlpha) {
        poseStack.pushPose();

        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        Matrix4f matrix4f3 = poseStack.last().pose();

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, STAR_LOCATION);

        poseStack.translate(posX, posY, posZ);
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        float easedSize = EasingSystem.getEasedValue(spawnAnimationStart, spawnAnimationEnd, 0, size, EasingSystem.EasingType.QUAD_IN_OUT);

        bufferBuilder.vertex(matrix4f3, -easedSize, 0.0F, -easedSize).uv(0.0F, 0.0F).endVertex();
        bufferBuilder.vertex(matrix4f3, easedSize, 0.0F, -easedSize).uv(1.0F, 0.0F).endVertex();
        bufferBuilder.vertex(matrix4f3, easedSize, 0.0F, easedSize).uv(1.0F, 1.0F).endVertex();
        bufferBuilder.vertex(matrix4f3, -easedSize, 0.0F, easedSize).uv(0.0F, 1.0F).endVertex();

        BufferUploader.drawWithShader(bufferBuilder.end());

        poseStack.popPose();
    }
}
