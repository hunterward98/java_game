package io.github.inherit_this.world;

import java.util.Random;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import io.github.inherit_this.util.Constants;
import java.util.ArrayList;
import java.util.List;

public class Chunk {

    private Tile[][] tiles = new Tile[Constants.CHUNK_SIZE][Constants.CHUNK_SIZE];
    private int chunkX;
    private int chunkY;
    private String biome;
    private static final TileTextureManager textureManager = TileTextureManager.getInstance();

    // Cache 3D models for performance
    private List<ModelInstance> cachedModels = null;

    public Chunk(int chunkX, int chunkY, String biome) {
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        this.biome = biome;

        generateTiles();
    }

    private void generateTiles() {
        // Use chunk coordinates as seed for consistent, reproducible generation
        long seed = ((long) chunkX << 32) | (chunkY & 0xFFFFFFFFL);
        Random rand = new Random(seed);

        for (int x = 0; x < Constants.CHUNK_SIZE; x++) {
            for (int y = 0; y < Constants.CHUNK_SIZE; y++) {
                int roll = rand.nextInt(100);

                if (roll < 10) {
                    int stoneType = rand.nextInt(2) + 1;
                    Texture tex = textureManager.getTexture("tiles/stone_" + stoneType + ".png");
                    tiles[x][y] = new Tile(tex, false, TileType.STONE); // Stone is not solid anymore
                } else {
                    int num = rand.nextInt(30) + 1;
                    if (num < 9) {
                        num = 1;
                    } else if (num < 15) {
                        num = 2;
                    } else if (num < 16) {
                        num = 3;
                    } else if (num < 20) {
                        num = 4;
                    } else if (num < 24) {
                        num = 5;
                    } else {
                        num = 6;
                    }
                    Texture tex = textureManager.getTexture("tiles/" + biome + "_" + num + ".png");
                    tiles[x][y] = new Tile(tex, false, TileType.GRASS);
                }
            }
        }
    }

    public Tile getTile(int x, int y) {
        if (x < 0 || y < 0 || x >= Constants.CHUNK_SIZE || y >= Constants.CHUNK_SIZE) {
            throw new IndexOutOfBoundsException("Tile coords out of bounds");
        }
        return tiles[x][y];
    }

    /**
     * Gets all tiles at the specified position (supports multiple layers).
     * Base implementation returns a single-element list for backward compatibility.
     * StaticChunk overrides this to support multiple layers.
     */
    public List<Tile> getTiles(int x, int y) {
        if (x < 0 || y < 0 || x >= Constants.CHUNK_SIZE || y >= Constants.CHUNK_SIZE) {
            throw new IndexOutOfBoundsException("Tile coords out of bounds");
        }
        List<Tile> result = new ArrayList<>();
        if (tiles[x][y] != null) {
            result.add(tiles[x][y]);
        }
        return result;
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkY() {
        return chunkY;
    }

    /**
     * Gets cached 3D models for this chunk. Creates them if not already cached.
     */
    public List<ModelInstance> getCachedModels() {
        if (cachedModels == null) {
            buildCachedModels();
        }
        return cachedModels;
    }

    /**
     * Builds and caches all tile ModelInstances for this chunk.
     * This is done once per chunk instead of every frame.
     */
    private void buildCachedModels() {
        cachedModels = new ArrayList<>();
        TileMesh3D tileMesh = TileMesh3D.getInstance();

        float baseX = chunkX * Constants.CHUNK_PIXEL_SIZE;
        float baseY = chunkY * Constants.CHUNK_PIXEL_SIZE;

        for (int x = 0; x < Constants.CHUNK_SIZE; x++) {
            for (int y = 0; y < Constants.CHUNK_SIZE; y++) {
                Tile tile = tiles[x][y];
                float tileWorldX = baseX + x * Constants.TILE_SIZE;
                float tileWorldY = baseY + y * Constants.TILE_SIZE;

                ModelInstance tileInstance = tileMesh.createTileInstance(
                    tile.getTexture(),
                    tileWorldX,
                    tileWorldY,
                    0f  // z = 0 for flat terrain
                );

                cachedModels.add(tileInstance);
            }
        }
    }

    /**
     * Clears cached models. Call this if chunk tiles change.
     */
    public void invalidateCache() {
        cachedModels = null;
    }

    /**
     * Disposes of this chunk's resources.
     * Clears cached models to free memory.
     */
    public void dispose() {
        if (cachedModels != null) {
            cachedModels.clear();
            cachedModels = null;
        }
    }
    // reload idea
    // public void reloadChunk(int chunkX, int chunkY) {
    //     ChunkKey key = new ChunkKey(chunkX, chunkY);

    //     // Remove old chunk
    //     chunks.remove(key);

    //     // Generate and insert new one
    //     Chunk newChunk = chunkGenerator.generateChunk(chunkX, chunkY);
    //     chunks.put(key, newChunk);
    // }

}
