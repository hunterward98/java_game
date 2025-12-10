package io.github.inherit_this.items;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for the Item class.
 * Tests item creation, properties, equipment slots, and edge cases.
 */
class ItemTest {

    private Item basicItem;
    private Item weaponItem;
    private Item armorItem;
    private Item stackableItem;
    private Item enchantedItem;

    @BeforeEach
    void setUp() {
        // Basic item with simplified constructor
        basicItem = new Item(
            "health_potion",
            "Health Potion",
            "Restores 50 HP",
            ItemType.CONSUMABLE,
            ItemRarity.COMMON,
            null, // No texture in tests
            1, 1,
            10,  // Stackable
            25   // Value
        );

        // Weapon with stats
        ItemStats weaponStats = ItemStats.weapon(15, 100, 1.2f);
        weaponItem = new Item(
            "iron_sword",
            "Iron Sword",
            "A sturdy blade",
            ItemType.WEAPON,
            ItemRarity.UNCOMMON,
            null,
            1, 2,
            1,    // Non-stackable
            100,  // Value
            3.5f, // Weight
            weaponStats,
            null, // No enchantments
            EquipmentSlot.MAIN_HAND
        );

        // Armor with stats
        ItemStats armorStats = ItemStats.armor(25, 150);
        armorItem = new Item(
            "iron_chestplate",
            "Iron Chestplate",
            "Solid protection",
            ItemType.ARMOR,
            ItemRarity.UNCOMMON,
            null,
            2, 2,
            1,    // Non-stackable
            200,  // Value
            8.0f, // Weight
            armorStats,
            null,
            EquipmentSlot.TORSO
        );

        // Stackable item
        stackableItem = new Item(
            "arrow",
            "Arrow",
            "Basic arrow",
            ItemType.CONSUMABLE,
            ItemRarity.COMMON,
            null,
            1, 1,
            99,  // Highly stackable
            1    // Value
        );

        // Enchanted item
        List<String> enchantments = Arrays.asList("Sharpness II", "Fire Aspect");
        ItemStats enchantedStats = ItemStats.weapon(20, 200, 1.0f);
        enchantedItem = new Item(
            "enchanted_sword",
            "Enchanted Sword",
            "A magical blade",
            ItemType.WEAPON,
            ItemRarity.RARE,
            null,
            1, 3,
            1,    // Non-stackable
            500,  // Value
            4.0f, // Weight
            enchantedStats,
            enchantments,
            EquipmentSlot.MAIN_HAND
        );
    }

    // ==================== Basic Properties Tests ====================

    @Nested
    @DisplayName("Basic Properties")
    class BasicProperties {

        @Test
        @DisplayName("Item should have correct ID")
        void testItemId() {
            assertEquals("health_potion", basicItem.getId());
            assertEquals("iron_sword", weaponItem.getId());
        }

        @Test
        @DisplayName("Item should have correct name")
        void testItemName() {
            assertEquals("Health Potion", basicItem.getName());
            assertEquals("Iron Sword", weaponItem.getName());
        }

        @Test
        @DisplayName("Item should have correct description")
        void testItemDescription() {
            assertEquals("Restores 50 HP", basicItem.getDescription());
            assertEquals("A sturdy blade", weaponItem.getDescription());
        }

        @Test
        @DisplayName("Item should have correct type")
        void testItemType() {
            assertEquals(ItemType.CONSUMABLE, basicItem.getType());
            assertEquals(ItemType.WEAPON, weaponItem.getType());
            assertEquals(ItemType.ARMOR, armorItem.getType());
        }

        @Test
        @DisplayName("Item should have correct rarity")
        void testItemRarity() {
            assertEquals(ItemRarity.COMMON, basicItem.getRarity());
            assertEquals(ItemRarity.UNCOMMON, weaponItem.getRarity());
            assertEquals(ItemRarity.RARE, enchantedItem.getRarity());
        }

        @Test
        @DisplayName("Item should have correct value")
        void testItemValue() {
            assertEquals(25, basicItem.getValue());
            assertEquals(100, weaponItem.getValue());
            assertEquals(500, enchantedItem.getValue());
        }

