package io.github.inherit_this.loot;

import io.github.inherit_this.LibGdxTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Disabled;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static io.github.inherit_this.loot.LootTableGenerator.ObjectType.*;

/**
 * Comprehensive tests for the Loot System: LootTableGenerator and ScaledLootTable classes.
 * Tests loot scaling with player level, dungeon depth, different object types,
 * item drops, gold ranges, XP rewards, and edge cases.
 *
 * NOTE: These tests are currently disabled because they require full LibGDX assets
 * (textures, etc.) to be loaded, which aren't available in the test environment.
 * ItemRegistry tries to load placeholder.png during initialization.
 */
@Disabled("Requires full LibGDX assets (placeholder.png) - enable for integration testing")
class LootTableGeneratorTest extends LibGdxTestBase {

    private LootTableGenerator generator;

    @BeforeEach
    void setUp() throws Exception {
        // Reset singleton instance for clean test state
        resetSingletonInstance(LootTableGenerator.class);

        generator = LootTableGenerator.getInstance();
    }

    /**
     * Reset singleton instance using reflection for testing.
     */
    private void resetSingletonInstance(Class<?> clazz) throws Exception {
        Field instance = clazz.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    // ==================== Basic Loot Table Generation Tests ====================

    @Nested
    @DisplayName("Basic Loot Table Generation")
    class BasicLootTableGeneration {

        @Test
        @DisplayName("Should generate loot table for POT object type")
        void testGenerateLootTableForPot() {
            ScaledLootTable lootTable = generator.generateLootTable(1, 0, POT);

            assertNotNull(lootTable, "Loot table should not be null");
            assertTrue(lootTable.getGoldMin() > 0, "Pots should have some gold");
            assertTrue(lootTable.getGoldMax() >= lootTable.getGoldMin(), "Max gold should be >= min gold");
            assertTrue(lootTable.getXP() > 0, "Pots should give some XP");
        }

        @Test
        @DisplayName("Should generate loot table for CRATE object type")
        void testGenerateLootTableForCrate() {
            ScaledLootTable lootTable = generator.generateLootTable(1, 0, CRATE);

            assertNotNull(lootTable, "Loot table should not be null");
            assertTrue(lootTable.getGoldMin() > 0, "Crates should have gold");
            assertTrue(lootTable.getGoldMax() >= lootTable.getGoldMin(), "Max gold should be >= min gold");
            assertTrue(lootTable.getXP() > 0, "Crates should give XP");
        }

        @Test
        @DisplayName("Should generate loot table for BARREL object type")
        void testGenerateLootTableForBarrel() {
            ScaledLootTable lootTable = generator.generateLootTable(1, 0, BARREL);

            assertNotNull(lootTable, "Loot table should not be null");
            assertTrue(lootTable.getGoldMin() > 0, "Barrels should have gold");
            assertTrue(lootTable.getGoldMax() >= lootTable.getGoldMin(), "Max gold should be >= min gold");
            assertTrue(lootTable.getXP() > 0, "Barrels should give XP");
        }

        @Test
        @DisplayName("Should generate loot table for CHEST object type")
        void testGenerateLootTableForChest() {
            ScaledLootTable lootTable = generator.generateLootTable(1, 0, CHEST);

            assertNotNull(lootTable, "Loot table should not be null");
            assertTrue(lootTable.getGoldMin() > 0, "Chests should have gold");
            assertTrue(lootTable.getGoldMax() >= lootTable.getGoldMin(), "Max gold should be >= min gold");
            assertTrue(lootTable.getXP() > 0, "Chests should give XP");
        }
    }

    // ==================== Object Type Comparison Tests ====================

    @Nested
    @DisplayName("Object Type Reward Scaling")
    class ObjectTypeRewardScaling {

        @Test
        @DisplayName("POT should give less gold than CRATE")
        void testPotVsCrateGold() {
            ScaledLootTable potTable = generator.generateLootTable(5, 0, POT);
            ScaledLootTable crateTable = generator.generateLootTable(5, 0, CRATE);

            assertTrue(potTable.getGoldMin() < crateTable.getGoldMin(),
                "Pots should give less minimum gold than crates");
            assertTrue(potTable.getGoldMax() < crateTable.getGoldMax(),
                "Pots should give less maximum gold than crates");
        }

        @Test
        @DisplayName("BARREL should give more gold than CRATE")
        void testBarrelVsCrateGold() {
            ScaledLootTable barrelTable = generator.generateLootTable(5, 0, BARREL);
            ScaledLootTable crateTable = generator.generateLootTable(5, 0, CRATE);

            assertTrue(barrelTable.getGoldMin() > crateTable.getGoldMin(),
                "Barrels should give more minimum gold than crates");
            assertTrue(barrelTable.getGoldMax() > crateTable.getGoldMax(),
                "Barrels should give more maximum gold than crates");
        }

        @Test
        @DisplayName("CHEST should give the most gold")
        void testChestGivesMaxGold() {
            ScaledLootTable potTable = generator.generateLootTable(5, 0, POT);
            ScaledLootTable crateTable = generator.generateLootTable(5, 0, CRATE);
            ScaledLootTable barrelTable = generator.generateLootTable(5, 0, BARREL);
            ScaledLootTable chestTable = generator.generateLootTable(5, 0, CHEST);

            assertTrue(chestTable.getGoldMin() > potTable.getGoldMin(),
                "Chests should give more gold than pots");
            assertTrue(chestTable.getGoldMin() > crateTable.getGoldMin(),
                "Chests should give more gold than crates");
            assertTrue(chestTable.getGoldMin() > barrelTable.getGoldMin(),
                "Chests should give more gold than barrels");
        }

        @Test
        @DisplayName("XP rewards should scale with object value")
        void testXPScalesWithObjectValue() {
            ScaledLootTable potTable = generator.generateLootTable(5, 0, POT);
            ScaledLootTable crateTable = generator.generateLootTable(5, 0, CRATE);
            ScaledLootTable barrelTable = generator.generateLootTable(5, 0, BARREL);
            ScaledLootTable chestTable = generator.generateLootTable(5, 0, CHEST);

            assertTrue(potTable.getXP() < crateTable.getXP(),
                "Pots should give less XP than crates");
            assertTrue(crateTable.getXP() < barrelTable.getXP(),
                "Crates should give less XP than barrels");
            assertTrue(barrelTable.getXP() < chestTable.getXP(),
                "Barrels should give less XP than chests");
        }
    }

    // ==================== Player Level Scaling Tests ====================

    @Nested
    @DisplayName("Player Level Scaling")
    class PlayerLevelScaling {

        @Test
        @DisplayName("Gold should increase with player level in overworld")
        void testGoldScalesWithPlayerLevel() {
            ScaledLootTable level1Table = generator.generateLootTable(1, 0, CRATE);
            ScaledLootTable level10Table = generator.generateLootTable(10, 0, CRATE);
            ScaledLootTable level50Table = generator.generateLootTable(50, 0, CRATE);

            assertTrue(level1Table.getGoldMin() < level10Table.getGoldMin(),
                "Level 10 should give more gold than level 1");
            assertTrue(level10Table.getGoldMin() < level50Table.getGoldMin(),
                "Level 50 should give more gold than level 10");
        }

        @Test
        @DisplayName("XP should increase with player level")
        void testXPScalesWithPlayerLevel() {
            ScaledLootTable level1Table = generator.generateLootTable(1, 0, CRATE);
            ScaledLootTable level10Table = generator.generateLootTable(10, 0, CRATE);
            ScaledLootTable level50Table = generator.generateLootTable(50, 0, CRATE);

            assertTrue(level1Table.getXP() < level10Table.getXP(),
                "Level 10 should give more XP than level 1");
            assertTrue(level10Table.getXP() < level50Table.getXP(),
                "Level 50 should give more XP than level 10");
        }

        @Test
        @DisplayName("Gold variance should be maintained across levels")
        void testGoldVarianceMaintainedAcrossLevels() {
            for (int level = 1; level <= 100; level += 10) {
                ScaledLootTable table = generator.generateLootTable(level, 0, POT);

                // Variance for POT is 1.5x, so max should be roughly 1.5 * min
                float actualVariance = (float) table.getGoldMax() / table.getGoldMin();
                assertEquals(1.5f, actualVariance, 0.1f,
                    "Gold variance should be maintained at level " + level);
            }
        }
    }

    // ==================== Dungeon Depth Scaling Tests ====================

    @Nested
    @DisplayName("Dungeon Depth Scaling")
    class DungeonDepthScaling {

        @Test
        @DisplayName("Rewards should increase with dungeon depth")
        void testRewardsScaleWithDungeonDepth() {
            int playerLevel = 10;
            ScaledLootTable depth1Table = generator.generateLootTable(playerLevel, 1, CRATE);
            ScaledLootTable depth10Table = generator.generateLootTable(playerLevel, 10, CRATE);
            ScaledLootTable depth50Table = generator.generateLootTable(playerLevel, 50, CRATE);

            assertTrue(depth1Table.getGoldMin() < depth10Table.getGoldMin(),
                "Depth 10 should give more gold than depth 1");
            assertTrue(depth10Table.getGoldMin() < depth50Table.getGoldMin(),
                "Depth 50 should give more gold than depth 10");
        }

        @Test
        @DisplayName("Dungeon level should have more weight than player level (70/30 split)")
        void testDungeonLevelWeighting() {
            // Test the 70/30 weighting of dungeon level vs player level
            // At player level 10, dungeon 1: effective = 10*0.3 + 1*0.7 = 3 + 0.7 = 3.7 ≈ 3
            // At player level 1, dungeon 10: effective = 1*0.3 + 10*0.7 = 0.3 + 7 = 7.3 ≈ 7
            ScaledLootTable highPlayerLowDungeon = generator.generateLootTable(10, 1, CRATE);
            ScaledLootTable lowPlayerHighDungeon = generator.generateLootTable(1, 10, CRATE);

            assertTrue(lowPlayerHighDungeon.getGoldMin() > highPlayerLowDungeon.getGoldMin(),
                "Dungeon level should have more weight than player level in loot calculation");
        }

        @Test
        @DisplayName("Overworld (depth 0) should use only player level")
        void testOverworldUsesOnlyPlayerLevel() {
            ScaledLootTable overworldTable = generator.generateLootTable(10, 0, CRATE);

            // At depth 0, effective level should equal player level
            // Calculate expected gold for player level 10
            int expectedGoldMin = (int) (CRATE.getBaseGold() * (1 + 10 * 0.15));

            assertEquals(expectedGoldMin, overworldTable.getGoldMin(),
                "Overworld loot should be based on player level only");
        }

        @Test
        @DisplayName("Deep dungeons should give significantly more rewards")
        void testDeepDungeonRewards() {
            ScaledLootTable shallowTable = generator.generateLootTable(10, 5, BARREL);
            ScaledLootTable deepTable = generator.generateLootTable(10, 100, BARREL);

            assertTrue(deepTable.getGoldMin() > shallowTable.getGoldMin() * 3,
                "Deep dungeons should give significantly more gold");
            assertTrue(deepTable.getXP() > shallowTable.getXP() * 3,
                "Deep dungeons should give significantly more XP");
        }
    }

    // ==================== Gold Range Tests ====================

    @Nested
    @DisplayName("Gold Range Validation")
    class GoldRangeValidation {

        @Test
        @DisplayName("Gold max should always be greater than or equal to gold min")
        void testGoldMaxGreaterThanMin() {
            for (LootTableGenerator.ObjectType objectType : LootTableGenerator.ObjectType.values()) {
                for (int level = 1; level <= 100; level += 10) {
                    ScaledLootTable table = generator.generateLootTable(level, 0, objectType);

                    assertTrue(table.getGoldMax() >= table.getGoldMin(),
                        "Gold max should be >= gold min for " + objectType + " at level " + level);
                }
            }
        }

        @Test
        @DisplayName("Gold variance should match object type specifications")
        void testGoldVarianceByObjectType() {
            int level = 10;

            // POT: variance 1.5x
            ScaledLootTable potTable = generator.generateLootTable(level, 0, POT);
            assertEquals(1.5f, (float) potTable.getGoldMax() / potTable.getGoldMin(), 0.1f);

            // CRATE: variance 2.0x
            ScaledLootTable crateTable = generator.generateLootTable(level, 0, CRATE);
            assertEquals(2.0f, (float) crateTable.getGoldMax() / crateTable.getGoldMin(), 0.1f);

            // BARREL: variance 2.5x
            ScaledLootTable barrelTable = generator.generateLootTable(level, 0, BARREL);
            assertEquals(2.5f, (float) barrelTable.getGoldMax() / barrelTable.getGoldMin(), 0.1f);

            // CHEST: variance 3.0x
            ScaledLootTable chestTable = generator.generateLootTable(level, 0, CHEST);
            assertEquals(3.0f, (float) chestTable.getGoldMax() / chestTable.getGoldMin(), 0.1f);
        }

        @Test
        @DisplayName("Gold values should be positive integers")
        void testGoldValuesArePositive() {
            for (LootTableGenerator.ObjectType objectType : LootTableGenerator.ObjectType.values()) {
                ScaledLootTable table = generator.generateLootTable(1, 1, objectType);

                assertTrue(table.getGoldMin() > 0,
                    "Gold min should be positive for " + objectType);
                assertTrue(table.getGoldMax() > 0,
                    "Gold max should be positive for " + objectType);
            }
        }
    }

    // ==================== XP Reward Tests ====================

    @Nested
    @DisplayName("XP Reward Validation")
    class XPRewardValidation {

        @Test
        @DisplayName("XP should always be positive")
        void testXPIsPositive() {
            for (LootTableGenerator.ObjectType objectType : LootTableGenerator.ObjectType.values()) {
                for (int level = 1; level <= 100; level += 25) {
                    ScaledLootTable table = generator.generateLootTable(level, 0, objectType);

                    assertTrue(table.getXP() > 0,
                        "XP should be positive for " + objectType + " at level " + level);
                }
            }
        }

        @Test
        @DisplayName("XP should scale according to formula: base * (1 + level * 0.1)")
        void testXPScalingFormula() {
            int effectiveLevel = 10;

            // POT: base XP = 10
            ScaledLootTable potTable = generator.generateLootTable(effectiveLevel, 0, POT);
            int expectedPotXP = (int) (POT.getBaseXP() * (1 + effectiveLevel * 0.1));
            assertEquals(expectedPotXP, potTable.getXP(),
                "POT XP should match scaling formula");

            // CHEST: base XP = 100
            ScaledLootTable chestTable = generator.generateLootTable(effectiveLevel, 0, CHEST);
            int expectedChestXP = (int) (CHEST.getBaseXP() * (1 + effectiveLevel * 0.1));
            assertEquals(expectedChestXP, chestTable.getXP(),
                "CHEST XP should match scaling formula");
        }
    }

    // ==================== Item Drop Tests ====================

    @Nested
    @DisplayName("Item Drop System")
    class ItemDropSystem {

        @Test
        @DisplayName("Loot table should contain item drops list")
        void testLootTableContainsItemDrops() {
            ScaledLootTable table = generator.generateLootTable(10, 5, CHEST);

            assertNotNull(table.getItemDrops(), "Item drops list should not be null");
            // Note: List might be empty if ItemRegistry has no items, which is fine for unit test
        }

        @Test
        @DisplayName("POT should have fewer item drops than CHEST")
        void testPotHasFewerDropsThanChest() {
            ScaledLootTable potTable = generator.generateLootTable(10, 0, POT);
            ScaledLootTable chestTable = generator.generateLootTable(10, 0, CHEST);

            // POTs have low drop chances, CHESTs have high drop chances
            // This is reflected in the number of potential drops added to the table
            assertTrue(potTable.getItemDrops().size() <= chestTable.getItemDrops().size(),
                "Pots should have fewer or equal item drops compared to chests");
        }

        @Test
        @DisplayName("Item drops should have valid quantity ranges")
        void testItemDropQuantityRanges() {
            ScaledLootTable table = generator.generateLootTable(10, 0, CRATE);

            for (ScaledLootTable.ItemDrop drop : table.getItemDrops()) {
                assertTrue(drop.getMinQuantity() > 0,
                    "Min quantity should be positive");
                assertTrue(drop.getMaxQuantity() >= drop.getMinQuantity(),
                    "Max quantity should be >= min quantity");
            }
        }

        @Test
        @DisplayName("Item drops should have valid drop chances (0.0 to 1.0)")
        void testItemDropChanceRange() {
            ScaledLootTable table = generator.generateLootTable(10, 0, CHEST);

            for (ScaledLootTable.ItemDrop drop : table.getItemDrops()) {
                assertTrue(drop.getDropChance() >= 0.0f,
                    "Drop chance should be >= 0.0");
                assertTrue(drop.getDropChance() <= 1.0f,
                    "Drop chance should be <= 1.0");
            }
        }

        @Test
        @DisplayName("Item drops should contain valid items")
        void testItemDropsContainValidItems() {
            ScaledLootTable table = generator.generateLootTable(10, 0, CHEST);

            for (ScaledLootTable.ItemDrop drop : table.getItemDrops()) {
                assertNotNull(drop.getItem(), "Item should not be null");
                assertNotNull(drop.getItem().getId(), "Item ID should not be null");
                assertNotNull(drop.getItem().getName(), "Item name should not be null");
            }
        }
    }

    // ==================== Edge Cases ====================

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Level 1 player in overworld should get minimum rewards")
        void testLevel1PlayerOverworld() {
            ScaledLootTable table = generator.generateLootTable(1, 0, POT);

            assertNotNull(table, "Loot table should be generated for level 1");
            assertTrue(table.getGoldMin() > 0, "Level 1 should still get some gold");
            assertTrue(table.getXP() > 0, "Level 1 should still get some XP");
        }

        @Test
        @DisplayName("Max level (100) player should get appropriate rewards")
        void testMaxLevelPlayer() {
            ScaledLootTable table = generator.generateLootTable(100, 0, CHEST);

            assertNotNull(table, "Loot table should be generated for level 100");
            assertTrue(table.getGoldMin() > 1000, "Level 100 should get substantial gold");
            assertTrue(table.getXP() > 100, "Level 100 should get substantial XP");
        }

        @Test
        @DisplayName("Very deep dungeon (level 100+) should give massive rewards")
        void testVeryDeepDungeon() {
            ScaledLootTable table = generator.generateLootTable(50, 100, CHEST);

            assertTrue(table.getGoldMin() > 5000,
                "Dungeon level 100 should give massive gold rewards");
            assertTrue(table.getXP() > 1000,
                "Dungeon level 100 should give massive XP rewards");
        }

        @Test
        @DisplayName("Low level player in high level dungeon should get dungeon-appropriate loot")
        void testLowLevelPlayerInHighDungeon() {
            ScaledLootTable lowPlayerTable = generator.generateLootTable(1, 50, CRATE);
            ScaledLootTable highPlayerTable = generator.generateLootTable(50, 50, CRATE);

            // Due to 70/30 weighting, both should have similar rewards (dungeon level dominates)
            // Low player: effective = 1*0.3 + 50*0.7 = 35.3
            // High player: effective = 50*0.3 + 50*0.7 = 50
            assertTrue(highPlayerTable.getGoldMin() > lowPlayerTable.getGoldMin(),
                "Higher player level should still matter, but dungeon level dominates");

            // But the difference shouldn't be massive
            assertTrue(highPlayerTable.getGoldMin() < lowPlayerTable.getGoldMin() * 2,
                "Difference should not be too extreme due to dungeon level weighting");
        }

        @Test
        @DisplayName("Zero dungeon level should work correctly (overworld)")
        void testZeroDungeonLevel() {
            ScaledLootTable table = generator.generateLootTable(10, 0, BARREL);

            assertNotNull(table, "Should handle dungeon level 0 (overworld)");
            assertTrue(table.getGoldMin() > 0, "Should generate valid loot for overworld");
        }

        @Test
        @DisplayName("Same level and depth should produce consistent results")
        void testConsistentResultsForSameInputs() {
            ScaledLootTable table1 = generator.generateLootTable(25, 15, CRATE);
            ScaledLootTable table2 = generator.generateLootTable(25, 15, CRATE);

            assertEquals(table1.getGoldMin(), table2.getGoldMin(),
                "Same inputs should produce same gold min");
            assertEquals(table1.getGoldMax(), table2.getGoldMax(),
                "Same inputs should produce same gold max");
            assertEquals(table1.getXP(), table2.getXP(),
                "Same inputs should produce same XP");
        }
    }

