package io.github.inherit_this.world;

/**
 * Configuration for dungeon generation.
 */
public class DungeonConfig {

    public enum GenerationStyle {
        PROCEDURAL,  // New procedural generation with Delaunay triangulation
        ROOM_BASED,  // Legacy simple room-based generation
        MAZE         // Legacy maze-based generation
    }

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
    private final GenerationStyle generationStyle;
    private final DungeonStyle style;
    private final DungeonLayout layout;
    private final DungeonTheme theme;  // Auto-selected based on dungeonLevel

    // Generation parameters (derived from style/layout)
    private final int roomMinSize;
    private final int roomMaxSize;
    private final int corridorWidth;
    private final float roomDensity;  // 0.0-1.0, higher = more rooms vs corridors

    // Organic generation features (new)
    private final boolean enableOrganicShapes;  // Use varied room shapes (irregular, L/T, circular)
    private final boolean enableCurvyHallways;  // Use A* pathfinding for curvy corridors

    public DungeonConfig(long seed, int dungeonLevel, int widthInChunks, int heightInChunks,
                        GenerationStyle generationStyle, DungeonStyle style, DungeonLayout layout,
                        boolean enableOrganicShapes, boolean enableCurvyHallways) {
        this.seed = seed;
        this.dungeonLevel = dungeonLevel;
        this.widthInChunks = widthInChunks;
        this.heightInChunks = heightInChunks;
        this.generationStyle = generationStyle;
        this.style = style;
        this.layout = layout;
        this.enableOrganicShapes = enableOrganicShapes;
        this.enableCurvyHallways = enableCurvyHallways;

        // Auto-select theme randomly based on dungeon seed (like FATE)
        // Same seed = same theme, but different dungeons get random variety
        this.theme = ThemeRegistry.getThemeForSeed(seed);

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

    // Legacy constructor for backward compatibility (defaults to ROOM_BASED generation, no organic features)
    public DungeonConfig(long seed, int dungeonLevel, int widthInChunks, int heightInChunks,
                        DungeonStyle style, DungeonLayout layout) {
        this(seed, dungeonLevel, widthInChunks, heightInChunks, GenerationStyle.ROOM_BASED, style, layout, false, false);
    }

    // Legacy constructor with generation style (no organic features)
    public DungeonConfig(long seed, int dungeonLevel, int widthInChunks, int heightInChunks,
                        GenerationStyle generationStyle, DungeonStyle style, DungeonLayout layout) {
        this(seed, dungeonLevel, widthInChunks, heightInChunks, generationStyle, style, layout, false, false);
    }

    // Default 64x64 dungeon
    public DungeonConfig(long seed, int dungeonLevel, DungeonStyle style, DungeonLayout layout) {
        this(seed, dungeonLevel, 64, 64, GenerationStyle.ROOM_BASED, style, layout, false, false);
    }

    // Simple constructor with random seed
    public static DungeonConfig createRandom(int dungeonLevel, DungeonStyle style, DungeonLayout layout) {
        return new DungeonConfig(System.nanoTime(), dungeonLevel, style, layout);
    }

    // Factory method for procedural dungeon generation (384×384 tiles)
    // Now with organic features enabled by default!
    public static DungeonConfig createProcedural(int dungeonLevel) {
        return new DungeonConfig(
            System.nanoTime(),
            dungeonLevel,
            48,  // 48×48 chunks = 384×384 tiles
            48,
            GenerationStyle.PROCEDURAL,
            DungeonStyle.NARROW,    // Narrow corridors to prevent rooms merging
            DungeonLayout.WINDING,  // Complex paths from MST algorithm
            true,  // Enable organic shapes
            true   // Enable curvy hallways
        );
    }

    // Factory method for procedural dungeon with custom seed
    public static DungeonConfig createProcedural(long seed, int dungeonLevel) {
        return new DungeonConfig(
            seed,
            dungeonLevel,
            48,  // 48×48 chunks = 384×384 tiles
            48,
            GenerationStyle.PROCEDURAL,
            DungeonStyle.NARROW,    // Narrow corridors to prevent rooms merging
            DungeonLayout.WINDING,
            true,  // Enable organic shapes
            true   // Enable curvy hallways
        );
    }

    public long getSeed() { return seed; }
    public int getDungeonLevel() { return dungeonLevel; }
    public int getWidthInChunks() { return widthInChunks; }
    public int getHeightInChunks() { return heightInChunks; }
    public GenerationStyle getGenerationStyle() { return generationStyle; }
    public DungeonStyle getStyle() { return style; }
    public DungeonLayout getLayout() { return layout; }
    public DungeonTheme getTheme() { return theme; }
    public int getRoomMinSize() { return roomMinSize; }
    public int getRoomMaxSize() { return roomMaxSize; }
    public int getCorridorWidth() { return corridorWidth; }
    public float getRoomDensity() { return roomDensity; }
    public boolean isOrganicShapesEnabled() { return enableOrganicShapes; }
    public boolean isCurvyHallwaysEnabled() { return enableCurvyHallways; }
}
