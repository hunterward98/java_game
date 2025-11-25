package io.github.inherit_this.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import io.github.inherit_this.items.Inventory;
import io.github.inherit_this.items.ItemStack;

/**
 * FATE-style grid-based inventory UI with drag-and-drop support.
 */
public class InventoryUI {
    private final Inventory inventory;
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;

    // UI Layout
    private static final int CELL_SIZE = 40;
    private static final int CELL_PADDING = 2;
    private static final int UI_PADDING = 10;
    private static final Color GRID_COLOR = new Color(0.3f, 0.3f, 0.3f, 0.9f);
    private static final Color CELL_COLOR = new Color(0.1f, 0.1f, 0.1f, 0.9f);
    private static final Color CELL_HOVER_COLOR = new Color(0.2f, 0.4f, 0.6f, 0.9f);
    private static final Color CELL_SELECTED_COLOR = new Color(0.4f, 0.6f, 1.0f, 0.9f);

    // Drag and drop state
    private ItemStack draggedItem;
    private int draggedFromX;
    private int draggedFromY;
    private final Vector2 dragOffset = new Vector2();

    // UI Position
    private float uiX;
    private float uiY;
    private float uiWidth;
    private float uiHeight;

    public InventoryUI(Inventory inventory) {
        this.inventory = inventory;
        this.shapeRenderer = new ShapeRenderer();
        this.font = new BitmapFont();
        this.font.getData().setScale(1.0f);

        calculateUIBounds();
    }

    private void calculateUIBounds() {
        uiWidth = inventory.getGridWidth() * (CELL_SIZE + CELL_PADDING) + UI_PADDING * 2;
        uiHeight = inventory.getGridHeight() * (CELL_SIZE + CELL_PADDING) + UI_PADDING * 2 + 40; // +40 for title/gold

        // Center on screen (will be adjusted by camera in actual game)
        uiX = -uiWidth / 2;
        uiY = -uiHeight / 2;
    }

    public void setPosition(float x, float y) {
        this.uiX = x;
        this.uiY = y;
    }

    /**
     * Render the inventory UI.
     */
    public void render(SpriteBatch batch) {
        batch.end(); // End sprite batch to use shape renderer

        // Draw background panel
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(GRID_COLOR);
        shapeRenderer.rect(uiX, uiY, uiWidth, uiHeight);
        shapeRenderer.end();

        // Draw grid cells
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int x = 0; x < inventory.getGridWidth(); x++) {
            for (int y = 0; y < inventory.getGridHeight(); y++) {
                float cellX = uiX + UI_PADDING + x * (CELL_SIZE + CELL_PADDING);
                float cellY = uiY + UI_PADDING + y * (CELL_SIZE + CELL_PADDING);

                // Determine cell color
                Color cellColor = CELL_COLOR;
                if (isMouseOverCell(x, y)) {
                    cellColor = CELL_HOVER_COLOR;
                }

                shapeRenderer.setColor(cellColor);
                shapeRenderer.rect(cellX, cellY, CELL_SIZE, CELL_SIZE);
            }
        }
        shapeRenderer.end();

