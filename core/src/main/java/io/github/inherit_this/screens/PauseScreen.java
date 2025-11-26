package io.github.inherit_this.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.inherit_this.Main;
import io.github.inherit_this.ui.MenuButton;
import io.github.inherit_this.util.Constants;
import io.github.inherit_this.util.FontManager;

public class PauseScreen extends BaseScreen {

    private Main game;
    private GameScreen gameScreen;

    private Texture bgTexture;
    private OrthographicCamera pauseCamera;
    private Viewport pauseViewport;
    private ShapeRenderer shapeRenderer;
    private BitmapFont menuFont;
    private Vector3 touchPos = new Vector3();

    private MenuButton resumeButton;
    private MenuButton settingsButton;
    private MenuButton saveExitButton;

    public PauseScreen(Main main, GameScreen gameScreen) {
        super(main);
        this.gameScreen = gameScreen;
        this.game = main;

        // Create a separate camera and viewport for the pause menu with pixel scaling
        pauseCamera = new OrthographicCamera();
        pauseViewport = new ScreenViewport(pauseCamera);
        pauseViewport.apply();
        pauseCamera.zoom = 1f / Constants.PIXEL_SCALE; // Scale up by PIXEL_SCALE
        pauseCamera.position.set(0, 0, 0);
        pauseCamera.update();

        bgTexture = new Texture("menu/pause_menu_background.png");
        shapeRenderer = new ShapeRenderer();

        // Get larger menu font for better visibility
        menuFont = FontManager.getInstance().getMenuFont();

        setupUI();
    }

    private void setupUI() {
        // Create font-based buttons
        resumeButton = new MenuButton("Resume", menuFont, 0, 0);
        settingsButton = new MenuButton("Settings", menuFont, 0, 0);
        saveExitButton = new MenuButton("Save & Exit", menuFont, 0, 0);

        // Center buttons horizontally and position vertically with spacing
        float spacing = 15f;

        resumeButton.bounds.x = -resumeButton.bounds.width / 2f;
        resumeButton.bounds.y = resumeButton.bounds.height + spacing;

        settingsButton.bounds.x = -settingsButton.bounds.width / 2f;
        settingsButton.bounds.y = 0;

        saveExitButton.bounds.x = -saveExitButton.bounds.width / 2f;
        saveExitButton.bounds.y = -saveExitButton.bounds.height - spacing;
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(gameScreen);
        }

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        pauseCamera.update();
        batch.setProjectionMatrix(pauseCamera.combined);
        shapeRenderer.setProjectionMatrix(pauseCamera.combined);

        // Update hover states based on mouse position
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        pauseViewport.unproject(touchPos);
        resumeButton.updateHover(touchPos.x, touchPos.y);
        settingsButton.updateHover(touchPos.x, touchPos.y);
        saveExitButton.updateHover(touchPos.x, touchPos.y);

        batch.begin();
        // Draw background to fill viewport
        float halfWidth = pauseViewport.getWorldWidth() / 2;
        float halfHeight = pauseViewport.getWorldHeight() / 2;
        batch.draw(bgTexture, -halfWidth, -halfHeight, pauseViewport.getWorldWidth(), pauseViewport.getWorldHeight());
        batch.end();

        // Render buttons
        batch.begin();
        resumeButton.render(batch, shapeRenderer);
        settingsButton.render(batch, shapeRenderer);
        saveExitButton.render(batch, shapeRenderer);
        batch.end();

        handleInput();
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            // Unproject screen coordinates to world coordinates
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            pauseViewport.unproject(touchPos);

            if (resumeButton.contains(touchPos.x, touchPos.y)) {
                game.setScreen(gameScreen);
            }

            if (settingsButton.contains(touchPos.x, touchPos.y)) {
                // TODO later: game.setScreen(new SettingsScreen(game, this));
            }

            if (saveExitButton.contains(touchPos.x, touchPos.y)) {
                // TODO: save
                Gdx.app.exit();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        pauseViewport.update(width, height, true);
        pauseCamera.position.set(0, 0, 0);
    }

    @Override
    public void dispose() {
        bgTexture.dispose();
        shapeRenderer.dispose();
        resumeButton.dispose();
        settingsButton.dispose();
        saveExitButton.dispose();
    }
}
