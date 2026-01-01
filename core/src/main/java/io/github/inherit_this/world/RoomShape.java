package io.github.inherit_this.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

/**
 * Defines the geometric shape of a dungeon room.
 * Decouples shape logic from room metadata (type, loot, etc).
 *
 * Implementations: RectangularShape, IrregularPolygonShape, CompositeShape, CircularShape
 */
public interface RoomShape {

    /**
     * Get the center point of the shape.
     * Used for graph algorithms (Delaunay, MST).
     */
    Vector2 getCenter();

    /**
     * Get the area of the shape in tiles.
     * Used for density balancing.
     */
    int getArea();

    /**
     * Get the axis-aligned bounding box.
     * Used for physics separation (fast collision detection).
     */
    Rectangle getBoundingBox();

    /**
     * Get points along the perimeter of the shape.
     * Used for random entrance placement.
     * Returns points in tile coordinates, evenly distributed.
     *
     * @return List of perimeter points (at least 4, typically 8-32)
     */
    List<Vector2> getPerimeterPoints();

    /**
     * Check if this shape intersects another shape.
     * Used for detailed collision detection during physics separation.
     *
     * @param other The other shape to check against
     * @param margin Minimum clearance in tiles (e.g., 1.0)
     * @return true if shapes overlap within margin
     */
    boolean intersects(RoomShape other, float margin);

    /**
     * Carve this shape into the walls grid.
     * Sets walls[x][y] = false for all tiles within the shape.
     *
     * @param walls The dungeon walls grid (true = wall, false = open space)
     * @param offsetX X offset to add to shape coordinates
     * @param offsetY Y offset to add to shape coordinates
     */
    void carve(boolean[][] walls, int offsetX, int offsetY);

    /**
     * Get the type name of this shape (for debugging).
     * Examples: "rectangular", "irregular_polygon", "composite", "circular"
     */
    String getShapeType();
}
