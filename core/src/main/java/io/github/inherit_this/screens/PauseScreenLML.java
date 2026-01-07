package io.github.inherit_this.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActionContainer;
import com.github.czyzby.lml.vis.util.VisLml;
import io.github.inherit_this.Main;
import io.github.inherit_this.audio.SoundManager;
import io.github.inherit_this.audio.SoundType;
import io.github.inherit_this.save.SaveManager;
import io.github.inherit_this.ui.CustomSkin;

/**
 * Pause menu using LML - much cleaner than manual button positioning!
 */
public class PauseScreenLML extends BaseScreen implements ActionContainer {

    private Stage stage;
    private GameScreen gameScreen;

    public PauseScreenLML(Main game, GameScreen gameScreen) {
        super(game);
        this.gameScreen = gameScreen;

        // Initialize VisUI if not already done
        CustomSkin.initialize();

        // Create stage
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Create LML parser
        LmlParser parser = VisLml.parser().build();
        parser.getData().addActionContainer("controller", this);

        // Parse template
        Actor root = parser.parseTemplate(Gdx.files.internal("ui/pauseMenu.lml")).first();
        stage.addActor(root);
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

    // ===== LML Actions =====

    @LmlAction("onResume")
    public void onResume() {
        SoundManager.getInstance().play(SoundType.UI_CLICK);
        game.setScreen(gameScreen);
    }

    @LmlAction("onSettings")
    public void onSettings() {
        SoundManager.getInstance().play(SoundType.UI_CLICK);
        game.setScreen(new SettingsScreenLML(game, this));
    }

    @LmlAction("onSaveExit")
    public void onSaveExit() {
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
