package io.github.inherit_this.entities;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.inherit_this.items.Equipment;
import io.github.inherit_this.items.Inventory;
import io.github.inherit_this.rendering.Billboard;
import io.github.inherit_this.util.Constants;
import io.github.inherit_this.world.World;

public class Player extends Entity {

    private float speed = 200f;
    private boolean noClip = false;
    private World world;
    private Inventory inventory;
    private Equipment equipment;
    private PlayerStats stats;
    private Billboard billboard;
    private float billboardZ = 1f;

    // Mouse-based movement
    private Vector2 targetPosition = null;
    private static final float ARRIVAL_THRESHOLD = 5f; // How close to target before stopping
    private float facingAngle = 0f; // Angle player is facing (in degrees)

    public Player(float x, float y, Texture texture, World world) {
        super(texture, x, y);
        this.world = world;
        this.inventory = new Inventory(8, 6); // 8 columns x 6 rows grid
        this.equipment = new Equipment();
        this.stats = new PlayerStats();

        // Create billboard for 3D rendering (64x64 sprite)
        this.billboard = new Billboard(texture, 22f, 56f);
        // Position billboard at billboardZ height
        this.billboard.setPosition(x, y, billboardZ);
    }

    public void update(float delta) {
        // Regenerate stamina over time
        stats.regenerateStamina(delta);

        // If we have a target position, move toward it
        if (targetPosition != null) {
            float dx = targetPosition.x - position.x;
            float dy = targetPosition.y - position.y;
            float distance = (float)Math.sqrt(dx*dx + dy*dy);

            // Check if we've arrived at target
            if (distance < ARRIVAL_THRESHOLD) {
                targetPosition = null;
            } else {
                // Normalize direction
                dx /= distance;
                dy /= distance;

                // Update facing angle
                facingAngle = (float)Math.toDegrees(Math.atan2(dy, dx));

                // Calculate movement
                float moveX = dx * speed * delta;
                float moveY = dy * speed * delta;

                if (noClip) {
                    position.x += moveX;
                    position.y += moveY;
                } else {
                    float newX = position.x + moveX;
                    float newY = position.y + moveY;

                    if (!isColliding(newX, newY)) {
                        position.x = newX;
                        position.y = newY;
                    } else {
                        // Try sliding along obstacles
                        if (!isColliding(newX, position.y)) {
                            position.x = newX;
                        }
                        if (!isColliding(position.x, newY)) {
                            position.y = newY;
                        }
                    }
                }
            }
        }

        // Update billboard position in 3D space (raised to stand on ground)
        billboard.setPosition(position.x, position.y, billboardZ);
    }

    /**
     * Sets the target position for the player to move toward.
     */
    public void setTargetPosition(float x, float y) {
        if (targetPosition == null) {
            targetPosition = new Vector2();
        }
        targetPosition.set(x, y);
    }

    /**
     * Clears the target position, stopping the player.
     */
    public void stopMoving() {
        targetPosition = null;
    }

    /**
     * Gets the angle the player is facing in degrees.
     */
    public float getFacingAngle() {
        return facingAngle;
    }

    private boolean isColliding(float x, float y) {
        float halfWidth = width / 2f;
        float halfHeight = height / 2f;

        boolean topLeft = world.isSolidAtPosition(x - halfWidth, y + halfHeight);
        boolean topRight = world.isSolidAtPosition(x + halfWidth, y + halfHeight);
        boolean bottomLeft = world.isSolidAtPosition(x - halfWidth, y - halfHeight);
        boolean bottomRight = world.isSolidAtPosition(x + halfWidth, y - halfHeight);

        return topLeft || topRight || bottomLeft || bottomRight;
    }

    /**
     * Renders the player as a billboard sprite in 3D space.
     * The sprite always faces the camera.
     */
    public void renderPlayer(ModelBatch batch, Camera camera) {
        billboard.render(batch, camera);
    }

    public void dispose() {
        billboard.dispose();
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
    }

    public void setTilePosition(int x, int y) {
        position.x = x * Constants.TILE_SIZE;
        position.y = y * Constants.TILE_SIZE;
    }
    
    public void setNoClip(boolean enabled) {
        this.noClip = enabled;
    }

    public boolean isNoClip() {
        return noClip;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public PlayerStats getStats() {
        return stats;
    }

    public void setBillboardZ(float z) {
        this.billboardZ = z;
    }

    public float getBillboardZ() {
        return billboardZ;
    }

}
