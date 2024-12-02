package com.restonic4.forgotten.mixin;

import com.restonic4.forgotten.Forgotten;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(JigsawPlacement.class)
public class JigsawPlacementMixin {
    @Unique private static ResourceLocation currentJigsawLocation;

    @Inject(
            method = "addPieces(Lnet/minecraft/world/level/levelgen/structure/Structure$GenerationContext;Lnet/minecraft/core/Holder;Ljava/util/Optional;ILnet/minecraft/core/BlockPos;ZLjava/util/Optional;I)Ljava/util/Optional;",
            at = @At("HEAD")
    )
    private static void addPieces(Structure.GenerationContext generationContext, Holder<StructureTemplatePool> holder, Optional<ResourceLocation> optional, int i, BlockPos blockPos, boolean bl, Optional<Heightmap.Types> optional2, int j, CallbackInfoReturnable<Optional<Structure.GenerationStub>> cir) {
        System.out.println("Level: " + i + ", MaxDistance: " + j + ", Part: " + currentJigsawLocation);
    }

    @Inject(
            method = "addPieces(Lnet/minecraft/world/level/levelgen/RandomState;IZLnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;Lnet/minecraft/world/level/LevelHeightAccessor;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/Registry;Lnet/minecraft/world/level/levelgen/structure/PoolElementStructurePiece;Ljava/util/List;Lnet/minecraft/world/phys/shapes/VoxelShape;)V",
            at = @At("HEAD")
    )
    private static void addPiecesIndividual(RandomState randomState, int i, boolean bl, ChunkGenerator chunkGenerator, StructureTemplateManager structureTemplateManager, LevelHeightAccessor levelHeightAccessor, RandomSource randomSource, Registry<StructureTemplatePool> registry, PoolElementStructurePiece poolElementStructurePiece, List<PoolElementStructurePiece> list, VoxelShape voxelShape, CallbackInfo ci) {
        System.out.println("Level: " + i + ", Part: " + currentJigsawLocation);
    }

    @ModifyVariable(
            method = "addPieces(Lnet/minecraft/world/level/levelgen/structure/Structure$GenerationContext;Lnet/minecraft/core/Holder;Ljava/util/Optional;ILnet/minecraft/core/BlockPos;ZLjava/util/Optional;I)Ljava/util/Optional;",
            at = @At("STORE"),
            ordinal = 0
    )
    private static Rotation modifyRotation(Rotation original) {
        if (shouldUpgrade()) {
            return Rotation.NONE;
        }

        return original;
    }

    @ModifyVariable(
            method = "addPieces(Lnet/minecraft/world/level/levelgen/structure/Structure$GenerationContext;Lnet/minecraft/core/Holder;Ljava/util/Optional;ILnet/minecraft/core/BlockPos;ZLjava/util/Optional;I)Ljava/util/Optional;",
            at = @At("HEAD"),
            ordinal = 1,
            argsOnly = true)
    private static int modifyAddPiecesMaxDistance(int maxDistanceFromCenter) {
        if (shouldUpgrade()) {
            return Math.max(maxDistanceFromCenter, 200);
        }
        return maxDistanceFromCenter;
    }

    @ModifyVariable(
            method = "generateJigsaw",
            at = @At("HEAD"),
            ordinal = 0,
            argsOnly = true)
    private static int modifyGenerateJigsawDepth(int depth) {
        if (shouldUpgrade()) {
            return Math.max(depth, 20);
        }
        return depth;
    }

    @Inject(method = "generateJigsaw", at = @At("HEAD"))
    private static void generateJigsaw(ServerLevel serverLevel, Holder<StructureTemplatePool> holder, ResourceLocation resourceLocation, int i, BlockPos blockPos, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        currentJigsawLocation = resourceLocation;
        System.out.println("Level: " + i + ", Part: " + currentJigsawLocation);
    }

    @Unique private static boolean shouldUpgrade() {
        if (currentJigsawLocation != null && currentJigsawLocation.getNamespace().equals(Forgotten.MOD_ID)) {
            return currentJigsawLocation.getPath().endsWith("_fixed");
        }

        return false;
    }
}
