package io.github.inherit_this.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import io.github.inherit_this.util.Constants;

/**
 * Renders animated portal visuals in the 3D world.
 * Portals glow and pulse to attract attention.
 */
public class PortalRenderer {

    private final ShapeRenderer shapeRenderer;
    private float animationTime = 0f;

    public PortalRenderer() {
        this.shapeRenderer = new ShapeRenderer();
    }

    /**
     * Update animation time.
     */
    public void update(float delta) {
        animationTime += delta;
    }

    /**
     * Render a portal at the given world position (pixel coordinates).
     */
    public void renderPortal(Portal portal, float cameraX, float cameraY, float cameraZ) {
        if (portal == null) {
            return;
        }

        float worldX = portal.getWorldX();
        float worldY = portal.getWorldY();

        // Portal size
        float portalWidth = Constants.TILE_SIZE * 1.5f;
        float portalHeight = Constants.TILE_SIZE * 2.0f;

        // Pulsing animation
        float pulse = 0.7f + MathUtils.sin(animationTime * 3f) * 0.3f; // 0.4 to 1.0

        // Color based on portal type
        Color portalColor;
        if (portal.getType() == Portal.PortalType.DUNGEON_ENTRANCE) {
            // Purple/magenta for dungeon entrance
            portalColor = new Color(0.8f * pulse, 0.2f * pulse, 1.0f * pulse, 0.7f);
        } else {
            // Blue/cyan for town return
            portalColor = new Color(0.2f * pulse, 0.6f * pulse, 1.0f * pulse, 0.7f);
        }

        // Project world position to screen (simplified billboard rendering)
        // Note: In 3D space, Y is up, X and Z are horizontal. worldY maps to Z coordinate.
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw main portal glow (vertical rectangle)
        // Position at worldX, worldY (which is Z in 3D space)
        shapeRenderer.setColor(portalColor);
        shapeRenderer.rect(
            worldX - portalWidth / 2,
            0f,  // Ground level
            portalWidth,
            portalHeight
        );

        // Draw outer glow ring (larger, more transparent)
        Color glowColor = new Color(portalColor.r, portalColor.g, portalColor.b, portalColor.a * 0.3f);
        shapeRenderer.setColor(glowColor);
        float glowSize = portalWidth * 1.5f * pulse;
        shapeRenderer.rect(
            worldX - glowSize / 2,
            0f,
            glowSize,
            portalHeight * 1.2f
        );

        // Note: worldY (Z coordinate) is implicitly at the portal's position in 3D space

        shapeRenderer.end();

        // Draw swirling particles (circles)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        int particleCount = 8;
        for (int i = 0; i < particleCount; i++) {
            float angle = (animationTime * 100f + i * (360f / particleCount)) % 360f;
            float radius = portalWidth * 0.4f;
            float particleX = worldX + MathUtils.cosDeg(angle) * radius;
            float particleY = (portalHeight / 2f) + MathUtils.sinDeg(animationTime * 50f + i * 45f) * (portalHeight * 0.3f);

            Color particleColor = new Color(1f, 1f, 1f, 0.8f * pulse);
            shapeRenderer.setColor(particleColor);
            shapeRenderer.circle(particleX, particleY, 4f);
        }

        shapeRenderer.end();
    }

    /**
     * Set the projection matrix for rendering (must be called before render).
     */
    public void setProjectionMatrix(com.badlogic.gdx.math.Matrix4 projectionMatrix) {
        shapeRenderer.setProjectionMatrix(projectionMatrix);
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
