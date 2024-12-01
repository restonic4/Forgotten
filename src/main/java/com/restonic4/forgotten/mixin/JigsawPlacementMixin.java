package com.restonic4.forgotten.mixin;

import com.restonic4.forgotten.Forgotten;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(JigsawPlacement.class)
public class JigsawPlacementMixin {
    @Unique private static ResourceLocation currentJigsawLocation;

    @Inject(
            method = "addPieces(Lnet/minecraft/world/level/levelgen/structure/Structure$GenerationContext;Lnet/minecraft/core/Holder;Ljava/util/Optional;ILnet/minecraft/core/BlockPos;ZLjava/util/Optional;I)Ljava/util/Optional;",
            at = @At("HEAD")
    )
    private static void addPieces(Structure.GenerationContext generationContext, Holder<StructureTemplatePool> holder, Optional<ResourceLocation> optional, int i, BlockPos blockPos, boolean bl, Optional<Heightmap.Types> optional2, int j, CallbackInfoReturnable<Optional<Structure.GenerationStub>> cir) {
        optional.ifPresent(resourceLocation -> currentJigsawLocation = (ResourceLocation) resourceLocation);
    }

    @ModifyVariable(
            method = "addPieces(Lnet/minecraft/world/level/levelgen/structure/Structure$GenerationContext;Lnet/minecraft/core/Holder;Ljava/util/Optional;ILnet/minecraft/core/BlockPos;ZLjava/util/Optional;I)Ljava/util/Optional;",
            at = @At("STORE"),
            ordinal = 0
    )
    private static Rotation modifyRotation(Rotation original) {
        if (currentJigsawLocation != null && currentJigsawLocation.getNamespace().equals(Forgotten.MOD_ID)) {
            if (currentJigsawLocation.getPath().equals("main_temple")) {
                return Rotation.NONE;
            }
        }

        return original;
    }
}
