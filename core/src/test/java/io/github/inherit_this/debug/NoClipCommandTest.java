package io.github.inherit_this.debug;

import io.github.inherit_this.entities.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("NoClipCommand Tests")
class NoClipCommandTest {

    private Player mockPlayer;
    private DebugConsole mockConsole;
    private NoClipCommand command;

    @BeforeEach
    void setUp() {
        mockPlayer = mock(Player.class);
        mockConsole = mock(DebugConsole.class);
        command = new NoClipCommand(mockPlayer);
    }

    @Nested
    @DisplayName("Command Properties")
    class CommandProperties {

        @Test
        @DisplayName("getName should return 'noclip'")
        void testGetName() {
            assertEquals("noclip", command.getName());
        }

        @Test
        @DisplayName("getDescription should return usage information")
        void testGetDescription() {
            String description = command.getDescription();
            assertNotNull(description);
            assertTrue(description.contains("noclip"));
        }
    }

    @Nested
    @DisplayName("Valid Execution")
    class ValidExecution {

        @Test
        @DisplayName("Should enable noclip with 'true'")
        void testEnableNoclip() {
            command.execute(new String[]{"true"}, mockConsole);

            verify(mockPlayer).setNoClip(true);
            verify(mockConsole).log(contains("NoClip set to true"));
        }

        @Test
        @DisplayName("Should disable noclip with 'false'")
        void testDisableNoclip() {
            command.execute(new String[]{"false"}, mockConsole);

            verify(mockPlayer).setNoClip(false);
            verify(mockConsole).log(contains("NoClip set to false"));
        }

        @Test
        @DisplayName("Should handle 'TRUE' in uppercase")
        void testUppercaseTrue() {
            command.execute(new String[]{"TRUE"}, mockConsole);

            verify(mockPlayer).setNoClip(true);
        }

        @Test
        @DisplayName("Should handle 'False' in mixed case")
        void testMixedCaseFalse() {
            command.execute(new String[]{"False"}, mockConsole);

            verify(mockPlayer).setNoClip(false);
        }
    }

    @Nested
    @DisplayName("Invalid Input")
    class InvalidInput {

        @Test
        @DisplayName("Should show usage when no arguments provided")
        void testNoArguments() {
            command.execute(new String[]{}, mockConsole);

            verify(mockConsole).log(contains("Usage"));
            verifyNoInteractions(mockPlayer);
        }

        @Test
        @DisplayName("Should show usage when too many arguments provided")
        void testTooManyArguments() {
            command.execute(new String[]{"true", "false"}, mockConsole);

            verify(mockConsole).log(contains("Usage"));
            verifyNoInteractions(mockPlayer);
        }

        @Test
        @DisplayName("Should treat invalid string as false")
        void testInvalidString() {
            command.execute(new String[]{"invalid"}, mockConsole);

            // Boolean.parseBoolean returns false for non-boolean strings
            verify(mockPlayer).setNoClip(false);
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle empty string as false")
        void testEmptyString() {
            command.execute(new String[]{""}, mockConsole);

            verify(mockPlayer).setNoClip(false);
        }

        @Test
        @DisplayName("Should handle '1' as false")
        void testNumericOne() {
            command.execute(new String[]{"1"}, mockConsole);

            verify(mockPlayer).setNoClip(false);
        }

        @Test
        @DisplayName("Should handle '0' as false")
        void testNumericZero() {
            command.execute(new String[]{"0"}, mockConsole);

            verify(mockPlayer).setNoClip(false);
        }
    }
}
