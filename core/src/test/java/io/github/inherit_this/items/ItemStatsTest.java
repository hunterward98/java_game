package io.github.inherit_this.items;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for the ItemStats class.
 * Tests stat creation, factory methods, and validation.
 */
class ItemStatsTest {

    // ==================== Factory Method Tests ====================

    @Nested
    @DisplayName("Factory Methods")
    class FactoryMethods {

        @Test
        @DisplayName("none() should create stats with all zeros")
        void testNoneFactory() {
            ItemStats stats = ItemStats.none();

            assertEquals(0, stats.getDamage(), "Damage should be 0");
            assertEquals(0, stats.getArmor(), "Armor should be 0");
            assertEquals(0, stats.getDurability(), "Durability should be 0");
            assertEquals(0f, stats.getAttackSpeed(), 0.001f, "Attack speed should be 0");
            assertEquals(0, stats.getStrength(), "Strength should be 0");
            assertEquals(0, stats.getDexterity(), "Dexterity should be 0");
            assertEquals(0, stats.getVitality(), "Vitality should be 0");
            assertEquals(0, stats.getMagic(), "Magic should be 0");
            assertFalse(stats.hasStats(), "Should have no stats");
        }

        @Test
        @DisplayName("weapon() with attack speed should create weapon stats")
        void testWeaponFactoryWithAttackSpeed() {
            ItemStats stats = ItemStats.weapon(15, 100, 1.5f);

            assertEquals(15, stats.getDamage(), "Damage should be 15");
            assertEquals(100, stats.getDurability(), "Durability should be 100");
            assertEquals(1.5f, stats.getAttackSpeed(), 0.001f, "Attack speed should be 1.5");
            assertEquals(0, stats.getArmor(), "Armor should be 0");
            assertEquals(0, stats.getStrength(), "Strength should be 0");
            assertTrue(stats.hasStats(), "Should have stats");
        }

        @Test
        @DisplayName("weapon() without attack speed should default to 1.0")
        void testWeaponFactoryDefaultAttackSpeed() {
            ItemStats stats = ItemStats.weapon(20, 150);

            assertEquals(20, stats.getDamage(), "Damage should be 20");
            assertEquals(150, stats.getDurability(), "Durability should be 150");
            assertEquals(1.0f, stats.getAttackSpeed(), 0.001f, "Attack speed should default to 1.0");
            assertEquals(0, stats.getArmor(), "Armor should be 0");
            assertTrue(stats.hasStats(), "Should have stats");
        }

        @Test
        @DisplayName("armor() should create armor stats")
        void testArmorFactory() {
            ItemStats stats = ItemStats.armor(25, 200);

            assertEquals(25, stats.getArmor(), "Armor should be 25");
            assertEquals(200, stats.getDurability(), "Durability should be 200");
            assertEquals(0, stats.getDamage(), "Damage should be 0");
            assertEquals(0f, stats.getAttackSpeed(), 0.001f, "Attack speed should be 0");
            assertEquals(0, stats.getStrength(), "Strength should be 0");
            assertTrue(stats.hasStats(), "Should have stats");
        }
    }

    // ==================== Full Constructor Tests ====================

    @Nested
    @DisplayName("Full Constructor")
    class FullConstructor {

        @Test
        @DisplayName("Should create stats with all parameters")
        void testFullConstructor() {
            ItemStats stats = new ItemStats(
                10,  // damage
                5,   // armor
                100, // durability
                1.2f,// attackSpeed
                3,   // strength
                4,   // dexterity
                2,   // vitality
                1    // magic
            );

            assertEquals(10, stats.getDamage());
            assertEquals(5, stats.getArmor());
            assertEquals(100, stats.getDurability());
            assertEquals(1.2f, stats.getAttackSpeed(), 0.001f);
            assertEquals(3, stats.getStrength());
            assertEquals(4, stats.getDexterity());
            assertEquals(2, stats.getVitality());
            assertEquals(1, stats.getMagic());
            assertTrue(stats.hasStats());
        }

