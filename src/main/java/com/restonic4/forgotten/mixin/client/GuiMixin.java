package com.restonic4.forgotten.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.restonic4.forgotten.client.DeathUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Inject(
            method = "render(Lnet/minecraft/client/gui/GuiGraphics;F)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderHotbar(FLnet/minecraft/client/gui/GuiGraphics;)V"),
            cancellable = true
    )
    private void onRenderHotbarInjected(GuiGraphics guiGraphics, float f, CallbackInfo ci) {
        if (DeathUtils.isDeath()) {
            Gui current = (Gui) (Object) this;

            Window window = current.minecraft.getWindow();
            current.screenWidth = guiGraphics.guiWidth();
            current.screenHeight = guiGraphics.guiHeight();

            if (!current.minecraft.options.hideGui) {
                RenderSystem.enableBlend();
                current.renderCrosshair(guiGraphics);
                RenderSystem.disableBlend();

                current.renderEffects(guiGraphics);
                if (current.minecraft.options.renderDebug) {
                    current.debugScreen.render(guiGraphics);
                }

                int o;

                RenderSystem.enableBlend();
                o = Mth.floor(current.minecraft.mouseHandler.xpos() * (double)window.getGuiScaledWidth() / (double)window.getScreenWidth());
                int q = Mth.floor(current.minecraft.mouseHandler.ypos() * (double)window.getGuiScaledHeight() / (double)window.getScreenHeight());
                current.minecraft.getProfiler().push("chat");
                current.getChat().render(guiGraphics, current.tickCount, o, q);
                current.minecraft.getProfiler().pop();

                current.renderSavingIndicator(guiGraphics);
            }

            ci.cancel();
        }
    }
}