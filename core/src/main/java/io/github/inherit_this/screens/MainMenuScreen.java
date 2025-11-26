package io.github.inherit_this.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.inherit_this.Main;
import io.github.inherit_this.ui.MenuButton;
import io.github.inherit_this.util.Constants;
import io.github.inherit_this.util.FontManager;
import io.github.inherit_this.save.SaveManager;
import io.github.inherit_this.save.SaveData;

public class MainMenuScreen extends BaseScreen {

    private Texture background;
    private MenuButton newGameButton;
    private MenuButton loadGameButton;
    private MenuButton exitButton;
    private Vector3 touchPos = new Vector3();
    private OrthographicCamera menuCamera;
    private Viewport menuViewport;
    private ShapeRenderer shapeRenderer;
    private BitmapFont menuFont;

    // Save slot selection state
    private boolean showingSaveSlots = false;
    private MenuButton slot1Button;
    private MenuButton slot2Button;
    private MenuButton slot3Button;
    private MenuButton backButton;

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
        shapeRenderer = new ShapeRenderer();

        // Get larger menu font for better visibility and testing
        menuFont = FontManager.getInstance().getMenuFont();

        // Create main menu buttons
        newGameButton = new MenuButton("New Game", menuFont, 0, 0);
        loadGameButton = new MenuButton("Load Game", menuFont, 0, 0);
        exitButton = new MenuButton("Exit", menuFont, 0, 0);

        // Center buttons horizontally and position vertically
        float spacing = 15f;
        newGameButton.bounds.x = -newGameButton.bounds.width / 2f;
        newGameButton.bounds.y = newGameButton.bounds.height + spacing;

        loadGameButton.bounds.x = -loadGameButton.bounds.width / 2f;
        loadGameButton.bounds.y = 0;

        exitButton.bounds.x = -exitButton.bounds.width / 2f;
        exitButton.bounds.y = -exitButton.bounds.height - spacing;

        // Create save slot buttons (initially hidden)
        slot1Button = new MenuButton("", menuFont, 0, 0);
        slot2Button = new MenuButton("", menuFont, 0, 0);
        slot3Button = new MenuButton("", menuFont, 0, 0);
        backButton = new MenuButton("Back", menuFont, 0, 0);

        // Position save slot buttons
        slot1Button.bounds.x = -slot1Button.bounds.width / 2f;
        slot1Button.bounds.y = slot1Button.bounds.height * 2 + spacing * 2;

        slot2Button.bounds.x = -slot2Button.bounds.width / 2f;
        slot2Button.bounds.y = slot2Button.bounds.height + spacing;

        slot3Button.bounds.x = -slot3Button.bounds.width / 2f;
        slot3Button.bounds.y = 0;

        backButton.bounds.x = -backButton.bounds.width / 2f;
        backButton.bounds.y = -backButton.bounds.height - spacing;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        menuCamera.update();
        batch.setProjectionMatrix(menuCamera.combined);
        shapeRenderer.setProjectionMatrix(menuCamera.combined);

        // Update hover state based on mouse position
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        menuViewport.unproject(touchPos);

        batch.begin();
        // Draw background to fill viewport
        float halfWidth = menuViewport.getWorldWidth() / 2;
        float halfHeight = menuViewport.getWorldHeight() / 2;
        batch.draw(background, -halfWidth, -halfHeight, menuViewport.getWorldWidth(), menuViewport.getWorldHeight());
        batch.end();

