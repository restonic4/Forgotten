package com.restonic4.forgotten.util.helpers;

import org.joml.Vector3f;

import java.awt.*;

public class MathHelper {
    public static Vector3f[] getQuadVertices() {
        return new Vector3f[] {
                new Vector3f(0, 0, 0),
                new Vector3f(1, 0, 0),
                new Vector3f(1, 0, 1),
                new Vector3f(0, 0, 1)
        };
    }

    public static void scaleVertices(Vector3f[] vertices, float scaleX, float scaleY, float scaleZ) {
        for (Vector3f vertex : vertices) {
            vertex.mul(scaleX, scaleY, scaleZ);
        }
    }

    public static void translateVertices(Vector3f[] vertices, float translateX, float translateY, float translateZ) {
        for (Vector3f vertex : vertices) {
            vertex.add(translateX, translateY, translateZ);
        }
    }

    public static void rotateVerticesX(Vector3f[] vertices, float angleDegrees) {
        float angleRadians = (float) Math.toRadians(angleDegrees);
        for (Vector3f vertex : vertices) {
            float y = vertex.y;
            float z = vertex.z;
            vertex.y = y * (float) Math.cos(angleRadians) - z * (float) Math.sin(angleRadians);
            vertex.z = y * (float) Math.sin(angleRadians) + z * (float) Math.cos(angleRadians);
        }
    }

    public static void rotateVerticesY(Vector3f[] vertices, float angleDegrees) {
        float angleRadians = (float) Math.toRadians(angleDegrees);
        for (Vector3f vertex : vertices) {
            float x = vertex.x;
            float z = vertex.z;
            vertex.x = x * (float) Math.cos(angleRadians) + z * (float) Math.sin(angleRadians);
            vertex.z = -x * (float) Math.sin(angleRadians) + z * (float) Math.cos(angleRadians);
        }
    }

    public static void rotateVerticesZ(Vector3f[] vertices, float angleDegrees) {
        float angleRadians = (float) Math.toRadians(angleDegrees);
        for (Vector3f vertex : vertices) {
            float x = vertex.x;
            float y = vertex.y;
            vertex.x = x * (float) Math.cos(angleRadians) - y * (float) Math.sin(angleRadians);
            vertex.y = x * (float) Math.sin(angleRadians) + y * (float) Math.cos(angleRadians);
        }
    }

    public static float calculateScale(Vector3f distance, float maxDistance, float maxValue) {
        float lengthXZ = (float) Math.sqrt(distance.x * distance.x + distance.z * distance.z);

        lengthXZ = Math.min(lengthXZ, maxDistance);

        return (lengthXZ / maxDistance) * maxValue;
    }

    public static float[] getNormalizedColor(Color color) {
        float[] colorData = new float[4];

        colorData[0] = color.getRed() > 1 ? (color.getRed() / 255f) : color.getRed();
        colorData[1] = color.getGreen() > 1 ? (color.getGreen() / 255f) : color.getGreen();
        colorData[2] = color.getBlue() > 1 ? (color.getBlue() / 255f) : color.getBlue();
        colorData[3] = color.getAlpha() > 1 ? (color.getAlpha() / 255f) : color.getAlpha();

        return colorData;
    }

    public static float getNormalizedColorR(Color color) {
        return color.getRed() > 1 ? (color.getRed() / 255f) : color.getRed();
    }

    public static float getNormalizedColorG(Color color) {
        return color.getGreen() > 1 ? (color.getGreen() / 255f) : color.getGreen();
    }

    public static float getNormalizedColorB(Color color) {
        return color.getBlue() > 1 ? (color.getBlue() / 255f) : color.getBlue();
    }

    public static float getNormalizedColorA(Color color) {
        return color.getAlpha() > 1 ? (color.getAlpha() / 255f) : color.getAlpha();
    }

    public static float getProgress(long startTime, long endTime) {
        long currentTime = System.currentTimeMillis();

        if (currentTime < startTime) {
            return 0f;
        }

        if (currentTime > endTime) {
            return 1f;
        }

        return (float) (currentTime - startTime) / (endTime - startTime);
    }

    public static double calculatePeak(float normalizedValue, double min, double max) {
        if (normalizedValue < 0 || normalizedValue > 1) {
            throw new IllegalArgumentException("The normalized value should be between 0 and 1.");
        }

        double peakValue = -4 * (normalizedValue - 0.5) * (normalizedValue - 0.5) + 1;

        return min + (max - min) * peakValue;
    }
}