    // ==================== ScaledLootTable Class Tests ====================

    @Nested
    @DisplayName("ScaledLootTable Class")
    class ScaledLootTableClass {

        @Test
        @DisplayName("New ScaledLootTable should start with zero values")
        void testNewLootTableInitialization() {
            ScaledLootTable table = new ScaledLootTable();

            assertEquals(0, table.getGoldMin(), "Initial gold min should be 0");
            assertEquals(0, table.getGoldMax(), "Initial gold max should be 0");
            assertEquals(0, table.getXP(), "Initial XP should be 0");
            assertNotNull(table.getItemDrops(), "Item drops list should be initialized");
            assertEquals(0, table.getItemDrops().size(), "Item drops list should be empty");
        }

        @Test
        @DisplayName("Should be able to set and get gold values")
        void testGoldSettersAndGetters() {
            ScaledLootTable table = new ScaledLootTable();

            table.setGoldMin(10);
            table.setGoldMax(50);

            assertEquals(10, table.getGoldMin(), "Gold min should be settable");
            assertEquals(50, table.getGoldMax(), "Gold max should be settable");
        }

        @Test
        @DisplayName("Should be able to set and get XP")
        void testXPSetterAndGetter() {
            ScaledLootTable table = new ScaledLootTable();

            table.setXP(100);

            assertEquals(100, table.getXP(), "XP should be settable");
        }

