package io.github.inherit_this.audio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for SoundType enum
 */
class SoundTypeTest {

    @Test
    @DisplayName("All UI sound types should exist")
    void testUISounds() {
        assertNotNull(SoundType.UI_CLICK);
        assertNotNull(SoundType.UI_HOVER);
        assertNotNull(SoundType.INVENTORY_PICKUP);
        assertNotNull(SoundType.INVENTORY_DROP);
        assertNotNull(SoundType.INVENTORY_EQUIP);
        assertNotNull(SoundType.INVENTORY_MOVE);
    }

    @Test
    @DisplayName("All combat sound types should exist")
    void testCombatSounds() {
        assertNotNull(SoundType.ATTACK_SWING);
        assertNotNull(SoundType.ATTACK_HIT);
        assertNotNull(SoundType.DAMAGE_TAKEN);
        assertNotNull(SoundType.ENEMY_DEATH);
    }

    @Test
    @DisplayName("All object interaction sound types should exist")
    void testObjectSounds() {
        assertNotNull(SoundType.OBJECT_BREAK_WOOD);
        assertNotNull(SoundType.OBJECT_BREAK_CERAMIC);
        assertNotNull(SoundType.OBJECT_BREAK_METAL);
        assertNotNull(SoundType.CHEST_OPEN);
    }

    @Test
    @DisplayName("All loot sound types should exist")
    void testLootSounds() {
        assertNotNull(SoundType.LOOT_GOLD);
        assertNotNull(SoundType.LOOT_ITEM);
    }

    @Test
    @DisplayName("All player action sound types should exist")
    void testPlayerSounds() {
        assertNotNull(SoundType.FOOTSTEP);
        assertNotNull(SoundType.LEVEL_UP);
    }

    @Test
    @DisplayName("All editor sound types should exist")
    void testEditorSounds() {
        assertNotNull(SoundType.EDITOR_PLACE);
        assertNotNull(SoundType.EDITOR_DELETE);
        assertNotNull(SoundType.EDITOR_MODE_SWITCH);
        assertNotNull(SoundType.EDITOR_ERROR);
    }

    @Test
    @DisplayName("valueOf should work for all sound types")
    void testValueOf() {
        assertEquals(SoundType.UI_CLICK, SoundType.valueOf("UI_CLICK"));
        assertEquals(SoundType.ATTACK_SWING, SoundType.valueOf("ATTACK_SWING"));
        assertEquals(SoundType.LOOT_GOLD, SoundType.valueOf("LOOT_GOLD"));
    }

    @Test
    @DisplayName("values() should return all sound types")
    void testValues() {
        SoundType[] values = SoundType.values();
        assertEquals(22, values.length, "Should have exactly 22 sound types");
    }

    @Test
    @DisplayName("Enum should be comparable")
    void testComparable() {
        SoundType[] values = SoundType.values();
        assertTrue(values.length > 1);
        // Enums are Comparable by declaration order
        assertNotEquals(0, values[0].compareTo(values[1]));
    }
}
