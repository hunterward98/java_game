package io.github.inherit_this.items;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Equipment class - equipping items, slot management, and stat calculations.
 */
class EquipmentTest {

    private Equipment equipment;
    private Item ironSword;
    private Item steelSword;
    private Item ironHelmet;
    private Item leatherArmor;
    private Item ironBoots;
    private Item goldRing;
    private Item magicRing;

    @BeforeEach
    void setUp() {
        equipment = new Equipment();

        // Create weapons
        ironSword = new Item(
            "iron_sword",
            "Iron Sword",
            "A basic iron sword",
            ItemType.WEAPON,
            ItemRarity.COMMON,
            null,
            1, 2,
            1,
            100,
            3.5f,
            ItemStats.weapon(15, 100, 1.2f),
            null,
            EquipmentSlot.MAIN_HAND
        );

        steelSword = new Item(
            "steel_sword",
            "Steel Sword",
            "A sturdy steel sword",
            ItemType.WEAPON,
            ItemRarity.UNCOMMON,
            null,
            1, 2,
            1,
            250,
            4.0f,
            ItemStats.weapon(25, 150, 1.0f),
            null,
            EquipmentSlot.MAIN_HAND
        );

        // Create armor pieces
        ironHelmet = new Item(
            "iron_helmet",
            "Iron Helmet",
            "Protects your head",
            ItemType.ARMOR,
            ItemRarity.COMMON,
            null,
            2, 2,
            1,
            75,
            2.0f,
            ItemStats.armor(8, 80),
            null,
            EquipmentSlot.HELMET
        );

        leatherArmor = new Item(
            "leather_armor",
            "Leather Armor",
            "Light chest protection",
            ItemType.ARMOR,
            ItemRarity.COMMON,
            null,
            2, 2,
            1,
            50,
            1.5f,
            ItemStats.armor(5, 60),
            null,
            EquipmentSlot.TORSO
        );

        ironBoots = new Item(
            "iron_boots",
            "Iron Boots",
            "Heavy boots",
            ItemType.ARMOR,
            ItemRarity.COMMON,
            null,
            2, 2,
            1,
            40,
            2.5f,
            ItemStats.armor(3, 50),
            null,
            EquipmentSlot.BOOTS
        );

        // Create accessories
        goldRing = new Item(
            "gold_ring",
            "Gold Ring",
            "A shiny ring",
            ItemType.MISC,
            ItemRarity.UNCOMMON,
            null,
            1, 1,
            1,
            200,
            0.1f,
            new ItemStats(0, 0, 0, 0f, 2, 1, 0, 0),
            null,
            EquipmentSlot.RING_1
        );

        magicRing = new Item(
            "magic_ring",
            "Magic Ring",
            "Increases magic power",
            ItemType.MISC,
            ItemRarity.RARE,
            null,
            1, 1,
            1,
            500,
            0.1f,
            new ItemStats(0, 0, 0, 0f, 0, 0, 0, 5),
            null,
            EquipmentSlot.RING_2
        );
    }

    // === Equipping Items Tests ===

    @Test
    @DisplayName("Should equip item to correct slot")
    void testEquipItem() {
        Item previous = equipment.equip(EquipmentSlot.MAIN_HAND, ironSword);

        assertNull(previous, "Should return null when slot was empty");
        assertEquals(ironSword, equipment.getEquipped(EquipmentSlot.MAIN_HAND), "Sword should be equipped");
        assertTrue(equipment.isEquipped(EquipmentSlot.MAIN_HAND), "Slot should be marked as equipped");
    }

    @Test
    @DisplayName("Should replace previously equipped item")
    void testReplaceEquippedItem() {
        equipment.equip(EquipmentSlot.MAIN_HAND, ironSword);
        Item previous = equipment.equip(EquipmentSlot.MAIN_HAND, steelSword);

        assertEquals(ironSword, previous, "Should return previously equipped item");
        assertEquals(steelSword, equipment.getEquipped(EquipmentSlot.MAIN_HAND), "New sword should be equipped");
    }

