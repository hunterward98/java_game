package io.github.inherit_this.debug;

import io.github.inherit_this.world.Tile;
import io.github.inherit_this.world.TileType;
import io.github.inherit_this.world.WorldProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("InspectCommand Tests")
class InspectCommandTest {

    private WorldProvider mockWorld;
    private DebugConsole mockConsole;
    private InspectCommand command;

    @BeforeEach
    void setUp() {
        mockWorld = mock(WorldProvider.class);
        mockConsole = mock(DebugConsole.class);
        command = new InspectCommand(mockWorld);
    }

    @Nested
    @DisplayName("Command Properties")
    class CommandProperties {

        @Test
        @DisplayName("getName should return 'inspect'")
        void testGetName() {
            assertEquals("inspect", command.getName());
        }

        @Test
        @DisplayName("getDescription should return usage information")
        void testGetDescription() {
            String description = command.getDescription();
            assertNotNull(description);
            assertTrue(description.contains("inspect") || description.contains("tile"));
        }
    }

    @Nested
    @DisplayName("Valid Execution")
    class ValidExecution {

        @Test
        @DisplayName("Should inspect tile at valid coordinates")
        void testInspectValidTile() {
            Tile mockTile = mock(Tile.class);
            when(mockTile.getType()).thenReturn(TileType.GRASS);
            when(mockTile.isSolid()).thenReturn(false);
            when(mockWorld.getTileAtWorldCoords(10, 20)).thenReturn(mockTile);

            command.execute(new String[]{"10", "20"}, mockConsole);

            verify(mockConsole).log(contains("Inspection at world coords (10,20)"));
            verify(mockConsole).log(contains("type=GRASS"));
            verify(mockConsole).log(contains("solid=false"));
        }

        @Test
        @DisplayName("Should inspect solid tile")
        void testInspectSolidTile() {
            Tile mockTile = mock(Tile.class);
            when(mockTile.getType()).thenReturn(TileType.STONE);
            when(mockTile.isSolid()).thenReturn(true);
            when(mockWorld.getTileAtWorldCoords(5, 5)).thenReturn(mockTile);

            command.execute(new String[]{"5", "5"}, mockConsole);

            verify(mockConsole).log(contains("solid=true"));
        }

        @Test
        @DisplayName("Should handle null tile")
        void testNullTile() {
            when(mockWorld.getTileAtWorldCoords(100, 100)).thenReturn(null);

            command.execute(new String[]{"100", "100"}, mockConsole);

            verify(mockConsole).log(contains("No tile at world coords (100,100)"));
        }
    }

    @Nested
    @DisplayName("Invalid Input")
    class InvalidInput {

        @Test
        @DisplayName("Should show usage when no arguments provided")
        void testNoArguments() {
            command.execute(new String[]{}, mockConsole);

            verify(mockConsole).log(contains("Usage: inspect x y"));
            verifyNoInteractions(mockWorld);
        }

        @Test
        @DisplayName("Should show usage when only one argument provided")
        void testOneArgument() {
            command.execute(new String[]{"10"}, mockConsole);

            verify(mockConsole).log(contains("Usage: inspect x y"));
            verifyNoInteractions(mockWorld);
        }

        @Test
        @DisplayName("Should handle invalid X coordinate")
        void testInvalidXCoordinate() {
            command.execute(new String[]{"abc", "20"}, mockConsole);

            verify(mockConsole).log(contains("Invalid coordinates"));
            verifyNoInteractions(mockWorld);
        }

        @Test
        @DisplayName("Should handle invalid Y coordinate")
        void testInvalidYCoordinate() {
            command.execute(new String[]{"10", "xyz"}, mockConsole);

            verify(mockConsole).log(contains("Invalid coordinates"));
            verifyNoInteractions(mockWorld);
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should inspect tile at negative coordinates")
        void testNegativeCoordinates() {
            Tile mockTile = mock(Tile.class);
            when(mockTile.getType()).thenReturn(TileType.VOID);
            when(mockTile.isSolid()).thenReturn(true);
            when(mockWorld.getTileAtWorldCoords(-5, -10)).thenReturn(mockTile);

            command.execute(new String[]{"-5", "-10"}, mockConsole);

            verify(mockConsole).log(contains("(-5,-10)"));
        }

        @Test
        @DisplayName("Should reject extra arguments")
        void testExtraArguments() {
            command.execute(new String[]{"1", "2", "extra"}, mockConsole);

            verify(mockConsole).log(contains("Usage: inspect x y"));
            verifyNoInteractions(mockWorld);
        }
    }
}
