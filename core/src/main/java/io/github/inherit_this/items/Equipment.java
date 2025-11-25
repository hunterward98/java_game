package io.github.inherit_this.items;

import java.util.EnumMap;
import java.util.Map;

/**
 * Manages equipped items on a player or entity.
 * Each equipment slot can hold one item.
 */
public class Equipment {
    private final Map<EquipmentSlot, Item> equippedItems;

    public Equipment() {
        this.equippedItems = new EnumMap<>(EquipmentSlot.class);
    }

    /**
     * Equip an item to a specific slot.
     * @param slot The equipment slot
     * @param item The item to equip
     * @return The previously equipped item in that slot, or null
     */
    public Item equip(EquipmentSlot slot, Item item) {
        if (item == null) {
            return unequip(slot);
        }

        // TODO: Add validation - check if item type matches slot
        // (e.g., can't equip a weapon to helmet slot)

        Item previousItem = equippedItems.get(slot);
        equippedItems.put(slot, item);
        return previousItem;
    }

    /**
     * Unequip an item from a specific slot.
     * @param slot The equipment slot to clear
     * @return The item that was unequipped, or null
     */
    public Item unequip(EquipmentSlot slot) {
        return equippedItems.remove(slot);
    }

    /**
     * Get the item equipped in a specific slot.
     * @param slot The equipment slot
     * @return The equipped item, or null if slot is empty
     */
    public Item getEquipped(EquipmentSlot slot) {
        return equippedItems.get(slot);
    }

    /**
     * Check if a specific slot has an item equipped.
     * @param slot The equipment slot
     * @return true if an item is equipped in this slot
     */
    public boolean isEquipped(EquipmentSlot slot) {
        return equippedItems.containsKey(slot);
    }

    /**
     * Get all equipped items.
     * @return Map of all equipped items by slot
     */
    public Map<EquipmentSlot, Item> getAllEquipped() {
        return new EnumMap<>(equippedItems);
    }

    /**
     * Calculate total stat bonuses from all equipped items.
     * @return Combined ItemStats from all equipped items
     */
    public ItemStats getTotalStats() {
        int totalDamage = 0;
        int totalArmor = 0;
        int totalDurability = 0;
        float avgAttackSpeed = 0f;
        int attackSpeedCount = 0;
        int totalStrength = 0;
        int totalDexterity = 0;
        int totalVitality = 0;
        int totalMagic = 0;

        for (Item item : equippedItems.values()) {
            if (item != null && item.getStats() != null) {
                ItemStats stats = item.getStats();
                totalDamage += stats.getDamage();
                totalArmor += stats.getArmor();
                totalDurability += stats.getDurability();
                if (stats.getAttackSpeed() > 0) {
                    avgAttackSpeed += stats.getAttackSpeed();
                    attackSpeedCount++;
                }
                totalStrength += stats.getStrength();
                totalDexterity += stats.getDexterity();
                totalVitality += stats.getVitality();
                totalMagic += stats.getMagic();
            }
        }

        // Calculate average attack speed if any weapons are equipped
        float finalAttackSpeed = attackSpeedCount > 0 ? avgAttackSpeed / attackSpeedCount : 0f;

        return new ItemStats(totalDamage, totalArmor, totalDurability, finalAttackSpeed,
                           totalStrength, totalDexterity, totalVitality, totalMagic);
    }

    /**
     * Calculate total weight of all equipped items.
     * @return Total weight
     */
    public float getTotalWeight() {
        float totalWeight = 0;
        for (Item item : equippedItems.values()) {
            if (item != null) {
                totalWeight += item.getWeight();
            }
        }
        return totalWeight;
    }

    /**
     * Clear all equipped items.
     */
    public void clearAll() {
        equippedItems.clear();
    }
}