        @Test
        @DisplayName("Should handle all zero values")
        void testAllZeroValues() {
            ItemStats stats = new ItemStats(0, 0, 0, 0f, 0, 0, 0, 0);

            assertEquals(0, stats.getDamage());
            assertEquals(0, stats.getArmor());
            assertEquals(0, stats.getDurability());
            assertEquals(0f, stats.getAttackSpeed(), 0.001f);
            assertEquals(0, stats.getStrength());
            assertEquals(0, stats.getDexterity());
            assertEquals(0, stats.getVitality());
            assertEquals(0, stats.getMagic());
            assertFalse(stats.hasStats(), "All zeros should have no stats");
        }

        @Test
        @DisplayName("Should handle negative values")
        void testNegativeValues() {
            ItemStats stats = new ItemStats(-10, -5, -100, -1.0f, -3, -4, -2, -1);

            assertEquals(-10, stats.getDamage(), "Should allow negative damage");
            assertEquals(-5, stats.getArmor(), "Should allow negative armor");
            assertEquals(-100, stats.getDurability(), "Should allow negative durability");
            assertEquals(-1.0f, stats.getAttackSpeed(), 0.001f, "Should allow negative attack speed");
            assertEquals(-3, stats.getStrength(), "Should allow negative strength");
            assertFalse(stats.hasStats(), "Negative stats should not count as hasStats");
        }

        @Test
        @DisplayName("Should handle very large values")
        void testLargeValues() {
            ItemStats stats = new ItemStats(
                9999,   // damage
                9999,   // armor
                99999,  // durability
                100.0f, // attackSpeed
                999,    // strength
                999,    // dexterity
                999,    // vitality
                999     // magic
            );

            assertEquals(9999, stats.getDamage());
            assertEquals(9999, stats.getArmor());
            assertEquals(99999, stats.getDurability());
            assertEquals(100.0f, stats.getAttackSpeed(), 0.001f);
            assertTrue(stats.hasStats());
        }
    }

    // ==================== hasStats Tests ====================

    @Nested
    @DisplayName("hasStats Method")
    class HasStatsMethod {

        @Test
        @DisplayName("Should return true when damage > 0")
        void testHasStatsWithDamage() {
            ItemStats stats = new ItemStats(1, 0, 0, 0f, 0, 0, 0, 0);
            assertTrue(stats.hasStats(), "Should have stats with damage > 0");
        }

        @Test
        @DisplayName("Should return true when armor > 0")
        void testHasStatsWithArmor() {
            ItemStats stats = new ItemStats(0, 1, 0, 0f, 0, 0, 0, 0);
            assertTrue(stats.hasStats(), "Should have stats with armor > 0");
        }

        @Test
        @DisplayName("Should return true when strength > 0")
        void testHasStatsWithStrength() {
            ItemStats stats = new ItemStats(0, 0, 0, 0f, 1, 0, 0, 0);
            assertTrue(stats.hasStats(), "Should have stats with strength > 0");
        }

        @Test
        @DisplayName("Should return true when dexterity > 0")
        void testHasStatsWithDexterity() {
            ItemStats stats = new ItemStats(0, 0, 0, 0f, 0, 1, 0, 0);
            assertTrue(stats.hasStats(), "Should have stats with dexterity > 0");
        }

        @Test
        @DisplayName("Should return true when vitality > 0")
        void testHasStatsWithVitality() {
            ItemStats stats = new ItemStats(0, 0, 0, 0f, 0, 0, 1, 0);
            assertTrue(stats.hasStats(), "Should have stats with vitality > 0");
        }

        @Test
        @DisplayName("Should return true when magic > 0")
        void testHasStatsWithMagic() {
            ItemStats stats = new ItemStats(0, 0, 0, 0f, 0, 0, 0, 1);
            assertTrue(stats.hasStats(), "Should have stats with magic > 0");
        }

        @Test
        @DisplayName("Should return false when only durability > 0")
        void testHasStatsWithOnlyDurability() {
            ItemStats stats = new ItemStats(0, 0, 100, 0f, 0, 0, 0, 0);
            assertFalse(stats.hasStats(), "Durability alone should not count as stats");
        }

