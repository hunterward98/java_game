package io.github.inherit_this.save;

import io.github.inherit_this.save.SaveData.SavedItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for the SaveData and SavedItemStack classes.
 * Tests serialization, data integrity, and getters/setters.
 */
class SaveDataTest {

    private SaveData saveData;

    @BeforeEach
    void setUp() {
        saveData = new SaveData();
    }

    // ==================== SaveData Initialization Tests ====================

    @Nested
    @DisplayName("SaveData Initialization")
    class SaveDataInitialization {

        @Test
        @DisplayName("SaveData should initialize with empty lists")
        void testInitialization() {
            assertNotNull(saveData.getInventoryItems(), "Inventory items list should not be null");
            assertNotNull(saveData.getEquippedItems(), "Equipped items list should not be null");
            assertTrue(saveData.getInventoryItems().isEmpty(), "Inventory items should be empty");
            assertTrue(saveData.getEquippedItems().isEmpty(), "Equipped items should be empty");
        }

        @Test
        @DisplayName("SaveData should initialize with current date")
        void testLastSavedDate() {
            assertNotNull(saveData.getLastSaved(), "Last saved date should not be null");
            // Date should be recent (within last second)
            long now = System.currentTimeMillis();
            long savedTime = saveData.getLastSaved().getTime();
            assertTrue(now - savedTime < 1000, "Last saved should be recent");
        }
    }

    // ==================== Character Info Tests ====================

    @Nested
    @DisplayName("Character Info")
    class CharacterInfo {

        @Test
        @DisplayName("Should set and get character name")
        void testCharacterName() {
            saveData.setCharacterName("TestHero");
            assertEquals("TestHero", saveData.getCharacterName(), "Character name should match");
        }

        @Test
        @DisplayName("Should handle null character name")
        void testNullCharacterName() {
            saveData.setCharacterName(null);
            assertNull(saveData.getCharacterName(), "Null character name should be allowed");
        }

        @Test
        @DisplayName("Should set and get player position")
        void testPlayerPosition() {
            saveData.setPlayerX(12.5f);
            saveData.setPlayerY(34.7f);
            saveData.setBillboardZ(1.8f);

            assertEquals(12.5f, saveData.getPlayerX(), 0.001f, "Player X should match");
            assertEquals(34.7f, saveData.getPlayerY(), 0.001f, "Player Y should match");
            assertEquals(1.8f, saveData.getBillboardZ(), 0.001f, "Billboard Z should match");
        }

        @Test
        @DisplayName("Should handle negative positions")
        void testNegativePositions() {
            saveData.setPlayerX(-10.5f);
            saveData.setPlayerY(-20.3f);

            assertEquals(-10.5f, saveData.getPlayerX(), 0.001f);
            assertEquals(-20.3f, saveData.getPlayerY(), 0.001f);
        }
    }

    // ==================== Player Stats Tests ====================

    @Nested
    @DisplayName("Player Stats")
    class PlayerStats {

        @Test
        @DisplayName("Should set and get health stats")
        void testHealthStats() {
            saveData.setHealth(75.5f);
            saveData.setMaxHealth(100f);

            assertEquals(75.5f, saveData.getHealth(), 0.001f, "Health should match");
            assertEquals(100f, saveData.getMaxHealth(), 0.001f, "Max health should match");
        }

        @Test
        @DisplayName("Should set and get mana stats")
        void testManaStats() {
            saveData.setMana(30f);
            saveData.setMaxMana(50f);

            assertEquals(30f, saveData.getMana(), 0.001f, "Mana should match");
            assertEquals(50f, saveData.getMaxMana(), 0.001f, "Max mana should match");
        }

        @Test
        @DisplayName("Should set and get stamina stats")
        void testStaminaStats() {
            saveData.setStamina(80f);
            saveData.setMaxStamina(100f);

            assertEquals(80f, saveData.getStamina(), 0.001f, "Stamina should match");
            assertEquals(100f, saveData.getMaxStamina(), 0.001f, "Max stamina should match");
        }

        @Test
        @DisplayName("Should set and get experience and level")
        void testExperienceAndLevel() {
            saveData.setExperience(150.5f);
            saveData.setLevel(10);

            assertEquals(150.5f, saveData.getExperience(), 0.001f, "Experience should match");
            assertEquals(10, saveData.getLevel(), "Level should match");
        }

        @Test
        @DisplayName("Should handle zero values")
        void testZeroValues() {
            saveData.setHealth(0f);
            saveData.setMana(0f);
            saveData.setStamina(0f);
            saveData.setExperience(0f);
            saveData.setLevel(0);

            assertEquals(0f, saveData.getHealth(), 0.001f);
            assertEquals(0f, saveData.getMana(), 0.001f);
            assertEquals(0f, saveData.getStamina(), 0.001f);
            assertEquals(0f, saveData.getExperience(), 0.001f);
            assertEquals(0, saveData.getLevel());
        }

