package io.github.inherit_this.combat;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the DamageSource enum.
 */
class DamageSourceTest {

    @Test
    void testEnumValues() {
        DamageSource[] sources = DamageSource.values();
        assertEquals(3, sources.length, "Should have 3 damage source types");
    }

    @Test
    void testEnumContainsExpectedValues() {
        // Verify all expected enum values exist
        assertNotNull(DamageSource.valueOf("ATTACK"));
        assertNotNull(DamageSource.valueOf("NATURAL_DRAIN"));
        assertNotNull(DamageSource.valueOf("ENVIRONMENTAL"));
    }

    @Test
    void testEnumValueOf() {
        assertEquals(DamageSource.ATTACK, DamageSource.valueOf("ATTACK"));
        assertEquals(DamageSource.NATURAL_DRAIN, DamageSource.valueOf("NATURAL_DRAIN"));
        assertEquals(DamageSource.ENVIRONMENTAL, DamageSource.valueOf("ENVIRONMENTAL"));
    }

    @Test
    void testInvalidValueOf() {
        assertThrows(IllegalArgumentException.class, () -> {
            DamageSource.valueOf("INVALID_SOURCE");
        });
    }
}
