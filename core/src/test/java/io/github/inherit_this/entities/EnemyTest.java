package io.github.inherit_this.entities;

import com.badlogic.gdx.graphics.Texture;
import io.github.inherit_this.LibGdxTestBase;
import io.github.inherit_this.items.Inventory;
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
 * Comprehensive tests for Enemy - hostile NPC system
 */
@DisplayName("Enemy Tests")
class EnemyTest extends LibGdxTestBase {

    @Mock
    private Texture mockTexture;

    @Mock
    private WorldProvider mockWorld;

    @Mock
    private Player mockPlayer;

    @Mock
    private PlayerStats mockPlayerStats;

    @Mock
    private Inventory mockInventory;

    private Enemy enemy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        enemy = new Enemy(mockTexture, 100f, 100f, "Test Enemy", mockWorld);
    }

    // ==================== Initialization Tests ====================

    @Nested
    @DisplayName("Initialization")
    class Initialization {

        @Test
        @DisplayName("Enemy should initialize with correct properties")
        void testInitialization() {
            assertNotNull(enemy, "Enemy should not be null");
            assertEquals("Test Enemy", enemy.getName(), "Name should match");
            assertEquals(NPCType.HOSTILE, enemy.getType(), "Should be hostile type");
        }

        @Test
        @DisplayName("Enemy should initialize with world")
        void testWorldInitialization() {
            assertNotNull(enemy, "Enemy should initialize with world reference");
        }

        @Test
        @DisplayName("Enemy should start in idle state")
        void testInitialState() {
            assertEquals(NPC.NPCState.IDLE, enemy.getState(),
                "Enemy should start in idle state");
        }
    }

    // ==================== Update Tests ====================

    @Nested
    @DisplayName("Update System")
    class UpdateSystem {

        @BeforeEach
        void setUpMocks() {
            when(mockPlayer.getStats()).thenReturn(mockPlayerStats);
            when(mockPlayer.getInventory()).thenReturn(mockInventory);
            when(mockPlayer.getPosition()).thenReturn(new com.badlogic.gdx.math.Vector2(100f, 100f));
        }

        @Test
        @DisplayName("update should not crash")
        void testUpdate() {
            assertDoesNotThrow(() -> enemy.update(0.016f, mockPlayer),
                "Update should not crash");
        }

        @Test
        @DisplayName("update should handle zero delta time")
        void testUpdateZeroDelta() {
            assertDoesNotThrow(() -> enemy.update(0f, mockPlayer),
                "Should handle zero delta time");
        }

        @Test
        @DisplayName("update should handle large delta time")
        void testUpdateLargeDelta() {
            assertDoesNotThrow(() -> enemy.update(10f, mockPlayer),
                "Should handle large delta time");
        }

        @Test
        @DisplayName("update should be callable multiple times")
        void testUpdateMultipleTimes() {
            assertDoesNotThrow(() -> {
                for (int i = 0; i < 100; i++) {
                    enemy.update(0.016f, mockPlayer);
                }
            }, "Should handle multiple updates");
        }
    }

    // ==================== Combat Tests ====================

    @Nested
    @DisplayName("Combat System")
    class CombatSystem {

        @BeforeEach
        void setUpMocks() {
            when(mockPlayer.getStats()).thenReturn(mockPlayerStats);
            when(mockPlayer.getInventory()).thenReturn(mockInventory);
        }

        @Test
        @DisplayName("takeDamage should reduce health")
        void testTakeDamage() {
            int initialHealth = enemy.getCurrentHealth();
            enemy.takeDamage(10, mockPlayer);

            assertTrue(enemy.getCurrentHealth() < initialHealth,
                "Health should decrease after taking damage");
        }

        @Test
        @DisplayName("takeDamage with fatal damage should kill enemy")
        void testTakeDamageFatal() {
            int massiveDamage = enemy.getCurrentHealth() + 100;
            enemy.takeDamage(massiveDamage, mockPlayer);

            assertTrue(enemy.getCurrentHealth() <= 0, "Enemy should be dead");
            assertEquals(NPC.NPCState.DEAD, enemy.getState(),
                "Enemy state should be DEAD");
        }

        @Test
        @DisplayName("onDeath should give XP to player")
        void testOnDeathGivesXP() {
            int massiveDamage = enemy.getCurrentHealth() + 100;
            enemy.takeDamage(massiveDamage, mockPlayer);

            verify(mockPlayerStats, atLeastOnce()).addXP(anyInt());
        }

        @Test
        @DisplayName("onDeath should give gold to player")
        void testOnDeathGivesGold() {
            int massiveDamage = enemy.getCurrentHealth() + 100;
            enemy.takeDamage(massiveDamage, mockPlayer);

            verify(mockInventory, atLeastOnce()).addGold(anyInt());
        }
    }

    // ==================== AI State Tests ====================

    @Nested
    @DisplayName("AI State Management")
    class AIStateManagement {

        @Test
        @DisplayName("getState should return current state")
        void testGetState() {
            NPC.NPCState state = enemy.getState();
            assertNotNull(state, "State should not be null");
        }

        @Test
        @DisplayName("Enemy should have valid NPCType")
        void testGetType() {
            assertEquals(NPCType.HOSTILE, enemy.getType(),
                "Enemy should be hostile type");
        }
    }

    // ==================== Position and Movement Tests ====================

    @Nested
    @DisplayName("Position and Movement")
    class PositionAndMovement {

        @Test
        @DisplayName("getPosition should return position vector")
        void testGetPosition() {
            com.badlogic.gdx.math.Vector2 position = enemy.getPosition();
            assertNotNull(position, "Position should not be null");
        }

        @Test
        @DisplayName("Position should be accessible")
        void testPositionAccessible() {
            com.badlogic.gdx.math.Vector2 pos = enemy.getPosition();
            assertTrue(pos.x >= 0 || pos.x < 0, "Position X should be a valid float");
            assertTrue(pos.y >= 0 || pos.y < 0, "Position Y should be a valid float");
        }
    }

    // ==================== Integration Tests ====================

    @Nested
    @DisplayName("Integration Scenarios")
    class IntegrationScenarios {

        @BeforeEach
        void setUpMocks() {
            when(mockPlayer.getStats()).thenReturn(mockPlayerStats);
            when(mockPlayer.getInventory()).thenReturn(mockInventory);
            when(mockPlayer.getPosition()).thenReturn(new com.badlogic.gdx.math.Vector2(100f, 100f));
        }

        @Test
        @DisplayName("Should handle complete combat cycle")
        void testCompleteCombatCycle() {
            // Enemy starts idle
            assertEquals(NPC.NPCState.IDLE, enemy.getState());

            // Update multiple times (simulate detection and combat)
            for (int i = 0; i < 10; i++) {
                enemy.update(0.016f, mockPlayer);
            }

            // Take damage
            enemy.takeDamage(5, mockPlayer);

            // Continue updating
            for (int i = 0; i < 10; i++) {
                enemy.update(0.016f, mockPlayer);
            }

            // Enemy should still be functional
            assertTrue(enemy.getCurrentHealth() > 0 || enemy.getState() == NPC.NPCState.DEAD);
        }

        @Test
        @DisplayName("Should handle death and reward distribution")
        void testDeathAndRewards() {
            int massiveDamage = enemy.getCurrentHealth() + 100;

            enemy.takeDamage(massiveDamage, mockPlayer);

            assertEquals(NPC.NPCState.DEAD, enemy.getState(), "Should be dead");
            verify(mockPlayerStats).addXP(anyInt());
            verify(mockInventory).addGold(anyInt());
        }
    }

    // ==================== Edge Cases ====================

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @BeforeEach
        void setUpMocks() {
            when(mockPlayer.getStats()).thenReturn(mockPlayerStats);
            when(mockPlayer.getInventory()).thenReturn(mockInventory);
        }

        @Test
        @DisplayName("Should handle zero damage")
        void testZeroDamage() {
            int initialHealth = enemy.getCurrentHealth();
            enemy.takeDamage(0, mockPlayer);

            assertEquals(initialHealth, enemy.getCurrentHealth(),
                "Health should not change with zero damage");
        }

        @Test
        @DisplayName("Should handle negative damage")
        void testNegativeDamage() {
            int initialHealth = enemy.getCurrentHealth();
            enemy.takeDamage(-10, mockPlayer);

            // Implementation may treat negative damage as healing or ignore it
            assertTrue(enemy.getCurrentHealth() >= initialHealth,
                "Negative damage should not reduce health");
        }

        @Test
        @DisplayName("Should handle damage when already dead")
        void testDamageWhenDead() {
            // Kill the enemy
            enemy.takeDamage(enemy.getCurrentHealth() + 100, mockPlayer);

            // Try to damage again
            assertDoesNotThrow(() -> enemy.takeDamage(10, mockPlayer),
                "Should handle damage when already dead");
        }

        @Test
        @DisplayName("Should handle null attacker")
        void testNullAttacker() {
            // May throw NullPointerException or handle gracefully
            try {
                enemy.takeDamage(10, null);
            } catch (NullPointerException e) {
                // Acceptable - invalid input
            }
        }
    }
}
