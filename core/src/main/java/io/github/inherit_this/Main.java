package io.github.inherit_this;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

import io.github.inherit_this.screens.MainMenuScreen;

import com.badlogic.gdx.graphics.OrthographicCamera;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    public SpriteBatch batch;
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
        batch = new SpriteBatch();
        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    

    @Override
    public void resize(int width, int height) {
        // Keep camera viewport size equal to window size, so box stays same size and aspect ratio

        //TODO: fix this
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
