package io.github.inherit_this.world;

import com.badlogic.gdx.graphics.Texture;

public class TileGrid {

    private Tile[][] tiles;
    public final int width;
    public final int height;

    public TileGrid(Texture defaultTexture) {
        // start with 8x8 for now
        this.width = 8;
        this.height = 8;
        tiles = new Tile[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = new Tile(defaultTexture);
            }
        }
    }

    public Tile getTile(int x, int y) {
        return tiles[x][y];
    }
}
