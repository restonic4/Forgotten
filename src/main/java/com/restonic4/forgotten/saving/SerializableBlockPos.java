package com.restonic4.forgotten.saving;

import net.minecraft.core.BlockPos;

import java.io.Serializable;

public class SerializableBlockPos implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int x, y, z;

    public SerializableBlockPos(BlockPos blockPos) {
        this.x = blockPos.getX();
        this.y = blockPos.getY();
        this.z = blockPos.getZ();
    }

    public BlockPos toBlockPos() {
        return new BlockPos(x, y, z);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }
}
