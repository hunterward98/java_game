package io.github.inherit_this.debug;

import io.github.inherit_this.entities.Player;
import io.github.inherit_this.world.Chunk;
import io.github.inherit_this.world.ProceduralWorld;
import io.github.inherit_this.world.WorldProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("ReloadChunkCommand Tests")
class ReloadChunkCommandTest {

    private Player mockPlayer;
    private DebugConsole mockConsole;

    @BeforeEach
    void setUp() {
        mockPlayer = mock(Player.class);
        mockConsole = mock(DebugConsole.class);
    }

    @Nested
    @DisplayName("Command Properties")
    class CommandProperties {

        @Test
        @DisplayName("getName should return 'reload_chunk'")
        void testGetName() {
            WorldProvider mockWorld = mock(WorldProvider.class);
            ReloadChunkCommand command = new ReloadChunkCommand(mockWorld, mockPlayer);
            assertEquals("reload_chunk", command.getName());
        }

        @Test
        @DisplayName("getDescription should return usage information")
        void testGetDescription() {
            WorldProvider mockWorld = mock(WorldProvider.class);
            ReloadChunkCommand command = new ReloadChunkCommand(mockWorld, mockPlayer);
            String description = command.getDescription();
            assertNotNull(description);
            assertTrue(description.contains("chunk") || description.contains("Reload"));
        }
    }

    @Nested
    @DisplayName("ProceduralWorld Execution")
    class ProceduralWorldExecution {

        @Test
        @DisplayName("Should reload chunk in ProceduralWorld")
        void testReloadChunkInProceduralWorld() {
            ProceduralWorld mockProceduralWorld = mock(ProceduralWorld.class);
            Chunk mockChunk = mock(Chunk.class);
            when(mockChunk.getChunkX()).thenReturn(5);
            when(mockChunk.getChunkY()).thenReturn(10);
            when(mockProceduralWorld.getActiveChunk(0, 0)).thenReturn(mockChunk);

            ReloadChunkCommand command = new ReloadChunkCommand(mockProceduralWorld, mockPlayer);
            command.execute(new String[]{}, mockConsole);

            verify(mockProceduralWorld).reloadChunk(5, 10);
            verify(mockConsole).log(contains("Reloaded chunk (5, 10)"));
        }

        @Test
        @DisplayName("Should handle null chunk")
        void testNullChunk() {
            ProceduralWorld mockProceduralWorld = mock(ProceduralWorld.class);
            when(mockProceduralWorld.getActiveChunk(0, 0)).thenReturn(null);

            ReloadChunkCommand command = new ReloadChunkCommand(mockProceduralWorld, mockPlayer);
            command.execute(new String[]{}, mockConsole);

            verify(mockConsole).log(contains("No active chunk to reload"));
            verify(mockProceduralWorld, never()).reloadChunk(anyInt(), anyInt());
        }
    }

    @Nested
    @DisplayName("Non-ProceduralWorld Execution")
    class NonProceduralWorldExecution {

        @Test
        @DisplayName("Should not reload chunk in non-ProceduralWorld")
        void testNonProceduralWorld() {
            WorldProvider mockStaticWorld = mock(WorldProvider.class);
            ReloadChunkCommand command = new ReloadChunkCommand(mockStaticWorld, mockPlayer);

            command.execute(new String[]{}, mockConsole);

            verify(mockConsole).log(contains("only works with ProceduralWorld"));
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should work with any arguments")
        void testWithArguments() {
            ProceduralWorld mockProceduralWorld = mock(ProceduralWorld.class);
            Chunk mockChunk = mock(Chunk.class);
            when(mockChunk.getChunkX()).thenReturn(0);
            when(mockChunk.getChunkY()).thenReturn(0);
            when(mockProceduralWorld.getActiveChunk(0, 0)).thenReturn(mockChunk);

            ReloadChunkCommand command = new ReloadChunkCommand(mockProceduralWorld, mockPlayer);
            command.execute(new String[]{"arg1", "arg2"}, mockConsole);

            verify(mockProceduralWorld).reloadChunk(0, 0);
        }

        @Test
        @DisplayName("Should handle chunk at origin")
        void testChunkAtOrigin() {
            ProceduralWorld mockProceduralWorld = mock(ProceduralWorld.class);
            Chunk mockChunk = mock(Chunk.class);
            when(mockChunk.getChunkX()).thenReturn(0);
            when(mockChunk.getChunkY()).thenReturn(0);
            when(mockProceduralWorld.getActiveChunk(0, 0)).thenReturn(mockChunk);

            ReloadChunkCommand command = new ReloadChunkCommand(mockProceduralWorld, mockPlayer);
            command.execute(new String[]{}, mockConsole);

            verify(mockProceduralWorld).reloadChunk(0, 0);
        }
    }
}
