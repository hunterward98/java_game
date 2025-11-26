package io.github.inherit_this.screens;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.inherit_this.Main;
import io.github.inherit_this.items.ItemRegistry;

import java.util.List;
import io.github.inherit_this.ui.EquipmentUI;
import io.github.inherit_this.ui.HotbarUI;
import io.github.inherit_this.ui.InventoryUI;
import io.github.inherit_this.world.World;
import io.github.inherit_this.world.Chunk;
import io.github.inherit_this.world.Tile;
import io.github.inherit_this.world.TileTextureManager;
import io.github.inherit_this.world.TileMesh3D;
import io.github.inherit_this.entities.*;
import io.github.inherit_this.util.Constants;
import io.github.inherit_this.util.FontManager;
import io.github.inherit_this.debug.*;

public class GameScreen extends ScreenAdapter {

    private final Main game;

    private PerspectiveCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private ModelBatch modelBatch;
    private Environment environment;

    // Camera rotation controls
    private float cameraAngle = 0f; // Rotation around Y axis (in degrees)
    private float cameraTilt = 45f;  // Tilt angle (30-70 degrees to prevent going under world)
    private float cameraDistance = 300f; // Distance from player

    // Camera limits
    private static final float MIN_CAMERA_DISTANCE = 200f;
    private static final float MAX_CAMERA_DISTANCE = 400f;
    private static final float MIN_CAMERA_TILT = 15f;
    private static final float MAX_CAMERA_TILT = 65f;
    private static final float ZOOM_SPEED = 25f; // Distance change per scroll notch

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

    // Fixed time step for game logic
    private static final float FIXED_TIME_STEP = 1f / 60f; // 60 ticks per second
    private float accumulator = 0f;

    // Performance tracking
    private BitmapFont fpsFont;
    private int frameCount = 0;
    private float fpsTimer = 0f;
    private int currentFPS = 0;

    public GameScreen(Main game) {
        this.game = game;
        this.batch = game.getBatch();

        // Preload all tile textures to prevent stuttering when loading chunks
        TileTextureManager textureManager = TileTextureManager.getInstance();
        textureManager.preloadCommonTextures();

        // Initialize item registry
        ItemRegistry.getInstance();

        // Set up 3D perspective camera
        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.near = 1f;
        camera.far = 2000f; // Increased from 1000 to help with distant chunk rendering
        viewport = new ScreenViewport(camera);
        viewport.apply();

        // Initialize 3D rendering
        modelBatch = new ModelBatch();

        // Set up lighting environment
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        // Enable pixel-perfect rendering (no texture filtering)
        playerTex = new Texture("character.png");
        playerTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        player = new Player(0, 0, playerTex, world);
         world.preloadChunks(20);
        
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

        // Set up input handling with scroll wheel support
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(new ScrollInputProcessor());
        inputMultiplexer.addProcessor(debugConsole);
        Gdx.input.setInputProcessor(inputMultiplexer);

        // Initialize FPS font (use default libGDX font for now)
        fpsFont = new BitmapFont();

        // Enable VSync to prevent screen tearing
        Gdx.graphics.setVSync(true);
    }

    /**
     * Custom input processor to handle scroll wheel zoom.
     */
    private class ScrollInputProcessor extends InputAdapter {
        @Override
        public boolean scrolled(float amountX, float amountY) {
            if (!debugConsole.isOpen() && !inventoryOpen) {
                // Scroll up (negative) = zoom in, scroll down (positive) = zoom out
                cameraDistance += amountY * ZOOM_SPEED;
                cameraDistance = Math.max(MIN_CAMERA_DISTANCE, Math.min(MAX_CAMERA_DISTANCE, cameraDistance));
                return true;
            }
            return false;
        }
    }

