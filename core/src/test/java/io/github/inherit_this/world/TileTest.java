package io.github.inherit_this.world;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for the Tile class.
 */
@DisplayName("Tile Tests")
class TileTest {

    // ==================== Constructor Tests ====================

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Simple constructor should use default values")
        void testSimpleConstructor() {
            Tile tile = new Tile(null);

            assertNull(tile.getTexture());
            assertFalse(tile.isSolid());
            assertEquals(TileType.GRASS, tile.getType());
        }

        @Test
        @DisplayName("Two-parameter constructor should set solid flag")
        void testTwoParameterConstructor() {
            Tile tile = new Tile(null, true);

            assertNull(tile.getTexture());
            assertTrue(tile.isSolid());
            assertEquals(TileType.GRASS, tile.getType());
        }

        @Test
        @DisplayName("Three-parameter constructor should set type")
        void testThreeParameterConstructor() {
            Tile tile = new Tile(null, true, TileType.STONE);

            assertNull(tile.getTexture());
            assertTrue(tile.isSolid());
            assertEquals(TileType.STONE, tile.getType());
        }

        @Test
        @DisplayName("Six-parameter constructor should set layer and angle")
        void testSixParameterConstructor() {
            Tile tile = new Tile(null, false, TileType.GRASS, TileLayer.WALL, 45.0f, 2);

            assertEquals(TileLayer.WALL, tile.getLayer());
            assertEquals(45.0f, tile.getAngle(), 0.001f);
            assertEquals(2, tile.getDirection());
        }

        @Test
        @DisplayName("Seven-parameter constructor should set level")
        void testSevenParameterConstructor() {
            Tile tile = new Tile(null, false, TileType.GRASS, TileLayer.GROUND, 0.0f, 0, 5);

            assertEquals(5, tile.getLevel());
        }

        @Test
        @DisplayName("Eight-parameter constructor should set flipped")
        void testEightParameterConstructor() {
            Tile tile = new Tile(null, false, TileType.GRASS, TileLayer.GROUND, 0.0f, 0, 0, true);

            assertTrue(tile.isFlipped());
        }

        @Test
        @DisplayName("Full constructor should set all properties")
        void testFullConstructor() {
            Tile tile = new Tile(null, true, TileType.STONE, TileLayer.WALL, 90.0f, 1, 3, true, 2);

            assertNull(tile.getTexture());
            assertTrue(tile.isSolid());
            assertEquals(TileType.STONE, tile.getType());
            assertEquals(TileLayer.WALL, tile.getLayer());
            assertEquals(90.0f, tile.getAngle(), 0.001f);
            assertEquals(1, tile.getDirection());
            assertEquals(3, tile.getLevel());
            assertTrue(tile.isFlipped());
            assertEquals(2, tile.getTextureRotation());
        }

        @Test
        @DisplayName("Default constructor should set default layer to GROUND")
        void testDefaultLayer() {
            Tile tile = new Tile(null, false, TileType.GRASS);

            assertEquals(TileLayer.GROUND, tile.getLayer());
        }

        @Test
        @DisplayName("Default constructor should set default angle to -1")
        void testDefaultAngle() {
            Tile tile = new Tile(null, false, TileType.GRASS);

            assertEquals(-1f, tile.getAngle(), 0.001f);
        }

        @Test
        @DisplayName("Default constructor should set default direction to 0")
        void testDefaultDirection() {
            Tile tile = new Tile(null, false, TileType.GRASS);

            assertEquals(0, tile.getDirection());
        }

        @Test
        @DisplayName("Default constructor should set default level to 0")
        void testDefaultLevel() {
            Tile tile = new Tile(null, false, TileType.GRASS);

            assertEquals(0, tile.getLevel());
        }

        @Test
        @DisplayName("Default constructor should set default flipped to false")
        void testDefaultFlipped() {
            Tile tile = new Tile(null, false, TileType.GRASS);

            assertFalse(tile.isFlipped());
        }

