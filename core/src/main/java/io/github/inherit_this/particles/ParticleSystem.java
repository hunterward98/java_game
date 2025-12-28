package io.github.inherit_this.particles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.inherit_this.entities.BreakableObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Manages all active particles in the game.
 * Handles updating, rendering, and cleanup of particles.
 * Renders particles as 2D sprites projected from 3D world positions (like player rendering).
 */
public class ParticleSystem {
    private List<Particle> particles;
    private PerspectiveCamera camera;

    // Particle textures
    private Texture woodParticle1;
    private Texture woodParticle2;
    private Texture stoneParticle1;
    private Texture stoneParticle2;

    // Combat particle textures
    private Texture bloodParticle1;
    private Texture bloodParticle2;
    private Texture bloodParticle3;
    private Texture manaParticle1;
    private Texture manaParticle2;
    private Texture manaParticle3;
    private Texture staminaParticle1;
    private Texture staminaParticle2;
    private Texture staminaParticle3;
    private Texture cursedParticle1;
    private Texture cursedParticle2;

    private static final float PARTICLE_SIZE = 4f; // 4x4 pixels

    public ParticleSystem(PerspectiveCamera camera) {
        this.particles = new ArrayList<>();
        this.camera = camera;

        // Load particle textures
        loadParticleTextures();
    }

    private void loadParticleTextures() {
        try {
            woodParticle1 = new Texture(Gdx.files.internal("particles/wood_particle_1.png"));
            woodParticle2 = new Texture(Gdx.files.internal("particles/wood_particle_2.png"));
            stoneParticle1 = new Texture(Gdx.files.internal("particles/stone_particle_1.png"));
            stoneParticle2 = new Texture(Gdx.files.internal("particles/stone_particle_2.png"));

            // Load combat particle textures
            bloodParticle1 = new Texture(Gdx.files.internal("particles/blood_particle_1.png"));
            bloodParticle2 = new Texture(Gdx.files.internal("particles/blood_particle_2.png"));
            bloodParticle3 = new Texture(Gdx.files.internal("particles/blood_particle_3.png"));
            manaParticle1 = new Texture(Gdx.files.internal("particles/mana_particle_1.png"));
            manaParticle2 = new Texture(Gdx.files.internal("particles/mana_particle_2.png"));
            manaParticle3 = new Texture(Gdx.files.internal("particles/mana_particle_3.png"));
            staminaParticle1 = new Texture(Gdx.files.internal("particles/stamina_particle_1.png"));
            staminaParticle2 = new Texture(Gdx.files.internal("particles/stamina_particle_2.png"));
            staminaParticle3 = new Texture(Gdx.files.internal("particles/stamina_particle_3.png"));
            cursedParticle1 = new Texture(Gdx.files.internal("particles/curse_particle_1.png"));
            cursedParticle2 = new Texture(Gdx.files.internal("particles/curse_particle_2.png"));

            Gdx.app.log("ParticleSystem", "Loaded particle textures");
        } catch (Exception e) {
            Gdx.app.error("ParticleSystem", "Failed to load particle textures", e);
        }
    }

    /**
     * Material types for particles.
     */
    public enum MaterialType {
        WOOD,
        STONE,
        BLOOD,      // Red droplets for health damage
        MANA,       // Blue sparkles for mana drain
        STAMINA,    // Yellow/green particles for stamina drain
        CURSED      // Purple/green dripping particles for cursed mushroom
    }