    @Test
    @DisplayName("Should equip multiple items to different slots")
    void testEquipMultipleItems() {
        equipment.equip(EquipmentSlot.MAIN_HAND, ironSword);
        equipment.equip(EquipmentSlot.HELMET, ironHelmet);
        equipment.equip(EquipmentSlot.TORSO, leatherArmor);

        assertEquals(ironSword, equipment.getEquipped(EquipmentSlot.MAIN_HAND));
        assertEquals(ironHelmet, equipment.getEquipped(EquipmentSlot.HELMET));
        assertEquals(leatherArmor, equipment.getEquipped(EquipmentSlot.TORSO));
    }

    @Test
    @DisplayName("Should equip items to both ring slots independently")
    void testEquipBothRings() {
        equipment.equip(EquipmentSlot.RING_1, goldRing);
        equipment.equip(EquipmentSlot.RING_2, magicRing);

        assertEquals(goldRing, equipment.getEquipped(EquipmentSlot.RING_1));
        assertEquals(magicRing, equipment.getEquipped(EquipmentSlot.RING_2));
        assertTrue(equipment.isEquipped(EquipmentSlot.RING_1));
        assertTrue(equipment.isEquipped(EquipmentSlot.RING_2));
    }

    @Test
    @DisplayName("Should handle equipping null item as unequip")
    void testEquipNullItem() {
        equipment.equip(EquipmentSlot.MAIN_HAND, ironSword);
        Item previous = equipment.equip(EquipmentSlot.MAIN_HAND, null);

        assertEquals(ironSword, previous, "Should return previously equipped item");
        assertNull(equipment.getEquipped(EquipmentSlot.MAIN_HAND), "Slot should be empty");
        assertFalse(equipment.isEquipped(EquipmentSlot.MAIN_HAND), "Slot should not be equipped");
    }

    // === Unequipping Tests ===

    @Test
    @DisplayName("Should unequip item from slot")
    void testUnequipItem() {
        equipment.equip(EquipmentSlot.HELMET, ironHelmet);
        Item unequipped = equipment.unequip(EquipmentSlot.HELMET);

        assertEquals(ironHelmet, unequipped, "Should return unequipped item");
        assertNull(equipment.getEquipped(EquipmentSlot.HELMET), "Slot should be empty");
        assertFalse(equipment.isEquipped(EquipmentSlot.HELMET), "Slot should not be equipped");
    }

    @Test
    @DisplayName("Should return null when unequipping empty slot")
    void testUnequipEmptySlot() {
        Item unequipped = equipment.unequip(EquipmentSlot.MAIN_HAND);

        assertNull(unequipped, "Should return null for empty slot");
        assertFalse(equipment.isEquipped(EquipmentSlot.MAIN_HAND), "Slot should remain empty");
    }

    @Test
    @DisplayName("Should unequip without affecting other slots")
    void testUnequipPreservesOtherSlots() {
        equipment.equip(EquipmentSlot.MAIN_HAND, ironSword);
        equipment.equip(EquipmentSlot.HELMET, ironHelmet);
        equipment.equip(EquipmentSlot.TORSO, leatherArmor);

        equipment.unequip(EquipmentSlot.HELMET);

        assertEquals(ironSword, equipment.getEquipped(EquipmentSlot.MAIN_HAND), "Weapon should remain");
        assertEquals(leatherArmor, equipment.getEquipped(EquipmentSlot.TORSO), "Armor should remain");
        assertNull(equipment.getEquipped(EquipmentSlot.HELMET), "Helmet should be removed");
    }

    // === Slot Validation Tests ===

    @Test
    @DisplayName("Should return null for empty slot")
    void testGetEquippedFromEmptySlot() {
        Item item = equipment.getEquipped(EquipmentSlot.SHIELD);

        assertNull(item, "Empty slot should return null");
    }

    @Test
    @DisplayName("isEquipped should return false for empty slot")
    void testIsEquippedForEmptySlot() {
        assertFalse(equipment.isEquipped(EquipmentSlot.MAIN_HAND), "Empty slot should return false");
    }