        @Test
        @DisplayName("Should return false when only attack speed > 0")
        void testHasStatsWithOnlyAttackSpeed() {
            ItemStats stats = new ItemStats(0, 0, 0, 2.0f, 0, 0, 0, 0);
            assertFalse(stats.hasStats(), "Attack speed alone should not count as stats");
        }

        @Test
        @DisplayName("Should return false when all values are 0")
        void testHasStatsAllZero() {
            ItemStats stats = ItemStats.none();
            assertFalse(stats.hasStats(), "All zeros should not have stats");
        }

        @Test
        @DisplayName("Should return true when multiple stats > 0")
        void testHasStatsMultiple() {
            ItemStats stats = new ItemStats(10, 5, 100, 1.0f, 3, 2, 1, 0);
            assertTrue(stats.hasStats(), "Should have stats with multiple positive values");
        }
    }

    // ==================== Getter Tests ====================

    @Nested
    @DisplayName("Getters")
    class Getters {

        @Test
        @DisplayName("All getters should return correct values")
        void testAllGetters() {
            ItemStats stats = new ItemStats(12, 8, 150, 1.3f, 5, 6, 7, 8);

            assertEquals(12, stats.getDamage(), "getDamage should return damage");
            assertEquals(8, stats.getArmor(), "getArmor should return armor");
            assertEquals(150, stats.getDurability(), "getDurability should return durability");
            assertEquals(1.3f, stats.getAttackSpeed(), 0.001f, "getAttackSpeed should return attack speed");
            assertEquals(5, stats.getStrength(), "getStrength should return strength");
            assertEquals(6, stats.getDexterity(), "getDexterity should return dexterity");
            assertEquals(7, stats.getVitality(), "getVitality should return vitality");
            assertEquals(8, stats.getMagic(), "getMagic should return magic");
        }

        @Test
        @DisplayName("Getters should work with zero values")
        void testGettersWithZero() {
            ItemStats stats = ItemStats.none();

            assertEquals(0, stats.getDamage());
            assertEquals(0, stats.getArmor());
            assertEquals(0, stats.getDurability());
            assertEquals(0f, stats.getAttackSpeed(), 0.001f);
            assertEquals(0, stats.getStrength());
            assertEquals(0, stats.getDexterity());
            assertEquals(0, stats.getVitality());
            assertEquals(0, stats.getMagic());
        }

        @Test
        @DisplayName("Getters should be consistent across multiple calls")
        void testGetterConsistency() {
            ItemStats stats = new ItemStats(15, 10, 200, 1.5f, 4, 5, 6, 7);

            assertEquals(stats.getDamage(), stats.getDamage(), "getDamage should be consistent");
            assertEquals(stats.getArmor(), stats.getArmor(), "getArmor should be consistent");
            assertEquals(stats.getAttackSpeed(), stats.getAttackSpeed(), "getAttackSpeed should be consistent");
        }
    }

    // ==================== Weapon Stats Comparison ====================

    @Nested
    @DisplayName("Weapon Stats Comparison")
    class WeaponStatsComparison {

        @Test
        @DisplayName("Fast weapon should have higher attack speed")
        void testFastWeapon() {
            ItemStats dagger = ItemStats.weapon(8, 50, 2.0f);
            ItemStats sword = ItemStats.weapon(15, 100, 1.0f);

            assertTrue(dagger.getAttackSpeed() > sword.getAttackSpeed(),
                "Dagger should be faster than sword");
            assertTrue(dagger.getDamage() < sword.getDamage(),
                "Dagger should have less damage than sword");
        }

        @Test
        @DisplayName("Heavy weapon should have lower attack speed")
        void testHeavyWeapon() {
            ItemStats hammer = ItemStats.weapon(30, 200, 0.6f);
            ItemStats sword = ItemStats.weapon(15, 100, 1.0f);

            assertTrue(hammer.getAttackSpeed() < sword.getAttackSpeed(),
                "Hammer should be slower than sword");
            assertTrue(hammer.getDamage() > sword.getDamage(),
                "Hammer should have more damage than sword");
        }
    }

