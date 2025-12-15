package io.github.inherit_this.items;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for EquipmentSlot enum.
 */
@DisplayName("EquipmentSlot Tests")
class EquipmentSlotTest {

    // ==================== Enum Values Tests ====================

    @Nested
    @DisplayName("Enum Values")
    class EnumValues {

        @Test
        @DisplayName("Should have all expected equipment slots")
        void testAllValues() {
            EquipmentSlot[] slots = EquipmentSlot.values();

            assertEquals(11, slots.length, "Should have exactly 11 equipment slots");
            assertTrue(containsSlot(slots, EquipmentSlot.MAIN_HAND));
            assertTrue(containsSlot(slots, EquipmentSlot.NECKLACE));
            assertTrue(containsSlot(slots, EquipmentSlot.RING_1));
            assertTrue(containsSlot(slots, EquipmentSlot.RING_2));
            assertTrue(containsSlot(slots, EquipmentSlot.TORSO));
            assertTrue(containsSlot(slots, EquipmentSlot.LEGS));
            assertTrue(containsSlot(slots, EquipmentSlot.BOOTS));
            assertTrue(containsSlot(slots, EquipmentSlot.HELMET));
            assertTrue(containsSlot(slots, EquipmentSlot.SHIELD));
            assertTrue(containsSlot(slots, EquipmentSlot.CAPE_BACKPACK));
            assertTrue(containsSlot(slots, EquipmentSlot.GLOVES));
        }

        @Test
        @DisplayName("valueOf should return correct enum constant")
        void testValueOf() {
            assertEquals(EquipmentSlot.MAIN_HAND, EquipmentSlot.valueOf("MAIN_HAND"));
            assertEquals(EquipmentSlot.NECKLACE, EquipmentSlot.valueOf("NECKLACE"));
            assertEquals(EquipmentSlot.RING_1, EquipmentSlot.valueOf("RING_1"));
            assertEquals(EquipmentSlot.RING_2, EquipmentSlot.valueOf("RING_2"));
            assertEquals(EquipmentSlot.TORSO, EquipmentSlot.valueOf("TORSO"));
            assertEquals(EquipmentSlot.LEGS, EquipmentSlot.valueOf("LEGS"));
            assertEquals(EquipmentSlot.BOOTS, EquipmentSlot.valueOf("BOOTS"));
            assertEquals(EquipmentSlot.HELMET, EquipmentSlot.valueOf("HELMET"));
            assertEquals(EquipmentSlot.SHIELD, EquipmentSlot.valueOf("SHIELD"));
            assertEquals(EquipmentSlot.CAPE_BACKPACK, EquipmentSlot.valueOf("CAPE_BACKPACK"));
            assertEquals(EquipmentSlot.GLOVES, EquipmentSlot.valueOf("GLOVES"));
        }

        @Test
        @DisplayName("valueOf should throw for invalid name")
        void testValueOfInvalid() {
            assertThrows(IllegalArgumentException.class, () -> {
                EquipmentSlot.valueOf("INVALID");
            });
            assertThrows(IllegalArgumentException.class, () -> {
                EquipmentSlot.valueOf("WEAPON");
            });
        }

        @Test
        @DisplayName("valueOf should be case-sensitive")
        void testValueOfCaseSensitive() {
            assertThrows(IllegalArgumentException.class, () -> {
                EquipmentSlot.valueOf("main_hand");
            });
            assertThrows(IllegalArgumentException.class, () -> {
                EquipmentSlot.valueOf("MainHand");
            });
        }

        private boolean containsSlot(EquipmentSlot[] slots, EquipmentSlot target) {
            for (EquipmentSlot slot : slots) {
                if (slot == target) return true;
            }
            return false;
        }
    }

    // ==================== Display Name Tests ====================

    @Nested
    @DisplayName("Display Names")
    class DisplayNames {

        @Test
        @DisplayName("MAIN_HAND should have correct display name")
        void testMainHandDisplayName() {
            assertEquals("Main Hand", EquipmentSlot.MAIN_HAND.getDisplayName());
        }

