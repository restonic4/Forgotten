package com.restonic4.forgotten.client;

public class DeathUtils {
    private static boolean isDeath = false;
    private static boolean shouldLightBolt = false;

    private static long firstLightTime = -1;

    public static boolean isDeath() {
        return isDeath;
    }

    public static void setDeathValue(boolean value) {
        isDeath = value;
        shouldLightBolt = value;
    }

    public static boolean shouldLightBolt() {
        return shouldLightBolt;
    }

    public static void lightBoltStepCompleted() {
        if (firstLightTime == -1) {
            firstLightTime = System.currentTimeMillis();
        }

        if (System.currentTimeMillis() >= firstLightTime + 250) {
            shouldLightBolt = false;
            firstLightTime = -1;
        }
    }
}
