package com.restonic4.forgotten.registries.common;

import com.restonic4.forgotten.Forgotten;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ForgottenSounds {
    public static SoundEvent DEATH_SOUND;
    public static SoundEvent REJECT;
    public static SoundEvent WAVE;
    public static SoundEvent EXPLOSION;
    public static SoundEvent WAVE_EXPLOSION;

    public static SoundEvent FIREBALL1;
    public static SoundEvent FIREBALL2;

    public static SoundEvent MAIN_RITUAL_BACKGROUND;

    public static SoundEvent BEAM;
    public static SoundEvent BEAM_STEREO;

    public static SoundEvent WHISPER1;
    public static SoundEvent WHISPER2;
    public static SoundEvent WHISPER3;
    public static SoundEvent WHISPER4;
    public static SoundEvent WHISPER5;
    public static SoundEvent WHISPER6;
    public static SoundEvent WHISPER7;
    public static SoundEvent WHISPER8;

    public static void register() {
        ResourceLocation deathLocation = new ResourceLocation(Forgotten.MOD_ID, "death");
        DEATH_SOUND = Registry.register(BuiltInRegistries.SOUND_EVENT, deathLocation, SoundEvent.createVariableRangeEvent(deathLocation));

        ResourceLocation rejectLocation = new ResourceLocation(Forgotten.MOD_ID, "reject");
        REJECT = Registry.register(BuiltInRegistries.SOUND_EVENT, rejectLocation, SoundEvent.createVariableRangeEvent(rejectLocation));

        ResourceLocation waveLocation = new ResourceLocation(Forgotten.MOD_ID, "wave");
        WAVE = Registry.register(BuiltInRegistries.SOUND_EVENT, waveLocation, SoundEvent.createVariableRangeEvent(waveLocation));

        ResourceLocation explosionLocation = new ResourceLocation(Forgotten.MOD_ID, "explosion");
        EXPLOSION = Registry.register(BuiltInRegistries.SOUND_EVENT, explosionLocation, SoundEvent.createVariableRangeEvent(explosionLocation));

        ResourceLocation fireball1Location = new ResourceLocation(Forgotten.MOD_ID, "fireball1");
        FIREBALL1 = Registry.register(BuiltInRegistries.SOUND_EVENT, fireball1Location, SoundEvent.createVariableRangeEvent(fireball1Location));
        ResourceLocation fireball2Location = new ResourceLocation(Forgotten.MOD_ID, "fireball2");
        FIREBALL2 = Registry.register(BuiltInRegistries.SOUND_EVENT, fireball2Location, SoundEvent.createVariableRangeEvent(fireball2Location));

        ResourceLocation mainRitualBackgroundLocation = new ResourceLocation(Forgotten.MOD_ID, "main_ritual_background");
        MAIN_RITUAL_BACKGROUND = Registry.register(BuiltInRegistries.SOUND_EVENT, mainRitualBackgroundLocation, SoundEvent.createVariableRangeEvent(mainRitualBackgroundLocation));

        ResourceLocation beamLocation = new ResourceLocation(Forgotten.MOD_ID, "beam");
        BEAM = Registry.register(BuiltInRegistries.SOUND_EVENT, beamLocation, SoundEvent.createVariableRangeEvent(beamLocation));
        ResourceLocation beamStereoLocation = new ResourceLocation(Forgotten.MOD_ID, "beam_stereo");
        BEAM_STEREO = Registry.register(BuiltInRegistries.SOUND_EVENT, beamStereoLocation, SoundEvent.createVariableRangeEvent(beamStereoLocation));

        ResourceLocation waveExplosionLocation = new ResourceLocation(Forgotten.MOD_ID, "wave_explosion");
        WAVE_EXPLOSION = Registry.register(BuiltInRegistries.SOUND_EVENT, waveExplosionLocation, SoundEvent.createVariableRangeEvent(waveExplosionLocation));

        WHISPER1 = registerWhisper(1);
        WHISPER2 = registerWhisper(2);
        WHISPER3 = registerWhisper(3);
        WHISPER4 = registerWhisper(4);
        WHISPER5 = registerWhisper(5);
        WHISPER6 = registerWhisper(6);
        WHISPER7 = registerWhisper(7);
        WHISPER8 = registerWhisper(8);
    }

    private static SoundEvent registerWhisper(int number) {
        ResourceLocation whisperLocation = new ResourceLocation(Forgotten.MOD_ID, "whisper" + number);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, whisperLocation, SoundEvent.createVariableRangeEvent(whisperLocation));
    }

    public static SoundEvent getRandomWhisper() {
        List<SoundEvent> whispers = Arrays.asList(
                WHISPER1,
                WHISPER2,
                WHISPER3,
                WHISPER4,
                WHISPER5,
                WHISPER6,
                WHISPER7,
                WHISPER8
        );

        int randomIndex = new Random().nextInt(whispers.size());
        return whispers.get(randomIndex);
    }
}