        @Test
        @DisplayName("Should be able to add item drops")
        void testAddItemDrop() {
            ScaledLootTable table = new ScaledLootTable();

            // We can't easily create a mock Item without LibGDX, so this test is limited
            // In a real scenario, we'd use a mocking framework like Mockito
            assertEquals(0, table.getItemDrops().size(), "Should start with no item drops");
        }
    }

    // ==================== Singleton Pattern Tests ====================

    @Nested
    @DisplayName("Singleton Pattern")
    class SingletonPattern {

        @Test
        @DisplayName("getInstance should return same instance")
        void testGetInstanceReturnsSameInstance() {
            LootTableGenerator instance1 = LootTableGenerator.getInstance();
            LootTableGenerator instance2 = LootTableGenerator.getInstance();

            assertSame(instance1, instance2, "getInstance should return the same instance");
        }

        @Test
        @DisplayName("getInstance should never return null")
        void testGetInstanceNotNull() {
            assertNotNull(LootTableGenerator.getInstance(),
                "getInstance should never return null");
        }
    }

    // ==================== ObjectType Enum Tests ====================

    @Nested
    @DisplayName("ObjectType Enum")
    class ObjectTypeEnum {

        @Test
        @DisplayName("POT should have correct base values")
        void testPotBaseValues() {
            assertEquals(5, POT.getBaseGold(), "POT base gold should be 5");
            assertEquals(10, POT.getBaseXP(), "POT base XP should be 10");
            assertEquals(1.5f, POT.getGoldVariance(), "POT gold variance should be 1.5");
        }

