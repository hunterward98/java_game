package io.github.inherit_this.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import io.github.inherit_this.Main;
import io.github.inherit_this.screens.GameScreen;

/**
 * Renders a live dungeon background for the main menu by reusing GameScreen in demo mode.
 * Shows a demo player automatically exploring a level 1 dungeon with AI auto-play.
 *
 * This is a thin wrapper around GameScreen that:
 * - Creates a GameScreen in demo mode (no UI, AI auto-play enabled)
 * - Renders it to a specific viewport region (right 2/3 of screen)
 * - Everything else (combat, particles, items, enemies, etc.) comes from GameScreen
 */
public class MenuBackgroundRenderer {

    private GameScreen demoGameScreen;
    private boolean isLoading = true;

    public MenuBackgroundRenderer(Main game) {
        // Create a GameScreen instance in demo mode
        demoGameScreen = new GameScreen(game, true); // true = demo mode
        isLoading = false;
    }

    private float accumulatedDelta = 0f;

    /**
     * Update the demo game (just accumulate delta for now).
     */
    public void update(float delta) {
        if (isLoading) {
            return;
        }
        // Store delta to use in render()
        accumulatedDelta = delta;
    }

    /**
     * Render the dungeon background to a specific screen region.
     *
     * @param x Left edge of render area (screen coordinates)
     * @param y Bottom edge of render area (screen coordinates)
     * @param width Width of render area
     * @param height Height of render area
     */
    public void render(int x, int y, int width, int height) {
        if (isLoading) {
            // Show loading screen
            Gdx.gl.glClearColor(0.05f, 0.05f, 0.08f, 1f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
            return;
        }

        // Set viewport to render to specific region
        Gdx.gl.glViewport(x, y, width, height);
        Gdx.gl.glScissor(x, y, width, height);
        Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);

        // GameScreen handles all rendering (3D world, sprites, particles, etc.)
        // Note: GameScreen.render() includes both update and render logic
        demoGameScreen.render(accumulatedDelta);

        // Disable scissor test
        Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);

        // Reset viewport to full screen for UI rendering
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void resize(int width, int height) {
        if (demoGameScreen != null) {
            demoGameScreen.resize(width, height);
        }
    }

    public void dispose() {
        if (demoGameScreen != null) {
            demoGameScreen.dispose();
        }
    }
}
