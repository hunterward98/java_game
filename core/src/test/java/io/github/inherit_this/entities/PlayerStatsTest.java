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
}
