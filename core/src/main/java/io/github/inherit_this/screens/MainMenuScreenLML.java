package io.github.inherit_this.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import io.github.inherit_this.Main;
import io.github.inherit_this.save.SaveData;
import io.github.inherit_this.save.SaveManager;
import io.github.inherit_this.ui.CustomSkin;
import io.github.inherit_this.ui.MenuBackgroundRenderer;

/**
 * Main menu using direct VisUI with live dungeon background!
 */
public class MainMenuScreenLML extends BaseScreen {

    private Stage stage;
    private MenuBackgroundRenderer backgroundRenderer;

    // UI Elements
    private VisTable mainMenuButtons;
    private VisTable saveSlotMenu;
    private VisTable deleteConfirmDialog;

    private VisTextButton loadGameButton;
    private VisTextButton slot1Button;
    private VisTextButton slot2Button;
    private VisTextButton slot3Button;
    private VisTextButton deleteSlot1Button;
    private VisTextButton deleteSlot2Button;
    private VisTextButton deleteSlot3Button;
    private VisLabel deleteWarningLabel;

    private int slotToDelete = -1;

    public MainMenuScreenLML(Main game) {
        super(game);

        // Initialize VisUI with custom font
        CustomSkin.initialize();

        // Create live dungeon background
        backgroundRenderer = new MenuBackgroundRenderer(game);

        // Create stage with viewport
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Build UI
        buildUI();

        // Update initial state
        updateLoadGameButton();
    }

    private void buildUI() {
        // Root table that fills the screen, aligned to left
        VisTable root = new VisTable();
        root.setFillParent(true);
        root.align(com.badlogic.gdx.utils.Align.left | com.badlogic.gdx.utils.Align.top);
        stage.addActor(root);

        // Container for menu UI (left side, takes 1/3 of screen width)
        VisTable menuContainer = new VisTable();
        menuContainer.pad(40);

        // Title
        VisLabel titleLabel = new VisLabel("Inherit This", "title");
        menuContainer.add(titleLabel).padBottom(20).row();

        // Main menu buttons (visible by default)
        mainMenuButtons = createMainMenuButtons();
        menuContainer.add(mainMenuButtons).pad(15).row();

        // Save slot menu (hidden by default)
        saveSlotMenu = createSaveSlotMenu();
        saveSlotMenu.setVisible(false);
        menuContainer.add(saveSlotMenu).pad(15).row();

        // Delete confirmation dialog (hidden by default)
        deleteConfirmDialog = createDeleteConfirmDialog();
        deleteConfirmDialog.setVisible(false);
        menuContainer.add(deleteConfirmDialog).pad(20).row();

        // Add menu container to root (left side)
        root.add(menuContainer).width(Gdx.graphics.getWidth() / 3f).expandY().top();
    }

