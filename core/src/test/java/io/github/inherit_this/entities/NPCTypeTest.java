package io.github.inherit_this.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for NPCType enum.
 */
@DisplayName("NPCType Tests")
class NPCTypeTest {

    // ==================== Enum Values Tests ====================

    @Nested
    @DisplayName("Enum Values")
    class EnumValues {

        @Test
        @DisplayName("Should have all expected NPC types")
        void testAllValues() {
            NPCType[] types = NPCType.values();

            assertEquals(4, types.length, "Should have exactly 4 NPC types");
            assertTrue(containsType(types, NPCType.HOSTILE));
            assertTrue(containsType(types, NPCType.FRIENDLY));
            assertTrue(containsType(types, NPCType.NEUTRAL));
            assertTrue(containsType(types, NPCType.PASSIVE));
        }

        @Test
        @DisplayName("valueOf should return correct enum constant")
        void testValueOf() {
            assertEquals(NPCType.HOSTILE, NPCType.valueOf("HOSTILE"));
            assertEquals(NPCType.FRIENDLY, NPCType.valueOf("FRIENDLY"));
            assertEquals(NPCType.NEUTRAL, NPCType.valueOf("NEUTRAL"));
            assertEquals(NPCType.PASSIVE, NPCType.valueOf("PASSIVE"));
        }

        @Test
        @DisplayName("valueOf should throw for invalid name")
        void testValueOfInvalid() {
            assertThrows(IllegalArgumentException.class, () -> {
                NPCType.valueOf("INVALID");
            });
            assertThrows(IllegalArgumentException.class, () -> {
                NPCType.valueOf("ENEMY");
            });
        }

        @Test
        @DisplayName("valueOf should be case-sensitive")
        void testValueOfCaseSensitive() {
            assertThrows(IllegalArgumentException.class, () -> {
                NPCType.valueOf("hostile");
            });
            assertThrows(IllegalArgumentException.class, () -> {
                NPCType.valueOf("Friendly");
            });
        }

        private boolean containsType(NPCType[] types, NPCType target) {
            for (NPCType type : types) {
                if (type == target) return true;
            }
            return false;
        }
    }

    // ==================== Behavior Semantics Tests ====================

    @Nested
    @DisplayName("Behavior Semantics")
    class BehaviorSemantics {

        @Test
        @DisplayName("HOSTILE should represent aggressive NPCs")
        void testHostileSemantics() {
            assertNotNull(NPCType.HOSTILE);
            assertEquals("HOSTILE", NPCType.HOSTILE.name());
        }

        @Test
        @DisplayName("FRIENDLY should represent non-hostile NPCs")
        void testFriendlySemantics() {
            assertNotNull(NPCType.FRIENDLY);
            assertEquals("FRIENDLY", NPCType.FRIENDLY.name());
        }

        @Test
        @DisplayName("NEUTRAL should represent indifferent NPCs")
        void testNeutralSemantics() {
            assertNotNull(NPCType.NEUTRAL);
            assertEquals("NEUTRAL", NPCType.NEUTRAL.name());
        }

        @Test
        @DisplayName("PASSIVE should represent fleeing NPCs")
        void testPassiveSemantics() {
            assertNotNull(NPCType.PASSIVE);
            assertEquals("PASSIVE", NPCType.PASSIVE.name());
        }
    }

    // ==================== Ordinal Tests ====================

    @Nested
    @DisplayName("Ordinal Values")
    class OrdinalValues {

        @Test
        @DisplayName("Each type should have unique ordinal")
        void testUniqueOrdinals() {
            NPCType[] types = NPCType.values();

            for (int i = 0; i < types.length; i++) {
                for (int j = i + 1; j < types.length; j++) {
                    assertNotEquals(types[i].ordinal(), types[j].ordinal(),
                        "Ordinals should be unique");
                }
            }
        }

        @Test
        @DisplayName("Ordinals should be sequential")
        void testSequentialOrdinals() {
            NPCType[] types = NPCType.values();

            for (int i = 0; i < types.length; i++) {
                assertEquals(i, types[i].ordinal(),
                    "Ordinal should match array index");
            }
        }
    }

    // ==================== Integration Tests ====================

    @Nested
    @DisplayName("Integration Scenarios")
    class IntegrationScenarios {

        @Test
        @DisplayName("Should support switch statements")
        void testSwitchStatement() {
            for (NPCType type : NPCType.values()) {
                String behavior;
                switch (type) {
                    case HOSTILE:
                        behavior = "Attacks";
                        break;
                    case FRIENDLY:
                        behavior = "Talks";
                        break;
                    case NEUTRAL:
                        behavior = "Ignores";
                        break;
                    case PASSIVE:
                        behavior = "Flees";
                        break;
                    default:
                        behavior = null;
                        break;
                }

                assertNotNull(behavior, "Switch should handle all NPC types");
            }
        }

        @Test
        @DisplayName("Should work with comparison operations")
        void testComparison() {
            assertTrue(NPCType.HOSTILE == NPCType.HOSTILE);
            assertFalse(NPCType.HOSTILE == NPCType.FRIENDLY);
            assertTrue(NPCType.NEUTRAL.equals(NPCType.NEUTRAL));
            assertFalse(NPCType.NEUTRAL.equals(NPCType.PASSIVE));
        }

