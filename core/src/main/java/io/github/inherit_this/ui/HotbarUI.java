package io.github.inherit_this.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.inherit_this.entities.PlayerStats;
import io.github.inherit_this.items.Inventory;
import io.github.inherit_this.items.Item;
import io.github.inherit_this.items.ItemStack;
import io.github.inherit_this.util.FontManager;

/**
 * Hotbar UI displayed at the bottom of the screen.
 * Shows 5 quick-access item slots, stat bars (XP, Health, Mana, Stamina), and gold.
 */
public class HotbarUI {
    private final Inventory inventory;
    private final PlayerStats stats;
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;
    private final ItemTooltip tooltip;
    private final OrthographicCamera camera;
    private final SpriteBatch batch;  // Own batch like debug console
    private Texture backgroundTexture; // Optional background image

    // Layout constants
    private static final int HOTBAR_SLOT_SIZE = 56;
    private static final int HOTBAR_SLOT_PADDING = 4;
    private static final int NUM_HOTBAR_SLOTS = 5;
    private static final int BAR_HEIGHT = 14;
    private static final int BAR_PADDING = 4;
    private static final int PANEL_PADDING = 10;

    // Colors
    private static final Color PANEL_COLOR = new Color(0.2f, 0.2f, 0.2f, 0.95f);
    private static final Color SLOT_COLOR = new Color(0.15f, 0.15f, 0.15f, 0.9f);
    private static final Color SLOT_BORDER_COLOR = new Color(0.4f, 0.4f, 0.4f, 1.0f);

    // Stat bar colors
    private static final Color XP_BAR_COLOR = new Color(0.8f, 0.6f, 0.2f, 1.0f);       // Gold
    private static final Color HEALTH_BAR_COLOR = new Color(0.8f, 0.2f, 0.2f, 1.0f);   // Red
    private static final Color MANA_BAR_COLOR = new Color(0.2f, 0.4f, 0.9f, 1.0f);     // Blue
    private static final Color STAMINA_BAR_COLOR = new Color(0.3f, 0.8f, 0.3f, 1.0f);  // Green
    private static final Color BAR_BACKGROUND_COLOR = new Color(0.1f, 0.1f, 0.1f, 0.9f);

    // Hotbar slot assignments (references to inventory grid positions)
    private final int[] hotbarSlotX = new int[NUM_HOTBAR_SLOTS];
    private final int[] hotbarSlotY = new int[NUM_HOTBAR_SLOTS];

    // UI bounds
    private float uiX;
    private float uiY;
    private float uiWidth;
    private float uiHeight;

    public HotbarUI(Inventory inventory, PlayerStats stats) {
        this.inventory = inventory;
        this.stats = stats;
        this.shapeRenderer = new ShapeRenderer();
        this.font = FontManager.getInstance().getHotbarFont(); // Use HUGE font for hotbar visibility
        this.tooltip = new ItemTooltip();
        this.batch = new SpriteBatch();  // Create own batch like debug console

        // Screen-space camera to prevent UI stretching
        this.camera = new OrthographicCamera();
        updateCameraProjection();

        // Initialize hotbar slots to empty (-1 = unassigned)
        for (int i = 0; i < NUM_HOTBAR_SLOTS; i++) {
            hotbarSlotX[i] = -1;
            hotbarSlotY[i] = -1;
        }

        loadBackgroundTexture();
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
        calculateUIBounds();
    }

