package io.github.inherit_this.world;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages dungeon levels and transitions between town and dungeons.
 * Handles dungeon generation, level progression, and return portals.
 */
public class DungeonManager {

    private static DungeonManager instance;

    // Currently active dungeon world (null if in town)
    private DungeonWorld currentDungeon;
    private int currentLevel;

    // Cache of generated dungeons by level
    private Map<Integer, DungeonWorld> dungeonCache;

    // Last position in town before entering dungeon
    private float townReturnX;
    private float townReturnY;

    // Dungeon generation settings
    private long baseSeed = 42L;  // Base seed for dungeons

    private DungeonManager() {
        this.dungeonCache = new HashMap<>();
        this.currentLevel = 0;  // 0 = town
    }

    public static DungeonManager getInstance() {
        if (instance == null) {
            instance = new DungeonManager();
        }
        return instance;
    }

    /**
     * Enter a dungeon at the specified level.
     * @param level The dungeon level (1 = first level)
     * @param townX Player's X position in town (for returning)
     * @param townY Player's Y position in town (for returning)
     * @return The dungeon world to switch to
     */
    public DungeonWorld enterDungeon(int level, float townX, float townY) {
        this.townReturnX = townX;
        this.townReturnY = townY;
        this.currentLevel = level;

        // Get or create dungeon for this level
        if (!dungeonCache.containsKey(level)) {
            dungeonCache.put(level, generateDungeon(level));
        }

        currentDungeon = dungeonCache.get(level);
        return currentDungeon;
    }

    /**
     * Generate a new dungeon for the specified level.
     */
    private DungeonWorld generateDungeon(int level) {
        // Create seed based on base seed + level
        long seed = baseSeed + level * 1000;

        // Determine dungeon style based on level
        DungeonConfig.DungeonStyle style;
        DungeonConfig.DungeonLayout layout;

        if (level <= 3) {
            // Early levels: more open
            style = DungeonConfig.DungeonStyle.OPEN;
            layout = DungeonConfig.DungeonLayout.STRAIGHT;
        } else if (level <= 7) {
            // Mid levels: mixed
            style = (level % 2 == 0) ? DungeonConfig.DungeonStyle.OPEN : DungeonConfig.DungeonStyle.NARROW;
            layout = DungeonConfig.DungeonLayout.WINDING;
        } else {
            // Deep levels: tight and winding
            style = DungeonConfig.DungeonStyle.NARROW;
            layout = DungeonConfig.DungeonLayout.WINDING;
        }

        DungeonConfig config = new DungeonConfig(seed, level, 64, 64, style, layout);
        return new DungeonWorld(config);
    }

    /**
     * Exit the current dungeon and return to town.
     * @return Array of [townX, townY] for player spawn position
     */
    public float[] exitToTown() {
        // Keep current dungeon in cache so player can return
        // Just return the town position
        currentDungeon = null;
        currentLevel = 0;

        return new float[]{townReturnX, townReturnY};
    }

    /**
     * Get the player's spawn position for a dungeon level.
     */
    public int[] getDungeonSpawnPosition(int level) {
        DungeonWorld dungeon = dungeonCache.get(level);
        if (dungeon != null) {
            return dungeon.getSpawnPosition();
        }
        return new int[]{0, 0};
    }

    /**
     * Check if player is currently in a dungeon.
     */
    public boolean isInDungeon() {
        return currentLevel > 0 && currentDungeon != null;
    }

    /**
     * Get current dungeon level (0 = town).
     */
    public int getCurrentLevel() {
        return currentLevel;
    }

    /**
     * Get current dungeon world (null if in town).
     */
    public DungeonWorld getCurrentDungeon() {
        return currentDungeon;
    }

    /**
     * Clear all cached dungeons (useful for testing or resetting).
     */
    public void clearDungeons() {
        for (DungeonWorld dungeon : dungeonCache.values()) {
            dungeon.dispose();
        }
        dungeonCache.clear();
        currentDungeon = null;
        currentLevel = 0;
    }

    /**
     * Set the base seed for dungeon generation.
     */
    public void setBaseSeed(long seed) {
        this.baseSeed = seed;
        // Clear cache so new dungeons are generated with new seed
        clearDungeons();
    }

    /**
     * Dispose of resources.
     */
    public void dispose() {
        clearDungeons();
    }
}
