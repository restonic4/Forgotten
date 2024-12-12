package com.restonic4.forgotten.mixin.client;

import com.mojang.blaze3d.vertex.*;
import com.restonic4.forgotten.client.rendering.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Shadow private @Nullable ClientLevel level;

    @Shadow public abstract int countRenderedChunks();

    @Inject(method = "renderSky", at = @At("RETURN"))
    private void addSky(PoseStack poseStack, Matrix4f matrix4f, float f, Camera camera, boolean bl, Runnable runnable, CallbackInfo ci) {
        SkyWaveEffectManager.render(poseStack, matrix4f);
    }

    @Inject(
            method = "renderSky",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V",
                    ordinal = 2,
                    shift = At.Shift.AFTER
            )
    )
    private void injectBeforeSunRendering(PoseStack poseStack, Matrix4f matrix4f, float f, Camera camera, boolean bl, Runnable runnable, CallbackInfo ci) {
        float alpha = 1.0F - this.level.getRainLevel(f);
        ClientShootingStarManager.renderStar(poseStack, matrix4f, camera, alpha);
    }

    @Inject(method = "renderLevel", at = @At("TAIL"))
    private void renderManagers(PoseStack poseStack, float f, long l, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, CallbackInfo ci) {
        float alpha = 1.0F - this.level.getRainLevel(f);

        ClientShootingStarManager.renderShootingStar(poseStack, matrix4f, camera, alpha);
        BeamEffectManager.render(poseStack, matrix4f, camera);
        EnergyOrbEffectManager.render(poseStack, matrix4f, camera);
        ClientShootingStarManager.renderEtherealFragmentEffects(poseStack, matrix4f, camera);
    }
}
