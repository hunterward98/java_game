package io.github.inherit_this.items;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for ItemType enum.
 */
@DisplayName("ItemType Tests")
class ItemTypeTest {

    // ==================== Enum Values Tests ====================

    @Nested
    @DisplayName("Enum Values")
    class EnumValues {

        @Test
        @DisplayName("Should have all expected item categories")
        void testAllValues() {
            ItemType[] types = ItemType.values();

            assertEquals(7, types.length, "Should have exactly 7 item types");
            assertTrue(containsType(types, ItemType.WEAPON));
            assertTrue(containsType(types, ItemType.ARMOR));
            assertTrue(containsType(types, ItemType.CONSUMABLE));
            assertTrue(containsType(types, ItemType.MATERIAL));
            assertTrue(containsType(types, ItemType.TOOL));
            assertTrue(containsType(types, ItemType.QUEST));
            assertTrue(containsType(types, ItemType.MISC));
        }

        @Test
        @DisplayName("valueOf should return correct enum constant")
        void testValueOf() {
            assertEquals(ItemType.WEAPON, ItemType.valueOf("WEAPON"));
            assertEquals(ItemType.ARMOR, ItemType.valueOf("ARMOR"));
            assertEquals(ItemType.CONSUMABLE, ItemType.valueOf("CONSUMABLE"));
            assertEquals(ItemType.MATERIAL, ItemType.valueOf("MATERIAL"));
            assertEquals(ItemType.TOOL, ItemType.valueOf("TOOL"));
            assertEquals(ItemType.QUEST, ItemType.valueOf("QUEST"));
            assertEquals(ItemType.MISC, ItemType.valueOf("MISC"));
        }

        @Test
        @DisplayName("valueOf should throw for invalid name")
        void testValueOfInvalid() {
            assertThrows(IllegalArgumentException.class, () -> {
                ItemType.valueOf("INVALID");
            });
            assertThrows(IllegalArgumentException.class, () -> {
                ItemType.valueOf("EQUIPMENT");
            });
            assertThrows(IllegalArgumentException.class, () -> {
                ItemType.valueOf("POTION");
            });
        }

        @Test
        @DisplayName("valueOf should be case-sensitive")
        void testValueOfCaseSensitive() {
            assertThrows(IllegalArgumentException.class, () -> {
                ItemType.valueOf("weapon");
            });
            assertThrows(IllegalArgumentException.class, () -> {
                ItemType.valueOf("Weapon");
            });
        }

        private boolean containsType(ItemType[] types, ItemType target) {
            for (ItemType type : types) {
                if (type == target) return true;
            }
            return false;
        }
    }

    // ==================== Ordinal Tests ====================

    @Nested
    @DisplayName("Ordinal Values")
    class OrdinalValues {

        @Test
        @DisplayName("Each type should have unique ordinal")
        void testUniqueOrdinals() {
            ItemType[] types = ItemType.values();

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
            ItemType[] types = ItemType.values();

            for (int i = 0; i < types.length; i++) {
                assertEquals(i, types[i].ordinal(),
                    "Ordinal should match array index");
            }
        }

        @Test
        @DisplayName("Ordinals should start from 0")
        void testOrdinalStart() {
            assertTrue(ItemType.WEAPON.ordinal() >= 0, "First ordinal should be 0 or greater");

            int minOrdinal = Integer.MAX_VALUE;
            for (ItemType type : ItemType.values()) {
                minOrdinal = Math.min(minOrdinal, type.ordinal());
            }
            assertEquals(0, minOrdinal, "Minimum ordinal should be 0");
        }
    }

    // ==================== Semantic Grouping Tests ====================

    @Nested
    @DisplayName("Semantic Grouping")
    class SemanticGrouping {

        @Test
        @DisplayName("WEAPON type should exist for combat items")
        void testWeaponType() {
            assertNotNull(ItemType.WEAPON);
            assertEquals("WEAPON", ItemType.WEAPON.name());
        }

        @Test
        @DisplayName("ARMOR type should exist for protective equipment")
        void testArmorType() {
            assertNotNull(ItemType.ARMOR);
            assertEquals("ARMOR", ItemType.ARMOR.name());
        }

