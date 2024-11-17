package com.restonic4.forgotten.client;

public class DeathUtils {
    private static boolean isDeath = false;

    public static boolean isDeath() {
        return isDeath;
    }

    public static void setDeathValue(boolean value) {
        isDeath = value;
    }
}
