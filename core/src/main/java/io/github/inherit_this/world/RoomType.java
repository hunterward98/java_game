package io.github.inherit_this.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Types of dungeon rooms with constrained size categories.
 * Each room type can only be certain sizes to maintain dungeon logic
 * (e.g., a plaza cannot be SMALL).
 */
public enum RoomType {
    STANDARD(RoomSize.SMALL, RoomSize.MEDIUM),              // Normal rooms
    BARRACKS(RoomSize.LARGE, RoomSize.HUGE),                // Military quarters - must be large
    TREASURE(RoomSize.SMALL, RoomSize.MEDIUM),              // Treasure rooms - small to medium
    BOSS(RoomSize.HUGE),                                    // Boss arenas - must be huge
    LIBRARY(RoomSize.MEDIUM, RoomSize.LARGE),               // Libraries - medium to large
    ARMORY(RoomSize.MEDIUM, RoomSize.LARGE),                // Weapon storage - medium to large
    SHRINE(RoomSize.SMALL),                                 // Small shrines only
    PLAZA(RoomSize.LARGE, RoomSize.HUGE),                   // Open areas - must be large
    SECRET(RoomSize.SMALL, RoomSize.MEDIUM);                // Hidden rooms - small to medium

    private final List<RoomSize> allowedSizes;

    RoomType(RoomSize... allowedSizes) {
        this.allowedSizes = Arrays.asList(allowedSizes);
    }

    /**
     * Get all allowed size categories for this room type.
     */
    public List<RoomSize> getAllowedSizes() {
        return new ArrayList<>(allowedSizes);
    }

    /**
     * Check if this room type allows the given size.
     */
    public boolean allowsSize(RoomSize size) {
        return allowedSizes.contains(size);
    }

    /**
     * Get a random allowed size for this room type.
     */
    public RoomSize getRandomAllowedSize(Random random) {
        if (allowedSizes.isEmpty()) {
            return RoomSize.MEDIUM; // Fallback
        }
        return allowedSizes.get(random.nextInt(allowedSizes.size()));
    }

    /**
     * Get the smallest allowed size for this room type.
     */
    public RoomSize getMinAllowedSize() {
        return allowedSizes.stream()
            .min((a, b) -> Integer.compare(a.getMin(), b.getMin()))
            .orElse(RoomSize.SMALL);
    }

    /**
     * Get the largest allowed size for this room type.
     */
    public RoomSize getMaxAllowedSize() {
        return allowedSizes.stream()
            .max((a, b) -> Integer.compare(a.getMax(), b.getMax()))
            .orElse(RoomSize.MEDIUM);
    }

    /**
     * Get hallway width appropriate for this room type.
     * Returns 7, 5, or 3 tiles.
     */
    public int getHallwayWidth() {
        switch (this) {
            case BOSS:
            case PLAZA:
                return 7;  // Grand corridors for boss rooms and plazas
            case BARRACKS:
            case LIBRARY:
            case ARMORY:
                return 5;  // Wide corridors for major rooms
            case STANDARD:
            case TREASURE:
            case SHRINE:
            case SECRET:
            default:
                return 3;  // Normal corridors for smaller rooms
        }
    }
}
