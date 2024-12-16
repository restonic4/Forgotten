package com.restonic4.forgotten.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class EtherealWrittenBookAccess extends BookViewScreen.WrittenBookAccess {
    public EtherealWrittenBookAccess(ItemStack itemStack) {
        super(itemStack);
    }
}
