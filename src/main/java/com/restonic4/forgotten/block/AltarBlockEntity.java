package com.restonic4.forgotten.block;

import com.restonic4.forgotten.registries.common.ForgottenBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class AltarBlockEntity extends BlockEntity {
    private ItemStack storedItem = ItemStack.EMPTY;

    public AltarBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public AltarBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ForgottenBlocks.ALTAR_BLOCK_ENTITY, blockPos, blockState);
    }

    public ItemStack getStoredItem() {
        return storedItem;
    }

    public void setStoredItem(ItemStack item) {
        this.storedItem = item;
        setChanged();
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        storedItem = ItemStack.of(tag.getCompound("StoredItem"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("StoredItem", storedItem.save(new CompoundTag()));
    }
}
