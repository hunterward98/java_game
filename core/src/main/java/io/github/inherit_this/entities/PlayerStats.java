package io.github.inherit_this.entities;

/**
 * Manages player statistics including health, mana, stamina, experience, and leveling.
 * Level progression: Each level requires 12% more XP than the previous.
 * Level 1-2 requires 100 XP baseline.
 */
public class PlayerStats {
    // Experience and leveling
    private int level;
    private float currentXP;

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

        // TODO: Add equipment bonuses from equipped weapon
        // Equipment equipment = player.getEquipment();
        // baseDamage += equipment.getTotalStats().getDamage();

        return baseDamage;
    }
}
