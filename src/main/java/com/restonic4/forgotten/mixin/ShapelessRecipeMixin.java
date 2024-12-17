package com.restonic4.forgotten.mixin;

import com.restonic4.forgotten.registries.common.ForgottenItems;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShapelessRecipe.class)
public class ShapelessRecipeMixin {
    @Shadow @Final private ItemStack result;

    @Inject(method = "assemble(Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/core/RegistryAccess;)Lnet/minecraft/world/item/ItemStack;", at = @At("HEAD"), cancellable = true)
    public void assemble(CraftingContainer craftingContainer, RegistryAccess registryAccess, CallbackInfoReturnable<ItemStack> cir) {
        if (this.result.is(ForgottenItems.ETHEREAL_WRITTEN_BOOK)) {
            ItemStack writtenBook = getDesiredItemInSlots(craftingContainer, Items.WRITTEN_BOOK);

            CompoundTag originalBookTag = writtenBook.getTag();

            ItemStack modWrittenBook = new ItemStack(ForgottenItems.ETHEREAL_WRITTEN_BOOK);
            if (originalBookTag != null) {
                modWrittenBook.setTag(originalBookTag);
            }

            cir.setReturnValue(modWrittenBook);
            cir.cancel();
        }
    }

    @Unique
    private ItemStack getDesiredItemInSlots(CraftingContainer craftingContainer, Item item) {
        for (int i = 0; i < craftingContainer.getContainerSize(); i++) {
            if (craftingContainer.getItem(i).is(item)) {
                return craftingContainer.getItem(i);
            }
        }

        return craftingContainer.getItem(0);
    }
}
