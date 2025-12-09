package io.github.inherit_this.loot;

import io.github.inherit_this.items.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a generated loot table with scaled gold, XP, and item drops.
 */
public class ScaledLootTable {

    private int goldMin;
    private int goldMax;
    private int xp;
    private final List<ItemDrop> itemDrops;

    public ScaledLootTable() {
        this.itemDrops = new ArrayList<>();
        this.goldMin = 0;
        this.goldMax = 0;
        this.xp = 0;
    }

    /**
     * Add an item drop to the loot table.
     */
    public void addItemDrop(Item item, int minQuantity, int maxQuantity, float dropChance) {
        itemDrops.add(new ItemDrop(item, minQuantity, maxQuantity, dropChance));
    }

    // Getters and setters
    public int getGoldMin() { return goldMin; }
    public void setGoldMin(int goldMin) { this.goldMin = goldMin; }

    public int getGoldMax() { return goldMax; }
    public void setGoldMax(int goldMax) { this.goldMax = goldMax; }

    public int getXP() { return xp; }
    public void setXP(int xp) { this.xp = xp; }

    public List<ItemDrop> getItemDrops() { return itemDrops; }

    /**
     * Represents a single item drop in the loot table.
     */
    public static class ItemDrop {
        private final Item item;
        private final int minQuantity;
        private final int maxQuantity;
        private final float dropChance;

        public ItemDrop(Item item, int minQuantity, int maxQuantity, float dropChance) {
            this.item = item;
            this.minQuantity = minQuantity;
            this.maxQuantity = maxQuantity;
            this.dropChance = dropChance;
        }

        public Item getItem() { return item; }
        public int getMinQuantity() { return minQuantity; }
        public int getMaxQuantity() { return maxQuantity; }
        public float getDropChance() { return dropChance; }
    }
}