    // ==================== Armor Stats Comparison ====================

    @Nested
    @DisplayName("Armor Stats Comparison")
    class ArmorStatsComparison {

        @Test
        @DisplayName("Heavy armor should have more armor value")
        void testHeavyArmor() {
            ItemStats leather = ItemStats.armor(10, 80);
            ItemStats iron = ItemStats.armor(25, 150);
            ItemStats plate = ItemStats.armor(50, 200);

            assertTrue(leather.getArmor() < iron.getArmor(),
                "Leather should have less armor than iron");
            assertTrue(iron.getArmor() < plate.getArmor(),
                "Iron should have less armor than plate");
        }

        @Test
        @DisplayName("Durable armor should have more durability")
        void testArmorDurability() {
            ItemStats weak = ItemStats.armor(20, 50);
            ItemStats strong = ItemStats.armor(20, 300);

            assertEquals(weak.getArmor(), strong.getArmor(),
                "Both should have same armor value");
            assertTrue(weak.getDurability() < strong.getDurability(),
                "Strong armor should have more durability");
        }
    }

    // ==================== Edge Cases ====================

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle fractional attack speed")
        void testFractionalAttackSpeed() {
            ItemStats stats = ItemStats.weapon(10, 100, 0.75f);
            assertEquals(0.75f, stats.getAttackSpeed(), 0.001f);
        }

        @Test
        @DisplayName("Should handle very small attack speed")
        void testVerySmallAttackSpeed() {
            ItemStats stats = ItemStats.weapon(50, 300, 0.1f);
            assertEquals(0.1f, stats.getAttackSpeed(), 0.001f);
            assertEquals(50, stats.getDamage());
        }

        @Test
        @DisplayName("Should handle very large attack speed")
        void testVeryLargeAttackSpeed() {
            ItemStats stats = ItemStats.weapon(1, 10, 10.0f);
            assertEquals(10.0f, stats.getAttackSpeed(), 0.001f);
        }

        @Test
        @DisplayName("ItemStats should be immutable")
        void testImmutability() {
            ItemStats stats = new ItemStats(10, 5, 100, 1.0f, 3, 4, 2, 1);

            int damage1 = stats.getDamage();
            int damage2 = stats.getDamage();

            assertEquals(damage1, damage2, "Values should not change");
        }

        @Test
        @DisplayName("Multiple ItemStats instances should be independent")
        void testIndependence() {
            ItemStats stats1 = ItemStats.weapon(10, 100);
            ItemStats stats2 = ItemStats.weapon(20, 200);

            assertNotSame(stats1, stats2, "Should be different instances");
            assertNotEquals(stats1.getDamage(), stats2.getDamage());
            assertNotEquals(stats1.getDurability(), stats2.getDurability());
        }

        @Test
        @DisplayName("Should handle zero durability")
        void testZeroDurability() {
            ItemStats stats = ItemStats.weapon(10, 0, 1.0f);
            assertEquals(0, stats.getDurability(), "Should allow zero durability");
            assertTrue(stats.hasStats(), "Should still have stats with damage");
        }

        @Test
        @DisplayName("Factory methods should create independent instances")
        void testFactoryMethodIndependence() {
            ItemStats none1 = ItemStats.none();
            ItemStats none2 = ItemStats.none();

            assertNotSame(none1, none2, "Each call should create new instance");
        }

        @Test
        @DisplayName("Should handle stats with only attribute bonuses")
        void testOnlyAttributeBonuses() {
            ItemStats stats = new ItemStats(0, 0, 0, 0f, 5, 3, 2, 4);

            assertEquals(0, stats.getDamage());
            assertEquals(0, stats.getArmor());
            assertTrue(stats.hasStats(), "Should have stats from attributes");
            assertEquals(5, stats.getStrength());
            assertEquals(3, stats.getDexterity());
            assertEquals(2, stats.getVitality());
            assertEquals(4, stats.getMagic());
        }
    }
}
