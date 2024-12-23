package com.restonic4.forgotten.mixin.map_atlases;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pepjebs.mapatlases.client.ui.MapAtlasesHUD;

@Mixin(MapAtlasesHUD.class)
public class MapAtlasesHUDMixin {
    @Inject(method = "drawMapTextCoords", at = @At("HEAD"), cancellable = true)
    private static void drawMapTextCoords(GuiGraphics context, int x, int y, int originOffsetWidth, int originOffsetHeight, float textScaling, BlockPos blockPos, CallbackInfo ci) {
        ci.cancel();
    }
}
