package io.github.inherit_this.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;

/**
 * Represents an interactable object in the game world (chest, workbench, etc).
 * Player can press E when near to interact and open a UI.
 */
public class InteractableObject extends Entity {

    private Model model;
    private InteractableType type;
    private boolean interacted;

    /**
     * Creates an interactable 3D object.
     * @param model The 3D model for this object
     * @param texture Fallback texture (used for identification)
     * @param x X position in tiles
     * @param y Y position in tiles
     * @param type Type of interactable (chest, workbench, etc)
     */
    public InteractableObject(Model model, Texture texture, float x, float y, InteractableType type) {
        super(texture, x, y);
        this.model = model;
        this.type = type;
        this.interacted = false;
    }

    /**
     * Called when the player interacts with this object.
     * Returns true if interaction was successful.
     */
    public boolean interact() {
        this.interacted = true;
        // TODO: Open appropriate UI based on type
        return true;
    }

    /**
     * Checks if a world position (in tiles) is within this object's interaction range.
     */
    public boolean isPlayerNear(float playerX, float playerY, float interactDistance) {
        float dx = (position.x + 0.5f) - playerX;
        float dy = (position.y + 0.5f) - playerY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        return distance <= interactDistance;
    }

    /**
     * Checks if a world position (in tiles) is within this object's bounds.
     */
    public boolean contains(float worldX, float worldY) {
        return worldX >= position.x && worldX < position.x + 1 &&
               worldY >= position.y && worldY < position.y + 1;
    }

    // Getters
    public Model getModel() {
        return model;
    }

    public InteractableType getType() {
        return type;
    }

    public boolean hasBeenInteracted() {
        return interacted;
    }

    public void resetInteraction() {
        this.interacted = false;
    }
}
