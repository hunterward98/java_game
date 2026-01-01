package io.github.inherit_this.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

/**
 * Circular/elliptical room shape approximated as a regular polygon.
 * Creates smooth, rounded rooms that fit within a bounding box.
 */
public class CircularShape implements RoomShape {
    private final List<Vector2> vertices;
    private final Vector2 center;
    private final int area;
    private final Rectangle boundingBox;
    private final float radiusX;
    private final float radiusY;

    /**
     * Create a circular/elliptical shape that fits within the given bounding box.
     *
     * @param x X position of bounding box
     * @param y Y position of bounding box
     * @param width Width of bounding box
     * @param height Height of bounding box
     */
    public CircularShape(float x, float y, int width, int height) {
        this.center = new Vector2(x + width / 2f, y + height / 2f);
        this.radiusX = width / 2f * 0.9f;  // 90% to stay within bounds
        this.radiusY = height / 2f * 0.9f;

        // Generate vertices as regular polygon (20-32 sides for smooth appearance)
        int numSides = Math.max(20, Math.min(32, (width + height) / 4));
        this.vertices = generateCircleVertices(numSides);

        // Calculate area (approximate using polygon vertices)
        this.area = calculateArea();

        // Calculate bounding box
        this.boundingBox = new Rectangle(x, y, width, height);
    }

    /**
     * Generate vertices for circle/ellipse as a regular polygon.
     */
    private List<Vector2> generateCircleVertices(int numSides) {
        List<Vector2> verts = new ArrayList<>();

        for (int i = 0; i < numSides; i++) {
            float angle = (float) (i * 2 * Math.PI / numSides);
            float vx = center.x + (float) Math.cos(angle) * radiusX;
            float vy = center.y + (float) Math.sin(angle) * radiusY;
            verts.add(new Vector2(vx, vy));
        }

        return verts;
    }

    /**
     * Calculate area using shoelace formula on polygon vertices.
     */
    private int calculateArea() {
        float sum = 0;

        for (int i = 0; i < vertices.size(); i++) {
            Vector2 v1 = vertices.get(i);
            Vector2 v2 = vertices.get((i + 1) % vertices.size());
            sum += (v1.x * v2.y) - (v2.x * v1.y);
        }

        return Math.abs((int) (sum / 2f));
    }

    @Override
    public Vector2 getCenter() {
        return center.cpy();
    }

    @Override
    public int getArea() {
        return area;
    }

    @Override
    public Rectangle getBoundingBox() {
        return new Rectangle(boundingBox);
    }

    @Override
    public List<Vector2> getPerimeterPoints() {
        List<Vector2> points = new ArrayList<>();

        // Sample perimeter evenly (use vertices as perimeter points)
        int samplesPerSide = 2;  // 2 points between each vertex

        for (int i = 0; i < vertices.size(); i++) {
            Vector2 v1 = vertices.get(i);
            Vector2 v2 = vertices.get((i + 1) % vertices.size());

            for (int j = 0; j < samplesPerSide; j++) {
                float t = j / (float) samplesPerSide;
                points.add(new Vector2(
                    v1.x + (v2.x - v1.x) * t,
                    v1.y + (v2.y - v1.y) * t
                ));
            }
        }

        return points;
    }

    @Override
    public boolean intersects(RoomShape other, float margin) {
        Rectangle thisBounds = getBoundingBox();
        Rectangle otherBounds = other.getBoundingBox();

        // Expand by margin
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
        // Find bounding box
        int minX = (int) (center.x - radiusX) + offsetX;
        int minY = (int) (center.y - radiusY) + offsetY;
        int maxX = (int) (center.x + radiusX) + offsetX;
        int maxY = (int) (center.y + radiusY) + offsetY;

        // For each tile in bounding box, check if inside ellipse
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                // Bounds check
                if (x < 0 || x >= walls.length || y < 0 || y >= walls[0].length) {
                    continue;
                }

                // Check if point is inside ellipse
                float px = x - offsetX;
                float py = y - offsetY;
                float dx = (px - center.x) / radiusX;
                float dy = (py - center.y) / radiusY;

                if (dx * dx + dy * dy <= 1.0f) {
                    walls[x][y] = false;  // false = open space
                }
            }
        }
    }

    @Override
    public String getShapeType() {
        return "circular";
    }
}
