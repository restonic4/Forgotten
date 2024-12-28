package com.restonic4.forgotten.mixin;

import com.restonic4.forgotten.entity.common.CoreEntity;
import com.restonic4.forgotten.entity.common.SmallCoreEntity;
import net.minecraft.world.entity.animal.Squid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Squid.class)
public class SquidMixin {
    @Inject(method = "spawnInk", at = @At("HEAD"), cancellable = true)
    private void spawnInk(CallbackInfo ci) {
        Squid current = (Squid) (Object) this;
        if (current instanceof SmallCoreEntity) {
            ci.cancel();
        }
    }
}
