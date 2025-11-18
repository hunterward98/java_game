package io.github.inherit_this.world;

import java.util.Random;
import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.graphics.Texture;

public class Chunk {
    public static final int CHUNK_SIZE = 8;

    private Tile[][] tiles = new Tile[CHUNK_SIZE][CHUNK_SIZE];
    private int chunkX;
    private int chunkY;
    private String biome;

    public Chunk(int chunkX, int chunkY, String biome) {
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        this.biome = biome;

        generateTiles();
    }

    private void generateTiles() {
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int y = 0; y < CHUNK_SIZE; y++) {
                Random rand = new Random();
                int num = rand.nextInt(30) + 1;
                // alternate between biome textures
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
                Gdx.app.debug("Num", Integer.toString(num));
                Texture tex = new Texture("tiles/" + biome + "_" + Integer.toString(num) + ".png");
                tiles[x][y] = new Tile(tex);
            }
        }
    }

    public Tile getTile(int x, int y) {
        if (x < 0 || y < 0 || x >= CHUNK_SIZE || y >= CHUNK_SIZE) {
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
}
