package io.github.inherit_this.util;

public class Constants {

    // World Generation
    public static final int TILE_SIZE = 32;
    public static final int CHUNK_SIZE = 8;
    public static final int CHUNK_PIXEL_SIZE = TILE_SIZE * CHUNK_SIZE;

    // Rendering
    public static final float EDGE_BLEND_SIZE = 4f;

    // Player
    public static final float DEFAULT_PLAYER_SPEED = 200f;

    // Input
    public static final int DEFAULT_PAUSE_KEY = com.badlogic.gdx.Input.Keys.ESCAPE;

    private Constants() {
        // Prevent instantiation
    }
}
