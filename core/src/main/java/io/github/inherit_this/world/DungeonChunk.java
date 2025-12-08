package io.github.inherit_this.world;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import io.github.inherit_this.util.Constants;
import java.util.ArrayList;
import java.util.List;

/**
 * Special chunk type for dungeons that supports multi-layer tiles
 * and 2-tile tall border walls.
 */
public class DungeonChunk extends Chunk {

    private final DungeonGenerator generator;
    private final Tile[][] dungeonTiles;
    private static final TileTextureManager textureManager = TileTextureManager.getInstance();

    public DungeonChunk(int chunkX, int chunkY, Tile[][] tiles, DungeonGenerator generator) {
        super(chunkX, chunkY, "dungeon");
        this.generator = generator;
        this.dungeonTiles = tiles;
    }

    @Override
    public Tile getTile(int x, int y) {
        if (x < 0 || y < 0 || x >= Constants.CHUNK_SIZE || y >= Constants.CHUNK_SIZE) {
            throw new IndexOutOfBoundsException("Tile coords out of bounds");
        }
        return dungeonTiles[x][y];
    }

    @Override
    public List<Tile> getTiles(int x, int y) {
        if (x < 0 || y < 0 || x >= Constants.CHUNK_SIZE || y >= Constants.CHUNK_SIZE) {
            throw new IndexOutOfBoundsException("Tile coords out of bounds");
        }
        List<Tile> result = new ArrayList<>();
        if (dungeonTiles[x][y] != null) {
            result.add(dungeonTiles[x][y]);
        }
        return result;
    }

    @Override
    public List<ModelInstance> getCachedModels() {
        // Build models with dungeon-specific logic
        return buildDungeonModels();
    }

    /**
     * Build 3D models for dungeon tiles including 2-tile tall borders.
     */
    private List<ModelInstance> buildDungeonModels() {
        List<ModelInstance> models = new ArrayList<>();
        TileMesh3D tileMesh = TileMesh3D.getInstance();

        float baseX = getChunkX() * Constants.CHUNK_PIXEL_SIZE;
        float baseY = getChunkY() * Constants.CHUNK_PIXEL_SIZE;

        int baseTileX = getChunkX() * Constants.CHUNK_SIZE;
        int baseTileY = getChunkY() * Constants.CHUNK_SIZE;

        for (int localX = 0; localX < Constants.CHUNK_SIZE; localX++) {
            for (int localY = 0; localY < Constants.CHUNK_SIZE; localY++) {
                int worldTileX = baseTileX + localX;
                int worldTileY = baseTileY + localY;

                float tileWorldX = baseX + localX * Constants.TILE_SIZE;
                float tileWorldY = baseY + localY * Constants.TILE_SIZE;

                boolean isWall = generator.isWall(worldTileX, worldTileY);
                boolean isBorder = generator.isBorder(worldTileX, worldTileY);

                if (isWall) {
                    // Get wall texture
                    com.badlogic.gdx.graphics.Texture wallTexture =
                        textureManager.getTexture("tiles/wood_wall.png");

                    if (isBorder) {
                        // Create 2-tile tall border wall
                        // Level 0 (ground level)
                        createWallModels(models, tileMesh, wallTexture, tileWorldX, tileWorldY,
                                       worldTileX, worldTileY, 0);

                        // Level 1 (one tile up)
                        createWallModels(models, tileMesh, wallTexture, tileWorldX, tileWorldY,
                                       worldTileX, worldTileY, 1);
                    } else {
                        // Regular wall (1 tile tall)
                        createWallModels(models, tileMesh, wallTexture, tileWorldX, tileWorldY,
                                       worldTileX, worldTileY, 0);
                    }
                } else {
                    // Floor tile
                    com.badlogic.gdx.graphics.Texture floorTexture =
                        textureManager.getTexture("tiles/stone_1.png");

                    ModelInstance floorInstance = tileMesh.createTileInstance(
                        floorTexture,
                        tileWorldX,
                        tileWorldY,
                        0f
                    );
                    models.add(floorInstance);
                }
            }
        }

        return models;
    }

    /**
     * Create wall model instances for a tile, checking all 4 directions.
     */
    private void createWallModels(List<ModelInstance> models, TileMesh3D tileMesh,
                                 com.badlogic.gdx.graphics.Texture texture,
                                 float tileWorldX, float tileWorldY,
                                 int worldTileX, int worldTileY, int level) {

        float wallHeight = Constants.TILE_SIZE;
        float yOffset = level * wallHeight;

        // Check all 4 directions for adjacent floor tiles
        // If there's a floor adjacent, create a wall facing that direction
        int[][] directions = {
            {0, 1, 0},   // North
            {1, 0, 1},   // East
            {0, -1, 2},  // South
            {-1, 0, 3}   // West
        };

        for (int[] dir : directions) {
            int dx = dir[0];
            int dy = dir[1];
            int direction = dir[2];

            int adjacentX = worldTileX + dx;
            int adjacentY = worldTileY + dy;

            // Check if adjacent tile is floor (or out of bounds = we want walls on edges)
            boolean createWall = false;

            if (adjacentX < 0 || adjacentX >= generator.getWidthInTiles() ||
                adjacentY < 0 || adjacentY >= generator.getHeightInTiles()) {
                // Out of bounds - don't create wall (it's the outer edge)
                createWall = false;
            } else if (!generator.isWall(adjacentX, adjacentY)) {
                // Adjacent to floor - create wall
                createWall = true;
            }

            if (createWall) {
                ModelInstance wallInstance = tileMesh.createWallInstance(
                    texture,
                    tileWorldX,
                    tileWorldY,
                    yOffset,
                    direction,
                    wallHeight,
                    false,  // not flipped
                    0       // no texture rotation
                );
                models.add(wallInstance);
            }
        }
    }
}
