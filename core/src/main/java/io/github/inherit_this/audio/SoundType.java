package io.github.inherit_this.audio;

/**
 * Categories of sound effects in the game.
 * Each type can have multiple variant sounds that play randomly.
 */
public enum SoundType {
    // UI Sounds
    UI_CLICK,           // Button clicks
    UI_HOVER,           // Hovering over buttons
    INVENTORY_PICKUP,   // Picking up an item in inventory
    INVENTORY_DROP,     // Dropping/placing an item
    INVENTORY_EQUIP,    // Equipping an item
    INVENTORY_MOVE,     // Moving items around

    // Combat Sounds
    ATTACK_SWING,       // Weapon swing
    ATTACK_HIT,         // Hitting an enemy
    DAMAGE_TAKEN,       // Player takes damage
    ENEMY_DEATH,        // Enemy dies

    // Object Interaction
    OBJECT_BREAK_WOOD,  // Breaking wooden objects (crates)
    OBJECT_BREAK_CERAMIC, // Breaking ceramic objects (pots)
    OBJECT_BREAK_METAL, // Breaking metal objects (barrels)
    CHEST_OPEN,         // Opening a chest

    // Loot Sounds
    LOOT_GOLD,          // Picking up gold
    LOOT_ITEM,          // Picking up an item

    // Player Actions
    FOOTSTEP,           // Walking sound
    LEVEL_UP,           // Level up achievement

    // Map Editor
    EDITOR_PLACE,       // Placing tile/object
    EDITOR_DELETE,      // Deleting tile/object
    EDITOR_MODE_SWITCH, // Switching editor mode
    EDITOR_ERROR        // Invalid placement/action
}