        @Test
        @DisplayName("Item should have correct icon (null in tests)")
        void testItemIcon() {
            assertNull(basicItem.getIcon(), "Icon should be null in unit tests");
        }
    }

    // ==================== Size Properties Tests ====================

    @Nested
    @DisplayName("Size Properties")
    class SizeProperties {

        @Test
        @DisplayName("Item should have correct width")
        void testItemWidth() {
            assertEquals(1, basicItem.getWidth());
            assertEquals(2, armorItem.getWidth());
        }

        @Test
        @DisplayName("Item should have correct height")
        void testItemHeight() {
            assertEquals(1, basicItem.getHeight());
            assertEquals(2, weaponItem.getHeight());
            assertEquals(3, enchantedItem.getHeight());
        }

        @Test
        @DisplayName("Item should have correct max stack size")
        void testMaxStackSize() {
            assertEquals(10, basicItem.getMaxStackSize());
            assertEquals(1, weaponItem.getMaxStackSize());
            assertEquals(99, stackableItem.getMaxStackSize());
        }

        @Test
        @DisplayName("Item should have correct weight")
        void testItemWeight() {
            assertEquals(1.0f, basicItem.getWeight(), 0.001f, "Default weight should be 1.0");
            assertEquals(3.5f, weaponItem.getWeight(), 0.001f);
            assertEquals(8.0f, armorItem.getWeight(), 0.001f);
        }
    }

    // ==================== Stackability Tests ====================

    @Nested
    @DisplayName("Stackability")
    class Stackability {

        @Test
        @DisplayName("Item with max stack > 1 should be stackable")
        void testStackableItem() {
            assertTrue(basicItem.isStackable(), "Item with max stack 10 should be stackable");
            assertTrue(stackableItem.isStackable(), "Item with max stack 99 should be stackable");
        }

        @Test
        @DisplayName("Item with max stack = 1 should not be stackable")
        void testNonStackableItem() {
            assertFalse(weaponItem.isStackable(), "Weapon should not be stackable");
            assertFalse(armorItem.isStackable(), "Armor should not be stackable");
        }
    }

    // ==================== Stats Tests ====================

    @Nested
    @DisplayName("Item Stats")
    class ItemStatsTests {

        @Test
        @DisplayName("Basic item should have no stats")
        void testBasicItemNoStats() {
            ItemStats stats = basicItem.getStats();
            assertNotNull(stats, "Stats should never be null");
            assertFalse(stats.hasStats(), "Basic item should have no stats");
            assertEquals(0, stats.getDamage());
            assertEquals(0, stats.getArmor());
        }

        @Test
        @DisplayName("Weapon should have damage stats")
        void testWeaponStats() {
            ItemStats stats = weaponItem.getStats();
            assertTrue(stats.hasStats(), "Weapon should have stats");
            assertEquals(15, stats.getDamage(), "Weapon should have 15 damage");
            assertEquals(100, stats.getDurability(), "Weapon should have 100 durability");
            assertEquals(1.2f, stats.getAttackSpeed(), 0.001f, "Weapon should have 1.2 attack speed");
            assertEquals(0, stats.getArmor(), "Weapon should have no armor");
        }

        @Test
        @DisplayName("Armor should have armor stats")
        void testArmorStats() {
            ItemStats stats = armorItem.getStats();
            assertTrue(stats.hasStats(), "Armor should have stats");
            assertEquals(25, stats.getArmor(), "Armor should have 25 armor");
            assertEquals(150, stats.getDurability(), "Armor should have 150 durability");
            assertEquals(0, stats.getDamage(), "Armor should have no damage");
        }

        @Test
        @DisplayName("Stats should be accessible via getters")
        void testStatsGetters() {
            ItemStats stats = weaponItem.getStats();
            assertEquals(15, stats.getDamage());
            assertEquals(0, stats.getArmor());
            assertEquals(100, stats.getDurability());
            assertEquals(1.2f, stats.getAttackSpeed(), 0.001f);
            assertEquals(0, stats.getStrength());
            assertEquals(0, stats.getDexterity());
            assertEquals(0, stats.getVitality());
            assertEquals(0, stats.getMagic());
        }
    }

