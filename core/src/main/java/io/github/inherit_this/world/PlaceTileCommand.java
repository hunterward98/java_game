package io.github.inherit_this.world;

/**
 * Command for placing a tile in the map editor.
 * Supports undo/redo by storing previous state.
 */
public class PlaceTileCommand implements EditorCommand {

    private StaticWorld staticWorld;
    private int worldTileX;
    private int worldTileY;
    private String newTileData;
    private TileLayer layer;
    private String previousTileData;  // State before this command was executed

    public PlaceTileCommand(StaticWorld staticWorld, int worldTileX, int worldTileY,
                           String newTileData, TileLayer layer) {
        this.staticWorld = staticWorld;
        this.worldTileX = worldTileX;
        this.worldTileY = worldTileY;
        this.newTileData = newTileData;
        this.layer = layer;

        // Capture current state for undo
        String key = worldTileX + "," + worldTileY;
        this.previousTileData = staticWorld.getMapData().tiles.get(key);
    }

    @Override
    public void execute() {
        staticWorld.setTileAt(worldTileX, worldTileY, newTileData, layer);
    }

    @Override
    public void undo() {
        // Restore previous state
        String key = worldTileX + "," + worldTileY;
        if (previousTileData == null) {
            staticWorld.getMapData().tiles.remove(key);
        } else {
            staticWorld.getMapData().tiles.put(key, previousTileData);
        }

        // Recreate chunk to reflect changes
        staticWorld.recreateChunkAt(worldTileX, worldTileY);
    }

    @Override
    public void redo() {
        execute();
    }
}
