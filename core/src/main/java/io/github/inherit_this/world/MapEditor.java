package io.github.inherit_this.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import io.github.inherit_this.util.Constants;
import io.github.inherit_this.audio.SoundManager;
import io.github.inherit_this.audio.SoundType;

/**
 * In-game map editor for creating and editing static world maps.
 * Toggle with F2 key.
 */
public class MapEditor {

    private boolean active = false;
    private StaticWorld staticWorld;
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch paletteBatch;
    private TileTextureManager textureManager;

    // Hover preview
    private int hoveredTileX = -1;
    private int hoveredTileY = -1;
    private boolean hasHoveredTile = false;

    // Edit mode
    private EditMode editMode = EditMode.TILE;

    // Available tile types
    private String[] tileTypes = {
            "grass_1", "grass_2", "grass_3", "grass_4", "grass_5", "grass_6",
            "mossy_stone_1", "mossy_stone_2",
            "path_1",
            "sand_1",
            "stone_1", "stone_2",
            "wood_left_pillar", "wood_right_pillar", "wood_wall", "wood_window_1"
    };
    private int selectedTileIndex = 0;
    private TileLayer selectedLayer = TileLayer.GROUND; // Current layer being edited
    private int selectedDirection = 0; // Direction (0=N, 1=E, 2=S, 3=W)
    private int selectedLevel = 0; // Height level for vertical stacking (0-15)
    private boolean selectedFlipped = false; // Whether texture is flipped horizontally
    private int selectedTextureRotation = 0; // Texture rotation in 90° increments (0-3: 0°, 90°, 180°, 270°)

    // Available object types
    private String[] objectTypes = {
            "Crate", "Pot", "Barrel", "Chest"
    };
    private int selectedObjectIndex = 0;

    // Callback for placing objects
    private ObjectPlacementCallback objectCallback;

    // Undo/Redo command history
    private java.util.Stack<EditorCommand> undoStack = new java.util.Stack<>();
    private java.util.Stack<EditorCommand> redoStack = new java.util.Stack<>();

    // UI state
    private static final int PALETTE_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 40;
    private boolean showPalette = true;

    public enum EditMode {
        TILE, OBJECT
    }

    public interface ObjectPlacementCallback {
        void placeObject(String objectType, float tileX, float tileY);
    }

    public MapEditor(BitmapFont font) {
        this.font = font;
        this.shapeRenderer = new ShapeRenderer();
        this.paletteBatch = new SpriteBatch();
        this.textureManager = TileTextureManager.getInstance();
    }

    /**
     * Sets the StaticWorld to edit. Only works if world is a StaticWorld.
     */
    public void setWorld(WorldProvider world) {
        if (world instanceof StaticWorld) {
            this.staticWorld = (StaticWorld) world;
        } else {
            this.staticWorld = null;
            Gdx.app.log("MapEditor", "Cannot edit non-static worlds. Switch to a StaticWorld first.");
        }
    }

    /**
     * Sets the callback for placing objects in the game world.
     */
    public void setObjectPlacementCallback(ObjectPlacementCallback callback) {
        this.objectCallback = callback;
    }

    /**
     * Toggles the map editor on/off.
     */
    public void toggle() {
        active = !active;
        if (active && staticWorld == null) {
            Gdx.app.log("MapEditor", "Map editor requires a StaticWorld. Current world is not editable.");
            active = false;
        }
    }

    public boolean isActive() {
        return active;
    }

    /**
     * Updates the hovered tile position for preview rendering.
     */
    public void setHoveredTile(int tileX, int tileY) {
        this.hoveredTileX = tileX;
        this.hoveredTileY = tileY;
        this.hasHoveredTile = true;
    }

    /**
     * Clears the hovered tile (when cursor leaves valid area).
     */
    public void clearHoveredTile() {
        this.hasHoveredTile = false;
    }

    /**
     * Gets the currently selected tile texture for preview rendering.
     */
    public com.badlogic.gdx.graphics.Texture getSelectedTileTexture() {
        if (editMode == EditMode.TILE && selectedTileIndex < tileTypes.length) {
            String tileName = tileTypes[selectedTileIndex];
            return textureManager.getTexture("tiles/" + tileName + ".png");
        }
        return null;
    }

