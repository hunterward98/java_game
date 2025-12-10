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
        STONE
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
    }
}
