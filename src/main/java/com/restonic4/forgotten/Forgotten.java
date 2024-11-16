package com.restonic4.forgotten;

import com.cartoonishvillain.incapacitated.Incapacitated;
import com.cartoonishvillain.incapacitated.IncapacitatedPlayerData;
import com.cartoonishvillain.incapacitated.events.AbstractedIncapacitation;
import com.cartoonishvillain.incapacitated.platform.Services;
import me.drex.vanish.api.VanishAPI;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class Forgotten implements ModInitializer {
    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {
        if (!FabricLoader.getInstance().isModLoaded("melius-vanish")) {
            throw new RuntimeException("Vanish mod needed to work");
        }

        ServerLivingEntityEvents.AFTER_DEATH.register((LivingEntity livingEntity, DamageSource damageSource) -> {
            if (livingEntity instanceof ServerPlayer serverPlayer) {
                if (FabricLoader.getInstance().isModLoaded("incapacitated")) {
                    IncapacitatedPlayerData incapacitatedPlayerData = Services.PLATFORM.getPlayerData(serverPlayer);

                    if (incapacitatedPlayerData.isIncapacitated()) {
                        System.out.println("Not vanishing, is not dead yet");
                    } else {
                        VanishAPI.setVanish(serverPlayer, true);
                    }
                } else {
                    VanishAPI.setVanish(serverPlayer, true);
                }
            }
        });
    }
}
