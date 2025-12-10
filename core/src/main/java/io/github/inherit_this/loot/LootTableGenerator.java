package io.github.inherit_this.loot;

import io.github.inherit_this.items.Item;
import io.github.inherit_this.items.ItemRegistry;
import io.github.inherit_this.items.ItemRarity;
import io.github.inherit_this.items.ItemType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generates scaled loot tables for breakable objects and chests based on
 * player level, dungeon level, and object type.
 * Similar to FATE's loot scaling system.
 */
public class LootTableGenerator {

    private static LootTableGenerator instance;
    private final ItemRegistry itemRegistry;
    private final Random random;

    private LootTableGenerator() {
        this.itemRegistry = ItemRegistry.getInstance();
        this.random = new Random();
    }

    public static LootTableGenerator getInstance() {
        if (instance == null) {
            instance = new LootTableGenerator();
        }
        return instance;
    }

    /**
     * Generates a scaled loot table for an object.
     * @param playerLevel Current player level (1-100)
     * @param dungeonLevel Current dungeon level (0 if overworld, 1-100+ in dungeons)
     * @param objectType Type of object (POT, CRATE, BARREL, CHEST)
     * @return Generated loot table with scaled rewards
     */
    public ScaledLootTable generateLootTable(int playerLevel, int dungeonLevel, ObjectType objectType) {
        ScaledLootTable table = new ScaledLootTable();

        // Effective level is average of player and dungeon level (dungeon has more weight)
        int effectiveLevel = calculateEffectiveLevel(playerLevel, dungeonLevel);

        // Scale gold and XP based on effective level and object type
        int goldMin = calculateGoldMin(effectiveLevel, objectType);
        int goldMax = calculateGoldMax(effectiveLevel, objectType);
        int xpAmount = calculateXP(effectiveLevel, objectType);

        table.setGoldMin(goldMin);
        table.setGoldMax(goldMax);
        table.setXP(xpAmount);

        // Add item drops based on object type and level
        addItemDrops(table, effectiveLevel, objectType);

        return table;
    }

    /**
     * Calculates effective level for loot scaling.
     * Dungeon level has more weight (70/30 split).
     */
    private int calculateEffectiveLevel(int playerLevel, int dungeonLevel) {
        if (dungeonLevel == 0) {
            // Overworld - use player level only
            return playerLevel;
        }
        // In dungeon - weight dungeon level more heavily
        return (int) (playerLevel * 0.3 + dungeonLevel * 0.7);
    }

    /**
     * Calculate minimum gold drop based on level and object type.
     */
    private int calculateGoldMin(int effectiveLevel, ObjectType objectType) {
        int baseGold = objectType.getBaseGold();
        // Scale: base * (1 + level * 0.15)
        return (int) (baseGold * (1 + effectiveLevel * 0.15));
    }

    /**
     * Calculate maximum gold drop based on level and object type.
     */
    private int calculateGoldMax(int effectiveLevel, ObjectType objectType) {
        int baseGold = objectType.getBaseGold();
        // Max is 2-4x min depending on object type
        float multiplier = objectType.getGoldVariance();
        return (int) (baseGold * (1 + effectiveLevel * 0.15) * multiplier);
    }

    /**
     * Calculate XP reward based on level and object type.
     */
    private int calculateXP(int effectiveLevel, ObjectType objectType) {
        int baseXP = objectType.getBaseXP();
        // Scale: base * (1 + level * 0.1)
        return (int) (baseXP * (1 + effectiveLevel * 0.12));
    }