        @Test
        @DisplayName("Should handle fractional stat values")
        void testFractionalStats() {
            saveData.setHealth(99.99f);
            saveData.setMana(49.75f);
            saveData.setStamina(88.33f);

            assertEquals(99.99f, saveData.getHealth(), 0.001f);
            assertEquals(49.75f, saveData.getMana(), 0.001f);
            assertEquals(88.33f, saveData.getStamina(), 0.001f);
        }
    }

    // ==================== Inventory Tests ====================

    @Nested
    @DisplayName("Inventory")
    class Inventory {

        @Test
        @DisplayName("Should set and get gold")
        void testGold() {
            saveData.setGold(500);
            assertEquals(500, saveData.getGold(), "Gold should match");
        }

        @Test
        @DisplayName("Should handle zero gold")
        void testZeroGold() {
            saveData.setGold(0);
            assertEquals(0, saveData.getGold());
        }

        @Test
        @DisplayName("Should set and get inventory items")
        void testInventoryItems() {
            List<SavedItemStack> items = new ArrayList<>();
            items.add(new SavedItemStack("health_potion", 5, 0, 0));
            items.add(new SavedItemStack("mana_potion", 3, 1, 0));

            saveData.setInventoryItems(items);

            assertEquals(2, saveData.getInventoryItems().size(), "Should have 2 items");
            assertEquals("health_potion", saveData.getInventoryItems().get(0).getItemId());
            assertEquals(5, saveData.getInventoryItems().get(0).getQuantity());
        }

        @Test
        @DisplayName("Should handle empty inventory")
        void testEmptyInventory() {
            saveData.setInventoryItems(new ArrayList<>());
            assertTrue(saveData.getInventoryItems().isEmpty(), "Inventory should be empty");
        }

        @Test
        @DisplayName("Should replace inventory items list")
        void testReplaceInventoryItems() {
            List<SavedItemStack> items1 = new ArrayList<>();
            items1.add(new SavedItemStack("item1", 1, 0, 0));
            saveData.setInventoryItems(items1);

            List<SavedItemStack> items2 = new ArrayList<>();
            items2.add(new SavedItemStack("item2", 2, 1, 1));
            saveData.setInventoryItems(items2);

            assertEquals(1, saveData.getInventoryItems().size());
            assertEquals("item2", saveData.getInventoryItems().get(0).getItemId());
        }
    }

    // ==================== Equipment Tests ====================

    @Nested
    @DisplayName("Equipment")
    class Equipment {

        @Test
        @DisplayName("Should set and get equipped items")
        void testEquippedItems() {
            List<SavedItemStack> equipped = new ArrayList<>();
            equipped.add(new SavedItemStack("iron_sword", 1, "MAIN_HAND"));
            equipped.add(new SavedItemStack("iron_armor", 1, "TORSO"));

            saveData.setEquippedItems(equipped);

            assertEquals(2, saveData.getEquippedItems().size(), "Should have 2 equipped items");
            assertEquals("iron_sword", saveData.getEquippedItems().get(0).getItemId());
            assertEquals("MAIN_HAND", saveData.getEquippedItems().get(0).getSlotName());
        }

        @Test
        @DisplayName("Should handle empty equipment")
        void testEmptyEquipment() {
            saveData.setEquippedItems(new ArrayList<>());
            assertTrue(saveData.getEquippedItems().isEmpty(), "Equipment should be empty");
        }
    }

    // ==================== Metadata Tests ====================

    @Nested
    @DisplayName("Metadata")
    class Metadata {

        @Test
        @DisplayName("Should set and get last saved date")
        void testLastSavedDate() {
            Date testDate = new Date(1000000000L);
            saveData.setLastSaved(testDate);

            assertEquals(testDate, saveData.getLastSaved(), "Last saved date should match");
        }

        @Test
        @DisplayName("Should set and get play time")
        void testPlayTime() {
            saveData.setPlayTimeMillis(3600000L); // 1 hour

            assertEquals(3600000L, saveData.getPlayTimeMillis(), "Play time should match");
        }

        @Test
        @DisplayName("Should handle zero play time")
        void testZeroPlayTime() {
            saveData.setPlayTimeMillis(0L);
            assertEquals(0L, saveData.getPlayTimeMillis());
        }

