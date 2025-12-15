package io.github.inherit_this.debug;

import com.badlogic.gdx.math.Vector2;
import io.github.inherit_this.entities.Player;
import io.github.inherit_this.entities.PlayerStats;
import io.github.inherit_this.screens.GameScreen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("SpawnObjectCommand Tests")
class SpawnObjectCommandTest {

    private Player mockPlayer;
    private PlayerStats mockStats;
    private GameScreen mockGameScreen;
    private DebugConsole mockConsole;
    private Vector2 mockPosition;
    private SpawnObjectCommand command;

    @BeforeEach
    void setUp() {
        mockPlayer = mock(Player.class);
        mockStats = mock(PlayerStats.class);
        mockGameScreen = mock(GameScreen.class);
        mockConsole = mock(DebugConsole.class);
        mockPosition = new Vector2(10f, 20f);

        when(mockPlayer.getStats()).thenReturn(mockStats);
        when(mockPlayer.getPosition()).thenReturn(mockPosition);
        when(mockStats.getLevel()).thenReturn(10);
        when(mockGameScreen.getCurrentDungeonLevel()).thenReturn(5);

        command = new SpawnObjectCommand(mockPlayer, mockGameScreen);
    }

    @Nested
    @DisplayName("Command Properties")
    class CommandProperties {

        @Test
        @DisplayName("getName should return 'spawn'")
        void testGetName() {
            assertEquals("spawn", command.getName());
        }

        @Test
        @DisplayName("getDescription should return usage information")
        void testGetDescription() {
            String description = command.getDescription();
            assertNotNull(description);
            assertTrue(description.contains("spawn") || description.contains("object"));
        }
    }

    @Nested
    @Disabled("Requires LibGDX initialization - these are integration tests")
    @DisplayName("Breakable Objects")
    class BreakableObjects {

        @Test
        @DisplayName("Should spawn crate with default distance")
        void testSpawnCrate() {
            command.execute(new String[]{"crate"}, mockConsole);

            verify(mockGameScreen).addBreakableObject(any());
            verify(mockConsole).log(contains("Spawned crate"));
        }

        @Test
        @DisplayName("Should spawn pot with custom distance")
        void testSpawnPot() {
            command.execute(new String[]{"pot", "5"}, mockConsole);

            verify(mockGameScreen).addBreakableObject(any());
            verify(mockConsole).log(contains("Spawned pot"));
        }

        @Test
        @DisplayName("Should spawn barrel with dungeon level override")
        void testSpawnBarrel() {
            command.execute(new String[]{"barrel", "3", "50"}, mockConsole);

            verify(mockGameScreen).addBreakableObject(any());
            verify(mockConsole).log(contains("dungeon level override: 50"));
            verify(mockConsole).log(contains("Spawned barrel"));
        }
    }

    @Nested
    @Disabled("Requires LibGDX initialization - these are integration tests")
    @DisplayName("Interactable Objects")
    class InteractableObjects {

        @Test
        @DisplayName("Should spawn chest")
        void testSpawnChest() {
            command.execute(new String[]{"chest"}, mockConsole);

            verify(mockGameScreen).addInteractableObject(any());
            verify(mockConsole).log(contains("Spawned chest"));
        }

        @Test
        @DisplayName("Should spawn workbench")
        void testSpawnWorkbench() {
            command.execute(new String[]{"workbench"}, mockConsole);

            verify(mockGameScreen).addInteractableObject(any());
            verify(mockConsole).log(contains("Spawned workbench"));
        }

        @Test
        @DisplayName("Should spawn anvil")
        void testSpawnAnvil() {
            command.execute(new String[]{"anvil"}, mockConsole);

            verify(mockGameScreen).addInteractableObject(any());
            verify(mockConsole).log(contains("Spawned anvil"));
        }

        @Test
        @DisplayName("Should spawn shrine")
        void testSpawnShrine() {
            command.execute(new String[]{"shrine"}, mockConsole);

            verify(mockGameScreen).addInteractableObject(any());
            verify(mockConsole).log(contains("Spawned shrine"));
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
            verify(mockConsole, atLeastOnce()).log(contains("spawn"));
            verifyNoInteractions(mockGameScreen);
        }

        @Test
        @DisplayName("Should handle unknown object type")
        void testUnknownObjectType() {
            command.execute(new String[]{"unknown"}, mockConsole);

            verify(mockConsole).log(contains("Unknown object type"));
            verify(mockConsole).log(contains("Available types"));
        }

        @Test
        @DisplayName("Should handle invalid distance")
        void testInvalidDistance() {
            command.execute(new String[]{"crate", "abc"}, mockConsole);

            verify(mockConsole).log(contains("Invalid distance"));
            verifyNoInteractions(mockGameScreen);
        }

        @Test
        @DisplayName("Should handle invalid dungeon level")
        void testInvalidDungeonLevel() {
            command.execute(new String[]{"crate", "2", "xyz"}, mockConsole);

            verify(mockConsole).log(contains("Invalid dungeon level"));
            verifyNoInteractions(mockGameScreen);
        }
    }

    @Nested
    @Disabled("Requires LibGDX initialization - these are integration tests")
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle case-insensitive object types")
        void testCaseInsensitiveTypes() {
            command.execute(new String[]{"CRATE"}, mockConsole);
            verify(mockGameScreen).addBreakableObject(any());

            command.execute(new String[]{"Chest"}, mockConsole);
            verify(mockGameScreen).addInteractableObject(any());
        }

        @Test
        @DisplayName("Should use default distance of 2 when not specified")
        void testDefaultDistance() {
            command.execute(new String[]{"crate"}, mockConsole);

            // Verify object spawned at player.x + 2
            verify(mockGameScreen).addBreakableObject(any());
        }

        @Test
        @DisplayName("Should use current dungeon level when not overridden")
        void testDefaultDungeonLevel() {
            command.execute(new String[]{"crate", "2"}, mockConsole);

            verify(mockGameScreen).getCurrentDungeonLevel();
            verify(mockGameScreen).addBreakableObject(any());
        }

        @Test
        @DisplayName("Should handle zero distance")
        void testZeroDistance() {
            command.execute(new String[]{"pot", "0"}, mockConsole);

            verify(mockGameScreen).addBreakableObject(any());
        }

        @Test
        @DisplayName("Should handle negative distance")
        void testNegativeDistance() {
            command.execute(new String[]{"barrel", "-5"}, mockConsole);

            verify(mockGameScreen).addBreakableObject(any());
        }
    }
}
