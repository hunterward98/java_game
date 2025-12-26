package io.github.inherit_this.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for PlayerStats class - XP system, leveling, and stat management.
 */
class PlayerStatsTest {

    private PlayerStats stats;

    @BeforeEach
    void setUp() {
        stats = new PlayerStats();
    }

    @Test
    @DisplayName("Player should start at level 1 with 0 XP")
    void testInitialState() {
        assertEquals(1, stats.getLevel(), "Player should start at level 1");
        assertEquals(0, stats.getCurrentXP(), "Player should start with 0 XP");
        assertFalse(stats.isMaxLevel(), "Player should not be at max level");
    }

    @Test
    @DisplayName("Player should start with correct initial stats")
    void testInitialStats() {
        assertEquals(100f, stats.getMaxHealth(), "Starting max health should be 100");
        assertEquals(100f, stats.getCurrentHealth(), "Starting current health should be 100");
        assertEquals(50f, stats.getMaxMana(), "Starting max mana should be 50");
        assertEquals(50f, stats.getCurrentMana(), "Starting current mana should be 50");
        assertEquals(100f, stats.getMaxStamina(), "Starting max stamina should be 100");
        assertEquals(100f, stats.getCurrentStamina(), "Starting current stamina should be 100");
    }

    @Test
    @DisplayName("XP should level up player when threshold is reached")
    void testLevelUp() {
        // Level 1->2 requires 100 XP
        int levelsGained = stats.addXP(100f);

        assertEquals(1, levelsGained, "Should gain exactly 1 level");
        assertEquals(2, stats.getLevel(), "Should be level 2");
        assertEquals(0f, stats.getCurrentXP(), "XP should reset to 0 after level up");
    }

    @Test
    @DisplayName("Multiple level ups should work correctly")
    void testMultipleLevelUps() {
        // Add enough XP for multiple levels
        // Level 1->2: 100 XP
        // Level 2->3: 112 XP (100 * 1.12)
        // Total: 212 XP for 2 levels
        int levelsGained = stats.addXP(212f);

        assertEquals(2, levelsGained, "Should gain 2 levels");
        assertEquals(3, stats.getLevel(), "Should be level 3");
    }

    @Test
    @DisplayName("Stats should increase on level up")
    void testStatIncreaseOnLevelUp() {
        float initialMaxHealth = stats.getMaxHealth();
        float initialMaxMana = stats.getMaxMana();
        float initialMaxStamina = stats.getMaxStamina();

        stats.addXP(100f); // Level up

        assertEquals(initialMaxHealth + 10f, stats.getMaxHealth(), "Max health should increase by 10");
        assertEquals(initialMaxMana + 5f, stats.getMaxMana(), "Max mana should increase by 5");
        assertEquals(initialMaxStamina + 5f, stats.getMaxStamina(), "Max stamina should increase by 5");
    }

    @Test
    @DisplayName("Leveling up should restore all stats to full")
    void testLevelUpRestoresStats() {
        // Damage the player first
        stats.takeDamage(50f);
        stats.useMana(25f);
        stats.useStamina(50f);

        // Verify stats are damaged
        assertTrue(stats.getCurrentHealth() < stats.getMaxHealth());
        assertTrue(stats.getCurrentMana() < stats.getMaxMana());
        assertTrue(stats.getCurrentStamina() < stats.getMaxStamina());

        // Level up
        stats.addXP(100f);

        // Stats should be restored to full (new max values)
        assertEquals(stats.getMaxHealth(), stats.getCurrentHealth(), "Health should be fully restored");
        assertEquals(stats.getMaxMana(), stats.getCurrentMana(), "Mana should be fully restored");
        assertEquals(stats.getMaxStamina(), stats.getCurrentStamina(), "Stamina should be fully restored");
    }

    @Test
    @DisplayName("Taking damage should reduce health")
    void testTakeDamage() {
        float initialHealth = stats.getCurrentHealth();
        stats.takeDamage(30f);

        assertEquals(initialHealth - 30f, stats.getCurrentHealth(), "Health should decrease by damage amount");
        assertTrue(stats.isAlive(), "Player should still be alive");
    }

    @Test
    @DisplayName("Health should not go below 0")
    void testHealthFloor() {
        stats.takeDamage(1000f); // Massive damage

        assertEquals(0f, stats.getCurrentHealth(), "Health should not go below 0");
        assertFalse(stats.isAlive(), "Player should not be alive at 0 health");
    }

    @Test
    @DisplayName("Healing should increase health but not exceed max")
    void testHealing() {
        stats.takeDamage(50f);
        stats.heal(30f);

        assertEquals(80f, stats.getCurrentHealth(), "Health should increase by heal amount");

        // Heal beyond max
        stats.heal(1000f);
        assertEquals(stats.getMaxHealth(), stats.getCurrentHealth(), "Health should not exceed max");
    }

    @Test
    @DisplayName("Using mana should decrease mana pool")
    void testUseMana() {
        boolean used = stats.useMana(20f);

        assertTrue(used, "Should successfully use mana");
        assertEquals(30f, stats.getCurrentMana(), "Mana should decrease");
    }

