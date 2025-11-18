package io.github.inherit_this.entities;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import io.github.inherit_this.world.Tile;
import io.github.inherit_this.Main;

public class Player extends Entity {

    private float speed = 120f;
    private boolean noClip = false;
    private Vector2 position = new Vector2();
    private SpriteBatch batch;

    public Player(float x, float y, Texture texture, Main game) {
        super(texture, x, y);
        this.batch = game.getBatch();
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

            setPosition(dx * speed * delta, dy * speed * delta);
        }
    }

    // move to player class
    public void renderPlayer() {
        float px = this.getPosition().x - 32 / 2f;
        float py = this.getPosition().y - 32 / 2f;
        batch.draw(this.texture, px, py);
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
    }

    public void setTilePosition(int x, int y) {
        position.x = x * Tile.TILE_SIZE;
        position.y = y * Tile.TILE_SIZE;
    }
    
    public void setNoClip(boolean enabled) {
        this.noClip = enabled;
    }
    public boolean isNoClip() {
        return noClip;
    }

}
