package com.restonic4.forgotten.mixin;

import com.restonic4.forgotten.client.DeathUtils;
import com.restonic4.forgotten.util.GriefingPrevention;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public abstract class LevelMixin {
    @Shadow public abstract boolean setBlock(BlockPos blockPos, BlockState blockState, int i, int j);

    @Shadow public abstract BlockState getBlockState(BlockPos blockPos);

    @Shadow public abstract boolean isClientSide();

    @Shadow public abstract boolean setBlockAndUpdate(BlockPos blockPos, BlockState blockState);

    @Shadow public abstract void sendBlockUpdated(BlockPos blockPos, BlockState blockState, BlockState blockState2, int i);

    @Inject(method = "isThundering", at = @At("HEAD"), cancellable = true)
    public void isThundering(CallbackInfoReturnable<Boolean> cir) {
        if (this.isClientSide() && DeathUtils.isDeath()) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Inject(method = "getThunderLevel", at = @At("HEAD"), cancellable = true)
    public void getThunderLevel(float f, CallbackInfoReturnable<Float> cir) {
        if (this.isClientSide() && DeathUtils.isDeath()) {
            cir.setReturnValue(1f);
            cir.cancel();
        }
    }

    @Inject(method = "isRaining", at = @At("HEAD"), cancellable = true)
    public void isRaining(CallbackInfoReturnable<Boolean> cir) {
        if (this.isClientSide() && DeathUtils.isDeath()) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Inject(method = "getRainLevel", at = @At("HEAD"), cancellable = true)
    public void getRainLevel(float f, CallbackInfoReturnable<Float> cir) {
        if (this.isClientSide() && DeathUtils.isDeath()) {
            cir.setReturnValue(1f);
            cir.cancel();
        }
    }

    @Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z", at = @At("HEAD"), cancellable = true)
    public void setBlock(BlockPos blockPos, BlockState blockState, int i, int j, CallbackInfoReturnable<Boolean> cir) {
        Level level = (Level) (Object) this;

        if (level instanceof ServerLevel serverLevel) {
            if (GriefingPrevention.isInProtectedArea(blockPos)) {
                BlockState originalBlockState = GriefingPrevention.getOriginalBlockAndRegister(blockPos, this.getBlockState(blockPos));

                if (blockState != originalBlockState) {
                    GriefingPrevention.onBlockModifiedInMainTemple(serverLevel, blockState, originalBlockState, blockPos);
                }
            }
        }
    }


}
