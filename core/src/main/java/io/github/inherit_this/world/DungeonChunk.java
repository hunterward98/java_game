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

    public DungeonChunk(int chunkX, int chunkY, Tile[][] tiles, DungeonGenerator generator) {
        super(chunkX, chunkY, "dungeon", false);  // Don't generate tiles - we provide our own
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

                // Get the actual tile we created (which has the themed texture)
                Tile tile = dungeonTiles[localX][localY];
                if (tile == null) continue;

                boolean isWall = tile.isSolid();

                if (isWall) {
                    // Use the texture from the actual tile (themed texture)
                    com.badlogic.gdx.graphics.Texture wallTexture = tile.getTexture();

                    // Walls are 2 tiles tall - only create once (not at multiple levels)
                    createWallModels(models, tileMesh, wallTexture, tileWorldX, tileWorldY,
                                   worldTileX, worldTileY);
                } else {
                    // Floor tile - use the texture from the actual tile (themed texture)
                    com.badlogic.gdx.graphics.Texture floorTexture = tile.getTexture();

                    ModelInstance floorInstance = tileMesh.createTileInstance(
                        floorTexture,
                        tileWorldX,
                        tileWorldY,
                        0f,
                        tile.getTextureRotation()  // Apply texture rotation for variety
                    );
                    models.add(floorInstance);
                }
            }
        }

        return models;
    }

    /**
     * Create wall model instances for a tile, checking all 4 directions.
     * The new wall geometry includes both exterior and interior faces in a single model.
     */
    private void createWallModels(List<ModelInstance> models, TileMesh3D tileMesh,
                                 com.badlogic.gdx.graphics.Texture texture,
                                 float tileWorldX, float tileWorldY,
                                 int worldTileX, int worldTileY) {

        float wallHeight = Constants.TILE_SIZE * 2;  // Walls are 2 tiles tall
        float yOffset = 0;  // Always start at ground level

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
                // Create wall (already includes both exterior and interior faces)
                ModelInstance wall = tileMesh.createWallInstance(
                    texture,
                    tileWorldX,
                    tileWorldY,
                    yOffset,
                    direction,
                    wallHeight,
                    false,  // not flipped
                    1       // 90° texture rotation
                );
                models.add(wall);
            }
        }

        // Check for corners (90-degree interior corners where two walls meet)
        // These need corner pieces to fill the gaps
        // Check if adjacent tiles are FLOOR (creating a corner)
        boolean hasFloorNorth = isFloorOrOutOfBounds(worldTileX, worldTileY + 1);
        boolean hasFloorEast = isFloorOrOutOfBounds(worldTileX + 1, worldTileY);
        boolean hasFloorSouth = isFloorOrOutOfBounds(worldTileX, worldTileY - 1);
        boolean hasFloorWest = isFloorOrOutOfBounds(worldTileX - 1, worldTileY);

        // NE corner: floor to North AND East (interior corner)
        if (hasFloorNorth && hasFloorEast) {
            createCornerWall(models, tileMesh, texture, tileWorldX, tileWorldY, yOffset, wallHeight, 0);
        }
        // SE corner: floor to South AND East (interior corner)
        if (hasFloorSouth && hasFloorEast) {
            createCornerWall(models, tileMesh, texture, tileWorldX, tileWorldY, yOffset, wallHeight, 1);
        }
        // SW corner: floor to South AND West (interior corner)
        if (hasFloorSouth && hasFloorWest) {
            createCornerWall(models, tileMesh, texture, tileWorldX, tileWorldY, yOffset, wallHeight, 2);
        }
        // NW corner: floor to North AND West (interior corner)
        if (hasFloorNorth && hasFloorWest) {
            createCornerWall(models, tileMesh, texture, tileWorldX, tileWorldY, yOffset, wallHeight, 3);
        }
    }

    /**
     * Helper method to check if a tile is floor or out of bounds
     */
    private boolean isFloorOrOutOfBounds(int x, int y) {
        if (x < 0 || x >= generator.getWidthInTiles() ||
            y < 0 || y >= generator.getHeightInTiles()) {
            return true;  // Treat out of bounds as floor for corner detection
        }
        return !generator.isWall(x, y);
    }

    /**
     * Creates a corner wall piece to fill 90-degree interior corners
     * @param cornerType 0=NE, 1=SE, 2=SW, 3=NW
     */
    private void createCornerWall(List<ModelInstance> models, TileMesh3D tileMesh,
                                 com.badlogic.gdx.graphics.Texture texture,
                                 float tileWorldX, float tileWorldY,
                                 float yOffset, float wallHeight, int cornerType) {
        // For now, create a wall facing the corner direction
        // Corner type maps to: 0=NE(45°), 1=SE(135°), 2=SW(225°), 3=NW(315°)
        // We'll use the same wall instance but position it at the corner
        int direction = cornerType;  // Use corner type as direction for now

        ModelInstance cornerWall = tileMesh.createWallInstance(
            texture,
            tileWorldX,
            tileWorldY,
            yOffset,
            direction,
            wallHeight,
            false,
            1
        );
        models.add(cornerWall);
    }
}
