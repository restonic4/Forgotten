package com.restonic4.forgotten.saving;

import com.restonic4.forgotten.util.SafeBlockPos;
import net.minecraft.core.BlockPos;

import java.io.Serial;
import java.io.Serializable;

public class PlayerData implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    private SerializableBlockPos defaultSpawnPoint = null;
    private boolean hasBeenSavedFromLethalDamage = false;

    public PlayerData() {}

    public PlayerData(SerializableBlockPos defaultSpawnPoint, boolean hasBeenSavedFromLethalDamage) {
        this.defaultSpawnPoint = defaultSpawnPoint;
        this.hasBeenSavedFromLethalDamage = hasBeenSavedFromLethalDamage;
    }

    public SerializableBlockPos getDefaultSpawnPoint() {
        return defaultSpawnPoint;
    }

    public void setDefaultSpawnPoint(SerializableBlockPos defaultSpawnPoint) {
        this.defaultSpawnPoint = defaultSpawnPoint;
    }

    public boolean hasBeenSavedFromLethalDamage() {
        return hasBeenSavedFromLethalDamage;
    }

    public void setHasBeenSavedFromLethalDamage(boolean hasBeenSavedFromLethalDamage) {
        this.hasBeenSavedFromLethalDamage = hasBeenSavedFromLethalDamage;
    }
}
