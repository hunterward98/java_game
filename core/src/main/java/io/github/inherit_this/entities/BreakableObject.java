package io.github.inherit_this.entities;

import com.badlogic.gdx.graphics.Texture;
import io.github.inherit_this.items.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a breakable object in the game world (crate, pot, etc).
 * Can be clicked to damage and eventually break, dropping loot.
 */
public class BreakableObject extends Entity {

    private int maxHealth;
    private int currentHealth;
    private List<LootDrop> lootTable;
    private int goldMin;
    private int goldMax;
    private boolean destroyed;

    /**
     * Creates a breakable object.
     * @param texture The texture for this object
     * @param x X position in tiles
     * @param y Y position in tiles
     * @param maxHealth How many hits it takes to break
     * @param goldMin Minimum gold dropped
     * @param goldMax Maximum gold dropped
     */
    public BreakableObject(Texture texture, float x, float y, int maxHealth, int goldMin, int goldMax) {
        super(texture, x, y);
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.goldMin = goldMin;
        this.goldMax = goldMax;
        this.lootTable = new ArrayList<>();
        this.destroyed = false;
    }

    /**
     * Adds an item to the loot table with a drop chance.
     * @param item The item to drop
     * @param minQuantity Minimum quantity to drop
     * @param maxQuantity Maximum quantity to drop
     * @param dropChance Chance to drop (0.0 - 1.0, where 1.0 = 100%)
     */
    public void addLoot(Item item, int minQuantity, int maxQuantity, float dropChance) {
        lootTable.add(new LootDrop(item, minQuantity, maxQuantity, dropChance));
    }

    /**
     * Damages the object, reducing its health.
     * @param damage Amount of damage to deal
     * @return true if the object was destroyed by this damage
     */
    public boolean damage(int damage) {
        if (destroyed) {
            return false;
        }

        currentHealth -= damage;
        if (currentHealth <= 0) {
            currentHealth = 0;
            destroyed = true;
            return true;
        }
        return false;
    }

    /**
     * Gets the loot that should be dropped when this object breaks.
     * @return List of items and quantities to drop
     */
    public List<LootResult> generateLoot() {
        List<LootResult> results = new ArrayList<>();

        // Add gold
        int gold = goldMin + (int)(Math.random() * (goldMax - goldMin + 1));
        if (gold > 0) {
            results.add(new LootResult(null, 0, gold));
        }

        // Roll for item drops
        for (LootDrop drop : lootTable) {
            if (Math.random() < drop.dropChance) {
                int quantity = drop.minQuantity + (int)(Math.random() * (drop.maxQuantity - drop.minQuantity + 1));
                results.add(new LootResult(drop.item, quantity, 0));
            }
        }

        return results;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public float getHealthPercent() {
        return (float) currentHealth / maxHealth;
    }

    /**
     * Checks if a world position (in tiles) is within this object's bounds.
     */
    public boolean contains(float worldX, float worldY) {
        return worldX >= position.x && worldX < position.x + 1 &&
               worldY >= position.y && worldY < position.y + 1;
    }

    /**
     * Internal class to store loot table entries.
     */
    private static class LootDrop {
        Item item;
        int minQuantity;
        int maxQuantity;
        float dropChance;

        LootDrop(Item item, int minQuantity, int maxQuantity, float dropChance) {
            this.item = item;
            this.minQuantity = minQuantity;
            this.maxQuantity = maxQuantity;
            this.dropChance = dropChance;
        }
    }

    /**
     * Result of loot generation.
     */
    public static class LootResult {
        public final Item item;      // null if this is gold
        public final int quantity;   // Item quantity
        public final int gold;       // Gold amount

        public LootResult(Item item, int quantity, int gold) {
            this.item = item;
            this.quantity = quantity;
            this.gold = gold;
        }

        public boolean isGold() {
            return item == null && gold > 0;
        }

        public boolean isItem() {
            return item != null && quantity > 0;
        }
    }
}
