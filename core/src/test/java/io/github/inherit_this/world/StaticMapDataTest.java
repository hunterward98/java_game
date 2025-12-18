package io.github.inherit_this.world;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for StaticWorld.StaticMapData class.
 */
@DisplayName("StaticMapData Tests")
public class StaticMapDataTest {

    private StaticWorld.StaticMapData mapData;

    @BeforeEach
    void setUp() {
        mapData = new StaticWorld.StaticMapData();
    }

    @Nested
    @DisplayName("Default Values")
    class DefaultValues {

        @Test
        @DisplayName("Should have default name")
        void testDefaultName() {
            assertEquals("Unnamed Map", mapData.name);
        }

        @Test
        @DisplayName("Should have default width")
        void testDefaultWidth() {
            assertEquals(100, mapData.width);
        }

        @Test
        @DisplayName("Should have default height")
        void testDefaultHeight() {
            assertEquals(100, mapData.height);
        }

        @Test
        @DisplayName("Should have default spawn X")
        void testDefaultSpawnX() {
            assertEquals(0, mapData.spawnX);
        }

        @Test
        @DisplayName("Should have default spawn Y")
        void testDefaultSpawnY() {
            assertEquals(0, mapData.spawnY);
        }

        @Test
        @DisplayName("Should have default tile type")
        void testDefaultTile() {
            assertEquals("grass_1", mapData.defaultTile);
        }

        @Test
        @DisplayName("Should have initialized empty tiles map")
        void testDefaultTilesMap() {
            assertNotNull(mapData.tiles);
            assertTrue(mapData.tiles instanceof HashMap);
            assertTrue(mapData.tiles.isEmpty());
        }
    }

    @Nested
    @DisplayName("Field Mutations")
    class FieldMutations {

        @Test
        @DisplayName("Should allow setting map name")
        void testSetName() {
            mapData.name = "Test Town";
            assertEquals("Test Town", mapData.name);
        }

        @Test
        @DisplayName("Should allow setting width")
        void testSetWidth() {
            mapData.width = 200;
            assertEquals(200, mapData.width);
        }

        @Test
        @DisplayName("Should allow setting height")
        void testSetHeight() {
            mapData.height = 150;
            assertEquals(150, mapData.height);
        }

        @Test
        @DisplayName("Should allow setting spawn coordinates")
        void testSetSpawnCoordinates() {
            mapData.spawnX = 50;
            mapData.spawnY = 75;

            assertEquals(50, mapData.spawnX);
            assertEquals(75, mapData.spawnY);
        }

        @Test
        @DisplayName("Should allow setting default tile")
        void testSetDefaultTile() {
            mapData.defaultTile = "stone_1";
            assertEquals("stone_1", mapData.defaultTile);
        }

        @Test
        @DisplayName("Should allow adding tiles to map")
        void testAddTiles() {
            mapData.tiles.put("0,0", "grass_1");
            mapData.tiles.put("1,0", "stone_1");
            mapData.tiles.put("0,1", "water");

            assertEquals(3, mapData.tiles.size());
            assertEquals("grass_1", mapData.tiles.get("0,0"));
            assertEquals("stone_1", mapData.tiles.get("1,0"));
            assertEquals("water", mapData.tiles.get("0,1"));
        }
    }

    @Nested
    @DisplayName("Map Configuration Scenarios")
    class MapConfigurationScenarios {

        @Test
        @DisplayName("Should configure a small map")
        void testSmallMap() {
            mapData.name = "Small Arena";
            mapData.width = 20;
            mapData.height = 20;
            mapData.spawnX = 10;
            mapData.spawnY = 10;
            mapData.defaultTile = "dirt";

            assertEquals("Small Arena", mapData.name);
            assertEquals(20, mapData.width);
            assertEquals(20, mapData.height);
            assertEquals(10, mapData.spawnX);
            assertEquals(10, mapData.spawnY);
            assertEquals("dirt", mapData.defaultTile);
        }

        @Test
        @DisplayName("Should configure a large map")
        void testLargeMap() {
            mapData.name = "Vast Kingdom";
            mapData.width = 1000;
            mapData.height = 800;
            mapData.spawnX = 500;
            mapData.spawnY = 400;

            assertEquals("Vast Kingdom", mapData.name);
            assertEquals(1000, mapData.width);
            assertEquals(800, mapData.height);
        }

        @Test
        @DisplayName("Should configure a town map")
        void testTownMap() {
            mapData.name = "Starter Town";
            mapData.width = 100;
            mapData.height = 100;
            mapData.defaultTile = "cobblestone";
            mapData.spawnX = 50;
            mapData.spawnY = 50;

            // Add some buildings
            mapData.tiles.put("25,25", "building_wall");
            mapData.tiles.put("75,75", "shop_entrance");

            assertEquals("Starter Town", mapData.name);
            assertEquals("cobblestone", mapData.defaultTile);
            assertEquals(2, mapData.tiles.size());
        }
    }

