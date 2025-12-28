package io.github.inherit_this.world;

import com.badlogic.gdx.math.Vector2;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Procedural dungeon generator using Delaunay triangulation and MST algorithms.
 * Generates elaborate dungeons with natural-looking room placement and connections.
 *
 * Algorithm steps:
 * 1. Generate rooms in elliptical area with normal distribution
 * 2. Separate overlapping rooms using physics simulation
 * 3. Select "main rooms" exceeding size threshold
 * 4. Create Delaunay triangulation of main room centers
 * 5. Reduce to Minimum Spanning Tree
 * 6. Add back 8-10% of removed edges for loops
 * 7. Carve rooms and hallways
 * 8. Create impassable border
 */
public class ProceduralDungeonGenerator extends DungeonGenerator {

    private List<DungeonRoom> rooms;
    private List<DungeonRoom> mainRooms;
    private List<DelaunayTriangulator.Edge> hallwayEdges;

    // Configuration
    private static final int TARGET_ROOM_COUNT = 90;
    private static final int MIN_ROOM_SIZE = 5;
    private static final int MAX_ROOM_SIZE = 15;
    private static final float MAIN_ROOM_THRESHOLD = 1.25f;  // Rooms > 1.25x mean size
    private static final float LOOP_EDGE_PERCENT = 0.10f;    // Add back 10% of removed edges
    private static final int MAX_SEPARATION_ITERATIONS = 100;
    private static final float VELOCITY_DAMPING = 0.95f;
    private static final float STABILITY_THRESHOLD = 0.1f;

    public ProceduralDungeonGenerator(DungeonConfig config) {
        super(config);
        this.rooms = new ArrayList<>();
        this.mainRooms = new ArrayList<>();
        this.hallwayEdges = new ArrayList<>();
    }

    @Override
    public void generate() {
        // Start with all walls
        fillWithWalls();

        // Step 1: Generate rooms in elliptical area
        generateRoomsInEllipse();

        // Step 2: Separate overlapping rooms with physics
        separateRooms();

        // Step 3: Select main rooms for triangulation
        selectMainRooms();

        // Step 4: Delaunay triangulation
        List<Vector2> mainRoomCenters = mainRooms.stream()
            .map(DungeonRoom::center)
            .collect(Collectors.toList());

        DelaunayTriangulator triangulator = new DelaunayTriangulator();
        List<DelaunayTriangulator.Edge> triangulation = triangulator.triangulate(mainRoomCenters);

        // Step 5: Compute MST
        MinimumSpanningTree mstComputer = new MinimumSpanningTree();
        List<DelaunayTriangulator.Edge> mst = mstComputer.compute(triangulation);

        // Step 6: Add loop edges (8-10% of removed edges)
        hallwayEdges = addLoopEdges(mst, triangulation);

        // Step 7: Carve rooms and hallways
        carveRoomsAndHallways();

        // Step 8: Create impassable border
        createBorder();
    }

    /**
     * Generate rooms randomly positioned within an elliptical area.
     * Uses normal distribution for natural clustering toward center.
     */
    private void generateRoomsInEllipse() {
        // Ellipse dimensions (slightly smaller than dungeon to leave border)
        float ellipseWidth = widthInTiles * 0.9f;
        float ellipseHeight = heightInTiles * 0.9f;
        float centerX = widthInTiles / 2f;
        float centerY = heightInTiles / 2f;

        for (int i = 0; i < TARGET_ROOM_COUNT; i++) {
            // Use normal distribution for position (centered at dungeon center)
            float nx = (float) random.nextGaussian() * 0.3f;  // Standard deviation = 0.3
            float ny = (float) random.nextGaussian() * 0.3f;

            // Clamp to ellipse bounds
            nx = Math.max(-1f, Math.min(1f, nx));
            ny = Math.max(-1f, Math.min(1f, ny));

            // Convert to tile coordinates within ellipse
            float x = centerX + nx * ellipseWidth / 2f;
            float y = centerY + ny * ellipseHeight / 2f;

            // Random room size (normal distribution around mean)
            int avgSize = (MIN_ROOM_SIZE + MAX_ROOM_SIZE) / 2;
            int width = MIN_ROOM_SIZE + (int) (Math.abs(random.nextGaussian()) * (avgSize - MIN_ROOM_SIZE));
            int height = MIN_ROOM_SIZE + (int) (Math.abs(random.nextGaussian()) * (avgSize - MIN_ROOM_SIZE));

            width = Math.min(MAX_ROOM_SIZE, width);
            height = Math.min(MAX_ROOM_SIZE, height);

            DungeonRoom room = new DungeonRoom(x, y, width, height);
            rooms.add(room);
        }
    }