    @Test
    @DisplayName("isEquipped should return true for occupied slot")
    void testIsEquippedForOccupiedSlot() {
        equipment.equip(EquipmentSlot.BOOTS, ironBoots);

        assertTrue(equipment.isEquipped(EquipmentSlot.BOOTS), "Occupied slot should return true");
    }

    @Test
    @DisplayName("Should handle all equipment slot types")
    void testAllEquipmentSlots() {
        // Verify we can equip to all slots without errors
        equipment.equip(EquipmentSlot.MAIN_HAND, ironSword);
        equipment.equip(EquipmentSlot.NECKLACE, goldRing);
        equipment.equip(EquipmentSlot.RING_1, goldRing);
        equipment.equip(EquipmentSlot.RING_2, magicRing);
        equipment.equip(EquipmentSlot.TORSO, leatherArmor);
        equipment.equip(EquipmentSlot.LEGS, leatherArmor);
        equipment.equip(EquipmentSlot.BOOTS, ironBoots);
        equipment.equip(EquipmentSlot.HELMET, ironHelmet);
        equipment.equip(EquipmentSlot.SHIELD, ironHelmet);
        equipment.equip(EquipmentSlot.CAPE_BACKPACK, leatherArmor);
        equipment.equip(EquipmentSlot.GLOVES, goldRing);

        // All 11 slots from EquipmentSlot enum
        assertEquals(11, EquipmentSlot.values().length, "Should have 11 equipment slots");
    }

    // === getAllEquipped Tests ===

    @Test
    @DisplayName("getAllEquipped should return empty map initially")
    void testGetAllEquippedInitiallyEmpty() {
        Map<EquipmentSlot, Item> allEquipped = equipment.getAllEquipped();

        assertNotNull(allEquipped, "Should not return null");
        assertTrue(allEquipped.isEmpty(), "Should be empty initially");
    }

    @Test
    @DisplayName("getAllEquipped should return all equipped items")
    void testGetAllEquipped() {
        equipment.equip(EquipmentSlot.MAIN_HAND, ironSword);
        equipment.equip(EquipmentSlot.HELMET, ironHelmet);
        equipment.equip(EquipmentSlot.RING_1, goldRing);

        Map<EquipmentSlot, Item> allEquipped = equipment.getAllEquipped();

        assertEquals(3, allEquipped.size(), "Should have 3 equipped items");
        assertEquals(ironSword, allEquipped.get(EquipmentSlot.MAIN_HAND));
        assertEquals(ironHelmet, allEquipped.get(EquipmentSlot.HELMET));
        assertEquals(goldRing, allEquipped.get(EquipmentSlot.RING_1));
    }

    @Test
    @DisplayName("getAllEquipped should return a copy, not internal reference")
    void testGetAllEquippedReturnsCopy() {
        equipment.equip(EquipmentSlot.MAIN_HAND, ironSword);

        Map<EquipmentSlot, Item> allEquipped1 = equipment.getAllEquipped();
        Map<EquipmentSlot, Item> allEquipped2 = equipment.getAllEquipped();

        assertNotSame(allEquipped1, allEquipped2, "Should return different map instances");
        assertEquals(allEquipped1, allEquipped2, "Maps should contain same data");
    }

    @Test
    @DisplayName("Modifying returned map should not affect equipment")
    void testGetAllEquippedImmutability() {
        equipment.equip(EquipmentSlot.MAIN_HAND, ironSword);

        Map<EquipmentSlot, Item> allEquipped = equipment.getAllEquipped();
        allEquipped.clear();

        assertTrue(equipment.isEquipped(EquipmentSlot.MAIN_HAND), "Equipment should not be affected");
        assertEquals(ironSword, equipment.getEquipped(EquipmentSlot.MAIN_HAND), "Item should still be equipped");
    }

    // === Equipment Stats Tests ===

