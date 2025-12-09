package io.github.inherit_this.entities;

/**
 * Types of interactable objects in the game.
 * Each type opens a different UI or provides different functionality.
 */
public enum InteractableType {
    /**
     * Storage chest - opens storage UI
     */
    CHEST("Chest", "Open Storage"),

    /**
     * Crafting workbench - opens crafting UI
     */
    WORKBENCH("Workbench", "Craft Items"),

    /**
     * Anvil - opens repair/upgrade UI
     */
    ANVIL("Anvil", "Repair & Upgrade"),

    /**
     * Shrine/Altar - opens blessing/buff UI
     */
    SHRINE("Shrine", "Receive Blessing");

    private final String displayName;
    private final String interactPrompt;

    InteractableType(String displayName, String interactPrompt) {
        this.displayName = displayName;
        this.interactPrompt = interactPrompt;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the interaction prompt text (e.g., "Press E to Craft Items")
     */
    public String getInteractPrompt() {
        return "Press E to " + interactPrompt;
    }
}
