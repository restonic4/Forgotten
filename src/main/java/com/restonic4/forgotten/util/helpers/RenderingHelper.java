package com.restonic4.forgotten.util.helpers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.restonic4.forgotten.registries.client.ForgottenRenderTypeTokens;
import com.restonic4.forgotten.registries.client.ForgottenShaderHolders;
import com.restonic4.forgotten.registries.client.custom.RenderShapes;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
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

    public static BufferBuilder.RenderedBuffer buildGeometry(BufferBuilder bufferBuilder, Vector3f[] positions) {
        RenderSystem.setShader(GameRenderer::getPositionShader);

        bufferBuilder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);

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
            RenderingHelper.renderDynamicGeometry(poseStack, matrix4f, camera, vector3fs);
        }
    }
}
