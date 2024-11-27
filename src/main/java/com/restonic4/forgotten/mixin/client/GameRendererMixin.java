package com.restonic4.forgotten.mixin.client;

import com.restonic4.forgotten.client.CachedClientData;
import com.restonic4.forgotten.client.DeathUtils;
import com.restonic4.forgotten.util.EasingSystem;
import com.restonic4.forgotten.util.trash.TestingVars;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Unique
    private static long easingStartTime = 0;
    @Unique private static long easingEndTime = 0;

    @Inject(method = "getFov", at = @At("HEAD"), cancellable = true)
    private void getFov(Camera camera, float f, boolean bl, CallbackInfoReturnable<Double> cir) {
        if (DeathUtils.isDeath()) {
            GameRenderer current = (GameRenderer) (Object) this;

            if (DeathUtils.shouldResetFovAnimations()) {
                DeathUtils.fovAnimationsRestarted();

                easingStartTime = System.currentTimeMillis();
                easingEndTime = System.currentTimeMillis() + 2000;
            }

            double easedFov = EasingSystem.getEasedValue(easingStartTime, easingEndTime, 10, current.minecraft.options.fov().get(), EasingSystem.EasingType.QUAD_IN_OUT);

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
