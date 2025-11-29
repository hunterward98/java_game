package io.github.inherit_this.world;

import java.util.HashMap;
import java.util.Map;
import io.github.inherit_this.util.Constants;

/**
 * Procedurally generated world for dungeons.
 * Generates terrain on-demand as chunks are requested.
 */
public class ProceduralWorld implements WorldProvider {

    private Map<Long, Chunk> chunks = new HashMap<>();

    public ProceduralWorld() {
    }

    @Override
    public Chunk getOrCreateChunk(int chunkX, int chunkY) {
        long key = pack(chunkX, chunkY);
        if (!chunks.containsKey(key)) {
            chunks.put(key, new Chunk(chunkX, chunkY, "grass"));
        }
        return chunks.get(key);
    }

    @Override
    public Tile getTileAtWorldCoords(int worldTileX, int worldTileY) {
        int chunkX = Math.floorDiv(worldTileX, Constants.CHUNK_SIZE);
        int chunkY = Math.floorDiv(worldTileY, Constants.CHUNK_SIZE);
        Chunk chunk = getOrCreateChunk(chunkX, chunkY);

        int localX = Math.floorMod(worldTileX, Constants.CHUNK_SIZE);
        int localY = Math.floorMod(worldTileY, Constants.CHUNK_SIZE);

        return chunk.getTile(localX, localY);
    }

    @Override
    public Tile getTileAtPosition(float worldX, float worldY) {
        int tileX = (int) Math.floor(worldX / Constants.TILE_SIZE);
        int tileY = (int) Math.floor(worldY / Constants.TILE_SIZE);
        return getTileAtWorldCoords(tileX, tileY);
    }

    @Override
    public boolean isSolidAtPosition(float worldX, float worldY) {
        Tile tile = getTileAtPosition(worldX, worldY);
        return tile != null && tile.isSolid();
    }

    public void reloadChunk(int chunkX, int chunkY) {
        long key = pack(chunkX, chunkY);
        chunks.remove(key);
        chunks.put(key, new Chunk(chunkX, chunkY, "grass"));
    }

    @Override
    public Chunk getActiveChunk(int worldTileX, int worldTileY) {
        int chunkX = Math.floorDiv(worldTileX, Constants.CHUNK_SIZE);
        int chunkY = Math.floorDiv(worldTileY, Constants.CHUNK_SIZE);
        return chunks.get(pack(chunkX, chunkY));
    }

    public void regenerateAll() {
        Map<Long, Chunk> newChunks = new HashMap<>();
        for (Long key : chunks.keySet()) {
            int chunkX = (int)(key >> 32);
            int chunkY = (int)(key.longValue());
            newChunks.put(key, new Chunk(chunkX, chunkY, "grass"));
        }
        chunks = newChunks;
    }

    @Override
    public int preloadChunks(int radius) {
        int count = 0;
        for (int cx = -radius; cx <= radius; cx++) {
            for (int cy = -radius; cy <= radius; cy++) {
                Chunk chunk = getOrCreateChunk(cx, cy);
                // Force build cached models now instead of on first render
                chunk.getCachedModels();
                count++;
            }
        }
        return count;
    }

    @Override
    public int getLoadedChunkCount() {
        return chunks.size();
    }

    @Override
    public void dispose() {
        // Dispose all chunks
        for (Chunk chunk : chunks.values()) {
            chunk.dispose();
        }
        chunks.clear();
    }

    private long pack(int x, int y) {
        return (((long)x) << 32) | (y & 0xffffffffL);
    }
}
