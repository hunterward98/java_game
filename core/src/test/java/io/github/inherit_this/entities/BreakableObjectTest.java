package io.github.inherit_this.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import io.github.inherit_this.items.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for BreakableObject class - 2D/3D objects, health, damage, and loot.
 */
class BreakableObjectTest {

    @Mock
    private Texture mockTexture;

    @Mock
    private Model mockModel;

    @Mock
    private Item mockItem;

    private BreakableObject breakableObject2D;
    private BreakableObject breakableObject3D;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock texture dimensions
        when(mockTexture.getWidth()).thenReturn(32);
        when(mockTexture.getHeight()).thenReturn(32);

        // Create 2D breakable object at (10, 15) with 50 health, 5-10 gold
        breakableObject2D = new BreakableObject(mockTexture, 10f, 15f, 50, 5, 10);

        // Create 3D breakable object
        breakableObject3D = new BreakableObject(mockModel, mockTexture, 20f, 25f, 100, 10, 20);
    }

    @Test
    @DisplayName("2D BreakableObject should initialize with correct values")
    void test2DInitialization() {
        assertEquals(10f, breakableObject2D.getPosition().x, "X position should be 10");
        assertEquals(15f, breakableObject2D.getPosition().y, "Y position should be 15");
        assertEquals(50, breakableObject2D.getMaxHealth(), "Max health should be 50");
        assertEquals(50, breakableObject2D.getCurrentHealth(), "Current health should be 50");
        assertFalse(breakableObject2D.isDestroyed(), "Should not be destroyed initially");
        assertFalse(breakableObject2D.is3D(), "Should be 2D object");
        assertNull(breakableObject2D.getModel(), "2D object should have no model");
        assertEquals("Object", breakableObject2D.getName(), "Default name should be 'Object'");
    }

    @Test
    @DisplayName("3D BreakableObject should initialize with correct values")
    void test3DInitialization() {
        assertEquals(20f, breakableObject3D.getPosition().x, "X position should be 20");
        assertEquals(25f, breakableObject3D.getPosition().y, "Y position should be 25");
        assertEquals(100, breakableObject3D.getMaxHealth(), "Max health should be 100");
        assertEquals(100, breakableObject3D.getCurrentHealth(), "Current health should be 100");
        assertFalse(breakableObject3D.isDestroyed(), "Should not be destroyed initially");
        assertTrue(breakableObject3D.is3D(), "Should be 3D object");
        assertNotNull(breakableObject3D.getModel(), "3D object should have a model");
        assertEquals(mockModel, breakableObject3D.getModel(), "Model should match");
    }

    @Test
    @DisplayName("Damage should reduce current health")
    void testDamage() {
        boolean destroyed = breakableObject2D.damage(20);

        assertEquals(30, breakableObject2D.getCurrentHealth(),
                "Health should be reduced by damage amount");
        assertFalse(destroyed, "Object should not be destroyed");
        assertFalse(breakableObject2D.isDestroyed(), "Object should not be destroyed");
    }

    @Test
    @DisplayName("Damage should destroy object when health reaches zero")
    void testDestroyOnZeroHealth() {
        boolean destroyed = breakableObject2D.damage(50);

        assertTrue(destroyed, "Damage should return true when object is destroyed");
        assertTrue(breakableObject2D.isDestroyed(), "Object should be destroyed");
        assertEquals(0, breakableObject2D.getCurrentHealth(),
                "Health should be 0 when destroyed");
    }

    @Test
    @DisplayName("Damage should destroy object when health goes below zero")
    void testDestroyOnOverkill() {
        boolean destroyed = breakableObject2D.damage(100);

        assertTrue(destroyed, "Object should be destroyed");
        assertEquals(0, breakableObject2D.getCurrentHealth(),
                "Health should not go below 0");
        assertTrue(breakableObject2D.isDestroyed(), "Object should be destroyed");
    }

    @Test
    @DisplayName("Damage should not affect already destroyed object")
    void testDamageDestroyedObject() {
        breakableObject2D.damage(50); // Destroy it
        assertTrue(breakableObject2D.isDestroyed(), "Object should be destroyed");

        boolean result = breakableObject2D.damage(10);

        assertFalse(result, "Damaging destroyed object should return false");
        assertEquals(0, breakableObject2D.getCurrentHealth(),
                "Health should remain 0");
    }

    @Test
    @DisplayName("GetHealthPercent should calculate correctly")
    void testGetHealthPercent() {
        assertEquals(1.0f, breakableObject2D.getHealthPercent(),
                "Full health should be 100%");

        breakableObject2D.damage(25);
        assertEquals(0.5f, breakableObject2D.getHealthPercent(), 0.01f,
                "Half health should be 50%");

        breakableObject2D.damage(25);
        assertEquals(0.0f, breakableObject2D.getHealthPercent(),
                "Zero health should be 0%");
    }

    @Test
    @DisplayName("SetName should update object name")
    void testSetName() {
        breakableObject2D.setName("Wooden Crate");
        assertEquals("Wooden Crate", breakableObject2D.getName(),
                "Name should be updated");
    }

    @Test
    @DisplayName("SetXPReward and GetXPReward should work correctly")
    void testXPReward() {
        assertEquals(0, breakableObject2D.getXPReward(),
                "Default XP reward should be 0");

        breakableObject2D.setXPReward(50);
        assertEquals(50, breakableObject2D.getXPReward(),
                "XP reward should be updated");
    }

    @Test
    @DisplayName("AddLoot should add items to loot table")
    void testAddLoot() {
        when(mockItem.getName()).thenReturn("Health Potion");

        // Add item with 100% drop chance
        breakableObject2D.addLoot(mockItem, 1, 3, 1.0f);

        // Generate loot multiple times to verify item drops
        List<BreakableObject.LootResult> loot = breakableObject2D.generateLoot();

        // Should have gold and item (with 100% drop chance)
        boolean hasItem = loot.stream().anyMatch(BreakableObject.LootResult::isItem);
        assertTrue(hasItem, "Loot should contain the added item");
    }

    @Test
    @DisplayName("GenerateLoot should include gold")
    void testGenerateLootGold() {
        List<BreakableObject.LootResult> loot = breakableObject2D.generateLoot();

        // Should have at least one gold entry
        boolean hasGold = loot.stream().anyMatch(BreakableObject.LootResult::isGold);
        assertTrue(hasGold, "Loot should contain gold");

        // Gold amount should be in range
        int goldAmount = loot.stream()
                .filter(BreakableObject.LootResult::isGold)
                .mapToInt(l -> l.gold)
                .sum();

        assertTrue(goldAmount >= 5 && goldAmount <= 10,
                "Gold amount should be between 5 and 10");
    }

    @Test
    @DisplayName("GenerateLoot should include XP when set")
    void testGenerateLootXP() {
        breakableObject2D.setXPReward(25);

        List<BreakableObject.LootResult> loot = breakableObject2D.generateLoot();

        boolean hasXP = loot.stream().anyMatch(BreakableObject.LootResult::isXP);
        assertTrue(hasXP, "Loot should contain XP");

        int xpAmount = loot.stream()
                .filter(BreakableObject.LootResult::isXP)
                .mapToInt(l -> l.xp)
                .sum();

        assertEquals(25, xpAmount, "XP amount should match");
    }

    @Test
    @DisplayName("GenerateLoot should not include XP when reward is 0")
    void testGenerateLootNoXP() {
        breakableObject2D.setXPReward(0);

        List<BreakableObject.LootResult> loot = breakableObject2D.generateLoot();

        boolean hasXP = loot.stream().anyMatch(BreakableObject.LootResult::isXP);
        assertFalse(hasXP, "Loot should not contain XP when reward is 0");
    }

    @Test
    @DisplayName("GenerateLoot should respect drop chance")
    void testGenerateLootDropChance() {
        // Add item with 0% drop chance
        breakableObject2D.addLoot(mockItem, 1, 1, 0.0f);

        // Generate loot multiple times
        for (int i = 0; i < 10; i++) {
            List<BreakableObject.LootResult> loot = breakableObject2D.generateLoot();
            boolean hasItem = loot.stream().anyMatch(BreakableObject.LootResult::isItem);
            assertFalse(hasItem, "Item with 0% drop chance should never drop");
        }
    }

    @Test
    @DisplayName("GenerateLoot should generate random quantities within range")
    void testGenerateLootQuantity() {
        // Add item with 100% drop chance and quantity 5-10
        breakableObject2D.addLoot(mockItem, 5, 10, 1.0f);

        List<BreakableObject.LootResult> loot = breakableObject2D.generateLoot();

        int itemQuantity = loot.stream()
                .filter(BreakableObject.LootResult::isItem)
                .mapToInt(l -> l.quantity)
                .sum();

        assertTrue(itemQuantity >= 5 && itemQuantity <= 10,
                "Item quantity should be between 5 and 10");
    }

    @Test
    @DisplayName("Contains should return true for positions inside object bounds")
    void testContainsInside() {
        assertTrue(breakableObject2D.contains(10.0f, 15.0f),
                "Should contain position at object's tile");
        assertTrue(breakableObject2D.contains(10.5f, 15.5f),
                "Should contain position in center of tile");
        assertTrue(breakableObject2D.contains(10.9f, 15.9f),
                "Should contain position at edge of tile");
    }

    @Test
    @DisplayName("Contains should return false for positions outside object bounds")
    void testContainsOutside() {
        assertFalse(breakableObject2D.contains(9.9f, 15.0f),
                "Should not contain position to the left");
        assertFalse(breakableObject2D.contains(11.0f, 15.0f),
                "Should not contain position to the right");
        assertFalse(breakableObject2D.contains(10.0f, 14.9f),
                "Should not contain position below");
        assertFalse(breakableObject2D.contains(10.0f, 16.0f),
                "Should not contain position above");
    }

    @Test
    @DisplayName("LootResult should correctly identify gold")
    void testLootResultIsGold() {
        BreakableObject.LootResult goldResult = new BreakableObject.LootResult(null, 0, 10, 0);
        assertTrue(goldResult.isGold(), "Should be identified as gold");
        assertFalse(goldResult.isItem(), "Should not be identified as item");
        assertFalse(goldResult.isXP(), "Should not be identified as XP");
    }

    @Test
    @DisplayName("LootResult should correctly identify item")
    void testLootResultIsItem() {
        BreakableObject.LootResult itemResult = new BreakableObject.LootResult(mockItem, 5, 0, 0);
        assertTrue(itemResult.isItem(), "Should be identified as item");
        assertFalse(itemResult.isGold(), "Should not be identified as gold");
        assertFalse(itemResult.isXP(), "Should not be identified as XP");
    }

    @Test
    @DisplayName("LootResult should correctly identify XP")
    void testLootResultIsXP() {
        BreakableObject.LootResult xpResult = new BreakableObject.LootResult(null, 0, 0, 25);
        assertTrue(xpResult.isXP(), "Should be identified as XP");
        assertFalse(xpResult.isGold(), "Should not be identified as gold");
        assertFalse(xpResult.isItem(), "Should not be identified as item");
    }

    @Test
    @DisplayName("LootResult legacy constructor should work")
    void testLootResultLegacyConstructor() {
        BreakableObject.LootResult result = new BreakableObject.LootResult(mockItem, 3, 50);
        assertEquals(mockItem, result.item, "Item should match");
        assertEquals(3, result.quantity, "Quantity should match");
        assertEquals(50, result.gold, "Gold should match");
        assertEquals(0, result.xp, "XP should default to 0");
    }

    @Test
    @DisplayName("Multiple damage calls should accumulate")
    void testMultipleDamage() {
        breakableObject2D.damage(10);
        assertEquals(40, breakableObject2D.getCurrentHealth(),
                "Health should be 40 after first damage");

        breakableObject2D.damage(15);
        assertEquals(25, breakableObject2D.getCurrentHealth(),
                "Health should be 25 after second damage");

        breakableObject2D.damage(25);
        assertEquals(0, breakableObject2D.getCurrentHealth(),
                "Health should be 0 after final damage");
        assertTrue(breakableObject2D.isDestroyed(), "Object should be destroyed");
    }

    @Test
    @DisplayName("Object with zero gold range should generate no gold")
    void testZeroGoldRange() {
        BreakableObject noGold = new BreakableObject(mockTexture, 0f, 0f, 10, 0, 0);

        List<BreakableObject.LootResult> loot = noGold.generateLoot();

        boolean hasGold = loot.stream()
                .filter(BreakableObject.LootResult::isGold)
                .anyMatch(l -> l.gold > 0);

        assertFalse(hasGold, "Should not generate gold when range is 0-0");
    }

    @Test
    @DisplayName("High health object should require multiple hits to destroy")
    void testHighHealthObject() {
        BreakableObject sturdy = new BreakableObject(mockTexture, 0f, 0f, 1000, 0, 0);

        for (int i = 0; i < 99; i++) {
            boolean destroyed = sturdy.damage(10);
            assertFalse(destroyed, "Object should not be destroyed yet");
            assertFalse(sturdy.isDestroyed(), "Object should not be destroyed yet");
        }

        boolean destroyed = sturdy.damage(10);
        assertTrue(destroyed, "Object should be destroyed on final hit");
        assertTrue(sturdy.isDestroyed(), "Object should be destroyed");
    }

    @Test
    @DisplayName("Adding multiple loot items should all be tracked")
    void testMultipleLootItems() {
        Item item1 = mock(Item.class);
        Item item2 = mock(Item.class);
        Item item3 = mock(Item.class);

        // Add multiple items with 100% drop chance
        breakableObject2D.addLoot(item1, 1, 1, 1.0f);
        breakableObject2D.addLoot(item2, 1, 1, 1.0f);
        breakableObject2D.addLoot(item3, 1, 1, 1.0f);

        List<BreakableObject.LootResult> loot = breakableObject2D.generateLoot();

        long itemCount = loot.stream().filter(BreakableObject.LootResult::isItem).count();
        assertEquals(3, itemCount, "Should have 3 items in loot");
    }
}
