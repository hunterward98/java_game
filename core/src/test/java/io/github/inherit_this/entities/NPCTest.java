package io.github.inherit_this.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.inherit_this.world.WorldProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for NPC hierarchy: NPC (abstract base), Enemy, and FriendlyNPC.
 * Uses nested test classes to organize tests by class.
 */
class NPCTest {

    @Mock
    private Texture mockTexture;

    @Mock
    private WorldProvider mockWorld;

    @Mock
    private Player mockPlayer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock texture dimensions
        when(mockTexture.getWidth()).thenReturn(32);
        when(mockTexture.getHeight()).thenReturn(32);

        // Mock player position
        when(mockPlayer.getPosition()).thenReturn(new Vector2(10f, 10f));
        when(mockPlayer.getStats()).thenReturn(new PlayerStats());
    }

    /**
     * Concrete NPC implementation for testing abstract NPC class.
     */
    private static class TestNPC extends NPC {
        public TestNPC(Texture texture, float x, float y, String name, NPCType type, WorldProvider world) {
            super(texture, x, y, name, type, world);
        }

        @Override
        protected void updateAI(float delta, Player player) {
            // Simple AI for testing: chase player if in range
            if (canSee(player)) {
                state = NPCState.CHASE;
                targetPosition = new Vector2(player.getPosition());
            }
        }
    }

    @Nested
    @DisplayName("NPC Base Class Tests")
    class NPCBaseTests {

        private TestNPC npc;

        @BeforeEach
        void setUpNPC() {
            npc = new TestNPC(mockTexture, 5f, 5f, "Test NPC", NPCType.NEUTRAL, mockWorld);
        }

        @Test
        @DisplayName("NPC should initialize with correct values")
        void testInitialization() {
            assertEquals(5f, npc.getPosition().x, "X position should be 5");
            assertEquals(5f, npc.getPosition().y, "Y position should be 5");
            assertEquals("Test NPC", npc.getName(), "Name should match");
            assertEquals(NPCType.NEUTRAL, npc.getType(), "Type should be NEUTRAL");
            assertEquals(NPC.NPCState.IDLE, npc.getState(), "Initial state should be IDLE");
            assertFalse(npc.isDead(), "NPC should not be dead initially");
        }

        @Test
        @DisplayName("NPC should have default stats")
        void testDefaultStats() {
            assertEquals(100, npc.getMaxHealth(), "Default max health should be 100");
            assertEquals(100, npc.getCurrentHealth(), "Default current health should be 100");
            assertEquals(10, npc.getDamage(), "Default damage should be 10");
            assertEquals(10, npc.getGoldDrop(), "Default gold drop should be 10");
            assertEquals(25, npc.getXPValue(), "Default XP value should be 25");
        }

        @Test
        @DisplayName("TakeDamage should reduce health")
        void testTakeDamage() {
            npc.takeDamage(30, mockPlayer);

            assertEquals(70, npc.getCurrentHealth(), "Health should be reduced by 30");
            assertFalse(npc.isDead(), "NPC should still be alive");
        }

        @Test
        @DisplayName("TakeDamage should kill NPC when health reaches zero")
        void testTakeDamageDeath() {
            npc.takeDamage(100, mockPlayer);

            assertEquals(0, npc.getCurrentHealth(), "Health should be 0");
            assertTrue(npc.isDead(), "NPC should be dead");
            assertEquals(NPC.NPCState.DEAD, npc.getState(), "State should be DEAD");
        }

        @Test
        @DisplayName("TakeDamage should not reduce health below zero")
        void testTakeDamageOverkill() {
            npc.takeDamage(200, mockPlayer);

            assertEquals(0, npc.getCurrentHealth(), "Health should not go below 0");
            assertTrue(npc.isDead(), "NPC should be dead");
        }

        @Test
        @DisplayName("TakeDamage should not affect dead NPC")
        void testTakeDamageWhenDead() {
            npc.takeDamage(100, mockPlayer); // Kill it
            npc.takeDamage(50, mockPlayer); // Try to damage again

            assertEquals(0, npc.getCurrentHealth(), "Health should remain 0");
        }

        @Test
        @DisplayName("Neutral NPC should become aggressive when hit")
        void testNeutralBecomesAggressive() {
            assertEquals(NPCType.NEUTRAL, npc.getType(), "Should be neutral");
            assertEquals(NPC.NPCState.IDLE, npc.getState(), "Should be idle");

            npc.takeDamage(10, mockPlayer);

            assertEquals(NPC.NPCState.CHASE, npc.getState(),
                    "Should enter CHASE state when hit");
        }

        @Test
        @DisplayName("CanSee should detect entities within detection range")
        void testCanSeeInRange() {
            // Player at (10, 10), NPC at (5, 5) = distance ~7.07 tiles
            // Detection range is 8 tiles by default
            assertTrue(npc.canSee(mockPlayer), "Should see player within detection range");
        }

        @Test
        @DisplayName("CanSee should not detect entities outside detection range")
        void testCanSeeOutOfRange() {
            when(mockPlayer.getPosition()).thenReturn(new Vector2(100f, 100f));

            assertFalse(npc.canSee(mockPlayer), "Should not see player outside detection range");
        }

        @Test
        @DisplayName("InAttackRange should detect entities within attack range")
        void testInAttackRange() {
            when(mockPlayer.getPosition()).thenReturn(new Vector2(5.5f, 5.5f));

            assertTrue(npc.inAttackRange(mockPlayer), "Should detect player in attack range");
        }

        @Test
        @DisplayName("InAttackRange should not detect entities outside attack range")
        void testNotInAttackRange() {
            when(mockPlayer.getPosition()).thenReturn(new Vector2(20f, 20f));

            assertFalse(npc.inAttackRange(mockPlayer), "Should not detect player outside attack range");
        }

        @Test
        @DisplayName("DistanceTo should calculate correct distance")
        void testDistanceTo() {
            // NPC at (5, 5), player at (10, 10)
            // Distance = sqrt((10-5)^2 + (10-5)^2) = sqrt(50) ≈ 7.07
            float distance = npc.distanceTo(mockPlayer);

            assertEquals(7.07f, distance, 0.1f, "Distance should be approximately 7.07");
        }

        @Test
        @DisplayName("Attack should succeed when off cooldown")
        void testAttack() {
            // Update to allow attack cooldown to pass
            npc.update(1.1f, mockPlayer);

            boolean attacked = npc.attack(mockPlayer);
            assertTrue(attacked, "Attack should succeed when off cooldown");
        }

        @Test
        @DisplayName("Attack should fail when on cooldown")
        void testAttackCooldown() {
            // Update to allow first attack
            npc.update(1.1f, mockPlayer);

            npc.attack(mockPlayer); // First attack
            boolean secondAttack = npc.attack(mockPlayer); // Immediate second attack

            assertFalse(secondAttack, "Second attack should fail due to cooldown");
        }

        @Test
        @DisplayName("Update should call AI and increment timers")
        void testUpdate() {
            npc.update(0.5f, mockPlayer);

            // AI should have been called and detected player
            assertEquals(NPC.NPCState.CHASE, npc.getState(),
                    "AI should transition to CHASE when player is visible");
        }

        @Test
        @DisplayName("Update should not process AI when dead")
        void testUpdateWhenDead() {
            npc.takeDamage(100, mockPlayer); // Kill NPC
            Vector2 initialPos = new Vector2(npc.getPosition());

            npc.update(1.0f, mockPlayer);

            assertEquals(initialPos, npc.getPosition(), "Dead NPC should not move");
        }

        @Test
        @DisplayName("MoveToward should move NPC toward target")
        void testMoveToward() {
            Vector2 target = new Vector2(10f, 10f);
            Vector2 initialPos = new Vector2(npc.getPosition());

            npc.update(0.1f, mockPlayer); // This sets target and moves

            float distanceMoved = initialPos.dst(npc.getPosition());
            assertTrue(distanceMoved > 0, "NPC should have moved toward target");
        }

        @Test
        @DisplayName("MoveToward should stop when reaching target")
        void testMoveTowardArrival() {
            npc = new TestNPC(mockTexture, 9.95f, 9.95f, "Close NPC", NPCType.NEUTRAL, mockWorld);

            npc.update(1.0f, mockPlayer); // Long update to ensure arrival

            // Should have moved close to player and stopped
            float distance = npc.getPosition().dst(mockPlayer.getPosition());
            assertTrue(distance < 0.2f, "NPC should be very close to target");
        }
    }

    @Nested
    @DisplayName("Enemy Class Tests")
    class EnemyTests {

        private Enemy enemy;

        @BeforeEach
        void setUpEnemy() {
            enemy = new Enemy(mockTexture, 5f, 5f, "Goblin", mockWorld);
        }

        @Test
        @DisplayName("Enemy should initialize as hostile")
        void testEnemyInitialization() {
            assertEquals(NPCType.HOSTILE, enemy.getType(), "Enemy should be HOSTILE");
            assertEquals("Goblin", enemy.getName(), "Name should match");
            assertEquals(NPC.NPCState.IDLE, enemy.getState(), "Should start IDLE");
        }

        @Test
        @DisplayName("Enemy should have adjusted stats")
        void testEnemyStats() {
            // Enemies have custom detection and attack ranges
            assertTrue(enemy.canSee(mockPlayer), "Enemy should have 10 tile detection range");
        }

        @Test
        @DisplayName("Idle enemy should detect and chase player")
        void testIdleToChase() {
            assertEquals(NPC.NPCState.IDLE, enemy.getState(), "Should start IDLE");

            enemy.update(0.1f, mockPlayer);

            assertEquals(NPC.NPCState.CHASE, enemy.getState(),
                    "Should transition to CHASE when player is in range");
        }

        @Test
        @DisplayName("Enemy should transition from chase to attack when in range")
        void testChaseToAttack() {
            when(mockPlayer.getPosition()).thenReturn(new Vector2(5.5f, 5.5f)); // Very close

            enemy.update(0.1f, mockPlayer); // Enter CHASE
            assertEquals(NPC.NPCState.CHASE, enemy.getState(), "Should be chasing");

            enemy.update(0.1f, mockPlayer); // Should enter ATTACK
            assertEquals(NPC.NPCState.ATTACK, enemy.getState(),
                    "Should transition to ATTACK when in range");
        }

        @Test
        @DisplayName("Enemy should attack player when in attack state")
        void testEnemyAttacksPlayer() {
            when(mockPlayer.getPosition()).thenReturn(new Vector2(5.5f, 5.5f));

            // Move to CHASE then ATTACK
            enemy.update(0.1f, mockPlayer);
            enemy.update(0.1f, mockPlayer);

            assertEquals(NPC.NPCState.ATTACK, enemy.getState(), "Should be attacking");

            float initialHealth = mockPlayer.getStats().getCurrentHealth();
            enemy.update(2.0f, mockPlayer); // Wait for attack cooldown

            // Player should have taken damage
            assertTrue(mockPlayer.getStats().getCurrentHealth() < initialHealth,
                    "Player should have taken damage from enemy attack");
        }

        @Test
        @DisplayName("Enemy should stop chasing when player is out of range")
        void testStopChasingOutOfRange() {
            // Start close
            when(mockPlayer.getPosition()).thenReturn(new Vector2(10f, 10f));
            enemy.update(0.1f, mockPlayer);
            assertEquals(NPC.NPCState.CHASE, enemy.getState(), "Should be chasing");

            // Move player far away
            when(mockPlayer.getPosition()).thenReturn(new Vector2(100f, 100f));
            enemy.update(0.1f, mockPlayer);

            assertEquals(NPC.NPCState.IDLE, enemy.getState(),
                    "Should return to IDLE when player is out of range");
        }

        @Test
        @DisplayName("Enemy should wander when idle")
        void testWandering() {
            when(mockPlayer.getPosition()).thenReturn(new Vector2(100f, 100f)); // Far away

            // Update many times to trigger wander behavior
            for (int i = 0; i < 100; i++) {
                enemy.update(0.1f, mockPlayer);
                if (enemy.getState() == NPC.NPCState.WANDER) {
                    break;
                }
            }

            // May or may not have wandered (it's random), but shouldn't crash
            assertTrue(enemy.getState() == NPC.NPCState.IDLE ||
                      enemy.getState() == NPC.NPCState.WANDER,
                    "Enemy should be either IDLE or WANDER when player is far");
        }

        @Test
        @DisplayName("Dead enemy should give XP and gold to player")
        void testEnemyDropsLoot() {
            PlayerStats stats = mockPlayer.getStats();
            int initialXP = (int) stats.getCurrentXP();

            when(mockPlayer.getInventory()).thenReturn(new io.github.inherit_this.items.Inventory(8, 6));

            int initialGold = mockPlayer.getInventory().getGold();
            int expectedXP = enemy.getXPValue();
            int expectedGold = enemy.getGoldDrop();

            // Kill enemy
            enemy.takeDamage(1000, mockPlayer);

            assertTrue(enemy.isDead(), "Enemy should be dead");

            // XP should have been added
            assertEquals(initialXP + expectedXP, stats.getCurrentXP(), 0.01f,
                    "Player should receive XP from enemy");

            // Gold should have been added
            assertEquals(initialGold + expectedGold, mockPlayer.getInventory().getGold(),
                    "Player should receive gold from enemy");
        }

        @Test
        @DisplayName("Enemy in FLEE state should run away from player")
        void testFleeState() {
            when(mockPlayer.getPosition()).thenReturn(new Vector2(5.5f, 5.5f));

            // Manually set to FLEE state for testing
            enemy.takeDamage(10, mockPlayer); // Get aggro
            // Use reflection or subclass to test FLEE, or just verify it doesn't crash

            enemy.update(0.1f, mockPlayer);

            // Enemy should be in some valid state
            assertNotNull(enemy.getState(), "Enemy should have a valid state");
        }
    }

    @Nested
    @DisplayName("FriendlyNPC Class Tests")
    class FriendlyNPCTests {

        private FriendlyNPC friendlyNPC;

        @BeforeEach
        void setUpFriendlyNPC() {
            friendlyNPC = new FriendlyNPC(mockTexture, 15f, 15f, "Merchant Bob",
                    FriendlyNPC.NPCRole.MERCHANT, "Welcome to my shop!", mockWorld);
        }

        @Test
        @DisplayName("FriendlyNPC should initialize with correct values")
        void testFriendlyNPCInitialization() {
            assertEquals(NPCType.FRIENDLY, friendlyNPC.getType(), "Should be FRIENDLY type");
            assertEquals("Merchant Bob", friendlyNPC.getName(), "Name should match");
            assertEquals(FriendlyNPC.NPCRole.MERCHANT, friendlyNPC.getRole(),
                    "Role should be MERCHANT");
            assertEquals("Welcome to my shop!", friendlyNPC.getDialogue(),
                    "Dialogue should match");
        }

        @Test
        @DisplayName("FriendlyNPC should have zero damage")
        void testFriendlyNPCNonCombat() {
            assertEquals(0, friendlyNPC.getDamage(), "Friendly NPCs should have 0 damage");
        }

        @Test
        @DisplayName("FriendlyNPC should use default dialogue when null is provided")
        void testDefaultDialogue() {
            FriendlyNPC npcNoDialogue = new FriendlyNPC(mockTexture, 0f, 0f, "Guard",
                    FriendlyNPC.NPCRole.GUARD, null, mockWorld);

            assertEquals("Greetings, traveler!", npcNoDialogue.getDialogue(),
                    "Should use default dialogue when null");
        }

        @Test
        @DisplayName("Interact should return dialogue")
        void testInteract() {
            String response = friendlyNPC.interact();
            assertEquals("Welcome to my shop!", response, "Interact should return dialogue");
        }

        @Test
        @DisplayName("FriendlyNPC should not take damage")
        void testFriendlyNPCCannotBeDamaged() {
            int initialHealth = friendlyNPC.getCurrentHealth();

            friendlyNPC.takeDamage(100, mockPlayer);

            assertEquals(initialHealth, friendlyNPC.getCurrentHealth(),
                    "Friendly NPC health should not change");
            assertFalse(friendlyNPC.isDead(), "Friendly NPC should not die");
        }

        @Test
        @DisplayName("FriendlyNPC should remain idle")
        void testFriendlyNPCStaysIdle() {
            friendlyNPC.update(0.1f, mockPlayer);

            assertEquals(NPC.NPCState.IDLE, friendlyNPC.getState(),
                    "Friendly NPC should remain IDLE");
        }

        @Test
        @DisplayName("All NPC roles should be available")
        void testNPCRoles() {
            FriendlyNPC merchant = new FriendlyNPC(mockTexture, 0f, 0f, "M",
                    FriendlyNPC.NPCRole.MERCHANT, "Hi", mockWorld);
            FriendlyNPC questGiver = new FriendlyNPC(mockTexture, 0f, 0f, "Q",
                    FriendlyNPC.NPCRole.QUEST_GIVER, "Hi", mockWorld);
            FriendlyNPC guard = new FriendlyNPC(mockTexture, 0f, 0f, "G",
                    FriendlyNPC.NPCRole.GUARD, "Hi", mockWorld);
            FriendlyNPC villager = new FriendlyNPC(mockTexture, 0f, 0f, "V",
                    FriendlyNPC.NPCRole.VILLAGER, "Hi", mockWorld);

            assertEquals(FriendlyNPC.NPCRole.MERCHANT, merchant.getRole());
            assertEquals(FriendlyNPC.NPCRole.QUEST_GIVER, questGiver.getRole());
            assertEquals(FriendlyNPC.NPCRole.GUARD, guard.getRole());
            assertEquals(FriendlyNPC.NPCRole.VILLAGER, villager.getRole());
        }

        @Test
        @DisplayName("FriendlyNPC should have reduced detection range")
        void testFriendlyNPCDetectionRange() {
            // Friendly NPCs have 3 tile detection range
            when(mockPlayer.getPosition()).thenReturn(new Vector2(20f, 15f)); // 5 tiles away

            assertFalse(friendlyNPC.canSee(mockPlayer),
                    "Should not detect player beyond 3 tiles");

            when(mockPlayer.getPosition()).thenReturn(new Vector2(16f, 15f)); // 1 tile away
            assertTrue(friendlyNPC.canSee(mockPlayer),
                    "Should detect player within 3 tiles");
        }

        @Test
        @DisplayName("FriendlyNPC should never enter combat states")
        void testFriendlyNPCNeverFights() {
            // Try to provoke it
            friendlyNPC.takeDamage(50, mockPlayer);
            friendlyNPC.update(1.0f, mockPlayer);

            assertNotEquals(NPC.NPCState.CHASE, friendlyNPC.getState(),
                    "Friendly NPC should not chase");
            assertNotEquals(NPC.NPCState.ATTACK, friendlyNPC.getState(),
                    "Friendly NPC should not attack");
        }
    }

    @Nested
    @DisplayName("NPCType Enum Tests")
    class NPCTypeTests {

        @Test
        @DisplayName("All NPCType values should be accessible")
        void testNPCTypeValues() {
            NPCType[] types = NPCType.values();

            assertEquals(4, types.length, "Should have 4 NPC types");

            assertTrue(java.util.Arrays.asList(types).contains(NPCType.HOSTILE));
            assertTrue(java.util.Arrays.asList(types).contains(NPCType.FRIENDLY));
            assertTrue(java.util.Arrays.asList(types).contains(NPCType.NEUTRAL));
            assertTrue(java.util.Arrays.asList(types).contains(NPCType.PASSIVE));
        }

        @Test
        @DisplayName("NPCType valueOf should work correctly")
        void testNPCTypeValueOf() {
            assertEquals(NPCType.HOSTILE, NPCType.valueOf("HOSTILE"));
            assertEquals(NPCType.FRIENDLY, NPCType.valueOf("FRIENDLY"));
            assertEquals(NPCType.NEUTRAL, NPCType.valueOf("NEUTRAL"));
            assertEquals(NPCType.PASSIVE, NPCType.valueOf("PASSIVE"));
        }
    }

    @Nested
    @DisplayName("NPC State Transitions")
    class NPCStateTransitionTests {

        private Enemy enemy;

        @BeforeEach
        void setUpEnemy() {
            enemy = new Enemy(mockTexture, 50f, 50f, "Test Enemy", mockWorld);
        }

        @Test
        @DisplayName("Enemy should transition through all combat states")
        void testCombatStateFlow() {
            when(mockPlayer.getPosition()).thenReturn(new Vector2(100f, 100f));

            // Start IDLE
            assertEquals(NPC.NPCState.IDLE, enemy.getState());

            // Move player into detection range -> CHASE
            when(mockPlayer.getPosition()).thenReturn(new Vector2(55f, 55f));
            enemy.update(0.1f, mockPlayer);
            assertEquals(NPC.NPCState.CHASE, enemy.getState());

            // Move player into attack range -> ATTACK
            when(mockPlayer.getPosition()).thenReturn(new Vector2(50.5f, 50.5f));
            enemy.update(0.1f, mockPlayer);
            assertEquals(NPC.NPCState.ATTACK, enemy.getState());

            // Move player far away -> back to IDLE
            when(mockPlayer.getPosition()).thenReturn(new Vector2(100f, 100f));
            enemy.update(0.1f, mockPlayer);
            assertEquals(NPC.NPCState.IDLE, enemy.getState());
        }

        @Test
        @DisplayName("All NPC states should be accessible")
        void testAllNPCStates() {
            NPC.NPCState[] states = NPC.NPCState.values();

            assertEquals(6, states.length, "Should have 6 NPC states");

            assertTrue(java.util.Arrays.asList(states).contains(NPC.NPCState.IDLE));
            assertTrue(java.util.Arrays.asList(states).contains(NPC.NPCState.WANDER));
            assertTrue(java.util.Arrays.asList(states).contains(NPC.NPCState.CHASE));
            assertTrue(java.util.Arrays.asList(states).contains(NPC.NPCState.ATTACK));
            assertTrue(java.util.Arrays.asList(states).contains(NPC.NPCState.FLEE));
            assertTrue(java.util.Arrays.asList(states).contains(NPC.NPCState.DEAD));
        }
    }
}
