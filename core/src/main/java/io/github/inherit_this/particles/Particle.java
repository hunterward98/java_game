package io.github.inherit_this.particles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import io.github.inherit_this.entities.Entity;

/**
 * Represents a single particle with physics (velocity, gravity) and lifetime.
 * Rendered as 2D sprite projected from 3D world space (like player rendering).
 * Can optionally attach to an entity to follow it before detaching.
 */
public class Particle {
    private TextureRegion textureRegion;
    private Vector3 position;
    private Vector3 velocity;
    private float lifetime;
    private float maxLifetime;
    private float gravity;
    private Color tintColor;
    private float size;

    // Attachment system
    private Entity attachedTo;
    private Vector3 attachmentOffset;  // Offset from entity position
    private float attachmentDuration;  // How long to stay attached (0 = detach immediately)
    private float attachmentTimer;     // Time spent attached

    /**
     * Creates a new particle.
     * @param textureRegion The texture region to render
     * @param x World X position (in pixels)
     * @param y World Y position (in pixels)
     * @param z World Z position (in pixels)
     * @param velocityX Initial velocity X (pixels per second)
     * @param velocityY Initial velocity Y (pixels per second)
     * @param velocityZ Initial velocity Z (pixels per second)
     * @param lifetime How long particle lives (seconds)
     * @param gravity Gravity strength (pixels per second squared, usually negative)
     * @param tintColor Color to tint the particle
     * @param size Particle size in pixels
     */
    public Particle(TextureRegion textureRegion, float x, float y, float z,
                    float velocityX, float velocityY, float velocityZ,
                    float lifetime, float gravity, Color tintColor, float size) {
        this.textureRegion = textureRegion;
        this.position = new Vector3(x, y, z);
        this.velocity = new Vector3(velocityX, velocityY, velocityZ);
        this.lifetime = lifetime;
        this.maxLifetime = lifetime;
        this.gravity = gravity;
        this.tintColor = new Color(tintColor);
        this.size = size;
        this.attachedTo = null;
        this.attachmentOffset = null;
        this.attachmentDuration = 0f;
        this.attachmentTimer = 0f;
    }

    /**
     * Attach this particle to an entity. While attached, particle will follow entity position.
     * @param entity Entity to attach to
     * @param offsetX X offset from entity's tile position
     * @param offsetY Y offset from entity's tile position
     * @param offsetZ Z offset from entity's tile position
     * @param duration How long to stay attached (seconds, 0 = never detach naturally)
     */
    public void attachTo(Entity entity, float offsetX, float offsetY, float offsetZ, float duration) {
        this.attachedTo = entity;
        this.attachmentOffset = new Vector3(offsetX, offsetY, offsetZ);
        this.attachmentDuration = duration;
        this.attachmentTimer = 0f;
    }

    /**
     * Detach from entity. Particle will continue with its current velocity.
     */
    public void detach() {
        this.attachedTo = null;
        this.attachmentOffset = null;
    }

    /**
     * Check if particle is attached to an entity.
     */
    public boolean isAttached() {
        return attachedTo != null;
    }

    /**
     * Update particle physics.
     * @param delta Time since last frame (seconds)
     * @return true if particle is still alive, false if it should be removed
     */
    public boolean update(float delta) {
        lifetime -= delta;
        if (lifetime <= 0) {
            return false;
        }

        // Handle attachment
        if (attachedTo != null) {
            attachmentTimer += delta;

            // Update position to follow entity (convert tile position to pixels)
            final float TILE_SIZE = 32f;
            float entityWorldX = attachedTo.getPosition().x * TILE_SIZE;
            float entityWorldZ = attachedTo.getPosition().y * TILE_SIZE;

            position.x = entityWorldX + attachmentOffset.x;
            position.y = attachmentOffset.y;  // Y offset is already in pixels
            position.z = entityWorldZ + attachmentOffset.z;

            // Apply velocity while attached (for dripping effect)
            position.x += velocity.x * delta;
            position.y += velocity.y * delta;
            position.z += velocity.z * delta;

            // Apply gravity to velocity while attached
            velocity.y += gravity * delta;

            // Check if we should detach (duration expired or hit ground)
            if ((attachmentDuration > 0 && attachmentTimer >= attachmentDuration) || position.y <= 0) {
                detach();
            }
        } else {
            // Not attached - normal physics
            // Apply velocity
            position.x += velocity.x * delta;
            position.y += velocity.y * delta;
            position.z += velocity.z * delta;

            // Apply gravity to velocity
            velocity.y += gravity * delta;

            // Ground collision - particles settle on the ground
            if (position.y <= 0) {
                position.y = 0;
                velocity.y = 0;
                // Slow down horizontal movement when on ground (friction)
                velocity.x *= 0.95f;
                velocity.z *= 0.95f;
            }
        }

        return true;
    }

    /**
     * Render particle by projecting 3D world position to 2D screen coordinates.
     * @param batch SpriteBatch to render with (must be between begin/end)
     * @param camera Camera for world-to-screen projection
     */
    public void render(SpriteBatch batch, PerspectiveCamera camera) {
        // Project 3D world position to screen coordinates
        Vector3 screenPos = camera.project(new Vector3(position));

        // Calculate alpha for fade out in last 30% of lifetime
        float alpha = 1.0f;
        float scale = 3.0f;
        if (lifetime < maxLifetime * 0.3f) {
            alpha = lifetime / (maxLifetime * 0.3f);
            scale = alpha;
        }

        // Apply tint color with alpha
        batch.setColor(tintColor.r, tintColor.g, tintColor.b, alpha);

        // Render particle centered at screen position
        float renderSize = size * scale;
        batch.draw(textureRegion,
            screenPos.x - renderSize / 2f,
            screenPos.y - renderSize / 2f,
            renderSize,
            renderSize
        );

        // Reset batch color
        batch.setColor(1, 1, 1, 1);
    }

    public boolean isAlive() {
        return lifetime > 0;
    }
}
