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
    private static final float MIN_CAMERA_PITCH = 15f;  // Minimum vertical angle (looking more down)
    private static final float MAX_CAMERA_PITCH = 75f;  // Maximum vertical angle (looking more up)

    private float cameraDistance = 300f;
    private float cameraAngle = 45f;      // Horizontal rotation
    private float cameraPitch = 45f;      // Vertical angle (elevation)

    // Input state
    private boolean rotateLeft = false;
    private boolean rotateRight = false;
    private boolean tiltUp = false;
    private boolean tiltDown = false;

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
     * Update camera rotation and tilt based on key input.
     */
    public void updateCameraRotation(float delta) {
        // Horizontal rotation with LEFT/RIGHT
        if (rotateLeft) {
            cameraAngle += CAMERA_ROTATION_SPEED * delta;
        }
        if (rotateRight) {
            cameraAngle -= CAMERA_ROTATION_SPEED * delta;
        }

        // Vertical tilt with UP/DOWN
        if (tiltUp) {
            cameraPitch += CAMERA_ROTATION_SPEED * delta;
            cameraPitch = Math.min(MAX_CAMERA_PITCH, cameraPitch);
        }
        if (tiltDown) {
            cameraPitch -= CAMERA_ROTATION_SPEED * delta;
            cameraPitch = Math.max(MIN_CAMERA_PITCH, cameraPitch);
        }

        // Normalize horizontal angle to 0-360 range
        cameraAngle = cameraAngle % 360f;
        if (cameraAngle < 0) cameraAngle += 360f;
    }

    /**
     * Update camera position to follow the player.
     * Uses spherical coordinates: horizontal angle, vertical angle (pitch), and distance.
     */
    public void updateCameraPosition() {
        // Convert angles to radians
        float horizontalRad = (float) Math.toRadians(cameraAngle);
        float pitchRad = (float) Math.toRadians(cameraPitch);

        // Calculate camera offset using spherical coordinates
        // x = distance * cos(pitch) * cos(azimuth)
        // y = distance * sin(pitch)
        // z = distance * cos(pitch) * sin(azimuth)
        float horizontalDistance = cameraDistance * (float) Math.cos(pitchRad);
        float offsetX = horizontalDistance * (float) Math.cos(horizontalRad);
        float offsetY = cameraDistance * (float) Math.sin(pitchRad);
        float offsetZ = horizontalDistance * (float) Math.sin(horizontalRad);

        // Convert player position from tiles to pixels for 3D camera
        float playerPixelX = player.getPosition().x * Constants.TILE_SIZE;
        float playerPixelZ = player.getPosition().y * Constants.TILE_SIZE;

        camera.position.set(
            playerPixelX + offsetX,
            offsetY,
            playerPixelZ + offsetZ
        );

        camera.lookAt(playerPixelX, 0, playerPixelZ);
        camera.up.set(0, 1, 0);
        camera.update();
    }

    /**
     * Check for camera rotation and tilt key inputs.
     */
    public void checkRotationKeys() {
        rotateLeft = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        rotateRight = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        tiltUp = Gdx.input.isKeyPressed(Input.Keys.UP);
        tiltDown = Gdx.input.isKeyPressed(Input.Keys.DOWN);
    }

    /**
     * Project mouse position to ground plane in world coordinates.
     * @return World position vector, or null if ray doesn't intersect ground
     */
    public Vector3 getGroundPositionFromMouse() {
        return getPlanePositionFromMouse(0);
    }

    /**
     * Project mouse position to a horizontal plane at the given Y height.
     * @param planeY The Y coordinate of the plane to intersect with
     * @return World position vector, or null if ray doesn't intersect plane
     */
    private Vector3 getPlanePositionFromMouse(float planeY) {
        int screenX = Gdx.input.getX();
        int screenY = Gdx.input.getY();

        // Create a ray from the camera through the mouse position
        Vector3 near = camera.unproject(new Vector3(screenX, screenY, 0));
        Vector3 far = camera.unproject(new Vector3(screenX, screenY, 1));
        Vector3 rayDirection = far.sub(near).nor();

        // Intersect ray with horizontal plane at planeY
        float deltaY = planeY - near.y;
        if (Math.abs(rayDirection.y) < 0.0001f) {
            return null; // Ray parallel to plane
        }

        // Calculate intersection point
        float t = deltaY / rayDirection.y;
        if (t < 0) {
            return null; // Intersection behind camera
        }

        float worldX = near.x + rayDirection.x * t;
        float worldZ = near.z + rayDirection.z * t;

        return new Vector3(worldX, planeY, worldZ);
    }

    /**
     * Get mouse position projected onto the object plane (raised above ground).
     * This is better for detecting 3D objects that are rendered above the ground.
     * @return World position vector at object height, or null if no intersection
     */
    public Vector3 getObjectPositionFromMouse() {
        // Objects are rendered at TILE_SIZE/2 above ground
        return getPlanePositionFromMouse(Constants.TILE_SIZE / 2f);
    }

    /**
     * Get tile coordinates from mouse position.
     * @return int[2] array with {tileX, tileY}, or null if no intersection
     */
    public int[] getTileFromMouse() {
        Vector3 groundPos = getGroundPositionFromMouse();
        if (groundPos == null) return null;

        // groundPos is in pixel coordinates, convert to tiles
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
