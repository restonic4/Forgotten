package com.restonic4.forgotten.util;

import com.restonic4.forgotten.entity.common.ChainEntity;
import com.restonic4.forgotten.entity.common.CoreEntity;
import com.restonic4.forgotten.entity.common.SmallCoreEntity;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class ServerCache {
    public static final List<ChainEntity> chains = new ArrayList<>();
    public static final List<SmallCoreEntity> cores = new ArrayList<>();
    public static final List<CoreEntity> coresMain = new ArrayList<>();

    public static List<Vec3> repulsionPoints = new ArrayList<>();

    public static void addChainIfPossible(ChainEntity entity) {
        if (chains.contains(entity)) {
            return;
        }

        chains.add(entity);
    }

    public static void addCoreIfPossible(SmallCoreEntity entity) {
        if (cores.contains(entity)) {
            return;
        }

        cores.add(entity);
    }

    public static void addMainCoreIfPossible(CoreEntity entity) {
        if (coresMain.contains(entity)) {
            return;
        }

        coresMain.add(entity);
    }

    public static void addRepulsionPointIfPossible(Vec3 point) {
        if (repulsionPoints.contains(point)) {
            return;
        }

        repulsionPoints.add(point);
    }

    public static void removeRepulsionPointIfPossible(Vec3 point) {
        for (int i = 0; i < repulsionPoints.size(); i++) {
            Vec3 vec3 = repulsionPoints.get(i);
            if (vec3.x == point.x && vec3.y == point.y && vec3.z == point.z) {
                repulsionPoints.remove(i);
                return;
            }
        }
    }

    public static CoreEntity getMainCore() {
        if (coresMain.isEmpty()) {
            return null;
        }

        return coresMain.get(0);
    }
}
