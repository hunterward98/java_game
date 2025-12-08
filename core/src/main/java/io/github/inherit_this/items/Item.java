package io.github.inherit_this.items;

import com.badlogic.gdx.graphics.Texture;
import java.util.ArrayList;
import java.util.List;

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
    private final float weight;           // Item weight (for inventory capacity)
    private final ItemStats stats;        // Combat/stat bonuses
    private final List<String> enchantments; // List of enchantment names
    private final EquipmentSlot equipmentSlot; // Which slot this can be equipped to (null if not equippable)

    public Item(String id, String name, String description, ItemType type, ItemRarity rarity,
                Texture icon, int width, int height, int maxStackSize, int value, float weight,
                ItemStats stats, List<String> enchantments, EquipmentSlot equipmentSlot) {
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
        this.weight = weight;
        this.stats = stats != null ? stats : ItemStats.none();
        this.enchantments = enchantments != null ? new ArrayList<>(enchantments) : new ArrayList<>();
        this.equipmentSlot = equipmentSlot;
    }

    // Simplified constructor for basic items (backward compatibility)
    public Item(String id, String name, String description, ItemType type, ItemRarity rarity,
                Texture icon, int width, int height, int maxStackSize, int value) {
        this(id, name, description, type, rarity, icon, width, height, maxStackSize, value,
             1.0f, ItemStats.none(), null, null);
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
    public float getWeight() { return weight; }
    public ItemStats getStats() { return stats; }
    public List<String> getEnchantments() { return new ArrayList<>(enchantments); }
    public EquipmentSlot getEquipmentSlot() { return equipmentSlot; }
    public boolean isStackable() { return maxStackSize > 1; }
    public boolean hasEnchantments() { return !enchantments.isEmpty(); }
    public boolean isEquippable() { return equipmentSlot != null; }

    @Override
    public String toString() {
        return name + " (" + type + ")";
    }
}
