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
    private boolean isDisabled;

    // Styling
    private Color backgroundColor = new Color(0.2f, 0.2f, 0.3f, 0.9f);
    private Color hoverBackgroundColor = new Color(0.3f, 0.3f, 0.5f, 0.9f);
    private Color disabledBackgroundColor = new Color(0.15f, 0.15f, 0.2f, 0.5f);
    private Color borderColor = new Color(0.6f, 0.6f, 0.8f, 1f);
    private Color disabledBorderColor = new Color(0.4f, 0.4f, 0.5f, 0.5f);
    private Color textColor = Color.WHITE;
    private Color disabledTextColor = new Color(0.5f, 0.5f, 0.5f, 0.7f);
    private float padding = 15f;  // Reduced from 20f to 15f (25% smaller buttons)
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
        this.isDisabled = false;
    }

    public boolean contains(float x, float y) {
        boolean contains = bounds.contains(x, y);
        isHovered = contains;
        return contains;
    }

    public void updateHover(float x, float y) {
        if (!isDisabled) {
            isHovered = bounds.contains(x, y);
        } else {
            isHovered = false;
        }
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        // End batch to render shapes
        batch.end();

        // Choose colors based on state
        Color bgColor;
        Color bColor;
        Color tColor;

        if (isDisabled) {
            bgColor = disabledBackgroundColor;
            bColor = disabledBorderColor;
            tColor = disabledTextColor;
        } else if (isHovered) {
            bgColor = hoverBackgroundColor;
            bColor = borderColor;
            tColor = textColor;
        } else {
            bgColor = backgroundColor;
            bColor = borderColor;
            tColor = textColor;
        }

        // Draw button background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(bgColor);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.end();

        // Draw button border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(bColor);
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
        font.setColor(tColor);
        font.draw(batch, text, textX, textY);
    }

    public void setDisabled(boolean disabled) {
        this.isDisabled = disabled;
        if (disabled) {
            this.isHovered = false;
        }
    }

    public boolean isDisabled() {
        return isDisabled;
    }

    public void setText(String text) {
        this.text = text;
        this.layout.setText(font, text);

        // Recalculate bounds based on new text size
        float width = layout.width + padding * 2;
        float height = layout.height + padding * 2;
        float centerX = bounds.x + bounds.width / 2f;
        float centerY = bounds.y + bounds.height / 2f;
        this.bounds.set(centerX - width / 2f, centerY - height / 2f, width, height);
    }

    public void dispose() {
        // Font is managed by FontManager, so we don't dispose it here
    }
}
