package com.restonic4.forgotten.mixin.client;

import com.restonic4.forgotten.registries.common.ForgottenItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
    @Shadow @Final protected Minecraft minecraft;

    @Inject(method = "openItemGui", at = @At("HEAD"), cancellable = true)
    public void openItemGui(ItemStack itemStack, InteractionHand interactionHand, CallbackInfo ci) {
        /*if (itemStack.is(ForgottenItems.ETHEREAL_WRITTEN_BOOK)) {
            LocalPlayer current = (LocalPlayer) (Object) this;
            this.minecraft.setScreen(new BookEditScreen(current, itemStack, interactionHand));

            ci.cancel();
        }*/
    }
}
