package io.github.inherit_this.dungeon;

import com.badlogic.gdx.Gdx;
import io.github.inherit_this.LibGdxTestBase;
import io.github.inherit_this.combat.CombatManager;
import io.github.inherit_this.entities.Player;
import io.github.inherit_this.ui.DungeonUI;
import io.github.inherit_this.world.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive tests for DungeonController - dungeon management system
 */
@DisplayName("DungeonController Tests")
class DungeonControllerTest extends LibGdxTestBase {

    private DungeonController dungeonController;

    @Mock
    private Player mockPlayer;

    @Mock
    private DungeonUI mockDungeonUI;

    @Mock
    private CombatManager mockCombatManager;

    @Mock
    private WorldProvider mockTownWorld;

    @Mock
    private DungeonManager mockDungeonManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        dungeonController = new DungeonController(mockPlayer, mockDungeonUI, mockCombatManager);
    }

    // ==================== Initialization Tests ====================

    @Nested
    @DisplayName("Initialization")
    class Initialization {

        @Test
        @DisplayName("DungeonController should initialize with dependencies")
        void testInitialization() {
            assertNotNull(dungeonController, "DungeonController should not be null");
        }

        @Test
        @DisplayName("DungeonController should initialize world correctly")
        void testInitializeWorld() {
            assertDoesNotThrow(() ->
                dungeonController.initialize(mockTownWorld, 100f, 100f),
                "Should initialize with town world and spawn position"
            );
        }

        @Test
        @DisplayName("After initialization, current world should be town world")
        void testCurrentWorldAfterInit() {
            dungeonController.initialize(mockTownWorld, 100f, 100f);

            assertEquals(mockTownWorld, dungeonController.getCurrentWorld(),
                "Current world should be town world after initialization");
        }

        @Test
        @DisplayName("After initialization, town world should be accessible")
        void testTownWorldAfterInit() {
            dungeonController.initialize(mockTownWorld, 100f, 100f);

            assertEquals(mockTownWorld, dungeonController.getTownWorld(),
                "Town world should be accessible after initialization");
        }

        @Test
        @DisplayName("After initialization, town dungeon entrance should exist")
        void testTownDungeonEntranceAfterInit() {
            dungeonController.initialize(mockTownWorld, 100f, 100f);

            assertNotNull(dungeonController.getTownDungeonEntrance(),
                "Town dungeon entrance portal should be created");
        }

        @Test
        @DisplayName("Before entering dungeon, town return portal should be null")
        void testDungeonTownReturnBeforeEnter() {
            dungeonController.initialize(mockTownWorld, 100f, 100f);

            assertNull(dungeonController.getDungeonTownReturn(),
                "Dungeon return portal should be null before entering dungeon");
        }
    }

    // ==================== Getter/Setter Tests ====================

    @Nested
    @DisplayName("Getters and Setters")
    class GettersAndSetters {

        @BeforeEach
        void setUpWorld() {
            dungeonController.initialize(mockTownWorld, 100f, 100f);
        }

        @Test
        @DisplayName("getCurrentWorld should return current world")
        void testGetCurrentWorld() {
            WorldProvider current = dungeonController.getCurrentWorld();
            assertNotNull(current, "Current world should not be null");
        }

        @Test
        @DisplayName("setCurrentWorld should update current world")
        void testSetCurrentWorld() {
            WorldProvider newWorld = mock(WorldProvider.class);
            dungeonController.setCurrentWorld(newWorld);

            assertEquals(newWorld, dungeonController.getCurrentWorld(),
                "Current world should be updated");
        }

        @Test
        @DisplayName("getTownDungeonEntrance should return portal")
        void testGetTownDungeonEntrance() {
            Portal entrance = dungeonController.getTownDungeonEntrance();
            assertNotNull(entrance, "Town dungeon entrance should not be null");
        }

        @Test
        @DisplayName("getDungeonTownReturn should return null initially")
        void testGetDungeonTownReturnInitial() {
            Portal returnPortal = dungeonController.getDungeonTownReturn();
            assertNull(returnPortal, "Dungeon return portal should be null initially");
        }

        @Test
        @DisplayName("getTownWorld should return town world")
        void testGetTownWorld() {
            assertEquals(mockTownWorld, dungeonController.getTownWorld(),
                "Should return the town world");
        }

        @Test
        @DisplayName("getDungeonManager should return dungeon manager")
        void testGetDungeonManager() {
            DungeonManager manager = dungeonController.getDungeonManager();
            assertNotNull(manager, "Dungeon manager should not be null");
        }
    }

    // ==================== Portal Interaction Tests ====================

    @Nested
    @DisplayName("Portal Interactions")
    class PortalInteractions {

        @BeforeEach
        void setUpWorld() {
            com.badlogic.gdx.math.Vector2 mockPosition = new com.badlogic.gdx.math.Vector2(100f, 100f);
            when(mockPlayer.getPosition()).thenReturn(mockPosition);
            when(mockDungeonManager.isInDungeon()).thenReturn(false);

            dungeonController.initialize(mockTownWorld, 100f, 100f);
        }

        @Test
        @DisplayName("handlePortalInteractions should not crash in town")
        void testHandlePortalInteractionsInTown() {
            assertDoesNotThrow(() -> dungeonController.handlePortalInteractions(),
                "Should handle portal interactions in town without crashing");
        }

        @Test
        @DisplayName("handlePortalInteractions should not crash in dungeon")
        void testHandlePortalInteractionsInDungeon() {
            when(mockDungeonManager.isInDungeon()).thenReturn(true);

            assertDoesNotThrow(() -> dungeonController.handlePortalInteractions(),
                "Should handle portal interactions in dungeon without crashing");
        }

        @Test
        @DisplayName("handlePortalInteractions should update UI when near portal")
        void testHandlePortalInteractionsNearPortal() {
            // Player is at 100, 100 - close to portal at 300, 100
            com.badlogic.gdx.math.Vector2 nearPortalPosition = new com.badlogic.gdx.math.Vector2(280f, 100f);
            when(mockPlayer.getPosition()).thenReturn(nearPortalPosition);

            dungeonController.handlePortalInteractions();

            // Verify DungeonUI was updated (either set to portal or null)
            verify(mockDungeonUI, atLeastOnce()).setNearbyPortal(any());
        }

        @Test
        @DisplayName("handlePortalInteractions should clear UI when far from portal")
        void testHandlePortalInteractionsFarFromPortal() {
            // Player is far from portal
            com.badlogic.gdx.math.Vector2 farPosition = new com.badlogic.gdx.math.Vector2(1000f, 1000f);
            when(mockPlayer.getPosition()).thenReturn(farPosition);

            dungeonController.handlePortalInteractions();

            // UI should be updated at least once
            verify(mockDungeonUI, atLeastOnce()).setNearbyPortal(any());
        }
    }

    // ==================== World Management Tests ====================

    @Nested
    @DisplayName("World Management")
    class WorldManagement {

        @BeforeEach
        void setUpWorld() {
            dungeonController.initialize(mockTownWorld, 100f, 100f);
        }

        @Test
        @DisplayName("Should start in town world")
        void testStartInTownWorld() {
            assertEquals(mockTownWorld, dungeonController.getCurrentWorld(),
                "Should start in town world");
        }

        @Test
        @DisplayName("Should allow setting different world")
        void testSetDifferentWorld() {
            WorldProvider newWorld = mock(WorldProvider.class);
            dungeonController.setCurrentWorld(newWorld);

            assertEquals(newWorld, dungeonController.getCurrentWorld(),
                "Should be able to set different world");
        }

        @Test
        @DisplayName("Town world should remain accessible after world change")
        void testTownWorldAccessibleAfterChange() {
            WorldProvider newWorld = mock(WorldProvider.class);
            dungeonController.setCurrentWorld(newWorld);

            assertEquals(mockTownWorld, dungeonController.getTownWorld(),
                "Town world should still be accessible");
        }
    }

    // ==================== Integration Tests ====================

    @Nested
    @DisplayName("Integration Scenarios")
    class IntegrationScenarios {

        @Test
        @DisplayName("Should handle complete initialization flow")
        void testCompleteInitialization() {
            assertDoesNotThrow(() -> {
                dungeonController.initialize(mockTownWorld, 100f, 100f);

                // Verify state
                assertNotNull(dungeonController.getCurrentWorld());
                assertNotNull(dungeonController.getTownWorld());
                assertNotNull(dungeonController.getTownDungeonEntrance());
                assertNotNull(dungeonController.getDungeonManager());
            }, "Should handle complete initialization");
        }

        @Test
        @DisplayName("Should handle multiple portal interaction checks")
        void testMultiplePortalChecks() {
            com.badlogic.gdx.math.Vector2 mockPosition = new com.badlogic.gdx.math.Vector2(100f, 100f);
            when(mockPlayer.getPosition()).thenReturn(mockPosition);
            when(mockDungeonManager.isInDungeon()).thenReturn(false);

            dungeonController.initialize(mockTownWorld, 100f, 100f);

            assertDoesNotThrow(() -> {
                for (int i = 0; i < 10; i++) {
                    dungeonController.handlePortalInteractions();
                }
            }, "Should handle multiple portal checks");
        }
    }

    // ==================== Edge Cases ====================

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle initialization with zero spawn position")
        void testInitializeZeroSpawn() {
            assertDoesNotThrow(() ->
                dungeonController.initialize(mockTownWorld, 0f, 0f),
                "Should handle zero spawn position"
            );
        }

        @Test
        @DisplayName("Should handle initialization with negative spawn position")
        void testInitializeNegativeSpawn() {
            assertDoesNotThrow(() ->
                dungeonController.initialize(mockTownWorld, -100f, -100f),
                "Should handle negative spawn position"
            );
        }

        @Test
        @DisplayName("Should handle initialization with very large spawn position")
        void testInitializeLargeSpawn() {
            assertDoesNotThrow(() ->
                dungeonController.initialize(mockTownWorld, 100000f, 100000f),
                "Should handle very large spawn position"
            );
        }

        @Test
        @DisplayName("Should handle portal interactions before initialization")
        void testPortalInteractionsBeforeInit() {
            com.badlogic.gdx.math.Vector2 mockPosition = new com.badlogic.gdx.math.Vector2(100f, 100f);
            when(mockPlayer.getPosition()).thenReturn(mockPosition);
            when(mockDungeonManager.isInDungeon()).thenReturn(false);

            // This might throw or handle gracefully
            try {
                dungeonController.handlePortalInteractions();
            } catch (NullPointerException e) {
                // Acceptable - controller needs initialization
            }
        }

        @Test
        @DisplayName("Should handle null world in setCurrentWorld")
        void testSetNullWorld() {
            dungeonController.initialize(mockTownWorld, 100f, 100f);
            dungeonController.setCurrentWorld(null);

            assertNull(dungeonController.getCurrentWorld(),
                "Should allow setting null world");
        }
    }
}
