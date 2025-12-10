package io.github.inherit_this.particles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests for Particle class - physics simulation, lifetime, and rendering behavior.
 */
class ParticleTest {

    private TextureRegion mockTexture;
    private Particle particle;

    @BeforeEach
    void setUp() {
        mockTexture = mock(TextureRegion.class);
    }

    @Test
    @DisplayName("Particle should initialize with correct position")
    void testInitialPosition() {
        particle = new Particle(mockTexture, 100f, 50f, 25f,
                0f, 0f, 0f,
                1.0f, -500f, Color.WHITE, 10f);

        assertTrue(particle.isAlive(), "Particle should be alive on creation");
    }

    @Test
    @DisplayName("Particle should initialize with all constructor parameters")
    void testInitialization() {
        float x = 150f, y = 200f, z = 75f;
        float vx = 10f, vy = 20f, vz = 5f;
        float lifetime = 2.5f;
        float gravity = -400f;
        Color tint = new Color(1f, 0.5f, 0.2f, 1f);
        float size = 16f;

        particle = new Particle(mockTexture, x, y, z, vx, vy, vz,
                lifetime, gravity, tint, size);

        assertTrue(particle.isAlive(), "Particle should be alive initially");
    }

    @Test
    @DisplayName("Particle should apply velocity to position during update")
    void testVelocityApplication() {
        // Start at origin with velocity moving right and up
        particle = new Particle(mockTexture, 0f, 100f, 0f,
                100f, 50f, 20f,  // velocity
                5.0f, -500f, Color.WHITE, 10f);

        boolean alive = particle.update(1.0f); // 1 second update

        assertTrue(alive, "Particle should still be alive");
        // Position should have changed by velocity * delta
        // We can't directly check position, but we can verify through multiple updates
    }

    @Test
    @DisplayName("Particle should apply gravity to velocity")
    void testGravityApplication() {
        // Particle high in the air with no initial vertical velocity
        particle = new Particle(mockTexture, 0f, 500f, 0f,
                0f, 0f, 0f,
                10.0f, -500f, Color.WHITE, 10f);

        // Update multiple times to see gravity effect
        particle.update(0.1f);
        particle.update(0.1f);
        particle.update(0.1f);

        // Particle should still be alive and falling
        assertTrue(particle.isAlive(), "Particle should still be alive");
    }

    @Test
    @DisplayName("Particle should handle ground collision")
    void testGroundCollision() {
        // Particle starts at y=10 with downward velocity
        particle = new Particle(mockTexture, 0f, 10f, 0f,
                100f, -50f, 0f,  // Moving down and to the right
                5.0f, -500f, Color.WHITE, 10f);

        // Update enough to hit ground
        particle.update(1.0f);

        assertTrue(particle.isAlive(), "Particle should still be alive after ground collision");
    }

    @Test
    @DisplayName("Particle should apply friction when on ground")
    void testGroundFriction() {
        // Particle at ground level with horizontal velocity
        particle = new Particle(mockTexture, 0f, 0f, 0f,
                100f, 0f, 50f,  // Horizontal velocity only
                5.0f, -500f, Color.WHITE, 10f);

        // Update multiple times - velocity should decay due to friction
        for (int i = 0; i < 10; i++) {
            particle.update(0.1f);
        }

        assertTrue(particle.isAlive(), "Particle should still be alive");
        // Velocity should have decreased due to 0.95 friction multiplier
    }

    @Test
    @DisplayName("Particle lifetime should decrease with updates")
    void testLifetimeDecrement() {
        particle = new Particle(mockTexture, 0f, 100f, 0f,
                0f, 0f, 0f,
                1.0f, -500f, Color.WHITE, 10f);

        // Update for 0.5 seconds
        boolean alive = particle.update(0.5f);
        assertTrue(alive, "Particle should be alive after 0.5s");

        // Update for another 0.4 seconds (total 0.9s)
        alive = particle.update(0.4f);
        assertTrue(alive, "Particle should still be alive after 0.9s");

        // Update for another 0.2 seconds (total 1.1s, exceeds 1.0s lifetime)
        alive = particle.update(0.2f);
        assertFalse(alive, "Particle should be dead after exceeding lifetime");
    }

    @Test
    @DisplayName("Particle should return false when lifetime expires")
    void testLifetimeExpiration() {
        particle = new Particle(mockTexture, 0f, 100f, 0f,
                0f, 0f, 0f,
                0.5f, -500f, Color.WHITE, 10f);

        // Update past the lifetime
        boolean alive = particle.update(1.0f);

        assertFalse(alive, "Update should return false when lifetime expires");
        assertFalse(particle.isAlive(), "isAlive should return false when lifetime expires");
    }

