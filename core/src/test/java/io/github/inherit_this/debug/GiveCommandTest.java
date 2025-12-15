package io.github.inherit_this.debug;

import io.github.inherit_this.entities.Player;
import io.github.inherit_this.items.Inventory;
import io.github.inherit_this.items.Item;
import io.github.inherit_this.items.ItemRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("GiveCommand Tests")
class GiveCommandTest {

    private Player mockPlayer;
    private Inventory mockInventory;
    private DebugConsole mockConsole;
    private ItemRegistry mockItemRegistry;
    private GiveCommand command;

    @BeforeEach
    void setUp() {
        mockPlayer = mock(Player.class);
        mockInventory = mock(Inventory.class);
        mockConsole = mock(DebugConsole.class);
        mockItemRegistry = mock(ItemRegistry.class);

        when(mockPlayer.getInventory()).thenReturn(mockInventory);

        command = new GiveCommand(mockPlayer);
    }

    @Nested
    @DisplayName("Command Properties")
    class CommandProperties {

        @Test
        @DisplayName("getName should return 'give'")
        void testGetName() {
            assertEquals("give", command.getName());
        }

        @Test
        @DisplayName("getDescription should return usage information")
        void testGetDescription() {
            String description = command.getDescription();
            assertNotNull(description);
            assertTrue(description.contains("give") || description.contains("item"));
        }
    }

    @Nested
    @DisplayName("Valid Execution")
    class ValidExecution {

        @Test
        @DisplayName("Should give item with default amount of 1")
        void testGiveItemDefaultAmount() {
            Item mockItem = mock(Item.class);
            when(mockItem.getName()).thenReturn("Iron Sword");

            try (MockedStatic<ItemRegistry> mockedRegistry = mockStatic(ItemRegistry.class)) {
                ItemRegistry mockRegistry = mock(ItemRegistry.class);
                mockedRegistry.when(ItemRegistry::getInstance).thenReturn(mockRegistry);
                when(mockRegistry.getItem("iron_sword")).thenReturn(mockItem);
                when(mockInventory.addItem(mockItem, 1)).thenReturn(true);

                command.execute(new String[]{"iron_sword"}, mockConsole);

                verify(mockInventory).addItem(mockItem, 1);
                verify(mockConsole).log(contains("Gave 1x Iron Sword"));
            }
        }

        @Test
        @DisplayName("Should give item with specified amount")
        void testGiveItemSpecifiedAmount() {
            Item mockItem = mock(Item.class);
            when(mockItem.getName()).thenReturn("Health Potion");

            try (MockedStatic<ItemRegistry> mockedRegistry = mockStatic(ItemRegistry.class)) {
                ItemRegistry mockRegistry = mock(ItemRegistry.class);
                mockedRegistry.when(ItemRegistry::getInstance).thenReturn(mockRegistry);
                when(mockRegistry.getItem("health_potion")).thenReturn(mockItem);
                when(mockInventory.addItem(mockItem, 5)).thenReturn(true);

                command.execute(new String[]{"health_potion", "5"}, mockConsole);

                verify(mockInventory).addItem(mockItem, 5);
                verify(mockConsole).log(contains("Gave 5x Health Potion"));
            }
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
            verify(mockConsole, atLeastOnce()).log(contains("give"));
            verifyNoInteractions(mockInventory);
        }

        @Test
        @DisplayName("Should handle unknown item ID")
        void testUnknownItem() {
            try (MockedStatic<ItemRegistry> mockedRegistry = mockStatic(ItemRegistry.class)) {
                ItemRegistry mockRegistry = mock(ItemRegistry.class);
                mockedRegistry.when(ItemRegistry::getInstance).thenReturn(mockRegistry);
                when(mockRegistry.getItem("unknown_item")).thenReturn(null);

                command.execute(new String[]{"unknown_item"}, mockConsole);

                verify(mockConsole).log(contains("Unknown item"));
                verify(mockInventory, never()).addItem(any(), anyInt());
            }
        }

        @Test
        @DisplayName("Should handle invalid amount")
        void testInvalidAmount() {
            command.execute(new String[]{"iron_sword", "abc"}, mockConsole);

            verify(mockConsole).log(contains("Invalid amount"));
            verify(mockInventory, never()).addItem(any(), anyInt());
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle inventory full")
        void testInventoryFull() {
            Item mockItem = mock(Item.class);
            when(mockItem.getName()).thenReturn("Iron Sword");

            try (MockedStatic<ItemRegistry> mockedRegistry = mockStatic(ItemRegistry.class)) {
                ItemRegistry mockRegistry = mock(ItemRegistry.class);
                mockedRegistry.when(ItemRegistry::getInstance).thenReturn(mockRegistry);
                when(mockRegistry.getItem("iron_sword")).thenReturn(mockItem);
                when(mockInventory.addItem(mockItem, 1)).thenReturn(false);

                command.execute(new String[]{"iron_sword"}, mockConsole);

                verify(mockConsole).log(contains("Failed to add item"));
            }
        }
    }
}
