package com.restonic4.forgotten.util;

import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;

public class TicketData {
    public final ServerChunkCache serverChunkCache;
    public final ChunkPos chunkPos;
    public final int radius;
    public final TicketType<ChunkPos> type;

    public TicketData(ServerChunkCache serverChunkCache, ChunkPos chunkPos, int radius, TicketType<ChunkPos> type) {
        this.serverChunkCache = serverChunkCache;
        this.chunkPos = chunkPos;
        this.radius = radius;
        this.type = type;
    }
}
