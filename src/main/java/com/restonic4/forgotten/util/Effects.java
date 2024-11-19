package com.restonic4.forgotten.util;

import com.restonic4.forgotten.registries.ForgottenSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

import java.util.concurrent.ThreadLocalRandom;

public class Effects {
    public static void invalidHeadPlacement(ServerLevel serverLevel, BlockPos blockPos) {
        particle(serverLevel, blockPos, ParticleTypes.TOTEM_OF_UNDYING, 4, 0.2, 0.2, 0.2, 0);
        particle(serverLevel, blockPos, ParticleTypes.ENCHANTED_HIT, 4, 0.2, 0.2, 0.2, 0);
        sound(serverLevel, blockPos, ForgottenSounds.REJECT, SoundSource.PLAYERS, 0.5f, getRandomPitch());
    }

    public static void particle(ServerLevel serverLevel, BlockPos blockPos, ParticleOptions particleOptions, int amount, double xExtrude, double yExtrude, double zExtrude, double velocity) {
        serverLevel.sendParticles(
                particleOptions,
                blockPos.getX() + 0.5, // x
                blockPos.getY() + 0.5, // y
                blockPos.getZ() + 0.5, // z
                amount, // Amount
                xExtrude, // X Offset
                yExtrude, // Y Offset
                zExtrude, // Z Offset
                velocity // Velocity
        );
    }

    private static void sound(ServerLevel serverLevel, BlockPos blockPos, SoundEvent sound, SoundSource soundSource) {
        sound(serverLevel, blockPos, sound, soundSource, 1, 1);
    }

    private static void sound(ServerLevel serverLevel, BlockPos blockPos, SoundEvent sound, SoundSource soundSource, float volume) {
        sound(serverLevel, blockPos, sound, soundSource, volume, 1);
    }

    private static void sound(ServerLevel serverLevel, BlockPos blockPos, SoundEvent sound, SoundSource soundSource, float volume, float pitch) {
        serverLevel.playSound(
                null,
                blockPos,
                sound,
                soundSource,
                volume,
                pitch
        );
    }

    private static float getRandomPitch() {
        return getRandomPitch(0.75f, 1.25f);
    }

    private static float getRandomPitch(float min, float max) {
        return ThreadLocalRandom.current().nextFloat() * (max - min) + min;
    }
}
