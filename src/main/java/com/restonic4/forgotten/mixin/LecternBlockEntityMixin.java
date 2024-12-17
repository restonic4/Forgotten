package com.restonic4.forgotten.mixin;

import com.restonic4.forgotten.registries.common.ForgottenItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LecternBlockEntity.class)
public abstract class LecternBlockEntityMixin extends BlockEntity {
    @Shadow private ItemStack book;

    public LecternBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(null, null, null);
    }

    @Inject(method = "resolveBook", at = @At("HEAD"), cancellable = true)
    private void resolveBook(ItemStack itemStack, Player player, CallbackInfoReturnable<ItemStack> cir) {
        LecternBlockEntity current = (LecternBlockEntity) (Object) this;

        if (current.getLevel() instanceof ServerLevel && itemStack.is(ForgottenItems.ETHEREAL_WRITTEN_BOOK)) {
            WrittenBookItem.resolveBookComponents(itemStack, current.createCommandSourceStack(player), player);
            cir.setReturnValue(itemStack);
            cir.cancel();
        }
    }

    @Inject(method = "hasBook", at = @At("HEAD"), cancellable = true)
    public void hasBook(CallbackInfoReturnable<Boolean> cir) {
        if (this.book.is(ForgottenItems.ETHEREAL_WRITTEN_BOOK)) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Inject(method = "setBook(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;)V", at = @At("RETURN"))
    public void setBook(ItemStack itemStack, Player player, CallbackInfo ci) {
        LecternBlockEntity current = (LecternBlockEntity) (Object) this;
        if (current.getLevel() != null) {
            syncWithClients(current.getLevel(), current, current.getBlockState(), current.getBlockPos());
        }
    }

    @Inject(method = "onBookItemRemove", at = @At("RETURN"))
    void onBookItemRemove(CallbackInfo ci) {
        LecternBlockEntity current = (LecternBlockEntity) (Object) this;
        if (current.getLevel() != null) {
            syncWithClients(current.getLevel(), current, current.getBlockState(), current.getBlockPos());
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        this.saveAdditional(tag);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Unique
    private void syncWithClients(Level level, BlockEntity blockEntity, BlockState blockState, BlockPos blockPos) {
        blockEntity.setChanged();
        level.sendBlockUpdated(blockPos, blockState, blockState, 3);
    }
}
