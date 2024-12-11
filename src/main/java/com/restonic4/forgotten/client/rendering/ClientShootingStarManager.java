package com.restonic4.forgotten.client.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.registries.common.ForgottenSounds;
import com.restonic4.forgotten.util.EasingSystem;
import com.restonic4.forgotten.util.helpers.RenderingHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import team.lodestar.lodestone.handlers.ScreenshakeHandler;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.screenshake.PositionedScreenshakeInstance;
import team.lodestar.lodestone.systems.screenshake.ScreenshakeInstance;

public class ClientShootingStarManager {
    public static final ResourceLocation STAR_LOCATION = new ResourceLocation(Forgotten.MOD_ID, "textures/environment/star.png");

    private static long spawnAnimationStart = 0;
    private static long spawnAnimationEnd = 0;

    private static float size = 0;
    private static float rotation = 0;
    private static float posX = 0;
    private static float posY = 100.0f;
    private static float posZ = 0;

    private static long fallAnimationStart = 0;
    private static long fallAnimationEnd = 0;
    private static BlockPos collisionPoint;
    private static boolean impacted = false;

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
        if (size == 0) {
            return;
        }

        ClientShootingStarManager.fallAnimationStart = fallAnimationStart;
        ClientShootingStarManager.fallAnimationEnd = fallAnimationEnd;

        ClientShootingStarManager.collisionPoint = collisionPoint;
        impacted = false;
    }

    public static void renderStar(PoseStack poseStack, Matrix4f matrix4f, Camera camera, float rainAlpha) {
        if (size == 0) {
            return;
        }

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
        if (collisionPoint == null) {
            return;
        }

        poseStack.pushPose();

        Vector3f collisionVec = collisionPoint.getCenter().toVector3f();

        size = 0;

        float easedStarX = EasingSystem.getEasedValue(fallAnimationStart, fallAnimationEnd, (float) (posX + camera.getPosition().x), collisionVec.x, EasingSystem.EasingType.EXPONENTIAL_IN);
        float easedStarY = EasingSystem.getEasedValue(fallAnimationStart, fallAnimationEnd, (float) (posY + camera.getPosition().y), collisionVec.y, EasingSystem.EasingType.EXPONENTIAL_IN);
        float easedStarZ = EasingSystem.getEasedValue(fallAnimationStart, fallAnimationEnd, (float) (posZ + camera.getPosition().z), collisionVec.z, EasingSystem.EasingType.EXPONENTIAL_IN);

        float easedCollisionX = EasingSystem.getEasedValue(fallAnimationStart, fallAnimationEnd - 1000, (float) (posX + camera.getPosition().x), collisionVec.x, EasingSystem.EasingType.EXPONENTIAL_IN);
        float easedCollisionY = EasingSystem.getEasedValue(fallAnimationStart, fallAnimationEnd - 1000, (float) (posY + camera.getPosition().y), collisionVec.y, EasingSystem.EasingType.EXPONENTIAL_IN);
        float easedCollisionZ = EasingSystem.getEasedValue(fallAnimationStart, fallAnimationEnd - 1000, (float) (posZ + camera.getPosition().z), collisionVec.z, EasingSystem.EasingType.EXPONENTIAL_IN);

        float easedWidth = EasingSystem.getEasedValue(fallAnimationStart, fallAnimationEnd, 2, 4, EasingSystem.EasingType.EXPONENTIAL_IN);

        float easedImpactRadius = EasingSystem.getEasedValue(fallAnimationEnd - 1000, fallAnimationEnd - 500, 0, 10, EasingSystem.EasingType.CIRC_IN);
        float easedImpactAlpha = EasingSystem.getEasedValue(fallAnimationEnd - 1000, fallAnimationEnd - 500, 1, 0, EasingSystem.EasingType.CIRC_IN);

        RenderSystem.setShaderColor(0.988f, 0.996f, 0.996f, 1);
        RenderingHelper.renderBillboardQuad(poseStack, matrix4f, camera, new Vector3f(easedStarX, easedStarY, easedStarZ), new Vector3f(easedCollisionX, easedCollisionY, easedCollisionZ), easedWidth);

        RenderSystem.setShaderColor(0.988f, 0.996f, 0.996f, easedImpactAlpha);
        RenderingHelper.renderSphere(poseStack, matrix4f, camera, collisionVec, easedImpactRadius);

        RenderSystem.setShaderColor(1, 1, 1, 1);

        if (easedImpactAlpha > 0 && !impacted) {
            impacted = true;

            ScreenshakeInstance orbShake = new ScreenshakeInstance(2 * 20).setEasing(Easing.CUBIC_IN, Easing.QUAD_IN_OUT).setIntensity(0.5f, 0);
            ScreenshakeHandler.addScreenshake(orbShake);

            Minecraft.getInstance().execute(() -> {
                Minecraft.getInstance().level.playLocalSound(collisionVec.x, collisionVec.y, collisionVec.z, ForgottenSounds.EXPLOSION, SoundSource.AMBIENT, 1, 1, false);
            });
        }

        poseStack.popPose();
    }
}
