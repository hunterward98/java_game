package io.github.inherit_this.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.inherit_this.Main;

public class PauseScreen extends BaseScreen {

    private Stage stage;
    private Main game;
    private GameScreen gameScreen;

    private Texture bgTexture;

    public PauseScreen(Main main, GameScreen gameScreen) {
        super(main);
        this.gameScreen = gameScreen;
        this.game = main;

        stage = new Stage(new ScreenViewport());
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

        float centerX = Gdx.graphics.getWidth() / 2f - 64;
        resumeBtn.setPosition(centerX, 270);
        saveExitBtn.setPosition(centerX, 200);
        settingsBtn.setPosition(centerX, 130);

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

        game.batch.begin();
        game.batch.draw(bgTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        bgTexture.dispose();
    }
}
