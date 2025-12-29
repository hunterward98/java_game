package io.github.inherit_this.world;

import com.badlogic.gdx.math.Vector2;

/**
 * Represents a room in the procedural dungeon generation algorithm.
 * Supports physics-based separation with position, velocity, and collision detection.
 */
public class DungeonRoom {
    // Position in tiles (can be float during physics separation)
    public float x;
    public float y;

    // Size in tiles
    public int width;
    public int height;

    // Velocity for physics-based separation
    public float velocityX;
    public float velocityY;

    // Whether this room is selected as a "main room" for Delaunay triangulation
    public boolean isMainRoom;

    // Room type and size category
    public RoomType type;
    public RoomSize sizeCategory;

    // Loot value - determines how much loot/items should spawn in this room
    public int lootValue;

    // Dungeon level (for scaling loot)
    private int dungeonLevel;

    /**
     * Create a new dungeon room.
     *
     * @param x X position in tiles
     * @param y Y position in tiles
     * @param width Width in tiles
     * @param height Height in tiles
     */
    public DungeonRoom(float x, float y, int width, int height) {
        this(x, y, width, height, RoomType.STANDARD, RoomSize.MEDIUM, 1);
    }

    /**
     * Create a new dungeon room with specific type and size.
     *
     * @param x X position in tiles
     * @param y Y position in tiles
     * @param width Width in tiles
     * @param height Height in tiles
     * @param type Room type
     * @param sizeCategory Size category
     * @param dungeonLevel Dungeon level (1-100+) for scaling loot
     */
    public DungeonRoom(float x, float y, int width, int height, RoomType type, RoomSize sizeCategory, int dungeonLevel) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.velocityX = 0f;
        this.velocityY = 0f;
        this.isMainRoom = false;
        this.type = type;
        this.sizeCategory = sizeCategory;
        this.dungeonLevel = dungeonLevel;
        this.lootValue = calculateLootValue();
    }

    /**
     * Calculate loot value based on room type, size, and dungeon level.
     * Higher value = more loot should spawn.
     * Loot scales significantly with dungeon level to reward deeper exploration.
     */
    private int calculateLootValue() {
        // Base value from room size (area / 100)
        int baseValue = getArea() / 100;

        // Scale base value with dungeon level
        // Level 1: 1x, Level 10: 2x, Level 50: 6x, Level 100: 11x
        float levelScaling = 1.0f + (dungeonLevel * 0.1f);
        baseValue = (int) (baseValue * levelScaling);

        // Multiplier based on room type
        float multiplier;
        switch (type) {
            case TREASURE:
                multiplier = 3.0f;      // Treasure rooms have 3x loot
                break;
            case BOSS:
                multiplier = 2.5f;      // Boss rooms have 2.5x loot
                break;
            case SECRET:
                multiplier = 2.0f;      // Secret rooms have 2x loot
                break;
            case ARMORY:
            case LIBRARY:
                multiplier = 1.5f;      // Special rooms have 1.5x loot
                break;
            case BARRACKS:
            case PLAZA:
                multiplier = 1.2f;      // Large rooms have 1.2x loot
                break;
            case SHRINE:
                multiplier = 1.0f;      // Shrines have normal loot
                break;
            default:
                multiplier = 0.8f;      // Standard rooms have 0.8x loot
                break;
        }

        return Math.max(1, (int) (baseValue * multiplier));
    }

    /**
     * Get the center point of this room.
     *
     * @return Vector2 containing the center coordinates in tiles
     */
    public Vector2 center() {
        return new Vector2(x + width / 2f, y + height / 2f);
    }

    /**
     * Check if this room intersects with another room.
     * Includes a small margin (1 tile) to prevent rooms from touching.
     *
     * @param other The other room to check
     * @return true if rooms intersect or are too close
     */
    public boolean intersects(DungeonRoom other) {
        // Add 1 tile margin on each side to prevent touching
        return !(x + width + 1 < other.x ||
                 other.x + other.width + 1 < x ||
                 y + height + 1 < other.y ||
                 other.y + other.height + 1 < y);
    }

    /**
     * Calculate the Euclidean distance between this room's center and another room's center.
     *
     * @param other The other room
     * @return Distance in tiles
     */
    public float distanceTo(DungeonRoom other) {
        Vector2 thisCenter = center();
        Vector2 otherCenter = other.center();
        return thisCenter.dst(otherCenter);
    }

    /**
     * Get the bounding box area of this room.
     *
     * @return Area in tiles (width × height)
     */
    public int getArea() {
        return width * height;
    }

    /**
     * Apply velocity to position (for physics simulation).
     *
     * @param delta Time step
     */
    public void updatePosition(float delta) {
        x += velocityX * delta;
        y += velocityY * delta;
    }

    /**
     * Apply damping to velocity (to converge physics simulation).
     *
     * @param damping Damping factor (0.0-1.0), typically 0.95
     */
    public void applyDamping(float damping) {
        velocityX *= damping;
        velocityY *= damping;
    }

    /**
     * Check if room velocity is negligible (for convergence detection).
     *
     * @param threshold Velocity threshold
     * @return true if both velocity components are below threshold
     */
    public boolean isStable(float threshold) {
        return Math.abs(velocityX) < threshold && Math.abs(velocityY) < threshold;
    }

    /**
     * Get the hallway width appropriate for connecting to this room.
     * Returns 3, 5, or 7 tiles based on room type.
     */
    public int getHallwayWidth() {
        return type != null ? type.getHallwayWidth() : 3;
    }

    @Override
    public String toString() {
        return String.format("Room[x=%.1f, y=%.1f, w=%d, h=%d, type=%s, size=%s, main=%b]",
                x, y, width, height, type, sizeCategory, isMainRoom);
    }
}
