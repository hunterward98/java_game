package io.github.inherit_this.items;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for the grid-based Inventory system.
 * Tests item placement, stacking, removal, gold management, and edge cases.
 */
class InventoryTest {

    private Inventory inventory;
    private Item smallItem;      // 1x1 stackable
    private Item largeItem;      // 2x2 non-stackable
    private Item tallItem;       // 1x2 non-stackable
    private Item wideItem;       // 2x1 stackable
    private Item anotherSmallItem; // 1x1 stackable (different ID)

    @BeforeEach
    void setUp() {
        inventory = new Inventory(5, 4); // 5 wide x 4 tall grid

        // Create test items
        smallItem = new Item(
            "health_potion",
            "Health Potion",
            "Restores 50 HP",
            ItemType.CONSUMABLE,
            ItemRarity.COMMON,
            null, // No texture in unit tests
            1, 1,
            10,  // Stackable
            25   // Value
        );

        largeItem = new Item(
            "iron_sword",
            "Iron Sword",
            "A heavy blade",
            ItemType.WEAPON,
            ItemRarity.COMMON,
            null,
            2, 2,
            1,   // Non-stackable
            100
        );

        tallItem = new Item(
            "long_bow",
            "Long Bow",
            "A tall bow",
            ItemType.WEAPON,
            ItemRarity.COMMON,
            null,
            1, 2,
            1,   // Non-stackable
            75
        );

        wideItem = new Item(
            "arrow_bundle",
            "Arrow Bundle",
            "Bundle of arrows",
            ItemType.CONSUMABLE,
            ItemRarity.COMMON,
            null,
            2, 1,
            20,  // Stackable
            10
        );

        anotherSmallItem = new Item(
            "mana_potion",
            "Mana Potion",
            "Restores 30 MP",
            ItemType.CONSUMABLE,
            ItemRarity.COMMON,
            null,
            1, 1,
            10,  // Stackable
            30
        );
    }

    // ==================== Initialization Tests ====================

    @Nested
    @DisplayName("Inventory Initialization")
    class InventoryInitialization {

        @Test
        @DisplayName("Should initialize with correct dimensions")
        void testInventoryDimensions() {
            assertEquals(5, inventory.getGridWidth(), "Grid width should be 5");
            assertEquals(4, inventory.getGridHeight(), "Grid height should be 4");
        }

        @Test
        @DisplayName("Should start with zero gold")
        void testInitialGold() {
            assertEquals(0, inventory.getGold(), "Should start with 0 gold");
        }

        @Test
        @DisplayName("Should start with empty grid")
        void testEmptyGrid() {
            for (int x = 0; x < inventory.getGridWidth(); x++) {
                for (int y = 0; y < inventory.getGridHeight(); y++) {
                    assertNull(inventory.getItemAt(x, y),
                        "Grid position (" + x + ", " + y + ") should be empty");
                }
            }
        }
    }

    // ==================== Gold Management Tests ====================

    @Nested
    @DisplayName("Gold Management")
    class GoldManagement {

        @Test
        @DisplayName("setGold should set gold amount")
        void testSetGold() {
            inventory.setGold(500);
            assertEquals(500, inventory.getGold(), "Gold should be set to 500");
        }

        @Test
        @DisplayName("setGold should not allow negative gold")
        void testSetGoldNegative() {
            inventory.setGold(-100);
            assertEquals(0, inventory.getGold(), "Negative gold should be clamped to 0");
        }

        @Test
        @DisplayName("addGold should increase gold amount")
        void testAddGold() {
            inventory.setGold(100);
            inventory.addGold(50);
            assertEquals(150, inventory.getGold(), "Gold should be 150 after adding 50");
        }

        @Test
        @DisplayName("addGold with negative amount should decrease gold")
        void testAddGoldNegative() {
            inventory.setGold(100);
            inventory.addGold(-30);
            assertEquals(70, inventory.getGold(), "Gold should be 70 after adding -30");
        }

