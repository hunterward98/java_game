package io.github.inherit_this.combat;

/**
 * Game difficulty levels that affect enemy stats and AI behavior.
 * Higher difficulties increase enemy health, damage, and intelligence.
 */
public enum Difficulty {
    EASY(0.75f, 0.75f, 1.25f, 0.8f),      // 75% HP/DMG, 125% loot, slower AI
    NORMAL(1.0f, 1.0f, 1.0f, 1.0f),       // Standard values
    HARD(1.5f, 1.5f, 0.75f, 1.2f);        // 150% HP/DMG, 75% loot, faster AI

    private final float healthMultiplier;
    private final float damageMultiplier;
    private final float lootMultiplier;
    private final float aiSpeedMultiplier;  // Affects reaction time and aggression

    Difficulty(float healthMultiplier, float damageMultiplier, float lootMultiplier, float aiSpeedMultiplier) {
        this.healthMultiplier = healthMultiplier;
        this.damageMultiplier = damageMultiplier;
        this.lootMultiplier = lootMultiplier;
        this.aiSpeedMultiplier = aiSpeedMultiplier;
    }

    public float getHealthMultiplier() {
        return healthMultiplier;
    }

    public float getDamageMultiplier() {
        return damageMultiplier;
    }

    public float getLootMultiplier() {
        return lootMultiplier;
    }

    public float getAiSpeedMultiplier() {
        return aiSpeedMultiplier;
    }

    /**
     * Get display name for UI.
     */
    public String getDisplayName() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
