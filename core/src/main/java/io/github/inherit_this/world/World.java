package io.github.inherit_this.world;

import java.util.HashMap;
import java.util.Map;
import io.github.inherit_this.util.Constants;

public class World {

    private Map<Long, Chunk> chunks = new HashMap<>();

    public World() {
    }

    public Chunk getOrCreateChunk(int chunkX, int chunkY) {
        long key = pack(chunkX, chunkY);
        if (!chunks.containsKey(key)) {
            chunks.put(key, new Chunk(chunkX, chunkY, "grass"));
        }
        return chunks.get(key);
    }

    public Tile getTileAtWorldCoords(int worldTileX, int worldTileY) {
        int chunkX = Math.floorDiv(worldTileX, Constants.CHUNK_SIZE);
        int chunkY = Math.floorDiv(worldTileY, Constants.CHUNK_SIZE);
        Chunk chunk = getOrCreateChunk(chunkX, chunkY);

        int localX = Math.floorMod(worldTileX, Constants.CHUNK_SIZE);
        int localY = Math.floorMod(worldTileY, Constants.CHUNK_SIZE);

        return chunk.getTile(localX, localY);
    }

    public Tile getTileAtPosition(float worldX, float worldY) {
        int tileX = (int) Math.floor(worldX / Constants.TILE_SIZE);
        int tileY = (int) Math.floor(worldY / Constants.TILE_SIZE);
        return getTileAtWorldCoords(tileX, tileY);
    }

    public boolean isSolidAtPosition(float worldX, float worldY) {
        Tile tile = getTileAtPosition(worldX, worldY);
        return tile != null && tile.isSolid();
    }

    public void reloadChunk(int chunkX, int chunkY) {
        long key = pack(chunkX, chunkY);
        chunks.remove(key);
        chunks.put(key, new Chunk(chunkX, chunkY, "grass"));
    }

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

    private long pack(int x, int y) {
        return (((long)x) << 32) | (y & 0xffffffffL);
    }
}
