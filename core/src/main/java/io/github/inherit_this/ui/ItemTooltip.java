package io.github.inherit_this.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.inherit_this.items.Item;
import io.github.inherit_this.items.ItemStats;
import io.github.inherit_this.util.FontManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Renders a tooltip showing detailed information about an item.
 */
public class ItemTooltip {
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;

    // Reusable objects to avoid allocations in render loop
    private final List<String> lines = new ArrayList<>();
    private final List<Color> colors = new ArrayList<>();
    private final com.badlogic.gdx.graphics.g2d.GlyphLayout layout = new com.badlogic.gdx.graphics.g2d.GlyphLayout();

    private static final int PADDING = 8;
    private static final int LINE_HEIGHT = 16;
    private static final Color BACKGROUND_COLOR = new Color(0.1f, 0.1f, 0.1f, 0.95f);
    private static final Color BORDER_COLOR = new Color(0.4f, 0.4f, 0.4f, 1.0f);

    public ItemTooltip() {
        this.shapeRenderer = new ShapeRenderer();
        this.font = FontManager.getInstance().getTooltipFont();
    }

    /**
     * Render tooltip for an item at mouse position (no value shown).
     */
    public void render(SpriteBatch batch, Item item, float mouseX, float mouseY) {
        render(batch, item, mouseX, mouseY, false);
    }

    /**
     * Render tooltip for an item at mouse position.
     * @param showValue If true, shows item value (for shop interfaces)
     */
    public void render(SpriteBatch batch, Item item, float mouseX, float mouseY, boolean showValue) {
        if (item == null) return;

        // Clear reusable lists to avoid allocations
        lines.clear();
        colors.clear();

        // Item name (colored by rarity)
        lines.add(item.getName());
        colors.add(item.getRarity().getColor());

        // Show damage and attack speed if weapon
        ItemStats stats = item.getStats();
        if (stats != null && stats.getDamage() > 0) {
            lines.add("Damage: " + stats.getDamage());
            colors.add(Color.WHITE);

            if (stats.getAttackSpeed() > 0) {
                lines.add("Attack Speed: " + String.format("%.1f", stats.getAttackSpeed()));
                colors.add(Color.LIGHT_GRAY);
            }
        }

        // Show value only in shop interfaces
        if (showValue) {
            if (lines.size() > 1) {
                lines.add("");
                colors.add(Color.WHITE);
            }
            lines.add("Value: " + item.getValue() + " gold");
            colors.add(new Color(0.8f, 0.6f, 0.2f, 1.0f)); // Gold color
        }

        // Calculate tooltip dimensions
        float maxWidth = 0;
        for (String line : lines) {
            float width = getTextWidth(line);
            if (width > maxWidth) {
                maxWidth = width;
            }
        }

        float tooltipWidth = maxWidth + PADDING * 2;
        float tooltipHeight = lines.size() * LINE_HEIGHT + PADDING * 2;

        // Position tooltip (avoid going off screen)
        float tooltipX = mouseX + 15; // Offset from mouse
        float tooltipY = mouseY - tooltipHeight;

        // Adjust if off-screen
        if (tooltipX + tooltipWidth > com.badlogic.gdx.Gdx.graphics.getWidth()) {
            tooltipX = mouseX - tooltipWidth - 5;
        }
        if (tooltipY < 0) {
            tooltipY = 0;
        }

        batch.end(); // End sprite batch to draw shapes

        // Draw background
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(BACKGROUND_COLOR);
        shapeRenderer.rect(tooltipX, tooltipY, tooltipWidth, tooltipHeight);
        shapeRenderer.end();

        // Draw border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(BORDER_COLOR);
        shapeRenderer.rect(tooltipX, tooltipY, tooltipWidth, tooltipHeight);
        shapeRenderer.end();

        batch.begin(); // Resume sprite batch for text

        // Draw text lines (vertically centered within each line)
        float textY = tooltipY + tooltipHeight - PADDING - (LINE_HEIGHT / 2f) - (font.getCapHeight() / 2f);
        for (int i = 0; i < lines.size(); i++) {
            font.setColor(colors.get(i));
            font.draw(batch, lines.get(i), tooltipX + PADDING, textY);
            textY -= LINE_HEIGHT;
        }
    }

    private float getTextWidth(String text) {
        layout.setText(font, text);
        return layout.width;
    }

    public void dispose() {
        shapeRenderer.dispose();
        // Don't dispose font - it's owned by FontManager singleton
    }
}
