package io.github.inherit_this.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import io.github.inherit_this.entities.Player;
import io.github.inherit_this.items.Equipment;
import io.github.inherit_this.items.Inventory;
import io.github.inherit_this.items.ItemRegistry;
import io.github.inherit_this.items.ItemStack;
import io.github.inherit_this.save.SaveData.SavedItemStack;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Manages saving and loading game states.
 * Supports up to 3 save slots.
 */
public class SaveManager {
    private static final int MAX_SAVE_SLOTS = 3;
    private static final String SAVE_DIR = "saves/";
    private static final String SAVE_FILE_PREFIX = "save_";
    private static final String SAVE_FILE_EXTENSION = ".dat";

    /**
     * Saves the current game state to the specified slot.
     */
    public static boolean saveGame(Player player, String characterName, int slot, long playTimeMillis) {
        if (slot < 0 || slot >= MAX_SAVE_SLOTS) {
            Gdx.app.error("SaveManager", "Invalid save slot: " + slot);
            return false;
        }

        try {
            SaveData data = new SaveData();
            data.setCharacterName(characterName);
            // Save player position in tile coordinates
            data.setPlayerX(player.getPosition().x);  // tiles, not pixels
            data.setPlayerY(player.getPosition().y);  // tiles, not pixels

            // Save stats
            data.setHealth(player.getStats().getCurrentHealth());
            data.setMaxHealth(player.getStats().getMaxHealth());
            data.setMana(player.getStats().getCurrentMana());
            data.setMaxMana(player.getStats().getMaxMana());
            data.setStamina(player.getStats().getCurrentStamina());
            data.setMaxStamina(player.getStats().getMaxStamina());
            data.setExperience(player.getStats().getCurrentXP());
            data.setLevel(player.getStats().getLevel());

            // Save inventory
            int goldAmount = player.getInventory().getGold();
            data.setGold(goldAmount);
            Gdx.app.log("SaveManager", "Saving gold: " + goldAmount);
            data.setInventoryItems(serializeInventory(player.getInventory()));

            // Save equipment
            data.setEquippedItems(serializeEquipment(player.getEquipment()));

            // Save metadata
            data.setLastSaved(new Date());
            data.setPlayTimeMillis(playTimeMillis);

            // Write to file
            FileHandle file = Gdx.files.local(SAVE_DIR + SAVE_FILE_PREFIX + slot + SAVE_FILE_EXTENSION);
            try (ObjectOutputStream oos = new ObjectOutputStream(file.write(false))) {
                oos.writeObject(data);
            }

            Gdx.app.log("SaveManager", "Game saved to slot " + slot);
            return true;
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Failed to save game", e);
            return false;
        }
    }

    /**
     * Loads a game state from the specified slot.
     */
    public static SaveData loadGame(int slot) {
        if (slot < 0 || slot >= MAX_SAVE_SLOTS) {
            Gdx.app.error("SaveManager", "Invalid save slot: " + slot);
            return null;
        }

        try {
            FileHandle file = Gdx.files.local(SAVE_DIR + SAVE_FILE_PREFIX + slot + SAVE_FILE_EXTENSION);
            if (!file.exists()) {
                return null;
            }

            try (ObjectInputStream ois = new ObjectInputStream(file.read())) {
                SaveData data = (SaveData) ois.readObject();
                Gdx.app.log("SaveManager", "Game loaded from slot " + slot);
                return data;
            }
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Failed to load game from slot " + slot, e);
            return null;
        }
    }

    /**
     * Applies loaded save data to a player.
     */
    public static void applySaveDataToPlayer(SaveData data, Player player) {
        // Apply position (saved as tile coordinates)
        player.setPosition(data.getPlayerX(), data.getPlayerY());  // tiles, not pixels

        // Apply stats
        player.getStats().setHealth(data.getHealth());
        player.getStats().setMaxHealth(data.getMaxHealth());
        player.getStats().setMana(data.getMana());
        player.getStats().setMaxMana(data.getMaxMana());
        player.getStats().setStamina(data.getStamina());
        player.getStats().setMaxStamina(data.getMaxStamina());
        player.getStats().setCurrentXP(data.getExperience());
        player.getStats().setLevel(data.getLevel());

        // Apply inventory
        player.getInventory().clear();  // This also resets gold to 0
        deserializeInventory(data.getInventoryItems(), player.getInventory());
        int goldToRestore = data.getGold();
        Gdx.app.log("SaveManager", "Restoring gold: " + goldToRestore);
        player.getInventory().addGold(goldToRestore);  // Add gold AFTER deserializing items
        Gdx.app.log("SaveManager", "Gold after restore: " + player.getInventory().getGold());

        // Apply equipment
        deserializeEquipment(data.getEquippedItems(), player.getEquipment());
    }

    /**
     * Gets metadata for a save slot without loading the full save.
     */
    public static SaveSlotInfo getSaveSlotInfo(int slot) {
        SaveData data = loadGame(slot);
        if (data == null) {
            return null;
        }
        return new SaveSlotInfo(
            data.getCharacterName(),
            data.getLevel(),
            data.getLastSaved(),
            data.getPlayTimeMillis()
        );
    }

    /**
     * Checks if a save exists in the given slot.
     */
    public static boolean saveExists(int slot) {
        if (slot < 0 || slot >= MAX_SAVE_SLOTS) {
            return false;
        }
        FileHandle file = Gdx.files.local(SAVE_DIR + SAVE_FILE_PREFIX + slot + SAVE_FILE_EXTENSION);
        return file.exists();
    }

