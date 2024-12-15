package com.restonic4.forgotten.mixin.client;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlUtil;
import com.restonic4.forgotten.Forgotten;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.Connection;
import net.minecraft.util.FrameTimer;
import net.minecraft.world.level.GameType;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Mixin(DebugScreenOverlay.class)
public abstract class DebugScreenOverlayMixin {
    @Shadow @Final private Minecraft minecraft;

    @Shadow private static long bytesToMegabytes(long l) {
        return 0;
    }

    @Shadow @Final private DebugScreenOverlay.AllocationRateCalculator allocationRateCalculator;

    @Shadow protected abstract void renderLines(GuiGraphics guiGraphics, List<String> list, boolean bl);

    @Inject(method = "getGameInformation", at = @At("HEAD"), cancellable = true)
    protected void getGameInformation(CallbackInfoReturnable<List<String>> cir) {
        if (this.minecraft.player != null && this.minecraft.player.getPlayerInfo() != null && (this.minecraft.player.getPlayerInfo().getGameMode() == GameType.SURVIVAL || this.minecraft.player.getPlayerInfo().getGameMode() == GameType.ADVENTURE)) {
            String serverInfo = getServerInfo();

            List<String> list = Lists.<String>newArrayList(
                    "Minecraft "
                            + SharedConstants.getCurrentVersion().getName()
                            + " ("
                            + this.minecraft.getLaunchedVersion()
                            + "/"
                            + ClientBrandRetriever.getClientModName()
                            + ("release".equalsIgnoreCase(this.minecraft.getVersionType()) ? "" : "/" + this.minecraft.getVersionType())
                            + ")",
                    this.minecraft.fpsString,
                    serverInfo,
                    "",
                    ChatFormatting.GOLD + "Debug screen overlay modified by " + Forgotten.MOD_ID
            );

            cir.setReturnValue(list);
            cir.cancel();
        }
    }

    @Inject(method = "getSystemInformation", at = @At("HEAD"), cancellable = true)
    protected void getSystemInformation(CallbackInfoReturnable<List<String>> cir) {
        if (this.minecraft.player != null && this.minecraft.player.getPlayerInfo() != null && (this.minecraft.player.getPlayerInfo().getGameMode() == GameType.SURVIVAL || this.minecraft.player.getPlayerInfo().getGameMode() == GameType.ADVENTURE)) {
            long maxMemory = Runtime.getRuntime().maxMemory();
            long totalMemory = Runtime.getRuntime().totalMemory();
            long freeMemory = Runtime.getRuntime().freeMemory();
            long memoryLeft = totalMemory - freeMemory;

            List<String> list = Lists.<String>newArrayList(
                    String.format(Locale.ROOT, "Java: %s %dbit", System.getProperty("java.version"), this.minecraft.is64Bit() ? 64 : 32),
                    String.format(Locale.ROOT, "Mem: % 2d%% %03d/%03dMB", memoryLeft * 100L / maxMemory, bytesToMegabytes(memoryLeft), bytesToMegabytes(maxMemory)),
                    String.format(Locale.ROOT, "Allocation rate: %03dMB /s", bytesToMegabytes(this.allocationRateCalculator.bytesAllocatedPerSecond(memoryLeft))),
                    String.format(Locale.ROOT, "Allocated: % 2d%% %03dMB", totalMemory * 100L / maxMemory, bytesToMegabytes(totalMemory)),
                    "",
                    String.format(Locale.ROOT, "CPU: %s", GlUtil.getCpuInfo()),
                    "",
                    String.format(
                            Locale.ROOT, "Display: %dx%d (%s)", Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight(), GlUtil.getVendor()
                    ),
                    GlUtil.getRenderer(),
                    GlUtil.getOpenGLVersion()
            );

            cir.setReturnValue(list);
            cir.cancel();
        }
    }

    @Inject(method = "drawChart", at = @At("HEAD"), cancellable = true)
    private void drawChart(GuiGraphics guiGraphics, FrameTimer frameTimer, int i, int j, boolean bl, CallbackInfo ci) {
        if (this.minecraft.player != null && this.minecraft.player.getPlayerInfo() != null && (this.minecraft.player.getPlayerInfo().getGameMode() == GameType.SURVIVAL || this.minecraft.player.getPlayerInfo().getGameMode() == GameType.ADVENTURE)) {
            ci.cancel();
        }
    }

    @Unique @NotNull
    private String getServerInfo() {
        IntegratedServer integratedServer = this.minecraft.getSingleplayerServer();
        Connection connection = this.minecraft.getConnection().getConnection();
        float averageSentPackets = connection.getAverageSentPackets();
        float averageReceivedPackets = connection.getAverageReceivedPackets();

        String serverInfo;
        if (integratedServer != null) {
            serverInfo = String.format(Locale.ROOT, "Integrated server @ %.0f ms ticks, %.0f tx, %.0f rx", integratedServer.getAverageTickTime(), averageSentPackets, averageReceivedPackets);
        } else {
            serverInfo = String.format(Locale.ROOT, "\"%s\" server, %.0f tx, %.0f rx", this.minecraft.player.getServerBrand(), averageSentPackets, averageReceivedPackets);
        }
        return serverInfo;
    }
}
