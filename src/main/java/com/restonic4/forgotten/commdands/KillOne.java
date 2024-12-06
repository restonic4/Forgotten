package com.restonic4.forgotten.commdands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.restonic4.forgotten.entity.common.ChainEntity;
import com.restonic4.forgotten.entity.common.CoreEntity;
import com.restonic4.forgotten.entity.common.SmallCoreEntity;
import com.restonic4.forgotten.util.ServerCache;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;

public class KillOne {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("kill_one_forgotten")
                        .requires(source -> source.hasPermission(2)).executes(KillOne::execute)
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        SmallCoreEntity entity = getSmallCore();

        if (entity == null) {
            return 0;
        }

        Holder<DamageType> holder = (Holder<DamageType>)source.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolder(DamageTypes.GENERIC).get();
        entity.hurt(new DamageSource(holder, null, null), 1);

        return 1;
    }

    private static SmallCoreEntity getSmallCore() {
        for (SmallCoreEntity smallCoreEntity : ServerCache.cores) {
            if (!smallCoreEntity.done) {
                return smallCoreEntity;
            }
        }

        return null;
    }
}
