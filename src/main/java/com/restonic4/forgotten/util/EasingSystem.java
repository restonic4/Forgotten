package com.restonic4.forgotten.util;

import java.util.function.Function;

public class EasingSystem {
    public enum EasingType {
        QUAD_IN, QUAD_OUT, QUAD_IN_OUT,
        BOUNCE_IN, BOUNCE_OUT, BOUNCE_IN_OUT,
        LINEAR,
        CUBIC_IN, CUBIC_OUT, CUBIC_IN_OUT
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

    private static Function<Float, Float> selectEasingFunction(EasingType type) {
        return switch (type) {
            case QUAD_IN -> t -> t * t;
            case QUAD_OUT -> t -> 1 - (1 - t) * (1 - t);
            case QUAD_IN_OUT -> t -> t < 0.5 ? 2 * t * t : (float) (1 - Math.pow(-2 * t + 2, 2) / 2);
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
            case LINEAR -> t -> t;
            case CUBIC_IN -> t -> t * t * t;
            case CUBIC_OUT -> t -> 1 - (float) Math.pow(1 - t, 3);
            case CUBIC_IN_OUT -> t -> t < 0.5
                    ? 4 * t * t * t
                    : 1 - (float) Math.pow(-2 * t + 2, 3) / 2;
        };
    }
}
