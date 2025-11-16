package io.github.inherit_this.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.GL20;
import io.github.inherit_this.Main;
import io.github.inherit_this.ui.MenuButton;

public class MainMenuScreen extends BaseScreen {

    private Texture background;
    private MenuButton newGameButton;
    private MenuButton exitButton;

    public MainMenuScreen(Main game) {
        super(game);
        background = new Texture("background.jpg");

        float centerX = Gdx.graphics.getWidth() / 2f;

        newGameButton = new MenuButton("menu/new_game.png",
                centerX - 300, 250);

        exitButton = new MenuButton("menu/exit.png",
                centerX - 300, 200);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(background, 0, 0,
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight());

        batch.draw(newGameButton.texture, newGameButton.bounds.x, newGameButton.bounds.y);
        batch.draw(exitButton.texture, exitButton.bounds.x, exitButton.bounds.y);
        batch.end();

        handleInput();
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            float x = Gdx.input.getX();
            float y = Gdx.graphics.getHeight() - Gdx.input.getY();

            if (newGameButton.contains(x, y)) {
                game.setScreen(new GameScreen(game));
            }

            if (exitButton.contains(x, y)) {
                Gdx.app.exit();
            }
        }
    }

    @Override
    public void dispose() {
        background.dispose();
        newGameButton.dispose();
        exitButton.dispose();
    }
}
