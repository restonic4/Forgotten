package com.restonic4.forgotten.mixin;

import com.restonic4.forgotten.registries.common.ForgottenItems;
import net.minecraft.network.protocol.game.ClientboundOpenBookPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WrittenBookItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;

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

    @Inject(method = "openMenu", at = @At("HEAD"), cancellable = true)
    public void openMenu(MenuProvider menuProvider, CallbackInfoReturnable<OptionalInt> cir) {
        ServerPlayer current = (ServerPlayer) (Object) this;

        System.out.println("Menu provider: " + menuProvider.getDisplayName());
        System.out.println(current.containerMenu);
        System.out.println(current.inventoryMenu);
        System.out.println(current.containerMenu != current.inventoryMenu);
    }

    @Inject(
            method = "openMenu(Lnet/minecraft/world/MenuProvider;)Ljava/util/OptionalInt;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/MenuProvider;createMenu(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/inventory/AbstractContainerMenu;",
                    shift = At.Shift.AFTER
            )
    )
    public void openMenuPoint1(MenuProvider menuProvider, CallbackInfoReturnable<OptionalInt> cir) {
        ServerPlayer current = (ServerPlayer) (Object) this;
        AbstractContainerMenu abstractContainerMenu = menuProvider.createMenu(current.containerCounter, current.getInventory(), current);
        System.out.println(abstractContainerMenu);
    }
}
