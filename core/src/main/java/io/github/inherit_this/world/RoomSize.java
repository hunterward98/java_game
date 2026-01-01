package io.github.inherit_this.world;

/**
 * Size categories for dungeon rooms.
 * Defines min/max dimensions for each category.
 */
public enum RoomSize {
    SMALL(12, 24),      // Standard small rooms
    MEDIUM(24, 40),     // Medium rooms
    LARGE(40, 60),      // Large rooms (reduced from 70)
    HUGE(50, 70);       // Very large rooms (reduced from 100, removed MASSIVE)

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
