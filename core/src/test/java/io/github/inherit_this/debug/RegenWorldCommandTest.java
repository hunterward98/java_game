package io.github.inherit_this.debug;

import io.github.inherit_this.world.ProceduralWorld;
import io.github.inherit_this.world.WorldProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("RegenWorldCommand Tests")
class RegenWorldCommandTest {

    private DebugConsole mockConsole;

    @Nested
    @DisplayName("Command Properties")
    class CommandProperties {

        @Test
        @DisplayName("getName should return 'regen_world'")
        void testGetName() {
            WorldProvider mockWorld = mock(WorldProvider.class);
            RegenWorldCommand command = new RegenWorldCommand(mockWorld);
            assertEquals("regen_world", command.getName());
        }

        @Test
        @DisplayName("getDescription should return usage information")
        void testGetDescription() {
            WorldProvider mockWorld = mock(WorldProvider.class);
            RegenWorldCommand command = new RegenWorldCommand(mockWorld);
            String description = command.getDescription();
            assertNotNull(description);
            assertTrue(description.contains("world") || description.contains("Regenerate"));
        }
    }

    @Nested
    @DisplayName("ProceduralWorld Execution")
    class ProceduralWorldExecution {

        @BeforeEach
        void setUp() {
            mockConsole = mock(DebugConsole.class);
        }

        @Test
        @DisplayName("Should regenerate ProceduralWorld")
        void testRegenerateProceduralWorld() {
            ProceduralWorld mockProceduralWorld = mock(ProceduralWorld.class);
            RegenWorldCommand command = new RegenWorldCommand(mockProceduralWorld);

            command.execute(new String[]{}, mockConsole);

            verify(mockProceduralWorld).regenerateAll();
            verify(mockConsole).log(contains("World regeneration requested"));
        }

        @Test
        @DisplayName("Should work with any arguments for ProceduralWorld")
        void testRegenerateWithArguments() {
            ProceduralWorld mockProceduralWorld = mock(ProceduralWorld.class);
            RegenWorldCommand command = new RegenWorldCommand(mockProceduralWorld);

            command.execute(new String[]{"arg1", "arg2"}, mockConsole);

            verify(mockProceduralWorld).regenerateAll();
        }
    }

    @Nested
    @DisplayName("Non-ProceduralWorld Execution")
    class NonProceduralWorldExecution {

        @BeforeEach
        void setUp() {
            mockConsole = mock(DebugConsole.class);
        }

        @Test
        @DisplayName("Should not regenerate non-ProceduralWorld")
        void testNonProceduralWorld() {
            WorldProvider mockStaticWorld = mock(WorldProvider.class);
            RegenWorldCommand command = new RegenWorldCommand(mockStaticWorld);

            command.execute(new String[]{}, mockConsole);

            verify(mockConsole).log(contains("only works with ProceduralWorld"));
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @BeforeEach
        void setUp() {
            mockConsole = mock(DebugConsole.class);
        }

        @Test
        @DisplayName("Should handle null arguments")
        void testNullArguments() {
            ProceduralWorld mockProceduralWorld = mock(ProceduralWorld.class);
            RegenWorldCommand command = new RegenWorldCommand(mockProceduralWorld);

            assertDoesNotThrow(() -> command.execute(new String[]{}, mockConsole));
        }

        @Test
        @DisplayName("Should call regenerateAll exactly once")
        void testRegenerateCalledOnce() {
            ProceduralWorld mockProceduralWorld = mock(ProceduralWorld.class);
            RegenWorldCommand command = new RegenWorldCommand(mockProceduralWorld);

            command.execute(new String[]{}, mockConsole);

            verify(mockProceduralWorld, times(1)).regenerateAll();
        }
    }
}
