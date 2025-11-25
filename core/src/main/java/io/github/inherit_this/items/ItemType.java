package io.github.inherit_this.items;

/**
 * Categories of items in the game.
 * Used for organizing inventory and determining item behavior.
 */
public enum ItemType {
    WEAPON,      // Swords, axes, bows
    ARMOR,       // Helmets, chest, legs, boots
    CONSUMABLE,  // Potions, food
    MATERIAL,    // Ore, wood, cloth
    TOOL,        // Pickaxes, fishing rods
    QUEST,       // Quest-specific items
    MISC         // Everything else
}
