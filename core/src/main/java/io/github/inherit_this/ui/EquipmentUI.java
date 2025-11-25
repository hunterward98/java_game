package io.github.inherit_this.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import io.github.inherit_this.items.Equipment;
import io.github.inherit_this.items.EquipmentSlot;
import io.github.inherit_this.items.Item;
import io.github.inherit_this.util.FontManager;

import java.util.EnumMap;
import java.util.Map;

/**
 * UI for displaying and managing equipped items.
 * Shows placeholder images for each equipment slot.
 */
public class EquipmentUI {
    private final Equipment equipment;
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;
    private final ItemTooltip tooltip;
    private final OrthographicCamera camera;

    // UI Layout
    private static final int SLOT_SIZE = 48;
    private static final int SLOT_PADDING = 4;
    private static final int UI_PADDING = 10;
    private static final Color PANEL_COLOR = new Color(0.3f, 0.3f, 0.3f, 0.9f);
    private static final Color SLOT_COLOR = new Color(0.1f, 0.1f, 0.1f, 0.9f);
    private static final Color SLOT_HOVER_COLOR = new Color(0.2f, 0.4f, 0.6f, 0.9f);

    // Placeholder textures for each slot
    private final Map<EquipmentSlot, Texture> placeholderTextures;

    // UI Position
    private float uiX;
    private float uiY;
    private float uiWidth;
    private float uiHeight;

    // Slot positions (relative to UI panel)
    private final Map<EquipmentSlot, Vector2> slotPositions;

    public EquipmentUI(Equipment equipment) {
        this.equipment = equipment;
        this.shapeRenderer = new ShapeRenderer();
        this.font = FontManager.getInstance().getUIFont();
        this.tooltip = new ItemTooltip();
        this.placeholderTextures = new EnumMap<>(EquipmentSlot.class);
        this.slotPositions = new EnumMap<>(EquipmentSlot.class);

        // Screen-space camera to prevent UI from following player
        this.camera = new OrthographicCamera();
        updateCameraProjection();

        loadPlaceholderTextures();
        calculateSlotPositions();
        calculateUIBounds();
    }

    /** Update camera projection to match screen dimensions. */
    private void updateCameraProjection() {
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();
        camera.setToOrtho(false, w, h);
        camera.update();
    }

    /** Public method to update camera on window resize. */
    public void updateCamera() {
        updateCameraProjection();
    }

    /**
     * Load placeholder textures for equipment slots.
     * Falls back to a default placeholder if specific ones don't exist.
     */
    private void loadPlaceholderTextures() {
        Texture defaultPlaceholder = null;

        // Load default placeholder first
        if (Gdx.files.internal("items/placeholder.png").exists()) {
            defaultPlaceholder = new Texture("items/placeholder.png");
            defaultPlaceholder.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            String path = "equipment/" + slot.name().toLowerCase() + "_placeholder.png";

            if (Gdx.files.internal(path).exists()) {
                Texture tex = new Texture(path);
                tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                placeholderTextures.put(slot, tex);
            } else if (defaultPlaceholder != null) {
                placeholderTextures.put(slot, defaultPlaceholder);
            }
        }
    }

    /**
     * Calculate the grid positions for equipment slots.
     * Layout:
     *   [Helmet]
     *   [Cape]   [Necklace]
     *   [Gloves] [Torso]    [MainHand]
     *   [Ring1]  [Legs]     [Shield]
     *   [Ring2]  [Boots]
     */
    private void calculateSlotPositions() {
        int col0X = UI_PADDING;
        int col1X = UI_PADDING + SLOT_SIZE + SLOT_PADDING;
        int col2X = UI_PADDING + 2 * (SLOT_SIZE + SLOT_PADDING);

        int row0Y = UI_PADDING + 4 * (SLOT_SIZE + SLOT_PADDING) + 30; // Top row (below title)
        int row1Y = UI_PADDING + 3 * (SLOT_SIZE + SLOT_PADDING) + 30;
        int row2Y = UI_PADDING + 2 * (SLOT_SIZE + SLOT_PADDING) + 30;
        int row3Y = UI_PADDING + (SLOT_SIZE + SLOT_PADDING) + 30;
        int row4Y = UI_PADDING + 30;

        // Top center - Helmet
        slotPositions.put(EquipmentSlot.HELMET, new Vector2(col1X, row0Y));

        // Second row
        slotPositions.put(EquipmentSlot.CAPE_BACKPACK, new Vector2(col0X, row1Y));
        slotPositions.put(EquipmentSlot.NECKLACE, new Vector2(col2X, row1Y));

        // Third row (middle)
        slotPositions.put(EquipmentSlot.GLOVES, new Vector2(col0X, row2Y));
        slotPositions.put(EquipmentSlot.TORSO, new Vector2(col1X, row2Y));
        slotPositions.put(EquipmentSlot.MAIN_HAND, new Vector2(col2X, row2Y));

        // Fourth row
        slotPositions.put(EquipmentSlot.RING_1, new Vector2(col0X, row3Y));
        slotPositions.put(EquipmentSlot.LEGS, new Vector2(col1X, row3Y));
        slotPositions.put(EquipmentSlot.SHIELD, new Vector2(col2X, row3Y));

        // Bottom row
        slotPositions.put(EquipmentSlot.RING_2, new Vector2(col0X, row4Y));
        slotPositions.put(EquipmentSlot.BOOTS, new Vector2(col1X, row4Y));
    }