    @Test
    @DisplayName("getTotalStats should return zero stats when nothing equipped")
    void testGetTotalStatsEmpty() {
        ItemStats stats = equipment.getTotalStats();

        assertNotNull(stats, "Should not return null");
        assertEquals(0, stats.getDamage(), "Damage should be 0");
        assertEquals(0, stats.getArmor(), "Armor should be 0");
        assertEquals(0, stats.getDurability(), "Durability should be 0");
        assertEquals(0f, stats.getAttackSpeed(), "Attack speed should be 0");
        assertEquals(0, stats.getStrength(), "Strength should be 0");
        assertEquals(0, stats.getDexterity(), "Dexterity should be 0");
        assertEquals(0, stats.getVitality(), "Vitality should be 0");
        assertEquals(0, stats.getMagic(), "Magic should be 0");
    }

    @Test
    @DisplayName("getTotalStats should sum damage from weapon")
    void testGetTotalStatsWithWeapon() {
        equipment.equip(EquipmentSlot.MAIN_HAND, ironSword);

        ItemStats stats = equipment.getTotalStats();

        assertEquals(15, stats.getDamage(), "Should have weapon damage");
        assertEquals(100, stats.getDurability(), "Should have weapon durability");
        assertEquals(1.2f, stats.getAttackSpeed(), 0.01f, "Should have weapon attack speed");
    }

    @Test
    @DisplayName("getTotalStats should sum armor from all armor pieces")
    void testGetTotalStatsWithArmor() {
        equipment.equip(EquipmentSlot.HELMET, ironHelmet);
        equipment.equip(EquipmentSlot.TORSO, leatherArmor);
        equipment.equip(EquipmentSlot.BOOTS, ironBoots);

        ItemStats stats = equipment.getTotalStats();

        assertEquals(16, stats.getArmor(), "Total armor should be 8+5+3 = 16");
        assertEquals(190, stats.getDurability(), "Total durability should be 80+60+50 = 190");
        assertEquals(0, stats.getDamage(), "Armor should not provide damage");
    }

    @Test
    @DisplayName("getTotalStats should combine weapon and armor")
    void testGetTotalStatsWithWeaponAndArmor() {
        equipment.equip(EquipmentSlot.MAIN_HAND, ironSword);
        equipment.equip(EquipmentSlot.HELMET, ironHelmet);

        ItemStats stats = equipment.getTotalStats();

        assertEquals(15, stats.getDamage(), "Should have weapon damage");
        assertEquals(8, stats.getArmor(), "Should have helmet armor");
        assertEquals(180, stats.getDurability(), "Should sum durability: 100+80");
    }

    @Test
    @DisplayName("getTotalStats should sum attribute bonuses from rings")
    void testGetTotalStatsWithRings() {
        equipment.equip(EquipmentSlot.RING_1, goldRing);
        equipment.equip(EquipmentSlot.RING_2, magicRing);

        ItemStats stats = equipment.getTotalStats();

        assertEquals(2, stats.getStrength(), "Should have strength from gold ring");
        assertEquals(1, stats.getDexterity(), "Should have dexterity from gold ring");
        assertEquals(5, stats.getMagic(), "Should have magic from magic ring");
    }

    @Test
    @DisplayName("getTotalStats should calculate average attack speed from multiple weapons")
    void testGetTotalStatsAverageAttackSpeed() {
        // In this system, only MAIN_HAND has a weapon, but test the averaging logic
        equipment.equip(EquipmentSlot.MAIN_HAND, ironSword);

        ItemStats stats = equipment.getTotalStats();

        assertEquals(1.2f, stats.getAttackSpeed(), 0.01f, "Single weapon should have its attack speed");
    }

    @Test
    @DisplayName("getTotalStats should handle full equipment set")
    void testGetTotalStatsFullSet() {
        equipment.equip(EquipmentSlot.MAIN_HAND, steelSword);
        equipment.equip(EquipmentSlot.HELMET, ironHelmet);
        equipment.equip(EquipmentSlot.TORSO, leatherArmor);
        equipment.equip(EquipmentSlot.BOOTS, ironBoots);
        equipment.equip(EquipmentSlot.RING_1, goldRing);
        equipment.equip(EquipmentSlot.RING_2, magicRing);

        ItemStats stats = equipment.getTotalStats();

        assertEquals(25, stats.getDamage(), "Damage from steel sword");
        assertEquals(16, stats.getArmor(), "Armor from 3 pieces: 8+5+3");
        assertEquals(2, stats.getStrength(), "Strength from gold ring");
        assertEquals(1, stats.getDexterity(), "Dexterity from gold ring");
        assertEquals(5, stats.getMagic(), "Magic from magic ring");
    }

