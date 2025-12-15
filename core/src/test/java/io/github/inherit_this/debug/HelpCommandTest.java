package io.github.inherit_this.debug;

import com.badlogic.gdx.utils.ObjectMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("HelpCommand Tests")
class HelpCommandTest {

    private ObjectMap<String, DebugCommand> mockRegistry;
    private DebugConsole mockConsole;
    private HelpCommand command;

    @BeforeEach
    void setUp() {
        mockRegistry = new ObjectMap<>();
        mockConsole = mock(DebugConsole.class);

        // Add some mock commands to registry
        DebugCommand mockCmd1 = mock(DebugCommand.class);
        when(mockCmd1.getName()).thenReturn("test1");
        when(mockCmd1.getDescription()).thenReturn("Test command 1");
        mockRegistry.put("test1", mockCmd1);

        DebugCommand mockCmd2 = mock(DebugCommand.class);
        when(mockCmd2.getName()).thenReturn("test2");
        when(mockCmd2.getDescription()).thenReturn("Test command 2");
        mockRegistry.put("test2", mockCmd2);

        command = new HelpCommand(mockRegistry);
    }

    @Nested
    @DisplayName("Command Properties")
    class CommandProperties {

        @Test
        @DisplayName("getName should return 'help'")
        void testGetName() {
            assertEquals("help", command.getName());
        }

        @Test
        @DisplayName("getDescription should return usage information")
        void testGetDescription() {
            String description = command.getDescription();
            assertNotNull(description);
            assertTrue(description.contains("help") || description.contains("command"));
        }
    }

    @Nested
    @DisplayName("Execution Without Arguments")
    class ExecutionWithoutArguments {

        @Test
        @DisplayName("Should list all commands when no arguments provided")
        void testListAllCommands() {
            command.execute(new String[]{}, mockConsole);

            verify(mockConsole).log("Available Commands:");
            verify(mockConsole, atLeastOnce()).log(contains("test1"));
            verify(mockConsole, atLeastOnce()).log(contains("test2"));
        }

        @Test
        @DisplayName("Should list command names with descriptions")
        void testListIncludesDescriptions() {
            command.execute(new String[]{}, mockConsole);

            verify(mockConsole).log(contains("Test command 1"));
            verify(mockConsole).log(contains("Test command 2"));
        }
    }

    @Nested
    @DisplayName("Execution With Specific Command")
    class ExecutionWithSpecificCommand {

        @Test
        @DisplayName("Should show specific command help when requested")
        void testSpecificCommandHelp() {
            command.execute(new String[]{"test1"}, mockConsole);

            verify(mockConsole).log(contains("test1"));
            verify(mockConsole).log(contains("Test command 1"));
        }

        @Test
        @DisplayName("Should show error for unknown command")
        void testUnknownCommand() {
            command.execute(new String[]{"unknown"}, mockConsole);

            verify(mockConsole).log(contains("Unknown command"));
            verify(mockConsole).log(contains("unknown"));
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle empty registry")
        void testEmptyRegistry() {
            HelpCommand emptyCommand = new HelpCommand(new ObjectMap<>());
            emptyCommand.execute(new String[]{}, mockConsole);

            verify(mockConsole).log("Available Commands:");
        }

        @Test
        @DisplayName("Should ignore extra arguments")
        void testExtraArguments() {
            command.execute(new String[]{"test1", "extra", "args"}, mockConsole);

            verify(mockConsole).log(contains("test1"));
        }
    }
}
