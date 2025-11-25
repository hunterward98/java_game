package io.github.inherit_this.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

/**
 * Generates placeholder textures for items when actual art assets don't exist.
 * This is temporary until real item icons are created.
 */
public class PlaceholderTextureGenerator {

    /**
     * Creates a simple colored square texture with a border.
     */
    public static Texture createPlaceholder(Color fillColor, int size) {
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);

        // Fill with transparent background
        pixmap.setColor(0, 0, 0, 0);
        pixmap.fill();

        // Draw filled rectangle
        pixmap.setColor(fillColor);
        pixmap.fillRectangle(2, 2, size - 4, size - 4);

        // Draw black border
        pixmap.setColor(Color.BLACK);
        pixmap.drawRectangle(1, 1, size - 2, size - 2);

        // Draw inner highlight
        Color highlight = new Color(
            Math.min(fillColor.r + 0.15f, 1f),
            Math.min(fillColor.g + 0.15f, 1f),
            Math.min(fillColor.b + 0.15f, 1f),
            1f
        );
        pixmap.setColor(highlight);
        pixmap.drawRectangle(3, 3, size - 6, size - 6);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        return texture;
    }

    /**
     * Get a placeholder texture for a specific item type.
     */
    public static Texture getPlaceholderForItem(String itemId) {
        Color color;

        switch (itemId) {
            case "iron_sword":
            case "steel_axe":
            case "iron_chestplate":
                color = Color.GRAY;
                break;
            case "leather_helmet":
            case "wood":
            case "pickaxe":
            case "fishing_rod":
                color = new Color(0.545f, 0.353f, 0.169f, 1f); // Brown
                break;
            case "health_potion":
                color = Color.RED;
                break;
            case "bread":
                color = new Color(0.824f, 0.706f, 0.549f, 1f); // Tan
                break;
            case "iron_ore":
                color = Color.DARK_GRAY;
                break;
            case "gold_ore":
                color = Color.GOLD;
                break;
            default:
                color = Color.WHITE;
                break;
        }

        return createPlaceholder(color, 32);
    }
}
