package com.restonic4.forgotten.saving;

import com.restonic4.forgotten.entity.common.ChainEntity;
import com.restonic4.forgotten.entity.common.SmallCoreEntity;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.CompoundTag;

public class SmallCoreComponent implements SmallCoreInterface, AutoSyncedComponent {
    private int index;

    private final SmallCoreEntity provider;

    public SmallCoreComponent(SmallCoreEntity provider) {
        this.provider = provider;
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    @Override
    public void setIndex(int value) {
        this.index = value;
        Components.SMALL_CORE.sync(this.provider);
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        if (tag.contains("Index")) {
            this.index = tag.getInt("Index");
        }

        Components.SMALL_CORE.sync(this.provider);
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.putInt("Index", getIndex());
    }
}
