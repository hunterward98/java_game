package io.github.inherit_this.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Irregular polygon room shape with 5-8 sides.
 * Creates organic-looking rooms using randomized vertex placement.
 */
public class IrregularPolygonShape implements RoomShape {
    private final List<Vector2> vertices;
    private final Vector2 center;
    private final int area;
    private final Rectangle boundingBox;

    /**
     * Create an irregular polygon that fits within the given bounding box.
     *
     * @param x X position of bounding box
     * @param y Y position of bounding box
     * @param width Width of bounding box
     * @param height Height of bounding box
     * @param random Random generator for vertex placement
     */
    public IrregularPolygonShape(float x, float y, int width, int height, Random random) {
        // Choose number of sides (5-8 for good variety)
        int numSides = 5 + random.nextInt(4);

        // Generate vertices in polar coordinates, then convert to cartesian
        this.vertices = generatePolygonVertices(x, y, width, height, numSides, random);

        // Calculate center (average of vertices)
        this.center = calculateCentroid();

        // Calculate area using shoelace formula
        this.area = calculateArea();

        // Calculate bounding box
        this.boundingBox = calculateBoundingBox();
    }

    /**
     * Generate polygon vertices within bounding box.
     * Uses polar coordinates with randomized radius for organic shapes.
     */
    private List<Vector2> generatePolygonVertices(float x, float y, int width, int height, int numSides, Random random) {
        List<Vector2> verts = new ArrayList<>();

        // Center of bounding box
        float cx = x + width / 2f;
        float cy = y + height / 2f;

        // Maximum radius (half of smaller dimension)
        float maxRadiusX = width / 2f;
        float maxRadiusY = height / 2f;

        // Generate vertices in polar coordinates
        for (int i = 0; i < numSides; i++) {
            // Base angle evenly distributed
            float baseAngle = (float) (i * 2 * Math.PI / numSides);

            // Add angular jitter (±15 degrees)
            float angleJitter = (float) Math.toRadians(-15 + random.nextFloat() * 30);
            float angle = baseAngle + angleJitter;

            // Randomize radius (0.6-0.95 of max for variety)
            float radiusFactor = 0.6f + random.nextFloat() * 0.35f;

            // Calculate vertex position (elliptical, respecting bounding box aspect ratio)
            float vx = cx + (float) Math.cos(angle) * maxRadiusX * radiusFactor;
            float vy = cy + (float) Math.sin(angle) * maxRadiusY * radiusFactor;

            verts.add(new Vector2(vx, vy));
        }

        // Ensure convexity by sorting vertices by angle from center
        verts.sort((v1, v2) -> {
            float angle1 = (float) Math.atan2(v1.y - cy, v1.x - cx);
            float angle2 = (float) Math.atan2(v2.y - cy, v2.x - cx);
            return Float.compare(angle1, angle2);
        });

        return verts;
    }

    /**
     * Calculate centroid (center) of polygon.
     */
    private Vector2 calculateCentroid() {
        float sumX = 0;
        float sumY = 0;

        for (Vector2 v : vertices) {
            sumX += v.x;
            sumY += v.y;
        }

        return new Vector2(sumX / vertices.size(), sumY / vertices.size());
    }

    /**
     * Calculate polygon area using shoelace formula.
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

    /**
     * Calculate axis-aligned bounding box containing all vertices.
     */
    private Rectangle calculateBoundingBox() {
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float maxY = Float.MIN_VALUE;

        for (Vector2 v : vertices) {
            minX = Math.min(minX, v.x);
            minY = Math.min(minY, v.y);
            maxX = Math.max(maxX, v.x);
            maxY = Math.max(maxY, v.y);
        }

        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
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

        // Sample points along each edge
        int samplesPerEdge = 4;  // 4 points per edge

        for (int i = 0; i < vertices.size(); i++) {
            Vector2 v1 = vertices.get(i);
            Vector2 v2 = vertices.get((i + 1) % vertices.size());

            // Interpolate between vertices (skip last point to avoid duplicates)
            for (int j = 0; j < samplesPerEdge - 1; j++) {
                float t = j / (float) (samplesPerEdge - 1);
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

        // First check: bounding box collision (fast)
        if (!expandedThis.overlaps(otherBounds)) {
            return false;
        }

        // For irregular polygons, bounding box check is sufficient
        // (detailed polygon-polygon collision would be expensive)
        return true;
    }

    @Override
    public void carve(boolean[][] walls, int offsetX, int offsetY) {
        // Find bounding box of polygon
        int minX = (int) boundingBox.x + offsetX;
        int minY = (int) boundingBox.y + offsetY;
        int maxX = (int) (boundingBox.x + boundingBox.width) + offsetX;
        int maxY = (int) (boundingBox.y + boundingBox.height) + offsetY;

        // For each tile in bounding box, check if it's inside polygon
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                // Bounds check
                if (x < 0 || x >= walls.length || y < 0 || y >= walls[0].length) {
                    continue;
                }

                // Point-in-polygon test (ray casting algorithm)
                if (isPointInPolygon(x - offsetX, y - offsetY)) {
                    walls[x][y] = false;  // false = open space
                }
            }
        }
    }

    /**
     * Check if point is inside polygon using ray casting algorithm.
     */
    private boolean isPointInPolygon(float px, float py) {
        boolean inside = false;

        for (int i = 0, j = vertices.size() - 1; i < vertices.size(); j = i++) {
            Vector2 vi = vertices.get(i);
            Vector2 vj = vertices.get(j);

            if ((vi.y > py) != (vj.y > py) &&
                (px < (vj.x - vi.x) * (py - vi.y) / (vj.y - vi.y) + vi.x)) {
                inside = !inside;
            }
        }

        return inside;
    }

    @Override
    public String getShapeType() {
        return "irregular_polygon";
    }
}