        // Render buttons based on current screen
        batch.begin();
        if (showingSaveSlots) {
            // Update save slot button labels
            updateSaveSlotLabels();

            // Update hover states for save slot buttons
            slot1Button.updateHover(touchPos.x, touchPos.y);
            slot2Button.updateHover(touchPos.x, touchPos.y);
            slot3Button.updateHover(touchPos.x, touchPos.y);
            backButton.updateHover(touchPos.x, touchPos.y);

            // Render save slot buttons
            slot1Button.render(batch, shapeRenderer);
            slot2Button.render(batch, shapeRenderer);
            slot3Button.render(batch, shapeRenderer);
            backButton.render(batch, shapeRenderer);
        } else {
            // Update hover states for main menu buttons
            newGameButton.updateHover(touchPos.x, touchPos.y);
            loadGameButton.updateHover(touchPos.x, touchPos.y);
            exitButton.updateHover(touchPos.x, touchPos.y);

            // Check if any saves exist to enable/disable Load Game button
            boolean hasSaves = SaveManager.saveExists(0) || SaveManager.saveExists(1) || SaveManager.saveExists(2);
            if (!hasSaves) {
                loadGameButton.setDisabled(true);
            } else {
                loadGameButton.setDisabled(false);
            }

            // Render main menu buttons
            newGameButton.render(batch, shapeRenderer);
            loadGameButton.render(batch, shapeRenderer);
            exitButton.render(batch, shapeRenderer);
        }
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

            if (showingSaveSlots) {
                // Handle save slot selection
                if (slot1Button.contains(touchPos.x, touchPos.y) && !slot1Button.isDisabled()) {
                    loadGameFromSlot(0);
                }
                if (slot2Button.contains(touchPos.x, touchPos.y) && !slot2Button.isDisabled()) {
                    loadGameFromSlot(1);
                }
                if (slot3Button.contains(touchPos.x, touchPos.y) && !slot3Button.isDisabled()) {
                    loadGameFromSlot(2);
                }
                if (backButton.contains(touchPos.x, touchPos.y)) {
                    showingSaveSlots = false;
                }
            } else {
                // Handle main menu
                if (newGameButton.contains(touchPos.x, touchPos.y)) {
                    game.setScreen(new CharacterCreationScreen(game));
                }

                if (loadGameButton.contains(touchPos.x, touchPos.y) && !loadGameButton.isDisabled()) {
                    showingSaveSlots = true;
                }

                if (exitButton.contains(touchPos.x, touchPos.y)) {
                    Gdx.app.exit();
                }
            }
        }
    }

    private void updateSaveSlotLabels() {
        // Update slot 1
        SaveManager.SaveSlotInfo slot1Info = SaveManager.getSaveSlotInfo(0);
        if (slot1Info != null) {
            slot1Button.setText("Slot 1: " + slot1Info.characterName + " (Lvl " + slot1Info.level + ")");
            slot1Button.setDisabled(false);
        } else {
            slot1Button.setText("Slot 1: Empty");
            slot1Button.setDisabled(true);
        }

        // Update slot 2
        SaveManager.SaveSlotInfo slot2Info = SaveManager.getSaveSlotInfo(1);
        if (slot2Info != null) {
            slot2Button.setText("Slot 2: " + slot2Info.characterName + " (Lvl " + slot2Info.level + ")");
            slot2Button.setDisabled(false);
        } else {
            slot2Button.setText("Slot 2: Empty");
            slot2Button.setDisabled(true);
        }

        // Update slot 3
        SaveManager.SaveSlotInfo slot3Info = SaveManager.getSaveSlotInfo(2);
        if (slot3Info != null) {
            slot3Button.setText("Slot 3: " + slot3Info.characterName + " (Lvl " + slot3Info.level + ")");
            slot3Button.setDisabled(false);
        } else {
            slot3Button.setText("Slot 3: Empty");
            slot3Button.setDisabled(true);
        }
    }

    private void loadGameFromSlot(int slot) {
        SaveData saveData = SaveManager.loadGame(slot);
        if (saveData != null) {
            GameScreen gameScreen = new GameScreen(game, saveData.getCharacterName());
            SaveManager.applySaveDataToPlayer(saveData, gameScreen.getPlayer());
            game.setScreen(gameScreen);
        } else {
            Gdx.app.error("MainMenuScreen", "Failed to load save from slot " + slot);
        }
    }

    @Override
    public void dispose() {
        background.dispose();
        shapeRenderer.dispose();
        newGameButton.dispose();
        loadGameButton.dispose();
        exitButton.dispose();
        slot1Button.dispose();
        slot2Button.dispose();
        slot3Button.dispose();
        backButton.dispose();
    }
}
