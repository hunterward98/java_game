package io.github.inherit_this.entities;

import com.badlogic.gdx.graphics.Texture;
import io.github.inherit_this.items.Equipment;
import io.github.inherit_this.items.Inventory;
import io.github.inherit_this.world.WorldProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for Player class - movement, combat, inventory, and equipment.
 */
class PlayerTest {

    @Mock
    private Texture mockTexture;

    @Mock
    private WorldProvider mockWorld;

    @Mock
    private NPC mockEnemy;

    private Player player;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock texture dimensions
        when(mockTexture.getWidth()).thenReturn(32);
        when(mockTexture.getHeight()).thenReturn(32);

        // Create player at position (5, 5)
        player = new Player(5f, 5f, mockTexture, mockWorld);
    }

    @Test
    @DisplayName("Player should initialize at correct position with default stats")
    void testInitialization() {
        assertEquals(5f, player.getPosition().x, "Initial X position should be 5");
        assertEquals(5f, player.getPosition().y, "Initial Y position should be 5");
        assertNotNull(player.getInventory(), "Inventory should be initialized");
        assertNotNull(player.getEquipment(), "Equipment should be initialized");
        assertNotNull(player.getStats(), "Stats should be initialized");
        assertFalse(player.isNoClip(), "NoClip should be disabled by default");
    }

    @Test
    @DisplayName("Player should have correct inventory dimensions")
    void testInventoryInitialization() {
        Inventory inventory = player.getInventory();
        assertNotNull(inventory, "Inventory should not be null");
        // Inventory is 8x6 grid
        assertEquals(8, inventory.getGridWidth(), "Inventory should have 8 columns");
        assertEquals(6, inventory.getGridHeight(), "Inventory should have 6 rows");
    }

    @Test
    @DisplayName("Player stats should be initialized correctly")
    void testStatsInitialization() {
        PlayerStats stats = player.getStats();
        assertNotNull(stats, "Stats should not be null");
        assertEquals(1, stats.getLevel(), "Should start at level 1");
        assertEquals(100f, stats.getCurrentHealth(), "Should start with full health");
    }

    @Test
    @DisplayName("Setting target position should initiate movement")
    void testSetTargetPosition() {
        player.setTargetPosition(10f, 10f);

        // Update player - should move toward target
        when(mockWorld.isSolidAtPosition(anyFloat(), anyFloat())).thenReturn(false);
        player.update(0.1f);

        // Player should have moved closer to target
        assertTrue(player.getPosition().x > 5f || player.getPosition().y > 5f,
                "Player should have moved from initial position");
    }

    @Test
    @DisplayName("StopMoving should clear target position")
    void testStopMoving() {
        player.setTargetPosition(10f, 10f);
        player.stopMoving();

        float initialX = player.getPosition().x;
        float initialY = player.getPosition().y;

        // Update player - should not move
        player.update(0.1f);

        assertEquals(initialX, player.getPosition().x, "X position should not change");
        assertEquals(initialY, player.getPosition().y, "Y position should not change");
    }

    @Test
    @DisplayName("Player should update facing angle when moving")
    void testFacingAngleUpdate() {
        // Set target to the right (east)
        player.setTargetPosition(10f, 5f);

        when(mockWorld.isSolidAtPosition(anyFloat(), anyFloat())).thenReturn(false);
        player.update(0.1f);

        // Facing angle should be approximately 0 degrees (east)
        float facingAngle = player.getFacingAngle();
        assertTrue(facingAngle >= -10 && facingAngle <= 10,
                "Facing angle should be approximately 0 degrees when moving east");
    }

    @Test
    @DisplayName("Player should calculate 8-directional facing correctly")
    void testEightDirectionalFacing() {
        // Set target to the right (east)
        player.setTargetPosition(10f, 5f);

        when(mockWorld.isSolidAtPosition(anyFloat(), anyFloat())).thenReturn(false);
        player.update(0.1f);

        int facingDirection = player.getFacingDirection();
        assertTrue(facingDirection >= 0 && facingDirection <= 7,
                "Facing direction should be in range 0-7");
    }

    @Test
    @DisplayName("Player should stop moving when reaching target")
    void testArrivalAtTarget() {
        player.setTargetPosition(5.05f, 5.05f); // Very close target

        when(mockWorld.isSolidAtPosition(anyFloat(), anyFloat())).thenReturn(false);
        player.update(1.0f); // Long update to ensure arrival

        // Player should have stopped moving (target cleared)
        float x = player.getPosition().x;
        player.update(0.1f);

        assertEquals(x, player.getPosition().x, 0.01f,
                "Player should not move after reaching target");
    }

    @Test
    @DisplayName("NoClip mode should bypass collision detection")
    void testNoClipMode() {
        player.setNoClip(true);
        assertTrue(player.isNoClip(), "NoClip should be enabled");

        // Set target through a solid wall
        player.setTargetPosition(10f, 10f);
        when(mockWorld.isSolidAtPosition(anyFloat(), anyFloat())).thenReturn(true);

        float initialX = player.getPosition().x;
        player.update(0.1f);

        // Player should move even through solid tiles
        assertNotEquals(initialX, player.getPosition().x,
                "Player should move through walls with NoClip enabled");

        player.setNoClip(false);
        assertFalse(player.isNoClip(), "NoClip should be disabled");
    }

    @Test
    @DisplayName("Player should be blocked by solid tiles when NoClip is off")
    void testCollisionWithSolidTiles() {
        player.setNoClip(false);
        player.setTargetPosition(6f, 5f);

        // Mock all collision checks to return solid
        when(mockWorld.isSolidAtPosition(anyFloat(), anyFloat())).thenReturn(true);

        float initialX = player.getPosition().x;
        player.update(0.1f);

        // Player should not move through solid tiles
        assertEquals(initialX, player.getPosition().x, 0.01f,
                "Player should not move through solid tiles");
    }

    @Test
    @DisplayName("Player should be blocked by breakable objects")
    void testCollisionWithBreakableObjects() {
        List<BreakableObject> breakableObjects = new ArrayList<>();

        // Create a breakable object blocking the path
        Texture objTexture = mock(Texture.class);
        when(objTexture.getWidth()).thenReturn(32);
        when(objTexture.getHeight()).thenReturn(32);
        BreakableObject obstacle = new BreakableObject(objTexture, 6f, 5f, 10, 0, 0);
        breakableObjects.add(obstacle);

        player.setBreakableObjects(breakableObjects);
        player.setTargetPosition(7f, 5f);

        when(mockWorld.isSolidAtPosition(anyFloat(), anyFloat())).thenReturn(false);

        float initialX = player.getPosition().x;
        player.update(0.1f);

        // Player should be somewhat blocked by the object
        assertTrue(Math.abs(player.getPosition().x - initialX) < 1.0f,
                "Player movement should be limited by breakable object");
    }

    @Test
    @DisplayName("Player can pass through destroyed breakable objects")
    void testNoCollisionWithDestroyedObjects() {
        List<BreakableObject> breakableObjects = new ArrayList<>();

        Texture objTexture = mock(Texture.class);
        when(objTexture.getWidth()).thenReturn(32);
        when(objTexture.getHeight()).thenReturn(32);
        BreakableObject obstacle = new BreakableObject(objTexture, 6f, 5f, 10, 0, 0);
        obstacle.damage(100); // Destroy it
        breakableObjects.add(obstacle);

        player.setBreakableObjects(breakableObjects);
        player.setTargetPosition(7f, 5f);

        when(mockWorld.isSolidAtPosition(anyFloat(), anyFloat())).thenReturn(false);

        player.update(0.1f);

        // Player should be able to move
        assertTrue(player.getPosition().x > 5f,
                "Player should pass through destroyed objects");
    }

    @Test
    @DisplayName("SetPosition should update player coordinates")
    void testSetPosition() {
        player.setPosition(10f, 20f);

        assertEquals(10f, player.getPosition().x, "X position should be updated");
        assertEquals(20f, player.getPosition().y, "Y position should be updated");
    }

    @Test
    @DisplayName("SetTilePosition should center player on tile")
    void testSetTilePosition() {
        player.setTilePosition(10, 20);

        assertEquals(10.5f, player.getPosition().x, "X should be centered on tile");
        assertEquals(20.5f, player.getPosition().y, "Y should be centered on tile");
    }

    @Test
    @DisplayName("GetTileX and GetTileY should return correct tile coordinates")
    void testGetTileCoordinates() {
        player.setPosition(10.7f, 20.3f);

        assertEquals(10, player.getTileX(), "Tile X should be 10");
        assertEquals(20, player.getTileY(), "Tile Y should be 20");
    }

    @Test
    @DisplayName("SetWorld should update world provider")
    void testSetWorld() {
        WorldProvider newWorld = mock(WorldProvider.class);
        player.setWorld(newWorld);

        // Verify new world is used by testing collision
        player.setTargetPosition(10f, 10f);
        when(newWorld.isSolidAtPosition(anyFloat(), anyFloat())).thenReturn(true);

        float initialX = player.getPosition().x;
        player.update(0.1f);

        // Should check collision with new world
        verify(newWorld, atLeastOnce()).isSolidAtPosition(anyFloat(), anyFloat());
    }

    @Test
    @DisplayName("Update should regenerate stamina over time")
    void testStaminaRegeneration() {
        player.getStats().useStamina(50f);
        float initialStamina = player.getStats().getCurrentStamina();

        player.update(1.0f); // 1 second

        assertTrue(player.getStats().getCurrentStamina() > initialStamina,
                "Stamina should regenerate over time");
    }

    @Test
    @DisplayName("GetAttackRange should return base melee range")
    void testGetAttackRange() {
        float range = player.getAttackRange();
        assertEquals(48f, range, "Base attack range should be 48 pixels");
    }

    @Test
    @DisplayName("GetAttackSpeed should return base attack speed")
    void testGetAttackSpeed() {
        float speed = player.getAttackSpeed();
        assertEquals(1.0f, speed, "Base attack speed should be 1 attack/second");
    }

    @Test
    @DisplayName("Attack should succeed when off cooldown")
    void testAttackSuccess() {
        when(mockEnemy.isDead()).thenReturn(false);
        when(mockEnemy.getName()).thenReturn("Test Enemy");

        // Update player to allow attack cooldown to pass
        player.update(1.1f);

        boolean attacked = player.attack(mockEnemy);

        assertTrue(attacked, "Attack should succeed when off cooldown");
        verify(mockEnemy).takeDamage(anyInt(), eq(player));
    }

    @Test
    @DisplayName("Attack should fail when on cooldown")
    void testAttackCooldown() {
        when(mockEnemy.isDead()).thenReturn(false);
        when(mockEnemy.getName()).thenReturn("Test Enemy");

        // Update to allow first attack
        player.update(1.1f);

        // First attack
        player.attack(mockEnemy);

        // Immediate second attack should fail (on cooldown)
        boolean secondAttack = player.attack(mockEnemy);

        assertFalse(secondAttack, "Second attack should fail due to cooldown");
    }

    @Test
    @DisplayName("Attack should succeed after cooldown expires")
    void testAttackAfterCooldown() {
        when(mockEnemy.isDead()).thenReturn(false);
        when(mockEnemy.getName()).thenReturn("Test Enemy");

        // Update to allow first attack
        player.update(1.1f);

        // First attack
        player.attack(mockEnemy);

        // Wait for cooldown (1 second at base attack speed)
        when(mockWorld.isSolidAtPosition(anyFloat(), anyFloat())).thenReturn(false);
        player.update(1.1f);

        // Second attack should succeed
        boolean secondAttack = player.attack(mockEnemy);

        assertTrue(secondAttack, "Attack should succeed after cooldown");
    }

    @Test
    @DisplayName("Attack should fail against null target")
    void testAttackNullTarget() {
        boolean attacked = player.attack(null);
        assertFalse(attacked, "Attack should fail with null target");
    }

    @Test
    @DisplayName("Attack should fail against dead enemy")
    void testAttackDeadEnemy() {
        when(mockEnemy.isDead()).thenReturn(true);

        boolean attacked = player.attack(mockEnemy);

        assertFalse(attacked, "Attack should fail against dead enemy");
        verify(mockEnemy, never()).takeDamage(anyInt(), any());
    }

    @Test
    @DisplayName("Attack should set target enemy")
    void testAttackSetsTargetEnemy() {
        when(mockEnemy.isDead()).thenReturn(false);
        when(mockEnemy.getName()).thenReturn("Test Enemy");

        // Update to allow attack
        player.update(1.1f);

        player.attack(mockEnemy);

        assertEquals(mockEnemy, player.getTargetEnemy(),
                "Attack should set the target enemy");
    }

    @Test
    @DisplayName("SetTargetEnemy should update target")
    void testSetTargetEnemy() {
        player.setTargetEnemy(mockEnemy);
        assertEquals(mockEnemy, player.getTargetEnemy(),
                "Target enemy should be updated");
    }

    @Test
    @DisplayName("IsInAttackRange should return true when NPC is close")
    void testIsInAttackRangeTrue() {
        when(mockEnemy.getPosition()).thenReturn(new com.badlogic.gdx.math.Vector2(5.5f, 5.5f));

        boolean inRange = player.isInAttackRange(mockEnemy);

        assertTrue(inRange, "Enemy should be in attack range when close");
    }

    @Test
    @DisplayName("IsInAttackRange should return false when NPC is far")
    void testIsInAttackRangeFalse() {
        when(mockEnemy.getPosition()).thenReturn(new com.badlogic.gdx.math.Vector2(100f, 100f));

        boolean inRange = player.isInAttackRange(mockEnemy);

        assertFalse(inRange, "Enemy should not be in attack range when far");
    }

    @Test
    @DisplayName("IsInAttackRange should return false for null NPC")
    void testIsInAttackRangeNull() {
        boolean inRange = player.isInAttackRange(null);
        assertFalse(inRange, "Null NPC should not be in range");
    }

    @Test
    @DisplayName("Attack should deal damage based on player stats")
    void testAttackDamageCalculation() {
        when(mockEnemy.isDead()).thenReturn(false);
        when(mockEnemy.getName()).thenReturn("Test Enemy");

        int expectedDamage = player.getStats().getTotalDamage();

        // Update to allow attack
        player.update(1.1f);

        player.attack(mockEnemy);

        verify(mockEnemy).takeDamage(eq(expectedDamage), eq(player));
    }

    @Test
    @DisplayName("Equipment should be accessible")
    void testGetEquipment() {
        Equipment equipment = player.getEquipment();
        assertNotNull(equipment, "Equipment should not be null");
    }

    @Test
    @DisplayName("Player should slide along obstacles when moving diagonally")
    void testObstacleSliding() {
        player.setTargetPosition(6f, 6f);

        // Mock X-axis as solid but Y-axis as free
        when(mockWorld.isSolidAtPosition(anyFloat(), anyFloat())).thenAnswer(invocation -> {
            float x = invocation.getArgument(0);
            // Block horizontal movement but allow vertical
            return x > 5.5f * 32; // Convert to pixels
        });

        float initialY = player.getPosition().y;
        player.update(0.1f);

        // Player should slide along Y axis even if X is blocked
        assertTrue(player.getPosition().y >= initialY,
                "Player should be able to slide along obstacles");
    }
}
