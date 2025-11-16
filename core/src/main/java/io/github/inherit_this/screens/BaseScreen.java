package io.github.inherit_this.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.inherit_this.Main;

public abstract class BaseScreen implements Screen {

    protected Main game;
    protected SpriteBatch batch;

    public BaseScreen(Main game) {
        this.game = game;
        this.batch = game.batch;
    }

    @Override
    public void show() { }

    @Override
    public void resize(int width, int height) { }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() { }
}
