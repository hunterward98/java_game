package io.github.inherit_this.debug;

import io.github.inherit_this.entities.Player;
import io.github.inherit_this.items.Inventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("SetGoldCommand Tests")
class SetGoldCommandTest {

    private Player mockPlayer;
    private Inventory mockInventory;
    private DebugConsole mockConsole;
    private SetGoldCommand command;

    @BeforeEach
    void setUp() {
        mockPlayer = mock(Player.class);
        mockInventory = mock(Inventory.class);
        mockConsole = mock(DebugConsole.class);

        when(mockPlayer.getInventory()).thenReturn(mockInventory);

        command = new SetGoldCommand(mockPlayer);
    }

    @Nested
    @DisplayName("Command Properties")
    class CommandProperties {

        @Test
        @DisplayName("getName should return 'setgold'")
        void testGetName() {
            assertEquals("setgold", command.getName());
        }

        @Test
        @DisplayName("getDescription should return usage information")
        void testGetDescription() {
            String description = command.getDescription();
            assertNotNull(description);
            assertTrue(description.contains("setgold") || description.contains("gold"));
        }
    }

    @Nested
    @DisplayName("Valid Execution")
    class ValidExecution {

        @Test
        @DisplayName("Should set gold to valid amount")
        void testSetGold() {
            when(mockInventory.getGold()).thenReturn(1000);

            command.execute(new String[]{"1000"}, mockConsole);

            verify(mockInventory).setGold(1000);
            verify(mockConsole).log(contains("Set gold to 1000"));
        }

        @Test
        @DisplayName("Should set gold to zero")
        void testSetGoldToZero() {
            when(mockInventory.getGold()).thenReturn(0);

            command.execute(new String[]{"0"}, mockConsole);

            verify(mockInventory).setGold(0);
        }

        @Test
        @DisplayName("Should set gold to large amount")
        void testSetGoldToLargeAmount() {
            when(mockInventory.getGold()).thenReturn(999999);

            command.execute(new String[]{"999999"}, mockConsole);

            verify(mockInventory).setGold(999999);
        }
    }

    @Nested
    @DisplayName("Invalid Input")
    class InvalidInput {

        @Test
        @DisplayName("Should show current gold when no arguments provided")
        void testNoArguments() {
            when(mockInventory.getGold()).thenReturn(500);

            command.execute(new String[]{}, mockConsole);

            verify(mockConsole).log(contains("Usage"));
            verify(mockConsole).log(contains("Current gold"));
            verify(mockInventory, never()).setGold(anyInt());
        }

        @Test
        @DisplayName("Should handle invalid number format")
        void testInvalidNumberFormat() {
            command.execute(new String[]{"abc"}, mockConsole);

            verify(mockConsole).log(contains("Invalid amount"));
            verify(mockInventory, never()).setGold(anyInt());
        }

        @Test
        @DisplayName("Should handle empty string")
        void testEmptyString() {
            command.execute(new String[]{""}, mockConsole);

            verify(mockConsole).log(contains("Invalid amount"));
            verify(mockInventory, never()).setGold(anyInt());
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle negative gold")
        void testNegativeGold() {
            when(mockInventory.getGold()).thenReturn(-100);

            command.execute(new String[]{"-100"}, mockConsole);

            verify(mockInventory).setGold(-100);
        }

        @Test
        @DisplayName("Should ignore extra arguments")
        void testExtraArguments() {
            when(mockInventory.getGold()).thenReturn(500);

            command.execute(new String[]{"500", "extra"}, mockConsole);

            verify(mockInventory).setGold(500);
        }
    }
}