        @Test
        @DisplayName("CRATE should have correct base values")
        void testCrateBaseValues() {
            assertEquals(10, CRATE.getBaseGold(), "CRATE base gold should be 10");
            assertEquals(20, CRATE.getBaseXP(), "CRATE base XP should be 20");
            assertEquals(2.0f, CRATE.getGoldVariance(), "CRATE gold variance should be 2.0");
        }

        @Test
        @DisplayName("BARREL should have correct base values")
        void testBarrelBaseValues() {
            assertEquals(15, BARREL.getBaseGold(), "BARREL base gold should be 15");
            assertEquals(30, BARREL.getBaseXP(), "BARREL base XP should be 30");
            assertEquals(2.5f, BARREL.getGoldVariance(), "BARREL gold variance should be 2.5");
        }

        @Test
        @DisplayName("CHEST should have correct base values")
        void testChestBaseValues() {
            assertEquals(50, CHEST.getBaseGold(), "CHEST base gold should be 50");
            assertEquals(100, CHEST.getBaseXP(), "CHEST base XP should be 100");
            assertEquals(3.0f, CHEST.getGoldVariance(), "CHEST gold variance should be 3.0");
        }

        @Test
        @DisplayName("All object types should be accessible")
        void testAllObjectTypesAccessible() {
            LootTableGenerator.ObjectType[] types = LootTableGenerator.ObjectType.values();

            assertEquals(4, types.length, "Should have exactly 4 object types");
            assertTrue(containsType(types, POT), "Should contain POT");
            assertTrue(containsType(types, CRATE), "Should contain CRATE");
            assertTrue(containsType(types, BARREL), "Should contain BARREL");
            assertTrue(containsType(types, CHEST), "Should contain CHEST");
        }

