package com.restonic4.forgotten.util.helpers;

import org.joml.Vector3f;

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
}
