package io.github.inherit_this.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import io.github.inherit_this.util.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Static world loaded from a map file (for towns and hand-crafted areas).
 * Map data is stored in JSON format and loaded at startup.
 */
public class StaticWorld implements WorldProvider {

    private Map<Long, Chunk> chunks = new HashMap<>();
    private StaticMapData mapData;
    private String mapFilePath;

    /**
     * Creates a StaticWorld by loading map data from a JSON file.
     * @param mapFilePath Path to the map JSON file (relative to assets)
     */
    public StaticWorld(String mapFilePath) {
        this.mapFilePath = mapFilePath;
        loadMapData();
        generateChunksFromMapData();
    }

    /**
     * Loads map data from JSON file.
     */
    private void loadMapData() {
        try {
            FileHandle file = Gdx.files.internal(mapFilePath);
            Json json = new Json();
            mapData = json.fromJson(StaticMapData.class, file.readString());
            Gdx.app.log("StaticWorld", "Loaded map: " + mapData.name + " (" + mapData.width + "x" + mapData.height + ")");
            Gdx.app.log("StaticWorld", "Spawn coordinates: (" + mapData.spawnX + ", " + mapData.spawnY + ")");
        } catch (Exception e) {
            Gdx.app.error("StaticWorld", "Failed to load map from " + mapFilePath, e);
            // Create empty default map
            mapData = new StaticMapData();
            mapData.name = "Empty Map";
            mapData.width = 10;
            mapData.height = 10;
            mapData.defaultTile = "grass_1";
            mapData.tiles = new HashMap<>();
            Gdx.app.log("StaticWorld", "Using fallback empty map");
        }
    }

    /**
     * Generates chunks from the loaded map data.
     */
    private void generateChunksFromMapData() {
        int chunksX = (int) Math.ceil((double) mapData.width / Constants.CHUNK_SIZE);
        int chunksY = (int) Math.ceil((double) mapData.height / Constants.CHUNK_SIZE);

        for (int cx = 0; cx < chunksX; cx++) {
            for (int cy = 0; cy < chunksY; cy++) {
                createChunkFromMapData(cx, cy);
            }
        }
    }

    /**
     * Creates a single chunk from map data.
     */
    private void createChunkFromMapData(int chunkX, int chunkY) {
        StaticChunk chunk = new StaticChunk(chunkX, chunkY, mapData);
        chunks.put(pack(chunkX, chunkY), chunk);
    }

    @Override
    public Chunk getOrCreateChunk(int chunkX, int chunkY) {
        long key = pack(chunkX, chunkY);
        if (!chunks.containsKey(key)) {
            createChunkFromMapData(chunkX, chunkY);
        }
        return chunks.get(key);
    }

    @Override
    public Tile getTileAtWorldCoords(int worldTileX, int worldTileY) {
        int chunkX = Math.floorDiv(worldTileX, Constants.CHUNK_SIZE);
        int chunkY = Math.floorDiv(worldTileY, Constants.CHUNK_SIZE);
        Chunk chunk = getOrCreateChunk(chunkX, chunkY);

        int localX = Math.floorMod(worldTileX, Constants.CHUNK_SIZE);
        int localY = Math.floorMod(worldTileY, Constants.CHUNK_SIZE);

        return chunk.getTile(localX, localY);
    }

    @Override
    public Tile getTileAtPosition(float worldX, float worldY) {
        int tileX = (int) Math.floor(worldX / Constants.TILE_SIZE);
        int tileY = (int) Math.floor(worldY / Constants.TILE_SIZE);
        return getTileAtWorldCoords(tileX, tileY);
    }

    @Override
    public boolean isSolidAtPosition(float worldX, float worldY) {
        Tile tile = getTileAtPosition(worldX, worldY);
        return tile != null && tile.isSolid();
    }

    @Override
    public Chunk getActiveChunk(int worldTileX, int worldTileY) {
        int chunkX = Math.floorDiv(worldTileX, Constants.CHUNK_SIZE);
        int chunkY = Math.floorDiv(worldTileY, Constants.CHUNK_SIZE);
        return chunks.get(pack(chunkX, chunkY));
    }

