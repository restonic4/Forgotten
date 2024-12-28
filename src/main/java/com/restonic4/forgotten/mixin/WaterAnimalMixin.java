package com.restonic4.forgotten.mixin;

import com.restonic4.forgotten.entity.common.CoreEntity;
import com.restonic4.forgotten.entity.common.SmallCoreEntity;
import net.minecraft.world.entity.animal.WaterAnimal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WaterAnimal.class)
public class WaterAnimalMixin {
    @Inject(method = "handleAirSupply", at = @At("HEAD"), cancellable = true)
    protected void handleAirSupply(int i, CallbackInfo ci) {
        WaterAnimal current = (WaterAnimal) (Object) this;
        if (current instanceof SmallCoreEntity) {
            ci.cancel();
        }
    }
}
