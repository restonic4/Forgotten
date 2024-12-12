package com.restonic4.forgotten.util;

import org.joml.Matrix4f;

public class MainMatrixStorage {
    private static Matrix4f currentMatrix;

    public static Matrix4f getCurrentMatrix() {
        if (currentMatrix == null) {
            currentMatrix = new Matrix4f();
        }

        return currentMatrix;
    }

    public static void setCurrentMatrix(Matrix4f matrix) {
        currentMatrix = matrix;
    }
}
