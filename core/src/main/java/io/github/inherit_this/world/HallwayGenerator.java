package io.github.inherit_this.world;

import com.badlogic.gdx.math.Vector2;

import java.util.List;
import java.util.Random;

/**
 * Generates L-shaped corridors between rooms for dungeon layouts.
 * Hallways can be horizontal-then-vertical or vertical-then-horizontal.
 */
public class HallwayGenerator {
    private final int corridorWidth;
    private final Random random;

    /**
     * Create a hallway generator with specified corridor width.
     *
     * @param corridorWidth Width of corridors in tiles
     * @param random Random generator for choosing hallway orientation
     */
    public HallwayGenerator(int corridorWidth, Random random) {
        this.corridorWidth = Math.max(1, corridorWidth);
        this.random = random;
    }

    /**
     * Carve L-shaped hallways into the walls array for all edges.
     *
     * @param walls The dungeon walls array to modify
     * @param edges List of edges representing room connections
     */
    public void carveHallways(boolean[][] walls, List<DelaunayTriangulator.Edge> edges) {
        for (DelaunayTriangulator.Edge edge : edges) {
            carveHallway(walls, edge.a, edge.b);
        }
    }

    /**
     * Carve an L-shaped hallway between two points.
     * Randomly chooses horizontal-first or vertical-first orientation.
     *
     * @param walls The dungeon walls array to modify
     * @param start Start point (room center)
     * @param end End point (room center)
     */
    public void carveHallway(boolean[][] walls, Vector2 start, Vector2 end) {
        int x1 = Math.round(start.x);
        int y1 = Math.round(start.y);
        int x2 = Math.round(end.x);
        int y2 = Math.round(end.y);

        // Randomly choose hallway orientation
        if (random.nextBoolean()) {
            // Horizontal then vertical
            carveHorizontalCorridor(walls, x1, x2, y1);
            carveVerticalCorridor(walls, y1, y2, x2);
        } else {
            // Vertical then horizontal
            carveVerticalCorridor(walls, y1, y2, x1);
            carveHorizontalCorridor(walls, x1, x2, y2);
        }
    }

    /**
     * Carve a horizontal corridor between two x-coordinates at a given y.
     *
     * @param walls The dungeon walls array to modify
     * @param x1 Start x-coordinate
     * @param x2 End x-coordinate
     * @param y Y-coordinate of the corridor
     */
    private void carveHorizontalCorridor(boolean[][] walls, int x1, int x2, int y) {
        int startX = Math.min(x1, x2);
        int endX = Math.max(x1, x2);

        for (int x = startX; x <= endX; x++) {
            for (int dy = 0; dy < corridorWidth; dy++) {
                int corridorY = y + dy - corridorWidth / 2;

                // Ensure within bounds (leave 1-tile border)
                if (x >= 1 && x < walls.length - 1 &&
                    corridorY >= 1 && corridorY < walls[0].length - 1) {
                    walls[x][corridorY] = false;
                }
            }
        }
    }

    /**
     * Carve a vertical corridor between two y-coordinates at a given x.
     *
     * @param walls The dungeon walls array to modify
     * @param y1 Start y-coordinate
     * @param y2 End y-coordinate
     * @param x X-coordinate of the corridor
     */
    private void carveVerticalCorridor(boolean[][] walls, int y1, int y2, int x) {
        int startY = Math.min(y1, y2);
        int endY = Math.max(y1, y2);

        for (int y = startY; y <= endY; y++) {
            for (int dx = 0; dx < corridorWidth; dx++) {
                int corridorX = x + dx - corridorWidth / 2;

                // Ensure within bounds (leave 1-tile border)
                if (corridorX >= 1 && corridorX < walls.length - 1 &&
                    y >= 1 && y < walls[0].length - 1) {
                    walls[corridorX][y] = false;
                }
            }
        }
    }

    /**
     * Carve straight hallways (no L-shape) between points.
     * Useful for debugging or simple connections.
     *
     * @param walls The dungeon walls array to modify
     * @param start Start point
     * @param end End point
     */
    public void carveStraightHallway(boolean[][] walls, Vector2 start, Vector2 end) {
        int x1 = Math.round(start.x);
        int y1 = Math.round(start.y);
        int x2 = Math.round(end.x);
        int y2 = Math.round(end.y);

        // Bresenham's line algorithm for straight corridor
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        int x = x1;
        int y = y1;

        while (true) {
            // Carve corridor at current position
            for (int i = -corridorWidth / 2; i <= corridorWidth / 2; i++) {
                for (int j = -corridorWidth / 2; j <= corridorWidth / 2; j++) {
                    int cx = x + i;
                    int cy = y + j;
                    if (cx >= 1 && cx < walls.length - 1 &&
                        cy >= 1 && cy < walls[0].length - 1) {
                        walls[cx][cy] = false;
                    }
                }
            }

            if (x == x2 && y == y2) {
                break;
            }

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x += sx;
            }
            if (e2 < dx) {
                err += dx;
                y += sy;
            }
        }
    }
}
