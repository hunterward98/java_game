package io.github.inherit_this.world;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for TileLayer enum.
 */
@DisplayName("TileLayer Tests")
class TileLayerTest {

    // ==================== Enum Values Tests ====================

    @Nested
    @DisplayName("Enum Values")
    class EnumValues {

        @Test
        @DisplayName("Should have all expected tile layers")
        void testAllValues() {
            TileLayer[] layers = TileLayer.values();

            assertEquals(3, layers.length, "Should have exactly 3 tile layers");
            assertTrue(containsLayer(layers, TileLayer.GROUND));
            assertTrue(containsLayer(layers, TileLayer.WALL));
            assertTrue(containsLayer(layers, TileLayer.ROOF));
        }

        @Test
        @DisplayName("valueOf should return correct enum constant")
        void testValueOf() {
            assertEquals(TileLayer.GROUND, TileLayer.valueOf("GROUND"));
            assertEquals(TileLayer.WALL, TileLayer.valueOf("WALL"));
            assertEquals(TileLayer.ROOF, TileLayer.valueOf("ROOF"));
        }

        @Test
        @DisplayName("valueOf should throw for invalid name")
        void testValueOfInvalid() {
            assertThrows(IllegalArgumentException.class, () -> {
                TileLayer.valueOf("INVALID");
            });
            assertThrows(IllegalArgumentException.class, () -> {
                TileLayer.valueOf("FLOOR");
            });
        }

        private boolean containsLayer(TileLayer[] layers, TileLayer target) {
            for (TileLayer layer : layers) {
                if (layer == target) return true;
            }
            return false;
        }
    }

    // ==================== Render Order Tests ====================

    @Nested
    @DisplayName("Render Order")
    class RenderOrder {

        @Test
        @DisplayName("GROUND should have render order 0")
        void testGroundRenderOrder() {
            assertEquals(0, TileLayer.GROUND.getRenderOrder());
        }

        @Test
        @DisplayName("WALL should have render order 1")
        void testWallRenderOrder() {
            assertEquals(1, TileLayer.WALL.getRenderOrder());
        }

        @Test
        @DisplayName("ROOF should have render order 2")
        void testRoofRenderOrder() {
            assertEquals(2, TileLayer.ROOF.getRenderOrder());
        }

        @Test
        @DisplayName("Render orders should be sequential")
        void testSequentialRenderOrder() {
            assertEquals(0, TileLayer.GROUND.getRenderOrder());
            assertEquals(1, TileLayer.WALL.getRenderOrder());
            assertEquals(2, TileLayer.ROOF.getRenderOrder());
        }

        @Test
        @DisplayName("Render orders should allow sorting")
        void testRenderOrderSorting() {
            assertTrue(TileLayer.GROUND.getRenderOrder() < TileLayer.WALL.getRenderOrder());
            assertTrue(TileLayer.WALL.getRenderOrder() < TileLayer.ROOF.getRenderOrder());
        }
    }

    // ==================== Default Angle Tests ====================

    @Nested
    @DisplayName("Default Angles")
    class DefaultAngles {

        @Test
        @DisplayName("GROUND should have 0 degree angle")
        void testGroundAngle() {
            assertEquals(0f, TileLayer.GROUND.getDefaultAngle(), 0.001f);
        }

        @Test
        @DisplayName("WALL should have 90 degree angle")
        void testWallAngle() {
            assertEquals(90f, TileLayer.WALL.getDefaultAngle(), 0.001f);
        }

        @Test
        @DisplayName("ROOF should have 45 degree angle")
        void testRoofAngle() {
            assertEquals(45f, TileLayer.ROOF.getDefaultAngle(), 0.001f);
        }

        @Test
        @DisplayName("All angles should be non-negative")
        void testAnglesNonNegative() {
            for (TileLayer layer : TileLayer.values()) {
                assertTrue(layer.getDefaultAngle() >= 0f,
                    layer + " angle should be non-negative");
            }
        }
    }

    // ==================== Y Offset Tests ====================

    @Nested
    @DisplayName("Y Offset")
    class YOffset {

        @Test
        @DisplayName("GROUND should have Y offset 0")
        void testGroundYOffset() {
            assertEquals(0f, TileLayer.GROUND.getYOffset(), 0.001f);
        }

        @Test
        @DisplayName("WALL should have Y offset 0")
        void testWallYOffset() {
            assertEquals(0f, TileLayer.WALL.getYOffset(), 0.001f);
        }

        @Test
        @DisplayName("ROOF should have Y offset 32")
        void testRoofYOffset() {
            assertEquals(32f, TileLayer.ROOF.getYOffset(), 0.001f);
        }

