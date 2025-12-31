package io.github.inherit_this.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.inherit_this.entities.NPC;
import io.github.inherit_this.util.FontManager;

/**
 * Renders a tooltip showing NPC name and health information.
 * Uses only SpriteBatch for rendering to avoid batch state issues.
 */
public class NPCTooltip {
    private final Texture whitePixel;
    private final BitmapFont font;
    private final com.badlogic.gdx.graphics.g2d.GlyphLayout layout = new com.badlogic.gdx.graphics.g2d.GlyphLayout();

    private static final int PADDING = 8;
    private static final int LINE_HEIGHT = 16;
    private static final int BORDER_WIDTH = 2;
    private static final Color BACKGROUND_COLOR = new Color(0.1f, 0.1f, 0.1f, 0.95f);
    private static final Color BORDER_COLOR = new Color(0.4f, 0.4f, 0.4f, 1.0f);
    private static final Color NAME_COLOR = new Color(1.0f, 1.0f, 0.6f, 1.0f); // Light yellow
    private static final Color HEALTH_COLOR = new Color(0.9f, 0.3f, 0.3f, 1.0f); // Red

    public NPCTooltip() {
        this.font = FontManager.getInstance().getTooltipFont();

        // Create a 1x1 white pixel texture for drawing rectangles
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        this.whitePixel = new Texture(pixmap);
        pixmap.dispose();
    }

    /**
     * Render tooltip for an NPC at mouse position.
     * Uses only SpriteBatch - no ShapeRenderer needed.
     */
    public void render(SpriteBatch batch, NPC npc, float mouseX, float mouseY) {
        if (npc == null || npc.isDead()) return;

        // Format NPC name (replace underscores with spaces)
        String displayName = npc.getName().replace("_", " ");
        String healthText = npc.getCurrentHealth() + " / " + npc.getMaxHealth() + " HP";

        // Calculate tooltip dimensions
        float nameWidth = getTextWidth(displayName);
        float healthWidth = getTextWidth(healthText);
        float maxWidth = Math.max(nameWidth, healthWidth);

        float tooltipWidth = maxWidth + PADDING * 2;
        float tooltipHeight = 2 * LINE_HEIGHT + PADDING * 2;

        // Position tooltip (avoid going off screen)
        float tooltipX = mouseX + 15; // Offset from mouse
        float tooltipY = mouseY - tooltipHeight;

        // Adjust if off-screen
        if (tooltipX + tooltipWidth > Gdx.graphics.getWidth()) {
            tooltipX = mouseX - tooltipWidth - 5;
        }
        if (tooltipY < 0) {
            tooltipY = 0;
        }

        // Draw background (filled rectangle)
        batch.setColor(BACKGROUND_COLOR);
        batch.draw(whitePixel, tooltipX, tooltipY, tooltipWidth, tooltipHeight);

        // Draw border (4 lines forming a rectangle)
        batch.setColor(BORDER_COLOR);
        // Top border
        batch.draw(whitePixel, tooltipX, tooltipY + tooltipHeight - BORDER_WIDTH, tooltipWidth, BORDER_WIDTH);
        // Bottom border
        batch.draw(whitePixel, tooltipX, tooltipY, tooltipWidth, BORDER_WIDTH);
        // Left border
        batch.draw(whitePixel, tooltipX, tooltipY, BORDER_WIDTH, tooltipHeight);
        // Right border
        batch.draw(whitePixel, tooltipX + tooltipWidth - BORDER_WIDTH, tooltipY, BORDER_WIDTH, tooltipHeight);

        // Reset batch color to white for text rendering
        batch.setColor(Color.WHITE);

        // Draw NPC name (vertically centered in first line)
        float centerY = tooltipY + tooltipHeight - PADDING - (LINE_HEIGHT / 2f);
        float textY = centerY + (font.getCapHeight() / 2f);
        font.setColor(NAME_COLOR);
        font.draw(batch, displayName, tooltipX + PADDING, textY);

        // Draw health (vertically centered in second line)
        centerY -= LINE_HEIGHT;
        textY = centerY + (font.getCapHeight() / 2f);
        font.setColor(HEALTH_COLOR);
        font.draw(batch, healthText, tooltipX + PADDING, textY);

        // Reset font color to white
        font.setColor(Color.WHITE);
    }

    private float getTextWidth(String text) {
        layout.setText(font, text);
        return layout.width;
    }

    public void dispose() {
        whitePixel.dispose();
        // Don't dispose font - it's owned by FontManager singleton
    }
}