    /**
     * Creates an explosion effect from a breakable object.
     * Spawns particles that match the object's material type.
     *
     * @param obj The breakable object to get material from
     * @param worldX World X position (in pixels)
     * @param worldY World Y position (in pixels)
     * @param worldZ World Z position (in pixels)
     * @param particleCount Number of particles to spawn
     * @param minSpeed Minimum particle speed (pixels/second)
     * @param maxSpeed Maximum particle speed (pixels/second)
     */
    public void createBreakEffect(BreakableObject obj, float worldX, float worldY, float worldZ,
                                  int particleCount, float minSpeed, float maxSpeed) {
        // Determine material type from object name
        MaterialType materialType = getMaterialType(obj);

        // Get particle textures for this material
        Texture[] textures = getTexturesForMaterial(materialType);
        if (textures == null || textures.length == 0) {
            Gdx.app.error("ParticleSystem", "No textures available for material: " + materialType);
            return;
        }

        // Sample base color from object texture for tinting
        Color baseColor = sampleObjectColor(obj);

        // Spawn particles
        for (int i = 0; i < particleCount; i++) {
            // Randomly pick one of the two texture variants
            Texture particleTexture = textures[(int) (Math.random() * textures.length)];
            TextureRegion textureRegion = new TextureRegion(particleTexture);

            // Random direction (spherical coordinates)
            float angle = (float) (Math.random() * Math.PI * 2);
            float elevation = (float) (Math.random() * Math.PI / 3); // 0 to 60 degrees

            // Random speed
            float speed = minSpeed + (float) Math.random() * (maxSpeed - minSpeed);
            float velocityX = (float) (Math.cos(angle) * Math.cos(elevation)) * speed;
            float velocityY = (float) Math.sin(elevation) * speed;
            float velocityZ = (float) (Math.sin(angle) * Math.cos(elevation)) * speed;

            // Add upward bias
            velocityY += speed * 0.3f;

            // Random lifetime (0.5 to 1.5 seconds)
            float lifetime = 0.5f + (float) Math.random() * 1.0f;

            // Gravity (negative to pull down)
            float gravity = -300f;

            // Apply slight random tint variation (±10% on each color channel)
            Color tintColor = new Color(
                baseColor.r * (0.9f + (float) Math.random() * 0.2f),
                baseColor.g * (0.9f + (float) Math.random() * 0.2f),
                baseColor.b * (0.9f + (float) Math.random() * 0.2f),
                1.0f
            );
            // Clamp to valid range
            tintColor.clamp();

            Particle particle = new Particle(
                textureRegion, worldX, worldY, worldZ,
                velocityX, velocityY, velocityZ,
                lifetime, gravity, tintColor, PARTICLE_SIZE
            );

            particles.add(particle);
        }
    }

    /**
     * Determine material type from breakable object.
     */
    private MaterialType getMaterialType(BreakableObject obj) {
        String name = obj.getName().toLowerCase();

        // Check for wood-based objects
        if (name.contains("crate") || name.contains("barrel") || name.contains("wood")) {
            return MaterialType.WOOD;
        }

        // Check for stone-based objects
        if (name.contains("pot") || name.contains("stone") || name.contains("vase")) {
            return MaterialType.STONE;
        }

        // Default to wood
        return MaterialType.WOOD;
    }

    /**
     * Get particle textures for a material type.
     */
    private Texture[] getTexturesForMaterial(MaterialType materialType) {
        switch (materialType) {
            case WOOD:
                return new Texture[]{woodParticle1, woodParticle2};
            case STONE:
                return new Texture[]{stoneParticle1, stoneParticle2};
            case BLOOD:
                return new Texture[]{bloodParticle1, bloodParticle2, bloodParticle3};
            case MANA:
                return new Texture[]{manaParticle1, manaParticle2, manaParticle3};
            case STAMINA:
                return new Texture[]{staminaParticle1, staminaParticle2, staminaParticle3};
            case CURSED:
                return new Texture[]{cursedParticle1, cursedParticle2};
            default:
                return new Texture[]{woodParticle1, woodParticle2};
        }
    }

    /**
     * Sample a representative color from the object's texture.
     */
    private Color sampleObjectColor(BreakableObject obj) {
        Texture texture = obj.getTexture();
        if (texture == null) {
            return Color.WHITE;
        }

        // For now, return a color based on material type
        // TODO: Actually sample texture pixels if needed
        MaterialType materialType = getMaterialType(obj);
        switch (materialType) {
            case WOOD:
                return new Color(0.8f, 0.6f, 0.4f, 1.0f); // Brown-ish
            case STONE:
                return new Color(0.7f, 0.7f, 0.7f, 1.0f); // Gray-ish
            default:
                return Color.WHITE;
        }
    }

    /**
     * Update all particles.
     * @param delta Time since last frame (seconds)
     */
    public void update(float delta) {
        Iterator<Particle> iterator = particles.iterator();
        while (iterator.hasNext()) {
            Particle particle = iterator.next();
            if (!particle.update(delta)) {
                iterator.remove();
            }
        }
    }

