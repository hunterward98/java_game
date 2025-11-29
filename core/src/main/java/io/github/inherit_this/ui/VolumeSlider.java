package io.github.inherit_this.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.inherit_this.util.FontManager;
import io.github.inherit_this.audio.SoundManager;
import io.github.inherit_this.audio.SoundType;

/**
 * A volume slider UI component with both a slider and text input.
 * Displays and controls volume as a percentage (0-100).
 */
public class VolumeSlider {

    private String label;
    private int percentage; // 0-100
    private float x, y;
    private float width;
    private float height = 30;

    // UI components
    private static final float SLIDER_WIDTH = 200;
    private static final float SLIDER_HEIGHT = 10;
    private static final float HANDLE_SIZE = 16;
    private static final float INPUT_WIDTH = 60;
    private static final float INPUT_HEIGHT = 25;
    private static final float SPACING = 10;

    private BitmapFont font;
    private ShapeRenderer shapeRenderer;

    // Interaction state
    private boolean dragging = false;
    private boolean inputFocused = false;
    private String inputText = "";

    // Callback for when value changes
    private VolumeChangeListener listener;

    public interface VolumeChangeListener {
        void onVolumeChanged(int newPercentage);
    }

    public VolumeSlider(String label, int initialPercentage, VolumeChangeListener listener) {
        this.label = label;
        this.percentage = Math.max(0, Math.min(100, initialPercentage));
        this.listener = listener;
        this.font = FontManager.getInstance().getUIFont();
        this.shapeRenderer = new ShapeRenderer();
        this.inputText = String.valueOf(this.percentage);
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    /**
     * Handles mouse input for slider and text input.
     * @param mouseX Mouse X in screen coordinates
     * @param mouseY Mouse Y in screen coordinates
     * @param clicked Whether mouse was clicked this frame
     * @return true if this slider handled the input
     */
    public boolean handleInput(float mouseX, float mouseY, boolean clicked) {
        // Check if clicking on text input
        float inputX = x + width - INPUT_WIDTH;
        float inputY = y;

        if (clicked) {
            if (mouseX >= inputX && mouseX <= inputX + INPUT_WIDTH &&
                mouseY >= inputY && mouseY <= inputY + INPUT_HEIGHT) {
                // Clicked on input box
                inputFocused = true;
                inputText = "";
                SoundManager.getInstance().play(SoundType.UI_CLICK, 0.5f);
                return true;
            } else {
                // Clicked elsewhere, try to apply input if focused
                if (inputFocused) {
                    applyTextInput();
                    inputFocused = false;
                }
            }
        }

        // Check if clicking/dragging on slider
        float sliderY = y + 8; // Center vertically
        float sliderX = x + 100; // After label

        if (Gdx.input.isButtonPressed(com.badlogic.gdx.Input.Buttons.LEFT)) {
            if (dragging || (mouseX >= sliderX && mouseX <= sliderX + SLIDER_WIDTH &&
                mouseY >= sliderY - 10 && mouseY <= sliderY + 10)) {
                dragging = true;

                // Calculate percentage from mouse position
                float relativeX = mouseX - sliderX;
                relativeX = Math.max(0, Math.min(SLIDER_WIDTH, relativeX));
                int newPercentage = Math.round((relativeX / SLIDER_WIDTH) * 100);

                if (newPercentage != percentage) {
                    setPercentage(newPercentage);
                }
                return true;
            }
        } else {
            dragging = false;
        }

        return false;
    }

    /**
     * Handles keyboard input for text input field.
     */
    public void handleKeyInput(char character) {
        if (!inputFocused) return;

        if (character >= '0' && character <= '9') {
            if (inputText.length() < 3) {
                inputText += character;
                SoundManager.getInstance().play(SoundType.UI_CLICK, 0.3f);
            }
        }
    }

    /**
     * Handles backspace for text input.
     */
    public void handleBackspace() {
        if (!inputFocused || inputText.isEmpty()) return;

        inputText = inputText.substring(0, inputText.length() - 1);
        SoundManager.getInstance().play(SoundType.UI_CLICK, 0.3f);
    }

    /**
     * Handles enter key to confirm text input.
     */
    public void handleEnter() {
        if (!inputFocused) return;

        applyTextInput();
        inputFocused = false;
    }

    private void applyTextInput() {
        try {
            if (!inputText.isEmpty()) {
                int value = Integer.parseInt(inputText);
                setPercentage(Math.max(0, Math.min(100, value)));
                SoundManager.getInstance().play(SoundType.UI_CLICK, 0.6f);
            }
        } catch (NumberFormatException e) {
            // Invalid input, reset to current percentage
            inputText = String.valueOf(percentage);
        }
    }

    public void setPercentage(int percentage) {
        this.percentage = Math.max(0, Math.min(100, percentage));
        this.inputText = String.valueOf(this.percentage);
        if (listener != null) {
            listener.onVolumeChanged(this.percentage);
        }
    }

    public int getPercentage() {
        return percentage;
    }

    /**
     * Renders the volume slider.
     */
    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        // Draw label
        font.setColor(Color.WHITE);
        font.draw(batch, label, x, y + 20);

        batch.end();

        // Draw slider background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        float sliderX = x + 100;
        float sliderY = y + 8;

        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1f);
        shapeRenderer.rect(sliderX, sliderY, SLIDER_WIDTH, SLIDER_HEIGHT);

        // Draw slider fill (shows current volume)
        shapeRenderer.setColor(0.2f, 0.6f, 1.0f, 1f);
        float fillWidth = (percentage / 100f) * SLIDER_WIDTH;
        shapeRenderer.rect(sliderX, sliderY, fillWidth, SLIDER_HEIGHT);

        // Draw slider handle
        shapeRenderer.setColor(1f, 1f, 1f, 1f);
        float handleX = sliderX + fillWidth - HANDLE_SIZE / 2;
        shapeRenderer.rect(handleX, sliderY - 3, HANDLE_SIZE, SLIDER_HEIGHT + 6);

        // Draw text input box
        float inputX = x + width - INPUT_WIDTH;
        float inputY = y;

        if (inputFocused) {
            shapeRenderer.setColor(0.4f, 0.6f, 0.8f, 1f);
        } else {
            shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 1f);
        }
        shapeRenderer.rect(inputX, inputY, INPUT_WIDTH, INPUT_HEIGHT);

        // Draw input box border
        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(inputX, inputY, INPUT_WIDTH, INPUT_HEIGHT);
        shapeRenderer.end();

        batch.begin();

        // Draw percentage text
        String displayText = inputFocused ? inputText + "_" : percentage + "%";
        font.setColor(Color.WHITE);
        font.draw(batch, displayText, inputX + 8, inputY + 17);
    }

    public float getHeight() {
        return height;
    }

    public boolean isFocused() {
        return inputFocused;
    }

    public void unfocus() {
        if (inputFocused) {
            applyTextInput();
            inputFocused = false;
        }
    }
}
