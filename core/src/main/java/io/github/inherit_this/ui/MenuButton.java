package io.github.inherit_this.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
// import com.badlogic.gdx.math.Vector2;

public class MenuButton {
    public Texture texture;
    public Rectangle bounds;

    public MenuButton(String texturePath, float x, float y) {
        this.texture = new Texture(texturePath);
        this.bounds = new Rectangle(x, y, texture.getWidth(), texture.getHeight());
    }

    public boolean contains(float x, float y) {
        return bounds.contains(x, y);
    }

    public void dispose() {
        texture.dispose();
    }
}
