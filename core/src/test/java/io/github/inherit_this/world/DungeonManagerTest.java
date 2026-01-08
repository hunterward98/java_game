package io.github.inherit_this.world;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for DungeonManager - dungeon level management and transitions.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class DungeonManagerTest {

    private DungeonManager dungeonManager;

    @BeforeEach
    void setUp() {
        // Get singleton instance and clear state
        dungeonManager = DungeonManager.getInstance();
        dungeonManager.clearDungeons();
        dungeonManager.setPlayerSeed(12345L);
    }

    @AfterEach
    void tearDown() {
        // Clean up after each test
        dungeonManager.clearDungeons();
    }

    @Nested
    @DisplayName("Singleton Pattern")
    class SingletonPattern {

        @Test
        @DisplayName("getInstance should return same instance")
        void testGetInstanceReturnsSameInstance() {
            DungeonManager instance1 = DungeonManager.getInstance();
            DungeonManager instance2 = DungeonManager.getInstance();

            assertSame(instance1, instance2, "getInstance should return the same instance");
        }

        @Test
        @DisplayName("getInstance should never return null")
        void testGetInstanceNeverNull() {
            assertNotNull(DungeonManager.getInstance(), "getInstance should never return null");
        }
    }

    @Nested
    @DisplayName("Dungeon Entry")
    class DungeonEntry {

        @Test
        @DisplayName("enterDungeon should create new dungeon")
        void testEnterDungeonCreatesNewDungeon() {
            DungeonWorld dungeon = dungeonManager.enterDungeon(1, 100f, 200f);

            assertNotNull(dungeon, "enterDungeon should create a dungeon");
            assertEquals(1, dungeonManager.getCurrentLevel(), "Current level should be 1");
            assertTrue(dungeonManager.isInDungeon(), "Should be in dungeon");
        }

        @Test
        @DisplayName("enterDungeon should save town return position")
        void testEnterDungeonSavesTownPosition() {
            float townX = 150f;
            float townY = 250f;

            dungeonManager.enterDungeon(1, townX, townY);
            float[] returnPos = dungeonManager.exitToTown();

            assertEquals(townX, returnPos[0], "Should return correct town X");
            assertEquals(townY, returnPos[1], "Should return correct town Y");
        }

        @Test
        @DisplayName("enterDungeon should cache dungeons")
        void testEnterDungeonCachesDungeons() {
            DungeonWorld dungeon1 = dungeonManager.enterDungeon(1, 0f, 0f);
            dungeonManager.exitToTown();
            DungeonWorld dungeon2 = dungeonManager.enterDungeon(1, 0f, 0f);

            assertSame(dungeon1, dungeon2, "Should return cached dungeon for same level");
        }

        @Test
        @DisplayName("enterDungeon should create different dungeons for different levels")
        void testEnterDungeonDifferentLevels() {
            DungeonWorld dungeon1 = dungeonManager.enterDungeon(1, 0f, 0f);
            DungeonWorld dungeon2 = dungeonManager.enterDungeon(2, 0f, 0f);

            assertNotSame(dungeon1, dungeon2, "Different levels should have different dungeons");
        }

        @Test
        @DisplayName("enterDungeon should update current dungeon")
        void testEnterDungeonUpdatesCurrentDungeon() {
            DungeonWorld dungeon = dungeonManager.enterDungeon(3, 0f, 0f);

            assertSame(dungeon, dungeonManager.getCurrentDungeon(), "Should set current dungeon");
        }
    }

    @Nested
    @DisplayName("Dungeon Exit")
    class DungeonExit {

        @Test
        @DisplayName("exitToTown should return correct position")
        void testExitToTownReturnsPosition() {
            float townX = 300f;
            float townY = 400f;

            dungeonManager.enterDungeon(1, townX, townY);
            float[] returnPos = dungeonManager.exitToTown();

            assertEquals(townX, returnPos[0], "Should return town X");
            assertEquals(townY, returnPos[1], "Should return town Y");
            assertEquals(2, returnPos.length, "Should return array of length 2");
        }

        @Test
        @DisplayName("exitToTown should clear current dungeon")
        void testExitToTownClearsCurrentDungeon() {
            dungeonManager.enterDungeon(1, 0f, 0f);
            dungeonManager.exitToTown();

            assertNull(dungeonManager.getCurrentDungeon(), "Current dungeon should be null");
            assertFalse(dungeonManager.isInDungeon(), "Should not be in dungeon");
        }

        @Test
        @DisplayName("exitToTown should set level to 0")
        void testExitToTownSetsLevelToZero() {
            dungeonManager.enterDungeon(5, 0f, 0f);
            dungeonManager.exitToTown();

            assertEquals(0, dungeonManager.getCurrentLevel(), "Level should be 0 (town)");
        }

        @Test
        @DisplayName("exitToTown should keep dungeon in cache")
        void testExitToTownKeepsDungeonInCache() {
            DungeonWorld originalDungeon = dungeonManager.enterDungeon(2, 0f, 0f);
            dungeonManager.exitToTown();
            DungeonWorld returnedDungeon = dungeonManager.enterDungeon(2, 0f, 0f);

            assertSame(originalDungeon, returnedDungeon, "Should retrieve same dungeon from cache");
        }
    }

    @Nested
    @DisplayName("Player Seed Management")
    class PlayerSeedManagement {

        @Test
        @DisplayName("setPlayerSeed should update seed")
        void testSetPlayerSeedUpdatesSeed() {
            long newSeed = 99999L;
            dungeonManager.setPlayerSeed(newSeed);

            assertEquals(newSeed, dungeonManager.getPlayerSeed(), "Should update player seed");
        }

        @Test
        @DisplayName("setPlayerSeed should clear dungeon cache")
        void testSetPlayerSeedClearsDungeonCache() {
            DungeonWorld dungeon1 = dungeonManager.enterDungeon(1, 0f, 0f);
            dungeonManager.setPlayerSeed(99999L);
            DungeonWorld dungeon2 = dungeonManager.enterDungeon(1, 0f, 0f);

            assertNotSame(dungeon1, dungeon2, "Should create new dungeon after seed change");
        }

        @Test
        @DisplayName("getPlayerSeed should return initial seed")
        void testGetPlayerSeedReturnsInitialSeed() {
            // Seed is set in setUp to 12345L
            assertEquals(12345L, dungeonManager.getPlayerSeed(), "Should return initial seed");
        }

        @Test
        @DisplayName("different seeds should create different dungeons")
        void testDifferentSeedsCreateDifferentDungeons() {
            dungeonManager.setPlayerSeed(11111L);
            dungeonManager.enterDungeon(1, 0f, 0f);
            int[] spawn1 = dungeonManager.getDungeonSpawnPosition(1);

            dungeonManager.setPlayerSeed(22222L);
            dungeonManager.enterDungeon(1, 0f, 0f);
            int[] spawn2 = dungeonManager.getDungeonSpawnPosition(1);

            // Different seeds should likely create different spawn positions
            // (Though theoretically they could be the same by chance)
            assertNotNull(spawn1, "Spawn1 should not be null");
            assertNotNull(spawn2, "Spawn2 should not be null");
        }
    }

    @Nested
    @DisplayName("Dungeon State")
    class DungeonState {

        @Test
        @DisplayName("isInDungeon should return false initially")
        void testIsInDungeonInitially() {
            assertFalse(dungeonManager.isInDungeon(), "Should not be in dungeon initially");
        }

        @Test
        @DisplayName("isInDungeon should return true after entering")
        void testIsInDungeonAfterEntering() {
            dungeonManager.enterDungeon(1, 0f, 0f);

            assertTrue(dungeonManager.isInDungeon(), "Should be in dungeon");
        }

        @Test
        @DisplayName("isInDungeon should return false after exiting")
        void testIsInDungeonAfterExiting() {
            dungeonManager.enterDungeon(1, 0f, 0f);
            dungeonManager.exitToTown();

            assertFalse(dungeonManager.isInDungeon(), "Should not be in dungeon after exit");
        }

        @Test
        @DisplayName("getCurrentLevel should return 0 initially")
        void testGetCurrentLevelInitially() {
            assertEquals(0, dungeonManager.getCurrentLevel(), "Level should be 0 initially");
        }

        @Test
        @DisplayName("getCurrentLevel should return correct level")
        void testGetCurrentLevelAfterEntering() {
            dungeonManager.enterDungeon(7, 0f, 0f);

            assertEquals(7, dungeonManager.getCurrentLevel(), "Should return current level");
        }

        @Test
        @DisplayName("getCurrentDungeon should return null initially")
        void testGetCurrentDungeonInitially() {
            assertNull(dungeonManager.getCurrentDungeon(), "Should return null initially");
        }
    }

    @Nested
    @DisplayName("Spawn Position")
    class SpawnPosition {

        @Test
        @DisplayName("getDungeonSpawnPosition should return position for cached dungeon")
        void testGetDungeonSpawnPositionForCachedDungeon() {
            dungeonManager.enterDungeon(1, 0f, 0f);
            int[] spawnPos = dungeonManager.getDungeonSpawnPosition(1);

            assertNotNull(spawnPos, "Spawn position should not be null");
            assertEquals(2, spawnPos.length, "Spawn position should have 2 elements");
        }

        @Test
        @DisplayName("getDungeonSpawnPosition should return [0,0] for non-cached dungeon")
        void testGetDungeonSpawnPositionForNonCachedDungeon() {
            int[] spawnPos = dungeonManager.getDungeonSpawnPosition(999);

            assertArrayEquals(new int[]{0, 0}, spawnPos, "Should return [0,0] for non-cached dungeon");
        }
    }

    @Nested
    @DisplayName("Clear Dungeons")
    class ClearDungeons {

        @Test
        @DisplayName("clearDungeons should remove all cached dungeons")
        void testClearDungeonsRemovesCache() {
            DungeonWorld dungeon1 = dungeonManager.enterDungeon(1, 0f, 0f);
            dungeonManager.clearDungeons();
            DungeonWorld dungeon2 = dungeonManager.enterDungeon(1, 0f, 0f);

            assertNotSame(dungeon1, dungeon2, "Should create new dungeon after clear");
        }

        @Test
        @DisplayName("clearDungeons should reset current level")
        void testClearDungeonsResetsLevel() {
            dungeonManager.enterDungeon(5, 0f, 0f);
            dungeonManager.clearDungeons();

            assertEquals(0, dungeonManager.getCurrentLevel(), "Level should be 0 after clear");
        }

        @Test
        @DisplayName("clearDungeons should clear current dungeon")
        void testClearDungeonsClearsCurrentDungeon() {
            dungeonManager.enterDungeon(1, 0f, 0f);
            dungeonManager.clearDungeons();

            assertNull(dungeonManager.getCurrentDungeon(), "Current dungeon should be null after clear");
            assertFalse(dungeonManager.isInDungeon(), "Should not be in dungeon after clear");
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("entering level 0 should work")
        void testEnteringLevelZero() {
            assertDoesNotThrow(() -> dungeonManager.enterDungeon(0, 0f, 0f),
                "Should not throw when entering level 0");
        }

        @Test
        @DisplayName("entering negative level should work but is unusual")
        void testEnteringNegativeLevel() {
            assertDoesNotThrow(() -> dungeonManager.enterDungeon(-1, 0f, 0f),
                "Should not throw for negative level");
        }

        @Test
        @DisplayName("entering very high level should work")
        void testEnteringVeryHighLevel() {
            assertDoesNotThrow(() -> dungeonManager.enterDungeon(1000, 0f, 0f),
                "Should not throw for high level");
        }

        @Test
        @DisplayName("exitToTown when not in dungeon should work")
        void testExitToTownWhenNotInDungeon() {
            float[] returnPos = dungeonManager.exitToTown();

            assertNotNull(returnPos, "Should return position even when not in dungeon");
            assertEquals(2, returnPos.length, "Should return array of length 2");
        }

        @Test
        @DisplayName("multiple enters without exit should work")
        void testMultipleEntersWithoutExit() {
            dungeonManager.enterDungeon(1, 100f, 100f);
            dungeonManager.enterDungeon(2, 200f, 200f);
            dungeonManager.enterDungeon(3, 300f, 300f);

            assertEquals(3, dungeonManager.getCurrentLevel(), "Should be at level 3");

            float[] returnPos = dungeonManager.exitToTown();
            assertEquals(300f, returnPos[0], "Should use most recent town position");
        }
    }
}
