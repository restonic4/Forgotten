package com.restonic4.forgotten.util;

import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;

import java.util.ArrayList;
import java.util.List;

public class ChunkManager {
    private static final List<TicketData> tickets = new ArrayList<>();

    public static void scheduleRemoval(ServerChunkCache serverChunkCache, ChunkPos chunkPos, int radius, TicketType<ChunkPos> type) {
        ChunkManager.tickets.add(new TicketData(serverChunkCache, chunkPos, radius, type));
        //serverChunkCache.removeRegionTicket(type, chunkPos, radius, chunkPos);
    }

    public static void cleanup() {
        //TODO: IMPLEMENTATION
    }
}
