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
import io.github.inherit_this.ui.InventoryUI;
import io.github.inherit_this.world.World;
import io.github.inherit_this.world.Chunk;
import io.github.inherit_this.world.Tile;
import io.github.inherit_this.world.TileTextureManager;
import io.github.inherit_this.entities.*;
import io.github.inherit_this.util.Constants;
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

    // Inventory system
    private InventoryUI inventoryUI;
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

        // Initialize inventory UI
        inventoryUI = new InventoryUI(player.getInventory());

        debugConsole = new DebugConsole();
        debugConsole.registerCommand(new HelpCommand(debugConsole.getCommands()));
        debugConsole.registerCommand(new NoClipCommand(player));
        debugConsole.registerCommand(new RegenWorldCommand(world));
        debugConsole.registerCommand(new ReloadChunkCommand(world, player));
        debugConsole.registerCommand(new GiveCommand(player));

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

        // Render inventory UI if open
        if (inventoryOpen) {
            inventoryUI.render(batch);
        }

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

        if (!debugConsole.isOpen() && !inventoryOpen && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new PauseScreen(game, this));
        }
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
    }

    @Override
    public void dispose() {
        playerTex.dispose();
        TileTextureManager.getInstance().dispose();
        ItemRegistry.getInstance().dispose();
        inventoryUI.dispose();
    }
}
