package io.github.inherit_this.entities;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.inherit_this.items.Equipment;
import io.github.inherit_this.items.Inventory;
import io.github.inherit_this.util.Constants;
import io.github.inherit_this.world.WorldProvider;

public class Player extends Entity {

    // Movement speed in tiles per second (6.25 tiles/sec = 200 pixels/sec at 32px/tile)
    private float speed = 6.25f;
    private boolean noClip = false;
    private WorldProvider world;
    private Inventory inventory;
    private Equipment equipment;
    private PlayerStats stats;

    // Mouse-based movement
    private Vector2 targetPosition = null;
    // Arrival threshold in tiles (0.15625 tiles = 5 pixels at 32px/tile)
    private static final float ARRIVAL_THRESHOLD = 0.15625f;
    private float facingAngle = 0f; // Angle player is facing (in degrees, raw movement angle)
    private int facingDirection = 0; // 8-directional facing: 0=S, 1=SW, 2=W, 3=NW, 4=N, 5=NE, 6=E, 7=SE

    // Collision box size in tiles (player hitbox is about 1 tile)
    private static final float COLLISION_HALF_WIDTH = 0.5f;  // 0.5 tiles = 16 pixels
    private static final float COLLISION_HALF_HEIGHT = 0.5f; // 0.5 tiles = 16 pixels

    /**
     * Creates a new Player at the given tile coordinates.
     * @param x X position in tiles
     * @param y Y position in tiles
     */
    public Player(float x, float y, Texture texture, WorldProvider world) {
        super(texture, x, y);
        this.world = world;
        this.inventory = new Inventory(8, 6); // 8 columns x 6 rows grid
        this.equipment = new Equipment();
        this.stats = new PlayerStats();
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

                // Update facing angle (raw angle in degrees)
                facingAngle = (float)Math.toDegrees(Math.atan2(dy, dx));

                // Calculate 8-directional facing for sprite rotation
                // Convert angle to 8 directions: 0=S, 1=SW, 2=W, 3=NW, 4=N, 5=NE, 6=E, 7=SE
                // Angle ranges: -180 to 180 degrees
                // Adjust so 0 degrees = East, 90 = North, -90 = South, 180/-180 = West
                float adjustedAngle = facingAngle + 180f; // Shift to 0-360 range
                facingDirection = Math.round(adjustedAngle / 45f) % 8;

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
    }

    /**
     * Sets the target position for the player to move toward.
     * @param x Target X position in tiles
     * @param y Target Y position in tiles
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

    /**
     * Gets the 8-directional facing of the player.
     * @return Direction index: 0=S, 1=SW, 2=W, 3=NW, 4=N, 5=NE, 6=E, 7=SE
     */
    public int getFacingDirection() {
        return facingDirection;
    }

    /**
     * Checks if the player collides with solid tiles at the given position.
     * @param x X position in tiles
     * @param y Y position in tiles
     * @return true if colliding with any solid tile
     */
    private boolean isColliding(float x, float y) {
        // Check collision at the four corners of the player's hitbox
        // Positions are in tiles, world.isSolidAtPosition expects pixel coordinates
        float pixelX = x * Constants.TILE_SIZE;
        float pixelY = y * Constants.TILE_SIZE;
        float halfWidthPixels = COLLISION_HALF_WIDTH * Constants.TILE_SIZE;
        float halfHeightPixels = COLLISION_HALF_HEIGHT * Constants.TILE_SIZE;

        boolean topLeft = world.isSolidAtPosition(pixelX - halfWidthPixels, pixelY + halfHeightPixels);
        boolean topRight = world.isSolidAtPosition(pixelX + halfWidthPixels, pixelY + halfHeightPixels);
        boolean bottomLeft = world.isSolidAtPosition(pixelX - halfWidthPixels, pixelY - halfHeightPixels);
        boolean bottomRight = world.isSolidAtPosition(pixelX + halfWidthPixels, pixelY - halfHeightPixels);

        return topLeft || topRight || bottomLeft || bottomRight;
    }


    /**
     * Sets the player's position in tile coordinates.
     * @param x X position in tiles
     * @param y Y position in tiles
     */
    public void setPosition(float x, float y) {
        position.set(x, y);
    }

    /**
     * Sets the player's position to the center of a specific tile.
     * @param x Tile X coordinate
     * @param y Tile Y coordinate
     */
    public void setTilePosition(int x, int y) {
        // Position is already in tiles, so just set it directly
        // Add 0.5 to center the player on the tile
        position.x = x + 0.5f;
        position.y = y + 0.5f;
    }

    /**
     * Gets the current tile X coordinate the player is on.
     */
    public int getTileX() {
        return (int) Math.floor(position.x);
    }

    /**
     * Gets the current tile Y coordinate the player is on.
     */
    public int getTileY() {
        return (int) Math.floor(position.y);
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

}
