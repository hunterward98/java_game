package io.github.inherit_this.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.inherit_this.entities.NPC;
import io.github.inherit_this.util.FontManager;

/**
 * Renders a tooltip showing NPC name and health information.
 */
public class NPCTooltip {
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;
    private final com.badlogic.gdx.graphics.g2d.GlyphLayout layout = new com.badlogic.gdx.graphics.g2d.GlyphLayout();

    private static final int PADDING = 8;
    private static final int LINE_HEIGHT = 16;
    private static final Color BACKGROUND_COLOR = new Color(0.1f, 0.1f, 0.1f, 0.95f);
    private static final Color BORDER_COLOR = new Color(0.4f, 0.4f, 0.4f, 1.0f);
    private static final Color NAME_COLOR = new Color(1.0f, 1.0f, 0.6f, 1.0f); // Light yellow
    private static final Color HEALTH_COLOR = new Color(0.9f, 0.3f, 0.3f, 1.0f); // Red

    public NPCTooltip() {
        this.shapeRenderer = new ShapeRenderer();
        this.font = FontManager.getInstance().getTooltipFont();
    }

    /**
     * Render tooltip for an NPC at mouse position.
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

        // Draw NPC name
        float textY = tooltipY + tooltipHeight - PADDING - (LINE_HEIGHT / 2f) - (font.getCapHeight() / 2f);
        font.setColor(NAME_COLOR);
        font.draw(batch, displayName, tooltipX + PADDING, textY);

        // Draw health
        textY -= LINE_HEIGHT;
        font.setColor(HEALTH_COLOR);
        font.draw(batch, healthText, tooltipX + PADDING, textY);
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
