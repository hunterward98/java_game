package io.github.inherit_this.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Composite room shape made of 2-3 connected rectangles.
 * Creates L-shaped and T-shaped rooms for visual variety.
 */
public class CompositeShape implements RoomShape {
    private enum LayoutType {
        L_SHAPE,      // Two rectangles forming L
        T_SHAPE       // Three rectangles forming T
    }

    private final List<Rectangle> components;
    private final Vector2 center;
    private final int area;
    private final Rectangle boundingBox;
    private final LayoutType layout;

    /**
     * Create a composite shape that fits within the given bounding box.
     *
     * @param x X position of bounding box
     * @param y Y position of bounding box
     * @param width Width of bounding box
     * @param height Height in tiles
     * @param random Random generator for layout selection
     */
    public CompositeShape(float x, float y, int width, int height, Random random) {
        this.components = new ArrayList<>();

        // Randomly choose L or T layout (70% L, 30% T)
        this.layout = random.nextFloat() < 0.7f ? LayoutType.L_SHAPE : LayoutType.T_SHAPE;

        // Generate component rectangles
        if (layout == LayoutType.L_SHAPE) {
            generateLShape(x, y, width, height, random);
        } else {
            generateTShape(x, y, width, height, random);
        }

        // Calculate properties
        this.area = calculateTotalArea();
        this.center = calculateCentroid();
        this.boundingBox = calculateBoundingBox();
    }

    /**
     * Generate L-shaped room from two rectangles.
     * Main rectangle + perpendicular branch.
     */
    private void generateLShape(float x, float y, int width, int height, Random random) {
        // Determine orientation (4 possible L orientations)
        int orientation = random.nextInt(4);

        // Main rectangle dimensions (60-70% of total area)
        float mainAreaRatio = 0.6f + random.nextFloat() * 0.1f;

        switch (orientation) {
            case 0: // L opening to bottom-right
                {
                    // Vertical main rectangle (left side)
                    int mainWidth = Math.max(width / 3, (int) (width * 0.4f));
                    int mainHeight = height;
                    components.add(new Rectangle(x, y, mainWidth, mainHeight));

                    // Horizontal branch (bottom)
                    int branchWidth = width - mainWidth;
                    int branchHeight = Math.max(height / 3, (int) (height * (1 - mainAreaRatio)));
                    components.add(new Rectangle(x + mainWidth, y, branchWidth, branchHeight));
                }
                break;

            case 1: // L opening to bottom-left
                {
                    // Vertical main rectangle (right side)
                    int mainWidth = Math.max(width / 3, (int) (width * 0.4f));
                    int mainHeight = height;
                    components.add(new Rectangle(x + (width - mainWidth), y, mainWidth, mainHeight));

                    // Horizontal branch (bottom)
                    int branchWidth = width - mainWidth;
                    int branchHeight = Math.max(height / 3, (int) (height * (1 - mainAreaRatio)));
                    components.add(new Rectangle(x, y, branchWidth, branchHeight));
                }
                break;

            case 2: // L opening to top-right
                {
                    // Vertical main rectangle (left side)
                    int mainWidth = Math.max(width / 3, (int) (width * 0.4f));
                    int mainHeight = height;
                    components.add(new Rectangle(x, y, mainWidth, mainHeight));

                    // Horizontal branch (top)
                    int branchWidth = width - mainWidth;
                    int branchHeight = Math.max(height / 3, (int) (height * (1 - mainAreaRatio)));
                    components.add(new Rectangle(x + mainWidth, y + (height - branchHeight), branchWidth, branchHeight));
                }
                break;

            case 3: // L opening to top-left
                {
                    // Vertical main rectangle (right side)
                    int mainWidth = Math.max(width / 3, (int) (width * 0.4f));
                    int mainHeight = height;
                    components.add(new Rectangle(x + (width - mainWidth), y, mainWidth, mainHeight));

                    // Horizontal branch (top)
                    int branchWidth = width - mainWidth;
                    int branchHeight = Math.max(height / 3, (int) (height * (1 - mainAreaRatio)));
                    components.add(new Rectangle(x, y + (height - branchHeight), branchWidth, branchHeight));
                }
                break;
        }
    }

