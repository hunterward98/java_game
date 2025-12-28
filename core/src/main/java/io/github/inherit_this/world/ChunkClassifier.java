package io.github.inherit_this.world;

import io.github.inherit_this.util.Constants;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * Classifies dungeon chunks into types based on their content.
 * Analyzes floor tile percentage and contiguity to determine chunk variety.
 */
public class ChunkClassifier {

    /**
     * Chunk type categories based on floor density and layout.
     */
    public enum ChunkType {
        BLANK,      // Mostly walls (0-5% floor tiles)
        PATH,       // Primarily corridors (6-40% floor, no large open areas)
        OPEN,       // Primarily rooms (>70% floor with contiguous areas ≥25 tiles)
        COMBINED    // Mix of rooms and corridors (40-70% floor)
    }

    /**
     * Classify a chunk based on its tile composition.
     *
     * @param walls The dungeon walls array
     * @param chunkX Chunk X coordinate
     * @param chunkY Chunk Y coordinate
     * @return The chunk type classification
     */
    public ChunkType classify(boolean[][] walls, int chunkX, int chunkY) {
        // Calculate chunk boundaries in tile coordinates
        int startX = chunkX * Constants.CHUNK_SIZE;
        int startY = chunkY * Constants.CHUNK_SIZE;
        int endX = Math.min(startX + Constants.CHUNK_SIZE, walls.length);
        int endY = Math.min(startY + Constants.CHUNK_SIZE, walls[0].length);

        // Count floor tiles
        int totalTiles = Constants.CHUNK_SIZE * Constants.CHUNK_SIZE;
        int floorTiles = countFloorTiles(walls, startX, startY, endX, endY);
        float floorPercent = (float) floorTiles / totalTiles;

        // Classify based on floor percentage
        if (floorPercent <= 0.05f) {
            return ChunkType.BLANK;
        }

        // Check for large open areas (rooms)
        boolean hasLargeOpenArea = hasLargeOpenArea(walls, startX, startY, endX, endY, 25);

        if (floorPercent > 0.70f && hasLargeOpenArea) {
            return ChunkType.OPEN;
        }

        if (floorPercent > 0.40f) {
            return ChunkType.COMBINED;
        }

        return ChunkType.PATH;
    }

    /**
     * Count the number of floor tiles in a region.
     *
     * @param walls The dungeon walls array
     * @param startX Start X coordinate (inclusive)
     * @param startY Start Y coordinate (inclusive)
     * @param endX End X coordinate (exclusive)
     * @param endY End Y coordinate (exclusive)
     * @return Number of floor tiles
     */
    private int countFloorTiles(boolean[][] walls, int startX, int startY, int endX, int endY) {
        int count = 0;
        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                if (!walls[x][y]) {  // false = floor
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Check if the region contains a contiguous open area of at least the specified size.
     * Uses flood-fill to find largest contiguous floor region.
     *
     * @param walls The dungeon walls array
     * @param startX Start X coordinate (inclusive)
     * @param startY Start Y coordinate (inclusive)
     * @param endX End X coordinate (exclusive)
     * @param endY End Y coordinate (exclusive)
     * @param minSize Minimum size for "large" open area
     * @return true if a large open area exists
     */
    private boolean hasLargeOpenArea(boolean[][] walls, int startX, int startY,
                                     int endX, int endY, int minSize) {
        boolean[][] visited = new boolean[walls.length][walls[0].length];

        // Find largest contiguous floor region
        int maxRegionSize = 0;

        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                if (!walls[x][y] && !visited[x][y]) {
                    // Flood-fill to find region size
                    int regionSize = floodFill(walls, visited, x, y, startX, startY, endX, endY);
                    maxRegionSize = Math.max(maxRegionSize, regionSize);
                }
            }
        }

        return maxRegionSize >= minSize;
    }

    /**
     * Flood-fill algorithm to find size of contiguous floor region.
     *
     * @param walls The dungeon walls array
     * @param visited Visited tiles tracker
     * @param startX Starting X position
     * @param startY Starting Y position
     * @param minX Minimum X bound (inclusive)
     * @param minY Minimum Y bound (inclusive)
     * @param maxX Maximum X bound (exclusive)
     * @param maxY Maximum Y bound (exclusive)
     * @return Size of the contiguous region
     */
    private int floodFill(boolean[][] walls, boolean[][] visited,
                         int startX, int startY, int minX, int minY, int maxX, int maxY) {
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{startX, startY});
        visited[startX][startY] = true;

        int count = 0;
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        while (!queue.isEmpty()) {
            int[] pos = queue.poll();
            int x = pos[0];
            int y = pos[1];
            count++;

            // Check all 4 directions
            for (int[] dir : directions) {
                int nx = x + dir[0];
                int ny = y + dir[1];

                // Check bounds and conditions
                if (nx >= minX && nx < maxX && ny >= minY && ny < maxY &&
                    !visited[nx][ny] && !walls[nx][ny]) {

                    visited[nx][ny] = true;
                    queue.add(new int[]{nx, ny});
                }
            }
        }

        return count;
    }

    /**
     * Get statistics about chunk classifications for a dungeon.
     * Useful for debugging and analysis.
     *
     * @param walls The dungeon walls array
     * @param chunksWide Number of chunks horizontally
     * @param chunksHigh Number of chunks vertically
     * @return Map of chunk type to count
     */
    public java.util.Map<ChunkType, Integer> getChunkStatistics(boolean[][] walls,
                                                                int chunksWide, int chunksHigh) {
        java.util.Map<ChunkType, Integer> stats = new java.util.HashMap<>();
        for (ChunkType type : ChunkType.values()) {
            stats.put(type, 0);
        }

        for (int chunkX = 0; chunkX < chunksWide; chunkX++) {
            for (int chunkY = 0; chunkY < chunksHigh; chunkY++) {
                ChunkType type = classify(walls, chunkX, chunkY);
                stats.put(type, stats.get(type) + 1);
            }
        }

        return stats;
    }
}
