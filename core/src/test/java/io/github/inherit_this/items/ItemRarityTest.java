package io.github.inherit_this.items;

import com.badlogic.gdx.graphics.Color;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for ItemRarity enum.
 */
@DisplayName("ItemRarity Tests")
class ItemRarityTest {

    // ==================== Enum Values Tests ====================

    @Nested
    @DisplayName("Enum Values")
    class EnumValues {

        @Test
        @DisplayName("Should have all expected rarity tiers")
        void testAllValues() {
            ItemRarity[] rarities = ItemRarity.values();

            assertEquals(5, rarities.length, "Should have exactly 5 rarity tiers");
            assertTrue(containsRarity(rarities, ItemRarity.COMMON));
            assertTrue(containsRarity(rarities, ItemRarity.UNCOMMON));
            assertTrue(containsRarity(rarities, ItemRarity.RARE));
            assertTrue(containsRarity(rarities, ItemRarity.EPIC));
            assertTrue(containsRarity(rarities, ItemRarity.LEGENDARY));
        }

        @Test
        @DisplayName("valueOf should return correct enum constant")
        void testValueOf() {
            assertEquals(ItemRarity.COMMON, ItemRarity.valueOf("COMMON"));
            assertEquals(ItemRarity.UNCOMMON, ItemRarity.valueOf("UNCOMMON"));
            assertEquals(ItemRarity.RARE, ItemRarity.valueOf("RARE"));
            assertEquals(ItemRarity.EPIC, ItemRarity.valueOf("EPIC"));
            assertEquals(ItemRarity.LEGENDARY, ItemRarity.valueOf("LEGENDARY"));
        }

        @Test
        @DisplayName("valueOf should throw for invalid name")
        void testValueOfInvalid() {
            assertThrows(IllegalArgumentException.class, () -> {
                ItemRarity.valueOf("INVALID");
            });
            assertThrows(IllegalArgumentException.class, () -> {
                ItemRarity.valueOf("MYTHIC");
            });
        }

        @Test
        @DisplayName("valueOf should be case-sensitive")
        void testValueOfCaseSensitive() {
            assertThrows(IllegalArgumentException.class, () -> {
                ItemRarity.valueOf("common");
            });
            assertThrows(IllegalArgumentException.class, () -> {
                ItemRarity.valueOf("Common");
            });
        }

        private boolean containsRarity(ItemRarity[] rarities, ItemRarity target) {
            for (ItemRarity rarity : rarities) {
                if (rarity == target) return true;
            }
            return false;
        }
    }

    // ==================== Color Tests ====================

    @Nested
    @DisplayName("Color Assignment")
    class ColorAssignment {

        @Test
        @DisplayName("COMMON should have WHITE color")
        void testCommonColor() {
            assertEquals(Color.WHITE, ItemRarity.COMMON.getColor());
        }

        @Test
        @DisplayName("UNCOMMON should have GREEN color")
        void testUncommonColor() {
            assertEquals(Color.GREEN, ItemRarity.UNCOMMON.getColor());
        }

        @Test
        @DisplayName("RARE should have BLUE color")
        void testRareColor() {
            assertEquals(Color.BLUE, ItemRarity.RARE.getColor());
        }

        @Test
        @DisplayName("EPIC should have PURPLE color")
        void testEpicColor() {
            assertEquals(Color.PURPLE, ItemRarity.EPIC.getColor());
        }

        @Test
        @DisplayName("LEGENDARY should have ORANGE color")
        void testLegendaryColor() {
            assertEquals(Color.ORANGE, ItemRarity.LEGENDARY.getColor());
        }

        @Test
        @DisplayName("All rarities should have non-null colors")
        void testAllColorsNonNull() {
            for (ItemRarity rarity : ItemRarity.values()) {
                assertNotNull(rarity.getColor(),
                    rarity + " should have a color assigned");
            }
        }

        @Test
        @DisplayName("getColor should return same instance on multiple calls")
        void testColorConsistency() {
            Color color1 = ItemRarity.COMMON.getColor();
            Color color2 = ItemRarity.COMMON.getColor();

            assertSame(color1, color2, "Should return same color instance");
        }
    }

