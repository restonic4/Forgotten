package com.restonic4.forgotten.registries.common;

import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.item.EtherealFragment;
import com.restonic4.forgotten.item.EtherealWrittenBook;
import com.restonic4.forgotten.item.InvincibleItem;
import com.restonic4.forgotten.item.PlayerSoul;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;


public class ForgottenItems {
    public static final PlayerSoul PLAYER_SOUL = Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Forgotten.MOD_ID, "player_soul"),
            new PlayerSoul(
                    new Item.Properties()
                            .stacksTo(1)
                            .rarity(Rarity.EPIC)
                            .fireResistant()
            )
    );

    public static final EtherealFragment ETHEREAL_SHARD = Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Forgotten.MOD_ID, "ethereal_shard"),
            new EtherealFragment(
                    new Item.Properties()
                            .stacksTo(1)
                            .rarity(Rarity.EPIC)
                            .fireResistant()
            )
    );

    public static final EtherealFragment ETHEREAL_NUGGET = Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Forgotten.MOD_ID, "ethereal_nugget"),
            new EtherealFragment(
                    new Item.Properties()
                            .stacksTo(4)
                            .rarity(Rarity.EPIC)
                            .fireResistant()
            )
    );

    public static final EtherealWrittenBook ETHEREAL_WRITTEN_BOOK = Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Forgotten.MOD_ID, "ethereal_written_book"),
            new EtherealWrittenBook(
                    new Item.Properties()
                            .stacksTo(16)
                            .rarity(Rarity.EPIC)
                            .fireResistant()
            )
    );

    public static void register() {
        System.out.println("Items yippie!");
    }
}
