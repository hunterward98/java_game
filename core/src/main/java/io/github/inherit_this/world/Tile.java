package io.github.inherit_this.world;

import com.badlogic.gdx.graphics.Texture;

public class Tile {

    public static final int TILE_SIZE = 32;

    private Texture texture;

    public Tile(Texture texture) {
        this.texture = texture;
    }

    public Texture getTexture() {
        return texture;
    }
}
