package io.github.inherit_this;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.OrthographicCamera;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture boxTexture;
    private Texture backgroundTexture;
    private OrthographicCamera camera;

    private static final float WORLD_WIDTH = 2000;
    private static final float WORLD_HEIGHT = 2000;

    private static final float BOX_SIZE = 64; // TODO: import character size

    private float boxX = WORLD_WIDTH / 2 - BOX_SIZE / 2;
    private float boxY = WORLD_HEIGHT / 2 - BOX_SIZE / 2;

    private Vector2 velocity = new Vector2();

    @Override
    public void create() {
        boxTexture = new Texture(Gdx.files.internal("character.png"));
        // TODO: determine last direction to generate correct character

        // TODO: create world
        backgroundTexture = new Texture(Gdx.files.internal("background.jpg"));
        batch = new SpriteBatch();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(boxX + BOX_SIZE / 2, boxY + BOX_SIZE / 2, 0);
        camera.update();;
    }

    @Override
    public void render() {
        handleInput(Gdx.graphics.getDeltaTime());

        camera.position.set(boxX + BOX_SIZE / 2, boxY + BOX_SIZE / 2, 0);

        float halfViewportWidth = camera.viewportWidth / 2f;
        float halfViewportHeight = camera.viewportHeight / 2f;

        camera.position.x = clamp(camera.position.x, halfViewportWidth, WORLD_WIDTH - halfViewportWidth);
        camera.position.y = clamp(camera.position.y, halfViewportHeight, WORLD_HEIGHT - halfViewportHeight);

        camera.update();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.draw(backgroundTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);

        batch.draw(boxTexture, boxX, boxY, BOX_SIZE, BOX_SIZE);

        batch.end();
    }

    private void handleInput(float delta) {
        float speed = 300;

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

        // prevents going outside current world boundary
        boxX = clamp(boxX, 0, WORLD_WIDTH - BOX_SIZE);
        boxY = clamp(boxY, 0, WORLD_HEIGHT - BOX_SIZE);
    }

    @Override
    public void resize(int width, int height) {
        // Keep camera viewport size equal to window size, so box stays same size and aspect ratio
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }

    @Override
    public void dispose() {
        batch.dispose();
        boxTexture.dispose();
        backgroundTexture.dispose();
    }

    private float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }
}
