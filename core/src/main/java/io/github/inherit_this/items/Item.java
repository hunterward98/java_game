package io.github.inherit_this.items;

import com.badlogic.gdx.graphics.Texture;

/**
 * Represents an item in the game world and inventory.
 * Items can be different sizes and have various properties.
 */
public class Item {
    private final String id;              // Unique identifier (e.g., "iron_sword")
    private final String name;            // Display name (e.g., "Iron Sword")
    private final String description;     // Tooltip description
    private final ItemType type;          // Category
    private final ItemRarity rarity;      // Rarity tier
    private final Texture icon;           // Visual representation
    private final int width;              // Grid width (in cells)
    private final int height;             // Grid height (in cells)
    private final int maxStackSize;       // Max items per stack (1 = not stackable)
    private final int value;              // Base sell value in gold

    public Item(String id, String name, String description, ItemType type, ItemRarity rarity,
                Texture icon, int width, int height, int maxStackSize, int value) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.rarity = rarity;
        this.icon = icon;
        this.width = width;
        this.height = height;
        this.maxStackSize = maxStackSize;
        this.value = value;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public ItemType getType() { return type; }
    public ItemRarity getRarity() { return rarity; }
    public Texture getIcon() { return icon; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getMaxStackSize() { return maxStackSize; }
    public int getValue() { return value; }
    public boolean isStackable() { return maxStackSize > 1; }

    @Override
    public String toString() {
        return name + " (" + type + ")";
    }
}