    /**
     * Adds item drops to the loot table based on effective level.
     */
    private void addItemDrops(ScaledLootTable table, int effectiveLevel, ObjectType objectType) {
        // Get all items from registry
        List<Item> allItems = new ArrayList<>(itemRegistry.getAllItems().values());

        // Filter items by level range (items within ±5 levels of effective level)
        List<Item> validItems = new ArrayList<>();
        for (Item item : allItems) {
            int itemLevel = getItemLevel(item);
            if (Math.abs(itemLevel - effectiveLevel) <= 5) {
                validItems.add(item);
            }
        }

        // Add drops based on object type
        switch (objectType) {
            case POT:
                // Pots: Low chance for consumables only
                addConsumableDrops(table, validItems, 0.15f, 1, 1);
                break;

            case CRATE:
                // Crates: Materials and low-tier equipment
                addMaterialDrops(table, validItems, 0.4f, 1, 3);
                addEquipmentDrops(table, validItems, 0.2f, 1, 1);
                addConsumableDrops(table, validItems, 0.25f, 1, 2);
                break;

            case BARREL:
                // Barrels: Food and materials
                addConsumableDrops(table, validItems, 0.35f, 1, 3);
                addMaterialDrops(table, validItems, 0.3f, 1, 2);
                break;

            case CHEST:
                // Chests: Higher chance for better loot
                addEquipmentDrops(table, validItems, 0.6f, 1, 2);
                addConsumableDrops(table, validItems, 0.5f, 2, 4);
                addMaterialDrops(table, validItems, 0.4f, 2, 5);
                break;
        }
    }

    /**
     * Determine an item's level based on its value.
     * Items with higher gold value are considered higher level.
     */
    private int getItemLevel(Item item) {
        int value = item.getValue();

        // Rough formula: level = sqrt(value / 10)
        // This means:
        // 10 gold = level 1
        // 40 gold = level 2
        // 90 gold = level 3
        // 1000 gold = level 10
        // 10000 gold = level 31
        int level = (int) Math.sqrt(value / 10.0);
        return Math.max(1, Math.min(100, level));
    }

    /**
     * Add consumable items (potions, food) to loot table.
     */
    private void addConsumableDrops(ScaledLootTable table, List<Item> validItems,
                                    float baseChance, int minQty, int maxQty) {
        for (Item item : validItems) {
            if (item.getType() == ItemType.CONSUMABLE) {
                // Adjust chance based on rarity
                float chance = baseChance * getRarityMultiplier(item.getRarity());
                table.addItemDrop(item, minQty, maxQty, chance);
            }
        }
    }

    /**
     * Add material items (wood, ore, etc.) to loot table.
     */
    private void addMaterialDrops(ScaledLootTable table, List<Item> validItems,
                                  float baseChance, int minQty, int maxQty) {
        for (Item item : validItems) {
            if (item.getType() == ItemType.MATERIAL) {
                float chance = baseChance * getRarityMultiplier(item.getRarity());
                table.addItemDrop(item, minQty, maxQty, chance);
            }
        }
    }

    /**
     * Add equipment items (weapons, armor) to loot table.
     */
    private void addEquipmentDrops(ScaledLootTable table, List<Item> validItems,
                                   float baseChance, int minQty, int maxQty) {
        for (Item item : validItems) {
            if (item.isEquippable()) {
                // Equipment is rarer, adjust by rarity more heavily
                float chance = baseChance * getRarityMultiplier(item.getRarity()) * 0.5f;
                table.addItemDrop(item, minQty, maxQty, chance);
            }
        }
    }

    /**
     * Get drop chance multiplier based on item rarity.
     * Rarer items have lower drop chances.
     */
    private float getRarityMultiplier(ItemRarity rarity) {
        switch (rarity) {
            case COMMON:
                return 1.0f;
            case UNCOMMON:
                return 0.5f;
            case RARE:
                return 0.2f;
            case EPIC:
                return 0.05f;
            case LEGENDARY:
                return 0.01f;
            default:
                return 1.0f;
        }
    }

    /**
     * Enum defining object types and their base reward values.
     */
    public enum ObjectType {
        POT(5, 10, 1.5f),           // Small gold, little XP
        CRATE(10, 20, 2.0f),         // Medium gold, medium XP
        BARREL(15, 30, 2.5f),        // Good gold, good XP
        CHEST(50, 100, 3.0f);        // High gold, high XP

        private final int baseGold;
        private final int baseXP;
        private final float goldVariance;

        ObjectType(int baseGold, int baseXP, float goldVariance) {
            this.baseGold = baseGold;
            this.baseXP = baseXP;
            this.goldVariance = goldVariance;
        }

        public int getBaseGold() { return baseGold; }
        public int getBaseXP() { return baseXP; }
        public float getGoldVariance() { return goldVariance; }
    }
}
