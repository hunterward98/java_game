package io.github.inherit_this.debug;

import io.github.inherit_this.entities.Player;
import io.github.inherit_this.entities.PlayerStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("SetLevelCommand Tests")
class SetLevelCommandTest {

    private Player mockPlayer;
    private PlayerStats mockStats;
    private DebugConsole mockConsole;
    private SetLevelCommand command;

    @BeforeEach
    void setUp() {
        mockPlayer = mock(Player.class);
        mockStats = mock(PlayerStats.class);
        mockConsole = mock(DebugConsole.class);

        when(mockPlayer.getStats()).thenReturn(mockStats);

        command = new SetLevelCommand(mockPlayer);
    }

    @Nested
    @DisplayName("Command Properties")
    class CommandProperties {

        @Test
        @DisplayName("getName should return 'setlevel'")
        void testGetName() {
            assertEquals("setlevel", command.getName());
        }

        @Test
        @DisplayName("getDescription should return usage information")
        void testGetDescription() {
            String description = command.getDescription();
            assertNotNull(description);
            assertTrue(description.contains("setlevel") || description.contains("level"));
        }
    }

    @Nested
    @DisplayName("Valid Execution")
    class ValidExecution {

        @Test
        @DisplayName("Should set level to valid value")
        void testSetLevel() {
            when(mockStats.getLevel()).thenReturn(50);
            when(mockStats.getCurrentHealth()).thenReturn(500f);
            when(mockStats.getMaxHealth()).thenReturn(500f);

            command.execute(new String[]{"50"}, mockConsole);

            verify(mockStats).setLevel(50);
            verify(mockConsole).log(contains("Set level to 50"));
        }

        @Test
        @DisplayName("Should set level to 1")
        void testSetLevelToOne() {
            when(mockStats.getLevel()).thenReturn(1);
            when(mockStats.getCurrentHealth()).thenReturn(100f);
            when(mockStats.getMaxHealth()).thenReturn(100f);

            command.execute(new String[]{"1"}, mockConsole);

            verify(mockStats).setLevel(1);
        }

        @Test
        @DisplayName("Should set level to 100")
        void testSetLevelToMax() {
            when(mockStats.getLevel()).thenReturn(100);
            when(mockStats.getCurrentHealth()).thenReturn(1000f);
            when(mockStats.getMaxHealth()).thenReturn(1000f);

            command.execute(new String[]{"100"}, mockConsole);

            verify(mockStats).setLevel(100);
        }
    }

    @Nested
    @DisplayName("Invalid Input")
    class InvalidInput {

        @Test
        @DisplayName("Should show current level when no arguments provided")
        void testNoArguments() {
            when(mockStats.getLevel()).thenReturn(25);

            command.execute(new String[]{}, mockConsole);

            verify(mockConsole).log(contains("Usage"));
            verify(mockConsole).log(contains("Current level"));
            verify(mockStats, never()).setLevel(anyInt());
        }

        @Test
        @DisplayName("Should reject level < 1")
        void testLevelTooLow() {
            command.execute(new String[]{"0"}, mockConsole);

            verify(mockConsole).log(contains("Level must be between 1 and 100"));
            verify(mockStats, never()).setLevel(anyInt());
        }

        @Test
        @DisplayName("Should reject level > 100")
        void testLevelTooHigh() {
            command.execute(new String[]{"101"}, mockConsole);

            verify(mockConsole).log(contains("Level must be between 1 and 100"));
            verify(mockStats, never()).setLevel(anyInt());
        }

        @Test
        @DisplayName("Should handle invalid number format")
        void testInvalidNumberFormat() {
            command.execute(new String[]{"abc"}, mockConsole);

            verify(mockConsole).log(contains("Invalid level"));
            verify(mockStats, never()).setLevel(anyInt());
        }

        @Test
        @DisplayName("Should handle negative level")
        void testNegativeLevel() {
            command.execute(new String[]{"-5"}, mockConsole);

            verify(mockConsole).log(contains("Level must be between 1 and 100"));
            verify(mockStats, never()).setLevel(anyInt());
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should ignore extra arguments")
        void testExtraArguments() {
            when(mockStats.getLevel()).thenReturn(10);
            when(mockStats.getCurrentHealth()).thenReturn(100f);
            when(mockStats.getMaxHealth()).thenReturn(100f);

            command.execute(new String[]{"10", "extra"}, mockConsole);

            verify(mockStats).setLevel(10);
        }

        @Test
        @DisplayName("Should handle empty string")
        void testEmptyString() {
            command.execute(new String[]{""}, mockConsole);

            verify(mockConsole).log(contains("Invalid level"));
            verify(mockStats, never()).setLevel(anyInt());
        }
    }
}
