package com.restonic4.forgotten.util;

import com.restonic4.forgotten.entity.common.ChainEntity;
import com.restonic4.forgotten.entity.common.SmallCoreEntity;

import java.util.*;

public class ServerCache {
    public static final List<ChainEntity> chains = new ArrayList<>();
    public static final List<SmallCoreEntity> cores = new ArrayList<>();

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
}
