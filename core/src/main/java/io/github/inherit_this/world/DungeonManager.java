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

    // Dungeon generation settings (set by player)
    private long playerSeed = 0L;  // Player's persistent seed for dungeon generation

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
     * Generate a new dungeon for the specified level using procedural generation.
     */
    private DungeonWorld generateDungeon(int level) {
        // Create seed based on player seed + level
        // This ensures dungeons are consistent for each player
        long seed = playerSeed + level * 1000;

        // Use procedural generation for all dungeons
        DungeonConfig config = DungeonConfig.createProcedural(seed, level);
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
     * Set the player seed for dungeon generation.
     * Should be called when player is loaded or created.
     */
    public void setPlayerSeed(long seed) {
        this.playerSeed = seed;
        // Clear cache so new dungeons are generated with new seed
        clearDungeons();
    }

    /**
     * Get the current player seed.
     */
    public long getPlayerSeed() {
        return playerSeed;
    }

    /**
     * Dispose of resources.
     */
    public void dispose() {
        clearDungeons();
    }
}