    private void calculateUIBounds() {
        // 3 columns of slots
        uiWidth = 3 * SLOT_SIZE + 2 * SLOT_PADDING + UI_PADDING * 2;
        // 5 rows of slots + title area
        uiHeight = 5 * SLOT_SIZE + 4 * SLOT_PADDING + UI_PADDING * 2 + 40;

        // Position to the right of inventory (will be adjusted by caller)
        uiX = 0;
        uiY = 0;
    }

    public void setPosition(float x, float y) {
        this.uiX = x;
        this.uiY = y;
    }

    /**
     * Render the equipment UI.
     */
    public void render(SpriteBatch batch) {
        // Update camera and set projection matrices to use screen-space coordinates
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        // Draw background panel
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(PANEL_COLOR);
        shapeRenderer.rect(uiX, uiY, uiWidth, uiHeight);
        shapeRenderer.end();

        // Draw equipment slots
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Map.Entry<EquipmentSlot, Vector2> entry : slotPositions.entrySet()) {
            EquipmentSlot slot = entry.getKey();
            Vector2 pos = entry.getValue();
            float slotX = uiX + pos.x;
            float slotY = uiY + pos.y;

            // Determine slot color
            Color slotColor = SLOT_COLOR;
            if (isMouseOverSlot(slot)) {
                slotColor = SLOT_HOVER_COLOR;
            }

            shapeRenderer.setColor(slotColor);
            shapeRenderer.rect(slotX, slotY, SLOT_SIZE, SLOT_SIZE);
        }
        shapeRenderer.end();

        // Draw slot borders
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);
        for (Vector2 pos : slotPositions.values()) {
            float slotX = uiX + pos.x;
            float slotY = uiY + pos.y;
            shapeRenderer.rect(slotX, slotY, SLOT_SIZE, SLOT_SIZE);
        }
        shapeRenderer.end();

        // Draw placeholder textures and equipped items
        for (Map.Entry<EquipmentSlot, Vector2> entry : slotPositions.entrySet()) {
            EquipmentSlot slot = entry.getKey();
            Vector2 pos = entry.getValue();
            float slotX = uiX + pos.x;
            float slotY = uiY + pos.y;

            // Draw placeholder texture
            Texture placeholder = placeholderTextures.get(slot);
            if (placeholder != null) {
                Color oldColor = batch.getColor();
                batch.setColor(1, 1, 1, 0.3f); // Semi-transparent placeholder
                batch.draw(placeholder, slotX + 4, slotY + 4, SLOT_SIZE - 8, SLOT_SIZE - 8);
                batch.setColor(oldColor);
            }

            // Draw equipped item (if any)
            Item equippedItem = equipment.getEquipped(slot);
            if (equippedItem != null) {
                batch.draw(equippedItem.getIcon(), slotX + 4, slotY + 4, SLOT_SIZE - 8, SLOT_SIZE - 8);
            }
        }

        // Draw title
        font.setColor(Color.WHITE);
        font.draw(batch, "Equipment", uiX + UI_PADDING, uiY + uiHeight - UI_PADDING);

        // Draw tooltip for hovered item
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        EquipmentSlot hoveredSlot = screenToSlot(mouseX, mouseY);

        if (hoveredSlot != null) {
            Item equippedItem = equipment.getEquipped(hoveredSlot);
            if (equippedItem != null) {
                tooltip.render(batch, equippedItem, mouseX, mouseY);
            }
        }
    }

    private boolean isMouseOverSlot(EquipmentSlot slot) {
        Vector2 pos = slotPositions.get(slot);
        if (pos == null) return false;

        float slotX = uiX + pos.x;
        float slotY = uiY + pos.y;

        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

        return mouseX >= slotX && mouseX <= slotX + SLOT_SIZE &&
               mouseY >= slotY && mouseY <= slotY + SLOT_SIZE;
    }

    /**
     * Convert screen coordinates to equipment slot.
     * @return The slot at the given coordinates, or null if outside slots
     */
    private EquipmentSlot screenToSlot(float screenX, float screenY) {
        for (Map.Entry<EquipmentSlot, Vector2> entry : slotPositions.entrySet()) {
            Vector2 pos = entry.getValue();
            float slotX = uiX + pos.x;
            float slotY = uiY + pos.y;

            if (screenX >= slotX && screenX <= slotX + SLOT_SIZE &&
                screenY >= slotY && screenY <= slotY + SLOT_SIZE) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Handle mouse click on equipment UI.
     * @return The equipment slot that was clicked, or null if no slot was clicked
     */
    public EquipmentSlot handleClick(float worldX, float worldY) {
        return screenToSlot(worldX, worldY);
    }

    public void dispose() {
        shapeRenderer.dispose();
        // Don't dispose font - it's owned by FontManager singleton
        tooltip.dispose();

        // Dispose placeholder textures (avoiding duplicates)
        Texture lastTexture = null;
        for (Texture tex : placeholderTextures.values()) {
            if (tex != lastTexture) {
                tex.dispose();
                lastTexture = tex;
            }
        }
    }

    public float getWidth() {
        return uiWidth;
    }

    public float getHeight() {
        return uiHeight;
    }
}
