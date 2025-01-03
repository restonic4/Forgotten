package com.restonic4.forgotten.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.restonic4.forgotten.client.DeathUtils;
import com.restonic4.forgotten.client.ForgottenClient;
import com.restonic4.forgotten.util.EasingSystem;
import com.restonic4.forgotten.util.trash.TestingVars;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow @Final public Minecraft minecraft;
    @Unique
    private static long easingStartTime = 0;
    @Unique private static long easingEndTime = 0;

    @Inject(method = "render", at = @At("HEAD"))
    private void render(float f, long l, boolean bl, CallbackInfo ci) {
        ForgottenClient.currentTime = System.currentTimeMillis();
    }

    @Inject(method = "getFov", at = @At("HEAD"), cancellable = true)
    private void getFov(Camera camera, float f, boolean bl, CallbackInfoReturnable<Double> cir) {
        if (DeathUtils.isDeath()) {
            if (DeathUtils.shouldResetFovAnimations()) {
                DeathUtils.fovAnimationsRestarted();

                easingStartTime = System.currentTimeMillis();
                easingEndTime = System.currentTimeMillis() + 2000;
            }

            double easedFov = EasingSystem.getEasedValue(easingStartTime, easingEndTime, 10, this.minecraft.options.fov().get(), EasingSystem.EasingType.QUAD_IN_OUT);

            cir.setReturnValue(easedFov);
            cir.cancel();
        }
    }

    @Inject(method = "getDepthFar", at = @At("HEAD"), cancellable = true)
    public void getDepthFar(CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(TestingVars.FAR_PLANE);
        cir.cancel();
    }
}
