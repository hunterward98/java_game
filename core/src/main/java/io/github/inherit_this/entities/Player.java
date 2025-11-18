package io.github.inherit_this.entities;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class Player extends Entity {

    private float speed = 120f;

    public Player(float x, float y, Texture texture) {
        super(texture, x, y);
    }

    public void update(float delta) {

        float dx = 0;
        float dy = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) dy += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) dy -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) dx -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) dx += 1;

        if (dx != 0 || dy != 0) {
            float length = (float)Math.sqrt(dx*dx + dy*dy);
            dx /= length;
            dy /= length;

            position.x += dx * speed * delta;
            position.y += dy * speed * delta;
        }
    }
}