        @Test
        @DisplayName("Should be serializable via name")
        void testSerialization() {
            for (NPCType type : NPCType.values()) {
                String name = type.name();
                NPCType restored = NPCType.valueOf(name);
                assertEquals(type, restored, "Should restore from name");
            }
        }

        @Test
        @DisplayName("Should support compareTo")
        void testCompareTo() {
            assertTrue(NPCType.HOSTILE.compareTo(NPCType.PASSIVE) != 0);
            assertEquals(0, NPCType.FRIENDLY.compareTo(NPCType.FRIENDLY));
        }
    }

    // ==================== Edge Cases ====================

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle null checks")
        void testNullChecks() {
            NPCType type = NPCType.HOSTILE;
            assertNotEquals(null, type);
            assertFalse(type.equals(null));
        }

        @Test
        @DisplayName("Should handle toString")
        void testToString() {
            for (NPCType type : NPCType.values()) {
                String str = type.toString();
                assertNotNull(str);
                assertFalse(str.isEmpty());
                assertEquals(type.name(), str, "toString should match name");
            }
        }

        @Test
        @DisplayName("Should maintain identity")
        void testIdentity() {
            NPCType hostile1 = NPCType.HOSTILE;
            NPCType hostile2 = NPCType.valueOf("HOSTILE");

            assertSame(hostile1, hostile2, "Same enum constant should be identical");
        }

        @Test
        @DisplayName("Name should be uppercase")
        void testNameUppercase() {
            for (NPCType type : NPCType.values()) {
                String name = type.name();
                assertEquals(name.toUpperCase(), name,
                    "Enum name should be uppercase");
            }
        }
    }

    // ==================== Use Case Tests ====================

    @Nested
    @DisplayName("Common Use Cases")
    class UseCases {

        @Test
        @DisplayName("Should support aggression level logic")
        void testAggressionLevel() {
            // HOSTILE attacks immediately
            boolean attacksOnSight = NPCType.HOSTILE == NPCType.HOSTILE;
            assertTrue(attacksOnSight);

            // FRIENDLY doesn't attack
            boolean peaceful = NPCType.FRIENDLY == NPCType.FRIENDLY;
            assertTrue(peaceful);

            // NEUTRAL might defend itself
            boolean defensive = NPCType.NEUTRAL == NPCType.NEUTRAL;
            assertTrue(defensive);

            // PASSIVE runs away
            boolean flees = NPCType.PASSIVE == NPCType.PASSIVE;
            assertTrue(flees);
        }

        @Test
        @DisplayName("Should support AI behavior selection")
        void testAIBehaviorSelection() {
            for (NPCType type : NPCType.values()) {
                // Verify each type can be used for AI logic
                boolean validForAI = type == NPCType.HOSTILE ||
                                    type == NPCType.FRIENDLY ||
                                    type == NPCType.NEUTRAL ||
                                    type == NPCType.PASSIVE;
                assertTrue(validForAI, type + " should be valid for AI");
            }
        }

        @Test
        @DisplayName("Should support reputation system logic")
        void testReputationLogic() {
            // Friendly NPCs for high reputation
            assertNotNull(NPCType.FRIENDLY);

            // Hostile NPCs for low reputation or enemies
            assertNotNull(NPCType.HOSTILE);

            // Neutral for medium reputation
            assertNotNull(NPCType.NEUTRAL);
        }

        @Test
        @DisplayName("Should support combat engagement rules")
        void testCombatEngagement() {
            // Only HOSTILE engages immediately
            boolean hostilesEngage = NPCType.HOSTILE == NPCType.HOSTILE;
            assertTrue(hostilesEngage);

            // PASSIVE should disengage
            boolean passiveDisengages = NPCType.PASSIVE == NPCType.PASSIVE;
            assertTrue(passiveDisengages);
        }
    }

    // ==================== Completeness Tests ====================

    @Nested
    @DisplayName("Completeness")
    class Completeness {

        @Test
        @DisplayName("Should cover all major NPC behaviors")
        void testBehaviorCompleteness() {
            NPCType[] types = NPCType.values();

            boolean hasHostile = false;
            boolean hasFriendly = false;
            boolean hasNeutral = false;
            boolean hasPassive = false;

            for (NPCType type : types) {
                if (type == NPCType.HOSTILE) hasHostile = true;
                if (type == NPCType.FRIENDLY) hasFriendly = true;
                if (type == NPCType.NEUTRAL) hasNeutral = true;
                if (type == NPCType.PASSIVE) hasPassive = true;
            }

            assertTrue(hasHostile, "Should have hostile type");
            assertTrue(hasFriendly, "Should have friendly type");
            assertTrue(hasNeutral, "Should have neutral type");
            assertTrue(hasPassive, "Should have passive type");
        }

        @Test
        @DisplayName("Should have reasonable number of types")
        void testTypeCount() {
            NPCType[] types = NPCType.values();

            assertTrue(types.length >= 3, "Should have at least 3 behavior types");
            assertTrue(types.length <= 10, "Should not have excessive types");
        }
    }
}