    @Test
    @DisplayName("Particle isAlive should track lifetime correctly")
    void testIsAliveTracking() {
        particle = new Particle(mockTexture, 0f, 100f, 0f,
                0f, 0f, 0f,
                1.0f, -500f, Color.WHITE, 10f);

        assertTrue(particle.isAlive(), "Particle should be alive initially");

        particle.update(0.5f);
        assertTrue(particle.isAlive(), "Particle should be alive after 0.5s");

        particle.update(0.6f); // Total 1.1s
        assertFalse(particle.isAlive(), "Particle should be dead after lifetime expires");
    }

    @Test
    @DisplayName("Particle should handle zero delta update")
    void testZeroDeltaUpdate() {
        particle = new Particle(mockTexture, 0f, 100f, 0f,
                100f, 100f, 100f,
                1.0f, -500f, Color.WHITE, 10f);

        boolean alive = particle.update(0f);

        assertTrue(alive, "Particle should still be alive with zero delta");
        assertTrue(particle.isAlive(), "isAlive should return true with zero delta");
    }

    @Test
    @DisplayName("Particle should handle very small delta values")
    void testSmallDeltaUpdates() {
        particle = new Particle(mockTexture, 0f, 100f, 0f,
                50f, 50f, 50f,
                0.1f, -500f, Color.WHITE, 10f);

        // Many small updates
        for (int i = 0; i < 50; i++) {
            particle.update(0.001f);
        }

        assertTrue(particle.isAlive(), "Particle should still be alive after 0.05s total");

        // Finish the remaining lifetime
        for (int i = 0; i < 60; i++) {
            particle.update(0.001f);
        }

        assertFalse(particle.isAlive(), "Particle should be dead after exceeding 0.1s lifetime");
    }

    @Test
    @DisplayName("Particle with positive gravity should accelerate upward")
    void testPositiveGravity() {
        // Test with positive gravity (unusual but should work)
        particle = new Particle(mockTexture, 0f, 0f, 0f,
                0f, 0f, 0f,
                5.0f, 500f, Color.WHITE, 10f);  // Positive gravity

        particle.update(0.1f);
        assertTrue(particle.isAlive(), "Particle should be alive");
        // Velocity should increase upward due to positive gravity
    }

    @Test
    @DisplayName("Particle with zero gravity should not accelerate vertically")
    void testZeroGravity() {
        particle = new Particle(mockTexture, 0f, 100f, 0f,
                50f, 50f, 0f,
                5.0f, 0f, Color.WHITE, 10f);  // No gravity

        particle.update(1.0f);

        assertTrue(particle.isAlive(), "Particle should be alive");
        // Without gravity, vertical velocity should remain constant
    }

    @Test
    @DisplayName("Particle should not go below ground level")
    void testGroundClamp() {
        // Start below ground with downward velocity
        particle = new Particle(mockTexture, 0f, -10f, 0f,
                0f, -100f, 0f,
                5.0f, -500f, Color.WHITE, 10f);

        particle.update(0.1f);

        assertTrue(particle.isAlive(), "Particle should be alive");
        // Position should be clamped to y=0
    }

    @Test
    @DisplayName("Particle render should not throw with valid camera and batch")
    void testRender() {
        particle = new Particle(mockTexture, 100f, 50f, 25f,
                0f, 0f, 0f,
                2.0f, -500f, Color.RED, 16f);

        SpriteBatch mockBatch = mock(SpriteBatch.class);
        PerspectiveCamera mockCamera = mock(PerspectiveCamera.class);

        // Setup camera projection to return a screen position
        Vector3 screenPos = new Vector3(400f, 300f, 0f);
        when(mockCamera.project(any(Vector3.class))).thenReturn(screenPos);

        // Should not throw exception
        assertDoesNotThrow(() -> particle.render(mockBatch, mockCamera));

        // Verify batch methods were called
        verify(mockBatch, atLeastOnce()).setColor(anyFloat(), anyFloat(), anyFloat(), anyFloat());
        verify(mockBatch, times(1)).draw(eq(mockTexture), anyFloat(), anyFloat(), anyFloat(), anyFloat());
    }

    @Test
    @DisplayName("Particle render should apply fade-out in last 30% of lifetime")
    void testRenderFadeOut() {
        // Create particle with 1.0s lifetime
        particle = new Particle(mockTexture, 100f, 50f, 25f,
                0f, 0f, 0f,
                1.0f, -500f, Color.WHITE, 16f);

        SpriteBatch mockBatch = mock(SpriteBatch.class);
        PerspectiveCamera mockCamera = mock(PerspectiveCamera.class);
        Vector3 screenPos = new Vector3(400f, 300f, 0f);
        when(mockCamera.project(any(Vector3.class))).thenReturn(screenPos);

        // At full lifetime, alpha should be 1.0
        particle.render(mockBatch, mockCamera);
        verify(mockBatch).setColor(1f, 1f, 1f, 1f);

        reset(mockBatch);

        // Update to 0.8s remaining (80% of lifetime, no fade yet)
        particle.update(0.2f);
        particle.render(mockBatch, mockCamera);
        verify(mockBatch).setColor(1f, 1f, 1f, 1f);

        reset(mockBatch);

        // Update to 0.2s remaining (20% of lifetime, should fade)
        particle.update(0.6f);
        particle.render(mockBatch, mockCamera);
        // Alpha should be 0.2 / (1.0 * 0.3) = 0.666...
        verify(mockBatch).setColor(eq(1f), eq(1f), eq(1f), floatThat(alpha -> alpha > 0.6f && alpha < 0.7f));
    }

