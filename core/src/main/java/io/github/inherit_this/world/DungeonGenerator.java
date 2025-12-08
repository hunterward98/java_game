package io.github.inherit_this.world;

import io.github.inherit_this.util.Constants;
import java.util.*;

/**
 * Generates procedural dungeon layouts using maze and room algorithms.
 * Supports seed-based generation for reproducibility.
 */
public class DungeonGenerator {

    private final DungeonConfig config;
    private final Random random;

    // Dungeon grid in TILES (not chunks)
    private final int widthInTiles;
    private final int heightInTiles;
    private boolean[][] walls;  // true = wall, false = floor

    // Track rooms for open dungeons
    private List<Room> rooms;

    public DungeonGenerator(DungeonConfig config) {
        this.config = config;
        this.random = new Random(config.getSeed());
        this.widthInTiles = config.getWidthInChunks() * Constants.CHUNK_SIZE;
        this.heightInTiles = config.getHeightInChunks() * Constants.CHUNK_SIZE;
        this.walls = new boolean[widthInTiles][heightInTiles];
        this.rooms = new ArrayList<>();
    }

    /**
     * Generate the dungeon layout.
     */
    public void generate() {
        // Start with all walls
        fillWithWalls();

        // Generate based on style
        if (config.getStyle() == DungeonConfig.DungeonStyle.OPEN) {
            generateRoomBased();
        } else {
            generateMazeBased();
        }

        // Add 2-tile tall border walls
        createBorder();
    }

    /**
     * Fill entire grid with walls.
     */
    private void fillWithWalls() {
        for (int x = 0; x < widthInTiles; x++) {
            for (int y = 0; y < heightInTiles; y++) {
                walls[x][y] = true;
            }
        }
    }

    /**
     * Generate using recursive backtracker maze algorithm.
     * Good for NARROW/WINDING dungeons.
     */
    private void generateMazeBased() {
        int cellSize = config.getCorridorWidth() + 1;  // Cell size in tiles
        int mazeWidth = (widthInTiles - 2) / cellSize;  // -2 for border
        int mazeHeight = (heightInTiles - 2) / cellSize;

        boolean[][] visited = new boolean[mazeWidth][mazeHeight];
        Stack<int[]> stack = new Stack<>();

        // Start at random position
        int startX = random.nextInt(mazeWidth);
        int startY = random.nextInt(mazeHeight);
        stack.push(new int[]{startX, startY});
        visited[startX][startY] = true;

        // Carve starting cell
        carveMazeCell(startX, startY, cellSize);

        // Recursive backtracker
        while (!stack.isEmpty()) {
            int[] current = stack.peek();
            int x = current[0];
            int y = current[1];

            // Get unvisited neighbors
            List<int[]> neighbors = getUnvisitedNeighbors(x, y, mazeWidth, mazeHeight, visited);

            if (neighbors.isEmpty()) {
                stack.pop();
            } else {
                // Choose random neighbor
                int[] next = neighbors.get(random.nextInt(neighbors.size()));
                int nx = next[0];
                int ny = next[1];

                // Carve path between current and next
                carvePathBetween(x, y, nx, ny, cellSize);
                carveMazeCell(nx, ny, cellSize);

                visited[nx][ny] = true;
                stack.push(next);
            }
        }

        // Occasionally carve extra connections for less linear paths
        if (config.getLayout() == DungeonConfig.DungeonLayout.WINDING) {
            addExtraConnections(mazeWidth, mazeHeight, cellSize, visited);
        }
    }

    /**
     * Generate using room placement and corridor connections.
     * Good for OPEN dungeons.
     */
    private void generateRoomBased() {
        int numRooms = (int)(widthInTiles * heightInTiles * config.getRoomDensity() / 1000);
        int attempts = 0;
        int maxAttempts = numRooms * 10;

        while (rooms.size() < numRooms && attempts < maxAttempts) {
            attempts++;

            int roomWidth = random.nextInt(config.getRoomMaxSize() - config.getRoomMinSize() + 1)
                          + config.getRoomMinSize();
            int roomHeight = random.nextInt(config.getRoomMaxSize() - config.getRoomMinSize() + 1)
                           + config.getRoomMinSize();
            int roomX = random.nextInt(widthInTiles - roomWidth - 4) + 2;  // -4 and +2 for border
            int roomY = random.nextInt(heightInTiles - roomHeight - 4) + 2;

            Room newRoom = new Room(roomX, roomY, roomWidth, roomHeight);

            // Check if room overlaps with existing rooms
            boolean overlaps = false;
            for (Room room : rooms) {
                if (newRoom.intersects(room)) {
                    overlaps = true;
                    break;
                }
            }

            if (!overlaps) {
                carveRoom(newRoom);

                // Connect to previous room with corridor
                if (!rooms.isEmpty()) {
                    Room prevRoom = rooms.get(rooms.size() - 1);
                    connectRooms(prevRoom, newRoom);
                }

                rooms.add(newRoom);
            }
        }
    }

    /**
     * Carve a maze cell (floor area).
     */
    private void carveMazeCell(int cellX, int cellY, int cellSize) {
        int tileX = 1 + cellX * cellSize;  // +1 for border
        int tileY = 1 + cellY * cellSize;

        for (int dx = 0; dx < config.getCorridorWidth(); dx++) {
            for (int dy = 0; dy < config.getCorridorWidth(); dy++) {
                int x = tileX + dx;
                int y = tileY + dy;
                if (x < widthInTiles - 1 && y < heightInTiles - 1) {
                    walls[x][y] = false;
                }
            }
        }
    }