    /**
     * Generate T-shaped room from three rectangles.
     * Vertical stem + two horizontal branches.
     */
    private void generateTShape(float x, float y, int width, int height, Random random) {
        // T can be oriented horizontally or vertically
        boolean horizontal = random.nextBoolean();

        if (horizontal) {
            // Horizontal T: Vertical stem with two horizontal branches
            int stemWidth = Math.max(width / 4, (int) (width * 0.3f));
            int stemHeight = (int) (height * 0.6f);
            int stemX = (int) (x + (width - stemWidth) / 2);

            // Vertical stem (center)
            components.add(new Rectangle(stemX, y, stemWidth, stemHeight));

            // Top branch (full width)
            int branchHeight = height - stemHeight;
            components.add(new Rectangle(x, y + stemHeight, width, branchHeight));
        } else {
            // Vertical T: Horizontal stem with two vertical branches (like upside-down T)
            int stemWidth = (int) (width * 0.6f);
            int stemHeight = Math.max(height / 4, (int) (height * 0.3f));
            int stemY = (int) (y + (height - stemHeight) / 2);

            // Horizontal stem (center)
            components.add(new Rectangle(x, stemY, stemWidth, stemHeight));

            // Right branch (full height)
            int branchWidth = width - stemWidth;
            components.add(new Rectangle(x + stemWidth, y, branchWidth, height));
        }
    }

    private int calculateTotalArea() {
        int total = 0;
        for (Rectangle rect : components) {
            total += (int) (rect.width * rect.height);
        }
        return total;
    }

    private Vector2 calculateCentroid() {
        float sumX = 0;
        float sumY = 0;
        int totalArea = 0;

        for (Rectangle rect : components) {
            int rectArea = (int) (rect.width * rect.height);
            sumX += (rect.x + rect.width / 2f) * rectArea;
            sumY += (rect.y + rect.height / 2f) * rectArea;
            totalArea += rectArea;
        }

        return new Vector2(sumX / totalArea, sumY / totalArea);
    }

    private Rectangle calculateBoundingBox() {
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float maxY = Float.MIN_VALUE;

        for (Rectangle rect : components) {
            minX = Math.min(minX, rect.x);
            minY = Math.min(minY, rect.y);
            maxX = Math.max(maxX, rect.x + rect.width);
            maxY = Math.max(maxY, rect.y + rect.height);
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

        // Sample perimeter of each component rectangle
        for (Rectangle rect : components) {
            int samplesPerSide = Math.max(2, (int) Math.max(rect.width, rect.height) / 4);

            // Top edge
            for (int i = 0; i < samplesPerSide; i++) {
                float t = i / (float) (samplesPerSide - 1);
                points.add(new Vector2(rect.x + t * rect.width, rect.y + rect.height));
            }

            // Right edge
            for (int i = 1; i < samplesPerSide; i++) {
                float t = i / (float) (samplesPerSide - 1);
                points.add(new Vector2(rect.x + rect.width, rect.y + rect.height - t * rect.height));
            }

            // Bottom edge
            for (int i = 1; i < samplesPerSide; i++) {
                float t = i / (float) (samplesPerSide - 1);
                points.add(new Vector2(rect.x + rect.width - t * rect.width, rect.y));
            }

            // Left edge
            for (int i = 1; i < samplesPerSide - 1; i++) {
                float t = i / (float) (samplesPerSide - 1);
                points.add(new Vector2(rect.x, rect.y + t * rect.height));
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
        // Carve each component rectangle
        for (Rectangle rect : components) {
            int startX = (int) rect.x + offsetX;
            int startY = (int) rect.y + offsetY;
            int endX = startX + (int) rect.width;
            int endY = startY + (int) rect.height;

            for (int x = startX; x < endX; x++) {
                for (int y = startY; y < endY; y++) {
                    // Bounds check
                    if (x >= 0 && x < walls.length && y >= 0 && y < walls[0].length) {
                        walls[x][y] = false;  // false = open space
                    }
                }
            }
        }
    }

    @Override
    public String getShapeType() {
        return layout == LayoutType.L_SHAPE ? "l_shape" : "t_shape";
    }
}
