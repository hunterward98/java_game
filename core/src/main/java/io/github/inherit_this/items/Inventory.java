package io.github.inherit_this.items;

/**
 * FATE-style grid-based inventory system.
 * Items occupy cells in a 2D grid based on their size.
 */
public class Inventory {
    private final int gridWidth;
    private final int gridHeight;
    private final ItemStack[][] grid;  // Grid of item stacks
    private int gold;

    public Inventory(int gridWidth, int gridHeight) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.grid = new ItemStack[gridWidth][gridHeight];
        this.gold = 0;
    }

    public int getGridWidth() {
        return gridWidth;
    }

    public int getGridHeight() {
        return gridHeight;
    }

    public int getGold() {
        return gold;
    }

    public void addGold(int amount) {
        gold = Math.max(0, gold + amount);
    }

    public void removeGold(int amount) {
        gold = Math.max(0, gold - amount);
    }

    /**
     * Gets the item stack at a specific grid position.
     */
    public ItemStack getItemAt(int x, int y) {
        if (!isValidPosition(x, y)) {
            return null;
        }
        return grid[x][y];
    }

    /**
     * Checks if a position is within grid bounds.
     */
    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < gridWidth && y >= 0 && y < gridHeight;
    }

    /**
     * Checks if an item can be placed at the given position.
     * Validates that all cells the item would occupy are empty.
     */
    public boolean canPlaceItem(Item item, int x, int y) {
        if (!isValidPosition(x, y)) {
            return false;
        }

        // Check if item would go out of bounds
        if (x + item.getWidth() > gridWidth || y + item.getHeight() > gridHeight) {
            return false;
        }

        // Check if all required cells are empty
        for (int dx = 0; dx < item.getWidth(); dx++) {
            for (int dy = 0; dy < item.getHeight(); dy++) {
                if (grid[x + dx][y + dy] != null) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Places an item stack at the specified position.
     * The item occupies cells based on its width and height.
     */
    public boolean placeItem(ItemStack stack, int x, int y) {
        if (!canPlaceItem(stack.getItem(), x, y)) {
            return false;
        }

        // Place the stack in all occupied cells
        // The top-left cell holds the actual stack, others reference it
        for (int dx = 0; dx < stack.getItem().getWidth(); dx++) {
            for (int dy = 0; dy < stack.getItem().getHeight(); dy++) {
                grid[x + dx][y + dy] = (dx == 0 && dy == 0) ? stack : stack; // All cells point to same stack
            }
        }

        return true;
    }

    /**
     * Removes an item from the grid.
     */
    public ItemStack removeItem(int x, int y) {
        ItemStack stack = getItemAt(x, y);
        if (stack == null) {
            return null;
        }

        // Clear all cells this item occupies
        Item item = stack.getItem();

        // Find the top-left corner of the item
        int topLeftX = x;
        int topLeftY = y;
        for (int dx = 0; dx <= x && dx < gridWidth; dx++) {
            for (int dy = 0; dy <= y && dy < gridHeight; dy++) {
                if (grid[dx][dy] == stack) {
                    topLeftX = dx;
                    topLeftY = dy;
                    break;
                }
            }
        }

        // Clear all occupied cells
        for (int dx = 0; dx < item.getWidth() && topLeftX + dx < gridWidth; dx++) {
            for (int dy = 0; dy < item.getHeight() && topLeftY + dy < gridHeight; dy++) {
                grid[topLeftX + dx][topLeftY + dy] = null;
            }
        }

        return stack;
    }

    /**
     * Attempts to add an item to the inventory automatically.
     * First tries to stack with existing items, then finds an empty spot.
     * @return true if successfully added, false if inventory is full
     */
    public boolean addItem(Item item, int quantity) {
        // Try to stack with existing items first
        if (item.isStackable()) {
            int remaining = quantity;
            for (int x = 0; x < gridWidth && remaining > 0; x++) {
                for (int y = 0; y < gridHeight && remaining > 0; y++) {
                    ItemStack existing = grid[x][y];
                    if (existing != null && existing.getItem().getId().equals(item.getId())) {
                        int canAdd = existing.getRemainingCapacity();
                        int toAdd = Math.min(canAdd, remaining);
                        existing.addQuantity(toAdd);
                        remaining -= toAdd;
                    }
                }
            }

            quantity = remaining;
        }

        // Create new stacks for remaining quantity
        while (quantity > 0) {
            int stackSize = Math.min(quantity, item.getMaxStackSize());
            ItemStack newStack = new ItemStack(item, stackSize);

            // Find first available position
            boolean placed = false;
            for (int y = 0; y < gridHeight && !placed; y++) {
                for (int x = 0; x < gridWidth && !placed; x++) {
                    if (canPlaceItem(item, x, y)) {
                        placeItem(newStack, x, y);
                        placed = true;
                        quantity -= stackSize;
                    }
                }
            }

            if (!placed) {
                return false; // Inventory full
            }
        }

        return true;
    }

    /**
     * Counts total quantity of a specific item in inventory.
     */
    public int countItem(String itemId) {
        int count = 0;
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                ItemStack stack = grid[x][y];
                if (stack != null && stack.getItem().getId().equals(itemId)) {
                    count += stack.getQuantity();
                }
            }
        }
        return count;
    }

    /**
     * Clears the entire inventory.
     */
    public void clear() {
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                grid[x][y] = null;
            }
        }
        gold = 0;
    }
}
