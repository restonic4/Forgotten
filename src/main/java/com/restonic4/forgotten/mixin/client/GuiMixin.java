package com.restonic4.forgotten.mixin.client;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.client.DeathUtils;
import com.restonic4.forgotten.util.EasingSystem;
import com.restonic4.forgotten.util.helpers.RandomUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Shadow public abstract void render(GuiGraphics guiGraphics, float f);

    @Unique private static long borderEasingStartTime = 0;
    @Unique private static long borderEasingEndTime = 0;

    @Unique private static long flashEasingStartTime = 0;
    @Unique private static long flashEasingEndTime = 0;

    @Unique private static long iconShakingEasingStartTime = 0;
    @Unique private static long iconShakingEasingEndTime = 0;

    @Inject(
            method = "render(Lnet/minecraft/client/gui/GuiGraphics;F)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderHotbar(FLnet/minecraft/client/gui/GuiGraphics;)V"),
            cancellable = true
    )
    private void onRenderHotbarInjected(GuiGraphics guiGraphics, float f, CallbackInfo ci) {
        if (DeathUtils.isDeath()) {
            Gui current = (Gui) (Object) this;

            if (DeathUtils.shouldResetGuiAnimations()) {
                DeathUtils.guiAnimationsRestarted();

                borderEasingStartTime = System.currentTimeMillis();
                borderEasingEndTime = System.currentTimeMillis() + 2000;

                flashEasingStartTime = System.currentTimeMillis();
                flashEasingEndTime = System.currentTimeMillis() + 500;

                iconShakingEasingStartTime = System.currentTimeMillis();
                iconShakingEasingEndTime = System.currentTimeMillis() + 24000;
            }

            Window window = current.minecraft.getWindow();
            current.screenWidth = guiGraphics.guiWidth();
            current.screenHeight = guiGraphics.guiHeight();

            float easedValue = EasingSystem.getEasedValue(flashEasingStartTime, flashEasingEndTime, 1, 0, EasingSystem.EasingType.BOUNCE_IN_OUT);
            if (easedValue >= 0.2f && easedValue <= 0.8f) {
                guiGraphics.blit(new ResourceLocation("minecraft", "textures/misc/white.png"), 0, 0, 0, 4, 4, current.screenWidth, current.screenHeight, 4, 4);
            }

            float easedAlpha = EasingSystem.getEasedValue(borderEasingStartTime, borderEasingEndTime, 0, 1, EasingSystem.EasingType.BOUNCE_IN_OUT);
            current.renderTextureOverlay(guiGraphics, new ResourceLocation(Forgotten.MOD_ID, "textures/gui/border.png"), easedAlpha);

            if (!current.minecraft.options.hideGui) {
                renderIcons(guiGraphics);

                renderOtherStuff(guiGraphics, window);
            }

            ci.cancel();
        }
    }

    @Unique
    public void renderIcons(GuiGraphics guiGraphics) {
        Minecraft client = Minecraft.getInstance();

        int iconCount = 3;
        int iconSize = 32;
        int spacing = 4;

        int totalWidth = (iconCount * iconSize) + ((iconCount - 1) * spacing);

        int screenWidth = client.getWindow().getGuiScaledWidth();
        int screenHeight = client.getWindow().getGuiScaledHeight();
        int startX = (screenWidth - totalWidth) / 2;
        int startY = screenHeight - 50;

        for (int i = 0; i < iconCount; i++) {
            int x = startX + (i * (iconSize + spacing));
            int y = startY;

            float easedShakingIntensity = EasingSystem.getEasedValue(iconShakingEasingStartTime, iconShakingEasingEndTime, 1, 0, EasingSystem.EasingType.CUBIC_OUT);
            if (easedShakingIntensity < 1) {
                x += (int) (RandomUtil.randomBetween(-10, 10) * easedShakingIntensity);
                y += (int) (RandomUtil.randomBetween(-10, 10) * easedShakingIntensity);
            }

            boolean isInCooldown = false;

            renderIcon(guiGraphics, x, y, iconSize, i, isInCooldown);
        }
    }

    @Unique
    private void renderIcon(GuiGraphics guiGraphics, int x, int y, int size, int iconID, boolean isCooldown) {
        ResourceLocation texture = new ResourceLocation(Forgotten.MOD_ID, "textures/gui/icons.png");

        int textureX = size * iconID;
        int textureY = 0;

        int globalTextureXSize = size * 3;
        int globalTextureYSize = size * 2;

        guiGraphics.blit(texture, x, y, 0, textureX, textureY, size, size, globalTextureXSize, globalTextureYSize);

        if (isCooldown) {
            guiGraphics.blit(texture, x, y, 0, 0, textureY + size, size, size, globalTextureXSize, globalTextureYSize);
        }
    }

    @Unique
    private void renderOtherStuff(GuiGraphics guiGraphics, Window window) {
        Gui current = (Gui) (Object) this;

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
        RenderSystem.disableBlend();
    }
}