package io.github.inherit_this.settings;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for InputSettings class
 */
class InputSettingsTest {

    @Test
    @DisplayName("DEFAULT_PAUSE should be ESCAPE")
    void testDefaultPause() {
        assertEquals("ESCAPE", InputSettings.DEFAULT_PAUSE);
    }

    @Test
    @DisplayName("DEFAULT_PAUSE should be public static final")
    void testDefaultPauseModifiers() throws Exception {
        java.lang.reflect.Field field = InputSettings.class.getField("DEFAULT_PAUSE");
        assertTrue(Modifier.isPublic(field.getModifiers()));
        assertTrue(Modifier.isStatic(field.getModifiers()));
        assertTrue(Modifier.isFinal(field.getModifiers()));
    }

    @Test
    @DisplayName("Class should have public constructor")
    void testConstructor() throws Exception {
        Constructor<InputSettings> constructor = InputSettings.class.getDeclaredConstructor();
        assertTrue(Modifier.isPublic(constructor.getModifiers()));
        assertNotNull(constructor.newInstance());
    }

    @Test
    @DisplayName("DEFAULT_PAUSE should not be null or empty")
    void testDefaultPauseNotEmpty() {
        assertNotNull(InputSettings.DEFAULT_PAUSE);
        assertFalse(InputSettings.DEFAULT_PAUSE.isEmpty());
    }
}
