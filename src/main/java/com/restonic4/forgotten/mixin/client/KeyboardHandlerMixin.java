package com.restonic4.forgotten.mixin.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameType;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(
            method = "handleDebugKeys(I)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/KeyboardHandler;setClipboard(Ljava/lang/String;)V",
                    ordinal = 0
            ),
            cancellable = true
    )
    private void cancelCopyLocation(int i, CallbackInfoReturnable<Boolean> cir) {
        if (i == 67 && this.minecraft.player != null && this.minecraft.player.getPlayerInfo() != null) {
            GameType gameMode = this.minecraft.player.getPlayerInfo().getGameMode();
            if (gameMode == GameType.SURVIVAL || gameMode == GameType.ADVENTURE) {
                this.minecraft.gui.getChat().addMessage(Component.literal(ChatFormatting.GOLD + "You cannot copy your location in this game mode!"));
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }

    @Inject(method = "copyRecreateCommand", at = @At("HEAD"), cancellable = true)
    private void copyRecreateCommand(boolean bl, boolean bl2, CallbackInfo ci) {
        if (this.minecraft.player != null && this.minecraft.player.getPlayerInfo() != null && (this.minecraft.player.getPlayerInfo().getGameMode() == GameType.SURVIVAL || this.minecraft.player.getPlayerInfo().getGameMode() == GameType.ADVENTURE)) {
            ci.cancel();
        }
    }
}
