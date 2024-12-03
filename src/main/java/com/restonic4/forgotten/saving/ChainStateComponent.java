package com.restonic4.forgotten.saving;

import com.restonic4.forgotten.entity.common.ChainEntity;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.CompoundTag;

public class ChainStateComponent implements ChainStateInterface, AutoSyncedComponent {
    private boolean isAlt, isVertical;

    private final ChainEntity provider;

    public ChainStateComponent(ChainEntity provider) {
        this.provider = provider;
    }

    @Override
    public boolean isAlt() {
        return this.isAlt;
    }

    @Override
    public void setAlt(boolean value) {
        this.isAlt = value;
        Components.CHAIN_STATE.sync(this.provider);
    }

    @Override
    public boolean isVertical() {
        return this.isVertical;
    }

    @Override
    public void setVertical(boolean value) {
        this.isVertical = value;
        Components.CHAIN_STATE.sync(this.provider);
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        if (tag.contains("IsAlt")) {
            this.isAlt = tag.getBoolean("IsAlt");
        }

        if (tag.contains("IsVertical")) {
            this.isVertical = tag.getBoolean("IsVertical");
        }

        Components.CHAIN_STATE.sync(this.provider);
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.putBoolean("IsVertical", isVertical());
        tag.putBoolean("IsAlt", isAlt());
    }
}
