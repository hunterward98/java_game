package io.github.inherit_this.entities;

import com.badlogic.gdx.graphics.Texture;
import io.github.inherit_this.LibGdxTestBase;
import io.github.inherit_this.world.WorldProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive tests for FriendlyNPC - non-hostile NPC system
 */
@DisplayName("FriendlyNPC Tests")
class FriendlyNPCTest extends LibGdxTestBase {

    @Mock
    private Texture mockTexture;

    @Mock
    private WorldProvider mockWorld;

    @Mock
    private Player mockPlayer;

    private FriendlyNPC npc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        npc = new FriendlyNPC(mockTexture, 100f, 100f, "Friendly Bob",
            FriendlyNPC.NPCRole.VILLAGER, "Hello, traveler!", mockWorld);
    }

    // ==================== Initialization Tests ====================

    @Nested
    @DisplayName("Initialization")
    class Initialization {

        @Test
        @DisplayName("FriendlyNPC should initialize with correct properties")
        void testInitialization() {
            assertNotNull(npc, "NPC should not be null");
            assertEquals("Friendly Bob", npc.getName(), "Name should match");
            assertEquals(NPCType.FRIENDLY, npc.getType(), "Should be friendly type");
            assertEquals(FriendlyNPC.NPCRole.VILLAGER, npc.getRole(), "Role should match");
        }

        @Test
        @DisplayName("FriendlyNPC should initialize with dialogue")
        void testDialogueInitialization() {
            assertEquals("Hello, traveler!", npc.getDialogue(),
                "Dialogue should match");
        }

        @Test
        @DisplayName("FriendlyNPC should use default dialogue when null provided")
        void testDefaultDialogue() {
            FriendlyNPC npcWithNullDialogue = new FriendlyNPC(
                mockTexture, 0f, 0f, "Bob", FriendlyNPC.NPCRole.VILLAGER, null, mockWorld
            );

            assertNotNull(npcWithNullDialogue.getDialogue(),
                "Should have default dialogue");
            assertTrue(npcWithNullDialogue.getDialogue().length() > 0,
                "Default dialogue should not be empty");
        }

        @Test
        @DisplayName("FriendlyNPC should accept empty dialogue")
        void testEmptyDialogue() {
            FriendlyNPC npcWithEmptyDialogue = new FriendlyNPC(
                mockTexture, 0f, 0f, "Bob", FriendlyNPC.NPCRole.VILLAGER, "", mockWorld
            );

            assertEquals("", npcWithEmptyDialogue.getDialogue(),
                "Should preserve empty dialogue");
        }
    }

    // ==================== Role Tests ====================

    @Nested
    @DisplayName("NPC Roles")
    class NPCRoles {

        @Test
        @DisplayName("Should support MERCHANT role")
        void testMerchantRole() {
            FriendlyNPC merchant = new FriendlyNPC(
                mockTexture, 0f, 0f, "Merchant", FriendlyNPC.NPCRole.MERCHANT,
                "Want to buy something?", mockWorld
            );

            assertEquals(FriendlyNPC.NPCRole.MERCHANT, merchant.getRole());
        }

        @Test
        @DisplayName("Should support QUEST_GIVER role")
        void testQuestGiverRole() {
            FriendlyNPC questGiver = new FriendlyNPC(
                mockTexture, 0f, 0f, "Quest Giver", FriendlyNPC.NPCRole.QUEST_GIVER,
                "I have a quest for you!", mockWorld
            );

            assertEquals(FriendlyNPC.NPCRole.QUEST_GIVER, questGiver.getRole());
        }

        @Test
        @DisplayName("Should support GUARD role")
        void testGuardRole() {
            FriendlyNPC guard = new FriendlyNPC(
                mockTexture, 0f, 0f, "Guard", FriendlyNPC.NPCRole.GUARD,
                "Halt!", mockWorld
            );

            assertEquals(FriendlyNPC.NPCRole.GUARD, guard.getRole());
        }

        @Test
        @DisplayName("Should support VILLAGER role")
        void testVillagerRole() {
            FriendlyNPC villager = new FriendlyNPC(
                mockTexture, 0f, 0f, "Villager", FriendlyNPC.NPCRole.VILLAGER,
                "Nice weather today.", mockWorld
            );

            assertEquals(FriendlyNPC.NPCRole.VILLAGER, villager.getRole());
        }
    }

    // ==================== Interaction Tests ====================

    @Nested
    @DisplayName("Interaction System")
    class InteractionSystem {

        @Test
        @DisplayName("interact should return dialogue")
        void testInteract() {
            String dialogue = npc.interact();

            assertNotNull(dialogue, "Dialogue should not be null");
            assertEquals("Hello, traveler!", dialogue, "Should return configured dialogue");
        }

        @Test
        @DisplayName("interact should be callable multiple times")
        void testInteractMultipleTimes() {
            String dialogue1 = npc.interact();
            String dialogue2 = npc.interact();
            String dialogue3 = npc.interact();

            assertEquals(dialogue1, dialogue2, "Dialogue should be consistent");
            assertEquals(dialogue2, dialogue3, "Dialogue should be consistent");
        }

        @Test
        @DisplayName("Different NPCs should have different dialogue")
        void testDifferentDialogue() {
            FriendlyNPC npc2 = new FriendlyNPC(
                mockTexture, 0f, 0f, "Other NPC", FriendlyNPC.NPCRole.VILLAGER,
                "Different dialogue", mockWorld
            );

            assertNotEquals(npc.interact(), npc2.interact(),
                "Different NPCs should have different dialogue");
        }
    }

    // ==================== Damage Handling Tests ====================

    @Nested
    @DisplayName("Damage Handling")
    class DamageHandling {

        @Test
        @DisplayName("takeDamage should not crash")
        void testTakeDamage() {
            assertDoesNotThrow(() -> npc.takeDamage(10, mockPlayer),
                "takeDamage should not crash");
        }

        @Test
        @DisplayName("takeDamage should not kill friendly NPC")
        void testTakeDamageDoesNotKill() {
            int initialHealth = npc.getCurrentHealth();

            npc.takeDamage(100, mockPlayer);

            assertEquals(initialHealth, npc.getCurrentHealth(),
                "Friendly NPCs should not take damage");
            assertNotEquals(NPC.NPCState.DEAD, npc.getState(),
                "Friendly NPCs should not die");
        }

        @Test
        @DisplayName("takeDamage with massive damage should not kill")
        void testMassiveDamage() {
            npc.takeDamage(99999, mockPlayer);

            assertTrue(npc.getCurrentHealth() > 0,
                "Friendly NPC should survive massive damage");
        }

        @Test
        @DisplayName("takeDamage with null attacker should not crash")
        void testTakeDamageNullAttacker() {
            assertDoesNotThrow(() -> npc.takeDamage(10, null),
                "Should handle null attacker");
        }
    }

    // ==================== Update Tests ====================

    @Nested
    @DisplayName("Update System")
    class UpdateSystem {

        @BeforeEach
        void setUpMocks() {
            when(mockPlayer.getPosition()).thenReturn(new com.badlogic.gdx.math.Vector2(100f, 100f));
        }

        @Test
        @DisplayName("update should not crash")
        void testUpdate() {
            assertDoesNotThrow(() -> npc.update(0.016f, mockPlayer),
                "Update should not crash");
        }

        @Test
        @DisplayName("update should handle zero delta")
        void testUpdateZeroDelta() {
            assertDoesNotThrow(() -> npc.update(0f, mockPlayer),
                "Should handle zero delta");
        }

        @Test
        @DisplayName("update should handle large delta")
        void testUpdateLargeDelta() {
            assertDoesNotThrow(() -> npc.update(10f, mockPlayer),
                "Should handle large delta");
        }

        @Test
        @DisplayName("update should be callable multiple times")
        void testUpdateMultipleTimes() {
            assertDoesNotThrow(() -> {
                for (int i = 0; i < 100; i++) {
                    npc.update(0.016f, mockPlayer);
                }
            }, "Should handle multiple updates");
        }

        @Test
        @DisplayName("Friendly NPC should remain in idle or wander state")
        void testFriendlyStates() {
            for (int i = 0; i < 10; i++) {
                npc.update(0.016f, mockPlayer);
            }

            NPC.NPCState state = npc.getState();
            assertTrue(
                state == NPC.NPCState.IDLE || state == NPC.NPCState.WANDER,
                "Friendly NPCs should only be idle or wandering"
            );
        }
    }

    // ==================== State Management Tests ====================

    @Nested
    @DisplayName("State Management")
    class StateManagement {

        @Test
        @DisplayName("Should start in idle state")
        void testInitialState() {
            assertEquals(NPC.NPCState.IDLE, npc.getState(),
                "Should start in idle state");
        }

        @Test
        @DisplayName("getType should return FRIENDLY")
        void testGetType() {
            assertEquals(NPCType.FRIENDLY, npc.getType(),
                "Should be friendly type");
        }

        @Test
        @DisplayName("getName should return name")
        void testGetName() {
            assertEquals("Friendly Bob", npc.getName(),
                "Name should match");
        }
    }

    // ==================== NPCRole Enum Tests ====================

    @Nested
    @DisplayName("NPCRole Enum")
    class NPCRoleEnum {

        @Test
        @DisplayName("All NPCRole values should be accessible")
        void testAllRoles() {
            FriendlyNPC.NPCRole[] roles = FriendlyNPC.NPCRole.values();

            assertEquals(4, roles.length, "Should have 4 NPC roles");
            assertTrue(containsRole(roles, FriendlyNPC.NPCRole.MERCHANT));
            assertTrue(containsRole(roles, FriendlyNPC.NPCRole.QUEST_GIVER));
            assertTrue(containsRole(roles, FriendlyNPC.NPCRole.GUARD));
            assertTrue(containsRole(roles, FriendlyNPC.NPCRole.VILLAGER));
        }

        private boolean containsRole(FriendlyNPC.NPCRole[] roles, FriendlyNPC.NPCRole target) {
            for (FriendlyNPC.NPCRole role : roles) {
                if (role == target) return true;
            }
            return false;
        }
    }

    // ==================== Integration Tests ====================

    @Nested
    @DisplayName("Integration Scenarios")
    class IntegrationScenarios {

        @BeforeEach
        void setUpMocks() {
            when(mockPlayer.getPosition()).thenReturn(new com.badlogic.gdx.math.Vector2(100f, 100f));
        }

        @Test
        @DisplayName("Should handle complete NPC lifecycle")
        void testCompleteLifecycle() {
            // Initial state
            assertEquals(NPC.NPCState.IDLE, npc.getState());
            assertEquals(NPCType.FRIENDLY, npc.getType());

            // Interact
            String dialogue = npc.interact();
            assertNotNull(dialogue);

            // Update
            npc.update(0.016f, mockPlayer);

            // Take damage (should not affect friendly NPC)
            int initialHealth = npc.getCurrentHealth();
            npc.takeDamage(50, mockPlayer);
            assertEquals(initialHealth, npc.getCurrentHealth());

            // Continue updating
            for (int i = 0; i < 10; i++) {
                npc.update(0.016f, mockPlayer);
            }

            // Should still be alive and functional
            assertTrue(npc.getCurrentHealth() > 0);
            assertNotNull(npc.interact());
        }

        @Test
        @DisplayName("Should handle different roles appropriately")
        void testDifferentRoles() {
            FriendlyNPC merchant = new FriendlyNPC(
                mockTexture, 0f, 0f, "Merchant", FriendlyNPC.NPCRole.MERCHANT,
                "Buy something", mockWorld
            );

            FriendlyNPC guard = new FriendlyNPC(
                mockTexture, 100f, 100f, "Guard", FriendlyNPC.NPCRole.GUARD,
                "Move along", mockWorld
            );

            // Both should be friendly type
            assertEquals(NPCType.FRIENDLY, merchant.getType());
            assertEquals(NPCType.FRIENDLY, guard.getType());

            // But have different roles
            assertNotEquals(merchant.getRole(), guard.getRole());

            // And different dialogues
            assertNotEquals(merchant.interact(), guard.interact());
        }
    }

    // ==================== Edge Cases ====================

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle very long dialogue")
        void testLongDialogue() {
            String longDialogue = "A".repeat(10000);
            FriendlyNPC npcWithLongDialogue = new FriendlyNPC(
                mockTexture, 0f, 0f, "Talker", FriendlyNPC.NPCRole.VILLAGER,
                longDialogue, mockWorld
            );

            assertEquals(longDialogue, npcWithLongDialogue.getDialogue());
            assertEquals(longDialogue, npcWithLongDialogue.interact());
        }

        @Test
        @DisplayName("Should handle special characters in dialogue")
        void testSpecialCharacters() {
            String specialDialogue = "Hello! @#$%^&*() <>\n\t";
            FriendlyNPC npcWithSpecial = new FriendlyNPC(
                mockTexture, 0f, 0f, "Special", FriendlyNPC.NPCRole.VILLAGER,
                specialDialogue, mockWorld
            );

            assertEquals(specialDialogue, npcWithSpecial.getDialogue());
        }

        @Test
        @DisplayName("Should handle repeated damage")
        void testRepeatedDamage() {
            int initialHealth = npc.getCurrentHealth();

            for (int i = 0; i < 100; i++) {
                npc.takeDamage(10, mockPlayer);
            }

            assertEquals(initialHealth, npc.getCurrentHealth(),
                "Health should not change despite repeated damage");
        }

        @Test
        @DisplayName("Should handle extreme positions")
        void testExtremePositions() {
            FriendlyNPC farNPC = new FriendlyNPC(
                mockTexture, 999999f, -999999f, "Far NPC",
                FriendlyNPC.NPCRole.VILLAGER, "Hello", mockWorld
            );

            assertNotNull(farNPC.getPosition());
            assertDoesNotThrow(() -> farNPC.interact());
        }
    }
}
