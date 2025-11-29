package io.github.inherit_this.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.inherit_this.Main;
import io.github.inherit_this.ui.MenuButton;
import io.github.inherit_this.ui.SoundSettingsUI;
import io.github.inherit_this.util.Constants;
import io.github.inherit_this.util.FontManager;
import io.github.inherit_this.audio.SoundManager;
import io.github.inherit_this.audio.SoundType;
import com.badlogic.gdx.math.Vector3;

/**
 * Settings screen with sound volume controls.
 */
public class SettingsScreen implements Screen {

    private Main game;
    private Screen previousScreen;

    private Texture bgTexture;
    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont menuFont;
    private Vector3 touchPos = new Vector3();

    private SoundSettingsUI soundSettings;
    private MenuButton backButton;

    private InputMultiplexer inputMultiplexer;

    public SettingsScreen(Main game, Screen previousScreen) {
        this.game = game;
        this.previousScreen = previousScreen;

        // Create camera and viewport
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        viewport.apply();
        camera.zoom = 1f / Constants.PIXEL_SCALE;
        camera.position.set(0, 0, 0);
        camera.update();

        batch = new SpriteBatch();
        bgTexture = new Texture("menu/pause_menu_background.png");
        shapeRenderer = new ShapeRenderer();
        menuFont = FontManager.getInstance().getMenuFont();

        // Create UI
        soundSettings = new SoundSettingsUI();

        backButton = new MenuButton("Back", menuFont, 0, 0);
        backButton.bounds.x = -backButton.bounds.width / 2f;
        backButton.bounds.y = -viewport.getWorldHeight() / 2 + 30;

        // Set up input processor for keyboard input
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(new SettingsInputProcessor());
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    /**
     * Custom input processor for keyboard input to text fields.
     */
    private class SettingsInputProcessor extends InputAdapter {
        @Override
        public boolean keyTyped(char character) {
            soundSettings.handleKeyTyped(character);
            return true;
        }
    }

    @Override
    public void render(float delta) {
        // Handle ESC to go back
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            goBack();
            return;
        }

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        // Update sound settings
        soundSettings.update();

        // Update button hover
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(touchPos);
        backButton.updateHover(touchPos.x, touchPos.y);

        // Render background
        batch.begin();
        float halfWidth = viewport.getWorldWidth() / 2;
        float halfHeight = viewport.getWorldHeight() / 2;
        batch.draw(bgTexture, -halfWidth, -halfHeight, viewport.getWorldWidth(), viewport.getWorldHeight());
        batch.end();

        // Render sound settings
        soundSettings.render();

        // Render back button
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        backButton.render(batch, shapeRenderer);
        batch.end();

        handleInput();
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            // Handle sound settings clicks
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.input.getY();
            soundSettings.handleClick(mouseX, mouseY);

            // Handle back button click
            touchPos.set(mouseX, mouseY, 0);
            viewport.unproject(touchPos);

            if (backButton.contains(touchPos.x, touchPos.y)) {
                SoundManager.getInstance().play(SoundType.UI_CLICK);
                goBack();
            }
        }
    }

    private void goBack() {
        soundSettings.unfocusAll(); // Apply any pending text input
        game.setScreen(previousScreen);
    }

    @Override
    public void show() {
        // Set input processor when screen is shown
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(0, 0, 0);
        soundSettings.updateCamera();

        // Reposition back button
        backButton.bounds.y = -viewport.getWorldHeight() / 2 + 30;
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        bgTexture.dispose();
        batch.dispose();
        shapeRenderer.dispose();
        soundSettings.dispose();
        backButton.dispose();
    }
}