        @Test
        @DisplayName("addGold should not go below zero")
        void testAddGoldBelowZero() {
            inventory.setGold(50);
            inventory.addGold(-100);
            assertEquals(0, inventory.getGold(), "Gold should be clamped at 0");
        }

        @Test
        @DisplayName("removeGold should decrease gold amount")
        void testRemoveGold() {
            inventory.setGold(100);
            inventory.removeGold(30);
            assertEquals(70, inventory.getGold(), "Gold should be 70 after removing 30");
        }

        @Test
        @DisplayName("removeGold should not go below zero")
        void testRemoveGoldBelowZero() {
            inventory.setGold(50);
            inventory.removeGold(100);
            assertEquals(0, inventory.getGold(), "Gold should be clamped at 0");
        }
    }

    // ==================== Position Validation Tests ====================

    @Nested
    @DisplayName("Position Validation")
    class PositionValidation {

        @Test
        @DisplayName("isValidPosition should return true for valid positions")
        void testValidPositions() {
            assertTrue(inventory.isValidPosition(0, 0), "Top-left corner should be valid");
            assertTrue(inventory.isValidPosition(4, 3), "Bottom-right corner should be valid");
            assertTrue(inventory.isValidPosition(2, 2), "Middle position should be valid");
        }

        @Test
        @DisplayName("isValidPosition should return false for negative positions")
        void testNegativePositions() {
            assertFalse(inventory.isValidPosition(-1, 0), "Negative X should be invalid");
            assertFalse(inventory.isValidPosition(0, -1), "Negative Y should be invalid");
            assertFalse(inventory.isValidPosition(-1, -1), "Both negative should be invalid");
        }

        @Test
        @DisplayName("isValidPosition should return false for out-of-bounds positions")
        void testOutOfBoundsPositions() {
            assertFalse(inventory.isValidPosition(5, 0), "X beyond grid width should be invalid");
            assertFalse(inventory.isValidPosition(0, 4), "Y beyond grid height should be invalid");
            assertFalse(inventory.isValidPosition(10, 10), "Both beyond bounds should be invalid");
        }

        @Test
        @DisplayName("getItemAt should return null for invalid positions")
        void testGetItemAtInvalidPosition() {
            assertNull(inventory.getItemAt(-1, 0), "Should return null for negative X");
            assertNull(inventory.getItemAt(0, -1), "Should return null for negative Y");
            assertNull(inventory.getItemAt(10, 10), "Should return null for out of bounds");
        }
    }

    // ==================== Item Placement Tests ====================

    @Nested
    @DisplayName("Item Placement")
    class ItemPlacement {

        @Test
        @DisplayName("Should place 1x1 item at valid position")
        void testPlaceSmallItem() {
            ItemStack stack = new ItemStack(smallItem, 5);
            boolean placed = inventory.placeItem(stack, 0, 0);

            assertTrue(placed, "Should successfully place item");
            assertEquals(stack, inventory.getItemAt(0, 0), "Item should be at position (0,0)");
        }

        @Test
        @DisplayName("Should place 2x2 item and occupy all cells")
        void testPlaceLargeItem() {
            ItemStack stack = new ItemStack(largeItem, 1);
            boolean placed = inventory.placeItem(stack, 0, 0);

            assertTrue(placed, "Should successfully place 2x2 item");
            assertEquals(stack, inventory.getItemAt(0, 0), "Top-left should have item");
            assertEquals(stack, inventory.getItemAt(1, 0), "Top-right should have item");
            assertEquals(stack, inventory.getItemAt(0, 1), "Bottom-left should have item");
            assertEquals(stack, inventory.getItemAt(1, 1), "Bottom-right should have item");
        }

        @Test
        @DisplayName("Should place tall item (1x2)")
        void testPlaceTallItem() {
            ItemStack stack = new ItemStack(tallItem, 1);
            boolean placed = inventory.placeItem(stack, 2, 0);

            assertTrue(placed, "Should successfully place tall item");
            assertEquals(stack, inventory.getItemAt(2, 0), "Top cell should have item");
            assertEquals(stack, inventory.getItemAt(2, 1), "Bottom cell should have item");
        }

