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
import com.kotcrab.vis.ui.widget.VisTextField;
import io.github.inherit_this.Main;
import io.github.inherit_this.ui.CustomSkin;

/**
 * Character creation screen using direct VisUI - clean and modern!
 */
public class CharacterCreationScreenLML extends BaseScreen {

    private Stage stage;

    // UI Elements
    private VisTextField nameInput;
    private VisTextButton startGameButton;

    public CharacterCreationScreenLML(Main game) {
        super(game);

        // Initialize VisUI with custom font
        CustomSkin.initialize();

        // Create stage with viewport
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Build UI
        buildUI();

        // Focus the text field
        stage.setKeyboardFocus(nameInput);
    }

    private void buildUI() {
        // Root table that fills the screen
        VisTable root = new VisTable();
        root.setFillParent(true);
        stage.addActor(root);

        // Title
        VisLabel titleLabel = new VisLabel("Create Your Character", "title");
        root.add(titleLabel).padBottom(20).row();

        // Name input section
        VisTable inputSection = new VisTable();
        inputSection.pad(10);

        VisLabel nameLabel = new VisLabel("Character Name:");
        inputSection.add(nameLabel).row();

        nameInput = new VisTextField("");
        nameInput.setMaxLength(20);
        nameInput.setMessageText("Enter name...");

        // Set up text field listener to enable/disable start button
        nameInput.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String text = nameInput.getText();
                startGameButton.setDisabled(text == null || text.trim().isEmpty());
            }
        });

        inputSection.add(nameInput).width(300).height(50).row();
        root.add(inputSection).padTop(20).row();

        // Buttons section
        VisTable buttonsSection = createButtons();
        root.add(buttonsSection).padTop(20).row();
    }

    private VisTable createButtons() {
        VisTable table = new VisTable();
        table.pad(15);

        // Start Game button (disabled by default)
        startGameButton = new VisTextButton("Start Game");
        startGameButton.setDisabled(true);
        startGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onStartGame();
            }
        });
        table.add(startGameButton).fillX().row();

        // Back button
        VisTextButton backButton = new VisTextButton("Back");
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onBack();
            }
        });
        table.add(backButton).fillX().padTop(10).row();

        return table;
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

    // ===== Action Methods =====

    private void onStartGame() {
        String characterName = nameInput.getText().trim();
        if (!characterName.isEmpty()) {
            game.setScreen(new GameScreen(game, characterName));
        }
    }

    private void onBack() {
        game.setScreen(new MainMenuScreenLML(game));
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
