package io.github.inherit_this.items;

import com.badlogic.gdx.graphics.Color;

/**
 * Rarity tiers for items.
 * Each rarity has an associated color for UI display.
 */
public enum ItemRarity {
    COMMON(Color.WHITE),
    UNCOMMON(Color.GREEN),
    RARE(Color.BLUE),
    EPIC(Color.PURPLE),
    LEGENDARY(Color.ORANGE);

    private final Color color;

    ItemRarity(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