    /**
     * Render all particles as 2D sprites projected from 3D world positions.
     * @param batch SpriteBatch to render with (must be between begin/end)
     */
    public void render(SpriteBatch batch) {
        for (Particle particle : particles) {
            particle.render(batch, camera);
        }
    }

    /**
     * Creates a combat particle effect for damage/drain visualization.
     * Used when players/NPCs take damage or have resources drained.
     *
     * @param type Material type (BLOOD, MANA, or STAMINA)
     * @param worldX World X position (in pixels)
     * @param worldY World Y position (in pixels)
     * @param worldZ World Z position (in pixels)
     * @param particleCount Number of particles to spawn
     */
    public void createCombatEffect(MaterialType type, float worldX, float worldY, float worldZ, int particleCount) {
        // Get particle textures for this type
        Texture[] textures = getTexturesForMaterial(type);
        if (textures == null || textures.length == 0) {
            return;
        }

        // Define color and physics properties based on type
        Color baseColor;
        float particleSize;
        float minLifetime, maxLifetime;
        float gravity;
        float minSpeed = 100f;   // Match break effect speed range
        float maxSpeed = 250f;

        switch (type) {
            case BLOOD:
                baseColor = new Color(0.8f, 0.2f, 0.2f, 1.0f);  // Red
                particleSize = PARTICLE_SIZE;  // Match break effect size for visibility
                minLifetime = 0.5f;
                maxLifetime = 1.5f;
                gravity = -300f;  // Same as break effects - negative pulls down
                break;
            case MANA:
                baseColor = new Color(0.3f, 0.5f, 1.0f, 1.0f);  // Blue
                particleSize = PARTICLE_SIZE;  // Match break effect size for visibility
                minLifetime = 0.5f;
                maxLifetime = 1.5f;
                gravity = -200f;  // Lighter, floatier than blood
                minSpeed = 80f;  // Match break effect speed range
                maxSpeed = 200f;
                break;
            case STAMINA:
                baseColor = new Color(0.9f, 0.8f, 0.3f, 1.0f);  // Yellow/gold
                particleSize = PARTICLE_SIZE;  // Match break effect size for visibility
                minLifetime = 0.5f;
                maxLifetime = 1.5f;
                gravity = -250f;  // Medium gravity between blood and mana
                break;
            case CURSED:
                baseColor = new Color(0.6f, 0.3f, 0.8f, 1.0f);  // Purple/magenta
                particleSize = PARTICLE_SIZE;
                minLifetime = 1.0f;  // Longer lifetime to see the drip
                maxLifetime = 2.0f;
                gravity = -400f;  // Strong gravity for dripping effect
                minSpeed = 20f;  // Very low initial speed
                maxSpeed = 50f;  // Slow dripping motion
                break;
            default:
                return;  // Don't create particles for WOOD/STONE via this method
        }

        // Create particles with varied behavior for more chaotic effect
        // TODO: Add directional bias based on attacker position
        for (int i = 0; i < particleCount; i++) {
            // Random texture variant
            Texture texture = textures[(int) (Math.random() * textures.length)];
            TextureRegion region = new TextureRegion(texture);

            float velocityX, velocityY, velocityZ;
            float particleGravity = gravity;

            // Special dripping behavior for CURSED particles
            if (type == MaterialType.CURSED) {
                // Mostly downward with minimal horizontal spread
                float speed = minSpeed + (float) Math.random() * (maxSpeed - minSpeed);
                float angle = (float) (Math.random() * Math.PI * 2);

                // Very small horizontal component (10-20% of speed)
                float horizontalSpread = speed * (0.1f + (float) Math.random() * 0.1f);
                velocityX = (float) Math.cos(angle) * horizontalSpread;
                velocityZ = (float) Math.sin(angle) * horizontalSpread;

                // Mostly downward with slight upward start to simulate dripping
                velocityY = -(speed * 0.3f) + (float) Math.random() * 20f;
            } else {
                // Normal combat particle behavior for blood/mana/stamina
                // Random direction (spherical coordinates) with more variation
                float angle = (float) (Math.random() * Math.PI * 2);
                float elevation = (float) (Math.random() * Math.PI / 2); // 0 to 90 degrees

                // Random speed with more variation
                float speed = minSpeed + (float) Math.random() * (maxSpeed - minSpeed);
                velocityX = (float) (Math.cos(angle) * Math.cos(elevation)) * speed;
                velocityY = (float) Math.sin(elevation) * speed;
                velocityZ = (float) (Math.sin(angle) * Math.cos(elevation)) * speed;

                // Randomize particle behavior: ~40% float up, ~60% fall down
                if (Math.random() < 0.4f) {
                    // This particle floats upward
                    particleGravity = Math.abs(gravity) * 0.5f;  // Positive gravity (weaker)
                    velocityY += speed * 0.5f;  // Strong upward boost
                } else {
                    // This particle falls down
                    velocityY += speed * 0.2f;  // Slight upward bias before falling
                }
            }

            // Random lifetime
            float lifetime = minLifetime + (float) (Math.random() * (maxLifetime - minLifetime));

            // Apply slight random tint variation (±15% for more variety)
            Color tintColor = new Color(
                    baseColor.r * (0.85f + (float) Math.random() * 0.3f),
                    baseColor.g * (0.85f + (float) Math.random() * 0.3f),
                    baseColor.b * (0.85f + (float) Math.random() * 0.3f),
                    1.0f
            );
            // Clamp to valid range
            tintColor.clamp();

            // Create particle with randomized gravity
            Particle particle = new Particle(
                    region, worldX, worldY, worldZ,
                    velocityX, velocityY, velocityZ,
                    lifetime, particleGravity, tintColor, particleSize
            );

            particles.add(particle);
        }
    }

