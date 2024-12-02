package com.restonic4.forgotten.mixin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.restonic4.forgotten.mixin.accessor.JigsawStructureAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.world.level.levelgen.structure.Structure.settingsCodec;

@Mixin(JigsawStructure.class)
public class JigsawStructureMixin {
    @Mutable @Final @Shadow public static int MAX_TOTAL_STRUCTURE_RANGE;
    @Mutable @Final @Shadow public static Codec<JigsawStructure> CODEC;

    @Unique private static int NEW_DISTANCE_MAX = 256;
    @Unique private static int NEW_LEVEL_MAX = 20;

    @ModifyConstant(
            method = "verifyRange",
            constant = @Constant(intValue = 128)
    )
    private static int modifyMaxDistanceLimit(int originalLimit) {
        return NEW_DISTANCE_MAX;
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void modifyMaxTotalStructureRange(CallbackInfo ci) {
        MAX_TOTAL_STRUCTURE_RANGE = NEW_DISTANCE_MAX;
        CODEC = ExtraCodecs.validate(
                        RecordCodecBuilder.mapCodec(
                                instance -> instance.group(
                                                settingsCodec(instance),
                                                StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(jigsawStructure -> jigsawStructure.startPool),
                                                ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(jigsawStructure -> jigsawStructure.startJigsawName),
                                                Codec.intRange(0, NEW_LEVEL_MAX).fieldOf("size").forGetter(jigsawStructure -> jigsawStructure.maxDepth),
                                                HeightProvider.CODEC.fieldOf("start_height").forGetter(jigsawStructure -> jigsawStructure.startHeight),
                                                Codec.BOOL.fieldOf("use_expansion_hack").forGetter(jigsawStructure -> jigsawStructure.useExpansionHack),
                                                Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(jigsawStructure -> jigsawStructure.projectStartToHeightmap),
                                                Codec.intRange(1, NEW_DISTANCE_MAX).fieldOf("max_distance_from_center").forGetter(jigsawStructure -> jigsawStructure.maxDistanceFromCenter)
                                        )
                                        .apply(instance, JigsawStructure::new)
                        ),
                        JigsawStructure::verifyRange
                )
                .codec();
        /*JigsawStructureAccessor.setMaxTotalStructureRange(NEW_DISTANCE_MAX);
        JigsawStructureAccessor.changeCodec(ExtraCodecs.validate(
                        RecordCodecBuilder.mapCodec(
                                instance -> instance.group(
                                                settingsCodec(instance),
                                                StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(jigsawStructure -> jigsawStructure.startPool),
                                                ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(jigsawStructure -> jigsawStructure.startJigsawName),
                                                Codec.intRange(0, NEW_LEVEL_MAX).fieldOf("size").forGetter(jigsawStructure -> jigsawStructure.maxDepth),
                                                HeightProvider.CODEC.fieldOf("start_height").forGetter(jigsawStructure -> jigsawStructure.startHeight),
                                                Codec.BOOL.fieldOf("use_expansion_hack").forGetter(jigsawStructure -> jigsawStructure.useExpansionHack),
                                                Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(jigsawStructure -> jigsawStructure.projectStartToHeightmap),
                                                Codec.intRange(1, NEW_DISTANCE_MAX).fieldOf("max_distance_from_center").forGetter(jigsawStructure -> jigsawStructure.maxDistanceFromCenter)
                                        )
                                        .apply(instance, JigsawStructure::new)
                        ),
                        JigsawStructure::verifyRange
                )
                .codec());*/
    }
}