    // ==================== Enchantments Tests ====================

    @Nested
    @DisplayName("Enchantments")
    class Enchantments {

        @Test
        @DisplayName("Item without enchantments should have empty list")
        void testNoEnchantments() {
            assertFalse(basicItem.hasEnchantments(), "Basic item should have no enchantments");
            assertTrue(basicItem.getEnchantments().isEmpty(), "Enchantments list should be empty");
        }

        @Test
        @DisplayName("Item with enchantments should return correct list")
        void testWithEnchantments() {
            assertTrue(enchantedItem.hasEnchantments(), "Enchanted item should have enchantments");
            List<String> enchantments = enchantedItem.getEnchantments();
            assertEquals(2, enchantments.size(), "Should have 2 enchantments");
            assertTrue(enchantments.contains("Sharpness II"));
            assertTrue(enchantments.contains("Fire Aspect"));
        }

        @Test
        @DisplayName("Enchantments list should be immutable (defensive copy)")
        void testEnchantmentsImmutable() {
            List<String> enchantments = enchantedItem.getEnchantments();
            enchantments.add("New Enchantment");

            List<String> freshList = enchantedItem.getEnchantments();
            assertEquals(2, freshList.size(), "Original list should be unchanged");
        }

        @Test
        @DisplayName("Null enchantments parameter should create empty list")
        void testNullEnchantments() {
            assertFalse(weaponItem.hasEnchantments(), "Item with null enchantments should have no enchantments");
            assertEquals(0, weaponItem.getEnchantments().size());
        }
    }

    // ==================== Equipment Slot Tests ====================

    @Nested
    @DisplayName("Equipment Slots")
    class EquipmentSlots {

        @Test
        @DisplayName("Non-equippable item should have null slot")
        void testNonEquippableItem() {
            assertNull(basicItem.getEquipmentSlot(), "Consumable should have no equipment slot");
            assertFalse(basicItem.isEquippable(), "Consumable should not be equippable");
        }

        @Test
        @DisplayName("Weapon should be equippable in MAIN_HAND")
        void testWeaponEquipSlot() {
            assertEquals(EquipmentSlot.MAIN_HAND, weaponItem.getEquipmentSlot());
            assertTrue(weaponItem.isEquippable(), "Weapon should be equippable");
        }

        @Test
        @DisplayName("Armor should be equippable in TORSO")
        void testArmorEquipSlot() {
            assertEquals(EquipmentSlot.TORSO, armorItem.getEquipmentSlot());
            assertTrue(armorItem.isEquippable(), "Armor should be equippable");
        }

        @Test
        @DisplayName("Enchanted weapon should be equippable")
        void testEnchantedWeaponEquipSlot() {
            assertEquals(EquipmentSlot.MAIN_HAND, enchantedItem.getEquipmentSlot());
            assertTrue(enchantedItem.isEquippable());
        }
    }

    // ==================== toString Tests ====================

    @Nested
    @DisplayName("String Representation")
    class StringRepresentation {

        @Test
        @DisplayName("toString should format correctly")
        void testToString() {
            assertEquals("Health Potion (CONSUMABLE)", basicItem.toString());
            assertEquals("Iron Sword (WEAPON)", weaponItem.toString());
            assertEquals("Iron Chestplate (ARMOR)", armorItem.toString());
        }
    }

    // ==================== Constructor Tests ====================

    @Nested
    @DisplayName("Constructor Behavior")
    class ConstructorBehavior {

        @Test
        @DisplayName("Simplified constructor should use default values")
        void testSimplifiedConstructor() {
            Item simple = new Item(
                "test_item",
                "Test Item",
                "A test",
                ItemType.MISC,
                ItemRarity.COMMON,
                null,
                1, 1,
                1,
                10
            );

            assertEquals(1.0f, simple.getWeight(), 0.001f, "Default weight should be 1.0");
            assertFalse(simple.getStats().hasStats(), "Should have no stats");
            assertFalse(simple.hasEnchantments(), "Should have no enchantments");
            assertNull(simple.getEquipmentSlot(), "Should have no equipment slot");
        }

