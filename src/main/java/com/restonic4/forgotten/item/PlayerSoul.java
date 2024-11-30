package com.restonic4.forgotten.item;

import com.mojang.authlib.GameProfile;
import com.restonic4.forgotten.registries.common.ForgottenItems;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

import java.util.UUID;

public class PlayerSoul extends Item {
    private static final String MAIN_TAG = "PlayerOwner";

    public PlayerSoul(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack itemStack) {
        if (itemStack.is(ForgottenItems.PLAYER_SOUL) && itemStack.hasTag()) {
            String string = null;
            CompoundTag compoundTag = itemStack.getTag();

            if (compoundTag.contains(MAIN_TAG, 8)) {
                string = compoundTag.getString(MAIN_TAG);
            } else if (compoundTag.contains(MAIN_TAG, 10)) {
                CompoundTag compoundTag2 = compoundTag.getCompound(MAIN_TAG);
                if (compoundTag2.contains("Name", 8)) {
                    string = compoundTag2.getString("Name");
                }
            }

            if (string != null) {
                return Component.translatable(this.getDescriptionId() + ".named", new Object[]{string});
            }
        }

        return super.getName(itemStack);
    }

    public void verifyTagAfterLoad(CompoundTag compoundTag) {
        super.verifyTagAfterLoad(compoundTag);
        if (compoundTag.contains(MAIN_TAG, 8) && !Util.isBlank(compoundTag.getString(MAIN_TAG))) {
            GameProfile gameProfile = new GameProfile((UUID)null, compoundTag.getString(MAIN_TAG));
            SkullBlockEntity.updateGameprofile(gameProfile, (gameProfilex) -> {
                compoundTag.put(MAIN_TAG, NbtUtils.writeGameProfile(new CompoundTag(), gameProfilex));
            });
        }
    }
}