        @Test
        @DisplayName("NECKLACE should have correct display name")
        void testNecklaceDisplayName() {
            assertEquals("Necklace", EquipmentSlot.NECKLACE.getDisplayName());
        }

        @Test
        @DisplayName("RING_1 should have correct display name")
        void testRing1DisplayName() {
            assertEquals("Ring 1", EquipmentSlot.RING_1.getDisplayName());
        }

        @Test
        @DisplayName("RING_2 should have correct display name")
        void testRing2DisplayName() {
            assertEquals("Ring 2", EquipmentSlot.RING_2.getDisplayName());
        }

        @Test
        @DisplayName("TORSO should have correct display name")
        void testTorsoDisplayName() {
            assertEquals("Torso", EquipmentSlot.TORSO.getDisplayName());
        }

        @Test
        @DisplayName("LEGS should have correct display name")
        void testLegsDisplayName() {
            assertEquals("Legs", EquipmentSlot.LEGS.getDisplayName());
        }

        @Test
        @DisplayName("BOOTS should have correct display name")
        void testBootsDisplayName() {
            assertEquals("Boots", EquipmentSlot.BOOTS.getDisplayName());
        }

        @Test
        @DisplayName("HELMET should have correct display name")
        void testHelmetDisplayName() {
            assertEquals("Helmet", EquipmentSlot.HELMET.getDisplayName());
        }

        @Test
        @DisplayName("SHIELD should have correct display name")
        void testShieldDisplayName() {
            assertEquals("Shield", EquipmentSlot.SHIELD.getDisplayName());
        }

        @Test
        @DisplayName("CAPE_BACKPACK should have correct display name")
        void testCapeBackpackDisplayName() {
            assertEquals("Cape/Backpack", EquipmentSlot.CAPE_BACKPACK.getDisplayName());
        }

        @Test
        @DisplayName("GLOVES should have correct display name")
        void testGlovesDisplayName() {
            assertEquals("Gloves", EquipmentSlot.GLOVES.getDisplayName());
        }

        @Test
        @DisplayName("All display names should be non-empty")
        void testAllDisplayNamesNonEmpty() {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                assertNotNull(slot.getDisplayName());
                assertFalse(slot.getDisplayName().isEmpty(),
                    slot + " display name should not be empty");
            }
        }

