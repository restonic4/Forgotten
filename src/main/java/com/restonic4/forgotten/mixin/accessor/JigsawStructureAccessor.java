package com.restonic4.forgotten.mixin.accessor;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(JigsawStructure.class)
public interface JigsawStructureAccessor {
    @Accessor("MAX_TOTAL_STRUCTURE_RANGE")
    static void setMaxTotalStructureRange(int value) {
        throw new AssertionError();
    }

    @Accessor("CODEC")
    static void changeCodec(Codec<JigsawStructure> codec) {
        throw new AssertionError();
    }
}
