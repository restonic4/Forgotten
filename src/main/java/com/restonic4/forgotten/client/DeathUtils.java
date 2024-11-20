package com.restonic4.forgotten.client;

public class DeathUtils {
    private static boolean isDeath = false;
    private static boolean shouldResetGuiAnimations= false;
    private static boolean shouldResetFovAnimations= false;

    public static boolean isDeath() {
        return isDeath;
    }

    public static void setDeathValue(boolean value) {
        isDeath = value;
        shouldResetGuiAnimations = value;
        shouldResetFovAnimations = value;
    }

    public static boolean shouldResetGuiAnimations() {
        return shouldResetGuiAnimations;
    }

    public static void guiAnimationsRestarted() {
        shouldResetGuiAnimations = false;
    }

    public static boolean shouldResetFovAnimations() {
        return shouldResetFovAnimations;
    }

    public static void fovAnimationsRestarted() {
        shouldResetFovAnimations = false;
    }
}
