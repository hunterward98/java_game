package io.github.inherit_this.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.Texture;

/**
 * Manages fonts for the game. Provides pixel-perfect fonts for UI elements.
 * Singleton pattern to ensure fonts are loaded once and shared across components.
 * Falls back on a default in case I mess something up :)
 */
public class FontManager {
    private static FontManager instance;

    private BitmapFont uiFont;          // For general UI labels (equipment, inventory titles)
    private BitmapFont consoleFont;     // For debug console
    private BitmapFont tooltipFont;     // For tooltips
    private BitmapFont menuFont;        // For menu buttons
    private BitmapFont hotbarFont;      // For hotbar slot numbers and item counts
    private BitmapFont inventoryFont;   // For inventory item counts (smaller, fits in cells)

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
        String ttfFontPath = "fonts/m3x6.ttf";

        if (Gdx.files.internal(ttfFontPath).exists()) {
            // Use FreeType to generate crisp fonts at exact sizes needed
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(ttfFontPath));
            FreeTypeFontParameter parameter = new FreeTypeFontParameter();

            // Enable pixel-perfect rendering for crisp pixel fonts
            parameter.minFilter = Texture.TextureFilter.Nearest;
            parameter.magFilter = Texture.TextureFilter.Nearest;
            parameter.hinting = FreeTypeFontGenerator.Hinting.Full;
            parameter.mono = false;

            // All in-game UI uses 30px (multiple of 6 for m3x6 font)
            parameter.size = 30;
            uiFont = generator.generateFont(parameter);

            parameter.size = 30;
            consoleFont = generator.generateFont(parameter);

            parameter.size = 30;
            tooltipFont = generator.generateFont(parameter);

            // Menu font slightly larger
            parameter.size = 36;
            menuFont = generator.generateFont(parameter);

            parameter.size = 30;
            hotbarFont = generator.generateFont(parameter);

            parameter.size = 30;
            inventoryFont = generator.generateFont(parameter);

            generator.dispose();

            System.out.println("Loaded fonts using FreeType from: " + ttfFontPath);
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

            menuFont = new BitmapFont();
            menuFont.getData().setScale(1.0f);
            menuFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

            hotbarFont = new BitmapFont();
            hotbarFont.getData().setScale(1.0f);
            hotbarFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

            inventoryFont = new BitmapFont();
            inventoryFont.getData().setScale(0.5f);
            inventoryFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

            System.out.println("Custom pixel font not found at: " + ttfFontPath);
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

    public BitmapFont getMenuFont() {
        return menuFont;
    }

    public BitmapFont getHotbarFont() {
        return hotbarFont;
    }

    public BitmapFont getInventoryFont() {
        return inventoryFont;
    }

    public void dispose() {
        if (uiFont != null) uiFont.dispose();
        if (consoleFont != null) consoleFont.dispose();
        if (tooltipFont != null) tooltipFont.dispose();
        if (menuFont != null) menuFont.dispose();
        if (hotbarFont != null) hotbarFont.dispose();
        if (inventoryFont != null) inventoryFont.dispose();
    }
}
