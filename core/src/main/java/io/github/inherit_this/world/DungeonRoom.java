package io.github.inherit_this.world;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Represents a room in the procedural dungeon generation algorithm.
 * Supports physics-based separation with position, velocity, and collision detection.
 * Now supports multiple room shapes via composition (RoomShape interface).
 */
public class DungeonRoom {
    // Position in tiles (can be float during physics separation)
    public float x;
    public float y;

    // Size in tiles (bounding box for physics)
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

    // Room shape (null = legacy rectangular behavior for backward compatibility)
    private RoomShape shape;

    // Entrance points for hallway connections (maps connected room to entrance point)
    private final Map<DungeonRoom, Vector2> entrances = new HashMap<>();

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
     * Set the shape of this room.
     * If null, uses legacy rectangular behavior.
     *
     * @param shape The room shape
     */
    public void setShape(RoomShape shape) {
        this.shape = shape;
    }

    /**
     * Get the room shape (may be null for legacy rooms).
     */
    public RoomShape getShape() {
        return shape;
    }

    /**
     * Get the center point of this room.
     * Delegates to shape if available, otherwise uses bounding box.
     *
     * @return Vector2 containing the center coordinates in tiles
     */
    public Vector2 center() {
        if (shape != null) {
            return shape.getCenter();
        }
        return new Vector2(x + width / 2f, y + height / 2f);
    }

    /**
     * Check if this room intersects with another room.
     * Includes a small margin (1 tile) to prevent rooms from touching.
     * Uses detailed shape collision if shapes available, otherwise bounding box.
     *
     * @param other The other room to check
     * @return true if rooms intersect or are too close
     */
    public boolean intersects(DungeonRoom other) {
        // If both rooms have shapes, use detailed collision
        if (shape != null && other.shape != null) {
            return shape.intersects(other.shape, 1.0f);
        }

        // Fall back to bounding box collision with 1 tile margin
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
     * Get the area of this room.
     * Delegates to shape if available, otherwise uses bounding box.
     *
     * @return Area in tiles
     */
    public int getArea() {
        if (shape != null) {
            return shape.getArea();
        }
        return width * height;
    }

    /**
     * Get perimeter points for this room (for entrance placement).
     * Delegates to shape if available, otherwise generates rectangle perimeter.
     *
     * @return List of points along the room's perimeter
     */
    public List<Vector2> getPerimeterPoints() {
        if (shape != null) {
            return shape.getPerimeterPoints();
        }

        // Legacy: Generate rectangular perimeter
        List<Vector2> points = new ArrayList<>();
        int samplesPerSide = Math.max(3, Math.max(width, height) / 4);

        // Top edge
        for (int i = 0; i < samplesPerSide; i++) {
            float t = i / (float) (samplesPerSide - 1);
            points.add(new Vector2(x + t * width, y + height));
        }

        // Right edge (skip corner)
        for (int i = 1; i < samplesPerSide; i++) {
            float t = i / (float) (samplesPerSide - 1);
            points.add(new Vector2(x + width, y + height - t * height));
        }

        // Bottom edge (skip corner)
        for (int i = 1; i < samplesPerSide; i++) {
            float t = i / (float) (samplesPerSide - 1);
            points.add(new Vector2(x + width - t * width, y));
        }

        // Left edge (skip both corners)
        for (int i = 1; i < samplesPerSide - 1; i++) {
            float t = i / (float) (samplesPerSide - 1);
            points.add(new Vector2(x, y + t * height));
        }

        return points;
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

    /**
     * Select an entrance point on this room's perimeter toward another room.
     * Finds the perimeter point closest to the other room's center, then adds random jitter.
     *
     * @param other The room to connect to
     * @param random Random generator for jitter
     * @return Entrance point in tile coordinates
     */
    public Vector2 selectEntranceToward(DungeonRoom other, Random random) {
        // Return cached entrance if already selected
        if (entrances.containsKey(other)) {
            return entrances.get(other);
        }

        // Get perimeter points from shape
        List<Vector2> perimeter = getPerimeterPoints();

        if (perimeter.isEmpty()) {
            // Fallback: use room center
            return center();
        }

        // Find perimeter point closest to other room's center
        Vector2 otherCenter = other.center();
        Vector2 bestPoint = perimeter.get(0);
        float bestDist = Float.MAX_VALUE;

        for (Vector2 p : perimeter) {
            float dist = p.dst(otherCenter);
            if (dist < bestDist) {
                bestDist = dist;
                bestPoint = p;
            }
        }

        // Add random jitter along perimeter (±20% of perimeter length)
        Vector2 entrance = addPerimeterJitter(bestPoint, perimeter, 0.2f, random);

        // Cache and return
        entrances.put(other, entrance);
        return entrance;
    }

    /**
     * Add random jitter to a perimeter point by moving along the perimeter.
     */
    private Vector2 addPerimeterJitter(Vector2 point, List<Vector2> perimeter, float jitterRatio, Random random) {
        // Find closest perimeter point (in case point isn't exactly on perimeter)
        int closestIndex = 0;
        float closestDist = Float.MAX_VALUE;

        for (int i = 0; i < perimeter.size(); i++) {
            float dist = point.dst(perimeter.get(i));
            if (dist < closestDist) {
                closestDist = dist;
                closestIndex = i;
            }
        }

        // Calculate jitter range
        int maxJitter = (int) (perimeter.size() * jitterRatio);
        int jitter = -maxJitter + random.nextInt(maxJitter * 2 + 1);

        // Apply jitter
        int newIndex = (closestIndex + jitter + perimeter.size()) % perimeter.size();
        return perimeter.get(newIndex).cpy();
    }

    @Override
    public String toString() {
        return String.format("Room[x=%.1f, y=%.1f, w=%d, h=%d, type=%s, size=%s, main=%b, shape=%s]",
                x, y, width, height, type, sizeCategory, isMainRoom,
                shape != null ? shape.getShapeType() : "legacy_rect");
    }
}
