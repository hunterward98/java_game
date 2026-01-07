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
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSlider;
import io.github.inherit_this.Main;
import io.github.inherit_this.audio.SoundManager;
import io.github.inherit_this.audio.SoundType;
import io.github.inherit_this.ui.CustomSkin;

/**
 * Settings screen using LML - clean declarative UI with volume controls.
 */
public class SettingsScreenLML extends BaseScreen implements ActionContainer {

    private Stage stage;
    private BaseScreen previousScreen;

    @LmlActor("masterVolumeSlider") private VisSlider masterVolumeSlider;
    @LmlActor("masterVolumeLabel") private VisLabel masterVolumeLabel;

    @LmlActor("areaVolumeSlider") private VisSlider areaVolumeSlider;
    @LmlActor("areaVolumeLabel") private VisLabel areaVolumeLabel;

    @LmlActor("uiVolumeSlider") private VisSlider uiVolumeSlider;
    @LmlActor("uiVolumeLabel") private VisLabel uiVolumeLabel;

    @LmlActor("primaryVolumeSlider") private VisSlider primaryVolumeSlider;
    @LmlActor("primaryVolumeLabel") private VisLabel primaryVolumeLabel;

    public SettingsScreenLML(Main game, BaseScreen previousScreen) {
        super(game);
        this.previousScreen = previousScreen;

        // Initialize VisUI if not already done
        CustomSkin.initialize();

        // Create stage
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Create LML parser
        LmlParser parser = VisLml.parser().build();
        parser.getData().addActionContainer("controller", this);

        // Parse template
        Actor root = parser.parseTemplate(Gdx.files.internal("ui/settings.lml")).first();
        stage.addActor(root);

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

    // ===== LML Actions =====

    @LmlAction("onBack")
    public void onBack() {
        SoundManager.getInstance().play(SoundType.UI_CLICK);
        game.setScreen(previousScreen);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
