package io.github.inherit_this.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Central registry of dungeon themes.
 * Provides random theme selection based on dungeon seed, similar to FATE.
 * Each dungeon gets a random theme for variety.
 */
public class ThemeRegistry {
    private static final List<DungeonTheme> ALL_THEMES = new ArrayList<>();
    private static boolean initialized = false;

    /**
     * Initialize the theme registry with all available themes.
     * Called automatically on first access.
     */
    private static void initialize() {
        if (initialized) {
            return;
        }

        // Ancient Crypt (Marble + Dark Brick)
        ALL_THEMES.add(new DungeonTheme(
                "Ancient Crypt",
                TileType.MARBLE_TILE,
                TileType.DARK_BRICK,
                getMarbleTileVariants(),
                getDarkBrickVariants()
        ));

        // Dark Depths (Dark Granite + Dark Brick)
        ALL_THEMES.add(new DungeonTheme(
                "Dark Depths",
                TileType.DARK_GRANITE,
                TileType.DARK_BRICK,
                getDarkGraniteVariants(),
                getDarkBrickVariants()
        ));

        // TODO: Add more themes here as new tilesets are created

        initialized = true;
    }

    /**
     * Get a random theme for a dungeon based on its seed.
     * This provides variety like FATE - each dungeon can have any theme.
     * The same seed will always return the same theme (deterministic).
     *
     * @param seed The dungeon seed for random selection
     * @return A randomly selected theme based on the seed
     */
    public static DungeonTheme getThemeForSeed(long seed) {
        if (!initialized) {
            initialize();
        }

        if (ALL_THEMES.isEmpty()) {
            throw new IllegalStateException("No themes registered!");
        }

        // Use seed to deterministically select a theme
        Random random = new Random(seed);
        int themeIndex = random.nextInt(ALL_THEMES.size());
        return ALL_THEMES.get(themeIndex);
    }

    /**
     * Get the appropriate theme for a given dungeon level.
     * DEPRECATED: Use getThemeForSeed() for random theme selection.
     * Kept for backward compatibility.
     *
     * @param level The dungeon level (1-based)
     * @return A randomly selected theme (now ignores level)
     * @deprecated Use getThemeForSeed(long seed) instead
     */
    @Deprecated
    public static DungeonTheme getThemeForLevel(int level) {
        // For backward compatibility, use level as seed
        return getThemeForSeed(level);
    }

    /**
     * Get all marble tile texture variant paths.
     * User mentioned 12 variants available.
     *
     * @return Array of texture paths for marble tile variants
     */
    private static String[] getMarbleTileVariants() {
        return new String[]{
                "tiles/marble_tile_1.png",
                "tiles/marble_tile_2.png",
                "tiles/marble_tile_3.png",
                "tiles/marble_tile_4.png",
                "tiles/marble_tile_5.png",
                "tiles/marble_tile_6.png",
                "tiles/marble_tile_7.png",
                "tiles/marble_tile_8.png",
                "tiles/marble_tile_9.png",
                "tiles/marble_tile_10.png",
                "tiles/marble_tile_11.png",
                "tiles/marble_tile_12.png"
        };
    }

    /**
     * Get all dark granite texture variant paths.
     * User mentioned 11 variants available.
     *
     * @return Array of texture paths for dark granite variants
     */
    private static String[] getDarkGraniteVariants() {
        return new String[]{
                "tiles/dark_granite_1.png",
                "tiles/dark_granite_2.png",
                "tiles/dark_granite_3.png",
                "tiles/dark_granite_4.png",
                "tiles/dark_granite_5.png",
                "tiles/dark_granite_6.png",
                "tiles/dark_granite_7.png",
                "tiles/dark_granite_8.png",
                "tiles/dark_granite_9.png",
                "tiles/dark_granite_10.png",
                "tiles/dark_granite_11.png"
        };
    }

    /**
     * Get all dark brick texture variant paths.
     * 5 variants available for wall variety.
     *
     * @return Array of texture paths for dark brick variants
     */
    private static String[] getDarkBrickVariants() {
        return new String[]{
                "tiles/dark_brick_1.png",
                "tiles/dark_brick_2.png",
                "tiles/dark_brick_3.png",
                "tiles/dark_brick_4.png",
                "tiles/dark_brick_5.png"
        };
    }

    /**
     * Dispose of all theme resources.
     * Should be called when shutting down the application.
     */
    public static void disposeAll() {
        for (DungeonTheme theme : ALL_THEMES) {
            if (theme != null) {
                theme.dispose();
            }
        }
        ALL_THEMES.clear();
        initialized = false;
    }

    /**
     * Get the total number of registered themes.
     * Useful for testing.
     *
     * @return Number of available themes
     */
    public static int getRegisteredThemeCount() {
        if (!initialized) {
            initialize();
        }
        return ALL_THEMES.size();
    }
}