    @Test
    @DisplayName("Cannot use more mana than available")
    void testCannotUseManaWhenInsufficient() {
        boolean used = stats.useMana(100f); // More than available

        assertFalse(used, "Should not be able to use mana");
        assertEquals(50f, stats.getCurrentMana(), "Mana should remain unchanged");
    }

    @Test
    @DisplayName("Stamina should regenerate over time")
    void testStaminaRegeneration() {
        stats.useStamina(50f);
        assertEquals(50f, stats.getCurrentStamina(), "Stamina should be reduced");

        // Regenerate for 1 second (10 stamina/sec)
        stats.regenerateStamina(1.0f);

        assertEquals(60f, stats.getCurrentStamina(), "Stamina should regenerate");
    }

    @Test
    @DisplayName("XP progress should be calculated correctly")
    void testXPProgress() {
        assertEquals(0f, stats.getXPProgress(), "Should start at 0% progress");

        // Add half the XP needed for next level
        stats.addXP(50f); // Need 100 for level 2

        assertEquals(0.5f, stats.getXPProgress(), 0.01f, "Should be at 50% progress");
    }

    @Test
    @DisplayName("Damage should scale with level")
    void testDamageScaling() {
        int level1Damage = stats.getTotalDamage();
        assertEquals(7, level1Damage, "Level 1 should have base damage of 7 (5 + 1*2)");

        // Level up to level 10
        for (int i = 1; i < 10; i++) {
            stats.addXP(stats.getXPRequiredForNextLevel());
        }

        int level10Damage = stats.getTotalDamage();
        assertEquals(25, level10Damage, "Level 10 should have damage of 25 (5 + 10*2)");
    }

    @Test
    @DisplayName("Cannot exceed max level")
    void testMaxLevel() {
        // Set to max level
        stats.setLevel(100);
        assertTrue(stats.isMaxLevel(), "Should be at max level");

        // Try to add XP at max level
        int levelsGained = stats.addXP(10000f);
        assertEquals(0, levelsGained, "Should not gain levels at max");
        assertEquals(100, stats.getLevel(), "Should remain at max level");
        assertEquals(0f, stats.getCurrentXP(), "XP should be 0 at max level");
    }

    @Test
    @DisplayName("XP requirement should scale exponentially")
    void testXPRequirementScaling() {
        float level2XP = stats.getXPRequiredForLevel(2);
        float level3XP = stats.getXPRequiredForLevel(3);
        float level10XP = stats.getXPRequiredForLevel(10);

        assertEquals(100f, level2XP, 0.01f, "Level 2 should require 100 XP");
        assertEquals(112f, level3XP, 0.01f, "Level 3 should require 112 XP (100 * 1.12)");
        assertTrue(level10XP > level3XP, "Higher levels should require more XP");
    }

    @Test
    @DisplayName("Should get total XP for current level")
    void testGetTotalXPForCurrentLevel() {
        assertEquals(0f, stats.getTotalXPForCurrentLevel(), "Level 1 should start at 0 total XP");

        stats.addXP(100f); // Level up to 2
        assertEquals(100f, stats.getTotalXPForCurrentLevel(), "Level 2 should have 100 total XP");

        stats.addXP(stats.getXPRequiredForNextLevel()); // Level up to 3
        float expectedLevel3TotalXP = 100f + 112f; // Level 2 XP + Level 3 XP
        assertEquals(expectedLevel3TotalXP, stats.getTotalXPForCurrentLevel(), 0.01f, "Total XP should accumulate");
    }

    @Test
    @DisplayName("Should calculate health percentage correctly")
    void testGetHealthPercent() {
        assertEquals(1.0f, stats.getHealthPercent(), 0.01f, "Should start at 100%");

        stats.takeDamage(50f);
        assertEquals(0.5f, stats.getHealthPercent(), 0.01f, "Should be at 50% after taking 50 damage");

        stats.takeDamage(50f);
        assertEquals(0.0f, stats.getHealthPercent(), 0.01f, "Should be at 0% when dead");
    }

    @Test
    @DisplayName("Should calculate mana percentage correctly")
    void testGetManaPercent() {
        assertEquals(1.0f, stats.getManaPercent(), 0.01f, "Should start at 100%");

        stats.useMana(25f);
        assertEquals(0.5f, stats.getManaPercent(), 0.01f, "Should be at 50% after using 25 mana");

        stats.useMana(25f);
        assertEquals(0.0f, stats.getManaPercent(), 0.01f, "Should be at 0% when mana depleted");
    }

    @Test
    @DisplayName("Should calculate stamina percentage correctly")
    void testGetStaminaPercent() {
        assertEquals(1.0f, stats.getStaminaPercent(), 0.01f, "Should start at 100%");

        stats.useStamina(50f);
        assertEquals(0.5f, stats.getStaminaPercent(), 0.01f, "Should be at 50% after using 50 stamina");

        stats.useStamina(50f);
        assertEquals(0.0f, stats.getStaminaPercent(), 0.01f, "Should be at 0% when stamina depleted");
    }

