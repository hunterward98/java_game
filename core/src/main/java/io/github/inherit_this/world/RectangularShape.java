package io.github.inherit_this.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

/**
 * Rectangular room shape - axis-aligned rectangle.
 * Mirrors the current dungeon generation behavior for backward compatibility.
 */
public class RectangularShape implements RoomShape {
    private final float x;
    private final float y;
    private final int width;
    private final int height;

    /**
     * Create a rectangular shape.
     *
     * @param x X position in tiles
     * @param y Y position in tiles
     * @param width Width in tiles
     * @param height Height in tiles
     */
    public RectangularShape(float x, float y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public Vector2 getCenter() {
        return new Vector2(x + width / 2f, y + height / 2f);
    }

    @Override
    public int getArea() {
        return width * height;
    }

    @Override
    public Rectangle getBoundingBox() {
        return new Rectangle(x, y, width, height);
    }

    @Override
    public List<Vector2> getPerimeterPoints() {
        List<Vector2> points = new ArrayList<>();

        // Sample perimeter with 1 point per 4 tiles (minimum 12 points total)
        int samplesPerSide = Math.max(3, Math.max(width, height) / 4);

        // Top edge (left to right)
        for (int i = 0; i < samplesPerSide; i++) {
            float t = i / (float) (samplesPerSide - 1);
            points.add(new Vector2(x + t * width, y + height));
        }

        // Right edge (top to bottom, skip corner)
        for (int i = 1; i < samplesPerSide; i++) {
            float t = i / (float) (samplesPerSide - 1);
            points.add(new Vector2(x + width, y + height - t * height));
        }

        // Bottom edge (right to left, skip corner)
        for (int i = 1; i < samplesPerSide; i++) {
            float t = i / (float) (samplesPerSide - 1);
            points.add(new Vector2(x + width - t * width, y));
        }

        // Left edge (bottom to top, skip both corners)
        for (int i = 1; i < samplesPerSide - 1; i++) {
            float t = i / (float) (samplesPerSide - 1);
            points.add(new Vector2(x, y + t * height));
        }

        return points;
    }

    @Override
    public boolean intersects(RoomShape other, float margin) {
        Rectangle thisBounds = getBoundingBox();
        Rectangle otherBounds = other.getBoundingBox();

        // Expand bounds by margin for clearance check
        Rectangle expandedThis = new Rectangle(
            thisBounds.x - margin,
            thisBounds.y - margin,
            thisBounds.width + margin * 2,
            thisBounds.height + margin * 2
        );

        return expandedThis.overlaps(otherBounds);
    }

    @Override
    public void carve(boolean[][] walls, int offsetX, int offsetY) {
        int startX = (int) x + offsetX;
        int startY = (int) y + offsetY;

        for (int dx = 0; dx < width; dx++) {
            for (int dy = 0; dy < height; dy++) {
                int wx = startX + dx;
                int wy = startY + dy;

                // Bounds check
                if (wx >= 0 && wx < walls.length && wy >= 0 && wy < walls[0].length) {
                    walls[wx][wy] = false;  // false = open space
                }
            }
        }
    }

    @Override
    public String getShapeType() {
        return "rectangular";
    }

    // Getters for direct access (used during migration)
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
