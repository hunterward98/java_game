package io.github.inherit_this.debug;

import io.github.inherit_this.entities.Player;
import io.github.inherit_this.entities.PlayerStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("AddXPCommand Tests")
class AddXPCommandTest {

    private Player mockPlayer;
    private PlayerStats mockStats;
    private DebugConsole mockConsole;
    private AddXPCommand command;

    @BeforeEach
    void setUp() {
        mockPlayer = mock(Player.class);
        mockStats = mock(PlayerStats.class);
        mockConsole = mock(DebugConsole.class);

        when(mockPlayer.getStats()).thenReturn(mockStats);

        command = new AddXPCommand(mockPlayer);
    }

    @Nested
    @DisplayName("Command Properties")
    class CommandProperties {

        @Test
        @DisplayName("getName should return 'addxp'")
        void testGetName() {
            assertEquals("addxp", command.getName());
        }

        @Test
        @DisplayName("getDescription should return usage information")
        void testGetDescription() {
            String description = command.getDescription();
            assertNotNull(description);
            assertTrue(description.contains("addxp"));
            assertTrue(description.contains("amount") || description.contains("XP"));
        }
    }

    @Nested
    @DisplayName("Valid Execution")
    class ValidExecution {

        @Test
        @DisplayName("Should add XP without level up")
        void testAddXPWithoutLevelUp() {
            when(mockStats.addXP(100f)).thenReturn(0);
            when(mockStats.getCurrentXP()).thenReturn(100f);
            when(mockStats.getXPRequiredForNextLevel()).thenReturn(200f);

            command.execute(new String[]{"100"}, mockConsole);

            verify(mockStats).addXP(100f);
            verify(mockConsole).log(contains("Added 100"));
            verify(mockConsole).log(contains("100/200"));
        }

        @Test
        @DisplayName("Should add XP with level up")
        void testAddXPWithLevelUp() {
            when(mockStats.addXP(500f)).thenReturn(2);
            when(mockStats.getLevel()).thenReturn(3);
            when(mockStats.getCurrentXP()).thenReturn(50f);
            when(mockStats.getXPRequiredForNextLevel()).thenReturn(300f);

            command.execute(new String[]{"500"}, mockConsole);

            verify(mockStats).addXP(500f);
            verify(mockConsole).log(contains("Leveled up"));
            verify(mockConsole).log(contains("Now level 3"));
        }

        @Test
        @DisplayName("Should handle decimal XP values")
        void testDecimalXPValues() {
            when(mockStats.addXP(50.5f)).thenReturn(0);
            when(mockStats.getCurrentXP()).thenReturn(50.5f);
            when(mockStats.getXPRequiredForNextLevel()).thenReturn(100f);

            command.execute(new String[]{"50.5"}, mockConsole);

            verify(mockStats).addXP(50.5f);
            verify(mockConsole).log(contains("Added 50.5"));
        }
    }

    @Nested
    @DisplayName("Invalid Input")
    class InvalidInput {

        @Test
        @DisplayName("Should show usage when no arguments provided")
        void testNoArguments() {
            command.execute(new String[]{}, mockConsole);

            verify(mockConsole, atLeastOnce()).log(contains("Usage"));
            verify(mockConsole, atLeastOnce()).log(contains("addxp"));
            verifyNoInteractions(mockStats);
        }

        @Test
        @DisplayName("Should handle invalid number format")
        void testInvalidNumberFormat() {
            command.execute(new String[]{"abc"}, mockConsole);

            verify(mockConsole).log(contains("Invalid amount"));
            verify(mockStats, never()).addXP(anyFloat());
        }

        @Test
        @DisplayName("Should handle empty string")
        void testEmptyString() {
            command.execute(new String[]{""}, mockConsole);

            verify(mockConsole).log(contains("Invalid amount"));
            verify(mockStats, never()).addXP(anyFloat());
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle zero XP")
        void testZeroXP() {
            when(mockStats.addXP(0f)).thenReturn(0);
            when(mockStats.getCurrentXP()).thenReturn(0f);
            when(mockStats.getXPRequiredForNextLevel()).thenReturn(100f);

            command.execute(new String[]{"0"}, mockConsole);

            verify(mockStats).addXP(0f);
        }

        @Test
        @DisplayName("Should handle negative XP")
        void testNegativeXP() {
            when(mockStats.addXP(-50f)).thenReturn(0);
            when(mockStats.getCurrentXP()).thenReturn(0f);
            when(mockStats.getXPRequiredForNextLevel()).thenReturn(100f);

            command.execute(new String[]{"-50"}, mockConsole);

            verify(mockStats).addXP(-50f);
        }

        @Test
        @DisplayName("Should handle very large XP values")
        void testLargeXPValues() {
            when(mockStats.addXP(999999f)).thenReturn(10);
            when(mockStats.getLevel()).thenReturn(50);
            when(mockStats.getCurrentXP()).thenReturn(1000f);
            when(mockStats.getXPRequiredForNextLevel()).thenReturn(5000f);

            command.execute(new String[]{"999999"}, mockConsole);

            verify(mockStats).addXP(999999f);
        }

        @Test
        @DisplayName("Should ignore extra arguments")
        void testExtraArguments() {
            when(mockStats.addXP(100f)).thenReturn(0);
            when(mockStats.getCurrentXP()).thenReturn(100f);
            when(mockStats.getXPRequiredForNextLevel()).thenReturn(200f);

            command.execute(new String[]{"100", "extra", "args"}, mockConsole);

            verify(mockStats).addXP(100f);
        }
    }
}
