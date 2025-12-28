package io.github.inherit_this.world;

import java.util.HashMap;
import java.util.Map;

/**
 * Central registry of dungeon themes mapped by level ranges.
 * Provides theme selection based on dungeon level and manages theme lifecycle.
 */
public class ThemeRegistry {
    private static final Map<Integer, DungeonTheme> LEVEL_THEMES = new HashMap<>();
    private static boolean initialized = false;

    /**
     * Initialize the theme registry with default themes.
     * Called automatically on first access.
     */
    private static void initialize() {
        if (initialized) {
            return;
        }

        // Level 1-10: Ancient Crypt (Marble + Dark Brick)
        DungeonTheme ancientCrypt = new DungeonTheme(
                "Ancient Crypt",
                TileType.MARBLE_TILE,
                TileType.DARK_BRICK,
                getMarbleTileVariants(),
                getDarkBrickVariants()
        );

        for (int level = 1; level <= 10; level++) {
            LEVEL_THEMES.put(level, ancientCrypt);
        }

        // Level 11-20: Dark Depths (Dark Granite + Dark Brick)
        DungeonTheme darkDepths = new DungeonTheme(
                "Dark Depths",
                TileType.DARK_GRANITE,
                TileType.DARK_BRICK,
                getDarkGraniteVariants(),
                getDarkBrickVariants()
        );

        for (int level = 11; level <= 20; level++) {
            LEVEL_THEMES.put(level, darkDepths);
        }

        // TODO: Add more level ranges and themes as needed

        initialized = true;
    }

    /**
     * Get the appropriate theme for a given dungeon level.
     * Automatically initializes the registry on first call.
     *
     * @param level The dungeon level (1-based)
     * @return The theme for this level, or a default theme if level is out of range
     */
    public static DungeonTheme getThemeForLevel(int level) {
        if (!initialized) {
            initialize();
        }

        // Check if we have a theme for this specific level
        if (LEVEL_THEMES.containsKey(level)) {
            return LEVEL_THEMES.get(level);
        }

        // For levels beyond defined ranges, cycle through existing themes
        // This ensures we always return a valid theme
        int cycledLevel = ((level - 1) % 20) + 1;  // Cycle through 1-20
        return LEVEL_THEMES.getOrDefault(cycledLevel, LEVEL_THEMES.get(1));
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
     * User mentioned 1 variant currently (plans to expand).
     *
     * @return Array of texture paths for dark brick variants
     */
    private static String[] getDarkBrickVariants() {
        return new String[]{
                "tiles/dark_brick.png"
        };
    }

    /**
     * Dispose of all theme resources.
     * Should be called when shutting down the application.
     */
    public static void disposeAll() {
        for (DungeonTheme theme : LEVEL_THEMES.values()) {
            if (theme != null) {
                theme.dispose();
            }
        }
        LEVEL_THEMES.clear();
        initialized = false;
    }

    /**
     * Get the total number of registered level-theme mappings.
     * Useful for testing.
     *
     * @return Number of level mappings
     */
    public static int getRegisteredLevelCount() {
        if (!initialized) {
            initialize();
        }
        return LEVEL_THEMES.size();
    }
}
