package io.github.inherit_this.debug;

import io.github.inherit_this.entities.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("TeleportCommand Tests")
class TeleportCommandTest {

    private Player mockPlayer;
    private DebugConsole mockConsole;
    private TeleportCommand command;

    @BeforeEach
    void setUp() {
        mockPlayer = mock(Player.class);
        mockConsole = mock(DebugConsole.class);
        command = new TeleportCommand(mockPlayer);
    }

    @Nested
    @DisplayName("Command Properties")
    class CommandProperties {

        @Test
        @DisplayName("getName should return 'tp'")
        void testGetName() {
            assertEquals("tp", command.getName());
        }

        @Test
        @DisplayName("getDescription should return usage information")
        void testGetDescription() {
            String description = command.getDescription();
            assertNotNull(description);
            assertTrue(description.contains("Teleport") || description.contains("tp"));
        }
    }

    @Nested
    @DisplayName("Valid Execution")
    class ValidExecution {

        @Test
        @DisplayName("Should teleport to valid coordinates")
        void testTeleportToValidCoordinates() {
            command.execute(new String[]{"10", "20"}, mockConsole);

            verify(mockPlayer).setTilePosition(10, 20);
            verify(mockConsole).log(contains("Teleported to tile position (10, 20)"));
        }

        @Test
        @DisplayName("Should teleport to zero coordinates")
        void testTeleportToZero() {
            command.execute(new String[]{"0", "0"}, mockConsole);

            verify(mockPlayer).setTilePosition(0, 0);
            verify(mockConsole).log(contains("(0, 0)"));
        }

        @Test
        @DisplayName("Should teleport to negative coordinates")
        void testTeleportToNegative() {
            command.execute(new String[]{"-5", "-10"}, mockConsole);

            verify(mockPlayer).setTilePosition(-5, -10);
            verify(mockConsole).log(contains("(-5, -10)"));
        }

        @Test
        @DisplayName("Should teleport to large coordinates")
        void testTeleportToLargeCoordinates() {
            command.execute(new String[]{"1000", "2000"}, mockConsole);

            verify(mockPlayer).setTilePosition(1000, 2000);
        }
    }

    @Nested
    @DisplayName("Invalid Input")
    class InvalidInput {

        @Test
        @DisplayName("Should show usage when no arguments provided")
        void testNoArguments() {
            command.execute(new String[]{}, mockConsole);

            verify(mockConsole).log(contains("Usage: tp x y"));
            verifyNoInteractions(mockPlayer);
        }

        @Test
        @DisplayName("Should show usage when only one argument provided")
        void testOneArgument() {
            command.execute(new String[]{"10"}, mockConsole);

            verify(mockConsole).log(contains("Usage: tp x y"));
            verifyNoInteractions(mockPlayer);
        }

        @Test
        @DisplayName("Should show usage when too many arguments provided")
        void testTooManyArguments() {
            command.execute(new String[]{"10", "20", "30"}, mockConsole);

            verify(mockConsole).log(contains("Usage: tp x y"));
            verifyNoInteractions(mockPlayer);
        }

        @Test
        @DisplayName("Should handle invalid X coordinate")
        void testInvalidXCoordinate() {
            command.execute(new String[]{"abc", "20"}, mockConsole);

            verify(mockConsole).log(contains("Invalid coordinates"));
            verifyNoInteractions(mockPlayer);
        }

        @Test
        @DisplayName("Should handle invalid Y coordinate")
        void testInvalidYCoordinate() {
            command.execute(new String[]{"10", "xyz"}, mockConsole);

            verify(mockConsole).log(contains("Invalid coordinates"));
            verifyNoInteractions(mockPlayer);
        }

        @Test
        @DisplayName("Should handle decimal coordinates")
        void testDecimalCoordinates() {
            command.execute(new String[]{"10.5", "20.5"}, mockConsole);

            verify(mockConsole).log(contains("Invalid coordinates"));
            verifyNoInteractions(mockPlayer);
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle exception from setTilePosition")
        void testExceptionHandling() {
            doThrow(new RuntimeException("Out of bounds")).when(mockPlayer).setTilePosition(anyInt(), anyInt());

            command.execute(new String[]{"10", "20"}, mockConsole);

            verify(mockConsole).log(contains("Invalid coordinates"));
        }

        @Test
        @DisplayName("Should handle empty string coordinates")
        void testEmptyStringCoordinates() {
            command.execute(new String[]{"", ""}, mockConsole);

            verify(mockConsole).log(contains("Invalid coordinates"));
            verifyNoInteractions(mockPlayer);
        }
    }
}
