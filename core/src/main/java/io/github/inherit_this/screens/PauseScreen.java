package io.github.inherit_this.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.inherit_this.Main;
import io.github.inherit_this.util.Constants;

public class PauseScreen extends BaseScreen {

    private Stage stage;
    private Main game;
    private GameScreen gameScreen;

    private Texture bgTexture;
    private OrthographicCamera pauseCamera;
    private Viewport pauseViewport;

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

        stage = new Stage(pauseViewport);
        Gdx.input.setInputProcessor(stage);

        bgTexture = new Texture("menu/pause_menu_background.png");

        setupUI();
    }

    private void setupUI() {
        Texture resumeTex = new Texture("menu/resume.png");
        Texture saveExitTex = new Texture("menu/save_exit.png");
        Texture settingsTex = new Texture("menu/settings.png");

        ImageButton resumeBtn = new ImageButton(new TextureRegionDrawable(resumeTex));
        ImageButton saveExitBtn = new ImageButton(new TextureRegionDrawable(saveExitTex));
        ImageButton settingsBtn = new ImageButton(new TextureRegionDrawable(settingsTex));

        // Center buttons at origin
        float centerX = -resumeTex.getWidth() / 2f;
        float spacing = 10f;

        resumeBtn.setPosition(centerX, resumeTex.getHeight() + spacing);
        saveExitBtn.setPosition(centerX, 0);
        settingsBtn.setPosition(centerX, -settingsTex.getHeight() - spacing);

        resumeBtn.addListener(e -> {
            if (!resumeBtn.isPressed()) return false;
            game.setScreen(gameScreen);
            return true;
        });

        saveExitBtn.addListener(e -> {
            if (!saveExitBtn.isPressed()) return false;
            // TODO: save
            Gdx.app.exit();
            return true;
        });

        settingsBtn.addListener(e -> {
            if (!settingsBtn.isPressed()) return false;
            // TODO later: main.setScreen(new SettingsScreen(main, this));
            return true;
        });

        stage.addActor(resumeBtn);
        stage.addActor(saveExitBtn);
        stage.addActor(settingsBtn);
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(gameScreen);
        }

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        pauseCamera.update();
        game.batch.setProjectionMatrix(pauseCamera.combined);

        game.batch.begin();
        // Draw background to fill viewport
        float halfWidth = pauseViewport.getWorldWidth() / 2;
        float halfHeight = pauseViewport.getWorldHeight() / 2;
        game.batch.draw(bgTexture, -halfWidth, -halfHeight, pauseViewport.getWorldWidth(), pauseViewport.getWorldHeight());
        game.batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        pauseViewport.update(width, height, true);
        pauseCamera.position.set(0, 0, 0);
    }

    @Override
    public void dispose() {
        stage.dispose();
        bgTexture.dispose();
    }
}
