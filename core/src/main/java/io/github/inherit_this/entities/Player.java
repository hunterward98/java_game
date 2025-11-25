package io.github.inherit_this.entities;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import io.github.inherit_this.items.Inventory;
import io.github.inherit_this.util.Constants;
import io.github.inherit_this.world.World;
import io.github.inherit_this.Main;

public class Player extends Entity {

    private float speed = 120f;
    private boolean noClip = false;
    private SpriteBatch batch;
    private World world;
    private Inventory inventory;

    public Player(float x, float y, Texture texture, Main game, World world) {
        super(texture, x, y);
        this.batch = game.getBatch();
        this.world = world;
        this.inventory = new Inventory(8, 6); // 8 columns x 6 rows grid
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

            float moveX = dx * speed * delta;
            float moveY = dy * speed * delta;

            if (noClip) {
                position.x += moveX;
                position.y += moveY;
            } else {
                float newX = position.x + moveX;
                float newY = position.y + moveY;

                if (!isColliding(newX, newY)) {
                    position.x = newX;
                    position.y = newY;
                } else {
                    if (!isColliding(newX, position.y)) {
                        position.x = newX;
                    }
                    if (!isColliding(position.x, newY)) {
                        position.y = newY;
                    }
                }
            }
        }
    }

    private boolean isColliding(float x, float y) {
        float halfWidth = width / 2f;
        float halfHeight = height / 2f;

        boolean topLeft = world.isSolidAtPosition(x - halfWidth, y + halfHeight);
        boolean topRight = world.isSolidAtPosition(x + halfWidth, y + halfHeight);
        boolean bottomLeft = world.isSolidAtPosition(x - halfWidth, y - halfHeight);
        boolean bottomRight = world.isSolidAtPosition(x + halfWidth, y - halfHeight);

        return topLeft || topRight || bottomLeft || bottomRight;
    }

    public void renderPlayer() {
        float px = position.x - 32 / 2f;
        float py = position.y - 32 / 2f;
        batch.draw(this.texture, px, py);
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
    }

    public void setTilePosition(int x, int y) {
        position.x = x * Constants.TILE_SIZE;
        position.y = y * Constants.TILE_SIZE;
    }
    
    public void setNoClip(boolean enabled) {
        this.noClip = enabled;
    }

    public boolean isNoClip() {
        return noClip;
    }

    public Inventory getInventory() {
        return inventory;
    }

}
