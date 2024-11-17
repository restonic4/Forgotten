package com.restonic4.forgotten.mixin;

import com.restonic4.forgotten.client.DeathUtils;
import com.restonic4.forgotten.networking.packets.DeathPacket;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public class LevelMixin {
    @Inject(method = "isThundering", at = @At("HEAD"), cancellable = true)
    public void isThundering(CallbackInfoReturnable<Boolean> cir) {
        Level current = (Level) (Object) this;
        if (current.isClientSide() && DeathUtils.isDeath()) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Inject(method = "getThunderLevel", at = @At("HEAD"), cancellable = true)
    public void getThunderLevel(float f, CallbackInfoReturnable<Float> cir) {
        Level current = (Level) (Object) this;
        if (current.isClientSide() && DeathUtils.isDeath()) {
            cir.setReturnValue(1f);
            cir.cancel();
        }
    }

    @Inject(method = "isRaining", at = @At("HEAD"), cancellable = true)
    public void isRaining(CallbackInfoReturnable<Boolean> cir) {
        Level current = (Level) (Object) this;
        if (current.isClientSide() && DeathUtils.isDeath()) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Inject(method = "getRainLevel", at = @At("HEAD"), cancellable = true)
    public void getRainLevel(float f, CallbackInfoReturnable<Float> cir) {
        Level current = (Level) (Object) this;
        if (current.isClientSide() && DeathUtils.isDeath()) {
            cir.setReturnValue(1f);
            cir.cancel();
        }
    }
}
