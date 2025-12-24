package io.github.inherit_this.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for FriendlyNPC.NPCRole enum.
 */
@DisplayName("NPCRole Tests")
public class NPCRoleTest {

    @Nested
    @DisplayName("Enum Values")
    class EnumValues {

        @Test
        @DisplayName("Should have all expected NPC roles")
        void testAllValues() {
            FriendlyNPC.NPCRole[] roles = FriendlyNPC.NPCRole.values();
            assertEquals(4, roles.length, "Should have exactly 4 NPC roles");
        }

        @Test
        @DisplayName("Should have MERCHANT role")
        void testMerchantExists() {
            assertNotNull(FriendlyNPC.NPCRole.MERCHANT);
        }

        @Test
        @DisplayName("Should have QUEST_GIVER role")
        void testQuestGiverExists() {
            assertNotNull(FriendlyNPC.NPCRole.QUEST_GIVER);
        }

        @Test
        @DisplayName("Should have GUARD role")
        void testGuardExists() {
            assertNotNull(FriendlyNPC.NPCRole.GUARD);
        }

        @Test
        @DisplayName("Should have VILLAGER role")
        void testVillagerExists() {
            assertNotNull(FriendlyNPC.NPCRole.VILLAGER);
        }

        @Test
        @DisplayName("valueOf should work for all roles")
        void testValueOf() {
            assertEquals(FriendlyNPC.NPCRole.MERCHANT,
                FriendlyNPC.NPCRole.valueOf("MERCHANT"));
            assertEquals(FriendlyNPC.NPCRole.QUEST_GIVER,
                FriendlyNPC.NPCRole.valueOf("QUEST_GIVER"));
            assertEquals(FriendlyNPC.NPCRole.GUARD,
                FriendlyNPC.NPCRole.valueOf("GUARD"));
            assertEquals(FriendlyNPC.NPCRole.VILLAGER,
                FriendlyNPC.NPCRole.valueOf("VILLAGER"));
        }

        @Test
        @DisplayName("valueOf should throw for invalid name")
        void testValueOfInvalid() {
            assertThrows(IllegalArgumentException.class, () -> {
                FriendlyNPC.NPCRole.valueOf("INVALID");
            });
            assertThrows(IllegalArgumentException.class, () -> {
                FriendlyNPC.NPCRole.valueOf("BLACKSMITH");
            });
        }
    }

    @Nested
    @DisplayName("Ordinal Values")
    class OrdinalValues {

        @Test
        @DisplayName("Each NPC role should have unique ordinal")
        void testUniqueOrdinals() {
            assertNotEquals(FriendlyNPC.NPCRole.MERCHANT.ordinal(),
                FriendlyNPC.NPCRole.QUEST_GIVER.ordinal());
            assertNotEquals(FriendlyNPC.NPCRole.MERCHANT.ordinal(),
                FriendlyNPC.NPCRole.GUARD.ordinal());
            assertNotEquals(FriendlyNPC.NPCRole.MERCHANT.ordinal(),
                FriendlyNPC.NPCRole.VILLAGER.ordinal());
        }

        @Test
        @DisplayName("Ordinals should be sequential from 0")
        void testSequentialOrdinals() {
            FriendlyNPC.NPCRole[] roles = FriendlyNPC.NPCRole.values();
            for (int i = 0; i < roles.length; i++) {
                assertEquals(i, roles[i].ordinal(), "Ordinal should match array index");
            }
        }
    }

    @Nested
    @DisplayName("Integration Scenarios")
    class IntegrationScenarios {

        @Test
        @DisplayName("Should support switch statements")
        void testSwitchStatement() {
            for (FriendlyNPC.NPCRole role : FriendlyNPC.NPCRole.values()) {
                String roleDescription;
                switch (role) {
                    case MERCHANT:
                        roleDescription = "Sells items";
                        break;
                    case QUEST_GIVER:
                        roleDescription = "Provides quests";
                        break;
                    case GUARD:
                        roleDescription = "Town guard";
                        break;
                    case VILLAGER:
                        roleDescription = "Generic NPC";
                        break;
                    default:
                        roleDescription = null;
                        break;
                }
                assertNotNull(roleDescription, "Switch should handle all NPC roles");
            }
        }

        @Test
        @DisplayName("Should work with comparison operations")
        void testComparison() {
            assertTrue(FriendlyNPC.NPCRole.MERCHANT == FriendlyNPC.NPCRole.MERCHANT);
            assertFalse(FriendlyNPC.NPCRole.MERCHANT == FriendlyNPC.NPCRole.GUARD);
            assertTrue(FriendlyNPC.NPCRole.QUEST_GIVER.equals(FriendlyNPC.NPCRole.QUEST_GIVER));
            assertFalse(FriendlyNPC.NPCRole.QUEST_GIVER.equals(FriendlyNPC.NPCRole.VILLAGER));
        }

        @Test
        @DisplayName("Should be serializable via name")
        void testSerialization() {
            for (FriendlyNPC.NPCRole role : FriendlyNPC.NPCRole.values()) {
                String name = role.name();
                FriendlyNPC.NPCRole restored = FriendlyNPC.NPCRole.valueOf(name);
                assertEquals(role, restored, "Should restore from name");
            }
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle null checks")
        void testNullChecks() {
            FriendlyNPC.NPCRole role = FriendlyNPC.NPCRole.MERCHANT;
            assertNotEquals(null, role);
            assertFalse(role.equals(null));
        }

        @Test
        @DisplayName("Should handle toString")
        void testToString() {
            for (FriendlyNPC.NPCRole role : FriendlyNPC.NPCRole.values()) {
                String str = role.toString();
                assertNotNull(str);
                assertFalse(str.isEmpty());
                assertEquals(role.name(), str, "toString should match name");
            }
        }

        @Test
        @DisplayName("Should maintain identity")
        void testIdentity() {
            FriendlyNPC.NPCRole merchant1 = FriendlyNPC.NPCRole.MERCHANT;
            FriendlyNPC.NPCRole merchant2 = FriendlyNPC.NPCRole.valueOf("MERCHANT");
            assertSame(merchant1, merchant2, "Same enum constant should be identical");
        }

        @Test
        @DisplayName("Name should be uppercase")
        void testNameUppercase() {
            for (FriendlyNPC.NPCRole role : FriendlyNPC.NPCRole.values()) {
                String name = role.name();
                assertEquals(name.toUpperCase(), name, "Enum name should be uppercase");
            }
        }
    }

    @Nested
    @DisplayName("Use Cases")
    class UseCases {

        @Test
        @DisplayName("NPC roles should represent different NPC functions")
        void testNPCRoleFunctions() {
            // MERCHANT for shops
            assertNotNull(FriendlyNPC.NPCRole.MERCHANT);

            // QUEST_GIVER for quest NPCs
            assertNotNull(FriendlyNPC.NPCRole.QUEST_GIVER);

            // GUARD for town guards
            assertNotNull(FriendlyNPC.NPCRole.GUARD);

            // VILLAGER for generic NPCs
            assertNotNull(FriendlyNPC.NPCRole.VILLAGER);
        }

        @Test
        @DisplayName("Each NPC role should be distinguishable")
        void testDistinguishable() {
            assertNotEquals(FriendlyNPC.NPCRole.MERCHANT, FriendlyNPC.NPCRole.QUEST_GIVER);
            assertNotEquals(FriendlyNPC.NPCRole.GUARD, FriendlyNPC.NPCRole.VILLAGER);
        }
    }
}
