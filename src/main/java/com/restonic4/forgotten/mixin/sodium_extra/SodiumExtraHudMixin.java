package com.restonic4.forgotten.mixin.sodium_extra;

import me.flashyreese.mods.sodiumextra.client.gui.SodiumExtraHud;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SodiumExtraHud.class)
public class SodiumExtraHudMixin {
    @Redirect(
            method = "onStartTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;showOnlyReducedInfo()Z"
            )
    )
    private boolean redirectReducedInfo(Minecraft instance) {
        return true;
    }
}
