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

    /**
     * Create a new dungeon room.
     *
     * @param x X position in tiles
     * @param y Y position in tiles
     * @param width Width in tiles
     * @param height Height in tiles
     */
    public DungeonRoom(float x, float y, int width, int height) {
        this(x, y, width, height, RoomType.STANDARD, RoomSize.MEDIUM);
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
     */
    public DungeonRoom(float x, float y, int width, int height, RoomType type, RoomSize sizeCategory) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.velocityX = 0f;
        this.velocityY = 0f;
        this.isMainRoom = false;
        this.type = type;
        this.sizeCategory = sizeCategory;
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
