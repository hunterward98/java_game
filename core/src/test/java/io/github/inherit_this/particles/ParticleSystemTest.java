package io.github.inherit_this.particles;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.inherit_this.LibGdxTestBase;
import io.github.inherit_this.entities.BreakableObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive tests for ParticleSystem - particle effect management system
 */
@DisplayName("ParticleSystem Tests")
class ParticleSystemTest extends LibGdxTestBase {

    private ParticleSystem particleSystem;

    @Mock
    private PerspectiveCamera mockCamera;

    @Mock
    private SpriteBatch mockBatch;

    @Mock
    private BreakableObject mockObject;

    @Mock
    private Texture mockTexture;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        particleSystem = new ParticleSystem(mockCamera);
    }

    // ==================== Initialization Tests ====================

    @Nested
    @DisplayName("Initialization")
    class Initialization {

        @Test
        @DisplayName("ParticleSystem should initialize with camera")
        void testInitialization() {
            assertNotNull(particleSystem, "ParticleSystem should not be null");
        }

        @Test
        @DisplayName("ParticleSystem should start with zero particles")
        void testInitialParticleCount() {
            assertEquals(0, particleSystem.getParticleCount(), "Should start with 0 particles");
        }

        @Test
        @DisplayName("ParticleSystem should not crash without textures")
        void testInitializationWithoutTextures() {
            assertDoesNotThrow(() -> new ParticleSystem(mockCamera),
                "Should handle missing particle textures gracefully");
        }
    }

    // ==================== Particle Count Tests ====================

    @Nested
    @DisplayName("Particle Count Management")
    class ParticleCountManagement {

        @Test
        @DisplayName("getParticleCount should return current count")
        void testGetParticleCount() {
            assertEquals(0, particleSystem.getParticleCount(), "Initial count should be 0");
        }

        @Test
        @DisplayName("clear should reset particle count to zero")
        void testClear() {
            particleSystem.clear();
            assertEquals(0, particleSystem.getParticleCount(), "Count should be 0 after clear");
        }

        @Test
        @DisplayName("clear should work when already empty")
        void testClearWhenEmpty() {
            particleSystem.clear();
            particleSystem.clear();
            assertEquals(0, particleSystem.getParticleCount(), "Should remain 0 after double clear");
        }
    }

    // ==================== Create Break Effect Tests ====================

    @Nested
    @DisplayName("Create Break Effect")
    class CreateBreakEffect {

        @BeforeEach
        void setUpMocks() {
            when(mockObject.getName()).thenReturn("Wooden Crate");
            when(mockObject.getTexture()).thenReturn(mockTexture);
        }

        @Test
        @DisplayName("createBreakEffect should not crash")
        void testCreateBreakEffect() {
            assertDoesNotThrow(() ->
                particleSystem.createBreakEffect(mockObject, 100f, 100f, 0f, 10, 50f, 150f),
                "createBreakEffect should not crash"
            );
        }

        @Test
        @DisplayName("createBreakEffect should handle zero particles")
        void testCreateBreakEffectZeroParticles() {
            assertDoesNotThrow(() ->
                particleSystem.createBreakEffect(mockObject, 100f, 100f, 0f, 0, 50f, 150f),
                "Should handle zero particle count gracefully"
            );
        }

        @Test
        @DisplayName("createBreakEffect should handle many particles")
        void testCreateBreakEffectManyParticles() {
            assertDoesNotThrow(() ->
                particleSystem.createBreakEffect(mockObject, 100f, 100f, 0f, 1000, 50f, 150f),
                "Should handle large particle count"
            );
        }

        @Test
        @DisplayName("createBreakEffect should handle wood objects")
        void testCreateBreakEffectWood() {
            when(mockObject.getName()).thenReturn("Wooden Crate");
            assertDoesNotThrow(() ->
                particleSystem.createBreakEffect(mockObject, 100f, 100f, 0f, 10, 50f, 150f)
            );
        }

        @Test
        @DisplayName("createBreakEffect should handle stone objects")
        void testCreateBreakEffectStone() {
            when(mockObject.getName()).thenReturn("Stone Pot");
            assertDoesNotThrow(() ->
                particleSystem.createBreakEffect(mockObject, 100f, 100f, 0f, 10, 50f, 150f)
            );
        }
    }

    // ==================== Update Tests ====================

    @Nested
    @DisplayName("Update System")
    class UpdateSystem {

        @Test
        @DisplayName("update should not crash with no particles")
        void testUpdateEmpty() {
            assertDoesNotThrow(() -> particleSystem.update(0.016f));
        }

        @Test
        @DisplayName("update should handle zero delta time")
        void testUpdateZeroDelta() {
            assertDoesNotThrow(() -> particleSystem.update(0f));
        }

        @Test
        @DisplayName("update should handle large delta time")
        void testUpdateLargeDelta() {
            assertDoesNotThrow(() -> particleSystem.update(10f));
        }
    }

    // ==================== Render Tests ====================

    @Nested
    @DisplayName("Render System")
    class RenderSystem {

        @Test
        @DisplayName("render should not crash with no particles")
        void testRenderEmpty() {
            assertDoesNotThrow(() -> particleSystem.render(mockBatch));
        }
    }

    // ==================== Disposal Tests ====================

    @Nested
    @DisplayName("Disposal")
    class Disposal {

        @Test
        @DisplayName("dispose should not crash")
        void testDispose() {
            assertDoesNotThrow(() -> particleSystem.dispose());
        }

        @Test
        @DisplayName("dispose should be callable multiple times")
        void testDisposeMultipleTimes() {
            assertDoesNotThrow(() -> {
                particleSystem.dispose();
                particleSystem.dispose();
            });
        }
    }

    // ==================== MaterialType Enum Tests ====================

    @Nested
    @DisplayName("MaterialType Enum")
    class MaterialTypeEnum {

        @Test
        @DisplayName("MaterialType should have WOOD value")
        void testMaterialTypeWood() {
            assertNotNull(ParticleSystem.MaterialType.WOOD);
        }

        @Test
        @DisplayName("MaterialType should have STONE value")
        void testMaterialTypeStone() {
            assertNotNull(ParticleSystem.MaterialType.STONE);
        }
    }
}
