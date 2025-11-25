package io.github.inherit_this.screens;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.inherit_this.Main;
import io.github.inherit_this.items.ItemRegistry;
import io.github.inherit_this.ui.EquipmentUI;
import io.github.inherit_this.ui.HotbarUI;
import io.github.inherit_this.ui.InventoryUI;
import io.github.inherit_this.world.World;
import io.github.inherit_this.world.Chunk;
import io.github.inherit_this.world.Tile;
import io.github.inherit_this.world.TileTextureManager;
import io.github.inherit_this.entities.*;
import io.github.inherit_this.util.Constants;
import io.github.inherit_this.util.FontManager;
import io.github.inherit_this.debug.*;

public class GameScreen extends ScreenAdapter {

    private final Main game;

    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;

    private Texture playerTex;
    private Player player;
    private World world = new World();
    private DebugConsole debugConsole;
    private InputMultiplexer inputMultiplexer;

    // Inventory and equipment system
    private InventoryUI inventoryUI;
    private EquipmentUI equipmentUI;
    private HotbarUI hotbarUI;
    private boolean inventoryOpen = false;

    public GameScreen(Main game) {
        this.game = game;
        this.batch = game.getBatch();

        // Preload all tile textures to prevent stuttering when loading chunks
        TileTextureManager textureManager = TileTextureManager.getInstance();
        textureManager.preloadCommonTextures();

        // Initialize item registry
        ItemRegistry.getInstance();

        // Set up viewport with fixed zoom - assets always same size regardless of window size
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        viewport.apply();
        camera.zoom = 1f / Constants.PIXEL_SCALE; // Scale everything by PIXEL_SCALE
        camera.position.set(0, 0, 0);
        camera.update();

        // Enable pixel-perfect rendering (no texture filtering)
        playerTex = new Texture("character.png");
        playerTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        player = new Player(0, 0, playerTex, game, world); // TODO: load position from save state

        // Initialize inventory, equipment, and hotbar UI
        inventoryUI = new InventoryUI(player.getInventory());
        equipmentUI = new EquipmentUI(player.getEquipment());
        hotbarUI = new HotbarUI(player.getInventory(), player.getStats());

        debugConsole = new DebugConsole();
        debugConsole.registerCommand(new HelpCommand(debugConsole.getCommands()));

        // Player commands
        debugConsole.registerCommand(new NoClipCommand(player));
        debugConsole.registerCommand(new TeleportCommand(player));
        debugConsole.registerCommand(new SetHealthCommand(player));
        debugConsole.registerCommand(new SetLevelCommand(player));
        debugConsole.registerCommand(new AddXPCommand(player));
        debugConsole.registerCommand(new SetGoldCommand(player));

        // Inventory commands
        debugConsole.registerCommand(new GiveCommand(player));
        debugConsole.registerCommand(new ClearInventoryCommand(player));

        // World commands
        debugConsole.registerCommand(new InspectCommand(world));
        debugConsole.registerCommand(new RegenWorldCommand(world));
        debugConsole.registerCommand(new ReloadChunkCommand(world, player));

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(debugConsole);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render(float delta) {
        handleInput();

        if (!debugConsole.isOpen() && !inventoryOpen) {
            player.update(delta);
        }

        camera.position.set(player.getPosition().x, player.getPosition().y, 0);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        renderVisibleChunks();
        player.renderPlayer();

        batch.end();

        // Begin batch for UI rendering (all UIs will use their own screen-space cameras)
        batch.begin();

        // Render inventory and equipment UI if open (they use screen-space cameras)
        if (inventoryOpen) {
            // Calculate total width of both UIs side by side
            float spacing = 20; // Space between inventory and equipment
            float totalWidth = inventoryUI.getWidth() + spacing + equipmentUI.getWidth();

            // Center both UIs on screen using pixel coordinates
            int screenWidth = Gdx.graphics.getWidth();
            int screenHeight = Gdx.graphics.getHeight();

            float inventoryX = (screenWidth - totalWidth) / 2;
            float inventoryY = (screenHeight - inventoryUI.getHeight()) / 2;
            inventoryUI.setPosition(inventoryX, inventoryY);
            inventoryUI.render(batch);

            // Position equipment UI to the right of inventory, centered vertically
            float equipmentX = inventoryX + inventoryUI.getWidth() + spacing;
            float equipmentY = (screenHeight - equipmentUI.getHeight()) / 2;
            equipmentUI.setPosition(equipmentX, equipmentY);
            equipmentUI.render(batch);
        }

        // Always render hotbar at bottom of screen
        hotbarUI.updatePosition(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        hotbarUI.render(batch);

        batch.end();

        debugConsole.render();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.GRAVE)) {
            debugConsole.toggle();
        }

        // Toggle inventory with 'I' key
        if (!debugConsole.isOpen() && Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            inventoryOpen = !inventoryOpen;
        }

        // Handle inventory clicks when open
        if (inventoryOpen && Gdx.input.justTouched()) {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
            // Convert to world coordinates
            inventoryUI.handleClick(mouseX, mouseY);
        }

        // Handle hotbar F1-F5 keys
        if (!debugConsole.isOpen() && !inventoryOpen) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
                useHotbarSlot(0);
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.F2)) {
                useHotbarSlot(1);
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
                useHotbarSlot(2);
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.F4)) {
                useHotbarSlot(3);
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.F5)) {
                useHotbarSlot(4);
            }
        }

        if (!debugConsole.isOpen() && !inventoryOpen && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new PauseScreen(game, this));
        }
    }

    /**
     * Use an item from the hotbar.
     */
    private void useHotbarSlot(int slotIndex) {
        // TODO: Implement item usage logic
        // For now, just log that the key was pressed
        System.out.println("Hotbar slot " + (slotIndex + 1) + " pressed");
    }

    private void renderVisibleChunks() {
        float left = camera.position.x - camera.viewportWidth / 2f;
        float right = camera.position.x + camera.viewportWidth / 2f;
        float bottom = camera.position.y - camera.viewportHeight / 2f;
        float top = camera.position.y + camera.viewportHeight / 2f;

        int chunkXStart = (int) Math.floor(left / Constants.CHUNK_PIXEL_SIZE);
        int chunkXEnd   = (int) Math.floor(right / Constants.CHUNK_PIXEL_SIZE);
        int chunkYStart = (int) Math.floor(bottom / Constants.CHUNK_PIXEL_SIZE);
        int chunkYEnd   = (int) Math.floor(top / Constants.CHUNK_PIXEL_SIZE);

        for (int cx = chunkXStart; cx <= chunkXEnd; cx++) {
            for (int cy = chunkYStart; cy <= chunkYEnd; cy++) {
                renderChunk(cx, cy);
            }
        }
    }

    private void renderChunk(int cx, int cy) {
        Chunk chunk = world.getOrCreateChunk(cx, cy);
        float baseX = cx * Constants.CHUNK_PIXEL_SIZE;
        float baseY = cy * Constants.CHUNK_PIXEL_SIZE;

        for (int x = 0; x < Constants.CHUNK_SIZE; x++) {
            for (int y = 0; y < Constants.CHUNK_SIZE; y++) {
                Tile tile = chunk.getTile(x, y);
                float tileScreenX = baseX + x * Constants.TILE_SIZE;
                float tileScreenY = baseY + y * Constants.TILE_SIZE;

                batch.enableBlending();

                batch.draw(tile.getTexture(), tileScreenX, tileScreenY, Constants.TILE_SIZE, Constants.TILE_SIZE);

                int worldTileX = cx * Constants.CHUNK_SIZE + x;
                int worldTileY = cy * Constants.CHUNK_SIZE + y;

                // Only blend tiles of different types (grass-stone, not grass-grass)
                Tile leftNeighbor = world.getTileAtWorldCoords(worldTileX - 1, worldTileY);
                if (leftNeighbor != null && leftNeighbor.getType() != tile.getType()) {
                    batch.setColor(1, 1, 1, 0.5f);
                    batch.draw(
                        leftNeighbor.getTexture(),
                        tileScreenX, tileScreenY,
                        Constants.EDGE_BLEND_SIZE, Constants.TILE_SIZE,
                        Constants.TILE_SIZE - (int) Constants.EDGE_BLEND_SIZE, 0,
                        (int) Constants.EDGE_BLEND_SIZE, Constants.TILE_SIZE,
                        false, false
                    );
                    batch.setColor(1, 1, 1, 1f);
                }

                Tile rightNeighbor = world.getTileAtWorldCoords(worldTileX + 1, worldTileY);
                if (rightNeighbor != null && rightNeighbor.getType() != tile.getType()) {
                    batch.setColor(1, 1, 1, 0.5f);
                    batch.draw(
                        rightNeighbor.getTexture(),
                        tileScreenX + Constants.TILE_SIZE - Constants.EDGE_BLEND_SIZE, tileScreenY,
                        Constants.EDGE_BLEND_SIZE, Constants.TILE_SIZE,
                        0, 0,
                        (int) Constants.EDGE_BLEND_SIZE, Constants.TILE_SIZE,
                        false, false
                    );
                    batch.setColor(1, 1, 1, 1f);
                }

                Tile bottomNeighbor = world.getTileAtWorldCoords(worldTileX, worldTileY - 1);
                if (bottomNeighbor != null && bottomNeighbor.getType() != tile.getType()) {
                    batch.setColor(1, 1, 1, 0.5f);
                    batch.draw(
                        bottomNeighbor.getTexture(),
                        tileScreenX, tileScreenY,
                        Constants.TILE_SIZE, Constants.EDGE_BLEND_SIZE,
                        0, Constants.TILE_SIZE - (int) Constants.EDGE_BLEND_SIZE,
                        Constants.TILE_SIZE, (int) Constants.EDGE_BLEND_SIZE,
                        false, false
                    );
                    batch.setColor(1, 1, 1, 1f);
                }

                Tile topNeighbor = world.getTileAtWorldCoords(worldTileX, worldTileY + 1);
                if (topNeighbor != null && topNeighbor.getType() != tile.getType()) {
                    batch.setColor(1, 1, 1, 0.5f);
                    batch.draw(
                        topNeighbor.getTexture(),
                        tileScreenX, tileScreenY + Constants.TILE_SIZE - Constants.EDGE_BLEND_SIZE,
                        Constants.TILE_SIZE, Constants.EDGE_BLEND_SIZE,
                        0, 0,
                        Constants.TILE_SIZE, (int) Constants.EDGE_BLEND_SIZE,
                        false, false
                    );
                    batch.setColor(1, 1, 1, 1f);
                }
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        // Update viewport when window is resized
        // FitViewport will maintain aspect ratio and add black bars if needed
        viewport.update(width, height);

        // Update UI cameras to prevent stretching
        debugConsole.updateCamera();
        hotbarUI.updateCamera();
        inventoryUI.updateCamera();
        equipmentUI.updateCamera();
    }

    @Override
    public void dispose() {
        playerTex.dispose();
        TileTextureManager.getInstance().dispose();
        ItemRegistry.getInstance().dispose();
        FontManager.getInstance().dispose();
        inventoryUI.dispose();
        equipmentUI.dispose();
        hotbarUI.dispose();
    }
}