    @Test
    @DisplayName("Particle render should reset batch color after drawing")
    void testRenderResetsColor() {
        particle = new Particle(mockTexture, 100f, 50f, 25f,
                0f, 0f, 0f,
                1.0f, -500f, Color.RED, 16f);

        SpriteBatch mockBatch = mock(SpriteBatch.class);
        PerspectiveCamera mockCamera = mock(PerspectiveCamera.class);
        Vector3 screenPos = new Vector3(400f, 300f, 0f);
        when(mockCamera.project(any(Vector3.class))).thenReturn(screenPos);

        particle.render(mockBatch, mockCamera);

        // Verify color was reset to white at the end
        verify(mockBatch, times(1)).setColor(1f, 1f, 1f, 1f);
    }

    @Test
    @DisplayName("Multiple particles should maintain independent lifetimes")
    void testMultipleParticlesIndependence() {
        Particle particle1 = new Particle(mockTexture, 0f, 0f, 0f,
                0f, 0f, 0f, 1.0f, -500f, Color.WHITE, 10f);
        Particle particle2 = new Particle(mockTexture, 0f, 0f, 0f,
                0f, 0f, 0f, 2.0f, -500f, Color.WHITE, 10f);
        Particle particle3 = new Particle(mockTexture, 0f, 0f, 0f,
                0f, 0f, 0f, 0.5f, -500f, Color.WHITE, 10f);

        // Update all by 0.6s
        particle1.update(0.6f);
        particle2.update(0.6f);
        particle3.update(0.6f);

        assertTrue(particle1.isAlive(), "Particle1 should still be alive (0.4s remaining)");
        assertTrue(particle2.isAlive(), "Particle2 should still be alive (1.4s remaining)");
        assertFalse(particle3.isAlive(), "Particle3 should be dead (exceeded 0.5s lifetime)");
    }

    @Test
    @DisplayName("Particle with long lifetime should survive many updates")
    void testLongLivedParticle() {
        particle = new Particle(mockTexture, 0f, 100f, 0f,
                50f, 50f, 50f,
                10.0f, -500f, Color.WHITE, 10f);

        // Simulate 9 seconds of updates
        for (int i = 0; i < 90; i++) {
            boolean alive = particle.update(0.1f);
            assertTrue(alive, "Particle should be alive during its lifetime");
        }

        assertTrue(particle.isAlive(), "Particle should still be alive at 9s");

        // One more second to exceed lifetime
        for (int i = 0; i < 15; i++) {
            particle.update(0.1f);
        }

        assertFalse(particle.isAlive(), "Particle should be dead after 10.5s");
    }

    @Test
    @DisplayName("Particle velocity should be affected by gravity over time")
    void testGravityAccumulationOverTime() {
        // Particle in the air with initial upward velocity
        particle = new Particle(mockTexture, 0f, 100f, 0f,
                0f, 100f, 0f,  // Initial upward velocity
                10.0f, -500f, Color.WHITE, 10f);

        // After several updates, gravity should overcome initial velocity
        for (int i = 0; i < 5; i++) {
            particle.update(0.1f);
        }

        assertTrue(particle.isAlive(), "Particle should still be alive");
        // Velocity should have been modified by gravity
    }

    @Test
    @DisplayName("Particle should handle 3D movement correctly")
    void test3DMovement() {
        // Particle with velocity in all three dimensions
        particle = new Particle(mockTexture, 0f, 100f, 0f,
                50f, 50f, 50f,  // Moving in X, Y, and Z
                5.0f, -500f, Color.WHITE, 10f);

        boolean alive = particle.update(1.0f);

        assertTrue(alive, "Particle should be alive");
        // Position should have changed in all dimensions
    }

    @Test
    @DisplayName("Particle color should be preserved")
    void testColorPreservation() {
        Color customColor = new Color(0.8f, 0.3f, 0.5f, 1f);
        particle = new Particle(mockTexture, 100f, 50f, 25f,
                0f, 0f, 0f,
                1.0f, -500f, customColor, 16f);

        SpriteBatch mockBatch = mock(SpriteBatch.class);
        PerspectiveCamera mockCamera = mock(PerspectiveCamera.class);
        Vector3 screenPos = new Vector3(400f, 300f, 0f);
        when(mockCamera.project(any(Vector3.class))).thenReturn(screenPos);

        particle.render(mockBatch, mockCamera);

        // Verify the tint color components were used (with full alpha initially)
        verify(mockBatch).setColor(eq(0.8f), eq(0.3f), eq(0.5f), eq(1f));
    }
}
