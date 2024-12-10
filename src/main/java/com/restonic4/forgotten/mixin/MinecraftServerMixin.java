package com.restonic4.forgotten.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    /*@Shadow @Final protected WorldData worldData;

    @ModifyConstant(method = "prepareLevels", constant = @Constant(intValue = 11))
    private int prepareLevels(int original) {
        return 4;
    }

    @ModifyConstant(
            method = "prepareLevels",
            constant = @Constant(intValue = 441)
    )
    int modifyChunkCount(int constant) {
        int radius = 4;
        return radius > 0 ? Mth.square(calculateDiameter(radius)) : 0;
    }

    @Unique
    private static int calculateDiameter(int radius) {
        return 2 * radius + 1;
    }*/

    @Unique
    private static final int FIXED_RADIUS = 4;

    @Shadow @Final protected WorldData worldData;

    @Redirect(
            method = "loadLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/progress/ChunkProgressListenerFactory;create(I)Lnet/minecraft/server/level/progress/ChunkProgressListener;"
            )
    )
    ChunkProgressListener redirectCreateChunkProgressListener(ChunkProgressListenerFactory instance, int i) {
        return instance.create(calculateExpectedChunks());
    }

    @Redirect(
            method = "prepareLevels",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerChunkCache;addRegionTicket(Lnet/minecraft/server/level/TicketType;Lnet/minecraft/world/level/ChunkPos;ILjava/lang/Object;)V"
            )
    )
    void redirectAddRegionTicket(ServerChunkCache instance, TicketType<?> type, ChunkPos pos, int radius, Object value) {
        instance.addRegionTicket(TicketType.START, pos, calculateDiameter(FIXED_RADIUS), Unit.INSTANCE);
    }

    @ModifyConstant(
            method = "prepareLevels",
            constant = @Constant(intValue = 441)
    )
    int modifyExpectedChunkCount(int original) {
        return calculateExpectedChunks();
    }

    @Unique
    private static int calculateExpectedChunks() {
        return Mth.square(calculateDiameter(FIXED_RADIUS));
    }

    @Unique
    private static int calculateDiameter(int radius) {
        return 2 * radius + 1;
    }
}
