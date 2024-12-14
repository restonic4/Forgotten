package com.restonic4.forgotten.util.helpers;

import org.joml.Vector3f;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class MathHelper {
    public static Vector3f[] getQuadVertices() {
        return new Vector3f[] {
                new Vector3f(0, 0, 0),
                new Vector3f(1, 0, 0),
                new Vector3f(1, 0, 1),
                new Vector3f(0, 0, 1)
        };
    }

    public static Vector3f[] getSphereVertices() {
        return generateSphere(1, 15, 15).toArray(new Vector3f[0]);
    }

    public static java.util.List<Vector3f> generateSphere(float radius, int longs, int lats) {
        java.util.List<Vector3f> vertices = new ArrayList<>();

        float startU = 0.0f;
        float startV = 0.0f;
        float endU = (float) (2 * Math.PI);
        float endV = (float) Math.PI;

        float stepU = (endU - startU) / longs;
        float stepV = (endV - startV) / lats;

        for (int i = 0; i < longs; i++) {
            for (int j = 0; j < lats; j++) {
                float u = i * stepU + startU;
                float v = j * stepV + startV;

                float un = (i + 1 == longs) ? endU : (i + 1) * stepU + startU;
                float vn = (j + 1 == lats) ? endV : (j + 1) * stepV + startV;

                Vector3f p0 = parametricSphere(u, v, radius);
                Vector3f p1 = parametricSphere(u, vn, radius);
                Vector3f p2 = parametricSphere(un, v, radius);
                Vector3f p3 = parametricSphere(un, vn, radius);

                vertices.add(p0);
                vertices.add(p2);
                vertices.add(p1);

                vertices.add(p3);
                vertices.add(p1);
                vertices.add(p2);
            }
        }

        return vertices;
    }

    private static Vector3f parametricSphere(float u, float v, float radius) {
        float x = (float) (radius * Math.sin(v) * Math.cos(u));
        float y = (float) (radius * Math.cos(v));
        float z = (float) (radius * Math.sin(v) * Math.sin(u));
        return new Vector3f(x, y, z);
    }

    public static void scaleVertices(Vector3f[] vertices, float scaleX, float scaleY, float scaleZ) {
        for (Vector3f vertex : vertices) {
            vertex.x *= scaleX;
            vertex.y *= scaleY;
            vertex.z *= scaleZ;
        }
    }

    public static void translateVertices(Vector3f[] vertices, float translateX, float translateY, float translateZ) {
        for (Vector3f vertex : vertices) {
            vertex.x += translateX;
            vertex.y += translateY;
            vertex.z += translateZ;
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

    public static void rotateVertices(Vector3f[] vertices, float angleXDegrees, float angleYDegrees, float angleZDegrees) {
        float angleXRadians = (float) Math.toRadians(angleXDegrees);
        float angleYRadians = (float) Math.toRadians(angleYDegrees);
        float angleZRadians = (float) Math.toRadians(angleZDegrees);

        float cosX = (float) Math.cos(angleXRadians);
        float sinX = (float) Math.sin(angleXRadians);
        float cosY = (float) Math.cos(angleYRadians);
        float sinY = (float) Math.sin(angleYRadians);
        float cosZ = (float) Math.cos(angleZRadians);
        float sinZ = (float) Math.sin(angleZRadians);

        for (Vector3f vertex : vertices) {
            float y = vertex.y;
            float z = vertex.z;
            vertex.y = y * cosX - z * sinX;
            vertex.z = y * sinX + z * cosX;

            float x = vertex.x;
            z = vertex.z;
            vertex.x = x * cosY + z * sinY;
            vertex.z = -x * sinY + z * cosY;

            x = vertex.x;
            y = vertex.y;
            vertex.x = x * cosZ - y * sinZ;
            vertex.y = x * sinZ + y * cosZ;
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

    public static double normalize(double value, double min, double max) {
        if (min >= max) {
            throw new IllegalArgumentException("The minimum value should be bigger than the max value.");
        }

        return (value - min) / (max - min);
    }

    public static Vector3f getMidPoint(Vector3f startPosition, Vector3f endPosition) {
        float midX = (startPosition.x + endPosition.x) / 2;
        float midY = (startPosition.y + endPosition.y) / 2;
        float midZ = (startPosition.z + endPosition.z) / 2;

        return new Vector3f(midX, midY, midZ);
    }
}
