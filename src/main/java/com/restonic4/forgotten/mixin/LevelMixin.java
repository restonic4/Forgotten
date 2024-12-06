package com.restonic4.forgotten.mixin;

import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.client.DeathUtils;
import com.restonic4.forgotten.saving.JsonDataManager;
import com.restonic4.forgotten.util.GriefingPrevention;
import com.restonic4.forgotten.util.ServerCache;
import com.restonic4.forgotten.util.helpers.RandomUtil;
import com.restonic4.forgotten.util.helpers.SimpleEffectHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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

    @Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z", at = @At("HEAD"), cancellable = true)
    public void setBlock(BlockPos blockPos, BlockState blockState, int i, int j, CallbackInfoReturnable<Boolean> cir) {
        JsonDataManager dataManager = Forgotten.getDataManager();
        BlockPos pos = dataManager.getBlockPos("center");

        if (pos != null && !this.isClientSide()) {
            ServerLevel serverLevel = (ServerLevel) (Level) (Object) this;

            Vec3 blockPosVec = blockPos.getCenter();
            double distance = blockPosVec.distanceTo(pos.getCenter());

            if (distance <= 120) {
                BlockState originalBlockState = GriefingPrevention.getOriginalBlockAndRegister(blockPos, this.getBlockState(blockPos));

                if (blockState != originalBlockState) {
                    new Thread(() -> {
                        try {
                            Thread.sleep(RandomUtil.randomBetween(2000, 8000));
                        } catch (Exception ignored) {}

                        if (serverLevel != null) {
                            serverLevel.getServer().execute(() -> {
                                SimpleEffectHelper.invalidHeadPlacement(serverLevel, blockPos);
                                serverLevel.setBlockAndUpdate(blockPos, originalBlockState);
                            });
                        }
                    }).start();
                }
            }
        }
    }
}