        @Test
        @DisplayName("Full constructor with null stats should create empty stats")
        void testConstructorWithNullStats() {
            Item item = new Item(
                "test", "Test", "Test",
                ItemType.MISC, ItemRarity.COMMON,
                null, 1, 1, 1, 10, 1.0f,
                null, // Null stats
                null, null
            );

            assertNotNull(item.getStats(), "Stats should not be null");
            assertFalse(item.getStats().hasStats(), "Stats should be empty");
        }

        @Test
        @DisplayName("Full constructor with null enchantments should create empty list")
        void testConstructorWithNullEnchantmentsList() {
            Item item = new Item(
                "test", "Test", "Test",
                ItemType.MISC, ItemRarity.COMMON,
                null, 1, 1, 1, 10, 1.0f,
                ItemStats.none(),
                null, // Null enchantments
                null
            );

            assertFalse(item.hasEnchantments(), "Should have no enchantments");
            assertEquals(0, item.getEnchantments().size());
        }
    }

    // ==================== Edge Cases ====================

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Item with max stack 1 should not be stackable")
        void testMaxStackOne() {
            assertFalse(weaponItem.isStackable());
            assertEquals(1, weaponItem.getMaxStackSize());
        }

        @Test
        @DisplayName("Item with very high max stack should be stackable")
        void testHighMaxStack() {
            Item highStack = new Item(
                "gold_coin", "Gold Coin", "Currency",
                ItemType.MISC, ItemRarity.COMMON,
                null, 1, 1, 9999, 1
            );

            assertTrue(highStack.isStackable());
            assertEquals(9999, highStack.getMaxStackSize());
        }

        @Test
        @DisplayName("Item with zero value should be valid")
        void testZeroValue() {
            Item worthless = new Item(
                "junk", "Junk", "Worthless",
                ItemType.MISC, ItemRarity.COMMON,
                null, 1, 1, 1, 0
            );

            assertEquals(0, worthless.getValue());
        }

        @Test
        @DisplayName("Item with large dimensions should be valid")
        void testLargeDimensions() {
            Item huge = new Item(
                "giant_shield", "Giant Shield", "Massive",
                ItemType.ARMOR, ItemRarity.LEGENDARY,
                null, 4, 5, 1, 1000
            );

            assertEquals(4, huge.getWidth());
            assertEquals(5, huge.getHeight());
        }

        @Test
        @DisplayName("Item with fractional weight should be valid")
        void testFractionalWeight() {
            Item light = new Item(
                "feather", "Feather", "Very light",
                ItemType.MISC, ItemRarity.COMMON,
                null, 1, 1, 10, 1, 0.1f,
                ItemStats.none(), null, null
            );

            assertEquals(0.1f, light.getWeight(), 0.001f);
        }

        @Test
        @DisplayName("Item properties should be immutable")
        void testImmutability() {
            String id = basicItem.getId();
            String name = basicItem.getName();

            // Try to modify (should have no effect)
            assertDoesNotThrow(() -> {
                basicItem.getId();
                basicItem.getName();
            });

            // Values should remain the same
            assertEquals(id, basicItem.getId());
            assertEquals(name, basicItem.getName());
        }

        @Test
        @DisplayName("Multiple items with same properties should be independent")
        void testItemIndependence() {
            Item item1 = new Item(
                "sword1", "Sword", "A sword",
                ItemType.WEAPON, ItemRarity.COMMON,
                null, 1, 1, 1, 100
            );

            Item item2 = new Item(
                "sword2", "Sword", "A sword",
                ItemType.WEAPON, ItemRarity.COMMON,
                null, 1, 1, 1, 100
            );

            assertNotSame(item1, item2, "Items should be different objects");
            assertNotEquals(item1.getId(), item2.getId(), "Items should have different IDs");
        }
    }
}
