package io.github.inherit_this.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Texture;

/**
 * Manages fonts for the game. Provides pixel-perfect fonts for UI elements.
 * Singleton pattern to ensure fonts are loaded once and shared across components.
 * Falls back on a default in case I mess something up :)
 */
public class FontManager {
    private static FontManager instance;

    private BitmapFont uiFont;
    private BitmapFont consoleFont;
    private BitmapFont tooltipFont;

    private FontManager() {
        loadFonts();
    }

    public static FontManager getInstance() {
        if (instance == null) {
            instance = new FontManager();
        }
        return instance;
    }

    private void loadFonts() {
        String customFontPath = "fonts/pixel.fnt";
        if (Gdx.files.internal(customFontPath).exists()) {
            uiFont = new BitmapFont(Gdx.files.internal(customFontPath));
            uiFont.getData().setScale(2.0f);
            uiFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

            consoleFont = new BitmapFont(Gdx.files.internal(customFontPath));
            consoleFont.getData().setScale(2.0f);
            consoleFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

            tooltipFont = new BitmapFont(Gdx.files.internal(customFontPath));
            tooltipFont.getData().setScale(0.45f);
            tooltipFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        } else {
            // Fallback to default LibGDX font with pixel-perfect rendering
            uiFont = new BitmapFont();
            uiFont.getData().setScale(1.0f);
            uiFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            uiFont.getData().markupEnabled = true;

            consoleFont = new BitmapFont();
            consoleFont.getData().setScale(1.0f);
            consoleFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

            tooltipFont = new BitmapFont();
            tooltipFont.getData().setScale(0.9f);
            tooltipFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

            System.out.println("Custom pixel font not found at: " + customFontPath);
            System.out.println("Using default font.");
        }
    }

    public BitmapFont getUIFont() {
        return uiFont;
    }

    public BitmapFont getConsoleFont() {
        return consoleFont;
    }

    public BitmapFont getTooltipFont() {
        return tooltipFont;
    }

    public void dispose() {
        if (uiFont != null) uiFont.dispose();
        if (consoleFont != null) consoleFont.dispose();
        if (tooltipFont != null) tooltipFont.dispose();
    }
}