    /**
     * Returns true if there is a valid hovered tile to preview.
     */
    public boolean hasHoveredTile() {
        return hasHoveredTile;
    }

    /**
     * Gets the hovered tile X coordinate.
     */
    public int getHoveredTileX() {
        return hoveredTileX;
    }

    /**
     * Gets the hovered tile Y coordinate.
     */
    public int getHoveredTileY() {
        return hoveredTileY;
    }

    /**
     * Gets the currently selected layer.
     */
    public TileLayer getSelectedLayer() {
        return selectedLayer;
    }

    /**
     * Gets the currently selected direction.
     */
    public int getSelectedDirection() {
        return selectedDirection;
    }

    /**
     * Gets whether the selected tile is flipped.
     */
    public boolean getSelectedFlipped() {
        return selectedFlipped;
    }

    /**
     * Gets the currently selected height level.
     */
    public int getSelectedLevel() {
        return selectedLevel;
    }

    /**
     * Gets the currently selected texture rotation.
     */
    public int getSelectedTextureRotation() {
        return selectedTextureRotation;
    }

    /**
     * Handles input for the map editor.
     * 
     * @param clickedWorldTileX World tile X coordinate of click
     * @param clickedWorldTileY World tile Y coordinate of click
     */
    public void handleClick(int clickedWorldTileX, int clickedWorldTileY) {
        if (!active)
            return;

        if (editMode == EditMode.TILE) {
            if (staticWorld == null)
                return;

            // Create tile data string with layer, direction, level, flipped, and texture rotation
            // Format: "tileType:layer:direction:level:flipped:textureRotation"
            String selectedTile = tileTypes[selectedTileIndex];
            String tileData = selectedTile + ":" + selectedLayer.name() + ":" + selectedDirection + ":" + selectedLevel + ":" + selectedFlipped + ":" + selectedTextureRotation;

            // Validate wall placement if placing on WALL layer
            if (selectedLayer == TileLayer.WALL) {
                if (!staticWorld.canPlaceWall(clickedWorldTileX, clickedWorldTileY, selectedDirection)) {
                    SoundManager.getInstance().play(SoundType.EDITOR_ERROR, 0.6f);
                    Gdx.app.log("MapEditor", "Cannot place wall at (" + clickedWorldTileX + ", " + clickedWorldTileY
                            + ") - no adjacent wall or ground");
                    return;
                }
            }

            // Execute command and add to undo stack
            EditorCommand command = new PlaceTileCommand(staticWorld, clickedWorldTileX, clickedWorldTileY, tileData,
                    selectedLayer);
            executeCommand(command);

            SoundManager.getInstance().play(SoundType.EDITOR_PLACE, 0.6f);
            Gdx.app.log("MapEditor", "Placed " + selectedTile + " (" + selectedLayer + ", dir:" + selectedDirection
                    + ") at (" + clickedWorldTileX + ", " + clickedWorldTileY + ")");
        } else if (editMode == EditMode.OBJECT) {
            if (objectCallback != null) {
                String objectType = objectTypes[selectedObjectIndex];
                objectCallback.placeObject(objectType, clickedWorldTileX, clickedWorldTileY);
                SoundManager.getInstance().play(SoundType.EDITOR_PLACE, 0.6f);
                Gdx.app.log("MapEditor",
                        "Placed " + objectType + " at (" + clickedWorldTileX + ", " + clickedWorldTileY + ")");
            }
        }
    }

    /**
     * Executes a command and adds it to the undo stack.
     */
    private void executeCommand(EditorCommand command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear(); // Clear redo stack when new action is performed
    }

    /**
     * Undoes the last command.
     */
    public void undo() {
        if (!undoStack.isEmpty()) {
            EditorCommand command = undoStack.pop();
            command.undo();
            redoStack.push(command);
            Gdx.app.log("MapEditor", "Undo performed");
        } else {
            Gdx.app.log("MapEditor", "Nothing to undo");
        }
    }