    @Override
    public void render(float delta) {
        // Clear screen - any void will be sky blue
        ScreenUtils.clear(0.53f, 0.81f, 0.92f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // updateFPSCounter(delta);

        handleInput();
        handleCameraRotation(delta);

        // Fixed time step for consistent game logic
        accumulator += Math.min(delta, 0.25f); // Cap delta to prevent spiral of death

        while (accumulator >= FIXED_TIME_STEP) {
            // Update game logic at fixed rate
            if (!debugConsole.isOpen() && !inventoryOpen) {
                player.update(FIXED_TIME_STEP);
            }
            accumulator -= FIXED_TIME_STEP;
        }

        updateCameraPosition();

        // Enable depth testing for 3D rendering
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

        // Render 3D world and player
        modelBatch.begin(camera);
        renderVisibleChunks3D();
        player.renderPlayer(modelBatch, camera);
        modelBatch.end();

        // Disable depth test for UI rendering
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);

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

        hotbarUI.updatePosition(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        hotbarUI.render(batch);

        // Render performance info
        fpsFont.draw(batch, "FPS: " + currentFPS, 10, Gdx.graphics.getHeight() - 10);
        fpsFont.draw(batch, "Frame Time: " + String.format("%.2f", delta * 1000) + "ms", 10, Gdx.graphics.getHeight() - 30);
        fpsFont.draw(batch, "Zoom: " + (int)cameraDistance, 10, Gdx.graphics.getHeight() - 50);
        fpsFont.draw(batch, "Pos: (" + (int)player.getPosition().x + ", " + (int)player.getBillboardZ() + ", " + (int)player.getPosition().y + ")", 10, Gdx.graphics.getHeight() - 70);

        // Calculate render radius (matches renderVisibleChunks3D calculation) (this is for speed improvements)
        // int renderRadius = (int) Math.ceil(cameraDistance / 100f);
        // renderRadius = Math.max(6, Math.min(renderRadius, 10));
        // int totalInRadius = (renderRadius * 2 + 1) * (renderRadius * 2 + 1);
        // fpsFont.draw(batch, "Chunks: " + chunksRenderedLastFrame + "/" + totalInRadius + " (R:" + renderRadius + ")", 10, Gdx.graphics.getHeight() - 90);
        // fpsFont.draw(batch, "Culled: " + chunksCulledLastFrame + " | Loaded: " + world.getLoadedChunkCount(), 10, Gdx.graphics.getHeight() - 110);

        debugConsole.render();
        batch.end();

    }

    /**
     * Updates the FPS counter.
     */
    private void updateFPSCounter(float delta) {
        frameCount++;
        fpsTimer += delta;

        if (fpsTimer >= 1.0f) {
            currentFPS = frameCount;
            frameCount = 0;
            fpsTimer = 0f;
        }
    }

    /**
     * Updates camera position based on player position and rotation angles.
     */
    private void updateCameraPosition() {
        float playerX = player.getPosition().x;
        float playerY = player.getPosition().y;

        // Calculate camera position using spherical coordinates
        // Convert angles to radians
        float angleRad = (float) Math.toRadians(cameraAngle);
        float tiltRad = (float) Math.toRadians(cameraTilt);

        // Calculate camera offset from player
        float offsetX = cameraDistance * (float) Math.sin(angleRad) * (float) Math.cos(tiltRad);
        float offsetZ = cameraDistance * (float) Math.cos(angleRad) * (float) Math.cos(tiltRad);
        float offsetY = cameraDistance * (float) Math.sin(tiltRad);

        // Position camera
        camera.position.set(
            playerX + offsetX,
            offsetY,
            playerY + offsetZ
        );

        // Look at player (y=0 in 3D world space is ground level)
        camera.lookAt(playerX, 0, playerY);
        camera.up.set(0, 1, 0);
        camera.update();
    }

    /**
     * Handles arrow key camera rotation (scroll wheel zoom is handled by ScrollInputProcessor).
     */
    private void handleCameraRotation(float delta) {
        if (debugConsole.isOpen() || inventoryOpen) {
            return; // Don't rotate camera when UI is open
        }

        float rotationSpeed = 90f; // degrees per second

        // Rotate around player with arrow keys
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            cameraAngle -= rotationSpeed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            cameraAngle += rotationSpeed * delta;
        }

        // Tilt camera up/down (with constraints to prevent going under world)
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            cameraTilt = Math.min(MAX_CAMERA_TILT, cameraTilt + rotationSpeed * delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            cameraTilt = Math.max(MIN_CAMERA_TILT, cameraTilt - rotationSpeed * delta);
        }

        // Keep angle in 0-360 range
        if (cameraAngle >= 360f) cameraAngle -= 360f;
        if (cameraAngle < 0f) cameraAngle += 360f;
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
            inventoryUI.handleClick(mouseX, mouseY);
        }

        // Handle hold-to-move (like FATE/Diablo)
        if (!debugConsole.isOpen() && !inventoryOpen && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            // Get mouse position and raycast to ground
            Vector3 groundPosition = getGroundPositionFromMouse();
            if (groundPosition != null) {
                player.setTargetPosition(groundPosition.x, groundPosition.z);
            }
        } else if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            // Stop moving when mouse button is released
            player.stopMoving();
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

