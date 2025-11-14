package io.github.inherit_this.classes;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class Player extends Character {
    private float speed = 200f;

    public Player(String texturePath, float x, float y) {
        super(texturePath, x, y);
    }

    public void handleInput(float delta) {
        Vector2 movement = new Vector2();

        if (Gdx.input.isKeyPressed(Input.Keys.R)) {
            speed *= 1.1f;
        }
        
        // player moves with mouse
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(mouse);
            Vector2 direction = new Vector2(mouse.x - position.x, mouse.y - position.y);

            if (direction.len2() > 1f) {
                direction.nor().scl(speed * delta);
                position.add(direction);
            }
        }
    }
}
