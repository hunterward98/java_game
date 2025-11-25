package io.github.inherit_this.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.inherit_this.Main;
import io.github.inherit_this.ui.MenuButton;
import io.github.inherit_this.util.Constants;

public class MainMenuScreen extends BaseScreen {

    private Texture background;
    private MenuButton newGameButton;
    private MenuButton exitButton;
    private Vector3 touchPos = new Vector3();
    private OrthographicCamera menuCamera;
    private Viewport menuViewport;

    public MainMenuScreen(Main game) {
        super(game);

        // Create a separate camera and viewport for the menu with pixel scaling
        menuCamera = new OrthographicCamera();
        menuViewport = new ScreenViewport(menuCamera);
        menuViewport.apply();
        menuCamera.zoom = 1f / Constants.PIXEL_SCALE; // Scale up by PIXEL_SCALE
        menuCamera.position.set(0, 0, 0);
        menuCamera.update();

        background = new Texture("background.jpg");

        // Position buttons in world coordinates (centered at origin)
        newGameButton = new MenuButton("menu/new_game.png", 0, 0);
        exitButton = new MenuButton("menu/exit.png", 0, 0);

        newGameButton.bounds.x = -newGameButton.texture.getWidth() / 2f;
        newGameButton.bounds.y = 20;

        exitButton.bounds.x = -exitButton.texture.getWidth() / 2f;
        exitButton.bounds.y = -exitButton.texture.getHeight() - 20;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        menuCamera.update();
        batch.setProjectionMatrix(menuCamera.combined);

        batch.begin();
        // Draw background to fill viewport
        float halfWidth = menuViewport.getWorldWidth() / 2;
        float halfHeight = menuViewport.getWorldHeight() / 2;
        batch.draw(background, -halfWidth, -halfHeight, menuViewport.getWorldWidth(), menuViewport.getWorldHeight());

        batch.draw(newGameButton.texture, newGameButton.bounds.x, newGameButton.bounds.y);
        batch.draw(exitButton.texture, exitButton.bounds.x, exitButton.bounds.y);
        batch.end();

        handleInput();
    }

    @Override
    public void resize(int width, int height) {
        menuViewport.update(width, height, true);
        menuCamera.position.set(0, 0, 0);
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            // Unproject screen coordinates to world coordinates
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            menuViewport.unproject(touchPos);

            if (newGameButton.contains(touchPos.x, touchPos.y)) {
                game.setScreen(new GameScreen(game));
            }

            if (exitButton.contains(touchPos.x, touchPos.y)) {
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
