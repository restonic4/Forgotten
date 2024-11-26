package com.restonic4.forgotten.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.restonic4.forgotten.registries.client.CustomRenderTypes;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
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

    public static void renderDynamicGeometry(PoseStack poseStack, Matrix4f matrix4f, Camera camera, Vector3f[] vertices) {
        BufferBuilder.RenderedBuffer renderedBuffer = RenderingHelper.buildGeometry(Tesselator.getInstance().getBuilder(), vertices);
        renderQuad(generateBuffer(renderedBuffer), poseStack, matrix4f, camera);
    }

    public static void renderQuad(VertexBuffer vertexBuffer, PoseStack poseStack, Matrix4f matrix4f, Camera camera) {
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();

        poseStack.pushPose();

        poseStack.translate(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z);

        vertexBuffer.bind();
        vertexBuffer.drawWithShader(poseStack.last().pose(), matrix4f, CustomRenderTypes.QUAD_SHADER.getInstance().get());

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

    public static BufferBuilder.RenderedBuffer buildGeometry(BufferBuilder bufferBuilder, Vector3f[] positions) {
        RenderSystem.setShader(GameRenderer::getPositionShader);

        bufferBuilder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);

        for (int i = 0; i < positions.length; i++) {
            bufferBuilder.vertex(positions[i].x, positions[i].y, positions[i].z).endVertex();
        }

        return bufferBuilder.end();
    }

    public static Vector3f[] getQuadVertices() {
        return new Vector3f[] {
                new Vector3f(0, 0, 0),
                new Vector3f(1, 0, 0),
                new Vector3f(1, 0, 1),
                new Vector3f(0, 0, 1)
        };
    }

    public static void scaleVertices(Vector3f[] vertices, float scaleX, float scaleY, float scaleZ) {
        for (Vector3f vertex : vertices) {
            vertex.mul(scaleX, scaleY, scaleZ);
        }
    }

    public static void translateVertices(Vector3f[] vertices, float translateX, float translateY, float translateZ) {
        for (Vector3f vertex : vertices) {
            vertex.add(translateX, translateY, translateZ);
        }
    }

    public static void rotateVerticesX(Vector3f[] vertices, float angleDegrees) {
        float angleRadians = (float) Math.toRadians(angleDegrees);
        for (Vector3f vertex : vertices) {
            float y = vertex.y;
            float z = vertex.z;
            vertex.y = y * (float) Math.cos(angleRadians) - z * (float) Math.sin(angleRadians);
            vertex.z = y * (float) Math.sin(angleRadians) + z * (float) Math.cos(angleRadians);
        }
    }

    public static void rotateVerticesY(Vector3f[] vertices, float angleDegrees) {
        float angleRadians = (float) Math.toRadians(angleDegrees);
        for (Vector3f vertex : vertices) {
            float x = vertex.x;
            float z = vertex.z;
            vertex.x = x * (float) Math.cos(angleRadians) + z * (float) Math.sin(angleRadians);
            vertex.z = -x * (float) Math.sin(angleRadians) + z * (float) Math.cos(angleRadians);
        }
    }

    public static void rotateVerticesZ(Vector3f[] vertices, float angleDegrees) {
        float angleRadians = (float) Math.toRadians(angleDegrees);
        for (Vector3f vertex : vertices) {
            float x = vertex.x;
            float y = vertex.y;
            vertex.x = x * (float) Math.cos(angleRadians) - y * (float) Math.sin(angleRadians);
            vertex.y = x * (float) Math.sin(angleRadians) + y * (float) Math.cos(angleRadians);
        }
    }
}
