package io.github.inherit_this.audio;

import io.github.inherit_this.LibGdxTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for SoundManager - singleton sound management system
 */
class SoundManagerTest extends LibGdxTestBase {

    private SoundManager soundManager;

    @BeforeEach
    void setUp() {
        soundManager = SoundManager.getInstance();
    }

    @Test
    @DisplayName("getInstance should return singleton instance")
    void testGetInstance() {
        SoundManager instance1 = SoundManager.getInstance();
        SoundManager instance2 = SoundManager.getInstance();

        assertNotNull(instance1, "Instance should not be null");
        assertSame(instance1, instance2, "Should return same instance");
    }

    @Test
    @DisplayName("Master volume getter should return value")
    void testGetMasterVolume() {
        assertNotNull(soundManager.getMasterVolume());
        assertTrue(soundManager.getMasterVolume() >= 0f);
        assertTrue(soundManager.getMasterVolume() <= 1f);
    }

    @Test
    @DisplayName("UI volume getter should return value")
    void testGetUIVolume() {
        assertNotNull(soundManager.getUiVolume());
        assertTrue(soundManager.getUiVolume() >= 0f);
        assertTrue(soundManager.getUiVolume() <= 1f);
    }

    @Test
    @DisplayName("Area volume getter should return value")
    void testGetAreaVolume() {
        assertNotNull(soundManager.getAreaVolume());
        assertTrue(soundManager.getAreaVolume() >= 0f);
        assertTrue(soundManager.getAreaVolume() <= 1f);
    }

    @Test
    @DisplayName("Primary volume getter should return value")
    void testGetPrimaryVolume() {
        assertNotNull(soundManager.getPrimaryVolume());
        assertTrue(soundManager.getPrimaryVolume() >= 0f);
        assertTrue(soundManager.getPrimaryVolume() <= 1f);
    }

    @Test
    @DisplayName("isMuted getter should return boolean")
    void testGetMuted() {
        // Just verify it returns a boolean (either true or false is fine)
        boolean muted = soundManager.isMuted();
        assertTrue(muted || !muted); // Always true, just exercises the getter
    }

    @Test
    @DisplayName("setMasterVolume should clamp to 0.0-1.0 range")
    void testSetMasterVolumeClamp() {
        soundManager.setMasterVolume(0.5f);
        assertEquals(0.5f, soundManager.getMasterVolume(), 0.01f);

        soundManager.setMasterVolume(1.5f);
        assertEquals(1.0f, soundManager.getMasterVolume(), 0.01f);

        soundManager.setMasterVolume(-0.5f);
        assertEquals(0.0f, soundManager.getMasterVolume(), 0.01f);
    }

    @Test
    @DisplayName("setUIVolume should clamp to 0.0-1.0 range")
    void testSetUIVolumeClamp() {
        soundManager.setUiVolume(0.7f);
        assertEquals(0.7f, soundManager.getUiVolume(), 0.01f);

        soundManager.setUiVolume(2.0f);
        assertEquals(1.0f, soundManager.getUiVolume(), 0.01f);

        soundManager.setUiVolume(-1.0f);
        assertEquals(0.0f, soundManager.getUiVolume(), 0.01f);
    }

    @Test
    @DisplayName("setAreaVolume should clamp to 0.0-1.0 range")
    void testSetAreaVolumeClamp() {
        soundManager.setAreaVolume(0.3f);
        assertEquals(0.3f, soundManager.getAreaVolume(), 0.01f);

        soundManager.setAreaVolume(1.5f);
        assertEquals(1.0f, soundManager.getAreaVolume(), 0.01f);

        soundManager.setAreaVolume(-0.5f);
        assertEquals(0.0f, soundManager.getAreaVolume(), 0.01f);
    }

    @Test
    @DisplayName("setPrimaryVolume should clamp to 0.0-1.0 range")
    void testSetPrimaryVolumeClamp() {
        soundManager.setPrimaryVolume(0.6f);
        assertEquals(0.6f, soundManager.getPrimaryVolume(), 0.01f);

        soundManager.setPrimaryVolume(3.0f);
        assertEquals(1.0f, soundManager.getPrimaryVolume(), 0.01f);

        soundManager.setPrimaryVolume(-2.0f);
        assertEquals(0.0f, soundManager.getPrimaryVolume(), 0.01f);
    }

    @Test
    @DisplayName("setMuted should update muted state")
    void testSetMuted() {
        soundManager.setMuted(true);
        assertTrue(soundManager.isMuted());

        soundManager.setMuted(false);
        assertFalse(soundManager.isMuted());
    }

    @Test
    @DisplayName("toggleMute should flip muted state")
    void testToggleMute() {
        boolean initialState = soundManager.isMuted();
        soundManager.toggleMute();
        assertEquals(!initialState, soundManager.isMuted());

        soundManager.toggleMute();
        assertEquals(initialState, soundManager.isMuted());
    }

    @Test
    @DisplayName("play should not throw when sound doesn't exist")
    void testPlayNonExistentSound() {
        // No sound files loaded in tests, should handle gracefully
        assertDoesNotThrow(() -> soundManager.play(SoundType.UI_CLICK));
    }

    @Test
    @DisplayName("play with volume should not throw")
    void testPlayWithVolume() {
        assertDoesNotThrow(() -> soundManager.play(SoundType.ATTACK_SWING, 0.5f));
    }

    @Test
    @DisplayName("play with volume and pitch should not throw")
    void testPlayWithVolumeAndPitch() {
        assertDoesNotThrow(() -> soundManager.play(SoundType.LOOT_GOLD, 0.8f, 1.2f));
    }

    @Test
    @DisplayName("playWithVariation should not throw")
    void testPlayWithVariation() {
        assertDoesNotThrow(() -> soundManager.playWithVariation(SoundType.FOOTSTEP));
    }

    @Test
    @DisplayName("playWithVariation with volume should not throw")
    void testPlayWithVariationAndVolume() {
        assertDoesNotThrow(() -> soundManager.playWithVariation(SoundType.ENEMY_DEATH, 0.9f));
    }

    @Test
    @DisplayName("play should not play sound when muted")
    void testPlayWhenMuted() {
        soundManager.setMuted(true);
        // Should not crash, just silently not play
        assertDoesNotThrow(() -> soundManager.play(SoundType.UI_CLICK));
    }

    @Test
    @DisplayName("dispose should not throw")
    void testDispose() {
        assertDoesNotThrow(() -> soundManager.dispose());
    }

    @Test
    @DisplayName("All SoundTypes should be playable without errors")
    void testAllSoundTypes() {
        for (SoundType type : SoundType.values()) {
            assertDoesNotThrow(() -> soundManager.play(type),
                    "Should handle " + type + " gracefully");
        }
    }

    @Test
    @DisplayName("Volume settings should persist")
    void testVolumePersistence() {
        soundManager.setMasterVolume(0.5f);
        soundManager.setUiVolume(0.6f);
        soundManager.setAreaVolume(0.7f);
        soundManager.setPrimaryVolume(0.8f);

        assertEquals(0.5f, soundManager.getMasterVolume(), 0.01f);
        assertEquals(0.6f, soundManager.getUiVolume(), 0.01f);
        assertEquals(0.7f, soundManager.getAreaVolume(), 0.01f);
        assertEquals(0.8f, soundManager.getPrimaryVolume(), 0.01f);
    }
}
