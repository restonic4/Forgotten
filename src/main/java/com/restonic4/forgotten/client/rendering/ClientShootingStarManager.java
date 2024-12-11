package com.restonic4.forgotten.client.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.util.EasingSystem;
import com.restonic4.forgotten.util.helpers.RenderingHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class ClientShootingStarManager {
    public static final ResourceLocation STAR_LOCATION = new ResourceLocation(Forgotten.MOD_ID, "textures/environment/star.png");

    private static long spawnAnimationStart = 0;
    private static long spawnAnimationEnd = 0;

    private static float size = 30.0f;
    private static float rotation = 0.0f;
    private static float posX = 0.0f;
    private static float posY = 100.0f;
    private static float posZ = 0.0f;

    private static long fallAnimationStart = 0;
    private static long fallAnimationEnd = 0;
    private static BlockPos collisionPoint;

    public static void loadStarDataFromServer(long spawnAnimationStart, long spawnAnimationEnd, float size, float rotation, float x, float y, float z) {
        ClientShootingStarManager.spawnAnimationStart = spawnAnimationStart;
        ClientShootingStarManager.spawnAnimationEnd = spawnAnimationEnd;

        ClientShootingStarManager.size = size;
        ClientShootingStarManager.rotation = rotation;
        ClientShootingStarManager.posX = x;
        ClientShootingStarManager.posY = y;
        ClientShootingStarManager.posZ = z;

        ClientShootingStarManager.collisionPoint = null;
    }

    public static void loadShootingStar(long fallAnimationStart, long fallAnimationEnd, BlockPos collisionPoint) {
        ClientShootingStarManager.fallAnimationStart = fallAnimationStart;
        ClientShootingStarManager.fallAnimationEnd = fallAnimationEnd;

        ClientShootingStarManager.collisionPoint = collisionPoint;
    }

    public static void render(PoseStack poseStack, Matrix4f matrix4f, Camera camera, float rainAlpha) {
        if (collisionPoint == null) {
            renderStar(poseStack, matrix4f, camera, rainAlpha);
        } else {
            renderShootingStar(poseStack, matrix4f, camera, rainAlpha);
        }
    }

    public static void renderStar(PoseStack poseStack, Matrix4f matrix4f, Camera camera, float rainAlpha) {
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

    public static void renderShootingStar(PoseStack poseStack, Matrix4f matrix4f, Camera camera, float rainAlpha) {
        poseStack.pushPose();

        RenderSystem.setShaderColor(1, 0, 0, 1);
        RenderingHelper.renderComplexBeam(poseStack, matrix4f, camera, collisionPoint.getCenter().toVector3f(), 100, 100);
        RenderSystem.setShaderColor(1, 1, 1, 1);

        RenderSystem.setShaderColor(0, 1, 0, 1);
        RenderingHelper.renderComplexBeam(poseStack, matrix4f, camera, new Vector3f(), 100, 100);
        RenderSystem.setShaderColor(1, 1, 1, 1);

        RenderSystem.setShaderColor(0, 0, 1, 1);
        RenderingHelper.renderComplexTwoPointsBeam(poseStack, matrix4f, camera, new Vector3f(0, 2000, 0), collisionPoint.getCenter().toVector3f(), 10);
        RenderSystem.setShaderColor(1, 1, 1, 1);

        poseStack.popPose();
    }
}
