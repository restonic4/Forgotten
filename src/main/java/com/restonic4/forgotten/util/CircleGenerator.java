package com.restonic4.forgotten.util;

import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class CircleGenerator {
    public static class CirclePoint {
        public final Vec3 position;
        public final Vec3 toCenter;

        public CirclePoint(Vec3 position, Vec3 toCenter) {
            this.position = position;
            this.toCenter = toCenter;
        }
    }

    public static List<CirclePoint> generateCircle(float radius, int precision) {
        List<CirclePoint> points = new ArrayList<>();
        float angleStep = (float) (2 * Math.PI / precision);

        for (int i = 0; i < precision; i++) {
            float angle = i * angleStep;

            float x = (float) Math.cos(angle) * radius;
            float y = (float) Math.sin(angle) * radius;

            Vec3 position = new Vec3(x, y, 0);

            Vec3 toCenter = new Vec3(-x, -y, 0).normalize();

            points.add(new CirclePoint(position, toCenter));
        }

        return points;
    }
}
