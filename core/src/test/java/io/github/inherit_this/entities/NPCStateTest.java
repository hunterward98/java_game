package io.github.inherit_this.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for NPC.NPCState enum.
 */
@DisplayName("NPCState Tests")
public class NPCStateTest {

    @Nested
    @DisplayName("Enum Values")
    class EnumValues {

        @Test
        @DisplayName("Should have all expected NPC states")
        void testAllValues() {
            NPC.NPCState[] states = NPC.NPCState.values();
            assertEquals(6, states.length, "Should have exactly 6 NPC states");
        }

        @Test
        @DisplayName("Should have IDLE state")
        void testIdleExists() {
            assertNotNull(NPC.NPCState.IDLE);
        }

        @Test
        @DisplayName("Should have WANDER state")
        void testWanderExists() {
            assertNotNull(NPC.NPCState.WANDER);
        }

        @Test
        @DisplayName("Should have CHASE state")
        void testChaseExists() {
            assertNotNull(NPC.NPCState.CHASE);
        }

        @Test
        @DisplayName("Should have ATTACK state")
        void testAttackExists() {
            assertNotNull(NPC.NPCState.ATTACK);
        }

        @Test
        @DisplayName("Should have FLEE state")
        void testFleeExists() {
            assertNotNull(NPC.NPCState.FLEE);
        }

        @Test
        @DisplayName("Should have DEAD state")
        void testDeadExists() {
            assertNotNull(NPC.NPCState.DEAD);
        }

        @Test
        @DisplayName("valueOf should work for all states")
        void testValueOf() {
            assertEquals(NPC.NPCState.IDLE, NPC.NPCState.valueOf("IDLE"));
            assertEquals(NPC.NPCState.WANDER, NPC.NPCState.valueOf("WANDER"));
            assertEquals(NPC.NPCState.CHASE, NPC.NPCState.valueOf("CHASE"));
            assertEquals(NPC.NPCState.ATTACK, NPC.NPCState.valueOf("ATTACK"));
            assertEquals(NPC.NPCState.FLEE, NPC.NPCState.valueOf("FLEE"));
            assertEquals(NPC.NPCState.DEAD, NPC.NPCState.valueOf("DEAD"));
        }

        @Test
        @DisplayName("valueOf should throw for invalid name")
        void testValueOfInvalid() {
            assertThrows(IllegalArgumentException.class, () -> {
                NPC.NPCState.valueOf("INVALID");
            });
            assertThrows(IllegalArgumentException.class, () -> {
                NPC.NPCState.valueOf("SLEEPING");
            });
        }
    }

    @Nested
    @DisplayName("Ordinal Values")
    class OrdinalValues {

        @Test
        @DisplayName("Each NPC state should have unique ordinal")
        void testUniqueOrdinals() {
            assertNotEquals(NPC.NPCState.IDLE.ordinal(), NPC.NPCState.WANDER.ordinal());
            assertNotEquals(NPC.NPCState.IDLE.ordinal(), NPC.NPCState.CHASE.ordinal());
            assertNotEquals(NPC.NPCState.IDLE.ordinal(), NPC.NPCState.ATTACK.ordinal());
            assertNotEquals(NPC.NPCState.IDLE.ordinal(), NPC.NPCState.FLEE.ordinal());
            assertNotEquals(NPC.NPCState.IDLE.ordinal(), NPC.NPCState.DEAD.ordinal());
        }

        @Test
        @DisplayName("Ordinals should be sequential from 0")
        void testSequentialOrdinals() {
            NPC.NPCState[] states = NPC.NPCState.values();
            for (int i = 0; i < states.length; i++) {
                assertEquals(i, states[i].ordinal(), "Ordinal should match array index");
            }
        }
    }

    @Nested
    @DisplayName("Integration Scenarios")
    class IntegrationScenarios {