    /**
     * Deletes a save from the given slot.
     */
    public static boolean deleteSave(int slot) {
        if (slot < 0 || slot >= MAX_SAVE_SLOTS) {
            return false;
        }
        try {
            FileHandle file = Gdx.files.local(SAVE_DIR + SAVE_FILE_PREFIX + slot + SAVE_FILE_EXTENSION);
            if (file.exists()) {
                file.delete();
                Gdx.app.log("SaveManager", "Deleted save in slot " + slot);
                return true;
            }
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Failed to delete save in slot " + slot, e);
        }
        return false;
    }

    /**
     * Finds the appropriate save slot for a character.
     * Logic:
     * 1. If a save already exists for this character name, return that slot
     * 2. If no save exists for this character, return the first empty slot
     * 3. If all slots are full with different characters, return the oldest save slot
     */
    public static int findSlotForCharacter(String characterName) {
        int firstEmptySlot = -1;
        int oldestSlot = -1;
        Date oldestDate = null;

        for (int slot = 0; slot < MAX_SAVE_SLOTS; slot++) {
            SaveSlotInfo info = getSaveSlotInfo(slot);

            if (info == null) {
                // Empty slot found
                if (firstEmptySlot == -1) {
                    firstEmptySlot = slot;
                }
            } else {
                // Check if this save is for the same character
                if (info.characterName.equals(characterName)) {
                    Gdx.app.log("SaveManager", "Found existing save for " + characterName + " in slot " + slot);
                    return slot;
                }

                // Track oldest save
                if (oldestDate == null || info.lastSaved.before(oldestDate)) {
                    oldestDate = info.lastSaved;
                    oldestSlot = slot;
                }
            }
        }

        // No existing save for this character, use first empty slot
        if (firstEmptySlot != -1) {
            Gdx.app.log("SaveManager", "Using empty slot " + firstEmptySlot + " for " + characterName);
            return firstEmptySlot;
        }

        // All slots full, overwrite oldest
        Gdx.app.log("SaveManager", "All slots full, overwriting oldest save in slot " + oldestSlot);
        return oldestSlot;
    }

    private static List<SavedItemStack> serializeInventory(Inventory inventory) {
        List<SavedItemStack> items = new ArrayList<>();
        for (int x = 0; x < inventory.getGridWidth(); x++) {
            for (int y = 0; y < inventory.getGridHeight(); y++) {
                ItemStack stack = inventory.getItemAt(x, y);
                if (stack != null) {
                    // Only save the top-left cell of multi-cell items
                    boolean isTopLeft = true;
                    if (x > 0 && inventory.getItemAt(x - 1, y) == stack) isTopLeft = false;
                    if (y > 0 && inventory.getItemAt(x, y - 1) == stack) isTopLeft = false;

                    if (isTopLeft) {
                        items.add(new SavedItemStack(
                            stack.getItem().getId(),
                            stack.getQuantity(),
                            x,
                            y
                        ));
                    }
                }
            }
        }
        return items;
    }

    private static void deserializeInventory(List<SavedItemStack> savedItems, Inventory inventory) {
        for (SavedItemStack saved : savedItems) {
            io.github.inherit_this.items.Item item = ItemRegistry.getInstance().getItem(saved.getItemId());
            if (item != null) {
                ItemStack stack = new ItemStack(item, saved.getQuantity());
                inventory.placeItem(stack, saved.getGridX(), saved.getGridY());
            }
        }
    }

    private static List<SavedItemStack> serializeEquipment(Equipment equipment) {
        List<SavedItemStack> items = new ArrayList<>();
        Map<io.github.inherit_this.items.EquipmentSlot, io.github.inherit_this.items.Item> allEquipped = equipment.getAllEquipped();

        for (Map.Entry<io.github.inherit_this.items.EquipmentSlot, io.github.inherit_this.items.Item> entry : allEquipped.entrySet()) {
            if (entry.getValue() != null) {
                items.add(new SavedItemStack(
                    entry.getValue().getId(),
                    1,  // Equipment items don't stack, quantity is always 1
                    entry.getKey().name()  // Store slot name
                ));
            }
        }

        return items;
    }

    private static void deserializeEquipment(List<SavedItemStack> savedItems, Equipment equipment) {
        equipment.clearAll();  // Clear existing equipment

        for (SavedItemStack saved : savedItems) {
            io.github.inherit_this.items.Item item = ItemRegistry.getInstance().getItem(saved.getItemId());
            if (item != null && saved.getSlotName() != null) {
                try {
                    io.github.inherit_this.items.EquipmentSlot slot = io.github.inherit_this.items.EquipmentSlot.valueOf(saved.getSlotName());
                    equipment.equip(slot, item);
                } catch (IllegalArgumentException e) {
                    Gdx.app.error("SaveManager", "Invalid equipment slot: " + saved.getSlotName(), e);
                }
            }
        }
    }

    /**
     * Metadata about a save slot.
     */
    public static class SaveSlotInfo {
        public final String characterName;
        public final int level;
        public final Date lastSaved;
        public final long playTimeMillis;

        public SaveSlotInfo(String characterName, int level, Date lastSaved, long playTimeMillis) {
            this.characterName = characterName;
            this.level = level;
            this.lastSaved = lastSaved;
            this.playTimeMillis = playTimeMillis;
        }
    }
}