    private VisTable createMainMenuButtons() {
        VisTable table = new VisTable();

        // New Game button
        VisTextButton newGameButton = new VisTextButton("New Game");
        newGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onNewGame();
            }
        });
        table.add(newGameButton).fillX().row();

        // Load Game button
        loadGameButton = new VisTextButton("Load Game");
        loadGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onLoadGame();
            }
        });
        table.add(loadGameButton).fillX().padTop(10).row();

        // Exit button
        VisTextButton exitButton = new VisTextButton("Exit");
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onExit();
            }
        });
        table.add(exitButton).fillX().padTop(10).row();

        return table;
    }

    private VisTable createSaveSlotMenu() {
        VisTable table = new VisTable();

        // Title
        VisLabel subtitleLabel = new VisLabel("Select Save Slot", "subtitle");
        table.add(subtitleLabel).colspan(2).padBottom(20).row();

        // Slot 1
        VisTable slot1Table = new VisTable();
        slot1Button = new VisTextButton("Slot 1: Empty");
        slot1Button.setUserObject(0);
        slot1Button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onLoadSlot(actor);
            }
        });
        deleteSlot1Button = new VisTextButton("Delete");
        deleteSlot1Button.setUserObject(0);
        deleteSlot1Button.setVisible(false);
        deleteSlot1Button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onDeleteSlot(actor);
            }
        });
        slot1Table.add(slot1Button).width(200);
        slot1Table.add(deleteSlot1Button).padLeft(10);
        table.add(slot1Table).colspan(2).padBottom(10).row();

        // Slot 2
        VisTable slot2Table = new VisTable();
        slot2Button = new VisTextButton("Slot 2: Empty");
        slot2Button.setUserObject(1);
        slot2Button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onLoadSlot(actor);
            }
        });
        deleteSlot2Button = new VisTextButton("Delete");
        deleteSlot2Button.setUserObject(1);
        deleteSlot2Button.setVisible(false);
        deleteSlot2Button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onDeleteSlot(actor);
            }
        });
        slot2Table.add(slot2Button).width(200);
        slot2Table.add(deleteSlot2Button).padLeft(10);
        table.add(slot2Table).colspan(2).padBottom(10).row();

        // Slot 3
        VisTable slot3Table = new VisTable();
        slot3Button = new VisTextButton("Slot 3: Empty");
        slot3Button.setUserObject(2);
        slot3Button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onLoadSlot(actor);
            }
        });
        deleteSlot3Button = new VisTextButton("Delete");
        deleteSlot3Button.setUserObject(2);
        deleteSlot3Button.setVisible(false);
        deleteSlot3Button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onDeleteSlot(actor);
            }
        });
        slot3Table.add(slot3Button).width(200);
        slot3Table.add(deleteSlot3Button).padLeft(10);
        table.add(slot3Table).colspan(2).padBottom(10).row();

        // Back button
        VisTextButton backButton = new VisTextButton("Back");
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onBack();
            }
        });
        table.add(backButton).colspan(2).padTop(10).row();

        return table;
    }

    private VisTable createDeleteConfirmDialog() {
        VisTable table = new VisTable();

        deleteWarningLabel = new VisLabel("Delete save in slot 1?");
        table.add(deleteWarningLabel).padBottom(10).row();

        VisLabel warningLabel2 = new VisLabel("This action cannot be undone!");
        table.add(warningLabel2).padBottom(20).row();

        // Confirm Delete button
        VisTextButton confirmButton = new VisTextButton("Confirm Delete");
        confirmButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onConfirmDelete();
            }
        });
        table.add(confirmButton).fillX().padBottom(10).row();

        // Cancel button
        VisTextButton cancelButton = new VisTextButton("Cancel");
        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onCancelDelete();
            }
        });
        table.add(cancelButton).fillX().row();

        return table;
    }

    @Override
    public void render(float delta) {
        // Update save slot labels if on save slot screen
        if (saveSlotMenu.isVisible()) {
            updateSaveSlotLabels();
        }

        // Update background dungeon demo
        backgroundRenderer.update(delta);

        // Clear screen
        com.badlogic.gdx.utils.ScreenUtils.clear(0.1f, 0.1f, 0.12f, 1f);

        // Render live dungeon background on right 2/3 of screen
        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();
        int menuWidth = screenWidth / 3;
        int backgroundX = menuWidth;
        int backgroundWidth = screenWidth - menuWidth;

        backgroundRenderer.render(backgroundX, 0, backgroundWidth, screenHeight);

        // Render UI on top (left side)
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        backgroundRenderer.resize(width, height);
    }

    // ===== Action Methods =====

    private void onNewGame() {
        game.setScreen(new CharacterCreationScreenLML(game));
    }

    private void onLoadGame() {
        mainMenuButtons.setVisible(false);
        saveSlotMenu.setVisible(true);
        updateSaveSlotLabels();
    }

    private void onExit() {
        Gdx.app.exit();
    }

    private void onBack() {
        saveSlotMenu.setVisible(false);
        mainMenuButtons.setVisible(true);
    }

    private void onLoadSlot(Actor button) {
        // Get slot number from button's userData
        int slot = (int) button.getUserObject();

        if (SaveManager.saveExists(slot)) {
            SaveData saveData = SaveManager.loadGame(slot);
            if (saveData != null) {
                GameScreen gameScreen = new GameScreen(game, saveData.getCharacterName());
                SaveManager.applySaveDataToPlayer(saveData, gameScreen.getPlayer());
                game.setScreen(gameScreen);
            } else {
                Gdx.app.error("MainMenuScreenLML", "Failed to load save from slot " + slot);
            }
        }
    }

    private void onDeleteSlot(Actor button) {
        // Get slot number from button's userData
        slotToDelete = (int) button.getUserObject();
        deleteWarningLabel.setText("Delete save in slot " + (slotToDelete + 1) + "?");

        saveSlotMenu.setVisible(false);
        deleteConfirmDialog.setVisible(true);
    }

    private void onConfirmDelete() {
        if (slotToDelete >= 0) {
            if (SaveManager.deleteSave(slotToDelete)) {
                Gdx.app.log("MainMenuScreenLML", "Successfully deleted save in slot " + slotToDelete);
            }
            slotToDelete = -1;
        }

        deleteConfirmDialog.setVisible(false);
        saveSlotMenu.setVisible(true);
    }

    private void onCancelDelete() {
        slotToDelete = -1;
        deleteConfirmDialog.setVisible(false);
        saveSlotMenu.setVisible(true);
    }

    // ===== Helper Methods =====

    private void updateLoadGameButton() {
        boolean hasSaves = SaveManager.saveExists(0) ||
                          SaveManager.saveExists(1) ||
                          SaveManager.saveExists(2);
        loadGameButton.setDisabled(!hasSaves);
    }

    private void updateSaveSlotLabels() {
        // Update slot 1
        updateSlotButton(slot1Button, deleteSlot1Button, 0);
        // Update slot 2
        updateSlotButton(slot2Button, deleteSlot2Button, 1);
        // Update slot 3
        updateSlotButton(slot3Button, deleteSlot3Button, 2);
    }

    private void updateSlotButton(VisTextButton slotButton, VisTextButton deleteButton, int slotIndex) {
        SaveManager.SaveSlotInfo info = SaveManager.getSaveSlotInfo(slotIndex);

        if (info != null) {
            slotButton.setText("Slot " + (slotIndex + 1) + ": " + info.characterName + " (Lvl " + info.level + ")");
            slotButton.setDisabled(false);
            deleteButton.setVisible(true);
        } else {
            slotButton.setText("Slot " + (slotIndex + 1) + ": Empty");
            slotButton.setDisabled(true);
            deleteButton.setVisible(false);
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
        backgroundRenderer.dispose();
    }
}
