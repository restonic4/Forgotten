package com.restonic4.forgotten.mixin.map_atlases;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import pepjebs.mapatlases.screen.MapAtlasesAtlasOverviewScreen;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mixin(MapAtlasesAtlasOverviewScreen.class)
public abstract class MapAtlasesAtlasOverviewScreenMixin {
    @Shadow private double rawMouseXMoved;

    @Shadow private double rawMouseYMoved;

    @Shadow protected abstract List<Map.Entry<String, MapDecoration>> getMapIconList();

    @Inject(method = "drawMapTextXZCoords", at = @At("HEAD"), cancellable = true)
    private static void drawMapTextXZCoords(GuiGraphics context, int x, int y, int originOffsetWidth, int originOffsetHeight, float textScaling, BlockPos blockPos, CallbackInfo ci) {
        ci.cancel();
    }

    @Unique
    private Component cachedMapIconText;

    @Inject(
            method = "drawMapIconTooltip",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;renderComponentTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;II)V",
                    ordinal = 0
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void captureMapIconText(GuiGraphics context, double x, double y, int atlasBgScaledSize, CallbackInfo ci, int scaledWidth, List mapList, int k, int targetIdx, Map.Entry entry, String stateIdStr, int stateId, Pair dimAndCenters, MapItemSavedData mapState, MapDecoration mapIcon, Component mapIconText, LiteralContents coordsText, MutableComponent formattedCoords) {
        this.cachedMapIconText = mapIconText;
    }

    @Redirect(
            method = "drawMapIconTooltip",
            at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/gui/GuiGraphics;renderComponentTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;II)V"
            )
    )
    private void drawMapIconTooltip(GuiGraphics instance, Font font, List<Component> list, int i, int j) {
        instance.renderComponentTooltip(Minecraft.getInstance().font, List.of(cachedMapIconText), (int)this.rawMouseXMoved, (int)this.rawMouseYMoved);
    }
}
