package io.github.inherit_this.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import io.github.inherit_this.Main;
import io.github.inherit_this.audio.SoundManager;
import io.github.inherit_this.audio.SoundType;
import io.github.inherit_this.save.SaveManager;
import io.github.inherit_this.ui.CustomSkin;

/**
 * Pause menu using direct VisUI - clean and modern!
 */
public class PauseScreenLML extends BaseScreen {

    private Stage stage;
    private GameScreen gameScreen;

    public PauseScreenLML(Main game, GameScreen gameScreen) {
        super(game);
        this.gameScreen = gameScreen;

        // Initialize VisUI with custom font
        CustomSkin.initialize();

        // Create stage with viewport
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Build UI
        buildUI();
    }

    private void buildUI() {
        // Root table that fills the screen
        VisTable root = new VisTable();
        root.setFillParent(true);
        stage.addActor(root);

        // Title
        VisLabel titleLabel = new VisLabel("Paused", "title");
        root.add(titleLabel).padBottom(20).row();

        // Menu buttons
        VisTable buttonsSection = createButtons();
        root.add(buttonsSection).pad(15).row();
    }

    private VisTable createButtons() {
        VisTable table = new VisTable();
        table.pad(15);

        // Resume button
        VisTextButton resumeButton = new VisTextButton("Resume");
        resumeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onResume();
            }
        });
        table.add(resumeButton).fillX().row();

        // Settings button
        VisTextButton settingsButton = new VisTextButton("Settings");
        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onSettings();
            }
        });
        table.add(settingsButton).fillX().padTop(10).row();

        // Save & Exit button
        VisTextButton saveExitButton = new VisTextButton("Save & Exit");
        saveExitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onSaveExit();
            }
        });
        table.add(saveExitButton).fillX().padTop(10).row();

        return table;
    }

    @Override
    public void render(float delta) {
        // ESC to resume
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(gameScreen);
            return;
        }

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    // ===== Action Methods =====

    private void onResume() {
        SoundManager.getInstance().play(SoundType.UI_CLICK);
        game.setScreen(gameScreen);
    }

    private void onSettings() {
        SoundManager.getInstance().play(SoundType.UI_CLICK);
        game.setScreen(new SettingsScreenLML(game, this));
    }

    private void onSaveExit() {
        SoundManager.getInstance().play(SoundType.UI_CLICK);

        // Auto-select appropriate save slot
        int slot = SaveManager.findSlotForCharacter(gameScreen.getCharacterName());
        if (gameScreen.saveGame(slot)) {
            Gdx.app.log("PauseScreenLML", "Game saved successfully to slot " + slot);
        } else {
            Gdx.app.error("PauseScreenLML", "Failed to save game");
        }

        game.setScreen(new MainMenuScreenLML(game));
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
