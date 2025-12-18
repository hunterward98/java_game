package io.github.inherit_this.loot;

import io.github.inherit_this.items.Item;
import io.github.inherit_this.items.ItemRarity;
import io.github.inherit_this.items.ItemType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ScaledLootTableTest {

    private ScaledLootTable lootTable;
    private Item testItem1;
    private Item testItem2;

    @BeforeEach
    void setUp() {
        lootTable = new ScaledLootTable();

        testItem1 = new Item(
            "health_potion",
            "Health Potion",
            "Restores HP",
            ItemType.CONSUMABLE,
            ItemRarity.COMMON,
            null,
            1, 1,
            10,
            25
        );

        testItem2 = new Item(
            "iron_sword",
            "Iron Sword",
            "A sword",
            ItemType.WEAPON,
            ItemRarity.UNCOMMON,
            null,
            1, 2,
            1,
            100
        );
    }

    @Nested
    @DisplayName("Constructor and Initial State")
    class ConstructorTests {

        @Test
        @DisplayName("New loot table should have zero gold")
        void testInitialGold() {
            assertEquals(0, lootTable.getGoldMin());
            assertEquals(0, lootTable.getGoldMax());
        }

        @Test
        @DisplayName("New loot table should have zero XP")
        void testInitialXP() {
            assertEquals(0, lootTable.getXP());
        }

        @Test
        @DisplayName("New loot table should have empty item drops list")
        void testInitialItemDrops() {
            assertNotNull(lootTable.getItemDrops());
            assertTrue(lootTable.getItemDrops().isEmpty());
        }
    }

    @Nested
    @DisplayName("Gold Configuration")
    class GoldTests {

        @Test
        @DisplayName("Should set and get gold minimum")
        void testSetGoldMin() {
            lootTable.setGoldMin(10);
            assertEquals(10, lootTable.getGoldMin());
        }

        @Test
        @DisplayName("Should set and get gold maximum")
        void testSetGoldMax() {
            lootTable.setGoldMax(50);
            assertEquals(50, lootTable.getGoldMax());
        }

        @Test
        @DisplayName("Should handle gold range")
        void testGoldRange() {
            lootTable.setGoldMin(10);
            lootTable.setGoldMax(100);

            assertEquals(10, lootTable.getGoldMin());
            assertEquals(100, lootTable.getGoldMax());
        }

        @Test
        @DisplayName("Should handle zero gold")
        void testZeroGold() {
            lootTable.setGoldMin(0);
            lootTable.setGoldMax(0);

            assertEquals(0, lootTable.getGoldMin());
            assertEquals(0, lootTable.getGoldMax());
        }

        @Test
        @DisplayName("Should handle large gold values")
        void testLargeGoldValues() {
            lootTable.setGoldMin(1000);
            lootTable.setGoldMax(10000);

            assertEquals(1000, lootTable.getGoldMin());
            assertEquals(10000, lootTable.getGoldMax());
        }
    }

    @Nested
    @DisplayName("XP Configuration")
    class XPTests {

        @Test
        @DisplayName("Should set and get XP")
        void testSetXP() {
            lootTable.setXP(100);
            assertEquals(100, lootTable.getXP());
        }

        @Test
        @DisplayName("Should handle zero XP")
        void testZeroXP() {
            lootTable.setXP(0);
            assertEquals(0, lootTable.getXP());
        }

        @Test
        @DisplayName("Should handle large XP values")
        void testLargeXP() {
            lootTable.setXP(999999);
            assertEquals(999999, lootTable.getXP());
        }

        @Test
        @DisplayName("Should update XP multiple times")
        void testUpdateXP() {
            lootTable.setXP(50);
            assertEquals(50, lootTable.getXP());

            lootTable.setXP(100);
            assertEquals(100, lootTable.getXP());

            lootTable.setXP(75);
            assertEquals(75, lootTable.getXP());
        }
    }

    @Nested
    @DisplayName("Item Drop Management")
    class ItemDropTests {

        @Test
        @DisplayName("Should add item drop to list")
        void testAddItemDrop() {
            lootTable.addItemDrop(testItem1, 1, 1, 1.0f);

            List<ScaledLootTable.ItemDrop> drops = lootTable.getItemDrops();
            assertEquals(1, drops.size());
        }

        @Test
        @DisplayName("Should add multiple item drops")
        void testAddMultipleItemDrops() {
            lootTable.addItemDrop(testItem1, 1, 3, 0.5f);
            lootTable.addItemDrop(testItem2, 1, 1, 1.0f);

            List<ScaledLootTable.ItemDrop> drops = lootTable.getItemDrops();
            assertEquals(2, drops.size());
        }

        @Test
        @DisplayName("Should preserve item drop order")
        void testItemDropOrder() {
            lootTable.addItemDrop(testItem1, 1, 1, 0.5f);
            lootTable.addItemDrop(testItem2, 2, 4, 0.75f);

            List<ScaledLootTable.ItemDrop> drops = lootTable.getItemDrops();
            assertEquals(testItem1, drops.get(0).getItem());
            assertEquals(testItem2, drops.get(1).getItem());
        }

        @Test
        @DisplayName("Item drops list should return actual list reference")
        void testItemDropsListReference() {
            lootTable.addItemDrop(testItem1, 1, 1, 1.0f);

            List<ScaledLootTable.ItemDrop> drops1 = lootTable.getItemDrops();
            List<ScaledLootTable.ItemDrop> drops2 = lootTable.getItemDrops();

            assertSame(drops1, drops2);
        }
    }

    @Nested
    @DisplayName("ItemDrop Inner Class")
    class ItemDropInnerClassTests {

        @Test
        @DisplayName("ItemDrop should store item correctly")
        void testItemDropItem() {
            ScaledLootTable.ItemDrop drop = new ScaledLootTable.ItemDrop(testItem1, 1, 3, 0.5f);
            assertEquals(testItem1, drop.getItem());
        }

        @Test
        @DisplayName("ItemDrop should store min quantity")
        void testItemDropMinQuantity() {
            ScaledLootTable.ItemDrop drop = new ScaledLootTable.ItemDrop(testItem1, 2, 5, 0.5f);
            assertEquals(2, drop.getMinQuantity());
        }

        @Test
        @DisplayName("ItemDrop should store max quantity")
        void testItemDropMaxQuantity() {
            ScaledLootTable.ItemDrop drop = new ScaledLootTable.ItemDrop(testItem1, 1, 10, 0.5f);
            assertEquals(10, drop.getMaxQuantity());
        }

        @Test
        @DisplayName("ItemDrop should store drop chance")
        void testItemDropChance() {
            ScaledLootTable.ItemDrop drop = new ScaledLootTable.ItemDrop(testItem1, 1, 1, 0.75f);
            assertEquals(0.75f, drop.getDropChance(), 0.001f);
        }

        @Test
        @DisplayName("ItemDrop should handle 100% drop chance")
        void testItemDropFullChance() {
            ScaledLootTable.ItemDrop drop = new ScaledLootTable.ItemDrop(testItem1, 1, 1, 1.0f);
            assertEquals(1.0f, drop.getDropChance(), 0.001f);
        }

        @Test
        @DisplayName("ItemDrop should handle 0% drop chance")
        void testItemDropZeroChance() {
            ScaledLootTable.ItemDrop drop = new ScaledLootTable.ItemDrop(testItem1, 1, 1, 0.0f);
            assertEquals(0.0f, drop.getDropChance(), 0.001f);
        }

        @Test
        @DisplayName("ItemDrop should handle single quantity")
        void testItemDropSingleQuantity() {
            ScaledLootTable.ItemDrop drop = new ScaledLootTable.ItemDrop(testItem1, 1, 1, 0.5f);
            assertEquals(1, drop.getMinQuantity());
            assertEquals(1, drop.getMaxQuantity());
        }

        @Test
        @DisplayName("ItemDrop should handle quantity range")
        void testItemDropQuantityRange() {
            ScaledLootTable.ItemDrop drop = new ScaledLootTable.ItemDrop(testItem1, 5, 20, 0.5f);
            assertEquals(5, drop.getMinQuantity());
            assertEquals(20, drop.getMaxQuantity());
        }
    }

    @Nested
    @DisplayName("Complete Loot Table Configuration")
    class CompleteLootTableTests {

        @Test
        @DisplayName("Should configure complete loot table")
        void testCompleteLootTable() {
            lootTable.setGoldMin(50);
            lootTable.setGoldMax(200);
            lootTable.setXP(150);
            lootTable.addItemDrop(testItem1, 1, 3, 0.5f);
            lootTable.addItemDrop(testItem2, 1, 1, 0.1f);

            assertEquals(50, lootTable.getGoldMin());
            assertEquals(200, lootTable.getGoldMax());
            assertEquals(150, lootTable.getXP());
            assertEquals(2, lootTable.getItemDrops().size());
        }

        @Test
        @DisplayName("Should handle loot table with only gold")
        void testGoldOnlyLootTable() {
            lootTable.setGoldMin(10);
            lootTable.setGoldMax(30);

            assertEquals(10, lootTable.getGoldMin());
            assertEquals(30, lootTable.getGoldMax());
            assertEquals(0, lootTable.getXP());
            assertTrue(lootTable.getItemDrops().isEmpty());
        }

        @Test
        @DisplayName("Should handle loot table with only items")
        void testItemsOnlyLootTable() {
            lootTable.addItemDrop(testItem1, 1, 1, 1.0f);
            lootTable.addItemDrop(testItem2, 1, 1, 1.0f);

            assertEquals(0, lootTable.getGoldMin());
            assertEquals(0, lootTable.getGoldMax());
            assertEquals(0, lootTable.getXP());
            assertEquals(2, lootTable.getItemDrops().size());
        }

        @Test
        @DisplayName("Should handle loot table with only XP")
        void testXPOnlyLootTable() {
            lootTable.setXP(500);

            assertEquals(0, lootTable.getGoldMin());
            assertEquals(0, lootTable.getGoldMax());
            assertEquals(500, lootTable.getXP());
            assertTrue(lootTable.getItemDrops().isEmpty());
        }

        @Test
        @DisplayName("Should handle empty loot table")
        void testEmptyLootTable() {
            assertEquals(0, lootTable.getGoldMin());
            assertEquals(0, lootTable.getGoldMax());
            assertEquals(0, lootTable.getXP());
            assertTrue(lootTable.getItemDrops().isEmpty());
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle many item drops")
        void testManyItemDrops() {
            for (int i = 0; i < 100; i++) {
                lootTable.addItemDrop(testItem1, 1, 1, 0.01f);
            }

            assertEquals(100, lootTable.getItemDrops().size());
        }

        @Test
        @DisplayName("Should handle very small drop chances")
        void testVerySmallDropChance() {
            lootTable.addItemDrop(testItem1, 1, 1, 0.0001f);

            ScaledLootTable.ItemDrop drop = lootTable.getItemDrops().get(0);
            assertEquals(0.0001f, drop.getDropChance(), 0.00001f);
        }

        @Test
        @DisplayName("Should handle negative gold values")
        void testNegativeGold() {
            lootTable.setGoldMin(-10);
            lootTable.setGoldMax(-5);

            assertEquals(-10, lootTable.getGoldMin());
            assertEquals(-5, lootTable.getGoldMax());
        }

        @Test
        @DisplayName("Should handle negative XP")
        void testNegativeXP() {
            lootTable.setXP(-100);
            assertEquals(-100, lootTable.getXP());
        }
    }
}
