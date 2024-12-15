package com.restonic4.forgotten.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

public class EtherealWrittenBook extends WrittenBookItem {
    public EtherealWrittenBook(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext useOnContext) {
        System.out.println("Ethereal book used on block");
        return super.useOn(useOnContext);
    }
}
