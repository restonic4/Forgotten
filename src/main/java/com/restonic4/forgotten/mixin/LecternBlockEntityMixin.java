package com.restonic4.forgotten.mixin;

import com.restonic4.forgotten.registries.common.ForgottenItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LecternBlockEntity.class)
public class LecternBlockEntityMixin {
    @Shadow private ItemStack book;

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
}
