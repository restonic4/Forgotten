package com.restonic4.forgotten.registries.client.custom;

import com.restonic4.forgotten.util.VertexArrayStack;
import com.restonic4.forgotten.util.helpers.MathHelper;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class RenderShapes {
    private final List<Vector3f[]> originalVertices;
    private List<Vector3f[]> vertices;

    public RenderShapes(List<Vector3f[]> vertices) {
        this.vertices = deepCopy(vertices);
        this.originalVertices = deepCopy(vertices);
    }

    public void setVertices(List<Vector3f[]> vertices) {
        this.vertices = vertices;
    }

    public List<Vector3f[]> getVertices() {
        reset();
        return vertices;
    }

    public void reset() {
        for (int i = 0; i < vertices.size(); i++) {
            Vector3f[] originalArray = originalVertices.get(i);
            Vector3f[] currentArray = vertices.get(i);

            for (int j = 0; j < currentArray.length; j++) {
                currentArray[j].set(originalArray[j]);
            }
        }
    }

    private static Vector3f[] copyArray(Vector3f[] original) {
        Vector3f[] copy = new Vector3f[original.length];
        for (int i = 0; i < original.length; i++) {
            copy[i] = new Vector3f(original[i]);
        }
        return copy;
    }

    private static List<Vector3f[]> deepCopy(List<Vector3f[]> original) {
        List<Vector3f[]> copy = new ArrayList<>();
        for (Vector3f[] array : original) {
            copy.add(copyArray(array));
        }
        return copy;
    }

    public static final RenderShapes BEAM;

    static {
        BEAM = new RenderShapes(buildBeamVertices());
    }

    private static List<Vector3f[]> buildBeamVertices() {
        List<Vector3f[]> verticesToSave = new ArrayList<>();

        Vector3f[] vertices = MathHelper.getQuadVertices();
        MathHelper.scaleVertices(vertices, 1, 1, 1);
        MathHelper.rotateVerticesX(vertices, -90);

        VertexArrayStack vertexArrayStack = new VertexArrayStack(vertices);

        vertexArrayStack.pushStack();
        MathHelper.translateVertices(vertexArrayStack.last(), 0, 0, 1);

        verticesToSave.add(vertexArrayStack.last());

        vertexArrayStack.popStack();

        vertexArrayStack.pushStack();
        MathHelper.rotateVerticesY(vertexArrayStack.last(), 90);
        MathHelper.translateVertices(vertexArrayStack.last(), 1, 0, 1);

        verticesToSave.add(vertexArrayStack.last());

        vertexArrayStack.popStack();

        vertexArrayStack.pushStack();
        MathHelper.rotateVerticesY(vertexArrayStack.last(), 180);
        MathHelper.translateVertices(vertexArrayStack.last(), 1, 0, 0);

        verticesToSave.add(vertexArrayStack.last());

        vertexArrayStack.popStack();

        vertexArrayStack.pushStack();
        MathHelper.rotateVerticesY(vertexArrayStack.last(), -90);
        MathHelper.translateVertices(vertexArrayStack.last(), 0, 0, 0);

        verticesToSave.add(vertexArrayStack.last());

        vertexArrayStack.popStack();

        return verticesToSave;
    }
}
