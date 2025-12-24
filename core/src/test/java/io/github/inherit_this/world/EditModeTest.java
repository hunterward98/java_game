package io.github.inherit_this.world;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for MapEditor.EditMode enum.
 */
@DisplayName("EditMode Tests")
public class EditModeTest {

    @Nested
    @DisplayName("Enum Values")
    class EnumValues {

        @Test
        @DisplayName("Should have all expected edit modes")
        void testAllValues() {
            MapEditor.EditMode[] modes = MapEditor.EditMode.values();
            assertEquals(2, modes.length, "Should have exactly 2 edit modes");
        }

        @Test
        @DisplayName("Should have TILE mode")
        void testTileExists() {
            assertNotNull(MapEditor.EditMode.TILE);
        }

        @Test
        @DisplayName("Should have OBJECT mode")
        void testObjectExists() {
            assertNotNull(MapEditor.EditMode.OBJECT);
        }

        @Test
        @DisplayName("valueOf should work for all modes")
        void testValueOf() {
            assertEquals(MapEditor.EditMode.TILE,
                MapEditor.EditMode.valueOf("TILE"));
            assertEquals(MapEditor.EditMode.OBJECT,
                MapEditor.EditMode.valueOf("OBJECT"));
        }

        @Test
        @DisplayName("valueOf should throw for invalid name")
        void testValueOfInvalid() {
            assertThrows(IllegalArgumentException.class, () -> {
                MapEditor.EditMode.valueOf("INVALID");
            });
            assertThrows(IllegalArgumentException.class, () -> {
                MapEditor.EditMode.valueOf("ENTITY");
            });
        }
    }

    @Nested
    @DisplayName("Ordinal Values")
    class OrdinalValues {

        @Test
        @DisplayName("Each edit mode should have unique ordinal")
        void testUniqueOrdinals() {
            assertNotEquals(MapEditor.EditMode.TILE.ordinal(),
                MapEditor.EditMode.OBJECT.ordinal());
        }

        @Test
        @DisplayName("Ordinals should be sequential from 0")
        void testSequentialOrdinals() {
            MapEditor.EditMode[] modes = MapEditor.EditMode.values();
            for (int i = 0; i < modes.length; i++) {
                assertEquals(i, modes[i].ordinal(), "Ordinal should match array index");
            }
        }
    }

    @Nested
    @DisplayName("Integration Scenarios")
    class IntegrationScenarios {

        @Test
        @DisplayName("Should support switch statements")
        void testSwitchStatement() {
            for (MapEditor.EditMode mode : MapEditor.EditMode.values()) {
                String modeDescription;
                switch (mode) {
                    case TILE:
                        modeDescription = "Tile editing mode";
                        break;
                    case OBJECT:
                        modeDescription = "Object placement mode";
                        break;
                    default:
                        modeDescription = null;
                        break;
                }
                assertNotNull(modeDescription, "Switch should handle all edit modes");
            }
        }

        @Test
        @DisplayName("Should work with comparison operations")
        void testComparison() {
            assertTrue(MapEditor.EditMode.TILE == MapEditor.EditMode.TILE);
            assertFalse(MapEditor.EditMode.TILE == MapEditor.EditMode.OBJECT);
            assertTrue(MapEditor.EditMode.OBJECT.equals(MapEditor.EditMode.OBJECT));
            assertFalse(MapEditor.EditMode.OBJECT.equals(MapEditor.EditMode.TILE));
        }

        @Test
        @DisplayName("Should be serializable via name")
        void testSerialization() {
            for (MapEditor.EditMode mode : MapEditor.EditMode.values()) {
                String name = mode.name();
                MapEditor.EditMode restored = MapEditor.EditMode.valueOf(name);
                assertEquals(mode, restored, "Should restore from name");
            }
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle null checks")
        void testNullChecks() {
            MapEditor.EditMode mode = MapEditor.EditMode.TILE;
            assertNotEquals(null, mode);
            assertFalse(mode.equals(null));
        }

        @Test
        @DisplayName("Should handle toString")
        void testToString() {
            for (MapEditor.EditMode mode : MapEditor.EditMode.values()) {
                String str = mode.toString();
                assertNotNull(str);
                assertFalse(str.isEmpty());
                assertEquals(mode.name(), str, "toString should match name");
            }
        }

        @Test
        @DisplayName("Should maintain identity")
        void testIdentity() {
            MapEditor.EditMode tile1 = MapEditor.EditMode.TILE;
            MapEditor.EditMode tile2 = MapEditor.EditMode.valueOf("TILE");
            assertSame(tile1, tile2, "Same enum constant should be identical");
        }

        @Test
        @DisplayName("Name should be uppercase")
        void testNameUppercase() {
            for (MapEditor.EditMode mode : MapEditor.EditMode.values()) {
                String name = mode.name();
                assertEquals(name.toUpperCase(), name, "Enum name should be uppercase");
            }
        }
    }

    @Nested
    @DisplayName("Use Cases")
    class UseCases {

        @Test
        @DisplayName("Edit modes should represent editor functionality")
        void testEditorModes() {
            // TILE for placing tiles
            assertNotNull(MapEditor.EditMode.TILE);

            // OBJECT for placing objects
            assertNotNull(MapEditor.EditMode.OBJECT);
        }

        @Test
        @DisplayName("Each edit mode should be distinguishable")
        void testDistinguishable() {
            assertNotEquals(MapEditor.EditMode.TILE, MapEditor.EditMode.OBJECT);
        }

        @Test
        @DisplayName("Should support mode toggling logic")
        void testModeToggling() {
            // Simulate mode toggling (like M key in MapEditor)
            MapEditor.EditMode currentMode = MapEditor.EditMode.TILE;
            MapEditor.EditMode newMode = (currentMode == MapEditor.EditMode.TILE)
                ? MapEditor.EditMode.OBJECT
                : MapEditor.EditMode.TILE;
            assertEquals(MapEditor.EditMode.OBJECT, newMode);

            // Toggle back
            currentMode = newMode;
            newMode = (currentMode == MapEditor.EditMode.TILE)
                ? MapEditor.EditMode.OBJECT
                : MapEditor.EditMode.TILE;
            assertEquals(MapEditor.EditMode.TILE, newMode);
        }
    }
}