    /**
     * Separate overlapping rooms using physics-based simulation.
     * Uses spatial hashing for O(n) collision detection per iteration.
     */
    private void separateRooms() {
        SpatialGrid grid = new SpatialGrid(widthInTiles, heightInTiles, 8);

        for (int iteration = 0; iteration < MAX_SEPARATION_ITERATIONS; iteration++) {
            // Clear and rebuild spatial grid
            grid.clear();
            for (DungeonRoom room : rooms) {
                grid.insert(room);
            }

            // Apply separation forces
            boolean allStable = true;
            for (DungeonRoom room : rooms) {
                // Get nearby rooms from spatial grid
                List<DungeonRoom> nearby = grid.getNearby(room);

                // Apply repulsion forces from overlapping rooms
                for (DungeonRoom other : nearby) {
                    if (room != other && room.intersects(other)) {
                        applyRepulsionForce(room, other);
                        allStable = false;
                    }
                }
            }

            // Update positions and apply damping
            for (DungeonRoom room : rooms) {
                room.updatePosition(1.0f);
                room.applyDamping(VELOCITY_DAMPING);
            }

            // Check for convergence
            if (allStable || iteration > 50) {
                boolean converged = true;
                for (DungeonRoom room : rooms) {
                    if (!room.isStable(STABILITY_THRESHOLD)) {
                        converged = false;
                        break;
                    }
                }
                if (converged) {
                    break;
                }
            }
        }

        // Snap room positions to integers
        for (DungeonRoom room : rooms) {
            room.x = Math.round(room.x);
            room.y = Math.round(room.y);
        }
    }

    /**
     * Apply repulsion force between two overlapping rooms.
     */
    private void applyRepulsionForce(DungeonRoom room1, DungeonRoom room2) {
        Vector2 center1 = room1.center();
        Vector2 center2 = room2.center();

        float dx = center1.x - center2.x;
        float dy = center1.y - center2.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance < 0.1f) {
            // Rooms are at same position - apply random force
            dx = (random.nextFloat() - 0.5f) * 2f;
            dy = (random.nextFloat() - 0.5f) * 2f;
            distance = 1f;
        }

        // Normalize direction
        float dirX = dx / distance;
        float dirY = dy / distance;

        // Calculate overlap amount
        float minDist = (room1.width + room2.width) / 2f + (room1.height + room2.height) / 2f;
        float overlap = Math.max(0, minDist - distance);

