package io.github.inherit_this.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class MenuButton {
    public Rectangle bounds;
    private String text;
    private BitmapFont font;
    private GlyphLayout layout;
    private boolean isHovered;

    // Styling
    private Color backgroundColor = new Color(0.2f, 0.2f, 0.3f, 0.9f);
    private Color hoverBackgroundColor = new Color(0.3f, 0.3f, 0.5f, 0.9f);
    private Color borderColor = new Color(0.6f, 0.6f, 0.8f, 1f);
    private Color textColor = Color.WHITE;
    private float padding = 20f;
    private float borderWidth = 2f;

    public MenuButton(String text, BitmapFont font, float x, float y) {
        this.text = text;
        this.font = font;
        this.layout = new GlyphLayout();
        this.layout.setText(font, text);

        // Calculate bounds based on text size plus padding
        float width = layout.width + padding * 2;
        float height = layout.height + padding * 2;
        this.bounds = new Rectangle(x, y, width, height);
        this.isHovered = false;
    }

    public boolean contains(float x, float y) {
        boolean contains = bounds.contains(x, y);
        isHovered = contains;
        return contains;
    }

    public void updateHover(float x, float y) {
        isHovered = bounds.contains(x, y);
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        // End batch to render shapes
        batch.end();

        // Draw button background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(isHovered ? hoverBackgroundColor : backgroundColor);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.end();

        // Draw button border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(borderColor);
        for (int i = 0; i < borderWidth; i++) {
            shapeRenderer.rect(bounds.x + i, bounds.y + i,
                             bounds.width - i * 2, bounds.height - i * 2);
        }
        shapeRenderer.end();

        // Resume batch for text rendering
        batch.begin();

        // Draw centered text
        float textX = bounds.x + (bounds.width - layout.width) / 2;
        float textY = bounds.y + (bounds.height + layout.height) / 2;
        font.setColor(textColor);
        font.draw(batch, text, textX, textY);
    }

    public void dispose() {
        // Font is managed by FontManager, so we don't dispose it here
    }
}
