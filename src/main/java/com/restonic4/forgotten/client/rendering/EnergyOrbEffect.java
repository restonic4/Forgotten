package com.restonic4.forgotten.client.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.restonic4.forgotten.util.EasingSystem;
import com.restonic4.forgotten.util.helpers.MathHelper;
import com.restonic4.forgotten.util.helpers.RandomUtil;
import com.restonic4.forgotten.util.helpers.RenderingHelper;
import net.minecraft.client.Camera;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import team.lodestar.lodestone.systems.rendering.VFXBuilders;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EnergyOrbEffect {
    private long startTime, endTime;
    private final List<OrbLayer> layers;
    private Vector3f position;
    private Animation fadeInAnimation;
    private Animation fadeOutAnimation;
    private long timeBetweenFades;

    private final AnimationContext currentAnimationContext = new AnimationContext();

    public EnergyOrbEffect() {
        this.startTime = System.currentTimeMillis();
        this.layers = new ArrayList<>();
        this.position = new Vector3f();
        this.timeBetweenFades = 0;
    }

    public EnergyOrbEffect lifetime(float lifetimeInSecs) {
        this.endTime = this.startTime + (long) (lifetimeInSecs * 1000);
        return this;
    }

    public EnergyOrbEffect addLayer(float radius, Color color) {
        layers.add(new OrbLayer(radius, color));
        return this;
    }

    public EnergyOrbEffect setPosition(Vector3f position) {
        this.position = position;
        return this;
    }

    public EnergyOrbEffect setFadeInAnimation(Animation animation) {
        this.fadeInAnimation = animation;
        return this;
    }

    public EnergyOrbEffect setFadeOutAnimation(Animation animation) {
        this.fadeOutAnimation = animation;
        return this;
    }

    public EnergyOrbEffect timeBetweenFades(float timeInSeconds) {
        this.timeBetweenFades = (long) (timeInSeconds * 1000);
        return this;
    }

    public void render(PoseStack poseStack, Matrix4f matrix4f, Camera camera) {
        for (int i = 0; i < this.layers.size(); i++) {
            currentAnimationContext.setColor(this.layers.get(i).getColor());
            currentAnimationContext.setRadius(this.layers.get(i).getRadius());

            if (isFadingIn() && this.fadeInAnimation != null) {
                this.fadeInAnimation.apply(currentAnimationContext, getFadeInProgress());
            }

            if (isFadingOut() && this.fadeOutAnimation != null) {
                this.fadeOutAnimation.apply(currentAnimationContext, getFadeOutProgress());
            }

            float r = MathHelper.getNormalizedColorR(currentAnimationContext.getColor());
            float g = MathHelper.getNormalizedColorG(currentAnimationContext.getColor());
            float b = MathHelper.getNormalizedColorB(currentAnimationContext.getColor());
            float a = MathHelper.getNormalizedColorA(currentAnimationContext.getColor());

            RenderSystem.setShaderColor(r, g, b, a);
            //GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
            RenderingHelper.renderSphere(poseStack, matrix4f, camera, this.position, currentAnimationContext.getRadius() * RandomUtil.randomBetween(0.8f, 1.2f));
            //GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        }

        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    public boolean isFinished() {
        return System.currentTimeMillis() >= this.endTime;
    }

    public void cleanup() {
        this.layers.clear();
    }

    public boolean isFadingIn() {
        long currentTime = System.currentTimeMillis();
        long midpoint = (startTime + endTime) / 2;
        long fadeInEnd = midpoint - timeBetweenFades / 2;

        return currentTime >= startTime && currentTime < fadeInEnd;
    }

    public boolean isFadingOut() {
        long currentTime = System.currentTimeMillis();
        long midpoint = (startTime + endTime) / 2;
        long fadeOutStart = midpoint + timeBetweenFades / 2;

        return currentTime >= fadeOutStart && currentTime <= endTime;
    }

    public float getFadeInProgress() {
        long currentTime = System.currentTimeMillis();
        long midpoint = (startTime + endTime) / 2;
        long fadeInEnd = midpoint - timeBetweenFades / 2;

        if (currentTime < startTime) {
            return 0.0f;
        }
        if (currentTime >= fadeInEnd) {
            return 1.0f;
        }

        return (float) (currentTime - startTime) / (fadeInEnd - startTime);
    }

    public float getFadeOutProgress() {
        long currentTime = System.currentTimeMillis();
        long midpoint = (startTime + endTime) / 2;
        long fadeOutStart = midpoint + timeBetweenFades / 2;

        if (currentTime < fadeOutStart) {
            return 0.0f;
        }
        if (currentTime >= endTime) {
            return 1.0f;
        }

        return (float) (currentTime - fadeOutStart) / (endTime - fadeOutStart);
    }

    private class OrbLayer {
        private final float radius;
        private final Color color;

        public OrbLayer(float radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        public float getRadius() {
            return radius;
        }

        public Color getColor() {
            return color;
        }
    }

    @FunctionalInterface
    public interface Animation {
        void apply(AnimationContext animationContext, float progress);
    }

    public class AnimationContext {
        private float radius;
        private Color color;

        public AnimationContext() {}

        public AnimationContext(Color color, float radius) {
            this.color = color;
            this.radius = radius;
        }

        public float getRadius() {
            return radius;
        }

        public void setRadius(float radius) {
            this.radius = radius;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }
    }

    public static Animation EASED_SCALE_IN = (animationContext, progress) -> {
        float easedProgress = EasingSystem.getEasedValue(
                progress, 0f, 1f, EasingSystem.EasingType.BACK_OUT
        );

        animationContext.setRadius(animationContext.getRadius() * easedProgress);
    };

    public static Animation EASED_SCALE_OUT = (animationContext, progress) -> {
        float easedProgress = EasingSystem.getEasedValue(
                progress, 1f, 0f, EasingSystem.EasingType.BACK_IN
        );

        animationContext.setRadius(animationContext.getRadius() * easedProgress);
    };
}

