package io.github.inherit_this.world;

import com.badlogic.gdx.graphics.Texture;

public class Tile {

    private Texture texture;
    private boolean isSolid;
    private TileType type;
    private TileLayer layer;      // Which layer this tile is on (GROUND, WALL, ROOF)
    private float angle;          // Rotation angle in degrees (overrides layer default if set)
    private int direction;        // Direction facing (0-3: N, E, S, W) for walls/roofs

    public Tile(Texture texture) {
        this(texture, false, TileType.GRASS);
    }

    public Tile(Texture texture, boolean isSolid) {
        this(texture, isSolid, TileType.GRASS);
    }

    public Tile(Texture texture, boolean isSolid, TileType type) {
        this(texture, isSolid, type, TileLayer.GROUND, -1f, 0);
    }

    public Tile(Texture texture, boolean isSolid, TileType type, TileLayer layer, float angle, int direction) {
        this.texture = texture;
        this.isSolid = isSolid;
        this.type = type;
        this.layer = layer;
        this.angle = angle;
        this.direction = direction;
    }

    public Texture getTexture() {
        return texture;
    }

    public boolean isSolid() {
        return isSolid;
    }

    public void setSolid(boolean solid) {
        this.isSolid = solid;
    }

    public TileType getType() {
        return type;
    }

    public void setType(TileType type) {
        this.type = type;
    }

    public TileLayer getLayer() {
        return layer;
    }

    public void setLayer(TileLayer layer) {
        this.layer = layer;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    /**
     * Gets the effective angle for rendering this tile.
     * Uses custom angle if set (>= 0), otherwise uses layer default.
     */
    public float getEffectiveAngle() {
        if (angle >= 0f) {
            return angle;
        }
        return layer != null ? layer.getDefaultAngle() : 0f;
    }
}