        @Test
        @DisplayName("Default constructor should set default texture rotation to 0")
        void testDefaultTextureRotation() {
            Tile tile = new Tile(null, false, TileType.GRASS);

            assertEquals(0, tile.getTextureRotation());
        }
    }

    // ==================== Getter and Setter Tests ====================

    @Nested
    @DisplayName("Solid Property")
    class SolidPropertyTests {

        @Test
        @DisplayName("Should get and set solid flag")
        void testSolidFlag() {
            Tile tile = new Tile(null, false, TileType.GRASS);

            assertFalse(tile.isSolid());

            tile.setSolid(true);
            assertTrue(tile.isSolid());

            tile.setSolid(false);
            assertFalse(tile.isSolid());
        }
    }

    @Nested
    @DisplayName("Type Property")
    class TypePropertyTests {

        @Test
        @DisplayName("Should get and set type")
        void testType() {
            Tile tile = new Tile(null, false, TileType.GRASS);

            assertEquals(TileType.GRASS, tile.getType());

            tile.setType(TileType.STONE);
            assertEquals(TileType.STONE, tile.getType());

            tile.setType(TileType.WATER);
            assertEquals(TileType.WATER, tile.getType());
        }
    }

    @Nested
    @DisplayName("Layer Property")
    class LayerPropertyTests {

        @Test
        @DisplayName("Should get and set layer")
        void testLayer() {
            Tile tile = new Tile(null, false, TileType.GRASS);

            assertEquals(TileLayer.GROUND, tile.getLayer());

            tile.setLayer(TileLayer.WALL);
            assertEquals(TileLayer.WALL, tile.getLayer());

            tile.setLayer(TileLayer.ROOF);
            assertEquals(TileLayer.ROOF, tile.getLayer());
        }
    }

    @Nested
    @DisplayName("Angle Property")
    class AnglePropertyTests {

        @Test
        @DisplayName("Should get and set angle")
        void testAngle() {
            Tile tile = new Tile(null, false, TileType.GRASS);

            tile.setAngle(45.0f);
            assertEquals(45.0f, tile.getAngle(), 0.001f);

            tile.setAngle(90.0f);
            assertEquals(90.0f, tile.getAngle(), 0.001f);

            tile.setAngle(0.0f);
            assertEquals(0.0f, tile.getAngle(), 0.001f);
        }

        @Test
        @DisplayName("Should handle negative angles")
        void testNegativeAngle() {
            Tile tile = new Tile(null, false, TileType.GRASS);

            tile.setAngle(-45.0f);
            assertEquals(-45.0f, tile.getAngle(), 0.001f);
        }

        @Test
        @DisplayName("Should handle large angles")
        void testLargeAngle() {
            Tile tile = new Tile(null, false, TileType.GRASS);

            tile.setAngle(360.0f);
            assertEquals(360.0f, tile.getAngle(), 0.001f);

            tile.setAngle(720.0f);
            assertEquals(720.0f, tile.getAngle(), 0.001f);
        }
    }

    @Nested
    @DisplayName("Direction Property")
    class DirectionPropertyTests {

        @Test
        @DisplayName("Should get and set direction")
        void testDirection() {
            Tile tile = new Tile(null, false, TileType.GRASS);

            tile.setDirection(1);
            assertEquals(1, tile.getDirection());

            tile.setDirection(2);
            assertEquals(2, tile.getDirection());

            tile.setDirection(3);
            assertEquals(3, tile.getDirection());
        }

        @Test
        @DisplayName("Should handle all cardinal directions (0-3)")
        void testCardinalDirections() {
            Tile tile = new Tile(null, false, TileType.GRASS);

            for (int i = 0; i < 4; i++) {
                tile.setDirection(i);
                assertEquals(i, tile.getDirection());
            }
        }
    }

    @Nested
    @DisplayName("Level Property")
    class LevelPropertyTests {

        @Test
        @DisplayName("Should get and set level")
        void testLevel() {
            Tile tile = new Tile(null, false, TileType.GRASS);

            tile.setLevel(5);
            assertEquals(5, tile.getLevel());

            tile.setLevel(10);
            assertEquals(10, tile.getLevel());
        }

        @Test
        @DisplayName("Should handle full level range (0-15)")
        void testLevelRange() {
            Tile tile = new Tile(null, false, TileType.GRASS);

            for (int i = 0; i <= 15; i++) {
                tile.setLevel(i);
                assertEquals(i, tile.getLevel());
            }
        }
    }

    @Nested
    @DisplayName("Flipped Property")
    class FlippedPropertyTests {

        @Test
        @DisplayName("Should get and set flipped flag")
        void testFlipped() {
            Tile tile = new Tile(null, false, TileType.GRASS);

            assertFalse(tile.isFlipped());

            tile.setFlipped(true);
            assertTrue(tile.isFlipped());

            tile.setFlipped(false);
            assertFalse(tile.isFlipped());
        }
    }

    @Nested
    @DisplayName("Texture Rotation Property")
    class TextureRotationPropertyTests {

        @Test
        @DisplayName("Should get and set texture rotation")
        void testTextureRotation() {
            Tile tile = new Tile(null, false, TileType.GRASS);

            tile.setTextureRotation(1);
            assertEquals(1, tile.getTextureRotation());

            tile.setTextureRotation(2);
            assertEquals(2, tile.getTextureRotation());

            tile.setTextureRotation(3);
            assertEquals(3, tile.getTextureRotation());
        }

        @Test
        @DisplayName("Should handle all rotation increments (0-3)")
        void testRotationIncrements() {
            Tile tile = new Tile(null, false, TileType.GRASS);

            // 0 = 0°, 1 = 90°, 2 = 180°, 3 = 270°
            for (int i = 0; i < 4; i++) {
                tile.setTextureRotation(i);
                assertEquals(i, tile.getTextureRotation());
            }
        }
    }

    // ==================== Effective Angle Tests ====================

    @Nested
    @DisplayName("Effective Angle Calculation")
    class EffectiveAngleTests {

        @Test
        @DisplayName("Should use custom angle when >= 0")
        void testCustomAngle() {
            Tile tile = new Tile(null, false, TileType.GRASS, TileLayer.GROUND, 45.0f, 0);

            assertEquals(45.0f, tile.getEffectiveAngle(), 0.001f);
        }

        @Test
        @DisplayName("Should use layer default angle when custom angle < 0")
        void testLayerDefaultAngle() {
            Tile tile = new Tile(null, false, TileType.GRASS, TileLayer.WALL, -1.0f, 0);

            assertEquals(90.0f, tile.getEffectiveAngle(), 0.001f);
        }

        @Test
        @DisplayName("Should use 0 degrees for GROUND layer by default")
        void testGroundLayerDefaultAngle() {
            Tile tile = new Tile(null, false, TileType.GRASS, TileLayer.GROUND, -1.0f, 0);

            assertEquals(0.0f, tile.getEffectiveAngle(), 0.001f);
        }

        @Test
        @DisplayName("Should use 90 degrees for WALL layer by default")
        void testWallLayerDefaultAngle() {
            Tile tile = new Tile(null, false, TileType.GRASS, TileLayer.WALL, -1.0f, 0);

            assertEquals(90.0f, tile.getEffectiveAngle(), 0.001f);
        }

        @Test
        @DisplayName("Should use 45 degrees for ROOF layer by default")
        void testRoofLayerDefaultAngle() {
            Tile tile = new Tile(null, false, TileType.GRASS, TileLayer.ROOF, -1.0f, 0);

            assertEquals(45.0f, tile.getEffectiveAngle(), 0.001f);
        }

        @Test
        @DisplayName("Should override layer default when custom angle is 0")
        void testZeroCustomAngle() {
            Tile tile = new Tile(null, false, TileType.GRASS, TileLayer.WALL, 0.0f, 0);

            // Custom angle is 0, which is >= 0, so it should use 0 instead of WALL's default 90
            assertEquals(0.0f, tile.getEffectiveAngle(), 0.001f);
        }

        @Test
        @DisplayName("Should return 0 when layer is null and angle < 0")
        void testNullLayerDefaultAngle() {
            Tile tile = new Tile(null, false, TileType.GRASS);
            tile.setLayer(null);
            tile.setAngle(-1.0f);

            assertEquals(0.0f, tile.getEffectiveAngle(), 0.001f);
        }

        @Test
        @DisplayName("Should use custom angle even when layer is null")
        void testCustomAngleWithNullLayer() {
            Tile tile = new Tile(null, false, TileType.GRASS);
            tile.setLayer(null);
            tile.setAngle(45.0f);

            assertEquals(45.0f, tile.getEffectiveAngle(), 0.001f);
        }
    }

    // ==================== Integration Tests ====================

    @Nested
    @DisplayName("Integration Scenarios")
    class IntegrationTests {

        @Test
        @DisplayName("Should support typical ground tile configuration")
        void testGroundTile() {
            Tile tile = new Tile(null, false, TileType.GRASS, TileLayer.GROUND, -1.0f, 0, 0, false, 0);

            assertFalse(tile.isSolid());
            assertEquals(TileType.GRASS, tile.getType());
            assertEquals(TileLayer.GROUND, tile.getLayer());
            assertEquals(0.0f, tile.getEffectiveAngle(), 0.001f);
            assertEquals(0, tile.getLevel());
        }

        @Test
        @DisplayName("Should support typical wall tile configuration")
        void testWallTile() {
            Tile tile = new Tile(null, true, TileType.STONE, TileLayer.WALL, -1.0f, 1, 0, false, 0);

            assertTrue(tile.isSolid());
            assertEquals(TileType.STONE, tile.getType());
            assertEquals(TileLayer.WALL, tile.getLayer());
            assertEquals(90.0f, tile.getEffectiveAngle(), 0.001f);
            assertEquals(1, tile.getDirection());
        }

        @Test
        @DisplayName("Should support typical roof tile configuration")
        void testRoofTile() {
            Tile tile = new Tile(null, false, TileType.STONE, TileLayer.ROOF, -1.0f, 2, 1, false, 0);

            assertFalse(tile.isSolid());
            assertEquals(TileType.STONE, tile.getType());
            assertEquals(TileLayer.ROOF, tile.getLayer());
            assertEquals(45.0f, tile.getEffectiveAngle(), 0.001f);
            assertEquals(2, tile.getDirection());
            assertEquals(1, tile.getLevel());
        }

        @Test
        @DisplayName("Should support flipped and rotated tiles")
        void testFlippedRotatedTile() {
            Tile tile = new Tile(null, false, TileType.GRASS, TileLayer.GROUND, 0.0f, 0, 0, true, 2);

            assertTrue(tile.isFlipped());
            assertEquals(2, tile.getTextureRotation());
        }

        @Test
        @DisplayName("Should support multi-level tiles")
        void testMultiLevelTile() {
            Tile tile = new Tile(null, false, TileType.GRASS, TileLayer.GROUND, 0.0f, 0, 10, false, 0);

            assertEquals(10, tile.getLevel());
        }
    }

    // ==================== Edge Cases ====================

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle maximum level value")
        void testMaxLevel() {
            Tile tile = new Tile(null, false, TileType.GRASS);
            tile.setLevel(15);

            assertEquals(15, tile.getLevel());
        }

        @Test
        @DisplayName("Should handle all tile types")
        void testAllTileTypes() {
            for (TileType type : TileType.values()) {
                Tile tile = new Tile(null, false, type);
                assertEquals(type, tile.getType());
            }
        }

        @Test
        @DisplayName("Should handle all tile layers")
        void testAllTileLayers() {
            for (TileLayer layer : TileLayer.values()) {
                Tile tile = new Tile(null, false, TileType.GRASS, layer, -1.0f, 0);
                assertEquals(layer, tile.getLayer());
            }
        }

        @Test
        @DisplayName("Should handle mutability of properties")
        void testPropertyMutability() {
            Tile tile = new Tile(null, false, TileType.GRASS);

            tile.setSolid(true);
            tile.setType(TileType.STONE);
            tile.setLayer(TileLayer.WALL);
            tile.setAngle(45.0f);
            tile.setDirection(2);
            tile.setLevel(5);
            tile.setFlipped(true);
            tile.setTextureRotation(3);

            assertTrue(tile.isSolid());
            assertEquals(TileType.STONE, tile.getType());
            assertEquals(TileLayer.WALL, tile.getLayer());
            assertEquals(45.0f, tile.getAngle(), 0.001f);
            assertEquals(2, tile.getDirection());
            assertEquals(5, tile.getLevel());
            assertTrue(tile.isFlipped());
            assertEquals(3, tile.getTextureRotation());
        }

        @Test
        @DisplayName("Should handle null texture")
        void testNullTexture() {
            Tile tile = new Tile(null, false, TileType.GRASS);

            assertNull(tile.getTexture());
        }
    }
}
