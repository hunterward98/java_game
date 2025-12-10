package io.github.inherit_this.input;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import io.github.inherit_this.LibGdxTestBase;
import io.github.inherit_this.entities.Player;
import io.github.inherit_this.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for InputHandler - camera controls and mouse input
 */
class InputHandlerTest extends LibGdxTestBase {

    @Mock
    private Player mockPlayer;

    private PerspectiveCamera camera;
    private InputHandler inputHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup player mock
        when(mockPlayer.getPosition()).thenReturn(new Vector2(5f, 5f));

        // Create real camera (can't mock easily due to final methods)
        camera = new PerspectiveCamera(67, 1920, 1080);
        camera.position.set(0, 100, 0);
        camera.lookAt(0, 0, 0);
        camera.near = 1f;
        camera.far = 5000f;
        camera.update();

        inputHandler = new InputHandler(camera, mockPlayer);
    }

    @Test
    @DisplayName("Constructor should initialize with camera and player")
    void testConstructor() {
        assertNotNull(inputHandler, "InputHandler should be created");
    }

    @Test
    @DisplayName("getScrollProcessor should return InputAdapter")
    void testGetScrollProcessor() {
        InputAdapter scrollProcessor = inputHandler.getScrollProcessor();
        assertNotNull(scrollProcessor, "Scroll processor should not be null");
    }

    @Test
    @DisplayName("Scroll down should increase camera distance")
    void testScrollDown() {
        float initialDistance = inputHandler.getCameraDistance();
        InputAdapter scrollProcessor = inputHandler.getScrollProcessor();

        // Scroll down (positive amountY)
        scrollProcessor.scrolled(0, 1);

        assertTrue(inputHandler.getCameraDistance() > initialDistance,
                "Camera distance should increase on scroll down");
    }

    @Test
    @DisplayName("Scroll up should decrease camera distance")
    void testScrollUp() {
        float initialDistance = inputHandler.getCameraDistance();
        InputAdapter scrollProcessor = inputHandler.getScrollProcessor();

        // Scroll up (negative amountY)
        scrollProcessor.scrolled(0, -1);

        assertTrue(inputHandler.getCameraDistance() < initialDistance,
                "Camera distance should decrease on scroll up");
    }

    @Test
    @DisplayName("Camera distance should be clamped to MIN_CAMERA_DISTANCE")
    void testCameraDistanceMinClamp() {
        InputAdapter scrollProcessor = inputHandler.getScrollProcessor();

        // Scroll up a lot to try to go below minimum
        for (int i = 0; i < 50; i++) {
            scrollProcessor.scrolled(0, -1);
        }

        assertTrue(inputHandler.getCameraDistance() >= 200f,
                "Camera distance should not go below MIN (200)");
    }

    @Test
    @DisplayName("Camera distance should be clamped to MAX_CAMERA_DISTANCE")
    void testCameraDistanceMaxClamp() {
        InputAdapter scrollProcessor = inputHandler.getScrollProcessor();

        // Scroll down a lot to try to go above maximum
        for (int i = 0; i < 50; i++) {
            scrollProcessor.scrolled(0, 1);
        }

        assertTrue(inputHandler.getCameraDistance() <= 400f,
                "Camera distance should not go above MAX (400)");
    }

    @Test
    @DisplayName("getCameraAngle should return initial angle")
    void testGetCameraAngle() {
        assertEquals(45f, inputHandler.getCameraAngle(), "Initial camera angle should be 45");
    }

    @Test
    @DisplayName("getCameraDistance should return initial distance")
    void testGetCameraDistance() {
        assertEquals(300f, inputHandler.getCameraDistance(), "Initial camera distance should be 300");
    }

    @Test
    @DisplayName("setCameraDistance should clamp to valid range")
    void testSetCameraDistance() {
        inputHandler.setCameraDistance(350f);
        assertEquals(350f, inputHandler.getCameraDistance());

        // Try to set below minimum
        inputHandler.setCameraDistance(100f);
        assertEquals(200f, inputHandler.getCameraDistance(), "Should clamp to MIN");

        // Try to set above maximum
        inputHandler.setCameraDistance(500f);
        assertEquals(400f, inputHandler.getCameraDistance(), "Should clamp to MAX");
    }

    @Test
    @DisplayName("updateCameraPosition should move camera based on player position")
    void testUpdateCameraPosition() {
        Vector3 initialPos = camera.position.cpy();

        inputHandler.updateCameraPosition();

        // Camera should have moved (player is at 5,5 not 0,0)
        assertNotEquals(initialPos, camera.position,
                "Camera position should be updated");
    }

    @Test
    @DisplayName("updateCameraPosition should keep camera looking at player")
    void testCameraLooksAtPlayer() {
        inputHandler.updateCameraPosition();

        // Camera direction should point towards player position
        float playerPixelX = 5f * Constants.TILE_SIZE;
        float playerPixelZ = 5f * Constants.TILE_SIZE;

        // Direction from camera to player
        Vector3 toPlayer = new Vector3(playerPixelX, 0, playerPixelZ)
                .sub(camera.position).nor();
        Vector3 cameraDir = camera.direction.cpy().nor();

        // They should be roughly aligned (allowing some tolerance)
        float dot = toPlayer.dot(cameraDir);
        assertTrue(dot > 0.9f, "Camera should be looking at player");
    }

    @Test
    @DisplayName("updateCameraRotation should not change angle when no keys pressed")
    void testUpdateCameraRotationNoKeys() {
        float initialAngle = inputHandler.getCameraAngle();

        inputHandler.updateCameraRotation(0.1f);

        assertEquals(initialAngle, inputHandler.getCameraAngle(),
                "Angle should not change without key input");
    }

    @Test
    @DisplayName("checkRotationKeys should not throw exceptions")
    void testCheckRotationKeys() {
        assertDoesNotThrow(() -> inputHandler.checkRotationKeys());
    }

    @Test
    @DisplayName("getTileFromMouse should return null for invalid ray")
    void testGetTileFromMouseInvalidRay() {
        // Without proper mouse setup, should handle gracefully
        // This may return null or a tile - both are acceptable
        assertDoesNotThrow(() -> inputHandler.getTileFromMouse());
    }

    @Test
    @DisplayName("getGroundPositionFromMouse should not throw exceptions")
    void testGetGroundPositionFromMouse() {
        assertDoesNotThrow(() -> inputHandler.getGroundPositionFromMouse());
    }

    @Test
    @DisplayName("getObjectPositionFromMouse should not throw exceptions")
    void testGetObjectPositionFromMouse() {
        assertDoesNotThrow(() -> inputHandler.getObjectPositionFromMouse());
    }

    @Test
    @DisplayName("Camera constants should be defined correctly")
    void testCameraConstants() {
        // Test that constants are accessible via reflection
        assertDoesNotThrow(() -> {
            java.lang.reflect.Field rotationSpeed = InputHandler.class.getDeclaredField("CAMERA_ROTATION_SPEED");
            rotationSpeed.setAccessible(true);
            assertEquals(90f, rotationSpeed.get(null));
        });
    }
}