    @Nested
    @DisplayName("Tile Coordinate Formats")
    class TileCoordinateFormats {

        @Test
        @DisplayName("Should support positive coordinates")
        void testPositiveCoordinates() {
            mapData.tiles.put("10,20", "grass_1");
            mapData.tiles.put("100,200", "stone_1");

            assertEquals("grass_1", mapData.tiles.get("10,20"));
            assertEquals("stone_1", mapData.tiles.get("100,200"));
        }

        @Test
        @DisplayName("Should support zero coordinates")
        void testZeroCoordinates() {
            mapData.tiles.put("0,0", "spawn_point");

            assertEquals("spawn_point", mapData.tiles.get("0,0"));
        }

        @Test
        @DisplayName("Should support large coordinates")
        void testLargeCoordinates() {
            mapData.tiles.put("9999,9999", "far_tile");

            assertEquals("far_tile", mapData.tiles.get("9999,9999"));
        }

        @Test
        @DisplayName("Should handle multiple tiles at different coordinates")
        void testMultipleTileCoordinates() {
            for (int x = 0; x < 5; x++) {
                for (int y = 0; y < 5; y++) {
                    mapData.tiles.put(x + "," + y, "tile_" + x + "_" + y);
                }
            }

            assertEquals(25, mapData.tiles.size());
            assertEquals("tile_0_0", mapData.tiles.get("0,0"));
            assertEquals("tile_4_4", mapData.tiles.get("4,4"));
        }
    }

    @Nested
    @DisplayName("Different Tile Types")
    class TileTypes {

        @Test
        @DisplayName("Should store grass tiles")
        void testGrassTiles() {
            mapData.tiles.put("0,0", "grass_1");
            mapData.tiles.put("1,0", "grass_2");
            mapData.tiles.put("2,0", "grass_3");

            assertEquals(3, mapData.tiles.size());
        }

        @Test
        @DisplayName("Should store stone tiles")
        void testStoneTiles() {
            mapData.tiles.put("0,0", "stone_1");
            mapData.tiles.put("0,1", "mossy_stone_1");

            assertEquals("stone_1", mapData.tiles.get("0,0"));
            assertEquals("mossy_stone_1", mapData.tiles.get("0,1"));
        }

        @Test
        @DisplayName("Should store water tiles")
        void testWaterTiles() {
            mapData.tiles.put("5,5", "water");
            mapData.tiles.put("5,6", "deep_water");

            assertEquals("water", mapData.tiles.get("5,5"));
            assertEquals("deep_water", mapData.tiles.get("5,6"));
        }

        @Test
        @DisplayName("Should store multi-layer tile data")
        void testMultiLayerTiles() {
            // Multi-layer format: "tileType:layer:direction"
            mapData.tiles.put("0,0", "grass_1:GROUND:0;wood_wall:WALL:1");

            String tileData = mapData.tiles.get("0,0");
            assertTrue(tileData.contains("grass_1"));
            assertTrue(tileData.contains("wood_wall"));
        }
    }

    @Nested
    @DisplayName("Spawn Point Variations")
    class SpawnPointVariations {

        @Test
        @DisplayName("Should support spawn at origin")
        void testSpawnAtOrigin() {
            mapData.spawnX = 0;
            mapData.spawnY = 0;

            assertEquals(0, mapData.spawnX);
            assertEquals(0, mapData.spawnY);
        }

        @Test
        @DisplayName("Should support spawn at center")
        void testSpawnAtCenter() {
            mapData.width = 100;
            mapData.height = 100;
            mapData.spawnX = 50;
            mapData.spawnY = 50;

            assertEquals(50, mapData.spawnX);
            assertEquals(50, mapData.spawnY);
        }

        @Test
        @DisplayName("Should support spawn at corner")
        void testSpawnAtCorner() {
            mapData.width = 100;
            mapData.height = 100;
            mapData.spawnX = 99;
            mapData.spawnY = 99;

            assertEquals(99, mapData.spawnX);
            assertEquals(99, mapData.spawnY);
        }

        @Test
        @DisplayName("Should support negative spawn coordinates")
        void testNegativeSpawnCoordinates() {
            mapData.spawnX = -10;
            mapData.spawnY = -20;

            assertEquals(-10, mapData.spawnX);
            assertEquals(-20, mapData.spawnY);
        }
    }

    @Nested
    @DisplayName("Map Dimensions")
    class MapDimensions {

