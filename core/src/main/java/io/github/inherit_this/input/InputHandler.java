package io.github.inherit_this.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import io.github.inherit_this.entities.Player;
import io.github.inherit_this.util.Constants;

/**
 * Handles all player input including mouse clicks, keyboard, and camera controls.
 * Extracted from GameScreen to reduce complexity.
 */
public class InputHandler {
    private final PerspectiveCamera camera;
    private final Player player;

    // Camera control
    private static final float CAMERA_ROTATION_SPEED = 90f; // degrees per second
    private static final float MIN_CAMERA_DISTANCE = 200f;
    private static final float MAX_CAMERA_DISTANCE = 400f;
    private static final float ZOOM_SPEED = 30f;

    private float cameraDistance = 300f;
    private float cameraAngle = 45f;

    // Input state
    private boolean rotateLeft = false;
    private boolean rotateRight = false;

    public InputHandler(PerspectiveCamera camera, Player player) {
        this.camera = camera;
        this.player = player;
    }

    /**
     * Get the scroll input processor for camera zoom.
     */
    public InputAdapter getScrollProcessor() {
        return new InputAdapter() {
            @Override
            public boolean scrolled(float amountX, float amountY) {
                cameraDistance += amountY * ZOOM_SPEED;
                cameraDistance = Math.max(MIN_CAMERA_DISTANCE, Math.min(MAX_CAMERA_DISTANCE, cameraDistance));
                return true;
            }
        };
    }

    /**
     * Update camera rotation based on Q/E key input.
     */
    public void updateCameraRotation(float delta) {
        if (rotateLeft) {
            cameraAngle += CAMERA_ROTATION_SPEED * delta;
        }
        if (rotateRight) {
            cameraAngle -= CAMERA_ROTATION_SPEED * delta;
        }

        // Normalize angle to 0-360 range
        cameraAngle = cameraAngle % 360f;
        if (cameraAngle < 0) cameraAngle += 360f;
    }

    /**
     * Update camera position to follow the player.
     */
    public void updateCameraPosition() {
        float angleRad = (float) Math.toRadians(cameraAngle);
        float offsetX = (float) Math.cos(angleRad) * cameraDistance;
        float offsetZ = (float) Math.sin(angleRad) * cameraDistance;

        camera.position.set(
            player.getPosition().x + offsetX,
            cameraDistance * 0.6f,
            player.getPosition().y + offsetZ
        );

        camera.lookAt(player.getPosition().x, 0, player.getPosition().y);
        camera.up.set(0, 1, 0);
        camera.update();
    }

    /**
     * Check for camera rotation key inputs.
     */
    public void checkRotationKeys() {
        rotateLeft = Gdx.input.isKeyPressed(Input.Keys.Q);
        rotateRight = Gdx.input.isKeyPressed(Input.Keys.E);
    }

    /**
     * Project mouse position to ground plane in world coordinates.
     * @return World position vector, or null if ray doesn't intersect ground
     */
    public Vector3 getGroundPositionFromMouse() {
        int screenX = Gdx.input.getX();
        int screenY = Gdx.input.getY();

        // Create a ray from the camera through the mouse position
        Vector3 near = camera.unproject(new Vector3(screenX, screenY, 0));
        Vector3 far = camera.unproject(new Vector3(screenX, screenY, 1));
        Vector3 rayDirection = far.sub(near).nor();

        // Intersect ray with ground plane (y = 0)
        if (rayDirection.y >= 0) {
            return null; // Ray pointing up, won't hit ground
        }

        // Calculate intersection point
        float t = -near.y / rayDirection.y;
        float worldX = near.x + rayDirection.x * t;
        float worldZ = near.z + rayDirection.z * t;

        return new Vector3(worldX, 0, worldZ);
    }

    /**
     * Get tile coordinates from mouse position.
     * @return int[2] array with {tileX, tileY}, or null if no intersection
     */
    public int[] getTileFromMouse() {
        Vector3 groundPos = getGroundPositionFromMouse();
        if (groundPos == null) return null;

        int tileX = (int) Math.floor(groundPos.x / Constants.TILE_SIZE);
        int tileY = (int) Math.floor(groundPos.z / Constants.TILE_SIZE);
        return new int[]{tileX, tileY};
    }

    // Getters
    public float getCameraDistance() { return cameraDistance; }
    public float getCameraAngle() { return cameraAngle; }
    public void setCameraDistance(float distance) {
        this.cameraDistance = Math.max(MIN_CAMERA_DISTANCE, Math.min(MAX_CAMERA_DISTANCE, distance));
    }
}
