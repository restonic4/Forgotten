package com.restonic4.forgotten.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public class GriefingPrevention {
    public static Map<SafeBlockPos, BlockState> originalBlocks = new HashMap<>();

    public static void register() {

    }

    public static BlockState getOriginalBlockAndRegister(BlockPos blockPos, BlockState blockStateFallBack) {
        if (originalBlocks.containsKey(new SafeBlockPos(blockPos))) {
            return originalBlocks.get(new SafeBlockPos(blockPos));
        } else {
            originalBlocks.put(new SafeBlockPos(blockPos), blockStateFallBack);
            return blockStateFallBack;
        }
    }
}