    // ==================== Uniqueness Tests ====================

    @Nested
    @DisplayName("Uniqueness")
    class Uniqueness {

        @Test
        @DisplayName("Each rarity should have unique color")
        void testUniqueColors() {
            ItemRarity[] rarities = ItemRarity.values();

            for (int i = 0; i < rarities.length; i++) {
                for (int j = i + 1; j < rarities.length; j++) {
                    assertNotEquals(rarities[i].getColor(), rarities[j].getColor(),
                        rarities[i] + " and " + rarities[j] + " should have different colors");
                }
            }
        }

        @Test
        @DisplayName("Each rarity should have unique ordinal")
        void testUniqueOrdinals() {
            ItemRarity[] rarities = ItemRarity.values();

            for (int i = 0; i < rarities.length; i++) {
                for (int j = i + 1; j < rarities.length; j++) {
                    assertNotEquals(rarities[i].ordinal(), rarities[j].ordinal(),
                        "Ordinals should be unique");
                }
            }
        }
    }

    // ==================== Ordering Tests ====================

    @Nested
    @DisplayName("Rarity Ordering")
    class RarityOrdering {

        @Test
        @DisplayName("Rarities should be ordered from lowest to highest")
        void testRarityOrder() {
            assertEquals(0, ItemRarity.COMMON.ordinal(), "COMMON should be first");
            assertEquals(1, ItemRarity.UNCOMMON.ordinal(), "UNCOMMON should be second");
            assertEquals(2, ItemRarity.RARE.ordinal(), "RARE should be third");
            assertEquals(3, ItemRarity.EPIC.ordinal(), "EPIC should be fourth");
            assertEquals(4, ItemRarity.LEGENDARY.ordinal(), "LEGENDARY should be fifth");
        }

        @Test
        @DisplayName("Should support ordinal-based comparisons")
        void testOrdinalComparisons() {
            assertTrue(ItemRarity.COMMON.ordinal() < ItemRarity.UNCOMMON.ordinal());
            assertTrue(ItemRarity.UNCOMMON.ordinal() < ItemRarity.RARE.ordinal());
            assertTrue(ItemRarity.RARE.ordinal() < ItemRarity.EPIC.ordinal());
            assertTrue(ItemRarity.EPIC.ordinal() < ItemRarity.LEGENDARY.ordinal());
        }

        @Test
        @DisplayName("Should support compareTo for sorting")
        void testCompareTo() {
            assertTrue(ItemRarity.COMMON.compareTo(ItemRarity.LEGENDARY) < 0);
            assertTrue(ItemRarity.LEGENDARY.compareTo(ItemRarity.COMMON) > 0);
            assertEquals(0, ItemRarity.RARE.compareTo(ItemRarity.RARE));
        }
    }

    // ==================== Integration Tests ====================

    @Nested
    @DisplayName("Integration Scenarios")
    class IntegrationScenarios {

        @Test
        @DisplayName("Should support switch statements")
        void testSwitchStatement() {
            for (ItemRarity rarity : ItemRarity.values()) {
                String result;
                switch (rarity) {
                    case COMMON:
                        result = "Basic";
                        break;
                    case UNCOMMON:
                        result = "Decent";
                        break;
                    case RARE:
                        result = "Good";
                        break;
                    case EPIC:
                        result = "Great";
                        break;
                    case LEGENDARY:
                        result = "Amazing";
                        break;
                    default:
                        result = null;
                        break;
                }

                assertNotNull(result, "Switch should handle all rarities");
            }
        }

        @Test
        @DisplayName("Should work with comparison operations")
        void testComparison() {
            assertTrue(ItemRarity.COMMON == ItemRarity.COMMON);
            assertFalse(ItemRarity.COMMON == ItemRarity.RARE);
            assertTrue(ItemRarity.EPIC.equals(ItemRarity.EPIC));
            assertFalse(ItemRarity.EPIC.equals(ItemRarity.LEGENDARY));
        }

