package io.github.inherit_this.world;

import com.badlogic.gdx.graphics.Texture;

public class Tile {

    private Texture texture;
    private boolean isSolid;
    private TileType type;

    public Tile(Texture texture) {
        this(texture, false, TileType.GRASS);
    }

    public Tile(Texture texture, boolean isSolid) {
        this(texture, isSolid, TileType.GRASS);
    }

    public Tile(Texture texture, boolean isSolid, TileType type) {
        this.texture = texture;
        this.isSolid = isSolid;
        this.type = type;
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
}
