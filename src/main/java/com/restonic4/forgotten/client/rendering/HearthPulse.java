package com.restonic4.forgotten.client.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.registries.common.ForgottenSounds;
import com.restonic4.forgotten.util.helpers.MathHelper;
import com.restonic4.forgotten.util.helpers.RandomUtil;
import com.restonic4.forgotten.util.trash.OldCodeThatCouldBeUsefulAtSomePoint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import team.lodestar.lodestone.helpers.RandomHelper;
import team.lodestar.lodestone.registry.common.particle.LodestoneScreenParticleRegistry;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.particle.builder.ScreenParticleBuilder;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;
import team.lodestar.lodestone.systems.particle.data.spin.SpinParticleData;
import team.lodestar.lodestone.systems.particle.screen.ScreenParticleHolder;
import team.lodestar.lodestone.systems.particle.screen.base.ScreenParticle;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.random.RandomGenerator;

public class HearthPulse {
    private int index;
    private long startTime, endTime;
    private long whiteEndTime;
    private boolean didParticlesSpawned = false;
    private Gui.HeartType heartType;

    public HearthPulse(int index) {
        this.index = index;
        this.startTime = System.currentTimeMillis();
    }

    public HearthPulse reset() {
        long lifeTime = this.endTime - this.startTime;

        this.startTime = System.currentTimeMillis();
        this.endTime = this.startTime + lifeTime;

        return this;
    }

    public HearthPulse lifetime(float lifetimeInSecs) {
        this.endTime = this.startTime + (long) (lifetimeInSecs * 1000);
        this.whiteEndTime = this.startTime + (long) ((lifetimeInSecs/3) * 1000);
        return this;
    }

    public HearthPulse hearthType(Gui.HeartType heartType) {
        this.heartType = heartType;
        return this;
    }

    public HearthPulse noParticles() {
        this.didParticlesSpawned = true;
        return this;
    }

    public void render(GuiGraphics guiGraphics, int x, int y, int u, int v) {
        float scale = 1.5f - getProgress() * 0.5f;

        int effectSize = (int) (9 * scale);

        int offsetX = (effectSize - 9) / 2;
        int offsetY = (effectSize - 9) / 2;

        int centeredX = x - offsetX;
        int centeredY = y - offsetY;

        int totalTextureSize = 256;

        ResourceLocation texture = Gui.GUI_ICONS_LOCATION;

        if (System.currentTimeMillis() <= this.whiteEndTime) {
            texture = new ResourceLocation(Forgotten.MOD_ID, "textures/gui/white_hearth.png");
            u = 0;
            v = 0;
            totalTextureSize = 9;

            RenderSystem.setShaderColor(1, 1, 1, 1 - MathHelper.getProgress(this.startTime, this.whiteEndTime));
        }

        renderHeartWithScale(
                guiGraphics,
                texture,
                centeredX,
                centeredY,
                effectSize,
                u,
                v,
                totalTextureSize
        );

        RenderSystem.setShaderColor(1, 1, 1, 1);

        if (!didParticlesSpawned && heartType != null) {
            didParticlesSpawned = true;
            spawnParticles(OldCodeThatCouldBeUsefulAtSomePoint.SCREEN_PARTICLES, x + offsetX, y + offsetY);
        }
    }

    public void renderHeartWithScale(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int size, int u, int v, int totalTextureSize) {
        guiGraphics.blit(
                texture,
                x,
                y,
                size,
                size,
                u,
                v,
                9,
                9,
                totalTextureSize,
                totalTextureSize
        );
    }

    public void spawnParticles(ScreenParticleHolder target, float x, float y) {
        Color color = getColor();

        createParticleBuilder(target, color).spawn(x, y).spawn(x, y).spawn(x, y).spawn(x, y);
        createParticleBuilder(target, color).spawn(x, y).spawn(x, y).spawn(x, y).spawn(x, y);
        createParticleBuilder(target, color).spawn(x, y).spawn(x, y).spawn(x, y).spawn(x, y);
        createParticleBuilder(target, color).spawn(x, y).spawn(x, y).spawn(x, y).spawn(x, y);

        Minecraft.getInstance().execute(() -> {
            if (Minecraft.getInstance().player == null || Minecraft.getInstance().level == null) {
                return;
            }

            BlockPos blockPos = Minecraft.getInstance().player.blockPosition();

            SoundEvent randomSound = RandomUtil.getRandomFromTwo(ForgottenSounds.FIREBALL1, ForgottenSounds.FIREBALL2);
            float pitch = RandomUtil.randomBetween(0.75f, 1.25f);
            Minecraft.getInstance().level.playLocalSound(blockPos.getX(), blockPos.getY(), blockPos.getZ(), randomSound, SoundSource.AMBIENT, 1, pitch, false);
        });
    }

    @NotNull
    private Color getColor() {
        Color color = new Color(255, 19, 19);

        if (this.heartType == Gui.HeartType.NORMAL) {
            color = new Color(255, 19, 19);
        } else if (this.heartType == Gui.HeartType.ABSORBING) {
            color = new Color(212, 175, 55);
        } else if (this.heartType == Gui.HeartType.FROZEN) {
            color = new Color(168, 247, 255);
        } else if (this.heartType == Gui.HeartType.POISIONED) {
            color = new Color(148, 120, 24);
        } else if (this.heartType == Gui.HeartType.WITHERED) {
            color = new Color(43, 43, 43);
        }

        return color;
    }

    public static ScreenParticleBuilder createParticleBuilder(ScreenParticleHolder target, Color color) {
        Random random = new Random();

        return ScreenParticleBuilder.create(LodestoneScreenParticleRegistry.TWINKLE, target)
                .setScaleData(GenericParticleData.create(RandomUtil.randomBetween(0.25f, 0.75f), 0).build())
                .setColorData(ColorParticleData.create(color).build())
                .setTransparencyData(GenericParticleData.create(1, 0.5f).setEasing(Easing.QUAD_IN_OUT).build())
                .setSpinData(SpinParticleData.create(random.nextFloat() * 2).setSpinOffset(random.nextFloat() * 360).build())
                .setLifetime(20)
                .setRandomMotion(2)
                .setRandomOffset(2);
    }

    public float getProgress() {
        long currentTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        if (duration <= 0) return 0.0f;

        long elapsedTime = currentTime - startTime;
        float progress = (float) elapsedTime / (float) duration;

        return Math.min(Math.max(progress, 0.0f), 1.0f);
    }

    public boolean isFinished() {
        return System.currentTimeMillis() >= this.endTime;
    }

    public int getIndex() {
        return this.index;
    }
}