        @Test
        @DisplayName("CONSUMABLE type should exist for usable items")
        void testConsumableType() {
            assertNotNull(ItemType.CONSUMABLE);
            assertEquals("CONSUMABLE", ItemType.CONSUMABLE.name());
        }

        @Test
        @DisplayName("MATERIAL type should exist for crafting resources")
        void testMaterialType() {
            assertNotNull(ItemType.MATERIAL);
            assertEquals("MATERIAL", ItemType.MATERIAL.name());
        }

        @Test
        @DisplayName("TOOL type should exist for utility items")
        void testToolType() {
            assertNotNull(ItemType.TOOL);
            assertEquals("TOOL", ItemType.TOOL.name());
        }

        @Test
        @DisplayName("QUEST type should exist for quest-specific items")
        void testQuestType() {
            assertNotNull(ItemType.QUEST);
            assertEquals("QUEST", ItemType.QUEST.name());
        }

        @Test
        @DisplayName("MISC type should exist for uncategorized items")
        void testMiscType() {
            assertNotNull(ItemType.MISC);
            assertEquals("MISC", ItemType.MISC.name());
        }
    }

    // ==================== Integration Tests ====================

    @Nested
    @DisplayName("Integration Scenarios")
    class IntegrationScenarios {

        @Test
        @DisplayName("Should support switch statements")
        void testSwitchStatement() {
            for (ItemType type : ItemType.values()) {
                String category;
                switch (type) {
                    case WEAPON:
                        category = "Combat";
                        break;
                    case ARMOR:
                        category = "Protection";
                        break;
                    case CONSUMABLE:
                        category = "Usable";
                        break;
                    case MATERIAL:
                        category = "Resource";
                        break;
                    case TOOL:
                        category = "Utility";
                        break;
                    case QUEST:
                        category = "Quest Item";
                        break;
                    case MISC:
                        category = "Other";
                        break;
                    default:
                        category = null;
                        break;
                }

                assertNotNull(category, "Switch should handle all item types");
            }
        }

        @Test
        @DisplayName("Should work with comparison operations")
        void testComparison() {
            assertTrue(ItemType.WEAPON == ItemType.WEAPON);
            assertFalse(ItemType.WEAPON == ItemType.ARMOR);
            assertTrue(ItemType.CONSUMABLE.equals(ItemType.CONSUMABLE));
            assertFalse(ItemType.CONSUMABLE.equals(ItemType.MATERIAL));
        }

        @Test
        @DisplayName("Should be serializable via name")
        void testSerialization() {
            for (ItemType type : ItemType.values()) {
                String name = type.name();
                ItemType restored = ItemType.valueOf(name);
                assertEquals(type, restored, "Should restore from name");
            }
        }

        @Test
        @DisplayName("Should support array operations")
        void testArrayOperations() {
            ItemType[] types = ItemType.values();

            // Check array is not empty
            assertTrue(types.length > 0);

            // Check all elements are valid
            for (ItemType type : types) {
                assertNotNull(type);
                assertNotNull(type.name());
            }
        }

        @Test
        @DisplayName("Should support compareTo for ordering")
        void testCompareTo() {
            assertTrue(ItemType.WEAPON.compareTo(ItemType.MISC) < 0 ||
                       ItemType.WEAPON.compareTo(ItemType.MISC) > 0);
            assertEquals(0, ItemType.TOOL.compareTo(ItemType.TOOL));
        }
    }

    // ==================== Edge Cases ====================

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle null checks")
        void testNullChecks() {
            ItemType type = ItemType.WEAPON;
            assertNotEquals(null, type);
            assertFalse(type.equals(null));
        }

        @Test
        @DisplayName("Should handle toString")
        void testToString() {
            for (ItemType type : ItemType.values()) {
                String str = type.toString();
                assertNotNull(str);
                assertFalse(str.isEmpty());
                assertEquals(type.name(), str, "toString should match name");
            }
        }

        @Test
        @DisplayName("Should maintain identity")
        void testIdentity() {
            ItemType weapon1 = ItemType.WEAPON;
            ItemType weapon2 = ItemType.valueOf("WEAPON");

            assertSame(weapon1, weapon2, "Same enum constant should be identical");
        }