    @Override
    public int preloadChunks(int radius) {
        // StaticWorld loads all chunks at startup, so this is a no-op
        // Just ensure cached models are built
        int count = 0;
        for (Chunk chunk : chunks.values()) {
            chunk.getCachedModels();
            count++;
        }
        return count;
    }

    @Override
    public int getLoadedChunkCount() {
        return chunks.size();
    }

    @Override
    public void dispose() {
        for (Chunk chunk : chunks.values()) {
            chunk.dispose();
        }
        chunks.clear();
    }

    /**
     * Sets a tile at the specified world coordinates on a specific layer.
     * Used by the map editor.
     * @param worldTileX World tile X coordinate
     * @param worldTileY World tile Y coordinate
     * @param tileData Tile data string (format: "tileType:layer:direction")
     * @param layer The layer to place this tile on
     */
    public void setTileAt(int worldTileX, int worldTileY, String tileData, TileLayer layer) {
        String key = worldTileX + "," + worldTileY;

        // Get existing tile data (may contain multiple layers separated by ";")
        String existingData = mapData.tiles.get(key);

        // Parse existing layers
        java.util.List<String> layers = new java.util.ArrayList<>();
        if (existingData != null && !existingData.isEmpty()) {
            String[] parts = existingData.split(";");
            for (String part : parts) {
                layers.add(part);
            }
        }

        // Parse direction from new tile data
        String[] newTileParts = tileData.split(":");
        int newDirection = newTileParts.length > 2 ? Integer.parseInt(newTileParts[2]) : 0;

        // Remove any existing tile on this layer
        // For WALL layers, only remove walls on the same side (direction)
        // For other layers, remove all tiles on that layer
        layers.removeIf(layerData -> {
            String[] parts = layerData.split(":");
            if (parts.length > 1) {
                boolean sameLayer = parts[1].equals(layer.name());
                if (!sameLayer) return false;

                // For WALL layer, only remove if same direction
                if (layer == TileLayer.WALL && parts.length > 2) {
                    int existingDirection = Integer.parseInt(parts[2]);
                    return existingDirection == newDirection;
                }

                // For non-WALL layers, remove any tile on the same layer
                return true;
            }
            return false; // Legacy format, keep it
        });

        // Add new tile data
        layers.add(tileData);

        // Reconstruct tile data string
        String newTileData = String.join(";", layers);

        // Save to map data
        if (layers.isEmpty() || newTileData.equals(mapData.defaultTile)) {
            mapData.tiles.remove(key);
        } else {
            mapData.tiles.put(key, newTileData);
        }

        // Recreate chunk with new data
        int chunkX = Math.floorDiv(worldTileX, Constants.CHUNK_SIZE);
        int chunkY = Math.floorDiv(worldTileY, Constants.CHUNK_SIZE);
        long chunkKey = pack(chunkX, chunkY);

        // Dispose old chunk properly before recreating
        Chunk oldChunk = chunks.get(chunkKey);
        if (oldChunk != null) {
            oldChunk.dispose();
            chunks.remove(chunkKey);
        }

        // Create new chunk with updated data
        createChunkFromMapData(chunkX, chunkY);
    }

