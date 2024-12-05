package com.restonic4.forgotten.mixin.client;

import com.restonic4.forgotten.registries.common.ForgottenSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    @Unique
    private static boolean hasPlayedMusic = false;

    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(CallbackInfo ci) {
        if (!hasPlayedMusic) {
            hasPlayedMusic = true;
            playCustomMusic();
        }
    }

    @Unique
    private void playCustomMusic() {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) {
            SoundEvent customMusic = ForgottenSounds.ELD_UNKNOWN;
            client.getSoundManager().play(SimpleSoundInstance.forUI(customMusic, 1f, 0.5f));
        }
    }
}