        // Apply force proportional to overlap
        float forceMagnitude = overlap * 0.5f;
        room1.velocityX += dirX * forceMagnitude;
        room1.velocityY += dirY * forceMagnitude;
    }

    /**
     * Select "main rooms" that exceed size threshold.
     * These will be used for Delaunay triangulation.
     */
    private void selectMainRooms() {
        // Calculate mean room size
        float totalSize = 0;
        for (DungeonRoom room : rooms) {
            totalSize += (room.width + room.height) / 2f;
        }
        float meanSize = totalSize / rooms.size();

        // Select rooms larger than threshold
        for (DungeonRoom room : rooms) {
            float avgDimension = (room.width + room.height) / 2f;
            if (avgDimension >= meanSize * MAIN_ROOM_THRESHOLD) {
                room.isMainRoom = true;
                mainRooms.add(room);
            }
        }

        // Ensure we have at least 3 main rooms for triangulation
        if (mainRooms.size() < 3) {
            // Add largest rooms until we have 3
            rooms.sort((r1, r2) -> Integer.compare(r2.getArea(), r1.getArea()));
            for (int i = 0; i < Math.min(3, rooms.size()) && mainRooms.size() < 3; i++) {
                if (!rooms.get(i).isMainRoom) {
                    rooms.get(i).isMainRoom = true;
                    mainRooms.add(rooms.get(i));
                }
            }
        }
    }

    /**
     * Add loop edges back to MST for alternate paths.
     * Adds the specified percentage of removed edges, prioritizing shorter edges.
     */
    private List<DelaunayTriangulator.Edge> addLoopEdges(
            List<DelaunayTriangulator.Edge> mst,
            List<DelaunayTriangulator.Edge> triangulation) {

        // Start with MST edges
        List<DelaunayTriangulator.Edge> result = new ArrayList<>(mst);

        // Find edges that were removed (in triangulation but not in MST)
        List<DelaunayTriangulator.Edge> removedEdges = new ArrayList<>();
        for (DelaunayTriangulator.Edge edge : triangulation) {
            if (!mst.contains(edge)) {
                removedEdges.add(edge);
            }
        }

        // Sort removed edges by length (prefer shorter loops)
        removedEdges.sort(Comparator.comparingDouble(DelaunayTriangulator.Edge::length));

        // Add back a percentage of removed edges
        int edgesToAdd = Math.max(1, (int) (removedEdges.size() * LOOP_EDGE_PERCENT));
        for (int i = 0; i < Math.min(edgesToAdd, removedEdges.size()); i++) {
            result.add(removedEdges.get(i));
        }

        return result;
    }

    /**
     * Carve all rooms and hallways into the walls array.
     */
    private void carveRoomsAndHallways() {
        // Carve all rooms (not just main rooms)
        for (DungeonRoom room : rooms) {
            carveRoom(room);
        }

        // Carve hallways between connected rooms
        HallwayGenerator hallwayGen = new HallwayGenerator(3, random);
        hallwayGen.carveHallways(walls, hallwayEdges);
    }

    /**
     * Carve a single room into the walls array.
     */
    private void carveRoom(DungeonRoom room) {
        int startX = Math.round(room.x);
        int startY = Math.round(room.y);

        for (int dx = 0; dx < room.width; dx++) {
            for (int dy = 0; dy < room.height; dy++) {
                int x = startX + dx;
                int y = startY + dy;

                // Ensure within bounds (leave 1-tile border)
                if (x >= 1 && x < widthInTiles - 1 && y >= 1 && y < heightInTiles - 1) {
                    walls[x][y] = false;
                }
            }
        }
    }

    /**
     * Spatial grid for efficient collision detection.
     * Divides space into cells and tracks which rooms are in each cell.
     */
    private static class SpatialGrid {
        private final int gridWidth;
        private final int gridHeight;
        private final int cellSize;
        private final List<DungeonRoom>[][] grid;

        @SuppressWarnings("unchecked")
        public SpatialGrid(int worldWidth, int worldHeight, int cellSize) {
            this.cellSize = cellSize;
            this.gridWidth = (worldWidth + cellSize - 1) / cellSize;
            this.gridHeight = (worldHeight + cellSize - 1) / cellSize;
            this.grid = new List[gridWidth][gridHeight];

            for (int x = 0; x < gridWidth; x++) {
                for (int y = 0; y < gridHeight; y++) {
                    grid[x][y] = new ArrayList<>();
                }
            }
        }

        public void clear() {
            for (int x = 0; x < gridWidth; x++) {
                for (int y = 0; y < gridHeight; y++) {
                    grid[x][y].clear();
                }
            }
        }

        public void insert(DungeonRoom room) {
            // Insert room into all cells it overlaps
            int minCellX = Math.max(0, (int) room.x / cellSize);
            int maxCellX = Math.min(gridWidth - 1, (int) (room.x + room.width) / cellSize);
            int minCellY = Math.max(0, (int) room.y / cellSize);
            int maxCellY = Math.min(gridHeight - 1, (int) (room.y + room.height) / cellSize);

            for (int cx = minCellX; cx <= maxCellX; cx++) {
                for (int cy = minCellY; cy <= maxCellY; cy++) {
                    grid[cx][cy].add(room);
                }
            }
        }

        public List<DungeonRoom> getNearby(DungeonRoom room) {
            Set<DungeonRoom> nearby = new HashSet<>();

            // Get all rooms from cells this room overlaps plus adjacent cells
            int minCellX = Math.max(0, (int) room.x / cellSize - 1);
            int maxCellX = Math.min(gridWidth - 1, (int) (room.x + room.width) / cellSize + 1);
            int minCellY = Math.max(0, (int) room.y / cellSize - 1);
            int maxCellY = Math.min(gridHeight - 1, (int) (room.y + room.height) / cellSize + 1);

            for (int cx = minCellX; cx <= maxCellX; cx++) {
                for (int cy = minCellY; cy <= maxCellY; cy++) {
                    nearby.addAll(grid[cx][cy]);
                }
            }

            return new ArrayList<>(nearby);
        }
    }
}
