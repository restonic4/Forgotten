package com.restonic4.forgotten.util;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class VertexArrayStack {
    private final List<Vector3f[]> stack;

    public VertexArrayStack() {
        this.stack = new ArrayList<>();
    }

    public void pushStack() {
        Vector3f[] lastVectorArray = this.stack.getLast();
        this.stack.add(lastVectorArray);
    }

    public void popStack() {
        this.stack.removeFirst();
    }

    public Vector3f[] last() {
        return this.stack.getLast();
    }

    public void setLast(Vector3f[] vector3fArray) {
        Vector3f[] lastVectorArray = this.stack.getLast();

        if (lastVectorArray != null) {
            for (int i = 0; i < lastVectorArray.length; i++) {
                lastVectorArray[i] = vector3fArray[i];
            }
        } else {
            this.stack.add(vector3fArray);
        }
    }
}
