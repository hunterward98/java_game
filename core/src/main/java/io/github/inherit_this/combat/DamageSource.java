package io.github.inherit_this.combat;

/**
 * Enum representing the source or cause of damage.
 * This is used to determine whether particles should be emitted and what type of damage processing occurs.
 */
public enum DamageSource {
    /**
     * Damage from a direct attack (melee, ranged, or spell).
     * This will trigger blood particles and weapon effect processing.
     */
    ATTACK,

    /**
     * Damage or resource drain from natural gameplay (running drains stamina, casting drains mana).
     * This will NOT trigger particles.
     */
    NATURAL_DRAIN,

    /**
     * Damage from environmental sources (fall damage, fire, poison over time).
     * May or may not trigger particles depending on implementation.
     */
    ENVIRONMENTAL
}
