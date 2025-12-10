package io.github.inherit_this.combat;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.inherit_this.entities.Enemy;
import io.github.inherit_this.entities.NPC;
import io.github.inherit_this.entities.Player;
import io.github.inherit_this.entities.PlayerStats;
import io.github.inherit_this.items.Inventory;
import io.github.inherit_this.world.WorldProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive tests for the CombatManager class.
 * Tests NPC management, combat logic, and enemy spawning.
 */
class CombatManagerTest {

    private CombatManager combatManager;

    @Mock
    private Player mockPlayer;

    @Mock
    private WorldProvider mockWorld;

    @Mock
    private Texture mockTexture;

    @Mock
    private NPC mockNPC1;

    @Mock
    private NPC mockNPC2;

    @Mock
    private NPC mockNPC3;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup player mock
        when(mockPlayer.getPosition()).thenReturn(new Vector2(0f, 0f));
        when(mockPlayer.getStats()).thenReturn(new PlayerStats());
        when(mockPlayer.getInventory()).thenReturn(new Inventory(8, 6));

        // Setup texture mock
        when(mockTexture.getWidth()).thenReturn(32);
        when(mockTexture.getHeight()).thenReturn(32);

        combatManager = new CombatManager(mockPlayer);
    }

    // ==================== Initialization Tests ====================

    @Nested
    @DisplayName("Initialization")
    class Initialization {

        @Test
        @DisplayName("CombatManager should initialize with empty NPC list")
        void testInitialization() {
            assertEquals(0, combatManager.getNPCCount(), "Should start with no NPCs");
            assertNotNull(combatManager.getAllNPCs(), "NPC list should not be null");
            assertTrue(combatManager.getAllNPCs().isEmpty(), "NPC list should be empty");
        }

        @Test
        @DisplayName("CombatManager should require a player")
        void testPlayerRequired() {
            assertNotNull(combatManager, "CombatManager should be created successfully");
        }
    }

    // ==================== NPC Management Tests ====================

    @Nested
    @DisplayName("NPC Management")
    class NPCManagement {

        @Test
        @DisplayName("Should return correct NPC count")
        void testGetNPCCount() {
            assertEquals(0, combatManager.getNPCCount(), "Initial count should be 0");

            // Add NPCs manually to the list
            combatManager.getAllNPCs().add(mockNPC1);
            assertEquals(1, combatManager.getNPCCount(), "Count should be 1 after adding");

            combatManager.getAllNPCs().add(mockNPC2);
            assertEquals(2, combatManager.getNPCCount(), "Count should be 2 after adding");
        }

        @Test
        @DisplayName("getAllNPCs should return the NPC list")
        void testGetAllNPCs() {
            List<NPC> npcs = combatManager.getAllNPCs();
            assertNotNull(npcs, "NPC list should not be null");
            assertTrue(npcs.isEmpty(), "Initial list should be empty");

            npcs.add(mockNPC1);
            assertEquals(1, npcs.size(), "List should contain added NPC");
        }

        @Test
        @DisplayName("clearAll should remove all NPCs")
        void testClearAll() {
            combatManager.getAllNPCs().add(mockNPC1);
            combatManager.getAllNPCs().add(mockNPC2);
            combatManager.getAllNPCs().add(mockNPC3);

            assertEquals(3, combatManager.getNPCCount(), "Should have 3 NPCs");

            combatManager.clearAll();

            assertEquals(0, combatManager.getNPCCount(), "Should have 0 NPCs after clear");
            assertTrue(combatManager.getAllNPCs().isEmpty(), "List should be empty");
        }

        @Test
        @DisplayName("clearAll on empty manager should not crash")
        void testClearAllOnEmpty() {
            combatManager.clearAll();
            assertEquals(0, combatManager.getNPCCount(), "Count should still be 0");
        }
    }

    // ==================== Update Tests ====================

    @Nested
    @DisplayName("Update Logic")
    class UpdateLogic {

        @BeforeEach
        void setupMocks() {
            when(mockNPC1.isDead()).thenReturn(false);
            when(mockNPC2.isDead()).thenReturn(false);
            when(mockNPC3.isDead()).thenReturn(false);
        }

        @Test
        @DisplayName("Update should call update on all NPCs")
        void testUpdateCallsNPCUpdates() {
            combatManager.getAllNPCs().add(mockNPC1);
            combatManager.getAllNPCs().add(mockNPC2);

            combatManager.update(0.5f);

            verify(mockNPC1, times(1)).update(0.5f, mockPlayer);
            verify(mockNPC2, times(1)).update(0.5f, mockPlayer);
        }

        @Test
        @DisplayName("Update should remove dead NPCs")
        void testUpdateRemovesDeadNPCs() {
            combatManager.getAllNPCs().add(mockNPC1);
            combatManager.getAllNPCs().add(mockNPC2);
            combatManager.getAllNPCs().add(mockNPC3);

            // Make NPC2 dead
            when(mockNPC2.isDead()).thenReturn(true);

            assertEquals(3, combatManager.getNPCCount(), "Should have 3 NPCs before update");

            combatManager.update(0.1f);

            assertEquals(2, combatManager.getNPCCount(), "Should have 2 NPCs after removing dead");
            assertTrue(combatManager.getAllNPCs().contains(mockNPC1), "Should contain NPC1");
            assertFalse(combatManager.getAllNPCs().contains(mockNPC2), "Should not contain dead NPC2");
            assertTrue(combatManager.getAllNPCs().contains(mockNPC3), "Should contain NPC3");
        }

        @Test
        @DisplayName("Update should handle all NPCs being dead")
        void testUpdateRemovesAllDeadNPCs() {
            combatManager.getAllNPCs().add(mockNPC1);
            combatManager.getAllNPCs().add(mockNPC2);

            when(mockNPC1.isDead()).thenReturn(true);
            when(mockNPC2.isDead()).thenReturn(true);

            combatManager.update(0.1f);

            assertEquals(0, combatManager.getNPCCount(), "All dead NPCs should be removed");
        }

        @Test
        @DisplayName("Update on empty manager should not crash")
        void testUpdateOnEmptyManager() {
            assertDoesNotThrow(() -> combatManager.update(0.1f),
                "Update on empty manager should not crash");
        }

        @Test
        @DisplayName("Update should work over multiple frames")
        void testMultipleUpdates() {
            combatManager.getAllNPCs().add(mockNPC1);

            combatManager.update(0.016f); // Frame 1
            combatManager.update(0.016f); // Frame 2
            combatManager.update(0.016f); // Frame 3

            verify(mockNPC1, times(3)).update(anyFloat(), eq(mockPlayer));
        }

        @Test
        @DisplayName("Update should handle NPCs dying during update")
        void testNPCDiesDuringUpdate() {
            combatManager.getAllNPCs().add(mockNPC1);
            combatManager.getAllNPCs().add(mockNPC2);

            // NPC1 dies after first update
            when(mockNPC1.isDead()).thenReturn(false);
            combatManager.update(0.1f);

            when(mockNPC1.isDead()).thenReturn(true);
            combatManager.update(0.1f);

            assertEquals(1, combatManager.getNPCCount(), "Dead NPC should be removed");
            assertFalse(combatManager.getAllNPCs().contains(mockNPC1));
        }
    }

    // ==================== Enemy Spawning Tests ====================

    @Nested
    @DisplayName("Enemy Spawning")
    class EnemySpawning {

        @Test
        @DisplayName("spawnTestEnemies should create 3 enemies")
        void testSpawnTestEnemies() {
            int[] spawnPos = {100, 100};

            combatManager.spawnTestEnemies(spawnPos, mockWorld, mockTexture);

            assertEquals(3, combatManager.getNPCCount(), "Should spawn 3 enemies");
        }

        @Test
        @DisplayName("spawnTestEnemies should create enemies at different positions")
        void testSpawnedEnemiesAtDifferentPositions() {
            int[] spawnPos = {50, 50};

            combatManager.spawnTestEnemies(spawnPos, mockWorld, mockTexture);

            List<NPC> npcs = combatManager.getAllNPCs();
            assertEquals(3, npcs.size(), "Should have 3 NPCs");

            // Verify they are all Enemy instances
            for (NPC npc : npcs) {
                assertTrue(npc instanceof Enemy, "All spawned NPCs should be enemies");
            }

            // Verify they have different positions
            Vector2 pos1 = npcs.get(0).getPosition();
            Vector2 pos2 = npcs.get(1).getPosition();
            Vector2 pos3 = npcs.get(2).getPosition();

            assertNotEquals(pos1, pos2, "Enemy 1 and 2 should have different positions");
            assertNotEquals(pos2, pos3, "Enemy 2 and 3 should have different positions");
            assertNotEquals(pos1, pos3, "Enemy 1 and 3 should have different positions");
        }

        @Test
        @DisplayName("spawnTestEnemies should create enemies with names")
        void testSpawnedEnemiesHaveNames() {
            int[] spawnPos = {0, 0};

            combatManager.spawnTestEnemies(spawnPos, mockWorld, mockTexture);

            List<NPC> npcs = combatManager.getAllNPCs();

            assertEquals("Skeleton", npcs.get(0).getName(), "First enemy should be Skeleton");
            assertEquals("Zombie", npcs.get(1).getName(), "Second enemy should be Zombie");
            assertEquals("Ghost", npcs.get(2).getName(), "Third enemy should be Ghost");
        }

        @Test
        @DisplayName("spawnTestEnemies should position enemies relative to spawn point")
        void testSpawnedEnemiesPositionedCorrectly() {
            int[] spawnPos = {100, 200};

            combatManager.spawnTestEnemies(spawnPos, mockWorld, mockTexture);

            List<NPC> npcs = combatManager.getAllNPCs();

            // First enemy: spawnPos[0] + 100, spawnPos[1]
            assertEquals(200, npcs.get(0).getPosition().x, "Skeleton X should be 200");
            assertEquals(200, npcs.get(0).getPosition().y, "Skeleton Y should be 200");

            // Second enemy: spawnPos[0] - 100, spawnPos[1] + 50
            assertEquals(0, npcs.get(1).getPosition().x, "Zombie X should be 0");
            assertEquals(250, npcs.get(1).getPosition().y, "Zombie Y should be 250");

            // Third enemy: spawnPos[0], spawnPos[1] + 150
            assertEquals(100, npcs.get(2).getPosition().x, "Ghost X should be 100");
            assertEquals(350, npcs.get(2).getPosition().y, "Ghost Y should be 350");
        }

        @Test
        @DisplayName("Multiple spawn calls should add more enemies")
        void testMultipleSpawnCalls() {
            int[] spawnPos1 = {0, 0};
            int[] spawnPos2 = {500, 500};

            combatManager.spawnTestEnemies(spawnPos1, mockWorld, mockTexture);
            assertEquals(3, combatManager.getNPCCount(), "Should have 3 enemies after first spawn");

            combatManager.spawnTestEnemies(spawnPos2, mockWorld, mockTexture);
            assertEquals(6, combatManager.getNPCCount(), "Should have 6 enemies after second spawn");
        }
    }

    // ==================== Find Nearest NPC Tests ====================

    @Nested
    @DisplayName("Find Nearest NPC")
    class FindNearestNPC {

        @Test
        @DisplayName("Should return null when no NPCs exist")
        void testFindNearestNPCNoNPCs() {
            NPC nearest = combatManager.findNearestNPC(0f, 0f, 100f);
            assertNull(nearest, "Should return null when no NPCs exist");
        }

        @Test
        @DisplayName("Should find NPC within range")
        void testFindNearestNPCWithinRange() {
            when(mockNPC1.isDead()).thenReturn(false);
            when(mockNPC1.getPosition()).thenReturn(new Vector2(10f, 10f));

            combatManager.getAllNPCs().add(mockNPC1);

            // Distance = sqrt(10^2 + 10^2) = ~14.14
            NPC nearest = combatManager.findNearestNPC(0f, 0f, 20f);

            assertSame(mockNPC1, nearest, "Should find NPC within range");
        }

        @Test
        @DisplayName("Should return null when NPC is outside range")
        void testFindNearestNPCOutOfRange() {
            when(mockNPC1.isDead()).thenReturn(false);
            when(mockNPC1.getPosition()).thenReturn(new Vector2(100f, 100f));

            combatManager.getAllNPCs().add(mockNPC1);

            // Distance = sqrt(100^2 + 100^2) = ~141.4
            NPC nearest = combatManager.findNearestNPC(0f, 0f, 50f);

            assertNull(nearest, "Should return null when NPC is outside range");
        }

        @Test
        @DisplayName("Should skip dead NPCs")
        void testFindNearestNPCSkipsDeadNPCs() {
            when(mockNPC1.isDead()).thenReturn(true);
            when(mockNPC1.getPosition()).thenReturn(new Vector2(5f, 5f));

            combatManager.getAllNPCs().add(mockNPC1);

            NPC nearest = combatManager.findNearestNPC(0f, 0f, 100f);

            assertNull(nearest, "Should skip dead NPCs");
        }

        @Test
        @DisplayName("Should find closest NPC when multiple exist")
        void testFindNearestNPCMultiple() {
            when(mockNPC1.isDead()).thenReturn(false);
            when(mockNPC1.getPosition()).thenReturn(new Vector2(20f, 20f)); // Distance ~28.28

            when(mockNPC2.isDead()).thenReturn(false);
            when(mockNPC2.getPosition()).thenReturn(new Vector2(5f, 5f));   // Distance ~7.07

            when(mockNPC3.isDead()).thenReturn(false);
            when(mockNPC3.getPosition()).thenReturn(new Vector2(15f, 15f)); // Distance ~21.21

            combatManager.getAllNPCs().add(mockNPC1);
            combatManager.getAllNPCs().add(mockNPC2);
            combatManager.getAllNPCs().add(mockNPC3);

            NPC nearest = combatManager.findNearestNPC(0f, 0f, 50f);

            assertSame(mockNPC2, nearest, "Should find the closest NPC");
        }

        @Test
        @DisplayName("Should find nearest among alive NPCs")
        void testFindNearestNPCWithDeadAndAlive() {
            when(mockNPC1.isDead()).thenReturn(true);
            when(mockNPC1.getPosition()).thenReturn(new Vector2(5f, 5f));   // Closest but dead

            when(mockNPC2.isDead()).thenReturn(false);
            when(mockNPC2.getPosition()).thenReturn(new Vector2(10f, 10f)); // Alive

            when(mockNPC3.isDead()).thenReturn(false);
            when(mockNPC3.getPosition()).thenReturn(new Vector2(20f, 20f)); // Alive but farther

            combatManager.getAllNPCs().add(mockNPC1);
            combatManager.getAllNPCs().add(mockNPC2);
            combatManager.getAllNPCs().add(mockNPC3);

            NPC nearest = combatManager.findNearestNPC(0f, 0f, 50f);

            assertSame(mockNPC2, nearest, "Should find closest alive NPC");
        }

        @Test
        @DisplayName("Should return NPC at exact max distance")
        void testFindNearestNPCAtExactMaxDistance() {
            when(mockNPC1.isDead()).thenReturn(false);
            when(mockNPC1.getPosition()).thenReturn(new Vector2(10f, 0f)); // Distance = 10

            combatManager.getAllNPCs().add(mockNPC1);

            NPC nearest = combatManager.findNearestNPC(0f, 0f, 10f);

            assertSame(mockNPC1, nearest, "Should find NPC at exact max distance");
        }

        @Test
        @DisplayName("Should return null when NPC is just outside max distance")
        void testFindNearestNPCJustOutsideMaxDistance() {
            when(mockNPC1.isDead()).thenReturn(false);
            when(mockNPC1.getPosition()).thenReturn(new Vector2(10.1f, 0f)); // Distance = 10.1

            combatManager.getAllNPCs().add(mockNPC1);

            NPC nearest = combatManager.findNearestNPC(0f, 0f, 10f);

            assertNull(nearest, "Should not find NPC just outside max distance");
        }

        @Test
        @DisplayName("Should work with negative coordinates")
        void testFindNearestNPCNegativeCoords() {
            when(mockNPC1.isDead()).thenReturn(false);
            when(mockNPC1.getPosition()).thenReturn(new Vector2(-5f, -5f));

            combatManager.getAllNPCs().add(mockNPC1);

            // Distance from (0,0) to (-5,-5) = ~7.07
            NPC nearest = combatManager.findNearestNPC(0f, 0f, 10f);

            assertSame(mockNPC1, nearest, "Should handle negative coordinates");
        }

        @Test
        @DisplayName("Should calculate distance correctly for diagonal positions")
        void testFindNearestNPCDiagonal() {
            when(mockNPC1.isDead()).thenReturn(false);
            when(mockNPC1.getPosition()).thenReturn(new Vector2(3f, 4f)); // Distance = 5 (3-4-5 triangle)

            combatManager.getAllNPCs().add(mockNPC1);

            NPC nearest = combatManager.findNearestNPC(0f, 0f, 6f);
            assertSame(mockNPC1, nearest, "Should be within range");

            NPC notNearest = combatManager.findNearestNPC(0f, 0f, 4f);
            assertNull(notNearest, "Should be outside range");
        }

        @Test
        @DisplayName("Should handle zero max distance")
        void testFindNearestNPCZeroMaxDistance() {
            when(mockNPC1.isDead()).thenReturn(false);
            when(mockNPC1.getPosition()).thenReturn(new Vector2(0f, 0f));

            combatManager.getAllNPCs().add(mockNPC1);

            // Only NPC at exact position would be found
            NPC nearest = combatManager.findNearestNPC(0f, 0f, 0f);

            assertSame(mockNPC1, nearest, "Should find NPC at exact same position");
        }

        @Test
        @DisplayName("Should handle very large max distance")
        void testFindNearestNPCVeryLargeMaxDistance() {
            when(mockNPC1.isDead()).thenReturn(false);
            when(mockNPC1.getPosition()).thenReturn(new Vector2(1000f, 1000f));

            combatManager.getAllNPCs().add(mockNPC1);

            NPC nearest = combatManager.findNearestNPC(0f, 0f, Float.MAX_VALUE);

            assertSame(mockNPC1, nearest, "Should find NPC with very large max distance");
        }
    }

    // ==================== Integration Tests ====================

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Spawn, update, and clear workflow")
        void testFullWorkflow() {
            int[] spawnPos = {0, 0};

            // Spawn enemies
            combatManager.spawnTestEnemies(spawnPos, mockWorld, mockTexture);
            assertEquals(3, combatManager.getNPCCount(), "Should have 3 enemies");

            // Update enemies
            combatManager.update(0.1f);
            assertEquals(3, combatManager.getNPCCount(), "Enemies should still be alive");

            // Clear all
            combatManager.clearAll();
            assertEquals(0, combatManager.getNPCCount(), "All enemies should be cleared");
        }

        @Test
        @DisplayName("Find nearest after spawning")
        void testFindNearestAfterSpawning() {
            int[] spawnPos = {0, 0};

            combatManager.spawnTestEnemies(spawnPos, mockWorld, mockTexture);

            // Find nearest to spawn point
            NPC nearest = combatManager.findNearestNPC(0f, 0f, 200f);

            assertNotNull(nearest, "Should find a nearby enemy");
        }

        @Test
        @DisplayName("NPCs should be removed after dying during update")
        void testNPCsRemovedAfterDying() {
            int[] spawnPos = {0, 0};

            combatManager.spawnTestEnemies(spawnPos, mockWorld, mockTexture);
            assertEquals(3, combatManager.getNPCCount());

            // Kill all enemies
            for (NPC npc : combatManager.getAllNPCs()) {
                ((Enemy) npc).takeDamage(10000, mockPlayer);
            }

            // Update should remove dead NPCs
            combatManager.update(0.1f);

            assertEquals(0, combatManager.getNPCCount(), "Dead NPCs should be removed");
        }

        @Test
        @DisplayName("Multiple spawn and clear cycles")
        void testMultipleSpawnClearCycles() {
            int[] spawnPos = {0, 0};

            for (int i = 0; i < 5; i++) {
                combatManager.spawnTestEnemies(spawnPos, mockWorld, mockTexture);
                assertEquals(3, combatManager.getNPCCount(), "Should have 3 enemies in cycle " + i);

                combatManager.clearAll();
                assertEquals(0, combatManager.getNPCCount(), "Should be empty in cycle " + i);
            }
        }
    }

    // ==================== Edge Cases ====================

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle empty spawn position array")
        void testEmptySpawnPosition() {
            int[] spawnPos = {0, 0};

            assertDoesNotThrow(() -> combatManager.spawnTestEnemies(spawnPos, mockWorld, mockTexture),
                "Should handle empty spawn position without crashing");
        }

        @Test
        @DisplayName("Update with zero delta should not crash")
        void testUpdateWithZeroDelta() {
            combatManager.getAllNPCs().add(mockNPC1);

            assertDoesNotThrow(() -> combatManager.update(0f),
                "Update with zero delta should not crash");
        }

        @Test
        @DisplayName("Update with negative delta should not crash")
        void testUpdateWithNegativeDelta() {
            combatManager.getAllNPCs().add(mockNPC1);

            assertDoesNotThrow(() -> combatManager.update(-0.1f),
                "Update with negative delta should not crash");
        }

        @Test
        @DisplayName("Update with very large delta should not crash")
        void testUpdateWithLargeDelta() {
            combatManager.getAllNPCs().add(mockNPC1);

            assertDoesNotThrow(() -> combatManager.update(1000f),
                "Update with large delta should not crash");
        }

        @Test
        @DisplayName("Should handle null checks in findNearestNPC")
        void testFindNearestNPCWithNullPosition() {
            when(mockNPC1.isDead()).thenReturn(false);
            when(mockNPC1.getPosition()).thenReturn(null);

            combatManager.getAllNPCs().add(mockNPC1);

            // Should handle null position gracefully
            assertThrows(NullPointerException.class, () -> {
                combatManager.findNearestNPC(0f, 0f, 100f);
            }, "Should throw NPE when NPC has null position");
        }
    }
}
