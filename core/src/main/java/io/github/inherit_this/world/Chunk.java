package io.github.inherit_this.world;

import java.util.Random;
import com.badlogic.gdx.graphics.Texture;
import io.github.inherit_this.util.Constants;

public class Chunk {

    private Tile[][] tiles = new Tile[Constants.CHUNK_SIZE][Constants.CHUNK_SIZE];
    private int chunkX;
    private int chunkY;
    private String biome;
    private static final TileTextureManager textureManager = TileTextureManager.getInstance();

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
                    tiles[x][y] = new Tile(tex, true, TileType.STONE);
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

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkY() {
        return chunkY;
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
