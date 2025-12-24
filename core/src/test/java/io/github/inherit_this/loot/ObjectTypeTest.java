package io.github.inherit_this.loot;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static io.github.inherit_this.loot.LootTableGenerator.ObjectType.*;

/**
 * Standalone tests for LootTableGenerator.ObjectType enum.
 * This tests the enum without requiring LibGDX initialization.
 */
@DisplayName("ObjectType Enum Tests")
public class ObjectTypeTest {

    @Nested
    @DisplayName("Enum Values")
    class EnumValues {

        @Test
        @DisplayName("Should have all expected object types")
        void testAllValues() {
            LootTableGenerator.ObjectType[] types = LootTableGenerator.ObjectType.values();

            assertEquals(4, types.length, "Should have exactly 4 object types");
            assertTrue(containsType(types, POT));
            assertTrue(containsType(types, CRATE));
            assertTrue(containsType(types, BARREL));
            assertTrue(containsType(types, CHEST));
        }

        @Test
        @DisplayName("valueOf should return correct enum constant")
        void testValueOf() {
            assertEquals(POT, LootTableGenerator.ObjectType.valueOf("POT"));
            assertEquals(CRATE, LootTableGenerator.ObjectType.valueOf("CRATE"));
            assertEquals(BARREL, LootTableGenerator.ObjectType.valueOf("BARREL"));
            assertEquals(CHEST, LootTableGenerator.ObjectType.valueOf("CHEST"));
        }

        @Test
        @DisplayName("valueOf should throw for invalid name")
        void testValueOfInvalid() {
            assertThrows(IllegalArgumentException.class, () -> {
                LootTableGenerator.ObjectType.valueOf("INVALID");
            });
            assertThrows(IllegalArgumentException.class, () -> {
                LootTableGenerator.ObjectType.valueOf("BOX");
            });
        }

        private boolean containsType(LootTableGenerator.ObjectType[] types, LootTableGenerator.ObjectType target) {
            for (LootTableGenerator.ObjectType type : types) {
                if (type == target) return true;
            }
            return false;
        }
    }

    @Nested
    @DisplayName("POT Base Values")
    class PotBaseValues {

        @Test
        @DisplayName("POT should have base gold of 5")
        void testBaseGold() {
            assertEquals(5, POT.getBaseGold());
        }

        @Test
        @DisplayName("POT should have base XP of 10")
        void testBaseXP() {
            assertEquals(10, POT.getBaseXP());
        }

        @Test
        @DisplayName("POT should have gold variance of 1.5")
        void testGoldVariance() {
            assertEquals(1.5f, POT.getGoldVariance(), 0.001f);
        }

        @Test
        @DisplayName("POT should have lowest base values")
        void testLowestValues() {
            assertTrue(POT.getBaseGold() < CRATE.getBaseGold());
            assertTrue(POT.getBaseGold() < BARREL.getBaseGold());
            assertTrue(POT.getBaseGold() < CHEST.getBaseGold());

            assertTrue(POT.getBaseXP() < CRATE.getBaseXP());
            assertTrue(POT.getBaseXP() < BARREL.getBaseXP());
            assertTrue(POT.getBaseXP() < CHEST.getBaseXP());
        }
    }

    @Nested
    @DisplayName("CRATE Base Values")
    class CrateBaseValues {

        @Test
        @DisplayName("CRATE should have base gold of 10")
        void testBaseGold() {
            assertEquals(10, CRATE.getBaseGold());
        }

        @Test
        @DisplayName("CRATE should have base XP of 20")
        void testBaseXP() {
            assertEquals(20, CRATE.getBaseXP());
        }

        @Test
        @DisplayName("CRATE should have gold variance of 2.0")
        void testGoldVariance() {
            assertEquals(2.0f, CRATE.getGoldVariance(), 0.001f);
        }

