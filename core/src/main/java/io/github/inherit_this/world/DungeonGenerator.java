package io.github.inherit_this.world;

import io.github.inherit_this.util.Constants;
import java.util.Random;

/**
 * Abstract base class for dungeon generators.
 * Provides common infrastructure for dungeon generation algorithms.
 * Subclasses implement specific generation strategies (procedural, maze-based, room-based).
 */
public abstract class DungeonGenerator {

    protected final DungeonConfig config;
    protected final Random random;

    // Dungeon grid in TILES (not chunks)
    protected final int widthInTiles;
    protected final int heightInTiles;
    protected boolean[][] walls;  // true = wall, false = floor

    protected DungeonGenerator(DungeonConfig config) {
        this.config = config;
        this.random = new Random(config.getSeed());
        this.widthInTiles = config.getWidthInChunks() * Constants.CHUNK_SIZE;
        this.heightInTiles = config.getHeightInChunks() * Constants.CHUNK_SIZE;
        this.walls = new boolean[widthInTiles][heightInTiles];
    }

    /**
     * Generate the dungeon layout.
     * Subclasses must implement their specific generation algorithm.
     */
    public abstract void generate();

    /**
     * Fill entire grid with walls.
     * Useful helper for subclasses to start generation.
     */
    protected void fillWithWalls() {
        for (int x = 0; x < widthInTiles; x++) {
            for (int y = 0; y < heightInTiles; y++) {
                walls[x][y] = true;
            }
        }
    }

    /**
     * Create 2-tile tall border around entire dungeon.
     * Subclasses can call this to enforce impassable borders.
     */
    protected void createBorder() {
        // Mark all border tiles as walls (they'll be rendered as 2-tile tall walls)
        for (int x = 0; x < widthInTiles; x++) {
            walls[x][0] = true;
            walls[x][heightInTiles - 1] = true;
        }
        for (int y = 0; y < heightInTiles; y++) {
            walls[0][y] = true;
            walls[widthInTiles - 1][y] = true;
        }
    }

    /**
     * Check if a tile position is a wall.
     */
    public boolean isWall(int tileX, int tileY) {
        if (tileX < 0 || tileX >= widthInTiles || tileY < 0 || tileY >= heightInTiles) {
            return true;
        }
        return walls[tileX][tileY];
    }

    /**
     * Check if a tile is on the border (for 2-tile tall walls).
     */
    public boolean isBorder(int tileX, int tileY) {
        return tileX == 0 || tileX == widthInTiles - 1 ||
               tileY == 0 || tileY == heightInTiles - 1;
    }

    public int getWidthInTiles() { return widthInTiles; }
    public int getHeightInTiles() { return heightInTiles; }
    public DungeonConfig getConfig() { return config; }
}
