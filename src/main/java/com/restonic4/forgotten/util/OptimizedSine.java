package com.restonic4.forgotten.util;

public class OptimizedSine {
    private static final int TABLE_SIZE = 360;
    private static final double[] sineTable = new double[TABLE_SIZE];

    static {
        for (int i = 0; i < TABLE_SIZE; i++) {
            sineTable[i] = Math.sin(Math.toRadians(i));
        }
    }

    public static double sin(double angle) {
        angle = angle % 360;
        if (angle < 0) {
            angle += 360;
        }

        int index = (int) angle;
        return sineTable[index];
    }
}
