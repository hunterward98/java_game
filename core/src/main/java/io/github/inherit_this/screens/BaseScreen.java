package io.github.inherit_this.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.inherit_this.Main;
import io.github.inherit_this.util.Constants;

public abstract class BaseScreen implements Screen {

    protected Main game;
    protected SpriteBatch batch;
    protected OrthographicCamera camera;
    protected Viewport viewport;

    public BaseScreen(Main game) {
        this.game = game;
        this.batch = game.batch;

        // Set up camera and viewport with fixed zoom for UI
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        viewport.apply();
        camera.zoom = 1f / Constants.PIXEL_SCALE;
        camera.position.set(0, 0, 0);
        camera.update();
    }

    @Override
    public void show() { }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() { }
}
