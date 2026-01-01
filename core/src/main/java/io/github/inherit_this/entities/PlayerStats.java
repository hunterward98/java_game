package io.github.inherit_this.entities;

import io.github.inherit_this.combat.DamageInfo;
import io.github.inherit_this.combat.DamageSource;
import io.github.inherit_this.items.WeaponEffect;
import io.github.inherit_this.particles.ParticleSystem;

/**
 * Manages player statistics including health, mana, stamina, experience, and leveling.
 * Level progression: Each level requires 12% more XP than the previous.
 * Level 1-2 requires 100 XP baseline.
 */
public class PlayerStats {
    // Experience and leveling
    private int level;
    private float currentXP;

    // Currency
    private int coins;

    // Dungeon seed - persistent across saves, ensures same dungeons always generate identically
    private long dungeonSeed;

    // These define the XP system - each level is 12% further than the last.
    private static final float BASE_XP_REQUIRED = 100f;
    private static final float XP_MULTIPLIER = 1.12f;
    private static final int MAX_LEVEL = 100;

    // Health
    private float currentHealth;
    private float maxHealth;

    // Mana
    private float currentMana;
    private float maxMana;

    // Stamina
    private float currentStamina;
    private float maxStamina;

    public PlayerStats() {
        this.level = 1;
        this.currentXP = 0f;
        this.coins = 0;

        // Generate random dungeon seed for this character
        this.dungeonSeed = System.nanoTime();

        // Starting stats
        this.maxHealth = 100f;
        this.currentHealth = maxHealth;

        this.maxMana = 50f;
        this.currentMana = maxMana;

        this.maxStamina = 100f;
        this.currentStamina = maxStamina;
    }

    /**
     * Calculate XP required for a specific level.
     * Formula: BASE_XP * (MULTIPLIER ^ (level - 1))
     */
    public float getXPRequiredForLevel(int level) {
        if (level <= 1) return 0;
        return BASE_XP_REQUIRED * (float)Math.pow(XP_MULTIPLIER, level - 2);
    }

    /**
     * Get total XP required to reach current level from level 1.
     */
    public float getTotalXPForCurrentLevel() {
        float total = 0;
        for (int i = 2; i <= level; i++) {
            total += getXPRequiredForLevel(i);
        }
        return total;
    }

    /**
     * Get XP required to reach next level.
     */
    public float getXPRequiredForNextLevel() {
        if (level >= MAX_LEVEL) return 0;
        return getXPRequiredForLevel(level + 1);
    }

    /**
     * Add experience points and handle leveling up.
     * @return Number of levels gained
     */
    public int addXP(float amount) {
        if (level >= MAX_LEVEL) return 0;

        currentXP += amount;
        int levelsGained = 0;

        // Check for level ups
        while (level < MAX_LEVEL && currentXP >= getXPRequiredForNextLevel()) {
            currentXP -= getXPRequiredForNextLevel();
            level++;
            levelsGained++;
            onLevelUp();
        }

        // Cap XP at max level
        if (level >= MAX_LEVEL) {
            currentXP = 0;
        }

        return levelsGained;
    }

    /**
     * Called when player levels up. Increases max stats.
     */
    private void onLevelUp() {
        // Increase max stats on level up
        maxHealth += 10f;
        maxMana += 5f;
        maxStamina += 5f;

        // Restore to full on level up
        currentHealth = maxHealth;
        currentMana = maxMana;
        currentStamina = maxStamina;
    }

    /**
     * Get XP progress as a percentage (0.0 to 1.0) for UI display.
     */
    public float getXPProgress() {
        if (level >= MAX_LEVEL) return 1.0f;
        float required = getXPRequiredForNextLevel();
        if (required <= 0) return 1.0f;
        return Math.min(1.0f, currentXP / required);
    }

    // Health methods
    /**
     * Take damage with weapon effects and particle emissions.
     * This is the new method that supports the full combat system.
     *
     * @param damageInfo Damage information including amount, source, and effects
     * @param particles Particle system for visual effects (null to disable particles)
     * @param player Player entity for particle attachment
     */
    public void takeDamage(DamageInfo damageInfo, ParticleSystem particles, Player player) {
        // Apply base damage
        currentHealth = Math.max(0, currentHealth - damageInfo.getBaseDamage());

        // Process weapon effects for ATTACK damage (not natural drains)
        if (damageInfo.getSource() == DamageSource.ATTACK) {
            // Emit blood particles attached to player so they follow the player
            if (damageInfo.getBaseDamage() > 0 && particles != null && player != null) {
                // Particles spawn at player's chest level and fall/splatter quickly
                particles.createCombatEffectAttached(
                    ParticleSystem.MaterialType.BLOOD,
                    player,
                    0f,    // Center X
                    20f,   // Chest height
                    0f,    // Center Z
                    6,     // 6 particles (reduced for performance)
                    0.05f  // Detach almost immediately for quick fall/splatter
                );
            }

            // Process weapon effects (always, regardless of particles)
            if (damageInfo.hasEffect(WeaponEffect.MANA_DRAIN)) {
                float manaDrained = Math.min(currentMana, damageInfo.getManaDrainAmount());
                currentMana -= manaDrained;
                // Emit particles only if available and mana was actually drained
                if (manaDrained > 0 && particles != null && player != null) {
                    particles.createCombatEffectAttached(
                        ParticleSystem.MaterialType.MANA,
                        player,
                        0f, 25f, 0f,  // Head level (mana drain from head)
                        8,
                        0.3f  // Stay attached briefly
                    );
                }
            }

            if (damageInfo.hasEffect(WeaponEffect.STAMINA_DRAIN)) {
                float staminaDrained = Math.min(currentStamina, damageInfo.getStaminaDrainAmount());
                currentStamina -= staminaDrained;
                // Emit particles only if available and stamina was actually drained
                if (staminaDrained > 0 && particles != null && player != null) {
                    particles.createCombatEffectAttached(
                        ParticleSystem.MaterialType.STAMINA,
                        player,
                        0f, 15f, 0f,  // Torso level (stamina drain from body)
                        8,
                        0.3f  // Stay attached briefly
                    );
                }
            }

            // Note: Life steal is handled by the attacker, not here
        }
    }

