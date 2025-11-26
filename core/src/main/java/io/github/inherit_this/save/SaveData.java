package io.github.inherit_this.save;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Holds all data for a single save file.
 * Serializable so it can be written to disk.
 */
public class SaveData implements Serializable {
    private static final long serialVersionUID = 1L;

    // Character info
    private String characterName;
    private float playerX;
    private float playerY;
    private float billboardZ;

    // Player stats
    private float health;
    private float maxHealth;
    private float mana;
    private float maxMana;
    private float stamina;
    private float maxStamina;
    private float experience;
    private int level;

    // Inventory
    private int gold;
    private List<SavedItemStack> inventoryItems;

    // Equipment
    private List<SavedItemStack> equippedItems;

    // Metadata
    private Date lastSaved;
    private long playTimeMillis;

    public SaveData() {
        inventoryItems = new ArrayList<>();
        equippedItems = new ArrayList<>();
        lastSaved = new Date();
    }

    // Getters and setters
    public String getCharacterName() { return characterName; }
    public void setCharacterName(String characterName) { this.characterName = characterName; }

    public float getPlayerX() { return playerX; }
    public void setPlayerX(float playerX) { this.playerX = playerX; }

    public float getPlayerY() { return playerY; }
    public void setPlayerY(float playerY) { this.playerY = playerY; }

    public float getBillboardZ() { return billboardZ; }
    public void setBillboardZ(float billboardZ) { this.billboardZ = billboardZ; }

    public float getHealth() { return health; }
    public void setHealth(float health) { this.health = health; }

    public float getMaxHealth() { return maxHealth; }
    public void setMaxHealth(float maxHealth) { this.maxHealth = maxHealth; }

    public float getMana() { return mana; }
    public void setMana(float mana) { this.mana = mana; }

    public float getMaxMana() { return maxMana; }
    public void setMaxMana(float maxMana) { this.maxMana = maxMana; }

    public float getStamina() { return stamina; }
    public void setStamina(float stamina) { this.stamina = stamina; }

    public float getMaxStamina() { return maxStamina; }
    public void setMaxStamina(float maxStamina) { this.maxStamina = maxStamina; }

    public float getExperience() { return experience; }
    public void setExperience(float experience) { this.experience = experience; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getGold() { return gold; }
    public void setGold(int gold) { this.gold = gold; }

    public List<SavedItemStack> getInventoryItems() { return inventoryItems; }
    public void setInventoryItems(List<SavedItemStack> inventoryItems) { this.inventoryItems = inventoryItems; }

    public List<SavedItemStack> getEquippedItems() { return equippedItems; }
    public void setEquippedItems(List<SavedItemStack> equippedItems) { this.equippedItems = equippedItems; }

    public Date getLastSaved() { return lastSaved; }
    public void setLastSaved(Date lastSaved) { this.lastSaved = lastSaved; }

    public long getPlayTimeMillis() { return playTimeMillis; }
    public void setPlayTimeMillis(long playTimeMillis) { this.playTimeMillis = playTimeMillis; }

    /**
     * Represents a saved item stack (item ID, quantity, grid position or equipment slot).
     */
    public static class SavedItemStack implements Serializable {
        private static final long serialVersionUID = 1L;

        private String itemId;
        private int quantity;
        private int gridX;
        private int gridY;
        private String slotName;  // For equipment: slot name, for inventory: null

        public SavedItemStack() {}

        public SavedItemStack(String itemId, int quantity, int gridX, int gridY) {
            this.itemId = itemId;
            this.quantity = quantity;
            this.gridX = gridX;
            this.gridY = gridY;
            this.slotName = null;
        }

        public SavedItemStack(String itemId, int quantity, String slotName) {
            this.itemId = itemId;
            this.quantity = quantity;
            this.gridX = 0;
            this.gridY = 0;
            this.slotName = slotName;
        }

        public String getItemId() { return itemId; }
        public void setItemId(String itemId) { this.itemId = itemId; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }

        public int getGridX() { return gridX; }
        public void setGridX(int gridX) { this.gridX = gridX; }

        public int getGridY() { return gridY; }
        public void setGridY(int gridY) { this.gridY = gridY; }

        public String getSlotName() { return slotName; }
        public void setSlotName(String slotName) { this.slotName = slotName; }
    }
}
