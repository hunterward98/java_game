package io.github.inherit_this.debug;

import io.github.inherit_this.entities.Player;
import io.github.inherit_this.entities.PlayerStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("SetCoinsCommand Tests")
class SetGoldCommandTest {

    private Player mockPlayer;
    private PlayerStats mockStats;
    private DebugConsole mockConsole;
    private SetGoldCommand command;

    @BeforeEach
    void setUp() {
        mockPlayer = mock(Player.class);
        mockStats = mock(PlayerStats.class);
        mockConsole = mock(DebugConsole.class);

        when(mockPlayer.getStats()).thenReturn(mockStats);

        command = new SetGoldCommand(mockPlayer);
    }

    @Nested
    @DisplayName("Command Properties")
    class CommandProperties {

        @Test
        @DisplayName("getName should return 'setcoins'")
        void testGetName() {
            assertEquals("setcoins", command.getName());
        }

        @Test
        @DisplayName("getDescription should return usage information")
        void testGetDescription() {
            String description = command.getDescription();
            assertNotNull(description);
            assertTrue(description.contains("setcoins") || description.contains("coins"));
        }
    }

    @Nested
    @DisplayName("Valid Execution")
    class ValidExecution {

        @Test
        @DisplayName("Should set coins to valid amount")
        void testSetCoins() {
            when(mockStats.getCoins()).thenReturn(1000);

            command.execute(new String[]{"1000"}, mockConsole);

            verify(mockStats).setCoins(1000);
            verify(mockConsole).log(contains("Set coins to 1000"));
        }

        @Test
        @DisplayName("Should set coins to zero")
        void testSetCoinsToZero() {
            when(mockStats.getCoins()).thenReturn(0);

            command.execute(new String[]{"0"}, mockConsole);

            verify(mockStats).setCoins(0);
        }

        @Test
        @DisplayName("Should set coins to large amount")
        void testSetCoinsToLargeAmount() {
            when(mockStats.getCoins()).thenReturn(999999);

            command.execute(new String[]{"999999"}, mockConsole);

            verify(mockStats).setCoins(999999);
        }
    }

    @Nested
    @DisplayName("Invalid Input")
    class InvalidInput {

        @Test
        @DisplayName("Should show current coins when no arguments provided")
        void testNoArguments() {
            when(mockStats.getCoins()).thenReturn(500);

            command.execute(new String[]{}, mockConsole);

            verify(mockConsole).log(contains("Usage"));
            verify(mockConsole).log(contains("Current coins"));
            verify(mockStats, never()).setCoins(anyInt());
        }

        @Test
        @DisplayName("Should handle invalid number format")
        void testInvalidNumberFormat() {
            command.execute(new String[]{"abc"}, mockConsole);

            verify(mockConsole).log(contains("Invalid amount"));
            verify(mockStats, never()).setCoins(anyInt());
        }

        @Test
        @DisplayName("Should handle empty string")
        void testEmptyString() {
            command.execute(new String[]{""}, mockConsole);

            verify(mockConsole).log(contains("Invalid amount"));
            verify(mockStats, never()).setCoins(anyInt());
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle negative coins")
        void testNegativeCoins() {
            when(mockStats.getCoins()).thenReturn(-100);

            command.execute(new String[]{"-100"}, mockConsole);

            verify(mockStats).setCoins(-100);
        }

        @Test
        @DisplayName("Should ignore extra arguments")
        void testExtraArguments() {
            when(mockStats.getCoins()).thenReturn(500);

            command.execute(new String[]{"500", "extra"}, mockConsole);

            verify(mockStats).setCoins(500);
        }
    }
}

