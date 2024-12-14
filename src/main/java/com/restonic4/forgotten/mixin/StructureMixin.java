package com.restonic4.forgotten.mixin;

import net.minecraft.world.level.levelgen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;

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