        @Test
        @DisplayName("Should place wide item (2x1)")
        void testPlaceWideItem() {
            ItemStack stack = new ItemStack(wideItem, 5);
            boolean placed = inventory.placeItem(stack, 0, 0);

            assertTrue(placed, "Should successfully place wide item");
            assertEquals(stack, inventory.getItemAt(0, 0), "Left cell should have item");
            assertEquals(stack, inventory.getItemAt(1, 0), "Right cell should have item");
        }

        @Test
        @DisplayName("Cannot place item at invalid position")
        void testCannotPlaceAtInvalidPosition() {
            ItemStack stack = new ItemStack(smallItem, 1);
            boolean placed = inventory.placeItem(stack, -1, 0);

            assertFalse(placed, "Should not place at negative position");
            assertNull(inventory.getItemAt(0, 0), "Grid should remain empty");
        }

        @Test
        @DisplayName("Cannot place item that would go out of bounds")
        void testCannotPlaceOutOfBounds() {
            ItemStack stack = new ItemStack(largeItem, 1); // 2x2 item
            boolean placed = inventory.placeItem(stack, 4, 3); // Would extend to (5,4)

            assertFalse(placed, "Should not place item extending beyond grid");
        }

        @Test
        @DisplayName("Cannot place item on occupied cells")
        void testCannotPlaceOnOccupiedCells() {
            ItemStack stack1 = new ItemStack(smallItem, 1);
            ItemStack stack2 = new ItemStack(anotherSmallItem, 1);

            inventory.placeItem(stack1, 0, 0);
            boolean placed = inventory.placeItem(stack2, 0, 0);

            assertFalse(placed, "Should not place item on occupied cell");
            assertEquals(stack1, inventory.getItemAt(0, 0), "Original item should remain");
        }

        @Test
        @DisplayName("Cannot place large item overlapping existing item")
        void testCannotPlaceLargeItemOverlapping() {
            ItemStack smallStack = new ItemStack(smallItem, 1);
            ItemStack largeStack = new ItemStack(largeItem, 1); // 2x2

            inventory.placeItem(smallStack, 1, 1); // Place at middle
            boolean placed = inventory.placeItem(largeStack, 0, 0); // Would overlap

            assertFalse(placed, "Should not place large item overlapping existing item");
        }
    }

    // ==================== Item Removal Tests ====================

    @Nested
    @DisplayName("Item Removal")
    class ItemRemoval {

        @Test
        @DisplayName("Should remove 1x1 item")
        void testRemoveSmallItem() {
            ItemStack stack = new ItemStack(smallItem, 5);
            inventory.placeItem(stack, 0, 0);

            ItemStack removed = inventory.removeItem(0, 0);

            assertEquals(stack, removed, "Should return removed stack");
            assertNull(inventory.getItemAt(0, 0), "Grid position should be empty");
        }

        @Test
        @DisplayName("Should remove 2x2 item and clear all cells")
        void testRemoveLargeItem() {
            ItemStack stack = new ItemStack(largeItem, 1);
            inventory.placeItem(stack, 0, 0);

            ItemStack removed = inventory.removeItem(0, 0);

            assertEquals(stack, removed, "Should return removed stack");
            assertNull(inventory.getItemAt(0, 0), "Top-left should be empty");
            assertNull(inventory.getItemAt(1, 0), "Top-right should be empty");
            assertNull(inventory.getItemAt(0, 1), "Bottom-left should be empty");
            assertNull(inventory.getItemAt(1, 1), "Bottom-right should be empty");
        }

