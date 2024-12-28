package com.restonic4.forgotten.mixin.client;

import com.restonic4.forgotten.client.CachedClientData;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow protected abstract void setPosition(double d, double e, double f);

    @Shadow public abstract void setRotation(float f, float g);

    @Shadow private boolean initialized;

    @Shadow private BlockGetter level;

    @Shadow private Entity entity;

    @Shadow private boolean detached;

    @Inject(method = "setup", at = @At("HEAD"), cancellable = true)
    public void setup(BlockGetter blockGetter, Entity entity, boolean bl, boolean bl2, float f, CallbackInfo ci) {
        long currentTime = System.currentTimeMillis();
        if (currentTime >= CachedClientData.chainsCutsceneStartTime && currentTime <= CachedClientData.chainsCutsceneEndTime) {
            this.initialized = true;
            this.level = blockGetter;
            this.entity = entity;
            this.detached = bl;
            this.setPosition(-492.316f, 162.34044f, -364.384);
            this.setRotation(134.8f, 42f);

            ci.cancel();
        }
    }
}
