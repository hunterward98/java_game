package io.github.inherit_this.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import java.util.Random;

/**
 * Defines a dungeon visual theme with floor and wall texture variants.
 * Each theme specifies the tile types to use and provides methods to randomly
 * select texture variants for visual variety.
 */
public class DungeonTheme {
    private final String name;
    private final TileType floorType;
    private final TileType wallType;
    private final String[] floorTextureVariants;
    private final String[] wallTextureVariants;

    // Cached textures for performance
    private Texture[] cachedFloorTextures;
    private Texture[] cachedWallTextures;

    /**
     * Creates a new dungeon theme.
     *
     * @param name The theme name (e.g., "Ancient Crypt")
     * @param floorType The tile type for floors
     * @param wallType The tile type for walls
     * @param floorTextureVariants Array of texture paths for floor variants
     * @param wallTextureVariants Array of texture paths for wall variants
     */
    public DungeonTheme(String name, TileType floorType, TileType wallType,
                        String[] floorTextureVariants, String[] wallTextureVariants) {
        this.name = name;
        this.floorType = floorType;
        this.wallType = wallType;
        this.floorTextureVariants = floorTextureVariants;
        this.wallTextureVariants = wallTextureVariants;

        // Initialize texture caches (lazy loading)
        this.cachedFloorTextures = null;
        this.cachedWallTextures = null;
    }

    /**
     * Get a random floor texture using the provided random generator.
     * Textures are cached on first access for performance.
     *
     * @param rand Random generator for variant selection
     * @return Random floor texture from this theme's variants
     */
    public Texture getRandomFloorTexture(Random rand) {
        if (cachedFloorTextures == null) {
            loadFloorTextures();
        }

        if (cachedFloorTextures.length == 0) {
            throw new IllegalStateException("No floor textures available for theme: " + name);
        }

        int index = rand.nextInt(cachedFloorTextures.length);
        return cachedFloorTextures[index];
    }

    /**
     * Get a random wall texture using the provided random generator.
     * Textures are cached on first access for performance.
     *
     * @param rand Random generator for variant selection
     * @return Random wall texture from this theme's variants
     */
    public Texture getRandomWallTexture(Random rand) {
        if (cachedWallTextures == null) {
            loadWallTextures();
        }

        if (cachedWallTextures.length == 0) {
            throw new IllegalStateException("No wall textures available for theme: " + name);
        }

        int index = rand.nextInt(cachedWallTextures.length);
        return cachedWallTextures[index];
    }

    /**
     * Load floor textures from file paths.
     * Called lazily on first texture access.
     */
    private void loadFloorTextures() {
        cachedFloorTextures = new Texture[floorTextureVariants.length];
        for (int i = 0; i < floorTextureVariants.length; i++) {
            try {
                Texture texture = new Texture(Gdx.files.internal(floorTextureVariants[i]));
                texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                cachedFloorTextures[i] = texture;
            } catch (Exception e) {
                Gdx.app.error("DungeonTheme", "Failed to load floor texture: " +
                        floorTextureVariants[i] + " for theme: " + name, e);
                // Use fallback - create error texture or reuse previous
                if (i > 0 && cachedFloorTextures[i - 1] != null) {
                    cachedFloorTextures[i] = cachedFloorTextures[i - 1];
                }
            }
        }
    }

    /**
     * Load wall textures from file paths.
     * Called lazily on first texture access.
     */
    private void loadWallTextures() {
        cachedWallTextures = new Texture[wallTextureVariants.length];
        for (int i = 0; i < wallTextureVariants.length; i++) {
            try {
                Texture texture = new Texture(Gdx.files.internal(wallTextureVariants[i]));
                texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                cachedWallTextures[i] = texture;
            } catch (Exception e) {
                Gdx.app.error("DungeonTheme", "Failed to load wall texture: " +
                        wallTextureVariants[i] + " for theme: " + name, e);
                // Use fallback - create error texture or reuse previous
                if (i > 0 && cachedWallTextures[i - 1] != null) {
                    cachedWallTextures[i] = cachedWallTextures[i - 1];
                }
            }
        }
    }

    /**
     * Dispose of all cached textures.
     * Should be called when the theme is no longer needed.
     */
    public void dispose() {
        if (cachedFloorTextures != null) {
            for (Texture texture : cachedFloorTextures) {
                if (texture != null) {
                    texture.dispose();
                }
            }
            cachedFloorTextures = null;
        }

        if (cachedWallTextures != null) {
            for (Texture texture : cachedWallTextures) {
                if (texture != null) {
                    texture.dispose();
                }
            }
            cachedWallTextures = null;
        }
    }

    // Getters

    public String getName() {
        return name;
    }

    public TileType getFloorType() {
        return floorType;
    }

    public TileType getWallType() {
        return wallType;
    }

    public int getFloorVariantCount() {
        return floorTextureVariants.length;
    }

    public int getWallVariantCount() {
        return wallTextureVariants.length;
    }
}
