package com.restonic4.forgotten.mixin;

import com.restonic4.forgotten.Forgotten;
import com.restonic4.forgotten.saving.JsonDataManager;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.Predicate;

@Mixin(Structure.class)
public class StructureMixin {
    /*@Unique private static boolean hasBeenGenerated = false;

    @Inject(method = "generate", at = @At("HEAD"), cancellable = true)
    public void generate(RegistryAccess registryAccess, ChunkGenerator chunkGenerator, BiomeSource biomeSource, RandomState randomState, StructureTemplateManager structureTemplateManager, long l, ChunkPos chunkPos, int i, LevelHeightAccessor levelHeightAccessor, Predicate<Holder<Biome>> predicate, CallbackInfoReturnable<StructureStart> cir) {
        Structure current = (Structure) (Object) this;

        if (hasBeenGenerated) {
            cir.setReturnValue(StructureStart.INVALID_START);
            cir.cancel();
        }

        Optional<Registry<StructureType<?>>> registry = registryAccess.registry(BuiltInRegistries.STRUCTURE_TYPE.key());
        if (registry.isPresent()) {
            ResourceLocation resourceLocation = registry.get().getKey(current.type());

            if (resourceLocation != null && resourceLocation.getNamespace().equals(Forgotten.MOD_ID)) {
                if (resourceLocation.getPath().equals("main_temple")) {
                    JsonDataManager dataManager = Forgotten.getDataManager();

                    if (dataManager.contains("main_temple")) {
                        hasBeenGenerated = true;

                        cir.setReturnValue(StructureStart.INVALID_START);
                        cir.cancel();
                    } else {
                        Structure.GenerationContext generationContext = new Structure.GenerationContext(
                                registryAccess, chunkGenerator, biomeSource, randomState, structureTemplateManager, l, chunkPos, levelHeightAccessor, predicate
                        );

                        Optional<Structure.GenerationStub> optional = current.findValidGenerationPoint(generationContext);

                        if (optional.isPresent()) {
                            StructurePiecesBuilder structurePiecesBuilder = ((Structure.GenerationStub)optional.get()).getPiecesBuilder();
                            StructureStart structureStart = new StructureStart(current, chunkPos, i, structurePiecesBuilder.build());
                            if (structureStart.isValid()) {
                                System.out.println("Generating the main temple for the first time");

                                dataManager.save("main_temple", chunkPos);

                                cir.setReturnValue(structureStart);
                                cir.cancel();
                            }
                        }
                    }
                }
            }
        }
    }*/
}
