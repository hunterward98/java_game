package io.github.inherit_this.world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import io.github.inherit_this.util.Constants;
import io.github.inherit_this.world.StaticWorld.StaticMapData;

import java.util.ArrayList;
import java.util.List;

/**
 * A chunk in a static world, loaded from map data instead of procedurally generated.
 * Supports multiple tile layers (ground, walls, roofs) at each position.
 */
public class StaticChunk extends Chunk {

    @SuppressWarnings("unchecked")
    private List<Tile>[][] tileLayers = (List<Tile>[][]) new List[Constants.CHUNK_SIZE][Constants.CHUNK_SIZE];
    private int chunkX;
    private int chunkY;
    private static final TileTextureManager textureManager = TileTextureManager.getInstance();

    // Cache 3D models for performance
    private List<ModelInstance> cachedModels = null;

    public StaticChunk(int chunkX, int chunkY, StaticMapData mapData) {
        super(chunkX, chunkY, "grass"); // Call parent constructor
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        generateTilesFromMapData(mapData);
    }

    /**
     * Generates tiles from static map data instead of procedural generation.
     * Supports multiple layers (ground, walls, roofs) at each position.
     */
    private void generateTilesFromMapData(StaticMapData mapData) {
        // Initialize all tile layer lists
        for (int x = 0; x < Constants.CHUNK_SIZE; x++) {
            for (int y = 0; y < Constants.CHUNK_SIZE; y++) {
                tileLayers[x][y] = new ArrayList<>();
            }
        }

        // Load tiles from map data
        for (int x = 0; x < Constants.CHUNK_SIZE; x++) {
            for (int y = 0; y < Constants.CHUNK_SIZE; y++) {
                // Calculate world tile coordinates
                int worldTileX = chunkX * Constants.CHUNK_SIZE + x;
                int worldTileY = chunkY * Constants.CHUNK_SIZE + y;

                // Get tile data for this position (format: "tileType:layer:direction" or just "tileType")
                String tileKey = worldTileX + "," + worldTileY;
                String tileData = mapData.tiles.getOrDefault(tileKey, mapData.defaultTile);

                // Check if outside map bounds
                if (worldTileX < 0 || worldTileX >= mapData.width ||
                    worldTileY < 0 || worldTileY >= mapData.height) {
                    // Outside map bounds - use void tile
                    Texture texture = textureManager.getTexture("tiles/void.png");
                    tileLayers[x][y].add(new Tile(texture, true, TileType.VOID, TileLayer.GROUND, -1f, 0, 0));
                } else {
                    // Parse tile data (supports legacy single-tile format and new multi-layer format)
                    String[] parts = tileData.split(";");
                    for (String part : parts) {
                        Tile tile = parseTileData(part);
                        if (tile != null) {
                            tileLayers[x][y].add(tile);
                        }
                    }

                    // If no tiles were added, add default grass
                    if (tileLayers[x][y].isEmpty()) {
                        Texture texture = textureManager.getTexture("tiles/grass_1.png");
                        tileLayers[x][y].add(new Tile(texture, false, TileType.GRASS, TileLayer.GROUND, -1f, 0, 0));
                    }
                }
            }
        }
    }

    /**
     * Parses a single tile data string into a Tile object.
     * Format: "tileType:layer:direction:level:flipped:textureRotation" or just "tileType" (defaults to GROUND layer, level 0, not flipped, 0° rotation)
     */
    private Tile parseTileData(String tileData) {
        String[] parts = tileData.split(":");
        String tileType = parts[0];
        TileLayer layer = parts.length > 1 ? TileLayer.valueOf(parts[1]) : TileLayer.GROUND;
        int direction = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
        int level = parts.length > 3 ? Integer.parseInt(parts[3]) : 0;
        boolean flipped = parts.length > 4 ? Boolean.parseBoolean(parts[4]) : false;
        int textureRotation = parts.length > 5 ? Integer.parseInt(parts[5]) : 0;

        // Load texture for any tile type
        Texture texture = textureManager.getTexture("tiles/" + tileType + ".png");
        if (texture == null) {
            // Texture not found, skip this tile
            return null;
        }

        // Determine properties based on tile name and layer
        // Only WALL layer tiles should be solid (block movement)
        // Ground and roof tiles are always walkable
        boolean solid = (layer == TileLayer.WALL);

        TileType type = TileType.GRASS;
        // Determine tile type based on name
        if (tileType.contains("stone") || tileType.contains("rock") ||
            tileType.contains("wall") || tileType.contains("pillar") || tileType.contains("window")) {
            type = TileType.STONE;
        } else {
            type = TileType.GRASS;
        }

        return new Tile(texture, solid, type, layer, -1f, direction, level, flipped, textureRotation);
    }

