package io.github.inherit_this.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.annotation.LmlActor;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActionContainer;
import com.github.czyzby.lml.vis.util.VisLml;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import io.github.inherit_this.Main;
import io.github.inherit_this.ui.CustomSkin;

/**
 * Character creation screen using LML - much simpler with built-in text field!
 */
public class CharacterCreationScreenLML extends BaseScreen implements ActionContainer {

    private Stage stage;

    @LmlActor("nameInput") private VisTextField nameInput;
    @LmlActor("startGameButton") private VisTextButton startGameButton;

    public CharacterCreationScreenLML(Main game) {
        super(game);

        // Initialize VisUI
        CustomSkin.initialize();

        // Create stage
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Create LML parser and register this screen as action controller
        LmlParser parser = VisLml.parser().build();
        parser.getData().addActionContainer("controller", this);

        // Parse template
        Actor root = parser.parseTemplate(Gdx.files.internal("ui/characterCreation.lml")).first();
        stage.addActor(root);

        // Set up text field listener to enable/disable start button
        nameInput.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String text = nameInput.getText();
                startGameButton.setDisabled(text == null || text.trim().isEmpty());
            }
        });

        // Focus the text field
        stage.setKeyboardFocus(nameInput);
    }

    @Override
    public void render(float delta) {
        // ESC to go back
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreenLML(game));
            return;
        }

        // Enter to start game (if name is valid)
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            String name = nameInput.getText();
            if (name != null && !name.trim().isEmpty()) {
                onStartGame();
                return;
            }
        }

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    // ===== LML Actions =====

    @LmlAction("onStartGame")
    public void onStartGame() {
        String characterName = nameInput.getText().trim();
        if (!characterName.isEmpty()) {
            game.setScreen(new GameScreen(game, characterName));
        }
    }

    @LmlAction("onBack")
    public void onBack() {
        game.setScreen(new MainMenuScreenLML(game));
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
