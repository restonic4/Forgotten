package com.restonic4.forgotten.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.restonic4.forgotten.client.rendering.BeamEffectManager;
import com.restonic4.forgotten.client.rendering.EnergyOrbEffectManager;
import com.restonic4.forgotten.client.rendering.SkyWaveEffect;
import com.restonic4.forgotten.client.rendering.SkyWaveEffectManager;
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
    @Inject(method = "renderSky", at = @At("RETURN"))
    private void addSky(PoseStack poseStack, Matrix4f matrix4f, float f, Camera camera, boolean bl, Runnable runnable, CallbackInfo ci) {
        SkyWaveEffectManager.render(poseStack, matrix4f);
    }

    @Inject(method = "renderLevel", at = @At("TAIL"))
    private void renderManagers(PoseStack poseStack, float f, long l, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, CallbackInfo ci) {
        BeamEffectManager.render(poseStack, matrix4f, camera);
        EnergyOrbEffectManager.render(poseStack, matrix4f, camera);
    }
}