        @Test
        @DisplayName("Should remove item when clicking top-left cell")
        void testRemoveFromTopLeftCell() {
            ItemStack stack = new ItemStack(largeItem, 1);
            inventory.placeItem(stack, 0, 0);

            // Click top-left corner of the 2x2 item
            ItemStack removed = inventory.removeItem(0, 0);

            assertEquals(stack, removed, "Should return stack when clicking top-left cell");
            assertNull(inventory.getItemAt(0, 0), "Top-left should be cleared");
            assertNull(inventory.getItemAt(1, 0), "Top-right should be cleared");
            assertNull(inventory.getItemAt(0, 1), "Bottom-left should be cleared");
            assertNull(inventory.getItemAt(1, 1), "Bottom-right should be cleared");
        }

        @Test
        @DisplayName("Should return null when removing from empty position")
        void testRemoveFromEmptyPosition() {
            ItemStack removed = inventory.removeItem(0, 0);

            assertNull(removed, "Should return null for empty position");
        }

        @Test
        @DisplayName("Should return null when removing from invalid position")
        void testRemoveFromInvalidPosition() {
            ItemStack removed = inventory.removeItem(-1, 0);

            assertNull(removed, "Should return null for invalid position");
        }
    }

    // ==================== Auto-Add Item Tests ====================

    @Nested
    @DisplayName("Auto-Add Item")
    class AutoAddItem {

        @Test
        @DisplayName("Should auto-add item to first available position")
        void testAutoAddToEmptyInventory() {
            boolean added = inventory.addItem(smallItem, 5);

            assertTrue(added, "Should successfully add item");
            ItemStack stack = inventory.getItemAt(0, 0);
            assertNotNull(stack, "Item should be placed at first position");
            assertEquals(5, stack.getQuantity(), "Should have correct quantity");
        }

        @Test
        @DisplayName("Should stack with existing items of same type")
        void testAutoAddStackWithExisting() {
            inventory.addItem(smallItem, 5);
            inventory.addItem(smallItem, 3);

            ItemStack stack = inventory.getItemAt(0, 0);
            assertEquals(8, stack.getQuantity(), "Should stack to 8 items");
        }

        @Test
        @DisplayName("Should create new stack when existing stack is full")
        void testAutoAddCreateNewStackWhenFull() {
            inventory.addItem(smallItem, 10); // Max stack
            inventory.addItem(smallItem, 5);  // Should create new stack

            ItemStack stack1 = inventory.getItemAt(0, 0);
            ItemStack stack2 = inventory.getItemAt(1, 0);

            assertEquals(10, stack1.getQuantity(), "First stack should be full");
            assertEquals(5, stack2.getQuantity(), "Second stack should have remaining");
        }

        @Test
        @DisplayName("Should fill multiple stacks correctly")
        void testAutoAddMultipleStacks() {
            boolean added = inventory.addItem(smallItem, 25); // 2.5 stacks

            assertTrue(added, "Should successfully add 25 items");

            ItemStack stack1 = inventory.getItemAt(0, 0);
            ItemStack stack2 = inventory.getItemAt(1, 0);
            ItemStack stack3 = inventory.getItemAt(2, 0);

            assertEquals(10, stack1.getQuantity(), "First stack should be full");
            assertEquals(10, stack2.getQuantity(), "Second stack should be full");
            assertEquals(5, stack3.getQuantity(), "Third stack should have remainder");
        }

        @Test
        @DisplayName("Should not stack different items together")
        void testAutoAddDifferentItems() {
            inventory.addItem(smallItem, 5);
            inventory.addItem(anotherSmallItem, 3);

            ItemStack stack1 = inventory.getItemAt(0, 0);
            ItemStack stack2 = inventory.getItemAt(1, 0);

            assertEquals(smallItem, stack1.getItem(), "First stack should be health potion");
            assertEquals(5, stack1.getQuantity());
            assertEquals(anotherSmallItem, stack2.getItem(), "Second stack should be mana potion");
            assertEquals(3, stack2.getQuantity());
        }