        @Test
        @DisplayName("CRATE should have medium base values")
        void testMediumValues() {
            assertTrue(CRATE.getBaseGold() > POT.getBaseGold());
            assertTrue(CRATE.getBaseGold() < BARREL.getBaseGold());
            assertTrue(CRATE.getBaseGold() < CHEST.getBaseGold());
        }
    }

    @Nested
    @DisplayName("BARREL Base Values")
    class BarrelBaseValues {

        @Test
        @DisplayName("BARREL should have base gold of 15")
        void testBaseGold() {
            assertEquals(15, BARREL.getBaseGold());
        }

        @Test
        @DisplayName("BARREL should have base XP of 30")
        void testBaseXP() {
            assertEquals(30, BARREL.getBaseXP());
        }

        @Test
        @DisplayName("BARREL should have gold variance of 2.5")
        void testGoldVariance() {
            assertEquals(2.5f, BARREL.getGoldVariance(), 0.001f);
        }

        @Test
        @DisplayName("BARREL should have higher values than POT and CRATE")
        void testHigherThanPotAndCrate() {
            assertTrue(BARREL.getBaseGold() > POT.getBaseGold());
            assertTrue(BARREL.getBaseGold() > CRATE.getBaseGold());
            assertTrue(BARREL.getBaseGold() < CHEST.getBaseGold());
        }
    }

    @Nested
    @DisplayName("CHEST Base Values")
    class ChestBaseValues {

        @Test
        @DisplayName("CHEST should have base gold of 50")
        void testBaseGold() {
            assertEquals(50, CHEST.getBaseGold());
        }

        @Test
        @DisplayName("CHEST should have base XP of 100")
        void testBaseXP() {
            assertEquals(100, CHEST.getBaseXP());
        }

        @Test
        @DisplayName("CHEST should have gold variance of 3.0")
        void testGoldVariance() {
            assertEquals(3.0f, CHEST.getGoldVariance(), 0.001f);
        }

        @Test
        @DisplayName("CHEST should have highest base values")
        void testHighestValues() {
            assertTrue(CHEST.getBaseGold() > POT.getBaseGold());
            assertTrue(CHEST.getBaseGold() > CRATE.getBaseGold());
            assertTrue(CHEST.getBaseGold() > BARREL.getBaseGold());

            assertTrue(CHEST.getBaseXP() > POT.getBaseXP());
            assertTrue(CHEST.getBaseXP() > CRATE.getBaseXP());
            assertTrue(CHEST.getBaseXP() > BARREL.getBaseXP());
        }
    }

    @Nested
    @DisplayName("Value Progressions")
    class ValueProgressions {

        @Test
        @DisplayName("Base gold should progress: POT < CRATE < BARREL < CHEST")
        void testBaseGoldProgression() {
            assertTrue(POT.getBaseGold() < CRATE.getBaseGold());
            assertTrue(CRATE.getBaseGold() < BARREL.getBaseGold());
            assertTrue(BARREL.getBaseGold() < CHEST.getBaseGold());
        }

        @Test
        @DisplayName("Base XP should progress: POT < CRATE < BARREL < CHEST")
        void testBaseXPProgression() {
            assertTrue(POT.getBaseXP() < CRATE.getBaseXP());
            assertTrue(CRATE.getBaseXP() < BARREL.getBaseXP());
            assertTrue(BARREL.getBaseXP() < CHEST.getBaseXP());
        }

        @Test
        @DisplayName("Gold variance should increase with object value")
        void testGoldVarianceProgression() {
            assertTrue(POT.getGoldVariance() < CRATE.getGoldVariance());
            assertTrue(CRATE.getGoldVariance() < BARREL.getGoldVariance());
            assertTrue(BARREL.getGoldVariance() < CHEST.getGoldVariance());
        }

        @Test
        @DisplayName("All base gold values should be positive")
        void testPositiveBaseGold() {
            for (LootTableGenerator.ObjectType type : LootTableGenerator.ObjectType.values()) {
                assertTrue(type.getBaseGold() > 0, type + " base gold should be positive");
            }
        }

