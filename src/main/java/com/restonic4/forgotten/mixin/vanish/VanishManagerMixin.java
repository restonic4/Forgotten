package com.restonic4.forgotten.mixin.vanish;

import me.drex.vanish.util.VanishManager;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VanishManager.class)
public class VanishManagerMixin {
    @Inject(method = "broadcastToOthers", at = @At("HEAD"), cancellable = true)
    private static void broadcastToOthers(ServerPlayer actor, Packet<?> packet, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "canViewVanished", at = @At("HEAD"), cancellable = true)
    private static void canViewVanished(SharedSuggestionProvider observer, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
        cir.cancel();
    }
}