    @Test
    @DisplayName("getTotalStats should handle items with no stats gracefully")
    void testGetTotalStatsWithNoStatsItem() {
        Item noStatsItem = new Item(
            "simple_item",
            "Simple Item",
            "Has no stats",
            ItemType.MISC,
            ItemRarity.COMMON,
            null,
            1, 1,
            1,
            10
        );

        equipment.equip(EquipmentSlot.NECKLACE, noStatsItem);

        ItemStats stats = equipment.getTotalStats();

        // Should not throw exception, just return zero stats
        assertNotNull(stats, "Should handle items with no stats");
        assertEquals(0, stats.getDamage(), "Should be 0");
    }

    // === Weight Tests ===

    @Test
    @DisplayName("getTotalWeight should return 0 when nothing equipped")
    void testGetTotalWeightEmpty() {
        float weight = equipment.getTotalWeight();

        assertEquals(0f, weight, 0.01f, "Empty equipment should have 0 weight");
    }

    @Test
    @DisplayName("getTotalWeight should sum weight of all equipped items")
    void testGetTotalWeight() {
        equipment.equip(EquipmentSlot.MAIN_HAND, ironSword);  // 3.5
        equipment.equip(EquipmentSlot.HELMET, ironHelmet);     // 2.0
        equipment.equip(EquipmentSlot.TORSO, leatherArmor);    // 1.5

        float weight = equipment.getTotalWeight();

        assertEquals(7.0f, weight, 0.01f, "Total weight should be 3.5+2.0+1.5 = 7.0");
    }

    @Test
    @DisplayName("getTotalWeight should update when items are unequipped")
    void testGetTotalWeightAfterUnequip() {
        equipment.equip(EquipmentSlot.MAIN_HAND, ironSword);
        equipment.equip(EquipmentSlot.HELMET, ironHelmet);

        float weightBefore = equipment.getTotalWeight();
        assertEquals(5.5f, weightBefore, 0.01f, "Weight should be 3.5+2.0");

        equipment.unequip(EquipmentSlot.HELMET);

        float weightAfter = equipment.getTotalWeight();
        assertEquals(3.5f, weightAfter, 0.01f, "Weight should be 3.5 after unequipping helmet");
    }

    @Test
    @DisplayName("getTotalWeight should handle light accessories")
    void testGetTotalWeightWithAccessories() {
        equipment.equip(EquipmentSlot.RING_1, goldRing);    // 0.1
        equipment.equip(EquipmentSlot.RING_2, magicRing);   // 0.1

        float weight = equipment.getTotalWeight();

        assertEquals(0.2f, weight, 0.01f, "Total weight should be 0.1+0.1");
    }

    // === Clear All Tests ===

    @Test
    @DisplayName("clearAll should remove all equipped items")
    void testClearAll() {
        equipment.equip(EquipmentSlot.MAIN_HAND, ironSword);
        equipment.equip(EquipmentSlot.HELMET, ironHelmet);
        equipment.equip(EquipmentSlot.RING_1, goldRing);

        equipment.clearAll();

        assertNull(equipment.getEquipped(EquipmentSlot.MAIN_HAND), "Weapon should be cleared");
        assertNull(equipment.getEquipped(EquipmentSlot.HELMET), "Helmet should be cleared");
        assertNull(equipment.getEquipped(EquipmentSlot.RING_1), "Ring should be cleared");
        assertTrue(equipment.getAllEquipped().isEmpty(), "All equipment should be empty");
        assertEquals(0f, equipment.getTotalWeight(), 0.01f, "Weight should be 0");
    }

