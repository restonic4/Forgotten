package com.restonic4.forgotten.mixin.immersive_weathering;

import com.ordana.immersive_weathering.blocks.cracked.Crackable;
import com.ordana.immersive_weathering.configs.CommonConfigs;
import com.restonic4.forgotten.util.GriefingPrevention;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Crackable.class)
public class CrackableMixin {
    @Inject(method = "shouldWeather", at = @At("HEAD"), cancellable = true)
    void shouldWeather(BlockState state, BlockPos pos, Level level, CallbackInfoReturnable<Boolean> cir) {
        if (level instanceof ServerLevel serverLevel && GriefingPrevention.isInProtectedArea(serverLevel, pos)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
