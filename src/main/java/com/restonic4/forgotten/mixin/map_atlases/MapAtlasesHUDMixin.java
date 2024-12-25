package com.restonic4.forgotten.mixin.map_atlases;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pepjebs.mapatlases.client.ui.MapAtlasesHUD;

@Mixin(MapAtlasesHUD.class)
public abstract class MapAtlasesHUDMixin {
    @Shadow
    public static void drawMapTextBiome(GuiGraphics context, int x, int y, int originOffsetWidth, int originOffsetHeight, float textScaling, BlockPos blockPos, Level world) {
    }

    @Inject(method = "drawMapTextCoords", at = @At("HEAD"), cancellable = true)
    private static void drawMapTextCoords(GuiGraphics context, int x, int y, int originOffsetWidth, int originOffsetHeight, float textScaling, BlockPos blockPos, CallbackInfo ci) {
        ci.cancel();
    }

    @Redirect(
            method = "renderMapHUD",
            at = @At(
                    value = "INVOKE",
                    target = "Lpepjebs/mapatlases/client/ui/MapAtlasesHUD;drawMapTextBiome(Lnet/minecraft/client/gui/GuiGraphics;IIIIFLnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/Level;)V"
            )
    )
    private void redirectDrawMapTextBiome(GuiGraphics context, int x, int y, int originOffsetWidth, int originOffsetHeight, float textScaling, BlockPos blockPos, Level level) {
        int modifiedTextHeightOffset = (int) (originOffsetHeight - 12.0F * textScaling);
        drawMapTextBiome(context, x, y, originOffsetWidth, modifiedTextHeightOffset, textScaling, blockPos, level);
    }
}
