package com.restonic4.forgotten.client.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.restonic4.forgotten.util.EasingSystem;
import com.restonic4.forgotten.util.helpers.MathHelper;
import com.restonic4.forgotten.util.helpers.RenderingHelper;
import net.minecraft.client.Camera;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeamEffect {
    private long startTime, endTime;
    private final List<BeamLayer> layers;
    private Vector3f position;
    private Animation fadeInAnimation;
    private Animation fadeOutAnimation;
    private long timeBetweenFades;

    private final AnimationContext currentAnimationContext = new AnimationContext();

    public BeamEffect() {
        this.startTime = System.currentTimeMillis();
        this.layers = new ArrayList<>();
        this.position = new Vector3f();
        this.timeBetweenFades = 0;
    }

    public BeamEffect lifetime(float lifetimeInSecs) {
        this.endTime = this.startTime + (long) (lifetimeInSecs * 1000);
        return this;
    }

    public BeamEffect addLayer(float width, float height, Color color) {
        layers.add(new BeamLayer(width, height, color));
        return this;
    }

    public BeamEffect setPosition(Vector3f position) {
        this.position = position;
        return this;
    }

    public BeamEffect setFadeInAnimation(Animation animation) {
        this.fadeInAnimation = animation;
        return this;
    }

    public BeamEffect setFadeOutAnimation(Animation animation) {
        this.fadeOutAnimation = animation;
        return this;
    }

    public BeamEffect timeBetweenFades(float timeInSeconds) {
        this.timeBetweenFades = (long) (timeInSeconds * 1000);
        return this;
    }

    public void render(PoseStack poseStack, Matrix4f matrix4f, Camera camera) {
        for (int i = 0; i < this.layers.size(); i++) {
            currentAnimationContext.setColor(this.layers.get(i).getColor());
            currentAnimationContext.setWidth(this.layers.get(i).getWidth());
            currentAnimationContext.setHeight(this.layers.get(i).getHeight());

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
            RenderingHelper.renderComplexBeam(poseStack, matrix4f, camera, this.position, currentAnimationContext.getWidth(), currentAnimationContext.getHeight());
        }

        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    public boolean isFinished() {
        return System.currentTimeMillis() >= this.endTime;
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

    private class BeamLayer {
        private final float width, height;
        private final Color color;

        public BeamLayer(float width, float height, Color color) {
            this.width = width;
            this.height = height;
            this.color = color;
        }

        public float getWidth() {
            return width;
        }

        public float getHeight() {
            return height;
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
        private float width, height;
        private Color color;

        public AnimationContext() {}

        public AnimationContext(Color color, float width, float height) {
            this.color = color;
            this.width = width;
            this.height = height;
        }

        public float getWidth() {
            return width;
        }

        public void setWidth(float width) {
            this.width = width;
        }

        public float getHeight() {
            return height;
        }

        public void setHeight(float height) {
            this.height = height;
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

        animationContext.setWidth(animationContext.getWidth() * easedProgress);
    };

    public static Animation EASED_SCALE_OUT = (animationContext, progress) -> {
        float easedProgress = EasingSystem.getEasedValue(
                progress, 1f, 0f, EasingSystem.EasingType.BACK_IN
        );

        animationContext.setWidth(animationContext.getWidth() * easedProgress);
    };
}

