package com.restonic4.forgotten.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.restonic4.forgotten.registries.client.ForgottenShaderHolders;
import com.restonic4.forgotten.util.helpers.RenderingHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
    @Unique private VertexBuffer quadBuffer;

    @Inject(method = "renderSky", at = @At("RETURN"))
    private void addSky(PoseStack poseStack, Matrix4f matrix4f, float f, Camera camera, boolean bl, Runnable runnable, CallbackInfo ci) {
        Vec3 vec3 = this.level.getSkyColor(this.minecraft.gameRenderer.getMainCamera().getPosition(), 18000);

        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();

        RenderSystem.depthMask(false);
        RenderSystem.setShaderColor((float)vec3.x, (float)vec3.y, (float)vec3.z, 1.0F);

        ShaderInstance shaderInstance = ForgottenShaderHolders.SKY_WAVE.getInstance().get();
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
        for (int i = 0; i < 5; i++) {
            RenderingHelper.renderComplexBeam(poseStack, matrix4f, camera, new Vector3f(), 1 + i, 500);
        }
    }
}
