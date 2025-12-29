package io.github.inherit_this.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.inherit_this.items.Item;

/**
 * Represents an item that has been dropped on the ground and can be picked up.
 * Used for loot drops from monsters, coins, and items placed in the dungeon.
 */
public class DroppedItem {
    private final Item item;
    private final Vector2 position; // Position in tiles
    private final Texture texture;
    private boolean pickedUp;

    /**
     * Create a dropped item at a specific position.
     * @param item The item that was dropped
     * @param tileX X position in tiles
     * @param tileY Y position in tiles
     * @param texture Texture to display (can be different from item texture)
     */
    public DroppedItem(Item item, float tileX, float tileY, Texture texture) {
        this.item = item;
        this.position = new Vector2(tileX, tileY);
        this.texture = texture;
        this.pickedUp = false;
    }

    /**
     * Create a dropped item using the item's default icon.
     */
    public DroppedItem(Item item, float tileX, float tileY) {
        this(item, tileX, tileY, item.getIcon());
    }

    /**
     * Mark this item as picked up.
     */
    public void pickup() {
        this.pickedUp = true;
    }

    /**
     * Check if the player is close enough to pick up this item.
     * @param playerX Player X position in tiles
     * @param playerY Player Y position in tiles
     * @param pickupRange Maximum pickup distance in tiles
     * @return true if within pickup range
     */
    public boolean isInRange(float playerX, float playerY, float pickupRange) {
        float dx = playerX - position.x;
        float dy = playerY - position.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        return distance <= pickupRange;
    }

    // Getters
    public Item getItem() { return item; }
    public Vector2 getPosition() { return position; }
    public Texture getTexture() { return texture; }
    public boolean isPickedUp() { return pickedUp; }
}