    /**
     * Carve path between two maze cells.
     */
    private void carvePathBetween(int x1, int y1, int x2, int y2, int cellSize) {
        int tileX1 = 1 + x1 * cellSize;
        int tileY1 = 1 + y1 * cellSize;
        int tileX2 = 1 + x2 * cellSize;
        int tileY2 = 1 + y2 * cellSize;

        int startX = Math.min(tileX1, tileX2);
        int endX = Math.max(tileX1, tileX2) + config.getCorridorWidth();
        int startY = Math.min(tileY1, tileY2);
        int endY = Math.max(tileY1, tileY2) + config.getCorridorWidth();

        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                if (x >= 0 && x < widthInTiles && y >= 0 && y < heightInTiles) {
                    walls[x][y] = false;
                }
            }
        }
    }

    /**
     * Get unvisited neighbors of a maze cell.
     */
    private List<int[]> getUnvisitedNeighbors(int x, int y, int width, int height, boolean[][] visited) {
        List<int[]> neighbors = new ArrayList<>();

        // North, East, South, West
        int[][] dirs = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}};

        for (int[] dir : dirs) {
            int nx = x + dir[0];
            int ny = y + dir[1];

            if (nx >= 0 && nx < width && ny >= 0 && ny < height && !visited[nx][ny]) {
                neighbors.add(new int[]{nx, ny});
            }
        }

        return neighbors;
    }

    /**
     * Add extra connections for more complex mazes.
     */
    private void addExtraConnections(int mazeWidth, int mazeHeight, int cellSize, boolean[][] visited) {
        int numConnections = mazeWidth * mazeHeight / 20;

        for (int i = 0; i < numConnections; i++) {
            int x = random.nextInt(mazeWidth - 1);
            int y = random.nextInt(mazeHeight - 1);

            if (visited[x][y] && visited[x + 1][y]) {
                carvePathBetween(x, y, x + 1, y, cellSize);
            }
            if (visited[x][y] && visited[x][y + 1]) {
                carvePathBetween(x, y, x, y + 1, cellSize);
            }
        }
    }

    /**
     * Carve out a room.
     */
    private void carveRoom(Room room) {
        for (int x = room.x; x < room.x + room.width; x++) {
            for (int y = room.y; y < room.y + room.height; y++) {
                if (x >= 1 && x < widthInTiles - 1 && y >= 1 && y < heightInTiles - 1) {
                    walls[x][y] = false;
                }
            }
        }
    }

    /**
     * Connect two rooms with a corridor.
     */
    private void connectRooms(Room room1, Room room2) {
        int x1 = room1.centerX();
        int y1 = room1.centerY();
        int x2 = room2.centerX();
        int y2 = room2.centerY();

        // Create L-shaped corridor
        if (config.getLayout() == DungeonConfig.DungeonLayout.STRAIGHT || random.nextBoolean()) {
            // Horizontal then vertical
            createHorizontalCorridor(x1, x2, y1);
            createVerticalCorridor(y1, y2, x2);
        } else {
            // Vertical then horizontal
            createVerticalCorridor(y1, y2, x1);
            createHorizontalCorridor(x1, x2, y2);
        }
    }

    private void createHorizontalCorridor(int x1, int x2, int y) {
        int startX = Math.min(x1, x2);
        int endX = Math.max(x1, x2);

        for (int x = startX; x <= endX; x++) {
            for (int dy = 0; dy < config.getCorridorWidth(); dy++) {
                int corridorY = y + dy - config.getCorridorWidth() / 2;
                if (x >= 1 && x < widthInTiles - 1 && corridorY >= 1 && corridorY < heightInTiles - 1) {
                    walls[x][corridorY] = false;
                }
            }
        }
    }

    private void createVerticalCorridor(int y1, int y2, int x) {
        int startY = Math.min(y1, y2);
        int endY = Math.max(y1, y2);

        for (int y = startY; y <= endY; y++) {
            for (int dx = 0; dx < config.getCorridorWidth(); dx++) {
                int corridorX = x + dx - config.getCorridorWidth() / 2;
                if (corridorX >= 1 && corridorX < widthInTiles - 1 && y >= 1 && y < heightInTiles - 1) {
                    walls[corridorX][y] = false;
                }
            }
        }
    }

    /**
     * Create 2-tile tall border around entire dungeon.
     */
    private void createBorder() {
        // Mark all border tiles as walls (they'll be rendered as 2-tile tall walls)
        for (int x = 0; x < widthInTiles; x++) {
            walls[x][0] = true;
            walls[x][heightInTiles - 1] = true;
        }
        for (int y = 0; y < heightInTiles; y++) {
            walls[0][y] = true;
            walls[widthInTiles - 1][y] = true;
        }
    }

    /**
     * Check if a tile position is a wall.
     */
    public boolean isWall(int tileX, int tileY) {
        if (tileX < 0 || tileX >= widthInTiles || tileY < 0 || tileY >= heightInTiles) {
            return true;
        }
        return walls[tileX][tileY];
    }

    /**
     * Check if a tile is on the border (for 2-tile tall walls).
     */
    public boolean isBorder(int tileX, int tileY) {
        return tileX == 0 || tileX == widthInTiles - 1 ||
               tileY == 0 || tileY == heightInTiles - 1;
    }

    public int getWidthInTiles() { return widthInTiles; }
    public int getHeightInTiles() { return heightInTiles; }
    public DungeonConfig getConfig() { return config; }

    /**
     * Simple room class for room-based generation.
     */
    private static class Room {
        int x, y, width, height;

        Room(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        int centerX() { return x + width / 2; }
        int centerY() { return y + height / 2; }

        boolean intersects(Room other) {
            return !(x + width + 1 < other.x || other.x + other.width + 1 < x ||
                    y + height + 1 < other.y || other.y + other.height + 1 < y);
        }
    }
}