        @Test
        @DisplayName("All base XP values should be positive")
        void testPositiveBaseXP() {
            for (LootTableGenerator.ObjectType type : LootTableGenerator.ObjectType.values()) {
                assertTrue(type.getBaseXP() > 0, type + " base XP should be positive");
            }
        }

        @Test
        @DisplayName("All gold variance values should be >= 1.0")
        void testGoldVarianceMinimum() {
            for (LootTableGenerator.ObjectType type : LootTableGenerator.ObjectType.values()) {
                assertTrue(type.getGoldVariance() >= 1.0f,
                    type + " gold variance should be >= 1.0");
            }
        }
    }

    @Nested
    @DisplayName("Ordinal Values")
    class OrdinalValues {

        @Test
        @DisplayName("Each type should have unique ordinal")
        void testUniqueOrdinals() {
            LootTableGenerator.ObjectType[] types = LootTableGenerator.ObjectType.values();

            for (int i = 0; i < types.length; i++) {
                for (int j = i + 1; j < types.length; j++) {
                    assertNotEquals(types[i].ordinal(), types[j].ordinal(),
                        "Ordinals should be unique");
                }
            }
        }

        @Test
        @DisplayName("Ordinals should be sequential from 0")
        void testSequentialOrdinals() {
            LootTableGenerator.ObjectType[] types = LootTableGenerator.ObjectType.values();

            for (int i = 0; i < types.length; i++) {
                assertEquals(i, types[i].ordinal(),
                    "Ordinal should match array index");
            }
        }
    }

    @Nested
    @DisplayName("Integration Scenarios")
    class IntegrationScenarios {

        @Test
        @DisplayName("Should support switch statements")
        void testSwitchStatement() {
            for (LootTableGenerator.ObjectType type : LootTableGenerator.ObjectType.values()) {
                String category;
                switch (type) {
                    case POT:
                        category = "Small container";
                        break;
                    case CRATE:
                        category = "Medium container";
                        break;
                    case BARREL:
                        category = "Large container";
                        break;
                    case CHEST:
                        category = "Treasure container";
                        break;
                    default:
                        category = null;
                        break;
                }

                assertNotNull(category, "Switch should handle all object types");
            }
        }

        @Test
        @DisplayName("Should work with comparison operations")
        void testComparison() {
            assertTrue(POT == POT);
            assertFalse(POT == CRATE);
            assertTrue(CHEST.equals(CHEST));
            assertFalse(CHEST.equals(BARREL));
        }

        @Test
        @DisplayName("Should be serializable via name")
        void testSerialization() {
            for (LootTableGenerator.ObjectType type : LootTableGenerator.ObjectType.values()) {
                String name = type.name();
                LootTableGenerator.ObjectType restored = LootTableGenerator.ObjectType.valueOf(name);
                assertEquals(type, restored, "Should restore from name");
            }
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle null checks")
        void testNullChecks() {
            LootTableGenerator.ObjectType type = POT;
            assertNotEquals(null, type);
            assertFalse(type.equals(null));
        }

        @Test
        @DisplayName("Should handle toString")
        void testToString() {
            for (LootTableGenerator.ObjectType type : LootTableGenerator.ObjectType.values()) {
                String str = type.toString();
                assertNotNull(str);
                assertFalse(str.isEmpty());
                assertEquals(type.name(), str, "toString should match name");
            }
        }

        @Test
        @DisplayName("Should maintain identity")
        void testIdentity() {
            LootTableGenerator.ObjectType pot1 = POT;
            LootTableGenerator.ObjectType pot2 = LootTableGenerator.ObjectType.valueOf("POT");

            assertSame(pot1, pot2, "Same enum constant should be identical");
        }

        @Test
        @DisplayName("Name should be uppercase")
        void testNameUppercase() {
            for (LootTableGenerator.ObjectType type : LootTableGenerator.ObjectType.values()) {
                String name = type.name();
                assertEquals(name.toUpperCase(), name, "Enum name should be uppercase");
            }
        }
    }

