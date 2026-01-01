package io.github.inherit_this.world;

import com.badlogic.gdx.math.Vector2;

import java.util.*;

/**
 * A* pathfinding for dungeon hallway generation.
 * Finds paths that route around obstacles (rooms) with support for variable corridor widths.
 */
public class AStarPathfinder {
    private static final long TIMEOUT_MS = 500;  // 500ms timeout per path

    /**
     * Node in the A* search grid.
     */
    private static class Node implements Comparable<Node> {
        int x, y;
        float gCost;  // Distance from start
        float hCost;  // Heuristic distance to goal
        float fCost;  // gCost + hCost
        Node parent;

        Node(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int compareTo(Node other) {
            return Float.compare(this.fCost, other.fCost);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Node)) return false;
            Node other = (Node) obj;
            return x == other.x && y == other.y;
        }

        @Override
        public int hashCode() {
            return x * 31 + y;
        }
    }

    /**
     * Find path from start to end using A* algorithm.
     * Routes around walls (rooms) while ensuring corridor width clearance.
     *
     * @param start Starting point in tile coordinates
     * @param end Ending point in tile coordinates
     * @param walls Dungeon walls grid (true = wall/obstacle, false = open space)
     * @param corridorWidth Width of corridor in tiles (must have clearance)
     * @return List of waypoints from start to end, or null if no path found
     */
    public List<Vector2> findPath(Vector2 start, Vector2 end, boolean[][] walls, int corridorWidth) {
        int startX = (int) start.x;
        int startY = (int) start.y;
        int endX = (int) end.x;
        int endY = (int) end.y;

        // Bounds check
        if (!isInBounds(startX, startY, walls) || !isInBounds(endX, endY, walls)) {
            return null;
        }

        long startTime = System.currentTimeMillis();
        int clearance = corridorWidth / 2;

        // A* data structures
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Set<Node> closedSet = new HashSet<>();
        Map<Integer, Node> allNodes = new HashMap<>();

        // Start node
        Node startNode = new Node(startX, startY);
        startNode.gCost = 0;
        startNode.hCost = heuristic(startX, startY, endX, endY);
        startNode.fCost = startNode.hCost;
        openSet.add(startNode);
        allNodes.put(getKey(startX, startY), startNode);

        // 8-directional movement (including diagonals for smoother paths)
        int[][] directions = {
            {0, 1}, {1, 0}, {0, -1}, {-1, 0},  // Cardinal
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}  // Diagonal
        };

        while (!openSet.isEmpty()) {
            // Timeout check
            if (System.currentTimeMillis() - startTime > TIMEOUT_MS) {
                return null;  // Timeout - fallback to L-shape
            }

            Node current = openSet.poll();
            closedSet.add(current);

            // Goal reached
            if (current.x == endX && current.y == endY) {
                return reconstructPath(current);
            }

            // Explore neighbors
            for (int[] dir : directions) {
                int nx = current.x + dir[0];
                int ny = current.y + dir[1];

                // Skip if out of bounds or in closed set
                if (!isInBounds(nx, ny, walls)) continue;

                Node neighbor = allNodes.computeIfAbsent(getKey(nx, ny), k -> new Node(nx, ny));
                if (closedSet.contains(neighbor)) continue;

                // Check if this position has required clearance for corridor width
                if (!hasRequiredClearance(nx, ny, walls, clearance)) {
                    continue;
                }

                // Calculate costs
                float moveCost = (dir[0] != 0 && dir[1] != 0) ? 1.414f : 1.0f;  // Diagonal costs more
                float newGCost = current.gCost + moveCost;

                // Add penalty for direction changes (encourages smooth curves)
                if (current.parent != null) {
                    int prevDirX = current.x - current.parent.x;
                    int prevDirY = current.y - current.parent.y;
                    if (prevDirX != dir[0] || prevDirY != dir[1]) {
                        newGCost += 0.1f;  // Small penalty for direction change
                    }
                }

                // Update neighbor if this path is better
                if (!openSet.contains(neighbor) || newGCost < neighbor.gCost) {
                    neighbor.parent = current;
                    neighbor.gCost = newGCost;
                    neighbor.hCost = heuristic(nx, ny, endX, endY);
                    neighbor.fCost = neighbor.gCost + neighbor.hCost;

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        // No path found
        return null;
    }

    /**
     * Euclidean distance heuristic.
     */
    private float heuristic(int x1, int y1, int x2, int y2) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Check if position is within grid bounds.
     */
    private boolean isInBounds(int x, int y, boolean[][] walls) {
        return x >= 0 && x < walls.length && y >= 0 && y < walls[0].length;
    }

    /**
     * Check if position has required clearance in all directions for corridor width.
     */
    private boolean hasRequiredClearance(int x, int y, boolean[][] walls, int clearance) {
        // Check that tiles within 'clearance' distance in all directions are open
        for (int dx = -clearance; dx <= clearance; dx++) {
            for (int dy = -clearance; dy <= clearance; dy++) {
                int cx = x + dx;
                int cy = y + dy;

                if (!isInBounds(cx, cy, walls)) {
                    return false;  // Too close to boundary
                }

                if (walls[cx][cy]) {
                    return false;  // Too close to wall/room
                }
            }
        }

        return true;
    }

    /**
     * Reconstruct path from end node back to start.
     */
    private List<Vector2> reconstructPath(Node endNode) {
        List<Vector2> path = new ArrayList<>();
        Node current = endNode;

        while (current != null) {
            path.add(new Vector2(current.x, current.y));
            current = current.parent;
        }

        Collections.reverse(path);
        return path;
    }

    /**
     * Generate unique key for node coordinates.
     */
    private int getKey(int x, int y) {
        return x * 10000 + y;  // Assumes dungeon < 10000 tiles wide
    }
}