    /**
     * Redoes the last undone command.
     */
    public void redo() {
        if (!redoStack.isEmpty()) {
            EditorCommand command = redoStack.pop();
            command.redo();
            undoStack.push(command);
            Gdx.app.log("MapEditor", "Redo performed");
        } else {
            Gdx.app.log("MapEditor", "Nothing to redo");
        }
    }

    /**
     * Handles keyboard input for the map editor.
     */
    public void handleInput() {
        if (!active)
            return;

        // Undo with Ctrl+Z
        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            undo();
            return;
        }

        // Redo with Ctrl+Y
        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.Y)) {
            redo();
            return;
        }

        // Toggle edit mode with M key
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            editMode = (editMode == EditMode.TILE) ? EditMode.OBJECT : EditMode.TILE;
            SoundManager.getInstance().play(SoundType.EDITOR_MODE_SWITCH, 0.7f);
            Gdx.app.log("MapEditor", "Switched to " + editMode + " mode");
        }

        if (editMode == EditMode.TILE) {
            // Tile mode controls
            // Cycle through tiles with < and >
            if (Gdx.input.isKeyJustPressed(Input.Keys.COMMA)) {
                selectedTileIndex = (selectedTileIndex - 1 + tileTypes.length) % tileTypes.length;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.PERIOD)) {
                selectedTileIndex = (selectedTileIndex + 1) % tileTypes.length;
            }

            // Q/E keys to change layer
            if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
                TileLayer[] layers = TileLayer.values();
                int currentIndex = selectedLayer.ordinal();
                selectedLayer = layers[(currentIndex - 1 + layers.length) % layers.length];
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                TileLayer[] layers = TileLayer.values();
                int currentIndex = selectedLayer.ordinal();
                selectedLayer = layers[(currentIndex + 1) % layers.length];
            }

            // R key to rotate direction (0=N, 1=E, 2=S, 3=W)
            if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
                selectedDirection = (selectedDirection + 1) % 4;
            }

            // [ and ] keys to change height level (0-15)
            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT_BRACKET)) {
                selectedLevel = Math.max(0, selectedLevel - 1);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT_BRACKET)) {
                selectedLevel = Math.min(15, selectedLevel + 1);
            }

            // F key to toggle flipped
            if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
                selectedFlipped = !selectedFlipped;
            }

            // T key to rotate texture (0-3: 0°, 90°, 180°, 270°)
            if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
                selectedTextureRotation = (selectedTextureRotation + 1) % 4;
            }

            // Save map with Ctrl+S
            if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.S)) {
                saveMap();
            }
        } else if (editMode == EditMode.OBJECT) {
            // Object mode controls
            // Number keys to select object type
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1))
                selectedObjectIndex = 0;
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2))
                selectedObjectIndex = 1;
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3))
                selectedObjectIndex = 2;
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4))
                selectedObjectIndex = 3;
        }

        // Toggle palette with Tab (works in both modes)
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            showPalette = !showPalette;
        }
    }

    /**
     * Renders the map editor UI.
     */
    public void render(SpriteBatch batch) {
        if (!active)
            return;

        font.setColor(Color.YELLOW);
        font.draw(batch, "MAP EDITOR MODE (F10 to exit) | Mode: " + editMode + " (M to toggle)", 10,
                Gdx.graphics.getHeight() - 90);

        // Show hovered tile info
        if (hasHoveredTile) {
            font.setColor(Color.CYAN);
            font.draw(batch, "Hovering: (" + hoveredTileX + ", " + hoveredTileY + ")", 10,
                    Gdx.graphics.getHeight() - 190);
            font.setColor(Color.YELLOW);
        }

        if (editMode == EditMode.TILE) {
            // Draw tile mode HUD
            String selectedTile = tileTypes[selectedTileIndex];
            String[] directionNames = { "North", "East", "South", "West" };
            String directionName = directionNames[selectedDirection];

            String rotationDegrees = (selectedTextureRotation * 90) + "°";

            font.draw(batch, "Tile: " + selectedTile + " [" + (selectedTileIndex + 1) + "/" + tileTypes.length + "]",
                    10, Gdx.graphics.getHeight() - 110);
            font.draw(batch, "Layer: " + selectedLayer + " (Q/E) | Direction: " + directionName + " (R) | Level: " + selectedLevel + " ([/])", 10,
                    Gdx.graphics.getHeight() - 130);
            font.draw(batch, "Flipped: " + (selectedFlipped ? "Yes" : "No") + " (F) | Texture Rotation: " + rotationDegrees + " (T)", 10,
                    Gdx.graphics.getHeight() - 150);
            font.draw(batch, "< and >: Change tile | Click: Place tile", 10, Gdx.graphics.getHeight() - 170);
            font.draw(batch, "Ctrl+S: Save | Ctrl+Z: Undo | Ctrl+Y: Redo | Tab: Palette", 10,
                    Gdx.graphics.getHeight() - 190);
        } else if (editMode == EditMode.OBJECT) {
            // Draw object mode HUD
            String selectedObject = objectTypes[selectedObjectIndex];

            font.draw(batch,
                    "Object: " + selectedObject + " [" + (selectedObjectIndex + 1) + "/" + objectTypes.length + "]", 10,
                    Gdx.graphics.getHeight() - 110);
            font.draw(batch, "Arrow Keys: Change object | Click: Place object", 10, Gdx.graphics.getHeight() - 130);
            font.draw(batch, "Tab: Toggle palette", 10, Gdx.graphics.getHeight() - 150);
        }

        font.setColor(Color.WHITE);

        // Draw palette if enabled
        if (showPalette) {
            batch.end();
            renderPalette();
            batch.begin();
        }
    }

    /**
     * Renders the tile palette on the right side of the screen with image previews.
     */
    private void renderPalette() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();
        int paletteX = screenWidth - PALETTE_WIDTH;

        // Background
        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 0.9f);
        shapeRenderer.rect(paletteX, 0, PALETTE_WIDTH, screenHeight);

        // Tile buttons
        for (int i = 0; i < tileTypes.length; i++) {
            int y = screenHeight - (i + 1) * (BUTTON_HEIGHT + 5) - 50;

            if (i == selectedTileIndex) {
                shapeRenderer.setColor(0.5f, 0.7f, 1.0f, 1.0f); // Highlight selected
            } else {
                shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1.0f);
            }
            shapeRenderer.rect(paletteX + 10, y, PALETTE_WIDTH - 20, BUTTON_HEIGHT);
        }

        shapeRenderer.end();

        // Draw tile previews and text labels
        paletteBatch.begin();
        font.setColor(Color.WHITE);
        font.draw(paletteBatch, "TILE PALETTE", paletteX + 15, screenHeight - 20);

        for (int i = 0; i < tileTypes.length; i++) {
            int y = screenHeight - (i + 1) * (BUTTON_HEIGHT + 5) - 50;

            // Draw tile preview
            String tileName = tileTypes[i];
            com.badlogic.gdx.graphics.Texture tileTexture = textureManager.getTexture("tiles/" + tileName + ".png");
            if (tileTexture != null) {
                // Draw tile at 32x32 size on the left side of the button
                paletteBatch.draw(tileTexture, paletteX + 15, y + 4, 32, 32);
            }

            // Draw text label to the right of the preview
            String label = (i + 1) + ". " + tileName;
            font.draw(paletteBatch, label, paletteX + 55, y + BUTTON_HEIGHT / 2 + 5);
        }
        paletteBatch.end();
    }

    /**
     * Saves the current map to a file.
     */
    private void saveMap() {
        if (staticWorld == null)
            return;

        // Save to the original file path instead of creating a new file
        String filename = staticWorld.getMapFilePath();
        staticWorld.saveMap(filename);
        Gdx.app.log("MapEditor", "Map saved to " + filename);
    }

    public void dispose() {
        shapeRenderer.dispose();
        paletteBatch.dispose();
    }
}