        @Test
        @DisplayName("Should be serializable via name")
        void testSerialization() {
            for (ItemRarity rarity : ItemRarity.values()) {
                String name = rarity.name();
                ItemRarity restored = ItemRarity.valueOf(name);
                assertEquals(rarity, restored, "Should restore from name");
            }
        }

        @Test
        @DisplayName("Should support array operations")
        void testArrayOperations() {
            ItemRarity[] rarities = ItemRarity.values();

            // Check array is not empty
            assertTrue(rarities.length > 0);

            // Check all elements are valid
            for (ItemRarity rarity : rarities) {
                assertNotNull(rarity);
                assertNotNull(rarity.getColor());
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
            ItemRarity rarity = ItemRarity.COMMON;
            assertNotEquals(null, rarity);
            assertFalse(rarity.equals(null));
        }

        @Test
        @DisplayName("Should handle toString")
        void testToString() {
            for (ItemRarity rarity : ItemRarity.values()) {
                String str = rarity.toString();
                assertNotNull(str);
                assertFalse(str.isEmpty());
                assertEquals(rarity.name(), str, "toString should match name");
            }
        }

        @Test
        @DisplayName("Should maintain identity")
        void testIdentity() {
            ItemRarity common1 = ItemRarity.COMMON;
            ItemRarity common2 = ItemRarity.valueOf("COMMON");

            assertSame(common1, common2, "Same enum constant should be identical");
        }

        @Test
        @DisplayName("Color objects should be LibGDX Color instances")
        void testColorTypes() {
            for (ItemRarity rarity : ItemRarity.values()) {
                assertTrue(rarity.getColor() instanceof Color,
                    rarity + " color should be a LibGDX Color instance");
            }
        }

        @Test
        @DisplayName("Should handle repeated getColor calls")
        void testRepeatedGetColor() {
            for (int i = 0; i < 100; i++) {
                Color color = ItemRarity.LEGENDARY.getColor();
                assertNotNull(color);
                assertEquals(Color.ORANGE, color);
            }
        }
    }

    // ==================== Use Case Tests ====================

    @Nested
    @DisplayName("Common Use Cases")
    class UseCases {

        @Test
        @DisplayName("Should support rarity progression logic")
        void testRarityProgression() {
            // Simulate upgrade system
            ItemRarity current = ItemRarity.COMMON;

            assertTrue(current.ordinal() < ItemRarity.UNCOMMON.ordinal(),
                "Can upgrade to uncommon");
            assertTrue(current.ordinal() < ItemRarity.LEGENDARY.ordinal(),
                "Can upgrade to legendary");
        }

        @Test
        @DisplayName("Should support rarity filtering")
        void testRarityFiltering() {
            // Simulate filtering items by minimum rarity
            ItemRarity minRarity = ItemRarity.RARE;

            assertTrue(ItemRarity.RARE.ordinal() >= minRarity.ordinal(),
                "RARE should pass RARE filter");
            assertTrue(ItemRarity.EPIC.ordinal() >= minRarity.ordinal(),
                "EPIC should pass RARE filter");
            assertTrue(ItemRarity.LEGENDARY.ordinal() >= minRarity.ordinal(),
                "LEGENDARY should pass RARE filter");
            assertFalse(ItemRarity.UNCOMMON.ordinal() >= minRarity.ordinal(),
                "UNCOMMON should not pass RARE filter");
        }

        @Test
        @DisplayName("Should support color-based UI rendering")
        void testColorUI() {
            // Verify all colors are suitable for UI display
            for (ItemRarity rarity : ItemRarity.values()) {
                Color color = rarity.getColor();

                // Colors should have valid RGBA values
                assertTrue(color.r >= 0 && color.r <= 1, "Red component should be valid");
                assertTrue(color.g >= 0 && color.g <= 1, "Green component should be valid");
                assertTrue(color.b >= 0 && color.b <= 1, "Blue component should be valid");
                assertTrue(color.a >= 0 && color.a <= 1, "Alpha component should be valid");
            }
        }
    }
}