        @Test
        @DisplayName("Should support square maps")
        void testSquareMap() {
            mapData.width = 50;
            mapData.height = 50;

            assertEquals(50, mapData.width);
            assertEquals(50, mapData.height);
        }

        @Test
        @DisplayName("Should support rectangular maps (wider)")
        void testWideMap() {
            mapData.width = 200;
            mapData.height = 100;

            assertEquals(200, mapData.width);
            assertEquals(100, mapData.height);
        }

        @Test
        @DisplayName("Should support rectangular maps (taller)")
        void testTallMap() {
            mapData.width = 100;
            mapData.height = 200;

            assertEquals(100, mapData.width);
            assertEquals(200, mapData.height);
        }

        @Test
        @DisplayName("Should support very small maps")
        void testVerySmallMap() {
            mapData.width = 5;
            mapData.height = 5;

            assertEquals(5, mapData.width);
            assertEquals(5, mapData.height);
        }

        @Test
        @DisplayName("Should support very large maps")
        void testVeryLargeMap() {
            mapData.width = 10000;
            mapData.height = 10000;

            assertEquals(10000, mapData.width);
            assertEquals(10000, mapData.height);
        }

        @Test
        @DisplayName("Should support zero dimensions")
        void testZeroDimensions() {
            mapData.width = 0;
            mapData.height = 0;

            assertEquals(0, mapData.width);
            assertEquals(0, mapData.height);
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle null map name")
        void testNullMapName() {
            mapData.name = null;

            assertNull(mapData.name);
        }

        @Test
        @DisplayName("Should handle empty map name")
        void testEmptyMapName() {
            mapData.name = "";

            assertEquals("", mapData.name);
        }

        @Test
        @DisplayName("Should handle null default tile")
        void testNullDefaultTile() {
            mapData.defaultTile = null;

            assertNull(mapData.defaultTile);
        }

        @Test
        @DisplayName("Should handle empty default tile")
        void testEmptyDefaultTile() {
            mapData.defaultTile = "";

            assertEquals("", mapData.defaultTile);
        }

        @Test
        @DisplayName("Should handle replacing tiles map")
        void testReplaceTilesMap() {
            HashMap<String, String> newTiles = new HashMap<>();
            newTiles.put("0,0", "new_tile");

            mapData.tiles = newTiles;

            assertEquals(1, mapData.tiles.size());
            assertEquals("new_tile", mapData.tiles.get("0,0"));
        }

        @Test
        @DisplayName("Should handle null tiles map")
        void testNullTilesMap() {
            mapData.tiles = null;

            assertNull(mapData.tiles);
        }

        @Test
        @DisplayName("Should handle negative dimensions")
        void testNegativeDimensions() {
            mapData.width = -100;
            mapData.height = -50;

            assertEquals(-100, mapData.width);
            assertEquals(-50, mapData.height);
        }

        @Test
        @DisplayName("Should handle very long map names")
        void testVeryLongMapName() {
            String longName = "A".repeat(1000);
            mapData.name = longName;

            assertEquals(1000, mapData.name.length());
        }

        @Test
        @DisplayName("Should handle special characters in map name")
        void testSpecialCharsInName() {
            mapData.name = "Map!@#$%^&*()_+-={}[]|\\:;\"'<>?,./";

            assertNotNull(mapData.name);
            assertTrue(mapData.name.contains("!@#"));
        }
    }

    @Nested
    @DisplayName("Tile Management")
    class TileManagement {

        @Test
        @DisplayName("Should allow clearing all tiles")
        void testClearAllTiles() {
            mapData.tiles.put("0,0", "tile1");
            mapData.tiles.put("1,1", "tile2");

            mapData.tiles.clear();

            assertTrue(mapData.tiles.isEmpty());
        }

        @Test
        @DisplayName("Should allow removing specific tiles")
        void testRemoveSpecificTile() {
            mapData.tiles.put("0,0", "tile1");
            mapData.tiles.put("1,1", "tile2");

            mapData.tiles.remove("0,0");

            assertEquals(1, mapData.tiles.size());
            assertNull(mapData.tiles.get("0,0"));
            assertEquals("tile2", mapData.tiles.get("1,1"));
        }

        @Test
        @DisplayName("Should allow updating tile data")
        void testUpdateTileData() {
            mapData.tiles.put("0,0", "grass_1");

            assertEquals("grass_1", mapData.tiles.get("0,0"));

            mapData.tiles.put("0,0", "stone_1");

            assertEquals("stone_1", mapData.tiles.get("0,0"));
        }

        @Test
        @DisplayName("Should handle large number of tiles")
        void testLargeNumberOfTiles() {
            for (int i = 0; i < 1000; i++) {
                mapData.tiles.put(i + ",0", "tile_" + i);
            }

            assertEquals(1000, mapData.tiles.size());
        }
    }
}
