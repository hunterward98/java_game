package io.github.inherit_this.classes;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Entity {
    protected Vector2 position;
    protected Texture texture;
    protected float width;
    protected float height;

    public Entity(String texturePath, float x, float y) {
        this.texture = new Texture(texturePath);
        this.position = new Vector2(x, y);

        this.width = texture.getWidth();
        this.height = texture.getHeight();
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y, width, height);
    }

    public void dispose() {
        texture.dispose();
    }
}