        // Draw cell borders
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);
        for (int x = 0; x < inventory.getGridWidth(); x++) {
            for (int y = 0; y < inventory.getGridHeight(); y++) {
                float cellX = uiX + UI_PADDING + x * (CELL_SIZE + CELL_PADDING);
                float cellY = uiY + UI_PADDING + y * (CELL_SIZE + CELL_PADDING);
                shapeRenderer.rect(cellX, cellY, CELL_SIZE, CELL_SIZE);
            }
        }
        shapeRenderer.end();

        batch.begin(); // Resume sprite batch for items and text

        // Draw items
        for (int x = 0; x < inventory.getGridWidth(); x++) {
            for (int y = 0; y < inventory.getGridHeight(); y++) {
                ItemStack stack = inventory.getItemAt(x, y);
                if (stack != null && stack != draggedItem) {
                    drawItemStack(batch, stack, x, y);
                }
            }
        }

        // Draw dragged item at mouse position
        if (draggedItem != null) {
            float mouseX = Gdx.input.getX() + dragOffset.x;
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY() + dragOffset.y;
            drawDraggedItem(batch, draggedItem, mouseX, mouseY);
        }

        // Draw title and gold
        font.setColor(Color.WHITE);
        font.draw(batch, "Inventory", uiX + UI_PADDING, uiY + uiHeight - UI_PADDING);
        font.draw(batch, "Gold: " + inventory.getGold(), uiX + uiWidth - 100, uiY + uiHeight - UI_PADDING);
    }

    private void drawItemStack(SpriteBatch batch, ItemStack stack, int gridX, int gridY) {
        float cellX = uiX + UI_PADDING + gridX * (CELL_SIZE + CELL_PADDING);
        float cellY = uiY + UI_PADDING + gridY * (CELL_SIZE + CELL_PADDING);

        // Draw item icon
        batch.draw(stack.getItem().getIcon(), cellX + 4, cellY + 4, CELL_SIZE - 8, CELL_SIZE - 8);

        // Draw quantity if stackable
        if (stack.getItem().isStackable() && stack.getQuantity() > 1) {
            font.setColor(Color.WHITE);
            font.draw(batch, "" + stack.getQuantity(), cellX + CELL_SIZE - 15, cellY + 12);
        }
    }

    private void drawDraggedItem(SpriteBatch batch, ItemStack stack, float x, float y) {
        // Draw semi-transparent item being dragged
        Color oldColor = batch.getColor();
        batch.setColor(1, 1, 1, 0.7f);
        batch.draw(stack.getItem().getIcon(), x, y, CELL_SIZE - 8, CELL_SIZE - 8);
        batch.setColor(oldColor);

        // Draw quantity
        if (stack.getItem().isStackable() && stack.getQuantity() > 1) {
            font.setColor(Color.WHITE);
            font.draw(batch, "" + stack.getQuantity(), x + CELL_SIZE - 23, y + 4);
        }
    }

    private boolean isMouseOverCell(int gridX, int gridY) {
        float cellX = uiX + UI_PADDING + gridX * (CELL_SIZE + CELL_PADDING);
        float cellY = uiY + UI_PADDING + gridY * (CELL_SIZE + CELL_PADDING);

        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

        return mouseX >= cellX && mouseX <= cellX + CELL_SIZE &&
               mouseY >= cellY && mouseY <= cellY + CELL_SIZE;
    }

    /**
     * Convert screen coordinates to grid coordinates.
     * @return Vector2 with x,y grid coords, or null if outside grid
     */
    private Vector2 screenToGrid(float screenX, float screenY) {
        float relX = screenX - uiX - UI_PADDING;
        float relY = screenY - uiY - UI_PADDING;

        int gridX = (int)(relX / (CELL_SIZE + CELL_PADDING));
        int gridY = (int)(relY / (CELL_SIZE + CELL_PADDING));

        if (gridX >= 0 && gridX < inventory.getGridWidth() &&
            gridY >= 0 && gridY < inventory.getGridHeight()) {
            return new Vector2(gridX, gridY);
        }

        return null;
    }

    /**
     * Handle mouse click on inventory.
     * @return true if the click was handled
     */
    public boolean handleClick(float worldX, float worldY) {
        Vector2 gridPos = screenToGrid(worldX, worldY);
        if (gridPos == null) {
            return false;
        }

        int gridX = (int)gridPos.x;
        int gridY = (int)gridPos.y;

        if (draggedItem == null) {
            // Start dragging
            ItemStack stack = inventory.getItemAt(gridX, gridY);
            if (stack != null) {
                draggedItem = inventory.removeItem(gridX, gridY);
                draggedFromX = gridX;
                draggedFromY = gridY;
                dragOffset.set(0, 0);
                return true;
            }
        } else {
            // Drop item
            if (inventory.placeItem(draggedItem, gridX, gridY)) {
                draggedItem = null;
            } else {
                // Can't place here, return to original position
                inventory.placeItem(draggedItem, draggedFromX, draggedFromY);
                draggedItem = null;
            }
            return true;
        }

        return false;
    }

    public void dispose() {
        shapeRenderer.dispose();
        font.dispose();
    }

    public float getWidth() {
        return uiWidth;
    }

    public float getHeight() {
        return uiHeight;
    }
}