        @Test
        @DisplayName("Should support switch statements")
        void testSwitchStatement() {
            for (NPC.NPCState state : NPC.NPCState.values()) {
                String stateDescription;
                switch (state) {
                    case IDLE:
                        stateDescription = "Standing still";
                        break;
                    case WANDER:
                        stateDescription = "Random movement";
                        break;
                    case CHASE:
                        stateDescription = "Pursuing target";
                        break;
                    case ATTACK:
                        stateDescription = "In combat";
                        break;
                    case FLEE:
                        stateDescription = "Running away";
                        break;
                    case DEAD:
                        stateDescription = "Defeated";
                        break;
                    default:
                        stateDescription = null;
                        break;
                }
                assertNotNull(stateDescription, "Switch should handle all NPC states");
            }
        }

        @Test
        @DisplayName("Should work with comparison operations")
        void testComparison() {
            assertTrue(NPC.NPCState.IDLE == NPC.NPCState.IDLE);
            assertFalse(NPC.NPCState.IDLE == NPC.NPCState.CHASE);
            assertTrue(NPC.NPCState.ATTACK.equals(NPC.NPCState.ATTACK));
            assertFalse(NPC.NPCState.ATTACK.equals(NPC.NPCState.FLEE));
        }

        @Test
        @DisplayName("Should be serializable via name")
        void testSerialization() {
            for (NPC.NPCState state : NPC.NPCState.values()) {
                String name = state.name();
                NPC.NPCState restored = NPC.NPCState.valueOf(name);
                assertEquals(state, restored, "Should restore from name");
            }
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle null checks")
        void testNullChecks() {
            NPC.NPCState state = NPC.NPCState.IDLE;
            assertNotEquals(null, state);
            assertFalse(state.equals(null));
        }

        @Test
        @DisplayName("Should handle toString")
        void testToString() {
            for (NPC.NPCState state : NPC.NPCState.values()) {
                String str = state.toString();
                assertNotNull(str);
                assertFalse(str.isEmpty());
                assertEquals(state.name(), str, "toString should match name");
            }
        }

        @Test
        @DisplayName("Should maintain identity")
        void testIdentity() {
            NPC.NPCState idle1 = NPC.NPCState.IDLE;
            NPC.NPCState idle2 = NPC.NPCState.valueOf("IDLE");
            assertSame(idle1, idle2, "Same enum constant should be identical");
        }

        @Test
        @DisplayName("Name should be uppercase")
        void testNameUppercase() {
            for (NPC.NPCState state : NPC.NPCState.values()) {
                String name = state.name();
                assertEquals(name.toUpperCase(), name, "Enum name should be uppercase");
            }
        }
    }

    @Nested
    @DisplayName("Use Cases")
    class UseCases {

        @Test
        @DisplayName("NPC states should represent AI behaviors")
        void testAIBehaviors() {
            // IDLE for standing still
            assertNotNull(NPC.NPCState.IDLE);

            // WANDER for random movement
            assertNotNull(NPC.NPCState.WANDER);

            // CHASE for pursuing target
            assertNotNull(NPC.NPCState.CHASE);

            // ATTACK for combat
            assertNotNull(NPC.NPCState.ATTACK);

            // FLEE for running away
            assertNotNull(NPC.NPCState.FLEE);

            // DEAD for defeated
            assertNotNull(NPC.NPCState.DEAD);
        }

        @Test
        @DisplayName("Each NPC state should be distinguishable")
        void testDistinguishable() {
            assertNotEquals(NPC.NPCState.IDLE, NPC.NPCState.WANDER);
            assertNotEquals(NPC.NPCState.CHASE, NPC.NPCState.ATTACK);
            assertNotEquals(NPC.NPCState.FLEE, NPC.NPCState.DEAD);
        }

        @Test
        @DisplayName("DEAD state should be final state")
        void testDeadState() {
            // DEAD is typically a terminal state in game logic
            assertNotNull(NPC.NPCState.DEAD);
            assertEquals("DEAD", NPC.NPCState.DEAD.name());
        }
    }
}