    /**
     * Validates if a wall can be placed at the specified position and direction.
     * Walls can only be placed if:
     * 1. There's an adjacent wall on the neighboring tile's opposite side, OR
     * 2. There's a ground tile at the current position (walls next to ground)
     *
     * @param worldTileX World tile X coordinate
     * @param worldTileY World tile Y coordinate
     * @param direction Direction of wall (0=N, 1=E, 2=S, 3=W)
     * @return true if wall placement is valid
     */
    public boolean canPlaceWall(int worldTileX, int worldTileY, int direction) {
        // Check if position is within map bounds
        if (worldTileX >= 0 && worldTileX < mapData.width &&
            worldTileY >= 0 && worldTileY < mapData.height) {

            // Check if there's a ground tile at this position
            String key = worldTileX + "," + worldTileY;
            String existingData = mapData.tiles.get(key);
            boolean hasGround = false;

            if (existingData != null && !existingData.isEmpty()) {
                String[] parts = existingData.split(";");
                for (String part : parts) {
                    String[] tileParts = part.split(":");
                    // Check for GROUND layer - handles both new format and legacy format
                    if (tileParts.length == 1) {
                        // Legacy format (just "grass_1") defaults to GROUND layer
                        hasGround = true;
                        break;
                    } else if (tileParts.length > 1 && tileParts[1].equals("GROUND")) {
                        // New format with explicit layer
                        hasGround = true;
                        break;
                    }
                }
            } else {
                // No explicit tile data means it's using the default tile (which is ground)
                hasGround = true;
            }

            // If there's ground, wall can be placed
            if (hasGround) {
                return true;
            }
        }

        // Otherwise, check for adjacent wall
        // Direction mapping: 0=North, 1=East, 2=South, 3=West
        int adjacentX = worldTileX;
        int adjacentY = worldTileY;
        int oppositeDirection = 0;

        switch (direction) {
            case 0: // North wall - check tile to the north
                adjacentY += 1;
                oppositeDirection = 2; // South wall on adjacent tile
                break;
            case 1: // East wall - check tile to the east
                adjacentX += 1;
                oppositeDirection = 3; // West wall on adjacent tile
                break;
            case 2: // South wall - check tile to the south
                adjacentY -= 1;
                oppositeDirection = 0; // North wall on adjacent tile
                break;
            case 3: // West wall - check tile to the west
                adjacentX -= 1;
                oppositeDirection = 1; // East wall on adjacent tile
                break;
        }

        // Check if adjacent tile has a wall on the opposite side
        String adjacentKey = adjacentX + "," + adjacentY;
        String adjacentData = mapData.tiles.get(adjacentKey);

        if (adjacentData != null && !adjacentData.isEmpty()) {
            String[] parts = adjacentData.split(";");
            for (String part : parts) {
                String[] tileParts = part.split(":");
                // Check if it's a WALL layer tile with the opposite direction
                if (tileParts.length > 2 &&
                    tileParts[1].equals("WALL") &&
                    Integer.parseInt(tileParts[2]) == oppositeDirection) {
                    return true;
                }
            }
        }

        // No valid placement condition met
        return false;
    }

    /**
     * Forces recreation of the chunk containing the specified tile position.
     * Used by undo/redo to refresh visual state after modifying map data directly.
     */
    public void recreateChunkAt(int worldTileX, int worldTileY) {
        int chunkX = Math.floorDiv(worldTileX, Constants.CHUNK_SIZE);
        int chunkY = Math.floorDiv(worldTileY, Constants.CHUNK_SIZE);
        long chunkKey = pack(chunkX, chunkY);

        // Dispose old chunk
        Chunk oldChunk = chunks.get(chunkKey);
        if (oldChunk != null) {
            oldChunk.dispose();
            chunks.remove(chunkKey);
        }

        // Create new chunk with current map data
        createChunkFromMapData(chunkX, chunkY);
    }

    /**
     * Saves the current map data to a JSON file.
     * Used by the map editor.
     */
    public void saveMap(String filePath) {
        try {
            Json json = new Json();
            json.setUsePrototypes(false);
            String jsonString = json.prettyPrint(mapData);
            FileHandle file = Gdx.files.local(filePath);
            file.writeString(jsonString, false);
            Gdx.app.log("StaticWorld", "Map saved to " + filePath);
        } catch (Exception e) {
            Gdx.app.error("StaticWorld", "Failed to save map to " + filePath, e);
        }
    }

    public StaticMapData getMapData() {
        return mapData;
    }

    public int getSpawnX() {
        return mapData.spawnX;
    }

    public int getSpawnY() {
        return mapData.spawnY;
    }

    private long pack(int x, int y) {
        return (((long)x) << 32) | (y & 0xffffffffL);
    }

    /**
     * Map data structure for JSON serialization.
     * Note: Using HashMap instead of Map interface for LibGDX Json compatibility
     */
    public static class StaticMapData {
        public String name = "Unnamed Map";
        public int width = 100;
        public int height = 100;
        public int spawnX = 0;
        public int spawnY = 0;
        public String defaultTile = "grass_1";
        public HashMap<String, String> tiles = new HashMap<>(); // "x,y" -> tileType
    }
}
