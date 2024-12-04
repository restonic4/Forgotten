package com.restonic4.forgotten.saving;

import com.restonic4.forgotten.entity.common.ChainEntity;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.CompoundTag;

public class ChainStateComponent implements ChainStateInterface, AutoSyncedComponent {
    private boolean isAlt, isVertical, isRotated, isDed;
    private int index;

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
    public boolean isRotated() {
        return this.isRotated;
    }

    @Override
    public void setRotated(boolean value) {
        this.isRotated = value;
        Components.CHAIN_STATE.sync(this.provider);
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    @Override
    public void setIndex(int value) {
        this.index = value;
        Components.CHAIN_STATE.sync(this.provider);
    }

    @Override
    public boolean isDed() {
        return this.isDed;
    }

    @Override
    public void setDed(boolean value) {
        this.isDed = value;
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

        if (tag.contains("IsRotated")) {
            this.isRotated = tag.getBoolean("IsRotated");
        }

        if (tag.contains("Index")) {
            this.index = tag.getInt("Index");
        }

        if (tag.contains("IsDed")) {
            this.isDed = tag.getBoolean("IsDed");
        }

        Components.CHAIN_STATE.sync(this.provider);
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.putBoolean("IsVertical", isVertical());
        tag.putBoolean("IsAlt", isAlt());
        tag.putBoolean("IsRotated", isRotated());
        tag.putInt("Index", getIndex());
        tag.putBoolean("IsDed", isDed());
    }
}
