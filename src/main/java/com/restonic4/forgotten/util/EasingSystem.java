package com.restonic4.forgotten.util;

import java.util.function.Function;

public class EasingSystem {
    public enum EasingType {
        LINEAR,
        QUAD_IN, QUAD_OUT, QUAD_IN_OUT,
        CUBIC_IN, CUBIC_OUT, CUBIC_IN_OUT,
        EXPONENTIAL_IN, EXPONENTIAL_OUT, EXPONENTIAL_IN_OUT,
        CIRC_IN, CIRC_OUT, CIRC_IN_OUT,
        BACK_IN, BACK_OUT, BACK_IN_OUT,
        BOUNCE_IN, BOUNCE_OUT, BOUNCE_IN_OUT,
    }

    public static float getEasedValue(long startTime, long endTime, float startValue, float endValue, EasingType type) {
        long currentTime = System.currentTimeMillis();
        if (currentTime >= endTime) return endValue;
        if (currentTime <= startTime) return startValue;

        float t = (float) (currentTime - startTime) / (endTime - startTime);
        float delta = endValue - startValue;

        Function<Float, Float> easingFunction = selectEasingFunction(type);
        return startValue + delta * easingFunction.apply(t);
    }

    public static float getEasedValue(float progress, float startValue, float endValue, EasingType type) {
        progress = Math.min(1f, Math.max(0f, progress));

        float delta = endValue - startValue;

        Function<Float, Float> easingFunction = selectEasingFunction(type);
        return startValue + delta * easingFunction.apply(progress);
    }


    private static Function<Float, Float> selectEasingFunction(EasingType type) {
        return switch (type) {
            case LINEAR -> t -> t;

            case QUAD_IN -> t -> t * t;
            case QUAD_OUT -> t -> 1 - (1 - t) * (1 - t);
            case QUAD_IN_OUT -> t -> t < 0.5 ? 2 * t * t : (float) (1 - Math.pow(-2 * t + 2, 2) / 2);

            case CUBIC_IN -> t -> t * t * t;
            case CUBIC_OUT -> t -> 1 - (float) Math.pow(1 - t, 3);
            case CUBIC_IN_OUT -> t -> t < 0.5
                    ? 4 * t * t * t
                    : 1 - (float) Math.pow(-2 * t + 2, 3) / 2;

            case EXPONENTIAL_IN -> t -> t == 0 ? 0 : (float) Math.pow(2, 10 * t - 10);
            case EXPONENTIAL_OUT -> t -> t == 1 ? 1 : (float) (1 - Math.pow(2, -10 * t));
            case EXPONENTIAL_IN_OUT -> t -> t == 0
                    ? 0
                    : (float) (t == 1
                    ? 1
                    : t < 0.5 ? Math.pow(2, 20 * t - 10) / 2
                    : (2 - Math.pow(2, -20 * t + 10)) / 2);

            case CIRC_IN -> t -> (float) (1 - Math.sqrt(1 - Math.pow(t, 2)));
            case CIRC_OUT -> t -> (float) Math.sqrt(1 - Math.pow(t - 1, 2));
            case CIRC_IN_OUT -> t -> (float) (t < 0.5
                                ? (1 - Math.sqrt(1 - Math.pow(2 * t, 2))) / 2
                                : (Math.sqrt(1 - Math.pow(-2 * t + 2, 2)) + 1) / 2);

            case BACK_IN -> t -> (float) ((1.70158f + 1) * t * t * t - 1.70158 * t * t);
            case BACK_OUT -> t -> (float) (1 + (1.70158 + 1) * Math.pow(t - 1, 3) + 1.70158f * Math.pow(t - 1, 2));
            case BACK_IN_OUT -> t -> (float) (t < 0.5
                                ? (Math.pow(2 * t, 2) * ((2.5949095f + 1) * 2 * t - 2.5949095f)) / 2
                                : (Math.pow(2 * t - 2, 2) * ((2.5949095f + 1) * (t * 2 - 2) + 2.5949095f) + 2) / 2);

            case BOUNCE_IN -> t -> 1 - selectEasingFunction(EasingType.BOUNCE_OUT).apply(1 - t);
            case BOUNCE_OUT -> t -> {
                if (t < (1 / 2.75f)) {
                    return 7.5625f * t * t;
                } else if (t < (2 / 2.75f)) {
                    return 7.5625f * (t -= 1.5f / 2.75f) * t + 0.75f;
                } else if (t < (2.5f / 2.75f)) {
                    return 7.5625f * (t -= 2.25f / 2.75f) * t + 0.9375f;
                } else {
                    return 7.5625f * (t -= 2.625f / 2.75f) * t + 0.984375f;
                }
            };
            case BOUNCE_IN_OUT -> t -> t < 0.5
                    ? 0.5f * selectEasingFunction(EasingType.BOUNCE_IN).apply(t * 2)
                    : 0.5f * selectEasingFunction(EasingType.BOUNCE_OUT).apply(t * 2 - 1) + 0.5f;
        };
    }
}