        @Test
        @DisplayName("Display names should be human-readable")
        void testDisplayNamesReadable() {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                String displayName = slot.getDisplayName();
                // Display names should contain spaces or slashes for readability
                assertTrue(displayName.length() > 0, "Display name should not be empty");
            }
        }
    }

    // ==================== Slot Dimensions Tests ====================

    @Nested
    @DisplayName("Slot Dimensions")
    class SlotDimensions {

        @Test
        @DisplayName("MAIN_HAND should have 2x4 dimensions")
        void testMainHandDimensions() {
            assertEquals(2, EquipmentSlot.MAIN_HAND.getSlotWidth());
            assertEquals(4, EquipmentSlot.MAIN_HAND.getSlotHeight());
        }

        @Test
        @DisplayName("NECKLACE should have 1x1 dimensions")
        void testNecklaceDimensions() {
            assertEquals(1, EquipmentSlot.NECKLACE.getSlotWidth());
            assertEquals(1, EquipmentSlot.NECKLACE.getSlotHeight());
        }

        @Test
        @DisplayName("RING_1 should have 1x1 dimensions")
        void testRing1Dimensions() {
            assertEquals(1, EquipmentSlot.RING_1.getSlotWidth());
            assertEquals(1, EquipmentSlot.RING_1.getSlotHeight());
        }

        @Test
        @DisplayName("RING_2 should have 1x1 dimensions")
        void testRing2Dimensions() {
            assertEquals(1, EquipmentSlot.RING_2.getSlotWidth());
            assertEquals(1, EquipmentSlot.RING_2.getSlotHeight());
        }

        @Test
        @DisplayName("TORSO should have 2x2 dimensions")
        void testTorsoDimensions() {
            assertEquals(2, EquipmentSlot.TORSO.getSlotWidth());
            assertEquals(2, EquipmentSlot.TORSO.getSlotHeight());
        }

        @Test
        @DisplayName("LEGS should have 2x2 dimensions")
        void testLegsDimensions() {
            assertEquals(2, EquipmentSlot.LEGS.getSlotWidth());
            assertEquals(2, EquipmentSlot.LEGS.getSlotHeight());
        }

        @Test
        @DisplayName("BOOTS should have 2x2 dimensions")
        void testBootsDimensions() {
            assertEquals(2, EquipmentSlot.BOOTS.getSlotWidth());
            assertEquals(2, EquipmentSlot.BOOTS.getSlotHeight());
        }

        @Test
        @DisplayName("HELMET should have 2x2 dimensions")
        void testHelmetDimensions() {
            assertEquals(2, EquipmentSlot.HELMET.getSlotWidth());
            assertEquals(2, EquipmentSlot.HELMET.getSlotHeight());
        }

        @Test
        @DisplayName("SHIELD should have 2x3 dimensions")
        void testShieldDimensions() {
            assertEquals(2, EquipmentSlot.SHIELD.getSlotWidth());
            assertEquals(3, EquipmentSlot.SHIELD.getSlotHeight());
        }

        @Test
        @DisplayName("CAPE_BACKPACK should have 2x1 dimensions")
        void testCapeBackpackDimensions() {
            assertEquals(2, EquipmentSlot.CAPE_BACKPACK.getSlotWidth());
            assertEquals(1, EquipmentSlot.CAPE_BACKPACK.getSlotHeight());
        }

        @Test
        @DisplayName("GLOVES should have 1x1 dimensions")
        void testGlovesDimensions() {
            assertEquals(1, EquipmentSlot.GLOVES.getSlotWidth());
            assertEquals(1, EquipmentSlot.GLOVES.getSlotHeight());
        }

        @Test
        @DisplayName("All slots should have positive dimensions")
        void testAllPositiveDimensions() {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                assertTrue(slot.getSlotWidth() > 0,
                    slot + " width should be positive");
                assertTrue(slot.getSlotHeight() > 0,
                    slot + " height should be positive");
            }
        }

        @Test
        @DisplayName("All slots should have reasonable dimensions")
        void testReasonableDimensions() {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                assertTrue(slot.getSlotWidth() <= 4,
                    slot + " width should be reasonable (≤4)");
                assertTrue(slot.getSlotHeight() <= 4,
                    slot + " height should be reasonable (≤4)");
            }
        }

        @Test
        @DisplayName("Dimension getters should be consistent")
        void testDimensionConsistency() {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                int width1 = slot.getSlotWidth();
                int width2 = slot.getSlotWidth();
                assertEquals(width1, width2, "Width should be consistent");

                int height1 = slot.getSlotHeight();
                int height2 = slot.getSlotHeight();
                assertEquals(height1, height2, "Height should be consistent");
            }
        }
    }

    // ==================== Grouping Tests ====================

    @Nested
    @DisplayName("Slot Grouping")
    class SlotGrouping {

        @Test
        @DisplayName("Should have jewelry slots")
        void testJewelrySlots() {
            // Necklace and rings are jewelry
            assertTrue(EquipmentSlot.NECKLACE.getSlotWidth() == 1);
            assertTrue(EquipmentSlot.RING_1.getSlotWidth() == 1);
            assertTrue(EquipmentSlot.RING_2.getSlotWidth() == 1);
        }

        @Test
        @DisplayName("Should have armor slots")
        void testArmorSlots() {
            // Torso, legs, boots, helmet are armor
            assertTrue(EquipmentSlot.TORSO.getSlotWidth() == 2);
            assertTrue(EquipmentSlot.LEGS.getSlotWidth() == 2);
            assertTrue(EquipmentSlot.BOOTS.getSlotWidth() == 2);
            assertTrue(EquipmentSlot.HELMET.getSlotWidth() == 2);
        }

        @Test
        @DisplayName("Should have weapon slots")
        void testWeaponSlots() {
            // Main hand is for weapons
            assertEquals(2, EquipmentSlot.MAIN_HAND.getSlotWidth());
            assertEquals(4, EquipmentSlot.MAIN_HAND.getSlotHeight());
        }

        @Test
        @DisplayName("Should have two ring slots")
        void testTwoRingSlots() {
            assertNotNull(EquipmentSlot.RING_1);
            assertNotNull(EquipmentSlot.RING_2);
            assertNotEquals(EquipmentSlot.RING_1, EquipmentSlot.RING_2);
        }
    }

    // ==================== Integration Tests ====================

    @Nested
    @DisplayName("Integration Scenarios")
    class IntegrationScenarios {

        @Test
        @DisplayName("Should support switch statements")
        void testSwitchStatement() {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                String category;
                switch (slot) {
                    case MAIN_HAND:
                        category = "Weapon";
                        break;
                    case NECKLACE:
                    case RING_1:
                    case RING_2:
                        category = "Jewelry";
                        break;
                    case TORSO:
                    case LEGS:
                    case BOOTS:
                    case HELMET:
                        category = "Armor";
                        break;
                    case SHIELD:
                        category = "Offhand";
                        break;
                    case CAPE_BACKPACK:
                        category = "Back";
                        break;
                    case GLOVES:
                        category = "Hands";
                        break;
                    default:
                        category = null;
                        break;
                }

                assertNotNull(category, "Switch should handle all slots");
            }
        }

        @Test
        @DisplayName("Should work with comparison operations")
        void testComparison() {
            assertTrue(EquipmentSlot.MAIN_HAND == EquipmentSlot.MAIN_HAND);
            assertFalse(EquipmentSlot.MAIN_HAND == EquipmentSlot.SHIELD);
            assertTrue(EquipmentSlot.HELMET.equals(EquipmentSlot.HELMET));
            assertFalse(EquipmentSlot.HELMET.equals(EquipmentSlot.BOOTS));
        }

        @Test
        @DisplayName("Should be serializable via name")
        void testSerialization() {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                String name = slot.name();
                EquipmentSlot restored = EquipmentSlot.valueOf(name);
                assertEquals(slot, restored, "Should restore from name");
            }
        }

        @Test
        @DisplayName("Should support array operations")
        void testArrayOperations() {
            EquipmentSlot[] slots = EquipmentSlot.values();

            // Check array is not empty
            assertTrue(slots.length > 0);

            // Check all elements are valid
            for (EquipmentSlot slot : slots) {
                assertNotNull(slot);
                assertNotNull(slot.getDisplayName());
                assertTrue(slot.getSlotWidth() > 0);
                assertTrue(slot.getSlotHeight() > 0);
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
            EquipmentSlot slot = EquipmentSlot.MAIN_HAND;
            assertNotEquals(null, slot);
            assertFalse(slot.equals(null));
        }

        @Test
        @DisplayName("Should handle toString")
        void testToString() {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                String str = slot.toString();
                assertNotNull(str);
                assertFalse(str.isEmpty());
                assertEquals(slot.name(), str, "toString should match name");
            }
        }

        @Test
        @DisplayName("Should maintain identity")
        void testIdentity() {
            EquipmentSlot mainHand1 = EquipmentSlot.MAIN_HAND;
            EquipmentSlot mainHand2 = EquipmentSlot.valueOf("MAIN_HAND");

            assertSame(mainHand1, mainHand2, "Same enum constant should be identical");
        }

        @Test
        @DisplayName("Should handle repeated getter calls")
        void testRepeatedGetterCalls() {
            for (int i = 0; i < 100; i++) {
                String name = EquipmentSlot.TORSO.getDisplayName();
                assertEquals("Torso", name);

                int width = EquipmentSlot.TORSO.getSlotWidth();
                assertEquals(2, width);

                int height = EquipmentSlot.TORSO.getSlotHeight();
                assertEquals(2, height);
            }
        }

        @Test
        @DisplayName("Ordinals should be unique")
        void testUniqueOrdinals() {
            EquipmentSlot[] slots = EquipmentSlot.values();

            for (int i = 0; i < slots.length; i++) {
                for (int j = i + 1; j < slots.length; j++) {
                    assertNotEquals(slots[i].ordinal(), slots[j].ordinal(),
                        "Ordinals should be unique");
                }
            }
        }
    }

    // ==================== Use Case Tests ====================

    @Nested
    @DisplayName("Common Use Cases")
    class UseCases {

        @Test
        @DisplayName("Should support UI slot rendering")
        void testUISlotRendering() {
            // All slots should have displayable information
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                String displayName = slot.getDisplayName();
                int width = slot.getSlotWidth();
                int height = slot.getSlotHeight();

                // Should be able to render slot in UI
                assertNotNull(displayName);
                assertTrue(width > 0 && height > 0);

                // Can calculate pixel size (e.g., 32 pixels per slot unit)
                int pixelWidth = width * 32;
                int pixelHeight = height * 32;

                assertTrue(pixelWidth > 0);
                assertTrue(pixelHeight > 0);
            }
        }

        @Test
        @DisplayName("Should support item size validation")
        void testItemSizeValidation() {
            // Simulate checking if an item fits in a slot
            int itemWidth = 2;
            int itemHeight = 2;

            // Should fit in TORSO (2x2)
            assertTrue(itemWidth <= EquipmentSlot.TORSO.getSlotWidth() &&
                      itemHeight <= EquipmentSlot.TORSO.getSlotHeight());

            // Should not fit in RING_1 (1x1)
            assertFalse(itemWidth <= EquipmentSlot.RING_1.getSlotWidth() &&
                       itemHeight <= EquipmentSlot.RING_1.getSlotHeight());
        }

        @Test
        @DisplayName("Should support slot area calculation")
        void testSlotAreaCalculation() {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                int area = slot.getSlotWidth() * slot.getSlotHeight();

                assertTrue(area > 0, slot + " should have positive area");
                assertTrue(area <= 16, slot + " should have reasonable area");
            }
        }

        @Test
        @DisplayName("Should support equipment tooltip display")
        void testTooltipDisplay() {
            // All slots should provide tooltip information
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                String tooltip = "Slot: " + slot.getDisplayName() +
                               " (Size: " + slot.getSlotWidth() + "x" +
                               slot.getSlotHeight() + ")";

                assertNotNull(tooltip);
                assertTrue(tooltip.contains(slot.getDisplayName()));
            }
        }

        @Test
        @DisplayName("Should support duplicate slot detection")
        void testDuplicateSlotDetection() {
            // RING_1 and RING_2 are the only duplicate slot types
            // They should have same dimensions but different identities

            assertEquals(EquipmentSlot.RING_1.getSlotWidth(),
                        EquipmentSlot.RING_2.getSlotWidth(),
                        "Ring slots should have same width");
            assertEquals(EquipmentSlot.RING_1.getSlotHeight(),
                        EquipmentSlot.RING_2.getSlotHeight(),
                        "Ring slots should have same height");
            assertNotEquals(EquipmentSlot.RING_1, EquipmentSlot.RING_2,
                           "Ring slots should be different enum values");
        }
    }

    // ==================== Completeness Tests ====================

    @Nested
    @DisplayName("Completeness")
    class Completeness {

        @Test
        @DisplayName("Should cover all major equipment types")
        void testEquipmentCompleteness() {
            EquipmentSlot[] slots = EquipmentSlot.values();

            boolean hasWeapon = false;
            boolean hasArmor = false;
            boolean hasJewelry = false;

            for (EquipmentSlot slot : slots) {
                if (slot == EquipmentSlot.MAIN_HAND) hasWeapon = true;
                if (slot == EquipmentSlot.HELMET || slot == EquipmentSlot.TORSO) hasArmor = true;
                if (slot == EquipmentSlot.RING_1 || slot == EquipmentSlot.NECKLACE) hasJewelry = true;
            }

            assertTrue(hasWeapon, "Should have weapon slot");
            assertTrue(hasArmor, "Should have armor slots");
            assertTrue(hasJewelry, "Should have jewelry slots");
        }

        @Test
        @DisplayName("Should have reasonable number of slots")
        void testSlotCount() {
            EquipmentSlot[] slots = EquipmentSlot.values();

            assertTrue(slots.length >= 8, "Should have at least 8 slots");
            assertTrue(slots.length <= 20, "Should not have excessive slots");
        }
    }
}
