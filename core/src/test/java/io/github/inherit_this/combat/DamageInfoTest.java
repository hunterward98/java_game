package io.github.inherit_this.combat;

import io.github.inherit_this.items.ItemStats;
import io.github.inherit_this.items.WeaponEffect;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the DamageInfo class.
 */
class DamageInfoTest {

    @Test
    void testAttackFactoryMethod() {
        DamageInfo damage = DamageInfo.attack(50);

        assertEquals(50, damage.getBaseDamage());
        assertEquals(DamageSource.ATTACK, damage.getSource());
        assertTrue(damage.getEffects().isEmpty());
        assertEquals(0f, damage.getManaDrainAmount());
        assertEquals(0f, damage.getStaminaDrainAmount());
        assertEquals(0f, damage.getLifeStealPercent());
    }

    @Test
    void testAttackWithEffectsNullWeaponStats() {
        DamageInfo damage = DamageInfo.attackWithEffects(30, null);

        assertEquals(30, damage.getBaseDamage());
        assertEquals(DamageSource.ATTACK, damage.getSource());
        assertTrue(damage.getEffects().isEmpty());
        assertEquals(0f, damage.getManaDrainAmount());
    }

    @Test
    void testAttackWithEffectsManaDrain() {
        ItemStats weaponStats = ItemStats.weaponWithEffects(
            25, 100, 1.0f,
            EnumSet.of(WeaponEffect.MANA_DRAIN),
            10f, 0f, 0f
        );

        DamageInfo damage = DamageInfo.attackWithEffects(25, weaponStats);

        assertEquals(25, damage.getBaseDamage());
        assertEquals(DamageSource.ATTACK, damage.getSource());
        assertTrue(damage.hasEffect(WeaponEffect.MANA_DRAIN));
        assertEquals(10f, damage.getManaDrainAmount());
        assertEquals(0f, damage.getStaminaDrainAmount());
        assertEquals(0f, damage.getLifeStealPercent());
    }

    @Test
    void testAttackWithEffectsStaminaDrain() {
        ItemStats weaponStats = ItemStats.weaponWithEffects(
            30, 120, 0.9f,
            EnumSet.of(WeaponEffect.STAMINA_DRAIN),
            0f, 15f, 0f
        );

        DamageInfo damage = DamageInfo.attackWithEffects(30, weaponStats);

        assertEquals(30, damage.getBaseDamage());
        assertTrue(damage.hasEffect(WeaponEffect.STAMINA_DRAIN));
        assertEquals(0f, damage.getManaDrainAmount());
        assertEquals(15f, damage.getStaminaDrainAmount());
    }

    @Test
    void testAttackWithEffectsLifeSteal() {
        ItemStats weaponStats = ItemStats.weaponWithEffects(
            20, 80, 1.5f,
            EnumSet.of(WeaponEffect.LIFE_STEAL),
            0f, 0f, 0.25f
        );

        DamageInfo damage = DamageInfo.attackWithEffects(20, weaponStats);

        assertEquals(20, damage.getBaseDamage());
        assertTrue(damage.hasEffect(WeaponEffect.LIFE_STEAL));
        assertEquals(0.25f, damage.getLifeStealPercent());
    }

    @Test
    void testAttackWithMultipleEffects() {
        ItemStats weaponStats = ItemStats.weaponWithEffects(
            40, 150, 1.0f,
            EnumSet.of(WeaponEffect.MANA_DRAIN, WeaponEffect.STAMINA_DRAIN),
            8f, 12f, 0f
        );

        DamageInfo damage = DamageInfo.attackWithEffects(40, weaponStats);

        assertEquals(40, damage.getBaseDamage());
        assertTrue(damage.hasEffect(WeaponEffect.MANA_DRAIN));
        assertTrue(damage.hasEffect(WeaponEffect.STAMINA_DRAIN));
        assertFalse(damage.hasEffect(WeaponEffect.LIFE_STEAL));
        assertEquals(8f, damage.getManaDrainAmount());
        assertEquals(12f, damage.getStaminaDrainAmount());
    }

    @Test
    void testNaturalDrainFactoryMethod() {
        DamageInfo damage = DamageInfo.naturalDrain();

        assertEquals(0, damage.getBaseDamage());
        assertEquals(DamageSource.NATURAL_DRAIN, damage.getSource());
        assertTrue(damage.getEffects().isEmpty());
    }

    @Test
    void testEnvironmentalFactoryMethod() {
        DamageInfo damage = DamageInfo.environmental(15);

        assertEquals(15, damage.getBaseDamage());
        assertEquals(DamageSource.ENVIRONMENTAL, damage.getSource());
        assertTrue(damage.getEffects().isEmpty());
    }

    @Test
    void testEffectsAreUnmodifiable() {
        ItemStats weaponStats = ItemStats.weaponWithEffects(
            25, 100, 1.0f,
            EnumSet.of(WeaponEffect.MANA_DRAIN),
            10f, 0f, 0f
        );

        DamageInfo damage = DamageInfo.attackWithEffects(25, weaponStats);

        assertThrows(UnsupportedOperationException.class, () -> {
            damage.getEffects().add(WeaponEffect.LIFE_STEAL);
        });
    }

    @Test
    void testToString() {
        DamageInfo damage = DamageInfo.attack(50);
        String str = damage.toString();

        assertNotNull(str);
        assertTrue(str.contains("50"));
        assertTrue(str.contains("ATTACK"));
    }
}
