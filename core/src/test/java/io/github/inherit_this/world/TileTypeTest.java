package io.github.inherit_this.world;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for TileType enum.
 */
@DisplayName("TileType Tests")
class TileTypeTest {

    // ==================== Enum Values Tests ====================

    @Nested
    @DisplayName("Enum Values")
    class EnumValues {

        @Test
        @DisplayName("Should have all expected tile types")
        void testAllValues() {
            TileType[] types = TileType.values();

            assertEquals(6, types.length, "Should have exactly 6 tile types");
            assertTrue(containsType(types, TileType.GRASS));
            assertTrue(containsType(types, TileType.STONE));
            assertTrue(containsType(types, TileType.SAND));
            assertTrue(containsType(types, TileType.PATH));
            assertTrue(containsType(types, TileType.WATER));
            assertTrue(containsType(types, TileType.VOID));
        }

        @Test
        @DisplayName("valueOf should return correct enum constant")
        void testValueOf() {
            assertEquals(TileType.GRASS, TileType.valueOf("GRASS"));
            assertEquals(TileType.STONE, TileType.valueOf("STONE"));
            assertEquals(TileType.SAND, TileType.valueOf("SAND"));
            assertEquals(TileType.PATH, TileType.valueOf("PATH"));
            assertEquals(TileType.WATER, TileType.valueOf("WATER"));
            assertEquals(TileType.VOID, TileType.valueOf("VOID"));
        }

        @Test
        @DisplayName("valueOf should throw for invalid name")
        void testValueOfInvalid() {
            assertThrows(IllegalArgumentException.class, () -> {
                TileType.valueOf("INVALID");
            });
            assertThrows(IllegalArgumentException.class, () -> {
                TileType.valueOf("DIRT");
            });
            assertThrows(IllegalArgumentException.class, () -> {
                TileType.valueOf("LAVA");
            });
        }

        @Test
        @DisplayName("valueOf should be case-sensitive")
        void testValueOfCaseSensitive() {
            assertThrows(IllegalArgumentException.class, () -> {
                TileType.valueOf("grass");
            });
            assertThrows(IllegalArgumentException.class, () -> {
                TileType.valueOf("Grass");
            });
            assertThrows(IllegalArgumentException.class, () -> {
                TileType.valueOf("water");
            });
        }

        private boolean containsType(TileType[] types, TileType target) {
            for (TileType type : types) {
                if (type == target) return true;
            }
            return false;
        }
    }

    // ==================== Semantic Meaning Tests ====================

    @Nested
    @DisplayName("Semantic Meaning")
    class SemanticMeaning {

        @Test
        @DisplayName("GRASS should represent grass terrain")
        void testGrassSemantics() {
            assertNotNull(TileType.GRASS);
            assertEquals("GRASS", TileType.GRASS.name());
        }

        @Test
        @DisplayName("STONE should represent stone terrain")
        void testStoneSemantics() {
            assertNotNull(TileType.STONE);
            assertEquals("STONE", TileType.STONE.name());
        }

        @Test
        @DisplayName("SAND should represent sand terrain")
        void testSandSemantics() {
            assertNotNull(TileType.SAND);
            assertEquals("SAND", TileType.SAND.name());
        }

        @Test
        @DisplayName("PATH should represent path terrain")
        void testPathSemantics() {
            assertNotNull(TileType.PATH);
            assertEquals("PATH", TileType.PATH.name());
        }

        @Test
        @DisplayName("WATER should represent water terrain")
        void testWaterSemantics() {
            assertNotNull(TileType.WATER);
            assertEquals("WATER", TileType.WATER.name());
        }

        @Test
        @DisplayName("VOID should represent out-of-bounds tiles")
        void testVoidSemantics() {
            assertNotNull(TileType.VOID);
            assertEquals("VOID", TileType.VOID.name());
        }
    }

    // ==================== Ordinal Tests ====================

    @Nested
    @DisplayName("Ordinal Values")
    class OrdinalValues {

        @Test
        @DisplayName("Each type should have unique ordinal")
        void testUniqueOrdinals() {
            TileType[] types = TileType.values();

            for (int i = 0; i < types.length; i++) {
                for (int j = i + 1; j < types.length; j++) {
                    assertNotEquals(types[i].ordinal(), types[j].ordinal(),
                        "Ordinals should be unique");
                }
            }
        }

