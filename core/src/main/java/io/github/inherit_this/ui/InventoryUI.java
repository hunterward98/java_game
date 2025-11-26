package io.github.inherit_this.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import io.github.inherit_this.items.Inventory;
import io.github.inherit_this.items.ItemStack;
import io.github.inherit_this.util.FontManager;
import java.util.HashSet;
import java.util.Set;

/**
 * FATE-style grid-based inventory UI with drag-and-drop support.
 */
public class InventoryUI {
    private final Inventory inventory;
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;
    private final ItemTooltip tooltip;
    private final OrthographicCamera camera;
    private final SpriteBatch batch;  // Own batch like debug console

    // UI Layout
    private static final int CELL_SIZE = 48;
    private static final int CELL_PADDING = 2;
    private static final int UI_PADDING = 10;
    private static final Color GRID_COLOR = new Color(0.3f, 0.3f, 0.3f, 0.9f);
    private static final Color CELL_COLOR = new Color(0.1f, 0.1f, 0.1f, 0.9f);
    private static final Color CELL_HOVER_COLOR = new Color(0.2f, 0.4f, 0.6f, 0.9f);

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
        this.font = FontManager.getInstance().getInventoryFont(); // Use smaller font for inventory cells
        this.tooltip = new ItemTooltip();
        this.batch = new SpriteBatch();  // Create own batch like debug console

