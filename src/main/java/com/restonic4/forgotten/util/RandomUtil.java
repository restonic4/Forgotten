package com.restonic4.forgotten.util;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {
    public static float randomBetween(float min, float max) {
        if (min > max) {
            throw new IllegalArgumentException("The min value can't be bigger than the max value.");
        }
        return ThreadLocalRandom.current().nextFloat() * (max - min) + min;
    }

    public static int randomBetween(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("The min value can't be bigger than the max value.");
        }
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
