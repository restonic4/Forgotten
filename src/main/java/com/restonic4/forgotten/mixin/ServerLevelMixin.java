package com.restonic4.forgotten.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;


@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level {
    /*@ModifyConstant(method = "setDefaultSpawnPos", constant = @Constant(intValue = 11, ordinal = 0))
    private int modifyRemoveSpawnChunkRadius(int original) {
        return 4;
    }

    @ModifyConstant(method = "setDefaultSpawnPos", constant = @Constant(intValue = 11, ordinal = 1))
    private int modifyAddSpawnChunkRadius(int original) {
        return 4;
    }*/

    @Unique
    private static final int FIXED_RADIUS = 4;

    protected ServerLevelMixin(WritableLevelData levelData, ResourceKey<Level> dimension, net.minecraft.core.RegistryAccess registryAccess, Holder<DimensionType> dimensionTypeHolder, Supplier<ProfilerFiller> profilerFillerSupplier, boolean isClientSide, boolean isDebug, long seed, int maxBuildHeight) {
        super(levelData, dimension, registryAccess, dimensionTypeHolder, profilerFillerSupplier, isClientSide, isDebug, seed, maxBuildHeight);
    }

    @Redirect(
            method = "setDefaultSpawnPos",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerChunkCache;addRegionTicket(Lnet/minecraft/server/level/TicketType;Lnet/minecraft/world/level/ChunkPos;ILjava/lang/Object;)V"
            )
    )
    void redirectAddRegionTicket(ServerChunkCache instance, TicketType<?> type, ChunkPos pos, int radius, Object value) {
        instance.addRegionTicket(TicketType.START, pos, FIXED_RADIUS + 1, Unit.INSTANCE);
    }
}
