package io.github.inherit_this.world;

import com.badlogic.gdx.graphics.Texture;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton texture manager to cache and reuse tile textures.
 * Prevents loading the same texture multiple times, which causes stuttering.
 */
public class TileTextureManager {
    private static TileTextureManager instance;
    private final Map<String, Texture> textureCache = new HashMap<>();

    private TileTextureManager() {
    }

    public static TileTextureManager getInstance() {
        if (instance == null) {
            instance = new TileTextureManager();
        }
        return instance;
    }

    /**
     * Gets a texture from cache, or loads it if not cached.
     */
    public Texture getTexture(String path) {
        if (!textureCache.containsKey(path)) {
            textureCache.put(path, new Texture(path));
        }
        return textureCache.get(path);
    }

    /**
     * Preload commonly used textures to prevent stuttering.
     */
    public void preloadCommonTextures() {
        // Preload all grass tiles
        for (int i = 1; i <= 6; i++) {
            getTexture("tiles/grass_" + i + ".png");
        }

        // Preload stone tiles
        getTexture("tiles/stone_1.png");
        getTexture("tiles/stone_2.png");

        // Preload other tiles
        getTexture("tiles/mossy_stone_1.png");
        getTexture("tiles/mossy_stone_2.png");
        getTexture("tiles/path_1.png");
        getTexture("tiles/sand_1.png");
    }

    /**
     * Dispose all cached textures. Call this when shutting down.
     */
    public void dispose() {
        for (Texture texture : textureCache.values()) {
            texture.dispose();
        }
        textureCache.clear();
    }
}
