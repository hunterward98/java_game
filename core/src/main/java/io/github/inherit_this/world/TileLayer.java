package io.github.inherit_this.world;

/**
 * Defines the rendering layer for tiles.
 * Multiple tiles can exist at the same position on different layers.
 *
 * Examples:
 * - GROUND: Floor tiles, grass, stone paths
 * - WALL: Vertical walls, fences (rendered at 90 degrees)
 * - ROOF: Angled roof tiles (rendered at 45 degrees)
 */
public enum TileLayer {
    GROUND(0, 0f),    // Flat on ground (Y=0)
    WALL(1, 90f),     // Vertical wall (rotated 90 degrees on X axis)
    ROOF(2, 45f);     // Angled roof (rotated 45 degrees on X axis)

    private final int renderOrder;  // Lower renders first
    private final float defaultAngle;  // Default rotation angle in degrees

    TileLayer(int renderOrder, float defaultAngle) {
        this.renderOrder = renderOrder;
        this.defaultAngle = defaultAngle;
    }

    public int getRenderOrder() {
        return renderOrder;
    }

    public float getDefaultAngle() {
        return defaultAngle;
    }

    /**
     * Gets the Y offset for this layer (height above ground).
     */
    public float getYOffset() {
        switch (this) {
            case GROUND: return 0f;
            case WALL: return 16f;  // Half tile height (32px / 2)
            case ROOF: return 32f;  // Full tile height
            default: return 0f;
        }
    }
}