        @Test
        @DisplayName("Should place non-stackable items separately")
        void testAutoAddNonStackableItems() {
            inventory.addItem(largeItem, 1);
            inventory.addItem(largeItem, 1);

            ItemStack stack1 = inventory.getItemAt(0, 0);
            ItemStack stack2 = inventory.getItemAt(2, 0);

            assertNotNull(stack1, "First item should be placed");
            assertNotNull(stack2, "Second item should be placed separately");
            assertEquals(1, stack1.getQuantity());
            assertEquals(1, stack2.getQuantity());
        }

        @Test
        @DisplayName("Should return false when inventory is full")
        void testAutoAddToFullInventory() {
            // Fill inventory with 2x2 items (5x4 grid can fit 4 items: 2 rows, 2 per row)
            for (int i = 0; i < 4; i++) {
                inventory.addItem(largeItem, 1);
            }

            // Try to add one more
            boolean added = inventory.addItem(largeItem, 1);

            assertFalse(added, "Should fail to add item to full inventory");
        }

        @Test
        @DisplayName("Should handle large item auto-placement")
        void testAutoAddLargeItem() {
            boolean added = inventory.addItem(largeItem, 1);

            assertTrue(added, "Should add large item");
            assertNotNull(inventory.getItemAt(0, 0), "Large item should occupy space");
        }

        @Test
        @DisplayName("Should find available space among placed items")
        void testAutoAddFindSpace() {
            // Place items leaving a gap
            inventory.placeItem(new ItemStack(smallItem, 1), 0, 0);
            inventory.placeItem(new ItemStack(smallItem, 1), 2, 0);

            boolean added = inventory.addItem(anotherSmallItem, 1);

            assertTrue(added, "Should find gap and place item");
            ItemStack stack = inventory.getItemAt(1, 0);
            assertNotNull(stack, "Should place in gap at (1,0)");
        }
    }

    // ==================== Item Counting Tests ====================

    @Nested
    @DisplayName("Item Counting")
    class ItemCounting {

        @Test
        @DisplayName("Should count items in single stack")
        void testCountSingleStack() {
            inventory.addItem(smallItem, 7);

            int count = inventory.countItem(smallItem.getId());
            assertEquals(7, count, "Should count 7 items");
        }

        @Test
        @DisplayName("Should count items across multiple stacks")
        void testCountMultipleStacks() {
            inventory.addItem(smallItem, 25); // 3 stacks: 10+10+5

            int count = inventory.countItem(smallItem.getId());
            assertEquals(25, count, "Should count all 25 items");
        }

        @Test
        @DisplayName("Should return 0 for items not in inventory")
        void testCountNonExistentItem() {
            int count = inventory.countItem("nonexistent_item");
            assertEquals(0, count, "Should return 0 for non-existent item");
        }

        @Test
        @DisplayName("Should count only matching item IDs")
        void testCountOnlyMatchingItems() {
            inventory.addItem(smallItem, 10);
            inventory.addItem(anotherSmallItem, 5);

            int count = inventory.countItem(smallItem.getId());
            assertEquals(10, count, "Should only count health potions");
        }

        @Test
        @DisplayName("Should handle counting in partially filled inventory")
        void testCountInPartialInventory() {
            inventory.placeItem(new ItemStack(smallItem, 5), 0, 0);
            inventory.placeItem(new ItemStack(largeItem, 1), 2, 0);
            inventory.placeItem(new ItemStack(smallItem, 3), 0, 2);

            int count = inventory.countItem(smallItem.getId());
            assertEquals(8, count, "Should count 8 total (5+3)");
        }
    }

    // ==================== Clear Inventory Tests ====================

    @Nested
    @DisplayName("Clear Inventory")
    class ClearInventory {

        @Test
        @DisplayName("Should clear all items from inventory")
        void testClearItems() {
            inventory.addItem(smallItem, 10);
            inventory.addItem(largeItem, 1);

            inventory.clear();

            for (int x = 0; x < inventory.getGridWidth(); x++) {
                for (int y = 0; y < inventory.getGridHeight(); y++) {
                    assertNull(inventory.getItemAt(x, y),
                        "Position (" + x + ", " + y + ") should be empty");
                }
            }
        }

