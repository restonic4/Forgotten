package com.restonic4.forgotten.util;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class VertexArrayStack {
    private final List<Vector3f[]> stack;

    public VertexArrayStack() {
        this.stack = new ArrayList<>();
    }

    public VertexArrayStack(Vector3f[] firstStack) {
        this.stack = new ArrayList<>();
        setLast(firstStack);
    }

    public void pushStack() {
        if (!stack.isEmpty()) {
            Vector3f[] lastVectorArray = stack.get(stack.size() - 1);
            Vector3f[] copy = new Vector3f[lastVectorArray.length];
            for (int i = 0; i < lastVectorArray.length; i++) {
                copy[i] = new Vector3f(lastVectorArray[i]);
            }
            stack.add(copy);
        } else {
            throw new IllegalStateException("No elements in the stack to push.");
        }
    }

    public void popStack() {
        if (!stack.isEmpty()) {
            stack.remove(stack.size() - 1);
        } else {
            throw new IllegalStateException("Cannot pop from an empty stack.");
        }
    }

    public Vector3f[] last() {
        if (!stack.isEmpty()) {
            return stack.get(stack.size() - 1);
        } else {
            throw new IllegalStateException("No elements in the stack.");
        }
    }

    public void setLast(Vector3f[] vector3fArray) {
        if (!stack.isEmpty()) {
            Vector3f[] lastVectorArray = stack.get(stack.size() - 1);

            if (lastVectorArray.length != vector3fArray.length) {
                throw new IllegalArgumentException("Vector array lengths do not match.");
            }

            for (int i = 0; i < lastVectorArray.length; i++) {
                lastVectorArray[i].set(vector3fArray[i]);
            }
        } else {
            stack.add(vector3fArray);
        }
    }
}