        private boolean containsType(LootTableGenerator.ObjectType[] types,
                                     LootTableGenerator.ObjectType target) {
            for (LootTableGenerator.ObjectType type : types) {
                if (type == target) return true;
            }
            return false;
        }
    }

    // ==================== Mathematical Accuracy Tests ====================

    @Nested
    @DisplayName("Mathematical Accuracy")
    class MathematicalAccuracy {

        @Test
        @DisplayName("Gold min calculation should match formula: base * (1 + level * 0.15)")
        void testGoldMinCalculation() {
            int effectiveLevel = 20;
            ScaledLootTable table = generator.generateLootTable(effectiveLevel, 0, CRATE);

            int expectedGoldMin = (int) (CRATE.getBaseGold() * (1 + effectiveLevel * 0.15));
            assertEquals(expectedGoldMin, table.getGoldMin(),
                "Gold min should match the scaling formula");
        }

        @Test
        @DisplayName("Gold max calculation should match formula: base * (1 + level * 0.15) * variance")
        void testGoldMaxCalculation() {
            int effectiveLevel = 20;
            ScaledLootTable table = generator.generateLootTable(effectiveLevel, 0, BARREL);

            int expectedGoldMax = (int) (BARREL.getBaseGold() * (1 + effectiveLevel * 0.15)
                                        * BARREL.getGoldVariance());
            assertEquals(expectedGoldMax, table.getGoldMax(),
                "Gold max should match the scaling formula");
        }

