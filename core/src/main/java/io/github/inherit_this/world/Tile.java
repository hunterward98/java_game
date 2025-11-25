package io.github.inherit_this.world;

import com.badlogic.gdx.graphics.Texture;

public class Tile {

    private Texture texture;
    private boolean isSolid;

    public Tile(Texture texture) {
        this(texture, false);
    }

    public Tile(Texture texture, boolean isSolid) {
        this.texture = texture;
        this.isSolid = isSolid;
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
}
