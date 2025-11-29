package io.github.inherit_this.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import java.util.*;

/**
 * Centralized sound manager for playing sound effects.
 * Supports multiple sound variants per type for variety.
 */
public class SoundManager {

    private static SoundManager instance;

    private Map<SoundType, List<Sound>> sounds;
    private float masterVolume = 1.0f;
    private float areaVolume = 1.0f;      // Ambient/environmental sounds
    private float uiVolume = 1.0f;        // UI interaction sounds
    private float primaryVolume = 1.0f;   // Combat, objects, loot
    private boolean muted = false;
    private Random random;

    private SoundManager() {
        sounds = new HashMap<>();
        random = new Random();
        loadAllSounds();
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    /**
     * Loads all sound files. For now, we'll use placeholder sounds.
     * In production, replace with actual audio files.
     */
    private void loadAllSounds() {
        Gdx.app.log("SoundManager", "Initializing sound system...");

        // Try to load sounds, but don't crash if they don't exist
        // This allows the game to run without sound files present

        // UI Sounds
        tryLoadSound(SoundType.UI_CLICK, "sounds/ui/click.wav", "sounds/ui/click.ogg");
        tryLoadSound(SoundType.UI_HOVER, "sounds/ui/hover.wav", "sounds/ui/hover.ogg");
        tryLoadSound(SoundType.INVENTORY_PICKUP, "sounds/inventory/pickup.wav");
        tryLoadSound(SoundType.INVENTORY_DROP, "sounds/inventory/drop.wav");
        tryLoadSound(SoundType.INVENTORY_EQUIP, "sounds/inventory/equip.wav");
        tryLoadSound(SoundType.INVENTORY_MOVE, "sounds/inventory/move.wav");

        // Combat Sounds
        tryLoadSound(SoundType.ATTACK_SWING, "sounds/combat/swing1.wav", "sounds/combat/swing2.wav");
        tryLoadSound(SoundType.ATTACK_HIT, "sounds/combat/hit1.wav", "sounds/combat/hit2.wav");
        tryLoadSound(SoundType.DAMAGE_TAKEN, "sounds/combat/damage.wav");
        tryLoadSound(SoundType.ENEMY_DEATH, "sounds/combat/death.wav");

        // Object Interaction
        tryLoadSound(SoundType.OBJECT_BREAK_WOOD, "sounds/objects/wood_break1.wav", "sounds/objects/wood_break2.wav");
        tryLoadSound(SoundType.OBJECT_BREAK_CERAMIC, "sounds/objects/ceramic_break1.wav", "sounds/objects/ceramic_break2.wav");
        tryLoadSound(SoundType.OBJECT_BREAK_METAL, "sounds/objects/metal_break.wav");
        tryLoadSound(SoundType.CHEST_OPEN, "sounds/objects/chest_open.wav");

        // Loot Sounds
        tryLoadSound(SoundType.LOOT_GOLD, "sounds/loot/gold1.wav", "sounds/loot/gold2.wav");
        tryLoadSound(SoundType.LOOT_ITEM, "sounds/loot/item.wav");

        // Player Actions
        tryLoadSound(SoundType.FOOTSTEP, "sounds/player/footstep1.wav", "sounds/player/footstep2.wav");
        tryLoadSound(SoundType.LEVEL_UP, "sounds/player/levelup.wav");

        // Map Editor
        tryLoadSound(SoundType.EDITOR_PLACE, "sounds/editor/place.wav");
        tryLoadSound(SoundType.EDITOR_DELETE, "sounds/editor/delete.wav");
        tryLoadSound(SoundType.EDITOR_MODE_SWITCH, "sounds/editor/switch.wav");

        Gdx.app.log("SoundManager", "Sound system initialized with " + sounds.size() + " sound types");
    }

    /**
     * Attempts to load one or more sound file paths for a sound type.
     * Silently fails if files don't exist (for development without assets).
     */
    private void tryLoadSound(SoundType type, String... paths) {
        List<Sound> loadedSounds = new ArrayList<>();

        for (String path : paths) {
            try {
                if (Gdx.files.internal(path).exists()) {
                    Sound sound = Gdx.audio.newSound(Gdx.files.internal(path));
                    loadedSounds.add(sound);
                }
            } catch (Exception e) {
                // Silently skip - sound file doesn't exist yet
            }
        }

        if (!loadedSounds.isEmpty()) {
            sounds.put(type, loadedSounds);
            Gdx.app.log("SoundManager", "Loaded " + loadedSounds.size() + " variant(s) for " + type);
        }
    }

    /**
     * Plays a sound effect at default volume.
     */
    public void play(SoundType type) {
        play(type, 1.0f);
    }

    /**
     * Plays a sound effect at specified volume.
     * @param type The sound type to play
     * @param volume Volume multiplier (0.0 - 1.0)
     */
    public void play(SoundType type, float volume) {
        play(type, volume, 1.0f);
    }

    /**
     * Plays a sound effect with volume and pitch control.
     * @param type The sound type to play
     * @param volume Volume multiplier (0.0 - 1.0)
     * @param pitch Pitch multiplier (0.5 - 2.0 typically)
     */
    public void play(SoundType type, float volume, float pitch) {
        if (muted || !sounds.containsKey(type)) {
            return;
        }

        List<Sound> soundList = sounds.get(type);
        if (soundList.isEmpty()) {
            return;
        }

        // Pick a random variant if multiple exist
        Sound sound = soundList.get(random.nextInt(soundList.size()));

        // Apply volume settings based on sound category
        float categoryVolume = getCategoryVolume(type);
        float finalVolume = volume * categoryVolume * masterVolume;
        sound.play(finalVolume, pitch, 0); // pan = 0 (center)
    }

    /**
     * Gets the appropriate volume for a sound type's category.
     */
    private float getCategoryVolume(SoundType type) {
        switch (type) {
            // UI sounds
            case UI_CLICK:
            case UI_HOVER:
            case INVENTORY_PICKUP:
            case INVENTORY_DROP:
            case INVENTORY_EQUIP:
            case INVENTORY_MOVE:
            case EDITOR_PLACE:
            case EDITOR_DELETE:
            case EDITOR_MODE_SWITCH:
                return uiVolume;

            // Area/ambient sounds
            case FOOTSTEP:
                return areaVolume;

            // Primary sounds (combat, objects, loot)
            case ATTACK_SWING:
            case ATTACK_HIT:
            case DAMAGE_TAKEN:
            case ENEMY_DEATH:
            case OBJECT_BREAK_WOOD:
            case OBJECT_BREAK_CERAMIC:
            case OBJECT_BREAK_METAL:
            case CHEST_OPEN:
            case LOOT_GOLD:
            case LOOT_ITEM:
            case LEVEL_UP:
                return primaryVolume;

            default:
                return primaryVolume;
        }
    }

    /**
     * Plays a sound with slight random pitch variation for variety.
     */
    public void playWithVariation(SoundType type) {
        playWithVariation(type, 1.0f);
    }

    /**
     * Plays a sound with slight random pitch variation.
     * @param type The sound type
     * @param volume Volume multiplier
     */
    public void playWithVariation(SoundType type, float volume) {
        float pitch = 0.9f + random.nextFloat() * 0.2f; // 0.9 - 1.1 pitch range
        play(type, volume, pitch);
    }

    /**
     * Sets master volume (affects all sounds).
     * @param volume 0.0 (silent) to 1.0 (full volume)
     */
    public void setMasterVolume(float volume) {
        this.masterVolume = Math.max(0f, Math.min(1f, volume));
    }

    /**
     * Sets area/ambient sound effects volume.
     * @param volume 0.0 (silent) to 1.0 (full volume)
     */
    public void setAreaVolume(float volume) {
        this.areaVolume = Math.max(0f, Math.min(1f, volume));
    }

    /**
     * Sets UI sound effects volume.
     * @param volume 0.0 (silent) to 1.0 (full volume)
     */
    public void setUiVolume(float volume) {
        this.uiVolume = Math.max(0f, Math.min(1f, volume));
    }

    /**
     * Sets primary sound effects volume (combat, objects, loot).
     * @param volume 0.0 (silent) to 1.0 (full volume)
     */
    public void setPrimaryVolume(float volume) {
        this.primaryVolume = Math.max(0f, Math.min(1f, volume));
    }

    /**
     * Toggles mute on/off.
     */
    public void toggleMute() {
        muted = !muted;
    }

    /**
     * Sets mute state.
     */
    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public boolean isMuted() {
        return muted;
    }

    public float getMasterVolume() {
        return masterVolume;
    }

    public float getAreaVolume() {
        return areaVolume;
    }

    public float getUiVolume() {
        return uiVolume;
    }

    public float getPrimaryVolume() {
        return primaryVolume;
    }

    /**
     * Disposes all loaded sounds.
     */
    public void dispose() {
        for (List<Sound> soundList : sounds.values()) {
            for (Sound sound : soundList) {
                sound.dispose();
            }
        }
        sounds.clear();
        Gdx.app.log("SoundManager", "Sound system disposed");
    }
}