    private void loadBackgroundTexture() {
        String path = "ui/hotbar_background.png";
        if (Gdx.files.internal(path).exists()) {
            backgroundTexture = new Texture(path);
            backgroundTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
    }

    private void calculateUIBounds() {
        // Calculate total width needed
        int slotsWidth = NUM_HOTBAR_SLOTS * HOTBAR_SLOT_SIZE + (NUM_HOTBAR_SLOTS - 1) * HOTBAR_SLOT_PADDING;
        int barsWidth = 200; // Width for stat bars
        int goldWidth = 100; // Width for gold display

        uiWidth = PANEL_PADDING * 2 + slotsWidth + PANEL_PADDING + barsWidth + PANEL_PADDING + goldWidth;
        uiHeight = PANEL_PADDING * 2 + HOTBAR_SLOT_SIZE + PANEL_PADDING + (4 * BAR_HEIGHT) + (3 * BAR_PADDING);
    }

    /**
     * Position the hotbar at the bottom center of the screen.
     */
    public void updatePosition(float screenWidth, float screenHeight) {
        uiX = (screenWidth - uiWidth) / 2;
        uiY = 10; // 10 pixels from bottom
    }

    /**
     * Assign an inventory slot to a hotbar slot.
     */
    public void assignSlot(int hotbarIndex, int inventoryX, int inventoryY) {
        if (hotbarIndex >= 0 && hotbarIndex < NUM_HOTBAR_SLOTS) {
            hotbarSlotX[hotbarIndex] = inventoryX;
            hotbarSlotY[hotbarIndex] = inventoryY;
        }
    }

    /**
     * Get the item in a specific hotbar slot.
     */
    public ItemStack getHotbarItem(int hotbarIndex) {
        if (hotbarIndex < 0 || hotbarIndex >= NUM_HOTBAR_SLOTS) return null;
        int invX = hotbarSlotX[hotbarIndex];
        int invY = hotbarSlotY[hotbarIndex];
        if (invX < 0 || invY < 0) return null;
        return inventory.getItemAt(invX, invY);
    }

    /**
     * Render the hotbar UI.
     */
    public void render(SpriteBatch unusedBatch) {
        // Update camera and set projection matrices to use screen-space coordinates (like debug console)
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        // Draw background panel
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(PANEL_COLOR);
        shapeRenderer.rect(uiX, uiY, uiWidth, uiHeight);
        shapeRenderer.end();

        // Draw hotbar slots
        float slotStartX = uiX + PANEL_PADDING;
        float slotY = uiY + uiHeight - PANEL_PADDING - HOTBAR_SLOT_SIZE;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < NUM_HOTBAR_SLOTS; i++) {
            float slotX = slotStartX + i * (HOTBAR_SLOT_SIZE + HOTBAR_SLOT_PADDING);
            shapeRenderer.setColor(SLOT_COLOR);
            shapeRenderer.rect(slotX, slotY, HOTBAR_SLOT_SIZE, HOTBAR_SLOT_SIZE);
        }
        shapeRenderer.end();

        // Draw slot borders
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(SLOT_BORDER_COLOR);
        for (int i = 0; i < NUM_HOTBAR_SLOTS; i++) {
            float slotX = slotStartX + i * (HOTBAR_SLOT_SIZE + HOTBAR_SLOT_PADDING);
            shapeRenderer.rect(slotX, slotY, HOTBAR_SLOT_SIZE, HOTBAR_SLOT_SIZE);
        }
        shapeRenderer.end();

        // Draw stat bars
        float barsStartX = slotStartX + NUM_HOTBAR_SLOTS * (HOTBAR_SLOT_SIZE + HOTBAR_SLOT_PADDING) + PANEL_PADDING;
        float barsY = uiY + PANEL_PADDING;
        float barWidth = 200;

        drawStatBar(XP_BAR_COLOR, stats.getXPProgress(), barsStartX, barsY + 3 * (BAR_HEIGHT + BAR_PADDING), barWidth);
        drawStatBar(HEALTH_BAR_COLOR, stats.getHealthPercent(), barsStartX, barsY + 2 * (BAR_HEIGHT + BAR_PADDING), barWidth);
        drawStatBar(MANA_BAR_COLOR, stats.getManaPercent(), barsStartX, barsY + (BAR_HEIGHT + BAR_PADDING), barWidth);
        drawStatBar(STAMINA_BAR_COLOR, stats.getStaminaPercent(), barsStartX, barsY, barWidth);

        // Begin batch for drawing (like debug console)
        batch.begin();

        // Draw hotbar items
        for (int i = 0; i < NUM_HOTBAR_SLOTS; i++) {
            float slotX = slotStartX + i * (HOTBAR_SLOT_SIZE + HOTBAR_SLOT_PADDING);
            ItemStack stack = getHotbarItem(i);

            if (stack != null) {
                Item item = stack.getItem();
                // Draw item icon (scaled to fit slot)
                batch.draw(item.getIcon(), slotX + 4, slotY + 4, HOTBAR_SLOT_SIZE - 8, HOTBAR_SLOT_SIZE - 8);

                // Draw quantity if stackable (use integer coordinates for pixel-perfect rendering)
                if (item.isStackable() && stack.getQuantity() > 1) {
                    font.setColor(Color.WHITE);
                    font.draw(batch, "" + stack.getQuantity(), Math.round(slotX + HOTBAR_SLOT_SIZE - 18), Math.round(slotY + 14));
                }
            }

            // Draw slot number (F1-F5) (use integer coordinates for pixel-perfect rendering)
            font.setColor(Color.LIGHT_GRAY);
            font.draw(batch, "F" + (i + 1), Math.round(slotX + 4), Math.round(slotY + HOTBAR_SLOT_SIZE - 4));
        }

        // Draw stat bar labels and values (use integer coordinates for pixel-perfect rendering)
        font.setColor(Color.WHITE);
        font.draw(batch, "Lvl " + stats.getLevel(), Math.round(barsStartX), Math.round(barsY + 3 * (BAR_HEIGHT + BAR_PADDING) + BAR_HEIGHT + 2));
        font.draw(batch, "HP: " + (int)stats.getCurrentHealth() + "/" + (int)stats.getMaxHealth(),
                  Math.round(barsStartX), Math.round(barsY + 2 * (BAR_HEIGHT + BAR_PADDING) + BAR_HEIGHT + 2));
        font.draw(batch, "MP: " + (int)stats.getCurrentMana() + "/" + (int)stats.getMaxMana(),
                  Math.round(barsStartX), Math.round(barsY + (BAR_HEIGHT + BAR_PADDING) + BAR_HEIGHT + 2));
        font.draw(batch, "Stam: " + (int)stats.getCurrentStamina() + "/" + (int)stats.getMaxStamina(),
                  Math.round(barsStartX), Math.round(barsY + BAR_HEIGHT + 2));

        // Draw gold (use integer coordinates for pixel-perfect rendering)
        float goldX = barsStartX + barWidth + PANEL_PADDING;
        float goldY = uiY + uiHeight / 2;
        font.setColor(XP_BAR_COLOR); // Gold color
        font.draw(batch, "Gold", Math.round(goldX), Math.round(goldY + 20));
        font.setColor(Color.WHITE);
        font.draw(batch, "" + stats.getGold(), Math.round(goldX), Math.round(goldY));

        // Draw XP progress (use integer coordinates for pixel-perfect rendering)
        if (!stats.isMaxLevel()) {
            font.setColor(Color.LIGHT_GRAY);
            String xpText = (int)stats.getCurrentXP() + "/" + (int)stats.getXPRequiredForNextLevel() + " XP";
            font.draw(batch, xpText, Math.round(barsStartX + barWidth - 80), Math.round(barsY + 3 * (BAR_HEIGHT + BAR_PADDING) + BAR_HEIGHT + 2));
        } else {
            font.setColor(XP_BAR_COLOR);
            font.draw(batch, "MAX", Math.round(barsStartX + barWidth - 40), Math.round(barsY + 3 * (BAR_HEIGHT + BAR_PADDING) + BAR_HEIGHT + 2));
        }

        // Draw tooltip for hovered hotbar item
        float mouseX = com.badlogic.gdx.Gdx.input.getX();
        float mouseY = com.badlogic.gdx.Gdx.graphics.getHeight() - com.badlogic.gdx.Gdx.input.getY();

        for (int i = 0; i < NUM_HOTBAR_SLOTS; i++) {
            float slotX = slotStartX + i * (HOTBAR_SLOT_SIZE + HOTBAR_SLOT_PADDING);

            // Check if mouse is over this slot
            if (mouseX >= slotX && mouseX <= slotX + HOTBAR_SLOT_SIZE &&
                mouseY >= slotY && mouseY <= slotY + HOTBAR_SLOT_SIZE) {

                ItemStack stack = getHotbarItem(i);
                if (stack != null) {
                    tooltip.render(batch, stack.getItem(), mouseX, mouseY);
                }
                break;
            }
        }

        batch.end();
    }

    private void drawStatBar(Color color, float percent, float x, float y, float width) {
        // Draw background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(BAR_BACKGROUND_COLOR);
        shapeRenderer.rect(x, y, width, BAR_HEIGHT);

        // Draw filled portion
        shapeRenderer.setColor(color);
        shapeRenderer.rect(x, y, width * Math.max(0, Math.min(1, percent)), BAR_HEIGHT);
        shapeRenderer.end();

        // Draw border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(x, y, width, BAR_HEIGHT);
        shapeRenderer.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();  // Dispose our own batch
        // Don't dispose font - it's owned by FontManager singleton
        tooltip.dispose();
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
    }

    public float getWidth() { return uiWidth; }
    public float getHeight() { return uiHeight; }
}
