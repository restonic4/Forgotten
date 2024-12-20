package com.restonic4.forgotten.mixin.supplementaries;

import net.mehvahdjukaar.supplementaries.common.block.blocks.GlobeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GlobeBlock.class)
public class GlobeBlockMixin {
    @Inject(method = "displayCurrentCoordinates", at = @At("HEAD"), cancellable = true)
    private static void displayCurrentCoordinates(Level level, Player player, BlockPos pos, CallbackInfo ci) {
        player.displayClientMessage(Component.translatable("compact.forgotten.block.supplementaries.globe.message"), true);
        ci.cancel();
    }
}
