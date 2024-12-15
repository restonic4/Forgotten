package com.restonic4.forgotten.mixin;

import com.restonic4.forgotten.registries.common.ForgottenItems;
import net.minecraft.network.protocol.game.ClientboundOpenBookPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WrittenBookItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
    @Shadow public ServerGamePacketListenerImpl connection;

    @Inject(method = "openItemGui", at = @At("HEAD"), cancellable = true)
    public void openItemGui(ItemStack itemStack, InteractionHand interactionHand, CallbackInfo ci) {
        if (itemStack.is(ForgottenItems.ETHEREAL_WRITTEN_BOOK)) {
            ServerPlayer current = (ServerPlayer) (Object) this;

            if (WrittenBookItem.resolveBookComponents(itemStack, current.createCommandSourceStack(), current)) {
                current.containerMenu.broadcastChanges();
            }

            this.connection.send(new ClientboundOpenBookPacket(interactionHand));

            ci.cancel();
        }
    }
}
