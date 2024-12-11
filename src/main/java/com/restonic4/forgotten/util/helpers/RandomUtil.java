package com.restonic4.forgotten.util.helpers;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {
    private static final Random RANDOM = new Random();

    public static Random getRandom() {
        return RANDOM;
    }

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

    public static <T> T getRandomFromArray(T[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        int index = RANDOM.nextInt(array.length);
        return array[index];
    }

    public static <T> T getRandomFromTwo(T obj1, T obj2) {
        return RANDOM.nextBoolean() ? obj1 : obj2;
    }

    public static <T> T getRandomFromThree(T obj1, T obj2, T obj3) {
        int index = RANDOM.nextInt(3);
        return switch (index) {
            case 0 -> obj1;
            case 1 -> obj2;
            default -> obj3;
        };
    }

    public static <T> T getRandomFromList(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        int index = RANDOM.nextInt(list.size());
        return list.get(index);
    }
}