        @Test
        @DisplayName("Effective level calculation for overworld should equal player level")
        void testEffectiveLevelOverworld() {
            int playerLevel = 25;
            ScaledLootTable table = generator.generateLootTable(playerLevel, 0, POT);

            // Effective level = player level when dungeon level is 0
            int expectedGoldMin = (int) (POT.getBaseGold() * (1 + playerLevel * 0.15));
            assertEquals(expectedGoldMin, table.getGoldMin(),
                "Overworld effective level should equal player level");
        }

        @Test
        @DisplayName("Effective level calculation for dungeon should use 30/70 weighting")
        void testEffectiveLevelDungeon() {
            int playerLevel = 20;
            int dungeonLevel = 10;
            ScaledLootTable table = generator.generateLootTable(playerLevel, dungeonLevel, CRATE);

            // Effective level = playerLevel * 0.3 + dungeonLevel * 0.7 = 20*0.3 + 10*0.7 = 6 + 7 = 13
            int effectiveLevel = (int) (playerLevel * 0.3 + dungeonLevel * 0.7);
            int expectedGoldMin = (int) (CRATE.getBaseGold() * (1 + effectiveLevel * 0.15));

            assertEquals(expectedGoldMin, table.getGoldMin(),
                "Dungeon effective level should use 30/70 weighting");
        }
    }
}
