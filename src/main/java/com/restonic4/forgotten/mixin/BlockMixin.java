package com.restonic4.forgotten.mixin;

import com.restonic4.forgotten.util.GriefingPrevention;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(method = "popResource(Lnet/minecraft/world/level/Level;Ljava/util/function/Supplier;Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"), cancellable = true)
    private static void popResource(Level level, Supplier<ItemEntity> supplier, ItemStack itemStack, CallbackInfo ci) {
        if (!level.isClientSide && !itemStack.isEmpty()) {
            if (GriefingPrevention.isInProtectedArea((ServerLevel) level, supplier.get().blockPosition())) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "popExperience", at = @At("HEAD"), cancellable = true)
    protected void popExperience(ServerLevel serverLevel, BlockPos blockPos, int i, CallbackInfo ci) {
        if (GriefingPrevention.isInProtectedArea(serverLevel, blockPos)) {
            ci.cancel();
        }
    }
}
