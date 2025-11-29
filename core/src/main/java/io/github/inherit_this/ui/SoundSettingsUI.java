package io.github.inherit_this.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.inherit_this.audio.SoundManager;
import io.github.inherit_this.util.FontManager;

/**
 * Sound settings UI with volume sliders for each category.
 */
public class SoundSettingsUI {

    private BitmapFont titleFont;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;

    private VolumeSlider masterSlider;
    private VolumeSlider areaSlider;
    private VolumeSlider uiSlider;
    private VolumeSlider primarySlider;

    private float uiX, uiY;
    private float uiWidth = 500;
    private float uiHeight = 350;

    private static final float SLIDER_SPACING = 50;

    public SoundSettingsUI() {
        this.titleFont = FontManager.getInstance().getUIFont();
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        this.camera = new OrthographicCamera();
        updateCameraProjection();

        // Create sliders with initial values from SoundManager
        SoundManager soundMgr = SoundManager.getInstance();

        masterSlider = new VolumeSlider("Master Volume",
            (int)(soundMgr.getMasterVolume() * 100),
            percentage -> soundMgr.setMasterVolume(percentage / 100f)
        );

        areaSlider = new VolumeSlider("Area SFX",
            (int)(soundMgr.getAreaVolume() * 100),
            percentage -> soundMgr.setAreaVolume(percentage / 100f)
        );

        uiSlider = new VolumeSlider("UI SFX",
            (int)(soundMgr.getUiVolume() * 100),
            percentage -> soundMgr.setUiVolume(percentage / 100f)
        );

        primarySlider = new VolumeSlider("Primary SFX",
            (int)(soundMgr.getPrimaryVolume() * 100),
            percentage -> soundMgr.setPrimaryVolume(percentage / 100f)
        );

        updateSliderPositions();
    }

    private void updateCameraProjection() {
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();
        camera.setToOrtho(false, w, h);
        camera.update();
    }

    public void updateCamera() {
        updateCameraProjection();
        updateSliderPositions();
    }

    private void updateSliderPositions() {
        // Center UI on screen
        uiX = (Gdx.graphics.getWidth() - uiWidth) / 2;
        uiY = (Gdx.graphics.getHeight() - uiHeight) / 2;

        // Position sliders
        float sliderY = uiY + uiHeight - 80;
        float sliderWidth = uiWidth - 40;

        masterSlider.setPosition(uiX + 20, sliderY);
        masterSlider.setWidth(sliderWidth);

        areaSlider.setPosition(uiX + 20, sliderY - SLIDER_SPACING);
        areaSlider.setWidth(sliderWidth);

        uiSlider.setPosition(uiX + 20, sliderY - SLIDER_SPACING * 2);
        uiSlider.setWidth(sliderWidth);

        primarySlider.setPosition(uiX + 20, sliderY - SLIDER_SPACING * 3);
        primarySlider.setWidth(sliderWidth);
    }

    /**
     * Handles mouse input for all sliders.
     */
    public boolean handleClick(float mouseX, float mouseY) {
        // Convert screen Y to our coordinate system
        mouseY = Gdx.graphics.getHeight() - mouseY;

        boolean handled = false;
        handled |= masterSlider.handleInput(mouseX, mouseY, true);
        handled |= areaSlider.handleInput(mouseX, mouseY, true);
        handled |= uiSlider.handleInput(mouseX, mouseY, true);
        handled |= primarySlider.handleInput(mouseX, mouseY, true);

        return handled;
    }

    /**
     * Updates slider dragging (call every frame).
     */
    public void update() {
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

        masterSlider.handleInput(mouseX, mouseY, false);
        areaSlider.handleInput(mouseX, mouseY, false);
        uiSlider.handleInput(mouseX, mouseY, false);
        primarySlider.handleInput(mouseX, mouseY, false);
    }

    /**
     * Handles keyboard input for text input fields.
     */
    public void handleKeyTyped(char character) {
        if (character == '\b') {
            // Backspace
            masterSlider.handleBackspace();
            areaSlider.handleBackspace();
            uiSlider.handleBackspace();
            primarySlider.handleBackspace();
        } else if (character == '\r' || character == '\n') {
            // Enter
            masterSlider.handleEnter();
            areaSlider.handleEnter();
            uiSlider.handleEnter();
            primarySlider.handleEnter();
        } else {
            // Regular character
            masterSlider.handleKeyInput(character);
            areaSlider.handleKeyInput(character);
            uiSlider.handleKeyInput(character);
            primarySlider.handleKeyInput(character);
        }
    }

    /**
     * Unfocuses all text inputs.
     */
    public void unfocusAll() {
        masterSlider.unfocus();
        areaSlider.unfocus();
        uiSlider.unfocus();
        primarySlider.unfocus();
    }

    /**
     * Renders the sound settings UI.
     */
    public void render() {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        // Draw background panel
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.15f, 0.15f, 0.15f, 0.95f);
        shapeRenderer.rect(uiX, uiY, uiWidth, uiHeight);
        shapeRenderer.end();

        // Draw border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(uiX, uiY, uiWidth, uiHeight);
        shapeRenderer.end();

        batch.begin();

        // Draw title
        titleFont.setColor(Color.YELLOW);
        titleFont.draw(batch, "Sound Settings", uiX + 20, uiY + uiHeight - 20);

        batch.end();

        // Render sliders
        batch.begin();
        masterSlider.render(batch, shapeRenderer);
        areaSlider.render(batch, shapeRenderer);
        uiSlider.render(batch, shapeRenderer);
        primarySlider.render(batch, shapeRenderer);
        batch.end();
    }

    public float getWidth() {
        return uiWidth;
    }

    public float getHeight() {
        return uiHeight;
    }

    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
    }
}
