package io.github.inherit_this.world;

import com.badlogic.gdx.graphics.Texture;
import io.github.inherit_this.util.Constants;
import java.util.HashMap;
import java.util.Map;

/**
 * Procedural dungeon world that generates dungeon chunks based on a DungeonGenerator.
 * Unlike ProceduralWorld which generates infinite terrain, DungeonWorld has fixed bounds.
 */
public class DungeonWorld implements WorldProvider {

    private final DungeonConfig config;
    private final DungeonGenerator generator;
    private final Map<Long, Chunk> chunks = new HashMap<>();
    private final TileTextureManager textureManager = TileTextureManager.getInstance();

    // Dungeon bounds in chunks
    private final int minChunkX = 0;
    private final int minChunkY = 0;
    private final int maxChunkX;
    private final int maxChunkY;

    public DungeonWorld(DungeonConfig config) {
        this.config = config;
        this.generator = new DungeonGenerator(config);
        this.maxChunkX = config.getWidthInChunks() - 1;
        this.maxChunkY = config.getHeightInChunks() - 1;

        // Generate the dungeon layout
        generator.generate();
    }

    @Override
    public Chunk getOrCreateChunk(int chunkX, int chunkY) {
        // Check bounds
        if (chunkX < minChunkX || chunkX > maxChunkX ||
            chunkY < minChunkY || chunkY > maxChunkY) {
            // Return empty chunk or null for out-of-bounds
            return null;
        }

        long key = pack(chunkX, chunkY);
        if (!chunks.containsKey(key)) {
            chunks.put(key, generateDungeonChunk(chunkX, chunkY));
        }
        return chunks.get(key);
    }

    /**
     * Generate a dungeon chunk based on the dungeon layout.
     */
    private Chunk generateDungeonChunk(int chunkX, int chunkY) {
        Tile[][] tiles = new Tile[Constants.CHUNK_SIZE][Constants.CHUNK_SIZE];

        // Get textures
        Texture floorTexture = textureManager.getTexture("tiles/stone_1.png");
        Texture wallTexture = textureManager.getTexture("tiles/wood_wall.png");

        // Calculate world tile offset for this chunk
        int baseTileX = chunkX * Constants.CHUNK_SIZE;
        int baseTileY = chunkY * Constants.CHUNK_SIZE;

        // Generate tiles based on dungeon layout
        for (int localX = 0; localX < Constants.CHUNK_SIZE; localX++) {
            for (int localY = 0; localY < Constants.CHUNK_SIZE; localY++) {
                int worldTileX = baseTileX + localX;
                int worldTileY = baseTileY + localY;

                boolean isWall = generator.isWall(worldTileX, worldTileY);
                boolean isBorder = generator.isBorder(worldTileX, worldTileY);

                if (isWall) {
                    if (isBorder) {
                        // Border walls - 2 tiles tall (level 0 and level 1)
                        // We'll need to handle this properly with multiple layers
                        // For now, create a wall at level 0
                        tiles[localX][localY] = new Tile(wallTexture, true, TileType.STONE,
                            TileLayer.WALL, -1f, 0, 0);
                    } else {
                        // Regular wall
                        tiles[localX][localY] = new Tile(wallTexture, true, TileType.STONE,
                            TileLayer.WALL, -1f, 0, 0);
                    }
                } else {
                    // Floor
                    tiles[localX][localY] = new Tile(floorTexture, false, TileType.STONE,
                        TileLayer.GROUND, -1f, 0, 0);
                }
            }
        }

        // Create custom chunk with our tiles
        return new DungeonChunk(chunkX, chunkY, tiles, generator);
    }

    @Override
    public Tile getTileAtWorldCoords(int worldTileX, int worldTileY) {
        int chunkX = Math.floorDiv(worldTileX, Constants.CHUNK_SIZE);
        int chunkY = Math.floorDiv(worldTileY, Constants.CHUNK_SIZE);
        Chunk chunk = getOrCreateChunk(chunkX, chunkY);

        if (chunk == null) {
            return null;
        }

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

    @Override
    public Chunk getActiveChunk(int worldTileX, int worldTileY) {
        int chunkX = Math.floorDiv(worldTileX, Constants.CHUNK_SIZE);
        int chunkY = Math.floorDiv(worldTileY, Constants.CHUNK_SIZE);
        return chunks.get(pack(chunkX, chunkY));
    }

    @Override
    public int preloadChunks(int radius) {
        int centerX = config.getWidthInChunks() / 2;
        int centerY = config.getHeightInChunks() / 2;

        int count = 0;
        for (int cx = centerX - radius; cx <= centerX + radius; cx++) {
            for (int cy = centerY - radius; cy <= centerY + radius; cy++) {
                if (cx >= minChunkX && cx <= maxChunkX &&
                    cy >= minChunkY && cy <= maxChunkY) {
                    Chunk chunk = getOrCreateChunk(cx, cy);
                    if (chunk != null) {
                        chunk.getCachedModels();
                        count++;
                    }
                }
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
        for (Chunk chunk : chunks.values()) {
            chunk.dispose();
        }
        chunks.clear();
    }

    private long pack(int x, int y) {
        return (((long)x) << 32) | (y & 0xffffffffL);
    }

    public DungeonConfig getConfig() {
        return config;
    }

    public DungeonGenerator getGenerator() {
        return generator;
    }

    /**
     * Get the spawn position (center of dungeon).
     */
    public int[] getSpawnPosition() {
        int centerTileX = generator.getWidthInTiles() / 2;
        int centerTileY = generator.getHeightInTiles() / 2;

        // Find nearest floor tile to center
        for (int radius = 0; radius < 100; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -radius; dy <= radius; dy++) {
                    int x = centerTileX + dx;
                    int y = centerTileY + dy;
                    if (!generator.isWall(x, y)) {
                        return new int[]{x * Constants.TILE_SIZE, y * Constants.TILE_SIZE};
                    }
                }
            }
        }

        // Fallback to center
        return new int[]{centerTileX * Constants.TILE_SIZE, centerTileY * Constants.TILE_SIZE};
    }
}