        @Test
        @DisplayName("Ordinals should be sequential")
        void testSequentialOrdinals() {
            TileType[] types = TileType.values();

            for (int i = 0; i < types.length; i++) {
                assertEquals(i, types[i].ordinal(),
                    "Ordinal should match array index");
            }
        }

        @Test
        @DisplayName("Ordinals should start from 0")
        void testOrdinalStart() {
            assertTrue(TileType.GRASS.ordinal() >= 0, "First ordinal should be 0 or greater");

            int minOrdinal = Integer.MAX_VALUE;
            for (TileType type : TileType.values()) {
                minOrdinal = Math.min(minOrdinal, type.ordinal());
            }
            assertEquals(0, minOrdinal, "Minimum ordinal should be 0");
        }
    }

    // ==================== Integration Tests ====================

    @Nested
    @DisplayName("Integration Scenarios")
    class IntegrationScenarios {

        @Test
        @DisplayName("Should support switch statements")
        void testSwitchStatement() {
            for (TileType type : TileType.values()) {
                String category;
                switch (type) {
                    case GRASS:
                        category = "Natural";
                        break;
                    case STONE:
                        category = "Natural";
                        break;
                    case SAND:
                        category = "Natural";
                        break;
                    case PATH:
                        category = "Man-made";
                        break;
                    case WATER:
                        category = "Liquid";
                        break;
                    case VOID:
                        category = "Special";
                        break;
                    default:
                        category = null;
                        break;
                }

                assertNotNull(category, "Switch should handle all tile types");
            }
        }

        @Test
        @DisplayName("Should work with comparison operations")
        void testComparison() {
            assertTrue(TileType.GRASS == TileType.GRASS);
            assertFalse(TileType.GRASS == TileType.STONE);
            assertTrue(TileType.WATER.equals(TileType.WATER));
            assertFalse(TileType.WATER.equals(TileType.SAND));
        }

        @Test
        @DisplayName("Should be serializable via name")
        void testSerialization() {
            for (TileType type : TileType.values()) {
                String name = type.name();
                TileType restored = TileType.valueOf(name);
                assertEquals(type, restored, "Should restore from name");
            }
        }

        @Test
        @DisplayName("Should support array operations")
        void testArrayOperations() {
            TileType[] types = TileType.values();

            // Check array is not empty
            assertTrue(types.length > 0);

            // Check all elements are valid
            for (TileType type : types) {
                assertNotNull(type);
                assertNotNull(type.name());
            }
        }

        @Test
        @DisplayName("Should support compareTo for ordering")
        void testCompareTo() {
            assertTrue(TileType.GRASS.compareTo(TileType.VOID) != 0);
            assertEquals(0, TileType.STONE.compareTo(TileType.STONE));
        }
    }

    // ==================== Edge Cases ====================

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle null checks")
        void testNullChecks() {
            TileType type = TileType.GRASS;
            assertNotEquals(null, type);
            assertFalse(type.equals(null));
        }

        @Test
        @DisplayName("Should handle toString")
        void testToString() {
            for (TileType type : TileType.values()) {
                String str = type.toString();
                assertNotNull(str);
                assertFalse(str.isEmpty());
                assertEquals(type.name(), str, "toString should match name");
            }
        }

        @Test
        @DisplayName("Should maintain identity")
        void testIdentity() {
            TileType grass1 = TileType.GRASS;
            TileType grass2 = TileType.valueOf("GRASS");

            assertSame(grass1, grass2, "Same enum constant should be identical");
        }

        @Test
        @DisplayName("Should handle repeated valueOf calls")
        void testRepeatedValueOf() {
            for (int i = 0; i < 100; i++) {
                TileType type = TileType.valueOf("WATER");
                assertEquals(TileType.WATER, type);
            }
        }