    @Nested
    @DisplayName("Loot Scaling Ratios")
    class LootScalingRatios {

        @Test
        @DisplayName("CHEST should have 10x more base gold than POT")
        void testChestToPotGoldRatio() {
            assertEquals(10, CHEST.getBaseGold() / POT.getBaseGold());
        }

        @Test
        @DisplayName("CHEST should have 10x more base XP than POT")
        void testChestToPotXPRatio() {
            assertEquals(10, CHEST.getBaseXP() / POT.getBaseXP());
        }

        @Test
        @DisplayName("CRATE should have 2x more base gold than POT")
        void testCrateToPotGoldRatio() {
            assertEquals(2, CRATE.getBaseGold() / POT.getBaseGold());
        }

        @Test
        @DisplayName("BARREL should have 3x more base gold than POT")
        void testBarrelToPotGoldRatio() {
            assertEquals(3, BARREL.getBaseGold() / POT.getBaseGold());
        }

        @Test
        @DisplayName("CHEST should have 2x variance of POT")
        void testChestToPotVarianceRatio() {
            assertEquals(2.0f, CHEST.getGoldVariance() / POT.getGoldVariance(), 0.01f);
        }
    }

    @Nested
    @DisplayName("Game Balance Validation")
    class GameBalanceValidation {

        @Test
        @DisplayName("Variance should allow for RNG excitement")
        void testVarianceAllowsRNG() {
            // All variances should be > 1.0 to allow for random variation
            assertTrue(POT.getGoldVariance() > 1.0f);
            assertTrue(CRATE.getGoldVariance() > 1.0f);
            assertTrue(BARREL.getGoldVariance() > 1.0f);
            assertTrue(CHEST.getGoldVariance() > 1.0f);
        }

        @Test
        @DisplayName("More valuable objects should have higher variance for excitement")
        void testValueVarianceCorrelation() {
            // Higher value containers should have more variance
            assertTrue(CHEST.getGoldVariance() > POT.getGoldVariance(),
                "Chests should have more exciting variance than pots");
        }

        @Test
        @DisplayName("Base values should follow RPG conventions")
        void testRPGConventions() {
            // Small containers (pots) typically give single-digit rewards
            assertTrue(POT.getBaseGold() < 10);

            // Chests should give substantial rewards
            assertTrue(CHEST.getBaseGold() >= 50);

            // XP should correlate with difficulty/value
            assertTrue(POT.getBaseXP() <= 20);
            assertTrue(CHEST.getBaseXP() >= 100);
        }
    }

    @Nested
    @DisplayName("Use Cases")
    class UseCases {

        @Test
        @DisplayName("Should calculate gold range correctly for POT")
        void testCalculateGoldRangeForPot() {
            int min = POT.getBaseGold();
            int max = (int) (POT.getBaseGold() * POT.getGoldVariance());

            assertEquals(5, min);
            assertEquals(7, max); // 5 * 1.5 = 7.5 → 7 (truncated)
        }

        @Test
        @DisplayName("Should calculate gold range correctly for CHEST")
        void testCalculateGoldRangeForChest() {
            int min = CHEST.getBaseGold();
            int max = (int) (CHEST.getBaseGold() * CHEST.getGoldVariance());

            assertEquals(50, min);
            assertEquals(150, max); // 50 * 3.0 = 150
        }

        @Test
        @DisplayName("Each object type should be distinguishable by values")
        void testDistinguishableByValues() {
            // No two object types should have the same base gold
            assertNotEquals(POT.getBaseGold(), CRATE.getBaseGold());
            assertNotEquals(POT.getBaseGold(), BARREL.getBaseGold());
            assertNotEquals(POT.getBaseGold(), CHEST.getBaseGold());
            assertNotEquals(CRATE.getBaseGold(), BARREL.getBaseGold());
            assertNotEquals(CRATE.getBaseGold(), CHEST.getBaseGold());
            assertNotEquals(BARREL.getBaseGold(), CHEST.getBaseGold());
        }
    }
}