    /**
     * Take damage (simple version for backward compatibility).
     * This version doesn't support weapon effects or particles.
     * @deprecated Use takeDamage(DamageInfo, ParticleSystem, float, float, float) instead
     */
    @Deprecated
    public void takeDamage(float amount) {
        currentHealth = Math.max(0, currentHealth - amount);
    }

    public void heal(float amount) {
        currentHealth = Math.min(maxHealth, currentHealth + amount);
    }

    public float getHealthPercent() {
        return maxHealth > 0 ? currentHealth / maxHealth : 0;
    }

    // Mana methods
    public boolean useMana(float amount) {
        if (currentMana >= amount) {
            currentMana -= amount;
            return true;
        }
        return false;
    }

    public void restoreMana(float amount) {
        currentMana = Math.min(maxMana, currentMana + amount);
    }

    public float getManaPercent() {
        return maxMana > 0 ? currentMana / maxMana : 0;
    }

    // Stamina methods
    public boolean useStamina(float amount) {
        if (currentStamina >= amount) {
            currentStamina -= amount;
            return true;
        }
        return false;
    }

    public void restoreStamina(float amount) {
        currentStamina = Math.min(maxStamina, currentStamina + amount);
    }

    public float getStaminaPercent() {
        return maxStamina > 0 ? currentStamina / maxStamina : 0;
    }

    // Stamina regeneration (call in update loop)
    public void regenerateStamina(float delta) {
        restoreStamina(10f * delta); // 10 stamina per second
    }

    // Getters
    public int getLevel() { return level; }
    public float getCurrentXP() { return currentXP; }
    public float getCurrentHealth() { return currentHealth; }
    public float getMaxHealth() { return maxHealth; }
    public float getCurrentMana() { return currentMana; }
    public float getMaxMana() { return maxMana; }
    public float getCurrentStamina() { return currentStamina; }
    public float getMaxStamina() { return maxStamina; }
    public boolean isAlive() { return currentHealth > 0; }
    public boolean isMaxLevel() { return level >= MAX_LEVEL; }
    public int getCoins() { return coins; }

    // Coin management
    public void addCoins(int amount) {
        this.coins = Math.max(0, this.coins + amount);
    }

    public boolean removeCoins(int amount) {
        if (this.coins >= amount) {
            this.coins -= amount;
            return true;
        }
        return false;
    }

    public void setCoins(int coins) {
        this.coins = Math.max(0, coins);
    }

    // Setters for testing/debugging and save/load
    public void setLevel(int level) {
        this.level = Math.max(1, Math.min(MAX_LEVEL, level));
        this.currentXP = 0;
    }

    public void setHealth(float health) {
        this.currentHealth = Math.max(0, Math.min(maxHealth, health));
    }

    public void setMaxHealth(float maxHealth) {
        this.maxHealth = Math.max(1, maxHealth);
        this.currentHealth = Math.min(currentHealth, maxHealth);
    }

    public void setMana(float mana) {
        this.currentMana = Math.max(0, Math.min(maxMana, mana));
    }

    public void setMaxMana(float maxMana) {
        this.maxMana = Math.max(0, maxMana);
        this.currentMana = Math.min(currentMana, maxMana);
    }

    public void setStamina(float stamina) {
        this.currentStamina = Math.max(0, Math.min(maxStamina, stamina));
    }

    public void setMaxStamina(float maxStamina) {
        this.maxStamina = Math.max(0, maxStamina);
        this.currentStamina = Math.min(currentStamina, maxStamina);
    }

    public void setCurrentXP(float xp) {
        this.currentXP = Math.max(0, xp);
    }

    // Dungeon seed methods
    public long getDungeonSeed() {
        return dungeonSeed;
    }

    public void setDungeonSeed(long seed) {
        this.dungeonSeed = seed;
    }

    /**
     * Calculate total damage output.
     * Base damage scales with level, can be enhanced by equipment.
     * @return Total damage per attack
     */
    public int getTotalDamage() {
        // Base damage: 5 + (level * 2)
        // Level 1 = 7 damage
        // Level 10 = 25 damage
        // Level 50 = 105 damage
        int baseDamage = 5 + (level * 2);

        return baseDamage;
    }
}