    /**
     * Creates a combat particle effect attached to an entity.
     * Particles will follow the entity until they detach naturally.
     *
     * @param type Material type (BLOOD, MANA, STAMINA, or CURSED)
     * @param entity Entity to attach particles to
     * @param offsetX X offset from entity center (in pixels)
     * @param offsetY Y offset from entity center (in pixels)
     * @param offsetZ Z offset from entity center (in pixels)
     * @param particleCount Number of particles to spawn
     * @param attachDuration How long particles stay attached (0 = until hit ground)
     */
    public void createCombatEffectAttached(MaterialType type, io.github.inherit_this.entities.Entity entity,
                                           float offsetX, float offsetY, float offsetZ,
                                           int particleCount, float attachDuration) {
        // Get particle textures for this type
        Texture[] textures = getTexturesForMaterial(type);
        if (textures == null || textures.length == 0) {
            return;
        }

        // Calculate world position for initial spawn
        final float TILE_SIZE = 32f;
        float worldX = entity.getPosition().x * TILE_SIZE + offsetX;
        float worldY = offsetY;
        float worldZ = entity.getPosition().y * TILE_SIZE + offsetZ;

        // Define color and physics properties based on type
        Color baseColor;
        float particleSize;
        float minLifetime, maxLifetime;
        float gravity;
        float minSpeed = 100f;
        float maxSpeed = 250f;

        switch (type) {
            case BLOOD:
                baseColor = new Color(0.8f, 0.2f, 0.2f, 1.0f);
                particleSize = PARTICLE_SIZE / 2f;  // Half size for blood droplets
                minLifetime = 0.5f;
                maxLifetime = 1.5f;
                gravity = -400f;  // Stronger gravity for faster fall
                minSpeed = 120f;  // Higher initial speed for splatter
                maxSpeed = 280f;
                break;
            case MANA:
                baseColor = new Color(0.3f, 0.5f, 1.0f, 1.0f);
                particleSize = PARTICLE_SIZE;
                minLifetime = 0.5f;
                maxLifetime = 1.5f;
                gravity = -200f;
                minSpeed = 80f;
                maxSpeed = 200f;
                break;
            case STAMINA:
                baseColor = new Color(0.9f, 0.8f, 0.3f, 1.0f);
                particleSize = PARTICLE_SIZE;
                minLifetime = 0.5f;
                maxLifetime = 1.5f;
                gravity = -250f;
                break;
            case CURSED:
                baseColor = new Color(0.6f, 0.3f, 0.8f, 1.0f);
                particleSize = PARTICLE_SIZE;
                minLifetime = 1.0f;
                maxLifetime = 2.0f;
                gravity = -400f;
                minSpeed = 20f;
                maxSpeed = 50f;
                break;
            default:
                return;
        }

        // Create particles
        for (int i = 0; i < particleCount; i++) {
            Texture texture = textures[(int) (Math.random() * textures.length)];
            TextureRegion region = new TextureRegion(texture);

            float velocityX, velocityY, velocityZ;
            float particleGravity = gravity;

            // Special dripping behavior for CURSED particles
            if (type == MaterialType.CURSED) {
                float speed = minSpeed + (float) Math.random() * (maxSpeed - minSpeed);
                float angle = (float) (Math.random() * Math.PI * 2);

                float horizontalSpread = speed * (0.1f + (float) Math.random() * 0.1f);
                velocityX = (float) Math.cos(angle) * horizontalSpread;
                velocityZ = (float) Math.sin(angle) * horizontalSpread;
                velocityY = -(speed * 0.3f) + (float) Math.random() * 20f;
            } else if (type == MaterialType.BLOOD) {
                // Blood splatter: strong horizontal spread, minimal upward velocity
                float angle = (float) (Math.random() * Math.PI * 2);
                float speed = minSpeed + (float) Math.random() * (maxSpeed - minSpeed);

                // Strong horizontal velocity (80-100% of speed)
                float horizontalFactor = 0.8f + (float) Math.random() * 0.2f;
                velocityX = (float) Math.cos(angle) * speed * horizontalFactor;
                velocityZ = (float) Math.sin(angle) * speed * horizontalFactor;

                // Minimal upward velocity (0-20% of speed) for quick fall
                velocityY = (float) Math.random() * speed * 0.2f;
            } else {
                // Normal combat particle behavior (mana, stamina)
                float angle = (float) (Math.random() * Math.PI * 2);
                float elevation = (float) (Math.random() * Math.PI / 2);

                float speed = minSpeed + (float) Math.random() * (maxSpeed - minSpeed);
                velocityX = (float) (Math.cos(angle) * Math.cos(elevation)) * speed;
                velocityY = (float) Math.sin(elevation) * speed;
                velocityZ = (float) (Math.sin(angle) * Math.cos(elevation)) * speed;

                if (Math.random() < 0.4f) {
                    particleGravity = Math.abs(gravity) * 0.5f;
                    velocityY += speed * 0.5f;
                } else {
                    velocityY += speed * 0.2f;
                }
            }

            float lifetime = minLifetime + (float) (Math.random() * (maxLifetime - minLifetime));

            Color tintColor = new Color(
                    baseColor.r * (0.85f + (float) Math.random() * 0.3f),
                    baseColor.g * (0.85f + (float) Math.random() * 0.3f),
                    baseColor.b * (0.85f + (float) Math.random() * 0.3f),
                    1.0f
            );
            tintColor.clamp();

            Particle particle = new Particle(
                    region, worldX, worldY, worldZ,
                    velocityX, velocityY, velocityZ,
                    lifetime, particleGravity, tintColor, particleSize
            );

            // Attach to entity
            particle.attachTo(entity, offsetX, offsetY, offsetZ, attachDuration);

            particles.add(particle);
        }
    }

    /**
     * Get the number of active particles.
     */
    public int getParticleCount() {
        return particles.size();
    }

    /**
     * Clear all particles.
     */
    public void clear() {
        particles.clear();
    }

    /**
     * Dispose of resources.
     */
    public void dispose() {
        if (woodParticle1 != null) woodParticle1.dispose();
        if (woodParticle2 != null) woodParticle2.dispose();
        if (stoneParticle1 != null) stoneParticle1.dispose();
        if (stoneParticle2 != null) stoneParticle2.dispose();
        if (bloodParticle1 != null) bloodParticle1.dispose();
        if (bloodParticle2 != null) bloodParticle2.dispose();
        if (bloodParticle3 != null) bloodParticle3.dispose();
        if (manaParticle1 != null) manaParticle1.dispose();
        if (manaParticle2 != null) manaParticle2.dispose();
        if (manaParticle3 != null) manaParticle3.dispose();
        if (staminaParticle1 != null) staminaParticle1.dispose();
        if (staminaParticle2 != null) staminaParticle2.dispose();
        if (staminaParticle3 != null) staminaParticle3.dispose();
    }
}
