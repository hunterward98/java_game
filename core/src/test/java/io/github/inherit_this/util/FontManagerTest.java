package io.github.inherit_this.util;

import io.github.inherit_this.LibGdxTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for FontManager - singleton font management system
 */
class FontManagerTest extends LibGdxTestBase {

    private FontManager fontManager;

    @BeforeEach
    void setUp() {
        // Get singleton instance
        fontManager = FontManager.getInstance();
    }

    @Test
    @DisplayName("getInstance should return singleton instance")
    void testGetInstance() {
        FontManager instance1 = FontManager.getInstance();
        FontManager instance2 = FontManager.getInstance();

        assertNotNull(instance1, "Instance should not be null");
        assertSame(instance1, instance2, "Should return same instance");
    }

    @Test
    @DisplayName("getUIFont should return non-null font")
    void testGetUIFont() {
        assertNotNull(fontManager.getUIFont(), "UI font should not be null");
    }

    @Test
    @DisplayName("getConsoleFont should return non-null font")
    void testGetConsoleFont() {
        assertNotNull(fontManager.getConsoleFont(), "Console font should not be null");
    }

    @Test
    @DisplayName("getTooltipFont should return non-null font")
    void testGetTooltipFont() {
        assertNotNull(fontManager.getTooltipFont(), "Tooltip font should not be null");
    }

    @Test
    @DisplayName("getMenuFont should return non-null font")
    void testGetMenuFont() {
        assertNotNull(fontManager.getMenuFont(), "Menu font should not be null");
    }

    @Test
    @DisplayName("getHotbarFont should return non-null font")
    void testGetHotbarFont() {
        assertNotNull(fontManager.getHotbarFont(), "Hotbar font should not be null");
    }

    @Test
    @DisplayName("getInventoryFont should return non-null font")
    void testGetInventoryFont() {
        assertNotNull(fontManager.getInventoryFont(), "Inventory font should not be null");
    }

    @Test
    @DisplayName("All fonts should be different instances")
    void testFontsAreDifferentInstances() {
        assertNotSame(fontManager.getUIFont(), fontManager.getConsoleFont());
        assertNotSame(fontManager.getUIFont(), fontManager.getTooltipFont());
        assertNotSame(fontManager.getUIFont(), fontManager.getMenuFont());
        assertNotSame(fontManager.getUIFont(), fontManager.getHotbarFont());
        assertNotSame(fontManager.getUIFont(), fontManager.getInventoryFont());
    }

    @Test
    @DisplayName("Fonts should have texture filters set to Nearest for pixel-perfect rendering")
    void testFontsHaveNearestFilter() {
        // All fonts should use Nearest filter for crisp pixel art
        assertEquals(
            com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest,
            fontManager.getUIFont().getRegion().getTexture().getMinFilter(),
            "UI font should use Nearest min filter"
        );
        assertEquals(
            com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest,
            fontManager.getUIFont().getRegion().getTexture().getMagFilter(),
            "UI font should use Nearest mag filter"
        );
    }

    @Test
    @DisplayName("dispose should not throw exceptions")
    void testDispose() {
        // Should not throw
        assertDoesNotThrow(() -> fontManager.dispose());
    }

    @Test
    @DisplayName("UI font should be usable for rendering text")
    void testUIFontCanRenderText() {
        // Just verify we can get glyph data
        assertNotNull(fontManager.getUIFont().getData());
        assertTrue(fontManager.getUIFont().getData().glyphs.length > 0);
    }

    @Test
    @DisplayName("Console font should be usable for rendering text")
    void testConsoleFontCanRenderText() {
        assertNotNull(fontManager.getConsoleFont().getData());
        assertTrue(fontManager.getConsoleFont().getData().glyphs.length > 0);
    }

    @Test
    @DisplayName("Tooltip font should be usable for rendering text")
    void testTooltipFontCanRenderText() {
        assertNotNull(fontManager.getTooltipFont().getData());
        assertTrue(fontManager.getTooltipFont().getData().glyphs.length > 0);
    }

    @Test
    @DisplayName("Menu font should be usable for rendering text")
    void testMenuFontCanRenderText() {
        assertNotNull(fontManager.getMenuFont().getData());
        assertTrue(fontManager.getMenuFont().getData().glyphs.length > 0);
    }

    @Test
    @DisplayName("Hotbar font should be usable for rendering text")
    void testHotbarFontCanRenderText() {
        assertNotNull(fontManager.getHotbarFont().getData());
        assertTrue(fontManager.getHotbarFont().getData().glyphs.length > 0);
    }

    @Test
    @DisplayName("Inventory font should be usable for rendering text")
    void testInventoryFontCanRenderText() {
        assertNotNull(fontManager.getInventoryFont().getData());
        assertTrue(fontManager.getInventoryFont().getData().glyphs.length > 0);
    }
}
