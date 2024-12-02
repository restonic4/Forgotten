package com.restonic4.forgotten.mixin;

import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JigsawPlacement.Placer.class)
public class JigsawPlacementPlacerMixin {
    @Shadow @Final private int maxDepth;

    @Inject(method = "tryPlacingChildren", at = @At("HEAD"))
    void tryPlacingChildren(PoolElementStructurePiece poolElementStructurePiece, MutableObject<VoxelShape> mutableObject, int i, boolean bl, LevelHeightAccessor levelHeightAccessor, RandomState randomState, CallbackInfo ci) {
        System.out.println("Level: " + i + ", max: " + this.maxDepth);
    }
}
