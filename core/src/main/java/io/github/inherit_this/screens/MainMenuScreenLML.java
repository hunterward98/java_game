package io.github.inherit_this.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.annotation.LmlActor;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActionContainer;
import com.github.czyzby.lml.vis.util.VisLml;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import io.github.inherit_this.Main;
import io.github.inherit_this.save.SaveData;
import io.github.inherit_this.save.SaveManager;
import io.github.inherit_this.ui.CustomSkin;

/**
 * Main menu using LML (LibGDX Markup Language) for declarative UI
 * This is much cleaner than the manual positioning in the old MainMenuScreen!
 */
public class MainMenuScreenLML extends BaseScreen implements ActionContainer {

    private Stage stage;
    private LmlParser parser;

    // UI Elements (injected from LML template via @LmlActor)
    @LmlActor("mainMenuButtons") private VisTable mainMenuButtons;
    @LmlActor("saveSlotMenu") private VisTable saveSlotMenu;
    @LmlActor("deleteConfirmDialog") private VisTable deleteConfirmDialog;

    @LmlActor("loadGameButton") private VisTextButton loadGameButton;
    @LmlActor("slot1Button") private VisTextButton slot1Button;
    @LmlActor("slot2Button") private VisTextButton slot2Button;
    @LmlActor("slot3Button") private VisTextButton slot3Button;
    @LmlActor("deleteSlot1Button") private VisTextButton deleteSlot1Button;
    @LmlActor("deleteSlot2Button") private VisTextButton deleteSlot2Button;
    @LmlActor("deleteSlot3Button") private VisTextButton deleteSlot3Button;

    private int slotToDelete = -1;

    public MainMenuScreenLML(Main game) {
        super(game);

        // Initialize VisUI with custom font
        CustomSkin.initialize();

        // Create stage with viewport
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Create LML parser with VisUI syntax
        parser = VisLml.parser().build();

        // Register this controller so @LmlAction methods work
        parser.getData().addActionContainer("controller", this);

        // Parse the LML template and create the UI
        Actor root = parser.parseTemplate(Gdx.files.internal("ui/mainMenu.lml")).first();
        stage.addActor(root);

        // Update initial state
        updateLoadGameButton();
    }

    @Override
    public void render(float delta) {
        // Update save slot labels if on save slot screen
        if (saveSlotMenu.isVisible()) {
            updateSaveSlotLabels();
        }

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    // ===== LML Action Methods (these are called from the template via onClick) =====

    @LmlAction("onNewGame")
    public void onNewGame() {
        game.setScreen(new CharacterCreationScreenLML(game));
    }

    @LmlAction("onLoadGame")
    public void onLoadGame() {
        mainMenuButtons.setVisible(false);
        saveSlotMenu.setVisible(true);
        updateSaveSlotLabels();
    }

    @LmlAction("onExit")
    public void onExit() {
        Gdx.app.exit();
    }

    @LmlAction("onBack")
    public void onBack() {
        saveSlotMenu.setVisible(false);
        mainMenuButtons.setVisible(true);
    }

    @LmlAction("onLoadSlot")
    public void onLoadSlot(Actor button) {
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

    @LmlAction("onDeleteSlot")
    public void onDeleteSlot(Actor button) {
        // Get slot number from button's userData
        slotToDelete = (int) button.getUserObject();

        saveSlotMenu.setVisible(false);
        deleteConfirmDialog.setVisible(true);
    }

    @LmlAction("onConfirmDelete")
    public void onConfirmDelete() {
        if (slotToDelete >= 0) {
            if (SaveManager.deleteSave(slotToDelete)) {
                Gdx.app.log("MainMenuScreenLML", "Successfully deleted save in slot " + slotToDelete);
            }
            slotToDelete = -1;
        }

        deleteConfirmDialog.setVisible(false);
        saveSlotMenu.setVisible(true);
    }

    @LmlAction("onCancelDelete")
    public void onCancelDelete() {
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
    }
}
