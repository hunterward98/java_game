package io.github.inherit_this.world;

import com.badlogic.gdx.math.Vector2;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Procedural dungeon generator using Delaunay triangulation and MST algorithms.
 * Generates elaborate dungeons with natural-looking room placement and connections.
 *
 * Algorithm steps:
 * 1. Generate rooms by type with appropriate sizes
 * 2. Balance density to target range (20-30%)
 * 3. Separate overlapping rooms using physics simulation
 * 4. Select "main rooms" exceeding size threshold
 * 5. Create Delaunay triangulation of main room centers
 * 6. Reduce to Minimum Spanning Tree
 * 7. Add back 8-10% of removed edges for loops
 * 8. Carve rooms and hallways with variable widths
 * 9. Create impassable border
 */
public class ProceduralDungeonGenerator extends DungeonGenerator {

    private List<DungeonRoom> rooms;
    private List<DungeonRoom> mainRooms;
    private List<DelaunayTriangulator.Edge> hallwayEdges;

    // Configuration
    private static final int TARGET_ROOM_COUNT = 200;        // Rooms for 384×384 dungeon
    private static final float MAIN_ROOM_THRESHOLD = 1.25f;  // Rooms > 1.25x mean size
    private static final float LOOP_EDGE_PERCENT = 0.10f;    // Add back 10% of removed edges
    private static final int MAX_SEPARATION_ITERATIONS = 100;
    private static final float VELOCITY_DAMPING = 0.95f;
    private static final float STABILITY_THRESHOLD = 0.1f;

    // Density balancing
    private static final float MIN_DENSITY = 0.20f;          // 20% minimum coverage
    private static final float MAX_DENSITY = 0.30f;          // 30% maximum coverage
    private static final float TARGET_DENSITY = 0.25f;       // 25% target coverage

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

        // Step 1: Generate rooms by type with appropriate sizes
        generateRoomsWithVariety();

        // Step 2: Balance density to target range (20-30%)
        balanceDensity();

        // Step 3: Separate overlapping rooms with physics
        separateRooms();

        // Step 4: Delaunay triangulation on ALL rooms (not just main rooms)
        List<Vector2> allRoomCenters = rooms.stream()
            .map(DungeonRoom::center)
            .collect(Collectors.toList());

        DelaunayTriangulator triangulator = new DelaunayTriangulator();
        List<DelaunayTriangulator.Edge> triangulation = triangulator.triangulate(allRoomCenters);

        // Step 5: Compute MST (pure tree - ensures all rooms connected with exactly one path)
        MinimumSpanningTree mstComputer = new MinimumSpanningTree();
        hallwayEdges = mstComputer.compute(triangulation);

        // Step 6: Mark larger rooms for future boss spawning, loot, etc.
        markImportantRooms();

        // Step 7: Carve rooms and hallways with variable widths
        carveRoomsAndHallways();

