package io.github.inherit_this.world;

import java.util.Random;

/**
 * Factory for creating room shapes based on room type and size.
 * Distributes shape types to create varied, organic dungeons.
 */
public class RoomShapeFactory {
    private final Random random;

    public RoomShapeFactory(Random random) {
        this.random = random;
    }

    /**
     * Create an appropriate shape for a room based on its type and size.
     *
     * @param x X position of bounding box
     * @param y Y position of bounding box
     * @param width Width in tiles
     * @param height Height in tiles
     * @param type Room type
     * @param sizeCategory Size category
     * @return Room shape instance
     */
    public RoomShape createShape(float x, float y, int width, int height, RoomType type, RoomSize sizeCategory) {
        // Add aspect ratio variance to rectangles (not perfect squares)
        if (width == height) {
            // Make slightly non-square
            if (random.nextBoolean()) {
                width = (int) (width * (0.8f + random.nextFloat() * 0.4f));  // 0.8-1.2x
            } else {
                height = (int) (height * (0.8f + random.nextFloat() * 0.4f));
            }
        }

        // Distribution varies by room type
        float roll = random.nextFloat();

        switch (type) {
            case BOSS:
            case PLAZA:
                // Large important rooms: 40% irregular, 30% circular, 30% rectangular
                if (roll < 0.4f) {
                    return new IrregularPolygonShape(x, y, width, height, random);
                } else if (roll < 0.7f) {
                    return new CircularShape(x, y, width, height);
                } else {
                    return new RectangularShape(x, y, width, height);
                }

            case BARRACKS:
            case LIBRARY:
            case ARMORY:
                // Medium special rooms: 30% L/T-shaped, 40% rectangular, 30% irregular
                if (roll < 0.3f) {
                    return new CompositeShape(x, y, width, height, random);
                } else if (roll < 0.7f) {
                    return new RectangularShape(x, y, width, height);
                } else {
                    return new IrregularPolygonShape(x, y, width, height, random);
                }

            case TREASURE:
            case SECRET:
                // Treasure/secret rooms: 50% circular, 30% irregular, 20% rectangular
                if (roll < 0.5f) {
                    return new CircularShape(x, y, width, height);
                } else if (roll < 0.8f) {
                    return new IrregularPolygonShape(x, y, width, height, random);
                } else {
                    return new RectangularShape(x, y, width, height);
                }

            case SHRINE:
                // Shrines: 60% circular, 40% irregular (mystical feel)
                if (roll < 0.6f) {
                    return new CircularShape(x, y, width, height);
                } else {
                    return new IrregularPolygonShape(x, y, width, height, random);
                }

            case STANDARD:
            default:
                // Standard rooms: mostly rectangular with some variety
                // 75% rectangular, 15% L-shape, 10% irregular
                if (roll < 0.75f) {
                    return new RectangularShape(x, y, width, height);
                } else if (roll < 0.90f) {
                    // Only use composite for larger standard rooms
                    if (width >= 20 && height >= 20) {
                        return new CompositeShape(x, y, width, height, random);
                    } else {
                        return new RectangularShape(x, y, width, height);
                    }
                } else {
                    return new IrregularPolygonShape(x, y, width, height, random);
                }
        }
    }

    /**
     * Create a rectangular shape (for backward compatibility or specific use).
     */
    public RoomShape createRectangular(float x, float y, int width, int height) {
        return new RectangularShape(x, y, width, height);
    }
}
