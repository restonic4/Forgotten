package com.restonic4.forgotten.saving;

public class StarData {
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
