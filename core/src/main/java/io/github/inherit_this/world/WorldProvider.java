package io.github.inherit_this.world;

/**
 * Interface for world providers that supply terrain data.
 * Implementations can be static (loaded from file) or procedural (generated).
 */
public interface WorldProvider {

    /**
     * Gets or creates a chunk at the specified chunk coordinates.
     * @param chunkX Chunk X coordinate
     * @param chunkY Chunk Y coordinate
     * @return The chunk at these coordinates
     */
    Chunk getOrCreateChunk(int chunkX, int chunkY);

    /**
     * Gets the tile at the specified world tile coordinates.
     * @param worldTileX World tile X coordinate
     * @param worldTileY World tile Y coordinate
     * @return The tile at these coordinates
     */
    Tile getTileAtWorldCoords(int worldTileX, int worldTileY);

    /**
     * Gets the tile at the specified world position (in pixels).
     * @param worldX World X position in pixels
     * @param worldY World Y position in pixels
     * @return The tile at this position
     */
    Tile getTileAtPosition(float worldX, float worldY);

    /**
     * Checks if the position is solid (for collision detection).
     * @param worldX World X position in pixels
     * @param worldY World Y position in pixels
     * @return true if the position is solid
     */
    boolean isSolidAtPosition(float worldX, float worldY);

    /**
     * Gets the active chunk at the specified world tile coordinates.
     * May return null if the chunk hasn't been loaded yet.
     * @param worldTileX World tile X coordinate
     * @param worldTileY World tile Y coordinate
     * @return The chunk, or null if not loaded
     */
    Chunk getActiveChunk(int worldTileX, int worldTileY);

    /**
     * Preloads chunks in a radius around the spawn point (0,0).
     * This builds cached 3D models to prevent lag on first render.
     * @param radius Number of chunks to preload in each direction
     * @return Number of chunks preloaded
     */
    int preloadChunks(int radius);

    /**
     * Gets the number of currently loaded chunks.
     * @return Number of loaded chunks
     */
    int getLoadedChunkCount();

    /**
     * Disposes of resources used by this world provider.
     * Should be called when switching worlds or exiting.
     */
    void dispose();
}
