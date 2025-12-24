package io.github.inherit_this.audio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SoundType Tests")
public class SoundTypeTest {

    @Test
    @DisplayName("Should have all expected sound types")
    void testAllValues() {
        SoundType[] types = SoundType.values();
        assertEquals(22, types.length, "Should have exactly 22 sound types");
    }

    @Test
    @DisplayName("Should have UI sound types")
    void testUISounds() {
        assertNotNull(SoundType.UI_CLICK);
        assertNotNull(SoundType.UI_HOVER);
        assertNotNull(SoundType.INVENTORY_PICKUP);
        assertNotNull(SoundType.INVENTORY_DROP);
        assertNotNull(SoundType.INVENTORY_EQUIP);
        assertNotNull(SoundType.INVENTORY_MOVE);
    }

    @Test
    @DisplayName("Should have combat sound types")
    void testCombatSounds() {
        assertNotNull(SoundType.ATTACK_SWING);
        assertNotNull(SoundType.ATTACK_HIT);
        assertNotNull(SoundType.DAMAGE_TAKEN);
        assertNotNull(SoundType.ENEMY_DEATH);
    }

    @Test
    @DisplayName("Should have object interaction sound types")
    void testObjectSounds() {
        assertNotNull(SoundType.OBJECT_BREAK_WOOD);
        assertNotNull(SoundType.OBJECT_BREAK_CERAMIC);
        assertNotNull(SoundType.OBJECT_BREAK_METAL);
        assertNotNull(SoundType.CHEST_OPEN);
    }

    @Test
    @DisplayName("Should have loot sound types")
    void testLootSounds() {
        assertNotNull(SoundType.LOOT_GOLD);
        assertNotNull(SoundType.LOOT_ITEM);
    }

    @Test
    @DisplayName("Should have player action sound types")
    void testPlayerActionSounds() {
        assertNotNull(SoundType.FOOTSTEP);
        assertNotNull(SoundType.LEVEL_UP);
    }

    @Test
    @DisplayName("Should have map editor sound types")
    void testEditorSounds() {
        assertNotNull(SoundType.EDITOR_PLACE);
        assertNotNull(SoundType.EDITOR_DELETE);
        assertNotNull(SoundType.EDITOR_MODE_SWITCH);
        assertNotNull(SoundType.EDITOR_ERROR);
    }

    @Test
    @DisplayName("valueOf should work for all types")
    void testValueOf() {
        assertEquals(SoundType.UI_CLICK, SoundType.valueOf("UI_CLICK"));
        assertEquals(SoundType.ATTACK_SWING, SoundType.valueOf("ATTACK_SWING"));
        assertEquals(SoundType.LOOT_GOLD, SoundType.valueOf("LOOT_GOLD"));
    }

    @Test
    @DisplayName("valueOf should throw for invalid name")
    void testValueOfInvalid() {
        assertThrows(IllegalArgumentException.class, () -> SoundType.valueOf("INVALID"));
    }

    @Test
    @DisplayName("Each sound type should have unique ordinal")
    void testUniqueOrdinals() {
        SoundType[] types = SoundType.values();
        for (int i = 0; i < types.length; i++) {
            for (int j = i + 1; j < types.length; j++) {
                assertNotEquals(types[i].ordinal(), types[j].ordinal());
            }
        }
    }

    @Test
    @DisplayName("Should support switch statements")
    void testSwitchStatement() {
        for (SoundType type : SoundType.values()) {
            String category;
            switch (type) {
                case UI_CLICK:
                case UI_HOVER:
                    category = "UI";
                    break;
                case ATTACK_SWING:
                case ATTACK_HIT:
                    category = "Combat";
                    break;
                default:
                    category = "Other";
                    break;
            }
            assertNotNull(category);
        }
    }
}