        // Screen-space camera to prevent UI from following player
        this.camera = new OrthographicCamera();
        updateCameraProjection();

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
    public void render(SpriteBatch unusedBatch) {
        // Update camera and set projection matrices to use screen-space coordinates (like debug console)
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        // Draw background panel
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

                // Determine cell color (only highlight if cell has an item)
                Color cellColor = CELL_COLOR;
                if (isMouseOverCell(x, y) && inventory.getItemAt(x, y) != null) {
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

        // Draw hover outline for multi-cell items (clean outline without middle lines)
        if (draggedItem == null) {
            float mouseX = com.badlogic.gdx.Gdx.input.getX();
            float mouseY = com.badlogic.gdx.Gdx.graphics.getHeight() - com.badlogic.gdx.Gdx.input.getY();
            Vector2 gridPos = screenToGrid(mouseX, mouseY);

            if (gridPos != null) {
                int gridX = (int)gridPos.x;
                int gridY = (int)gridPos.y;
                ItemStack hoveredStack = inventory.getItemAt(gridX, gridY);

                if (hoveredStack != null) {
                    // Find the top-left corner of this item
                    int topLeftX = findItemTopLeftX(hoveredStack, gridX, gridY);
                    int topLeftY = findItemTopLeftY(hoveredStack, gridX, gridY);

                    // Calculate outline dimensions based on item size
                    int itemWidth = hoveredStack.getItem().getWidth();
                    int itemHeight = hoveredStack.getItem().getHeight();
                    float outlineX = uiX + UI_PADDING + topLeftX * (CELL_SIZE + CELL_PADDING);
                    float outlineY = uiY + UI_PADDING + topLeftY * (CELL_SIZE + CELL_PADDING);
                    float outlineWidth = itemWidth * (CELL_SIZE + CELL_PADDING) - CELL_PADDING;
                    float outlineHeight = itemHeight * (CELL_SIZE + CELL_PADDING) - CELL_PADDING;

                    // Draw thick outline around entire item
                    shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                    com.badlogic.gdx.Gdx.gl.glLineWidth(3);
                    shapeRenderer.setColor(new Color(0.9f, 0.9f, 0.3f, 1.0f)); // Yellow highlight
                    shapeRenderer.rect(outlineX, outlineY, outlineWidth, outlineHeight);
                    com.badlogic.gdx.Gdx.gl.glLineWidth(1); // Reset line width
                    shapeRenderer.end();
                }
            }
        }

        // Begin batch for drawing (like debug console)
        batch.begin();

        // Reset batch color to white to prevent darkness from 3D rendering environment
        batch.setColor(Color.WHITE);

        // Draw items (use set to prevent drawing multi-cell items multiple times)
        Set<ItemStack> drawnStacks = new HashSet<>();
        for (int x = 0; x < inventory.getGridWidth(); x++) {
            for (int y = 0; y < inventory.getGridHeight(); y++) {
                ItemStack stack = inventory.getItemAt(x, y);
                if (stack != null && stack != draggedItem && !drawnStacks.contains(stack)) {
                    drawItemStack(this.batch, stack, x, y);
                    drawnStacks.add(stack);
                }
            }
        }

        // Draw dragged item at mouse position
        if (draggedItem != null) {
            float mouseX = Gdx.input.getX() + dragOffset.x;
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY() + dragOffset.y;
            drawDraggedItem(this.batch, draggedItem, mouseX, mouseY);
        }

        // Draw title and gold (use integer coordinates for pixel-perfect rendering)
        font.setColor(Color.WHITE);
        font.draw(batch, "Inventory", Math.round(uiX + UI_PADDING), Math.round(uiY + uiHeight - UI_PADDING));
        font.draw(batch, "Gold: " + inventory.getGold(), Math.round(uiX + uiWidth - 100), Math.round(uiY + uiHeight - UI_PADDING));

        // Draw tooltip for hovered item (if not dragging)
        if (draggedItem == null) {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
            Vector2 gridPos = screenToGrid(mouseX, mouseY);

            if (gridPos != null) {
                int gridX = (int)gridPos.x;
                int gridY = (int)gridPos.y;
                ItemStack hoveredStack = inventory.getItemAt(gridX, gridY);

                if (hoveredStack != null) {
                    tooltip.render(batch, hoveredStack.getItem(), mouseX, mouseY);
                }
            }
        }

        batch.end();
    }

    private void drawItemStack(SpriteBatch batch, ItemStack stack, int gridX, int gridY) {
        float cellX = uiX + UI_PADDING + gridX * (CELL_SIZE + CELL_PADDING);
        float cellY = uiY + UI_PADDING + gridY * (CELL_SIZE + CELL_PADDING);

        // Calculate render dimensions based on item size
        int itemWidth = stack.getItem().getWidth();
        int itemHeight = stack.getItem().getHeight();
        float renderWidth = itemWidth * CELL_SIZE + (itemWidth - 1) * CELL_PADDING - 8;
        float renderHeight = itemHeight * CELL_SIZE + (itemHeight - 1) * CELL_PADDING - 8;

        // Draw item icon across all cells it occupies
        batch.draw(stack.getItem().getIcon(), cellX + 4, cellY + 4, renderWidth, renderHeight);

        // Draw quantity if stackable (use integer coordinates for pixel-perfect rendering)
        if (stack.getItem().isStackable() && stack.getQuantity() > 1) {
            font.setColor(Color.WHITE);
            font.draw(batch, "" + stack.getQuantity(), Math.round(cellX + CELL_SIZE - 15), Math.round(cellY + 12));
        }
    }

    private void drawDraggedItem(SpriteBatch batch, ItemStack stack, float x, float y) {
        // Calculate render dimensions based on item size
        int itemWidth = stack.getItem().getWidth();
        int itemHeight = stack.getItem().getHeight();
        float renderWidth = itemWidth * CELL_SIZE + (itemWidth - 1) * CELL_PADDING - 8;
        float renderHeight = itemHeight * CELL_SIZE + (itemHeight - 1) * CELL_PADDING - 8;

        // Draw semi-transparent item being dragged
        batch.setColor(1, 1, 1, 0.7f);
        batch.draw(stack.getItem().getIcon(), x, y, renderWidth, renderHeight);
        batch.setColor(Color.WHITE);

        // Draw quantity (use integer coordinates for pixel-perfect rendering)
        if (stack.getItem().isStackable() && stack.getQuantity() > 1) {
            font.setColor(Color.WHITE);
            font.draw(batch, "" + stack.getQuantity(), Math.round(x + CELL_SIZE - 23), Math.round(y + 4));
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
        batch.dispose();  // Dispose our own batch
        // Don't dispose font - it's owned by FontManager singleton
        tooltip.dispose();
    }

    public float getWidth() {
        return uiWidth;
    }

    public float getHeight() {
        return uiHeight;
    }

    /**
     * Finds the top-left X coordinate of a multi-cell item.
     */
    private int findItemTopLeftX(ItemStack stack, int currentX, int currentY) {
        // Search left from current position to find the leftmost cell with this stack
        for (int x = currentX; x >= 0; x--) {
            if (inventory.getItemAt(x, currentY) != stack) {
                return x + 1;
            }
            if (x == 0) {
                return 0;
            }
        }
        return currentX;
    }

    /**
     * Finds the top-left Y coordinate of a multi-cell item.
     */
    private int findItemTopLeftY(ItemStack stack, int currentX, int currentY) {
        // Search down from current position to find the bottommost cell with this stack
        for (int y = currentY; y >= 0; y--) {
            if (inventory.getItemAt(currentX, y) != stack) {
                return y + 1;
            }
            if (y == 0) {
                return 0;
            }
        }
        return currentY;
    }
}
