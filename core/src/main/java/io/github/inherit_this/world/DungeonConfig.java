package io.github.inherit_this.world;

/**
 * Configuration for dungeon generation.
 */
public class DungeonConfig {

    public enum DungeonStyle {
        OPEN,    // Larger rooms, fewer corridors
        NARROW   // Smaller rooms, more corridors
    }

    public enum DungeonLayout {
        WINDING,   // Many turns, complex paths
        STRAIGHT   // Direct paths, fewer turns
    }

    private final long seed;
    private final int dungeonLevel;  // 1-100+, affects difficulty and loot
    private final int widthInChunks;
    private final int heightInChunks;
    private final DungeonStyle style;
    private final DungeonLayout layout;

    // Generation parameters (derived from style/layout)
    private final int roomMinSize;
    private final int roomMaxSize;
    private final int corridorWidth;
    private final float roomDensity;  // 0.0-1.0, higher = more rooms vs corridors

    public DungeonConfig(long seed, int dungeonLevel, int widthInChunks, int heightInChunks,
                        DungeonStyle style, DungeonLayout layout) {
        this.seed = seed;
        this.dungeonLevel = dungeonLevel;
        this.widthInChunks = widthInChunks;
        this.heightInChunks = heightInChunks;
        this.style = style;
        this.layout = layout;

        // Calculate parameters based on style
        if (style == DungeonStyle.OPEN) {
            this.roomMinSize = 5;
            this.roomMaxSize = 12;
            this.corridorWidth = 3;
            this.roomDensity = 0.7f;
        } else {
            this.roomMinSize = 3;
            this.roomMaxSize = 6;
            this.corridorWidth = 1;
            this.roomDensity = 0.3f;
        }
    }

    // Default 64x64 dungeon
    public DungeonConfig(long seed, int dungeonLevel, DungeonStyle style, DungeonLayout layout) {
        this(seed, dungeonLevel, 64, 64, style, layout);
    }

    // Simple constructor with random seed
    public static DungeonConfig createRandom(int dungeonLevel, DungeonStyle style, DungeonLayout layout) {
        return new DungeonConfig(System.nanoTime(), dungeonLevel, style, layout);
    }

    public long getSeed() { return seed; }
    public int getDungeonLevel() { return dungeonLevel; }
    public int getWidthInChunks() { return widthInChunks; }
    public int getHeightInChunks() { return heightInChunks; }
    public DungeonStyle getStyle() { return style; }
    public DungeonLayout getLayout() { return layout; }
    public int getRoomMinSize() { return roomMinSize; }
    public int getRoomMaxSize() { return roomMaxSize; }
    public int getCorridorWidth() { return corridorWidth; }
    public float getRoomDensity() { return roomDensity; }
}
