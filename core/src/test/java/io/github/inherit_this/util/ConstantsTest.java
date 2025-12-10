package io.github.inherit_this.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Constants class - verify all constants are defined correctly
 */
class ConstantsTest {

    @Test
    @DisplayName("Constructor should be private to prevent instantiation")
    void testPrivateConstructor() throws Exception {
        Constructor<Constants> constructor = Constants.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()),
                "Constructor should be private");

        // Can still instantiate via reflection for coverage
        constructor.setAccessible(true);
        assertNotNull(constructor.newInstance());
    }

    @Test
    @DisplayName("TILE_SIZE should be 32")
    void testTileSize() {
        assertEquals(32, Constants.TILE_SIZE);
    }

    @Test
    @DisplayName("CHUNK_SIZE should be 8")
    void testChunkSize() {
        assertEquals(8, Constants.CHUNK_SIZE);
    }

    @Test
    @DisplayName("CHUNK_PIXEL_SIZE should be TILE_SIZE * CHUNK_SIZE")
    void testChunkPixelSize() {
        assertEquals(Constants.TILE_SIZE * Constants.CHUNK_SIZE, Constants.CHUNK_PIXEL_SIZE);
        assertEquals(256, Constants.CHUNK_PIXEL_SIZE);
    }

    @Test
    @DisplayName("EDGE_BLEND_SIZE should be 4.0f")
    void testEdgeBlendSize() {
        assertEquals(4.0f, Constants.EDGE_BLEND_SIZE);
    }

    @Test
    @DisplayName("PIXEL_SCALE should be 2.0f")
    void testPixelScale() {
        assertEquals(2.0f, Constants.PIXEL_SCALE);
    }

    @Test
    @DisplayName("VIEWPORT_WIDTH should be 1920")
    void testViewportWidth() {
        assertEquals(1920, Constants.VIEWPORT_WIDTH);
    }

    @Test
    @DisplayName("VIEWPORT_HEIGHT should be 1080")
    void testViewportHeight() {
        assertEquals(1080, Constants.VIEWPORT_HEIGHT);
    }

    @Test
    @DisplayName("DEFAULT_PLAYER_SPEED should be 200f")
    void testDefaultPlayerSpeed() {
        assertEquals(200f, Constants.DEFAULT_PLAYER_SPEED);
    }

    @Test
    @DisplayName("DEFAULT_PAUSE_KEY should be ESCAPE")
    void testDefaultPauseKey() {
        assertEquals(com.badlogic.gdx.Input.Keys.ESCAPE, Constants.DEFAULT_PAUSE_KEY);
    }

    @Test
    @DisplayName("All constants should be public static final")
    void testConstantsArePublicStaticFinal() throws Exception {
        java.lang.reflect.Field tileSize = Constants.class.getField("TILE_SIZE");
        assertTrue(Modifier.isPublic(tileSize.getModifiers()));
        assertTrue(Modifier.isStatic(tileSize.getModifiers()));
        assertTrue(Modifier.isFinal(tileSize.getModifiers()));
    }
}
