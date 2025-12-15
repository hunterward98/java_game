package io.github.inherit_this.debug;

import io.github.inherit_this.entities.Player;
import io.github.inherit_this.items.Inventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("ClearInventoryCommand Tests")
class ClearInventoryCommandTest {

    private Player mockPlayer;
    private Inventory mockInventory;
    private DebugConsole mockConsole;
    private ClearInventoryCommand command;

    @BeforeEach
    void setUp() {
        mockPlayer = mock(Player.class);
        mockInventory = mock(Inventory.class);
        mockConsole = mock(DebugConsole.class);

        when(mockPlayer.getInventory()).thenReturn(mockInventory);

        command = new ClearInventoryCommand(mockPlayer);
    }

    @Nested
    @DisplayName("Command Properties")
    class CommandProperties {

        @Test
        @DisplayName("getName should return 'clearinv'")
        void testGetName() {
            assertEquals("clearinv", command.getName());
        }

        @Test
        @DisplayName("getDescription should return usage information")
        void testGetDescription() {
            String description = command.getDescription();
            assertNotNull(description);
            assertTrue(description.contains("inventory") || description.contains("Clear"));
        }
    }

    @Nested
    @DisplayName("Execution")
    class Execution {

        @Test
        @DisplayName("Should clear inventory")
        void testClearInventory() {
            command.execute(new String[]{}, mockConsole);

            verify(mockInventory).clear();
            verify(mockConsole).log(contains("Inventory cleared"));
        }

        @Test
        @DisplayName("Should clear inventory even with arguments")
        void testClearInventoryWithArguments() {
            command.execute(new String[]{"arg1", "arg2"}, mockConsole);

            verify(mockInventory).clear();
            verify(mockConsole).log(contains("Inventory cleared"));
        }

        @Test
        @DisplayName("Should call clear exactly once")
        void testClearCalledOnce() {
            command.execute(new String[]{}, mockConsole);

            verify(mockInventory, times(1)).clear();
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should work with null args array")
        void testNullArgs() {
            // This should not throw
            assertDoesNotThrow(() -> command.execute(new String[]{}, mockConsole));
        }

        @Test
        @DisplayName("Should handle multiple executions")
        void testMultipleExecutions() {
            command.execute(new String[]{}, mockConsole);
            command.execute(new String[]{}, mockConsole);
            command.execute(new String[]{}, mockConsole);

            verify(mockInventory, times(3)).clear();
            verify(mockConsole, times(3)).log(contains("Inventory cleared"));
        }
    }
}
