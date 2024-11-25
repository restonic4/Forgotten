package com.restonic4.forgotten.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.restonic4.forgotten.client.RenderingHelper;
import com.restonic4.forgotten.registries.client.CustomRenderTypes;
import com.restonic4.forgotten.util.CircleGenerator;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.List;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Shadow private VertexBuffer skyBuffer;
    @Shadow private ClientLevel level;
    @Final @Shadow private Minecraft minecraft;
    @Shadow private VertexBuffer starBuffer;
    @Shadow private VertexBuffer darkBuffer;
    @Shadow  private static BufferBuilder.RenderedBuffer buildSkyDisc(BufferBuilder bufferBuilder, float f) {
        return null;
    };


    @Unique private VertexBuffer waveBuffer;

    @Inject(method = "renderSky", at = @At("RETURN"))
    private void addSky(PoseStack poseStack, Matrix4f matrix4f, float f, Camera camera, boolean bl, Runnable runnable, CallbackInfo ci) {
        Vec3 vec3 = this.level.getSkyColor(this.minecraft.gameRenderer.getMainCamera().getPosition(), 18000);

        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();

        RenderSystem.depthMask(false);
        RenderSystem.setShaderColor((float)vec3.x, (float)vec3.y, (float)vec3.z, 1.0F);

        ShaderInstance shaderInstance = CustomRenderTypes.WAVE_SHADER.getInstance().get();
        updateSkyShaderData(shaderInstance);

        RenderSystem.enableBlend();

        if (this.waveBuffer == null) {
            this.waveBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
            BufferBuilder.RenderedBuffer renderedBuffer = buildSkyDisc(bufferBuilder, 16.0F);
            this.waveBuffer.bind();
            this.waveBuffer.upload(renderedBuffer);
            VertexBuffer.unbind();
        }

        this.waveBuffer.bind();
        this.waveBuffer.drawWithShader(poseStack.last().pose(), matrix4f, shaderInstance);

        VertexBuffer.unbind();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.depthMask(true);
    }

    @Unique
    private void updateSkyShaderData(ShaderInstance shader) {
        if (this.minecraft.player == null || this.level == null) {
            return;
        }

        Vec3 pos = this.minecraft.player.position();
        float[] uniformPos = new float[3];

        uniformPos[0] = (float) pos.x;
        uniformPos[1] = (float) pos.y;
        uniformPos[2] = (float) pos.z;

        shader.safeGetUniform("PlayerPos").set(uniformPos);
    }

    @Inject(method = "renderLevel", at = @At("TAIL"))
    private void renderBeams(PoseStack poseStack, float f, long l, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, CallbackInfo ci) {
        List<CircleGenerator.CirclePoint> circle = CircleGenerator.generateCircle(100, 30);

        for (int i = 0; i < circle.size(); i++) {
            CircleGenerator.CirclePoint point = circle.get(i);

            RenderingHelper.renderBeam(poseStack, camera, new Vec3(0, 0, 0), point.position, 5);
        }

        //RenderingHelper.renderBeam(poseStack, camera, new Vec3(0, 0, 0), new Vec3(0, 100, 0), 20);
    }
}
