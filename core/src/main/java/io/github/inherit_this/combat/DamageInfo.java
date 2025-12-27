package io.github.inherit_this.combat;

import io.github.inherit_this.items.ItemStats;
import io.github.inherit_this.items.WeaponEffect;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Immutable class containing metadata about damage being dealt.
 * This includes the base damage, source, and any weapon effects to apply.
 */
public class DamageInfo {
    private final int baseDamage;
    private final DamageSource source;
    private final Set<WeaponEffect> effects;
    private final float manaDrainAmount;
    private final float staminaDrainAmount;
    private final float lifeStealPercent;

    /**
     * Private constructor - use factory methods to create instances.
     */
    private DamageInfo(int baseDamage, DamageSource source, Set<WeaponEffect> effects,
                       float manaDrainAmount, float staminaDrainAmount, float lifeStealPercent) {
        this.baseDamage = baseDamage;
        this.source = source;
        this.effects = new HashSet<>(effects);
        this.manaDrainAmount = manaDrainAmount;
        this.staminaDrainAmount = staminaDrainAmount;
        this.lifeStealPercent = lifeStealPercent;
    }

    /**
     * Create basic attack damage with no weapon effects.
     */
    public static DamageInfo attack(int damage) {
        return new DamageInfo(damage, DamageSource.ATTACK, Collections.emptySet(), 0f, 0f, 0f);
    }

    /**
     * Create attack damage with weapon effects from equipped weapon stats.
     */
    public static DamageInfo attackWithEffects(int damage, ItemStats weaponStats) {
        if (weaponStats == null) {
            return attack(damage);
        }

        Set<WeaponEffect> effects = weaponStats.getWeaponEffects();
        float manaDrain = weaponStats.getManaDrainAmount();
        float staminaDrain = weaponStats.getStaminaDrainAmount();
        float lifeSteal = weaponStats.getLifeStealPercent();

        return new DamageInfo(damage, DamageSource.ATTACK, effects, manaDrain, staminaDrain, lifeSteal);
    }

    /**
     * Create natural drain damage info (for running, casting spells).
     * This will not trigger particle emissions.
     */
    public static DamageInfo naturalDrain() {
        return new DamageInfo(0, DamageSource.NATURAL_DRAIN, Collections.emptySet(), 0f, 0f, 0f);
    }

    /**
     * Create environmental damage (fall damage, fire, poison).
     */
    public static DamageInfo environmental(int damage) {
        return new DamageInfo(damage, DamageSource.ENVIRONMENTAL, Collections.emptySet(), 0f, 0f, 0f);
    }

    // Getters

    public int getBaseDamage() {
        return baseDamage;
    }

    public DamageSource getSource() {
        return source;
    }

    public Set<WeaponEffect> getEffects() {
        return Collections.unmodifiableSet(effects);
    }

    public boolean hasEffect(WeaponEffect effect) {
        return effects.contains(effect);
    }

    public float getManaDrainAmount() {
        return manaDrainAmount;
    }

    public float getStaminaDrainAmount() {
        return staminaDrainAmount;
    }

    public float getLifeStealPercent() {
        return lifeStealPercent;
    }

    @Override
    public String toString() {
        return String.format("DamageInfo{damage=%d, source=%s, effects=%s, manaDrain=%.1f, staminaDrain=%.1f, lifeSteal=%.1f%%}",
                baseDamage, source, effects, manaDrainAmount, staminaDrainAmount, lifeStealPercent * 100);
    }
}
