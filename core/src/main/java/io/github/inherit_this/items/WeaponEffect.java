package io.github.inherit_this.items;

/**
 * Enum representing special weapon effects that can be applied on attack.
 * These effects trigger when a weapon with the effect successfully hits a target.
 */
public enum WeaponEffect {
    /**
     * No special effect.
     */
    NONE,

    /**
     * Drains mana from the target on hit.
     * The amount drained is specified in ItemStats.manaDrainAmount.
     */
    MANA_DRAIN,

    /**
     * Drains stamina from the target on hit.
     * The amount drained is specified in ItemStats.staminaDrainAmount.
     */
    STAMINA_DRAIN,

    /**
     * Converts a percentage of damage dealt into health for the attacker.
     * The percentage is specified in ItemStats.lifeStealPercent (0.0-1.0).
     */
    LIFE_STEAL,

    /**
     * Applies poison damage over time to the target.
     * Future implementation will include duration and damage per tick.
     */
    POISON,

    /**
     * Applies fire damage over time or additional burn damage.
     * Future implementation will include duration and damage per tick.
     */
    FIRE
}