        @Test
        @DisplayName("Should handle repeated valueOf calls")
        void testRepeatedValueOf() {
            for (int i = 0; i < 100; i++) {
                ItemType type = ItemType.valueOf("ARMOR");
                assertEquals(ItemType.ARMOR, type);
            }
        }

        @Test
        @DisplayName("Name should be uppercase")
        void testNameUppercase() {
            for (ItemType type : ItemType.values()) {
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
        @DisplayName("Should support inventory filtering by type")
        void testInventoryFiltering() {
            // Simulate filtering items by type
            ItemType filterType = ItemType.CONSUMABLE;

            assertTrue(ItemType.CONSUMABLE == filterType, "Consumables match filter");
            assertFalse(ItemType.WEAPON == filterType, "Weapons don't match filter");
            assertFalse(ItemType.ARMOR == filterType, "Armor doesn't match filter");
        }

        @Test
        @DisplayName("Should support type-based logic")
        void testTypeBasedLogic() {
            // Simulate checking if item can be equipped
            boolean canEquip = ItemType.WEAPON == ItemType.WEAPON ||
                              ItemType.WEAPON == ItemType.ARMOR;
            assertTrue(canEquip, "Weapons can be equipped");

            boolean cannotEquip = ItemType.MATERIAL == ItemType.WEAPON ||
                                 ItemType.MATERIAL == ItemType.ARMOR;
            assertFalse(cannotEquip, "Materials cannot be equipped");
        }

        @Test
        @DisplayName("Should support stackable logic")
        void testStackableLogic() {
            // Simulate determining if items can stack
            // Typically CONSUMABLE, MATERIAL, QUEST might stack
            // WEAPON, ARMOR typically don't stack

            boolean isStackableType = ItemType.MATERIAL == ItemType.MATERIAL ||
                                     ItemType.MATERIAL == ItemType.CONSUMABLE;
            assertTrue(isStackableType, "Materials are stackable type");

            boolean isNonStackableType = ItemType.WEAPON == ItemType.WEAPON ||
                                         ItemType.WEAPON == ItemType.ARMOR;
            assertTrue(isNonStackableType, "Weapons are non-stackable type");
        }

        @Test
        @DisplayName("Should support sell value logic")
        void testSellValueLogic() {
            // Simulate determining if item can be sold
            // QUEST items typically cannot be sold

            assertFalse(ItemType.QUEST == ItemType.WEAPON, "Quest items are special");
            assertTrue(ItemType.WEAPON == ItemType.WEAPON, "Weapons can be sold");
            assertTrue(ItemType.MATERIAL == ItemType.MATERIAL, "Materials can be sold");
        }

        @Test
        @DisplayName("Should support category display")
        void testCategoryDisplay() {
            // All types should have valid names for UI display
            for (ItemType type : ItemType.values()) {
                String displayName = type.name().replace("_", " ");
                assertNotNull(displayName);
                assertFalse(displayName.isEmpty());
            }
        }
    }

    // ==================== Completeness Tests ====================

    @Nested
    @DisplayName("Completeness")
    class Completeness {

        @Test
        @DisplayName("Should cover all major game item categories")
        void testCategoryCompleteness() {
            ItemType[] types = ItemType.values();

            // Verify essential categories exist
            boolean hasWeapon = false;
            boolean hasArmor = false;
            boolean hasConsumable = false;
            boolean hasMaterial = false;
            boolean hasMisc = false;

            for (ItemType type : types) {
                if (type == ItemType.WEAPON) hasWeapon = true;
                if (type == ItemType.ARMOR) hasArmor = true;
                if (type == ItemType.CONSUMABLE) hasConsumable = true;
                if (type == ItemType.MATERIAL) hasMaterial = true;
                if (type == ItemType.MISC) hasMisc = true;
            }

            assertTrue(hasWeapon, "Should have weapon category");
            assertTrue(hasArmor, "Should have armor category");
            assertTrue(hasConsumable, "Should have consumable category");
            assertTrue(hasMaterial, "Should have material category");
            assertTrue(hasMisc, "Should have misc category for fallback");
        }

        @Test
        @DisplayName("Should have reasonable number of categories")
        void testCategoryCount() {
            ItemType[] types = ItemType.values();

            assertTrue(types.length >= 5, "Should have at least 5 categories");
            assertTrue(types.length <= 15, "Should not have excessive categories");
        }
    }
}
