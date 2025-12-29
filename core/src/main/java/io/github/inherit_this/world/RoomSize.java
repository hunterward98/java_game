package io.github.inherit_this.world;

/**
 * Size categories for dungeon rooms.
 * Defines min/max dimensions for each category.
 */
public enum RoomSize {
    SMALL(6, 12),       // Standard small rooms
    MEDIUM(12, 20),     // Medium rooms
    LARGE(20, 35),      // Large multi-chunk rooms
    HUGE(35, 50),       // Very large rooms (boss arenas, grand halls)
    MASSIVE(50, 80);    // Massive special rooms (throne rooms, plazas)

    private final int min;
    private final int max;

    RoomSize(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    /**
     * Get a random size within this category's range.
     */
    public int getRandomSize(java.util.Random random) {
        return min + random.nextInt(max - min + 1);
    }

    /**
     * Get the average size for this category.
     */
    public int getAverageSize() {
        return (min + max) / 2;
    }
}
