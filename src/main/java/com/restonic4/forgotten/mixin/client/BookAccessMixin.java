package com.restonic4.forgotten.mixin.client;

import com.restonic4.forgotten.item.EtherealWrittenBookAccess;
import com.restonic4.forgotten.registries.common.ForgottenItems;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BookViewScreen.BookAccess.class)
public interface BookAccessMixin {
    @Inject(
            method = "fromItem",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void injectCustomBook(ItemStack itemStack, CallbackInfoReturnable<BookViewScreen.BookAccess> cir) {
        if (itemStack.is(ForgottenItems.ETHEREAL_WRITTEN_BOOK)) {
            cir.setReturnValue(new EtherealWrittenBookAccess(itemStack));
        }
    }
}