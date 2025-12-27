package io.github.inherit_this.items;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the WeaponEffect enum.
 */
class WeaponEffectTest {

    @Test
    void testEnumValues() {
        WeaponEffect[] effects = WeaponEffect.values();
        assertEquals(6, effects.length, "Should have 6 weapon effect types");
    }

    @Test
    void testEnumContainsExpectedValues() {
        // Verify all expected enum values exist
        assertNotNull(WeaponEffect.valueOf("NONE"));
        assertNotNull(WeaponEffect.valueOf("MANA_DRAIN"));
        assertNotNull(WeaponEffect.valueOf("STAMINA_DRAIN"));
        assertNotNull(WeaponEffect.valueOf("LIFE_STEAL"));
        assertNotNull(WeaponEffect.valueOf("POISON"));
        assertNotNull(WeaponEffect.valueOf("FIRE"));
    }

    @Test
    void testEnumValueOf() {
        assertEquals(WeaponEffect.NONE, WeaponEffect.valueOf("NONE"));
        assertEquals(WeaponEffect.MANA_DRAIN, WeaponEffect.valueOf("MANA_DRAIN"));
        assertEquals(WeaponEffect.STAMINA_DRAIN, WeaponEffect.valueOf("STAMINA_DRAIN"));
        assertEquals(WeaponEffect.LIFE_STEAL, WeaponEffect.valueOf("LIFE_STEAL"));
        assertEquals(WeaponEffect.POISON, WeaponEffect.valueOf("POISON"));
        assertEquals(WeaponEffect.FIRE, WeaponEffect.valueOf("FIRE"));
    }

    @Test
    void testInvalidValueOf() {
        assertThrows(IllegalArgumentException.class, () -> {
            WeaponEffect.valueOf("INVALID_EFFECT");
        });
    }
}
