package io.github.inherit_this.debug;

import io.github.inherit_this.entities.Player;
import io.github.inherit_this.entities.PlayerStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("SetHealthCommand Tests")
class SetHealthCommandTest {

    private Player mockPlayer;
    private PlayerStats mockStats;
    private DebugConsole mockConsole;
    private SetHealthCommand command;

    @BeforeEach
    void setUp() {
        mockPlayer = mock(Player.class);
        mockStats = mock(PlayerStats.class);
        mockConsole = mock(DebugConsole.class);

        when(mockPlayer.getStats()).thenReturn(mockStats);

        command = new SetHealthCommand(mockPlayer);
    }

    @Nested
    @DisplayName("Command Properties")
    class CommandProperties {

        @Test
        @DisplayName("getName should return 'sethealth'")
        void testGetName() {
            assertEquals("sethealth", command.getName());
        }

        @Test
        @DisplayName("getDescription should return usage information")
        void testGetDescription() {
            String description = command.getDescription();
            assertNotNull(description);
            assertTrue(description.contains("sethealth") || description.contains("health"));
        }
    }

    @Nested
    @DisplayName("Valid Execution")
    class ValidExecution {

        @Test
        @DisplayName("Should set health to valid value")
        void testSetHealth() {
            when(mockStats.getCurrentHealth()).thenReturn(100f);
            when(mockStats.getMaxHealth()).thenReturn(100f);

            command.execute(new String[]{"100"}, mockConsole);

            verify(mockStats).setHealth(100f);
            verify(mockConsole).log(contains("Set health"));
        }

        @Test
        @DisplayName("Should handle decimal health values")
        void testDecimalHealth() {
            when(mockStats.getCurrentHealth()).thenReturn(75.5f);
            when(mockStats.getMaxHealth()).thenReturn(100f);

            command.execute(new String[]{"75.5"}, mockConsole);

            verify(mockStats).setHealth(75.5f);
        }

        @Test
        @DisplayName("Should set health to zero")
        void testSetHealthToZero() {
            when(mockStats.getCurrentHealth()).thenReturn(0f);
            when(mockStats.getMaxHealth()).thenReturn(100f);

            command.execute(new String[]{"0"}, mockConsole);

            verify(mockStats).setHealth(0f);
        }
    }

    @Nested
    @DisplayName("Invalid Input")
    class InvalidInput {

        @Test
        @DisplayName("Should show current health when no arguments provided")
        void testNoArguments() {
            when(mockStats.getCurrentHealth()).thenReturn(75f);
            when(mockStats.getMaxHealth()).thenReturn(100f);

            command.execute(new String[]{}, mockConsole);

            verify(mockConsole).log(contains("Usage"));
            verify(mockConsole).log(contains("Current health"));
            verify(mockStats, never()).setHealth(anyFloat());
        }

        @Test
        @DisplayName("Should handle invalid number format")
        void testInvalidNumberFormat() {
            command.execute(new String[]{"abc"}, mockConsole);

            verify(mockConsole).log(contains("Invalid amount"));
            verify(mockStats, never()).setHealth(anyFloat());
        }

        @Test
        @DisplayName("Should handle empty string")
        void testEmptyString() {
            command.execute(new String[]{""}, mockConsole);

            verify(mockConsole).log(contains("Invalid amount"));
            verify(mockStats, never()).setHealth(anyFloat());
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle negative health")
        void testNegativeHealth() {
            when(mockStats.getCurrentHealth()).thenReturn(-10f);
            when(mockStats.getMaxHealth()).thenReturn(100f);

            command.execute(new String[]{"-10"}, mockConsole);

            verify(mockStats).setHealth(-10f);
        }

        @Test
        @DisplayName("Should handle very large health values")
        void testLargeHealth() {
            when(mockStats.getCurrentHealth()).thenReturn(99999f);
            when(mockStats.getMaxHealth()).thenReturn(99999f);

            command.execute(new String[]{"99999"}, mockConsole);

            verify(mockStats).setHealth(99999f);
        }

        @Test
        @DisplayName("Should ignore extra arguments")
        void testExtraArguments() {
            when(mockStats.getCurrentHealth()).thenReturn(50f);
            when(mockStats.getMaxHealth()).thenReturn(100f);

            command.execute(new String[]{"50", "extra"}, mockConsole);

            verify(mockStats).setHealth(50f);
        }
    }
}