        @Test
        @DisplayName("All Y offsets should be non-negative")
        void testYOffsetsNonNegative() {
            for (TileLayer layer : TileLayer.values()) {
                assertTrue(layer.getYOffset() >= 0f,
                    layer + " Y offset should be non-negative");
            }
        }

        @Test
        @DisplayName("ROOF should be highest layer")
        void testRoofHighest() {
            assertTrue(TileLayer.ROOF.getYOffset() > TileLayer.GROUND.getYOffset());
            assertTrue(TileLayer.ROOF.getYOffset() > TileLayer.WALL.getYOffset());
        }
    }

    // ==================== Integration Tests ====================

    @Nested
    @DisplayName("Integration Scenarios")
    class IntegrationScenarios {

        @Test
        @DisplayName("Should support switch statements")
        void testSwitchStatement() {
            for (TileLayer layer : TileLayer.values()) {
                String description;
                switch (layer) {
                    case GROUND:
                        description = "Floor tiles";
                        break;
                    case WALL:
                        description = "Vertical walls";
                        break;
                    case ROOF:
                        description = "Angled roofs";
                        break;
                    default:
                        description = null;
                        break;
                }

                assertNotNull(description, "Switch should handle all tile layers");
            }
        }

        @Test
        @DisplayName("Should work with comparison operations")
        void testComparison() {
            assertTrue(TileLayer.GROUND == TileLayer.GROUND);
            assertFalse(TileLayer.GROUND == TileLayer.WALL);
            assertTrue(TileLayer.ROOF.equals(TileLayer.ROOF));
            assertFalse(TileLayer.ROOF.equals(TileLayer.GROUND));
        }

        @Test
        @DisplayName("Should be serializable via name")
        void testSerialization() {
            for (TileLayer layer : TileLayer.values()) {
                String name = layer.name();
                TileLayer restored = TileLayer.valueOf(name);
                assertEquals(layer, restored, "Should restore from name");
            }
        }

        @Test
        @DisplayName("Should support sorting by render order")
        void testSortingByRenderOrder() {
            TileLayer[] layers = {TileLayer.ROOF, TileLayer.GROUND, TileLayer.WALL};

            java.util.Arrays.sort(layers,
                (a, b) -> Integer.compare(a.getRenderOrder(), b.getRenderOrder()));

            assertEquals(TileLayer.GROUND, layers[0]);
            assertEquals(TileLayer.WALL, layers[1]);
            assertEquals(TileLayer.ROOF, layers[2]);
        }
    }

    // ==================== Use Case Tests ====================

    @Nested
    @DisplayName("Common Use Cases")
    class UseCases {

        @Test
        @DisplayName("Should support 3D rendering logic")
        void test3DRendering() {
            // Ground tiles render flat
            assertEquals(0f, TileLayer.GROUND.getDefaultAngle(), 0.001f);

            // Wall tiles render vertically
            assertEquals(90f, TileLayer.WALL.getDefaultAngle(), 0.001f);

            // Roof tiles render at angle
            assertEquals(45f, TileLayer.ROOF.getDefaultAngle(), 0.001f);
        }

        @Test
        @DisplayName("Should support layer-based height calculation")
        void testHeightCalculation() {
            // Can calculate total height based on layer
            float groundHeight = TileLayer.GROUND.getYOffset();
            float wallHeight = TileLayer.WALL.getYOffset();
            float roofHeight = TileLayer.ROOF.getYOffset();

            assertTrue(roofHeight >= wallHeight);
            assertTrue(wallHeight >= groundHeight);
        }

        @Test
        @DisplayName("Should support render order priority")
        void testRenderPriority() {
            // Lower render order should render first
            for (int i = 0; i < TileLayer.values().length - 1; i++) {
                TileLayer current = TileLayer.values()[i];
                TileLayer next = TileLayer.values()[i + 1];

                assertTrue(current.getRenderOrder() < next.getRenderOrder(),
                    current + " should render before " + next);
            }
        }
    }

    // ==================== Edge Cases ====================

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle null checks")
        void testNullChecks() {
            TileLayer layer = TileLayer.GROUND;
            assertNotEquals(null, layer);
            assertFalse(layer.equals(null));
        }

        @Test
        @DisplayName("Should handle toString")
        void testToString() {
            for (TileLayer layer : TileLayer.values()) {
                String str = layer.toString();
                assertNotNull(str);
                assertFalse(str.isEmpty());
                assertEquals(layer.name(), str, "toString should match name");
            }
        }

        @Test
        @DisplayName("Should maintain identity")
        void testIdentity() {
            TileLayer ground1 = TileLayer.GROUND;
            TileLayer ground2 = TileLayer.valueOf("GROUND");

            assertSame(ground1, ground2, "Same enum constant should be identical");
        }
    }
}