    @Override
    public Tile getTile(int x, int y) {
        if (x < 0 || y < 0 || x >= Constants.CHUNK_SIZE || y >= Constants.CHUNK_SIZE) {
            throw new IndexOutOfBoundsException("Tile coords out of bounds");
        }
        // Return ground tile (first tile) for backward compatibility
        List<Tile> tiles = tileLayers[x][y];
        return (tiles != null && !tiles.isEmpty()) ? tiles.get(0) : null;
    }

    @Override
    public List<Tile> getTiles(int x, int y) {
        if (x < 0 || y < 0 || x >= Constants.CHUNK_SIZE || y >= Constants.CHUNK_SIZE) {
            throw new IndexOutOfBoundsException("Tile coords out of bounds");
        }
        return tileLayers[x][y] != null ? tileLayers[x][y] : new ArrayList<>();
    }

    @Override
    public int getChunkX() {
        return chunkX;
    }

    @Override
    public int getChunkY() {
        return chunkY;
    }

    @Override
    public List<ModelInstance> getCachedModels() {
        if (cachedModels == null) {
            buildCachedModels();
        }
        return cachedModels;
    }

    private void buildCachedModels() {
        cachedModels = new ArrayList<>();
        TileMesh3D tileMesh = TileMesh3D.getInstance();

        float baseX = chunkX * Constants.CHUNK_PIXEL_SIZE;
        float baseY = chunkY * Constants.CHUNK_PIXEL_SIZE;

        for (int x = 0; x < Constants.CHUNK_SIZE; x++) {
            for (int y = 0; y < Constants.CHUNK_SIZE; y++) {
                float tileWorldX = baseX + x * Constants.TILE_SIZE;
                float tileWorldY = baseY + y * Constants.TILE_SIZE;

                // Render all tile layers at this position
                List<Tile> tilesAtPosition = tileLayers[x][y];
                if (tilesAtPosition != null) {
                    for (Tile tile : tilesAtPosition) {
                        TileLayer layer = tile.getLayer();
                        float baseYOffset = layer != null ? layer.getYOffset() : 0f;
                        int level = tile.getLevel();
                        // Add level offset: each level adds one tile height (32 pixels)
                        float yOffset = baseYOffset + (level * Constants.TILE_SIZE);
                        float angle = tile.getEffectiveAngle();
                        int direction = tile.getDirection();

                        ModelInstance tileInstance;

                        // Use wall rendering for WALL layer tiles
                        if (layer == TileLayer.WALL) {
                            float wallHeight = Constants.TILE_SIZE;  // Default wall height
                            boolean flipped = tile.isFlipped();
                            int textureRotation = tile.getTextureRotation();
                            tileInstance = tileMesh.createWallInstance(
                                tile.getTexture(),
                                tileWorldX,
                                tileWorldY,
                                yOffset,
                                direction,
                                wallHeight,
                                flipped,
                                textureRotation
                            );
                        } else {
                            // Use angled tile rendering for other layers
                            tileInstance = tileMesh.createAngledTileInstance(
                                tile.getTexture(),
                                tileWorldX,
                                tileWorldY,
                                yOffset,
                                angle,
                                direction
                            );
                        }

                        cachedModels.add(tileInstance);
                    }
                }
            }
        }
    }

    @Override
    public void invalidateCache() {
        cachedModels = null;
    }

    @Override
    public void dispose() {
        if (cachedModels != null) {
            cachedModels.clear();
            cachedModels = null;
        }
    }
}