    @Test
    @DisplayName("clearAll should work on empty equipment")
    void testClearAllWhenEmpty() {
        equipment.clearAll();

        assertTrue(equipment.getAllEquipped().isEmpty(), "Should remain empty");
    }

    @Test
    @DisplayName("clearAll should reset stats to zero")
    void testClearAllResetsStats() {
        equipment.equip(EquipmentSlot.MAIN_HAND, ironSword);
        equipment.equip(EquipmentSlot.HELMET, ironHelmet);

        equipment.clearAll();

        ItemStats stats = equipment.getTotalStats();
        assertEquals(0, stats.getDamage(), "Damage should be 0");
        assertEquals(0, stats.getArmor(), "Armor should be 0");
    }

    // === Edge Cases and Integration Tests ===

    @Test
    @DisplayName("Should handle rapid equip/unequip cycles")
    void testRapidEquipUnequip() {
        for (int i = 0; i < 10; i++) {
            equipment.equip(EquipmentSlot.MAIN_HAND, ironSword);
            equipment.unequip(EquipmentSlot.MAIN_HAND);
        }

        assertNull(equipment.getEquipped(EquipmentSlot.MAIN_HAND), "Should end empty");
        assertEquals(0f, equipment.getTotalWeight(), "Weight should be 0");
    }

    @Test
    @DisplayName("Should handle swapping items between slots")
    void testSwapItemsBetweenSlots() {
        equipment.equip(EquipmentSlot.RING_1, goldRing);

        // Move to ring 2
        Item fromRing1 = equipment.unequip(EquipmentSlot.RING_1);
        equipment.equip(EquipmentSlot.RING_2, fromRing1);

        assertNull(equipment.getEquipped(EquipmentSlot.RING_1), "Ring 1 should be empty");
        assertEquals(goldRing, equipment.getEquipped(EquipmentSlot.RING_2), "Ring 2 should have item");
    }

    @Test
    @DisplayName("Should maintain separate state for each equipment instance")
    void testMultipleEquipmentInstances() {
        Equipment equipment1 = new Equipment();
        Equipment equipment2 = new Equipment();

        equipment1.equip(EquipmentSlot.MAIN_HAND, ironSword);
        equipment2.equip(EquipmentSlot.MAIN_HAND, steelSword);

        assertEquals(ironSword, equipment1.getEquipped(EquipmentSlot.MAIN_HAND), "Equipment1 should have iron sword");
        assertEquals(steelSword, equipment2.getEquipped(EquipmentSlot.MAIN_HAND), "Equipment2 should have steel sword");
    }

    @Test
    @DisplayName("Should handle equipping same item to different slots on different equipment")
    void testSameItemDifferentEquipment() {
        Equipment equipment1 = new Equipment();
        Equipment equipment2 = new Equipment();

        equipment1.equip(EquipmentSlot.RING_1, goldRing);
        equipment2.equip(EquipmentSlot.RING_2, goldRing);

        // Same item reference can be in multiple equipment instances
        assertSame(equipment1.getEquipped(EquipmentSlot.RING_1),
                   equipment2.getEquipped(EquipmentSlot.RING_2),
                   "Both should reference same item instance");
    }

    @Test
    @DisplayName("Should calculate stats correctly after multiple equipment changes")
    void testStatsAfterMultipleChanges() {
        // Equip initial set
        equipment.equip(EquipmentSlot.MAIN_HAND, ironSword);
        equipment.equip(EquipmentSlot.HELMET, ironHelmet);

        ItemStats stats1 = equipment.getTotalStats();
        int damage1 = stats1.getDamage();
        int armor1 = stats1.getArmor();

        // Upgrade weapon
        equipment.equip(EquipmentSlot.MAIN_HAND, steelSword);

        ItemStats stats2 = equipment.getTotalStats();
        assertEquals(armor1, stats2.getArmor(), "Armor should remain same");
        assertTrue(stats2.getDamage() > damage1, "Damage should increase with better weapon");
        assertEquals(25, stats2.getDamage(), "Should have steel sword damage");
    }
}
