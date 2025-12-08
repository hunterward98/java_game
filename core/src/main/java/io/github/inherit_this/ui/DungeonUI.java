package io.github.inherit_this.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.inherit_this.util.FontManager;
import io.github.inherit_this.world.DungeonManager;
import io.github.inherit_this.world.Portal;

/**
 * UI overlay for dungeon information and portal interactions.
 * Displays current dungeon level and portal interaction prompts.
 */
public class DungeonUI {

    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private final BitmapFont font;

    private Portal nearbyPortal;  // Currently nearby portal (for interaction prompt)

    public DungeonUI() {
        this.batch = new SpriteBatch();
        this.camera = new OrthographicCamera();
        this.font = FontManager.getInstance().getUIFont();

        updateCameraProjection();
    }

    /**
     * Update camera projection to match screen dimensions.
     */
    private void updateCameraProjection() {
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();
        camera.setToOrtho(false, w, h);
        camera.update();
    }

    /**
     * Call this when window is resized.
     */
    public void updateCamera() {
        updateCameraProjection();
    }

    /**
     * Set the nearby portal for interaction prompts.
     */
    public void setNearbyPortal(Portal portal) {
        this.nearbyPortal = portal;
    }

    /**
     * Render the dungeon UI.
     */
    public void render() {
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.setColor(Color.WHITE);

        DungeonManager dungeonManager = DungeonManager.getInstance();

        // Display dungeon level if in dungeon
        if (dungeonManager.isInDungeon()) {
            int level = dungeonManager.getCurrentLevel();
            String levelText = "Dungeon Level: " + level;

            font.setColor(new Color(1.0f, 0.9f, 0.3f, 1.0f));  // Gold color
            font.draw(batch, levelText, 10, Gdx.graphics.getHeight() - 10);
        }

        // Display portal interaction prompt if near portal
        if (nearbyPortal != null) {
            String promptText = nearbyPortal.getInteractionText();

            // Draw centered at bottom of screen
            float textWidth = getTextWidth(promptText);
            float x = (Gdx.graphics.getWidth() - textWidth) / 2f;
            float y = 80;

            font.setColor(Color.WHITE);
            font.draw(batch, promptText, x, y);
        }

        batch.end();
    }

    /**
     * Estimate text width (rough calculation).
     */
    private float getTextWidth(String text) {
        // Simple estimation - adjust based on your font
        return text.length() * 8;
    }

    public void dispose() {
        batch.dispose();
    }
}
