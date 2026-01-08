package io.github.inherit_this.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import io.github.inherit_this.Main;
import io.github.inherit_this.audio.SoundManager;
import io.github.inherit_this.audio.SoundType;
import io.github.inherit_this.ui.CustomSkin;

/**
 * Settings screen using direct VisUI - clean and modern with volume controls!
 */
public class SettingsScreenLML extends BaseScreen {

    private Stage stage;
    private BaseScreen previousScreen;

    // UI Elements
    private VisSlider masterVolumeSlider;
    private VisLabel masterVolumeLabel;
    private VisSlider areaVolumeSlider;
    private VisLabel areaVolumeLabel;
    private VisSlider uiVolumeSlider;
    private VisLabel uiVolumeLabel;
    private VisSlider primaryVolumeSlider;
    private VisLabel primaryVolumeLabel;

    public SettingsScreenLML(Main game, BaseScreen previousScreen) {
        super(game);
        this.previousScreen = previousScreen;

        // Initialize VisUI with custom font
        CustomSkin.initialize();

        // Create stage with viewport
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Build UI
        buildUI();

        // Initialize sliders with current volume values
        SoundManager soundMgr = SoundManager.getInstance();
        initializeSlider(masterVolumeSlider, masterVolumeLabel,
            soundMgr.getMasterVolume(),
            value -> soundMgr.setMasterVolume(value / 100f));

        initializeSlider(areaVolumeSlider, areaVolumeLabel,
            soundMgr.getAreaVolume(),
            value -> soundMgr.setAreaVolume(value / 100f));

        initializeSlider(uiVolumeSlider, uiVolumeLabel,
            soundMgr.getUiVolume(),
            value -> soundMgr.setUiVolume(value / 100f));

        initializeSlider(primaryVolumeSlider, primaryVolumeLabel,
            soundMgr.getPrimaryVolume(),
            value -> soundMgr.setPrimaryVolume(value / 100f));
    }

    private void buildUI() {
        // Root table that fills the screen
        VisTable root = new VisTable();
        root.setFillParent(true);
        stage.addActor(root);

        // Title
        VisLabel titleLabel = new VisLabel("Settings", "title");
        root.add(titleLabel).padBottom(20).row();

        // Volume controls section
        VisTable volumeSection = createVolumeControls();
        root.add(volumeSection).pad(15).row();

        // Back button
        VisTextButton backButton = new VisTextButton("Back");
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onBack();
            }
        });
        root.add(backButton).padTop(20).row();
    }

    private VisTable createVolumeControls() {
        VisTable table = new VisTable();

        // Master Volume
        VisLabel masterLabel = new VisLabel("Master Volume:");
        table.add(masterLabel).left().row();

        VisTable masterSliderTable = new VisTable();
        masterVolumeSlider = new VisSlider(0, 100, 1, false);
        masterVolumeLabel = new VisLabel("50");
        masterSliderTable.add(masterVolumeSlider).width(200).padRight(10);
        masterSliderTable.add(masterVolumeLabel).width(30);
        table.add(masterSliderTable).padBottom(15).row();

        // Area Volume
        VisLabel areaLabel = new VisLabel("Area Volume:");
        table.add(areaLabel).left().row();

        VisTable areaSliderTable = new VisTable();
        areaVolumeSlider = new VisSlider(0, 100, 1, false);
        areaVolumeLabel = new VisLabel("50");
        areaSliderTable.add(areaVolumeSlider).width(200).padRight(10);
        areaSliderTable.add(areaVolumeLabel).width(30);
        table.add(areaSliderTable).padBottom(15).row();

        // UI Volume
        VisLabel uiLabel = new VisLabel("UI Volume:");
        table.add(uiLabel).left().row();

        VisTable uiSliderTable = new VisTable();
        uiVolumeSlider = new VisSlider(0, 100, 1, false);
        uiVolumeLabel = new VisLabel("50");
        uiSliderTable.add(uiVolumeSlider).width(200).padRight(10);
        uiSliderTable.add(uiVolumeLabel).width(30);
        table.add(uiSliderTable).padBottom(15).row();

        // Primary Volume
        VisLabel primaryLabel = new VisLabel("Primary Volume:");
        table.add(primaryLabel).left().row();

        VisTable primarySliderTable = new VisTable();
        primaryVolumeSlider = new VisSlider(0, 100, 1, false);
        primaryVolumeLabel = new VisLabel("50");
        primarySliderTable.add(primaryVolumeSlider).width(200).padRight(10);
        primarySliderTable.add(primaryVolumeLabel).width(30);
        table.add(primarySliderTable).padBottom(15).row();

        return table;
    }

    /**
     * Initializes a slider with its current volume value and change listener.
     *
     * @param slider The VisSlider to initialize
     * @param label The VisLabel to update with the value
     * @param currentVolume The current volume (0.0-1.0)
     * @param onChange Callback when slider value changes
     */
    private void initializeSlider(VisSlider slider, VisLabel label, float currentVolume, VolumeChangeListener onChange) {
        // Set initial value (convert from 0.0-1.0 to 0-100)
        int percentage = (int)(currentVolume * 100);
        slider.setValue(percentage);
        label.setText(String.valueOf(percentage));

        // Add change listener
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int value = (int)slider.getValue();
                label.setText(String.valueOf(value));
                onChange.onVolumeChanged(value);
            }
        });
    }

    /**
     * Functional interface for volume change callbacks.
     */
    @FunctionalInterface
    private interface VolumeChangeListener {
        void onVolumeChanged(int percentage);
    }

    @Override
    public void render(float delta) {
        // ESC to go back
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            onBack();
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

    private void onBack() {
        SoundManager.getInstance().play(SoundType.UI_CLICK);
        game.setScreen(previousScreen);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
