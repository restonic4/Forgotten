package com.restonic4.forgotten.item;

import com.restonic4.forgotten.client.gui.EtherealSendingScreen;
import com.restonic4.forgotten.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class EtherealWrittenBook extends WrittenBookItem {
    public EtherealWrittenBook(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext useOnContext) {
        return super.useOn(useOnContext);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand interactionHand) {
        boolean couldOpenMenu = openSendMenuIfPossible(player);

        return (couldOpenMenu) ?
                InteractionResultHolder.sidedSuccess(player.getItemInHand(interactionHand), level.isClientSide()) :
                super.use(level, player, interactionHand);
    }

    private boolean openSendMenuIfPossible(Player player) {
        if (player == null) {
            return false;
        }

        if (player.isShiftKeyDown()) {
            if (player.level().isClientSide()) {
                GuiHelper.openEtherealBook();
            } else {
                // sync shit or something
            }

            return true;
        }

        return false;
    }
}