        // Step 8: Create impassable border
        createBorder();
    }

    /**
     * Generate rooms with variety in types and sizes.
     * Creates boss rooms, barracks, treasure rooms, plazas, and standard rooms.
     */
    private void generateRoomsWithVariety() {
        float centerX = widthInTiles / 2f;
        float centerY = heightInTiles / 2f;
        float ellipseWidth = widthInTiles * 0.9f;
        float ellipseHeight = heightInTiles * 0.9f;

        // 1. Generate 1-2 BOSS rooms (guaranteed, HUGE or MASSIVE)
        generateRoomsByType(RoomType.BOSS, 2, centerX, centerY, ellipseWidth, ellipseHeight);

        // 2. Generate 5-8 LARGE special rooms (BARRACKS, PLAZAS)
        generateRoomsByType(RoomType.BARRACKS, 3, centerX, centerY, ellipseWidth, ellipseHeight);
        generateRoomsByType(RoomType.PLAZA, 3, centerX, centerY, ellipseWidth, ellipseHeight);

        // 3. Generate 15-25 MEDIUM rooms (TREASURE, LIBRARY, ARMORY)
        generateRoomsByType(RoomType.TREASURE, 8, centerX, centerY, ellipseWidth, ellipseHeight);
        generateRoomsByType(RoomType.LIBRARY, 5, centerX, centerY, ellipseWidth, ellipseHeight);
        generateRoomsByType(RoomType.ARMORY, 5, centerX, centerY, ellipseWidth, ellipseHeight);
        generateRoomsByType(RoomType.SHRINE, 7, centerX, centerY, ellipseWidth, ellipseHeight);

        // 4. Generate 5-10 SECRET rooms (off-path treasures)
        generateRoomsByType(RoomType.SECRET, 7, centerX, centerY, ellipseWidth, ellipseHeight);

        // 5. Fill remaining with STANDARD rooms to reach target count
        int remaining = TARGET_ROOM_COUNT - rooms.size();
        generateRoomsByType(RoomType.STANDARD, remaining, centerX, centerY, ellipseWidth, ellipseHeight);
    }

    /**
     * Generate a specific number of rooms of a given type.
     */
    private void generateRoomsByType(RoomType type, int count, float centerX, float centerY,
                                     float ellipseWidth, float ellipseHeight) {
        for (int i = 0; i < count; i++) {
            // Random position using Gaussian distribution
            float nx = (float) random.nextGaussian() * 0.3f;
            float ny = (float) random.nextGaussian() * 0.3f;
            nx = Math.max(-1f, Math.min(1f, nx));
            ny = Math.max(-1f, Math.min(1f, ny));

            float x = centerX + nx * ellipseWidth / 2f;
            float y = centerY + ny * ellipseHeight / 2f;

            // Get random allowed size for this room type
            RoomSize sizeCategory = type.getRandomAllowedSize(random);
            int width = sizeCategory.getRandomSize(random);
            int height = sizeCategory.getRandomSize(random);

            DungeonRoom room = new DungeonRoom(x, y, width, height, type, sizeCategory);
            rooms.add(room);
        }
    }

    /**
     * Calculate current density (room area / total dungeon area).
     */
    private float calculateDensity() {
        int totalRoomArea = 0;
        for (DungeonRoom room : rooms) {
            totalRoomArea += room.getArea();
        }
        int totalDungeonArea = widthInTiles * heightInTiles;
        return (float) totalRoomArea / totalDungeonArea;
    }

    /**
     * Balance density to target range (20-30%) by adjusting room sizes.
     * Grows smaller rooms if density too low, shrinks larger rooms if too high.
     */
    private void balanceDensity() {
        float density = calculateDensity();
        int iterations = 0;
        final int MAX_BALANCE_ITERATIONS = 10;

        while ((density < MIN_DENSITY || density > MAX_DENSITY) && iterations < MAX_BALANCE_ITERATIONS) {
            if (density < MIN_DENSITY) {
                // Density too low - grow some smaller rooms
                growSmallerRooms();
            } else if (density > MAX_DENSITY) {
                // Density too high - shrink some larger rooms
                shrinkLargerRooms();
            }

            density = calculateDensity();
            iterations++;
        }
    }

    /**
     * Grow smaller rooms to increase density.
     */
    private void growSmallerRooms() {
        // Sort rooms by area (smallest first)
        rooms.sort(Comparator.comparingInt(DungeonRoom::getArea));

        // Grow bottom 20% of rooms
        int roomsToGrow = Math.max(1, rooms.size() / 5);
        for (int i = 0; i < roomsToGrow && i < rooms.size(); i++) {
            DungeonRoom room = rooms.get(i);

            // Get next larger size category if available
            RoomSize currentSize = room.sizeCategory;
            RoomSize nextSize = getNextLargerSize(currentSize);

            if (nextSize != null && room.type.allowsSize(nextSize)) {
                // Upgrade to next size category
                room.sizeCategory = nextSize;
                room.width = nextSize.getRandomSize(random);
                room.height = nextSize.getRandomSize(random);
            } else {
                // Just grow within current category
                room.width = Math.min(room.width + 2, currentSize.getMax());
                room.height = Math.min(room.height + 2, currentSize.getMax());
            }
        }
    }

    /**
     * Shrink larger rooms to decrease density.
     */
    private void shrinkLargerRooms() {
        // Sort rooms by area (largest first)
        rooms.sort((r1, r2) -> Integer.compare(r2.getArea(), r1.getArea()));

        // Shrink top 20% of rooms
        int roomsToShrink = Math.max(1, rooms.size() / 5);
        for (int i = 0; i < roomsToShrink && i < rooms.size(); i++) {
            DungeonRoom room = rooms.get(i);

            // Get next smaller size category if available
            RoomSize currentSize = room.sizeCategory;
            RoomSize nextSize = getNextSmallerSize(currentSize);

            if (nextSize != null && room.type.allowsSize(nextSize)) {
                // Downgrade to next size category
                room.sizeCategory = nextSize;
                room.width = nextSize.getRandomSize(random);
                room.height = nextSize.getRandomSize(random);
            } else {
                // Just shrink within current category
                room.width = Math.max(room.width - 2, currentSize.getMin());
                room.height = Math.max(room.height - 2, currentSize.getMin());
            }
        }
    }

    /**
     * Get the next larger room size category.
     */
    private RoomSize getNextLargerSize(RoomSize current) {
        switch (current) {
            case SMALL: return RoomSize.MEDIUM;
            case MEDIUM: return RoomSize.LARGE;
            case LARGE: return RoomSize.HUGE;
            case HUGE: return RoomSize.MASSIVE;
            case MASSIVE: return null;
            default: return null;
        }
    }

    /**
     * Get the next smaller room size category.
     */
    private RoomSize getNextSmallerSize(RoomSize current) {
        switch (current) {
            case MASSIVE: return RoomSize.HUGE;
            case HUGE: return RoomSize.LARGE;
            case LARGE: return RoomSize.MEDIUM;
            case MEDIUM: return RoomSize.SMALL;
            case SMALL: return null;
            default: return null;
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
     * Mark important rooms (larger rooms for boss spawns, special loot, etc.).
     * All rooms are now connected, but we still want to track which are "important".
     */
    private void markImportantRooms() {
        // Mark BOSS, PLAZA, and BARRACKS rooms as "main rooms" for gameplay purposes
        for (DungeonRoom room : rooms) {
            if (room.type == RoomType.BOSS ||
                room.type == RoomType.PLAZA ||
                room.type == RoomType.BARRACKS) {
                room.isMainRoom = true;
                mainRooms.add(room);
            }
        }
    }

    // Removed addLoopEdges() - no longer needed since we use pure MST (tree structure)

    /**
     * Carve all rooms and hallways into the walls array.
     * Uses variable hallway widths (3, 5, or 7) based on connected room types.
     */
    private void carveRoomsAndHallways() {
        // Carve all rooms (not just main rooms)
        for (DungeonRoom room : rooms) {
            carveRoom(room);
        }

        // Carve hallways with variable widths based on room types
        for (DelaunayTriangulator.Edge edge : hallwayEdges) {
            // Find rooms connected by this edge
            DungeonRoom room1 = findRoomByCenter(edge.a);
            DungeonRoom room2 = findRoomByCenter(edge.b);

            // Determine hallway width (use max of both rooms for grand corridors)
            int width1 = room1 != null ? room1.getHallwayWidth() : 3;
            int width2 = room2 != null ? room2.getHallwayWidth() : 3;
            int hallwayWidth = Math.max(width1, width2);

            // Carve hallway with appropriate width
            HallwayGenerator hallwayGen = new HallwayGenerator(hallwayWidth, random);
            hallwayGen.carveHallway(walls, edge.a, edge.b);
        }
    }

    /**
     * Find a room by its center point (used for hallway connection).
     */
    private DungeonRoom findRoomByCenter(Vector2 center) {
        // Search ALL rooms now (not just main rooms)
        for (DungeonRoom room : rooms) {
            Vector2 roomCenter = room.center();
            float dist = roomCenter.dst(center);
            if (dist < 1.0f) {  // Close enough to be considered the same point
                return room;
            }
        }
        return null;
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