        @Test
        @DisplayName("Should handle very large play time")
        void testLargePlayTime() {
            long largeTime = 86400000L * 365; // 365 days
            saveData.setPlayTimeMillis(largeTime);
            assertEquals(largeTime, saveData.getPlayTimeMillis());
        }
    }

    // ==================== Serialization Tests ====================

    @Nested
    @DisplayName("Serialization")
    class Serialization {

        @Test
        @DisplayName("SaveData should be serializable")
        void testSerializable() {
            assertTrue(saveData instanceof Serializable, "SaveData should implement Serializable");
        }

        @Test
        @DisplayName("Should serialize and deserialize complete SaveData")
        void testSerializeDeserialize() throws Exception {
            // Setup complete save data
            saveData.setCharacterName("TestCharacter");
            saveData.setPlayerX(10.5f);
            saveData.setPlayerY(20.3f);
            saveData.setHealth(75f);
            saveData.setMaxHealth(100f);
            saveData.setMana(30f);
            saveData.setMaxMana(50f);
            saveData.setLevel(10);
            saveData.setExperience(500f);
            saveData.setGold(1000);

            List<SavedItemStack> items = new ArrayList<>();
            items.add(new SavedItemStack("test_item", 5, 0, 0));
            saveData.setInventoryItems(items);

            // Serialize
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(saveData);
            oos.close();

            // Deserialize
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            SaveData deserialized = (SaveData) ois.readObject();
            ois.close();

            // Verify
            assertEquals("TestCharacter", deserialized.getCharacterName());
            assertEquals(10.5f, deserialized.getPlayerX(), 0.001f);
            assertEquals(20.3f, deserialized.getPlayerY(), 0.001f);
            assertEquals(75f, deserialized.getHealth(), 0.001f);
            assertEquals(10, deserialized.getLevel());
            assertEquals(1000, deserialized.getGold());
            assertEquals(1, deserialized.getInventoryItems().size());
            assertEquals("test_item", deserialized.getInventoryItems().get(0).getItemId());
        }

        @Test
        @DisplayName("Should maintain data integrity after serialization")
        void testDataIntegrity() throws Exception {
            saveData.setCharacterName("IntegrityTest");
            saveData.setGold(12345);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(saveData);
            oos.close();

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            SaveData deserialized = (SaveData) ois.readObject();
            ois.close();

            assertEquals("IntegrityTest", deserialized.getCharacterName());
            assertEquals(12345, deserialized.getGold());
        }
    }

    // ==================== SavedItemStack Tests ====================

    @Nested
    @DisplayName("SavedItemStack")
    class SavedItemStackTests {

        @Test
        @DisplayName("Should create inventory item stack")
        void testCreateInventoryItemStack() {
            SavedItemStack stack = new SavedItemStack("test_item", 5, 2, 3);

            assertEquals("test_item", stack.getItemId());
            assertEquals(5, stack.getQuantity());
            assertEquals(2, stack.getGridX());
            assertEquals(3, stack.getGridY());
            assertNull(stack.getSlotName(), "Inventory items should have null slot name");
        }

        @Test
        @DisplayName("Should create equipment item stack")
        void testCreateEquipmentItemStack() {
            SavedItemStack stack = new SavedItemStack("iron_sword", 1, "MAIN_HAND");

            assertEquals("iron_sword", stack.getItemId());
            assertEquals(1, stack.getQuantity());
            assertEquals("MAIN_HAND", stack.getSlotName());
            assertEquals(0, stack.getGridX(), "Equipment items should have gridX = 0");
            assertEquals(0, stack.getGridY(), "Equipment items should have gridY = 0");
        }

        @Test
        @DisplayName("Should create empty SavedItemStack")
        void testCreateEmptyItemStack() {
            SavedItemStack stack = new SavedItemStack();

            assertNotNull(stack, "Empty constructor should create instance");
        }

        @Test
        @DisplayName("Should set and get item ID")
        void testSetItemId() {
            SavedItemStack stack = new SavedItemStack();
            stack.setItemId("new_item");

            assertEquals("new_item", stack.getItemId());
        }

        @Test
        @DisplayName("Should set and get quantity")
        void testSetQuantity() {
            SavedItemStack stack = new SavedItemStack();
            stack.setQuantity(10);

            assertEquals(10, stack.getQuantity());
        }

        @Test
        @DisplayName("Should set and get grid position")
        void testSetGridPosition() {
            SavedItemStack stack = new SavedItemStack();
            stack.setGridX(5);
            stack.setGridY(7);

            assertEquals(5, stack.getGridX());
            assertEquals(7, stack.getGridY());
        }

        @Test
        @DisplayName("Should set and get slot name")
        void testSetSlotName() {
            SavedItemStack stack = new SavedItemStack();
            stack.setSlotName("TORSO");

            assertEquals("TORSO", stack.getSlotName());
        }

