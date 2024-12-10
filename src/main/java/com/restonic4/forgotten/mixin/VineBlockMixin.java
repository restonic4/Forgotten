package com.restonic4.forgotten.mixin;

import com.restonic4.forgotten.util.GriefingPrevention;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VineBlock.class)
public class VineBlockMixin {
    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource, CallbackInfo ci) {
        if (GriefingPrevention.isInProtectedArea(blockPos)) {
            ci.cancel();
        }
    }

    @Inject(method = "canSpread", at = @At("HEAD"), cancellable = true)
    private void canSpread(BlockGetter blockGetter, BlockPos blockPos, CallbackInfoReturnable<Boolean> cir) {
        if (GriefingPrevention.isInProtectedArea(blockPos)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
