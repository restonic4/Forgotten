package com.restonic4.forgotten.util.helpers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import com.restonic4.forgotten.registries.client.ForgottenRenderTypeTokens;
import com.restonic4.forgotten.registries.client.ForgottenShaderHolders;
import com.restonic4.forgotten.registries.client.custom.RenderShapes;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import team.lodestar.lodestone.handlers.RenderHandler;
import team.lodestar.lodestone.registry.client.LodestoneRenderTypeRegistry;
import team.lodestar.lodestone.systems.rendering.VFXBuilders;

import java.awt.*;
import java.util.List;

public class RenderingHelper {
    public static void renderBeamFromEntity(PoseStack poseStack, Vec3 startPos, Vec3 endPos, float width) {
        renderBeamFromEntity(
                poseStack,
                LodestoneRenderTypeRegistry.ADDITIVE_TEXTURE.applyAndCache(ForgottenRenderTypeTokens.BEAM),
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
                LodestoneRenderTypeRegistry.ADDITIVE_TEXTURE.applyAndCache(ForgottenRenderTypeTokens.BEAM),
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

    public static void renderDynamicGeometry(PoseStack poseStack, Matrix4f matrix4f, Camera camera, VertexFormat.Mode mode, Vector3f[] vertices) {
        BufferBuilder.RenderedBuffer renderedBuffer = RenderingHelper.buildGeometry(Tesselator.getInstance().getBuilder(), mode, vertices);
        renderQuad(generateBuffer(renderedBuffer), poseStack, matrix4f, camera);
    }

    public static void renderQuad(VertexBuffer vertexBuffer, PoseStack poseStack, Matrix4f matrix4f, Camera camera) {
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();

        poseStack.pushPose();

        poseStack.translate(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z);

        vertexBuffer.bind();
        vertexBuffer.drawWithShader(poseStack.last().pose(), matrix4f, ForgottenShaderHolders.SIMPLE_COLOR.getInstance().get());

        VertexBuffer.unbind();

        poseStack.popPose();

        RenderSystem.depthMask(true);
    }

    public static VertexBuffer generateBuffer(BufferBuilder.RenderedBuffer renderedBuffer) {
        VertexBuffer buffer = new VertexBuffer(VertexBuffer.Usage.STATIC);

        buffer.bind();
        buffer.upload(renderedBuffer);
        VertexBuffer.unbind();

        return buffer;
    }

    public static BufferBuilder.RenderedBuffer buildGeometry(BufferBuilder bufferBuilder, VertexFormat.Mode mode, Vector3f[] positions) {
        RenderSystem.setShader(GameRenderer::getPositionShader);

        bufferBuilder.begin(mode, DefaultVertexFormat.POSITION);

        for (int i = 0; i < positions.length; i++) {
            bufferBuilder.vertex(positions[i].x, positions[i].y, positions[i].z).endVertex();
        }

        return bufferBuilder.end();
    }



    public static void renderComplexBeam(PoseStack poseStack, Matrix4f matrix4f, Camera camera, Vector3f position, float width, float height) {
        List<Vector3f[]> vector3fList = RenderShapes.BEAM.getVertices();

        for (int i = 0; i < vector3fList.size(); i++) {
            Vector3f[] vector3fs = vector3fList.get(i);

            MathHelper.scaleVertices(vector3fs, width, height, width);
            MathHelper.translateVertices(vector3fs, position.x - width/2, position.y, position.z - width/2);
            RenderingHelper.renderDynamicGeometry(poseStack, matrix4f, camera, VertexFormat.Mode.TRIANGLE_FAN, vector3fs);
        }
    }

    public static Vector3f cachedPoint = new Vector3f();
    public static void renderDebugBeam(PoseStack poseStack, Matrix4f matrix4f, Camera camera, Vector3f start, Vector3f end, int numPoints) {
        for (int i = 0; i <= numPoints; i++) {
            float t = (float) i / (float) numPoints;

            cachedPoint.set(0, 0, 0);
            start.lerp(end, t, cachedPoint);

            renderPoint(poseStack, matrix4f, camera, cachedPoint);
        }
    }

    private static void renderPoint(PoseStack poseStack, Matrix4f matrix4f, Camera camera, Vector3f point) {
        RenderSystem.setShaderColor(0, 0, 1, 1);
        RenderingHelper.renderSphere(poseStack, matrix4f, camera, point, 10);
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    public static void renderBillboardQuad(PoseStack poseStack, Matrix4f matrix4f, Camera camera, Vector3f pointA, Vector3f pointB, float width) {
        Vector3f delta = new Vector3f(pointB).sub(pointA);

        Vector3f cameraDirection = new Vector3f(camera.getPosition().toVector3f()).sub(pointA).normalize();

        Vector3f normal = new Vector3f(cameraDirection).cross(delta).normalize().mul(width / 2f, width / 2f, width / 2f);

        Vector3f topLeft = new Vector3f(pointA).sub(normal);
        Vector3f topRight = new Vector3f(pointA).add(normal);
        Vector3f bottomLeft = new Vector3f(pointB).sub(normal);
        Vector3f bottomRight = new Vector3f(pointB).add(normal);

        Vector3f[] vertices = new Vector3f[] { topLeft, bottomLeft, bottomRight, topRight };

        BufferBuilder.RenderedBuffer renderedBuffer = RenderingHelper.buildGeometry(Tesselator.getInstance().getBuilder(), VertexFormat.Mode.QUADS, vertices);

        renderQuad(generateBuffer(renderedBuffer), poseStack, matrix4f, camera);
    }

    public static void renderSphere(PoseStack poseStack, Matrix4f matrix4f, Camera camera, Vector3f position, float radius) {
        List<Vector3f[]> vector3fList = RenderShapes.SPHERE.getVertices();

        for (int i = 0; i < vector3fList.size(); i++) {
            Vector3f[] vector3fs = vector3fList.get(i);

            MathHelper.scaleVertices(vector3fs, radius, radius, radius);
            MathHelper.translateVertices(vector3fs, position.x, position.y, position.z);
            RenderingHelper.renderDynamicGeometry(poseStack, matrix4f, camera, VertexFormat.Mode.TRIANGLES, vector3fs);
        }
    }
}