    /**
     * Raycasts from the mouse position to the ground plane (y=0) and returns the intersection point.
     * This is used for accurate click-to-move in 3D space.
     */
    private Vector3 getGroundPositionFromMouse() {
        int screenX = Gdx.input.getX();
        int screenY = Gdx.input.getY();

        // Create a ray from the camera through the mouse position
        Vector3 near = camera.unproject(new Vector3(screenX, screenY, 0));
        Vector3 far = camera.unproject(new Vector3(screenX, screenY, 1));
        Vector3 rayDirection = far.sub(near).nor();

        // Ground plane is at y = 0
        float groundY = 0f;

        // Calculate intersection with ground plane
        // Ray equation: point = origin + t * direction
        // Plane equation: y = groundY
        // Solve for t: origin.y + t * direction.y = groundY
        float t = (groundY - near.y) / rayDirection.y;

        if (t < 0) {
            // Intersection is behind the camera
            return null;
        }

        // Calculate intersection point
        Vector3 intersection = new Vector3(
            near.x + t * rayDirection.x,
            groundY,
            near.z + t * rayDirection.z
        );

        return intersection;
    }

    /**
     * Renders visible chunks in 3D with frustum culling.
     * Only renders chunks that are actually visible in the camera's view frustum.
     * This provides massive performance improvement over rendering all chunks in radius.
     */
    private void renderVisibleChunks3D() {
        float playerX = player.getPosition().x;
        float playerY = player.getPosition().y;

        // Dynamic render radius based on camera distance
        // More zoom out = render more chunks to fill screen
        // Reduced max from 16 to 10 to prevent FPS drops
        int renderRadius = (int) Math.ceil(cameraDistance / 100f);
        renderRadius = Math.max(6, Math.min(renderRadius, 10)); // Clamp between 6-10 chunks

        int playerChunkX = (int) Math.floor(playerX / Constants.CHUNK_PIXEL_SIZE);
        int playerChunkY = (int) Math.floor(playerY / Constants.CHUNK_PIXEL_SIZE);

        int chunksRendered = 0;
        int chunksCulled = 0;

        for (int cx = playerChunkX - renderRadius; cx <= playerChunkX + renderRadius; cx++) {
            for (int cy = playerChunkY - renderRadius; cy <= playerChunkY + renderRadius; cy++) {
                // Calculate chunk bounds in world space
                float chunkWorldX = cx * Constants.CHUNK_PIXEL_SIZE;
                float chunkWorldZ = cy * Constants.CHUNK_PIXEL_SIZE;
                float chunkSize = Constants.CHUNK_PIXEL_SIZE;

                // Create a simple bounding box for the chunk
                // Center point of the chunk
                float centerX = chunkWorldX + chunkSize / 2f;
                float centerZ = chunkWorldZ + chunkSize / 2f;
                float centerY = 0f; // Chunks are flat on ground

                // Radius that encompasses the entire chunk (diagonal)
                float boundingSphereRadius = (float) Math.sqrt(chunkSize * chunkSize * 2) / 2f;

                // Frustum culling: Check if chunk is visible
                if (camera.frustum.sphereInFrustum(centerX, centerY, centerZ, boundingSphereRadius)) {
                    renderChunk3D(cx, cy);
                    chunksRendered++;
                } else {
                    chunksCulled++;
                }
            }
        }

        // Store for debug display
        this.chunksRenderedLastFrame = chunksRendered;
        this.chunksCulledLastFrame = chunksCulled;
    }

    private int chunksRenderedLastFrame = 0;
    private int chunksCulledLastFrame = 0;

    /**
     * Renders a single chunk in 3D using cached ModelInstances.
     * MASSIVE performance improvement - no object creation per frame!
     */
    private void renderChunk3D(int cx, int cy) {
        Chunk chunk = world.getOrCreateChunk(cx, cy);

        // Get cached models (created once, reused every frame)
        List<ModelInstance> models = chunk.getCachedModels();

        // Render all tiles in this chunk
        for (ModelInstance model : models) {
            modelBatch.render(model, environment);
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
        player.dispose();
        modelBatch.dispose();
        fpsFont.dispose();
        TileMesh3D.getInstance().dispose();
        TileTextureManager.getInstance().dispose();
        ItemRegistry.getInstance().dispose();
        FontManager.getInstance().dispose();
        inventoryUI.dispose();
        equipmentUI.dispose();
        hotbarUI.dispose();
    }
}
