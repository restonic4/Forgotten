package com.restonic4.forgotten.mixin.client;

import com.restonic4.forgotten.client.gui.EtherealBookViewScreen;
import com.restonic4.forgotten.registries.common.ForgottenItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundOpenBookPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "handleOpenBook", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;",
            shift = At.Shift.AFTER
    ), cancellable = true)
    public void handleOpenBook(ClientboundOpenBookPacket clientboundOpenBookPacket, CallbackInfo ci) {
        ItemStack itemStack = this.minecraft.player.getItemInHand(clientboundOpenBookPacket.getHand());
        if (itemStack.is(ForgottenItems.ETHEREAL_WRITTEN_BOOK)) {
            this.minecraft.setScreen(new EtherealBookViewScreen(new BookViewScreen.WrittenBookAccess(itemStack)));
            ci.cancel();
        }
    }
}
