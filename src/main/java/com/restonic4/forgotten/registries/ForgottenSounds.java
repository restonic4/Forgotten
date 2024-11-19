package com.restonic4.forgotten.registries;

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
