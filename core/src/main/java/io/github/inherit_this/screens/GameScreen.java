package io.github.inherit_this.screens;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.Gdx;
import io.github.inherit_this.Main;

public class GameScreen extends BaseScreen {

    public GameScreen(Main game) {
        super(game);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        // draw game world
        batch.end();
    }
}
