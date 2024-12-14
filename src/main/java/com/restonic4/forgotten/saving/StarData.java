package com.restonic4.forgotten.saving;

import java.io.Serializable;

public class StarData implements Serializable {
    private static final long serialVersionUID = 1L;

    private float size, rotation, x, y, z;

    public StarData(float size, float rotation, float x, float y, float z) {
        this.size = size;
        this.rotation = rotation;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getSize() {
        return size;
    }

    public float getRotation() {
        return rotation;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }
}
