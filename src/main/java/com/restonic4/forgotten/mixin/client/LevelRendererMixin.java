package com.restonic4.forgotten.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.restonic4.forgotten.registries.client.CustomRenderTypes;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.logging.Level;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    /*@Redirect(method = "renderSky",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;getShader()Lnet/minecraft/client/renderer/ShaderInstance;"))
    private ShaderInstance modifyShaderInstance() {
        ShaderInstance customShader = CustomRenderTypes.SKY_SHADER.getInstance().get();

        if (customShader == null) {
            return RenderSystem.getShader();
        }

        return customShader;
    }*/

    @Shadow private VertexBuffer skyBuffer;
    @Shadow private ClientLevel level;
    @Final @Shadow private Minecraft minecraft;

    @Inject(method = "renderSky", at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/VertexBuffer;unbind()V",
            shift = At.Shift.AFTER,
            ordinal = 0
    ))
    private void addSky(PoseStack poseStack, Matrix4f matrix4f, float f, Camera camera, boolean bl, Runnable runnable, CallbackInfo ci) {
        Vec3 vec3 = this.level.getSkyColor(this.minecraft.gameRenderer.getMainCamera().getPosition(), 18000);

        float r = (float)vec3.x;
        float g = (float)vec3.y;
        float b = (float)vec3.z;

        FogRenderer.levelFogColor();
        RenderSystem.depthMask(false);
        RenderSystem.setShaderColor(r, g, b, 1.0F);

        ShaderInstance shaderInstance = CustomRenderTypes.SKY_SHADER.getInstance().get();
        updateSkyShaderData(shaderInstance);

        RenderSystem.enableBlend();

        this.skyBuffer.bind();
        this.skyBuffer.drawWithShader(poseStack.last().pose(), matrix4f, shaderInstance);

        VertexBuffer.unbind();
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


}