    @Test
    @DisplayName("Should restore mana")
    void testRestoreMana() {
        stats.useMana(30f);
        assertEquals(20f, stats.getCurrentMana(), "Mana should be reduced");

        stats.restoreMana(15f);
        assertEquals(35f, stats.getCurrentMana(), "Mana should be restored");

        stats.restoreMana(20f);
        assertEquals(50f, stats.getCurrentMana(), "Mana should not exceed max");
    }

    @Test
    @DisplayName("Should set health directly")
    void testSetHealth() {
        stats.setHealth(75f);
        assertEquals(75f, stats.getCurrentHealth(), "Health should be set to 75");

        stats.setHealth(0f);
        assertEquals(0f, stats.getCurrentHealth(), "Health should be set to 0");
    }

    @Test
    @DisplayName("Should set mana directly")
    void testSetMana() {
        stats.setMana(30f);
        assertEquals(30f, stats.getCurrentMana(), "Mana should be set to 30");

        stats.setMana(50f);
        assertEquals(50f, stats.getCurrentMana(), "Mana should be set to max");
    }

    @Test
    @DisplayName("Should set stamina directly")
    void testSetStamina() {
        stats.setStamina(60f);
        assertEquals(60f, stats.getCurrentStamina(), "Stamina should be set to 60");

        stats.setStamina(0f);
        assertEquals(0f, stats.getCurrentStamina(), "Stamina should be set to 0");
    }

    @Test
    @DisplayName("Should set current XP directly")
    void testSetCurrentXP() {
        stats.setCurrentXP(50f);
        assertEquals(50f, stats.getCurrentXP(), "XP should be set to 50");

        stats.setCurrentXP(0f);
        assertEquals(0f, stats.getCurrentXP(), "XP should be set to 0");
    }

    @Test
    @DisplayName("Should set max health")
    void testSetMaxHealth() {
        stats.setMaxHealth(150f);
        assertEquals(150f, stats.getMaxHealth(), "Max health should be set to 150");

        // Current health should also be adjusted if it exceeds new max
        stats.setHealth(200f);
        stats.setMaxHealth(100f);
        assertTrue(stats.getCurrentHealth() <= stats.getMaxHealth(), "Current health should not exceed max");
    }

    @Test
    @DisplayName("Should set max mana")
    void testSetMaxMana() {
        stats.setMaxMana(75f);
        assertEquals(75f, stats.getMaxMana(), "Max mana should be set to 75");

        stats.setMaxMana(100f);
        assertEquals(100f, stats.getMaxMana(), "Max mana should be updated");
    }

    @Test
    @DisplayName("Should set max stamina")
    void testSetMaxStamina() {
        stats.setMaxStamina(120f);
        assertEquals(120f, stats.getMaxStamina(), "Max stamina should be set to 120");

        stats.setMaxStamina(80f);
        assertEquals(80f, stats.getMaxStamina(), "Max stamina should be updated");
    }

    // Edge case tests for complete coverage

    @Test
    @DisplayName("XP progress should be 100% at max level")
    void testXPProgressAtMaxLevel() {
        stats.setLevel(100);
        assertEquals(1.0f, stats.getXPProgress(), 0.01f, "XP progress should be 100% at max level");
    }

    @Test
    @DisplayName("XP required for level 1 should be 0")
    void testXPRequiredForLevel1() {
        assertEquals(0f, stats.getXPRequiredForLevel(1), "Level 1 should require 0 XP");
        assertEquals(0f, stats.getXPRequiredForLevel(0), "Level 0 should require 0 XP");
    }

    @Test
    @DisplayName("XP required for next level should be 0 at max level")
    void testXPRequiredForNextLevelAtMaxLevel() {
        stats.setLevel(100);
        assertEquals(0f, stats.getXPRequiredForNextLevel(), "Should require 0 XP at max level");
    }

    @Test
    @DisplayName("Cannot use more stamina than available")
    void testCannotUseStaminaWhenInsufficient() {
        boolean used = stats.useStamina(150f); // More than available
        assertFalse(used, "Should not be able to use more stamina than available");
        assertEquals(100f, stats.getCurrentStamina(), "Stamina should remain unchanged");
    }

    @Test
    @DisplayName("Health percent should be 0 when max health is 0")
    void testHealthPercentWhenMaxHealthIsZero() {
        stats.setMaxHealth(1f);
        stats.setHealth(0f);
        stats.setMaxHealth(0f);
        assertEquals(0f, stats.getHealthPercent(), 0.01f, "Health percent should be 0 when max health is 0");
    }

    @Test
    @DisplayName("Mana percent should be 0 when max mana is 0")
    void testManaPercentWhenMaxManaIsZero() {
        stats.setMaxMana(0f);
        assertEquals(0f, stats.getManaPercent(), 0.01f, "Mana percent should be 0 when max mana is 0");
    }

    @Test
    @DisplayName("Stamina percent should be 0 when max stamina is 0")
    void testStaminaPercentWhenMaxStaminaIsZero() {
        stats.setMaxStamina(0f);
        assertEquals(0f, stats.getStaminaPercent(), 0.01f, "Stamina percent should be 0 when max stamina is 0");
    }
}