        @Test
        @DisplayName("Should reset gold to zero")
        void testClearResetsGold() {
            inventory.setGold(500);
            inventory.addItem(smallItem, 5);

            inventory.clear();

            assertEquals(0, inventory.getGold(), "Gold should be reset to 0");
        }

        @Test
        @DisplayName("Clear should work on empty inventory")
        void testClearEmptyInventory() {
            inventory.clear();

            assertEquals(0, inventory.getGold(), "Gold should be 0");
            assertNull(inventory.getItemAt(0, 0), "Grid should still be empty");
        }
    }

    // ==================== Edge Cases ====================

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle placing item at bottom-right corner")
        void testPlaceAtBottomRightCorner() {
            ItemStack stack = new ItemStack(smallItem, 1);
            boolean placed = inventory.placeItem(stack, 4, 3); // Last position in 5x4 grid

            assertTrue(placed, "Should place at bottom-right corner");
            assertEquals(stack, inventory.getItemAt(4, 3));
        }

        @Test
        @DisplayName("Should handle 2x2 item at exact fit position")
        void testLargeItemExactFit() {
            ItemStack stack = new ItemStack(largeItem, 1); // 2x2
            boolean placed = inventory.placeItem(stack, 3, 2); // Would occupy (3,2) to (4,3)

            assertTrue(placed, "Should fit 2x2 item at bottom-right");
        }

        @Test
        @DisplayName("Should reject 2x2 item that would partially extend beyond grid")
        void testLargeItemPartialOverflow() {
            ItemStack stack = new ItemStack(largeItem, 1); // 2x2
            boolean placed = inventory.placeItem(stack, 4, 2); // Would extend to x=5 (out of bounds)

            assertFalse(placed, "Should reject item that would overflow");
        }

        @Test
        @DisplayName("Should handle adding 0 quantity")
        void testAddZeroQuantity() {
            boolean added = inventory.addItem(smallItem, 0);

            assertTrue(added, "Adding 0 items should succeed trivially");
            assertEquals(0, inventory.countItem(smallItem.getId()), "Should have 0 items");
        }

        @Test
        @DisplayName("Should handle single cell inventory")
        void testSingleCellInventory() {
            Inventory tiny = new Inventory(1, 1);
            ItemStack stack = new ItemStack(smallItem, 5);

            boolean placed = tiny.placeItem(stack, 0, 0);

            assertTrue(placed, "Should place item in single cell");
            assertEquals(stack, tiny.getItemAt(0, 0));
        }

        @Test
        @DisplayName("Single cell inventory should reject large items")
        void testSingleCellRejectsLargeItem() {
            Inventory tiny = new Inventory(1, 1);
            boolean placed = tiny.placeItem(new ItemStack(largeItem, 1), 0, 0);

            assertFalse(placed, "Should reject 2x2 item in 1x1 inventory");
        }

        @Test
        @DisplayName("Should handle very large inventory")
        void testLargeInventory() {
            Inventory large = new Inventory(20, 20);

            assertEquals(20, large.getGridWidth());
            assertEquals(20, large.getGridHeight());

            boolean added = large.addItem(smallItem, 1);
            assertTrue(added, "Should work with large inventory");
        }

        @Test
        @DisplayName("Should maintain integrity after multiple operations")
        void testMultipleOperationsIntegrity() {
            inventory.addItem(smallItem, 15);     // 2 stacks
            inventory.addItem(largeItem, 1);      // 1 large item
            inventory.setGold(100);
            inventory.removeItem(0, 0);           // Remove first stack
            inventory.addGold(50);
            inventory.addItem(tallItem, 1);

            assertTrue(inventory.countItem(smallItem.getId()) >= 5, "Should still have small items");
            assertEquals(150, inventory.getGold(), "Gold should be 150");
            assertNotNull(inventory.getItemAt(2, 0), "Large item should still exist");
        }
    }
}