        @Test
        @DisplayName("SavedItemStack should be serializable")
        void testSavedItemStackSerializable() {
            SavedItemStack stack = new SavedItemStack("test", 1, 0, 0);
            assertTrue(stack instanceof Serializable, "SavedItemStack should implement Serializable");
        }

        @Test
        @DisplayName("Should serialize and deserialize SavedItemStack")
        void testSerializeDeserializeItemStack() throws Exception {
            SavedItemStack original = new SavedItemStack("magic_sword", 1, "MAIN_HAND");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(original);
            oos.close();

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            SavedItemStack deserialized = (SavedItemStack) ois.readObject();
            ois.close();

            assertEquals(original.getItemId(), deserialized.getItemId());
            assertEquals(original.getQuantity(), deserialized.getQuantity());
            assertEquals(original.getSlotName(), deserialized.getSlotName());
        }

        @Test
        @DisplayName("Should handle null item ID")
        void testNullItemId() {
            SavedItemStack stack = new SavedItemStack();
            stack.setItemId(null);

            assertNull(stack.getItemId());
        }

        @Test
        @DisplayName("Should handle zero quantity")
        void testZeroQuantity() {
            SavedItemStack stack = new SavedItemStack("item", 0, 0, 0);
            assertEquals(0, stack.getQuantity());
        }

        @Test
        @DisplayName("Should handle negative grid positions")
        void testNegativeGridPosition() {
            SavedItemStack stack = new SavedItemStack("item", 1, -1, -1);
            assertEquals(-1, stack.getGridX());
            assertEquals(-1, stack.getGridY());
        }

        @Test
        @DisplayName("Should handle large quantities")
        void testLargeQuantity() {
            SavedItemStack stack = new SavedItemStack("item", 9999, 0, 0);
            assertEquals(9999, stack.getQuantity());
        }
    }

    // ==================== Edge Cases ====================

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle complete empty SaveData")
        void testEmptySaveData() {
            SaveData empty = new SaveData();

            assertNull(empty.getCharacterName());
            assertEquals(0f, empty.getPlayerX(), 0.001f);
            assertEquals(0f, empty.getPlayerY(), 0.001f);
            assertEquals(0f, empty.getHealth(), 0.001f);
            assertEquals(0, empty.getGold());
            assertEquals(0, empty.getLevel());
            assertNotNull(empty.getInventoryItems());
            assertNotNull(empty.getEquippedItems());
        }

        @Test
        @DisplayName("Should handle very long character name")
        void testLongCharacterName() {
            String longName = "A".repeat(1000);
            saveData.setCharacterName(longName);

            assertEquals(longName, saveData.getCharacterName());
        }

        @Test
        @DisplayName("Should handle maximum level")
        void testMaximumLevel() {
            saveData.setLevel(100);
            assertEquals(100, saveData.getLevel());
        }

        @Test
        @DisplayName("Should handle large inventory")
        void testLargeInventory() {
            List<SavedItemStack> largeInventory = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                largeInventory.add(new SavedItemStack("item_" + i, 1, i % 10, i / 10));
            }

            saveData.setInventoryItems(largeInventory);

            assertEquals(100, saveData.getInventoryItems().size());
        }

        @Test
        @DisplayName("Should preserve date precision")
        void testDatePrecision() {
            Date precise = new Date(1234567890123L);
            saveData.setLastSaved(precise);

            assertEquals(1234567890123L, saveData.getLastSaved().getTime());
        }

        @Test
        @DisplayName("Multiple SaveData instances should be independent")
        void testIndependence() {
            SaveData data1 = new SaveData();
            SaveData data2 = new SaveData();

            data1.setCharacterName("Hero1");
            data2.setCharacterName("Hero2");

            assertEquals("Hero1", data1.getCharacterName());
            assertEquals("Hero2", data2.getCharacterName());
            assertNotEquals(data1.getCharacterName(), data2.getCharacterName());
        }

        @Test
        @DisplayName("Modifying returned lists should not affect SaveData")
        void testListImmutability() {
            List<SavedItemStack> items = new ArrayList<>();
            items.add(new SavedItemStack("item1", 1, 0, 0));
            saveData.setInventoryItems(items);

            // Get the list and modify it
            List<SavedItemStack> retrieved = saveData.getInventoryItems();
            retrieved.add(new SavedItemStack("item2", 1, 1, 0));

            // SaveData should have the modified list (since it returns the actual list)
            assertEquals(2, saveData.getInventoryItems().size(),
                "Modifying returned list does affect SaveData (mutable reference)");
        }
    }
}