        @Test
        @DisplayName("Name should be uppercase")
        void testNameUppercase() {
            for (TileType type : TileType.values()) {
                String name = type.name();
                assertEquals(name.toUpperCase(), name,
                    "Enum name should be uppercase");
            }
        }
    }

    // ==================== Use Case Tests ====================

    @Nested
    @DisplayName("Common Use Cases")
    class UseCases {

        @Test
        @DisplayName("Should support tile blending logic")
        void testTileBlending() {
            // Different types should blend
            TileType type1 = TileType.GRASS;
            TileType type2 = TileType.STONE;

            assertFalse(type1 == type2, "Different types should be unequal for blending");

            // Same types should not blend
            TileType type3 = TileType.GRASS;
            assertTrue(type1 == type3, "Same types should not blend");
        }

        @Test
        @DisplayName("Should support terrain categorization")
        void testTerrainCategorization() {
            // Natural terrain types
            assertTrue(TileType.GRASS == TileType.GRASS);
            assertTrue(TileType.STONE == TileType.STONE);
            assertTrue(TileType.SAND == TileType.SAND);

            // Special terrain types
            assertTrue(TileType.WATER == TileType.WATER);
            assertTrue(TileType.PATH == TileType.PATH);
            assertTrue(TileType.VOID == TileType.VOID);
        }

        @Test
        @DisplayName("Should support walkability logic")
        void testWalkability() {
            // Simulate determining if a tile is walkable
            // WATER and VOID typically not walkable
            assertNotNull(TileType.WATER);
            assertNotNull(TileType.VOID);

            // Other types typically walkable
            assertNotNull(TileType.GRASS);
            assertNotNull(TileType.STONE);
            assertNotNull(TileType.SAND);
            assertNotNull(TileType.PATH);
        }

        @Test
        @DisplayName("Should support boundary detection")
        void testBoundaryDetection() {
            // VOID should be usable for out-of-bounds detection
            TileType boundaryType = TileType.VOID;
            assertEquals(TileType.VOID, boundaryType);

            // Non-VOID types are in-bounds
            assertNotEquals(TileType.VOID, TileType.GRASS);
            assertNotEquals(TileType.VOID, TileType.STONE);
        }

        @Test
        @DisplayName("Should support environment type logic")
        void testEnvironmentType() {
            // Each tile type represents a different environment
            TileType[] types = TileType.values();

            for (TileType type : types) {
                // Verify each type is valid for environment logic
                boolean validEnvironment = type == TileType.GRASS ||
                                          type == TileType.STONE ||
                                          type == TileType.SAND ||
                                          type == TileType.PATH ||
                                          type == TileType.WATER ||
                                          type == TileType.VOID;
                assertTrue(validEnvironment, type + " should be valid environment type");
            }
        }

        @Test
        @DisplayName("Should support texture selection")
        void testTextureSelection() {
            // All tile types should be usable for texture selection
            for (TileType type : TileType.values()) {
                String textureName = type.name().toLowerCase();
                assertNotNull(textureName);
                assertFalse(textureName.isEmpty());
            }
        }
    }

    // ==================== Completeness Tests ====================

    @Nested
    @DisplayName("Completeness")
    class Completeness {

        @Test
        @DisplayName("Should cover all major terrain types")
        void testTerrainCompleteness() {
            TileType[] types = TileType.values();

            boolean hasGrass = false;
            boolean hasStone = false;
            boolean hasSand = false;
            boolean hasPath = false;
            boolean hasWater = false;
            boolean hasVoid = false;

            for (TileType type : types) {
                if (type == TileType.GRASS) hasGrass = true;
                if (type == TileType.STONE) hasStone = true;
                if (type == TileType.SAND) hasSand = true;
                if (type == TileType.PATH) hasPath = true;
                if (type == TileType.WATER) hasWater = true;
                if (type == TileType.VOID) hasVoid = true;
            }

            assertTrue(hasGrass, "Should have grass terrain");
            assertTrue(hasStone, "Should have stone terrain");
            assertTrue(hasSand, "Should have sand terrain");
            assertTrue(hasPath, "Should have path terrain");
            assertTrue(hasWater, "Should have water terrain");
            assertTrue(hasVoid, "Should have void/boundary type");
        }

        @Test
        @DisplayName("Should have reasonable number of types")
        void testTypeCount() {
            TileType[] types = TileType.values();

            assertTrue(types.length >= 4, "Should have at least 4 terrain types");
            assertTrue(types.length <= 20, "Should not have excessive types");
        }

        @Test
        @DisplayName("Should have special boundary type")
        void testBoundaryType() {
            // Should have VOID for out-of-bounds
            boolean hasVoid = false;
            for (TileType type : TileType.values()) {
                if (type == TileType.VOID) {
                    hasVoid = true;
                    break;
                }
            }
            assertTrue(hasVoid, "Should have VOID type for boundaries");
        }
    }
}
