package com.restonic4.forgotten.mixin;

import com.restonic4.forgotten.client.DeathUtils;
import com.restonic4.forgotten.networking.packets.DeathPacket;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Biome.class)
public class BiomeMixin {
    @Inject(method = "hasPrecipitation", at = @At("HEAD"), cancellable = true)
    public void getPrecipitationAt(CallbackInfoReturnable<Boolean> cir) {
        Biome current = (Biome) (Object) this;

        if (DeathUtils.isDeath()) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
