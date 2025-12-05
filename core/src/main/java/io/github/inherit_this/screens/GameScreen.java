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
import io.github.inherit_this.audio.SoundManager;
import io.github.inherit_this.audio.SoundType;

import java.util.List;
import io.github.inherit_this.ui.EquipmentUI;
import io.github.inherit_this.ui.HotbarUI;
import io.github.inherit_this.ui.InventoryUI;
import io.github.inherit_this.world.World;
import io.github.inherit_this.world.WorldProvider;
import io.github.inherit_this.world.ProceduralWorld;
import io.github.inherit_this.world.StaticWorld;
import io.github.inherit_this.world.Chunk;
import io.github.inherit_this.world.Tile;
import io.github.inherit_this.world.TileTextureManager;
import io.github.inherit_this.world.TileMesh3D;
import io.github.inherit_this.world.TileLayer;
import io.github.inherit_this.entities.*;
import io.github.inherit_this.util.Constants;
import io.github.inherit_this.util.FontManager;
import io.github.inherit_this.debug.*;
import io.github.inherit_this.save.SaveManager;

public class GameScreen extends ScreenAdapter {

    private final Main game;
    private String characterName;
    private long playTimeMillis = 0;
    private long sessionStartTime;

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
    private WorldProvider world;  // Can be StaticWorld (town) or ProceduralWorld (dungeon)
    private DebugConsole debugConsole;
    private InputMultiplexer inputMultiplexer;

    // Inventory and equipment system
    private InventoryUI inventoryUI;
    private EquipmentUI equipmentUI;
    private HotbarUI hotbarUI;
    private boolean inventoryOpen = false;

    // Map editor
    private io.github.inherit_this.world.MapEditor mapEditor;

    // Breakable objects
    private java.util.List<BreakableObject> breakableObjects;

    // Fixed time step for game logic
    private static final float FIXED_TIME_STEP = 1f / 60f; // 60 ticks per second
    private float accumulator = 0f;

    // Performance tracking
    private BitmapFont fpsFont;
    private int frameCount = 0;
    private float fpsTimer = 0f;
    private int currentFPS = 0;

    public GameScreen(Main game, String characterName) {
        this.game = game;
        this.characterName = characterName;
        this.sessionStartTime = System.currentTimeMillis();
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

        // Initialize world - start with StaticWorld for map editing
        // If map file doesn't exist, StaticWorld will create an empty default map
        world = new StaticWorld("maps/default_map.json");
        world.preloadChunks(20);

        // Use spawn coordinates from map if available
        int spawnX = 0;
        int spawnY = 0;
        if (world instanceof StaticWorld) {
            StaticWorld staticWorld = (StaticWorld) world;
            spawnX = staticWorld.getSpawnX();
            spawnY = staticWorld.getSpawnY();
            Gdx.app.log("GameScreen", "Using spawn coordinates: (" + spawnX + ", " + spawnY + ")");
        }
        player = new Player(spawnX, spawnY, playerTex, world);
        
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
        debugConsole.registerCommand(new SwitchWorldCommand(this));

        // Set up input handling with scroll wheel support
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(new ScrollInputProcessor());
        inputMultiplexer.addProcessor(debugConsole);
        Gdx.input.setInputProcessor(inputMultiplexer);

        // Initialize FPS font (use default libGDX font for now)
        fpsFont = new BitmapFont();

        // Initialize map editor
        mapEditor = new io.github.inherit_this.world.MapEditor(fpsFont);
        mapEditor.setWorld(world);

        // Initialize breakable objects list
        breakableObjects = new java.util.ArrayList<>();

        // Set up map editor object placement callback
        mapEditor.setObjectPlacementCallback((objectType, tileX, tileY) -> {
            BreakableObject obj = null;
            switch (objectType) {
                case "Crate":
                    obj = BreakableObjectFactory.createCrate(tileX, tileY);
                    break;
                case "Pot":
                    obj = BreakableObjectFactory.createPot(tileX, tileY);
                    break;
                case "Barrel":
                    obj = BreakableObjectFactory.createBarrel(tileX, tileY);
                    break;
                case "Chest":
                    obj = BreakableObjectFactory.createChest(tileX, tileY);
                    break;
            }
            if (obj != null) {
                addBreakableObject(obj);
            }
        });

        // Add some test breakable objects around spawn point
        addBreakableObject(BreakableObjectFactory.createCrate(2, 2));
        addBreakableObject(BreakableObjectFactory.createPot(4, 2));
        addBreakableObject(BreakableObjectFactory.createBarrel(-2, 3));
        addBreakableObject(BreakableObjectFactory.createChest(0, 5));

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

        // Track play time
        updatePlayTime();

        updateFPSCounter(delta);

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

        // Render 3D world (player will be rendered as 2D overlay later)
        modelBatch.begin(camera);
        renderVisibleChunks3D();

        // Render map editor tile preview
        if (mapEditor.isActive() && mapEditor.hasHoveredTile()) {
            renderTilePreview();
        }

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

        // Render breakable objects (world to screen projection)
        renderBreakableObjects(batch);

        // Render player sprite with perspective scaling
        // Scale inversely with camera distance for proper perspective
        // Base scale multiplied by 1.15 to make player 15% larger relative to tiles
        // At MIN distance (200): scale = 1.73x (closer = bigger)
        // At default (300): scale = 1.15x (normal)
        // At MAX distance (400): scale = 0.86x (farther = smaller)
        float scale = (300f / cameraDistance) * 1.15f;

        Texture playerTexture = player.getTexture();
        float scaledWidth = playerTexture.getWidth() * scale;
        float scaledHeight = playerTexture.getHeight() * scale;

        // Center horizontally, offset vertically by 28px (scaled) so feet anchor to tile
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;
        float yOffset = 28f * scale; // Scale the offset too for consistent positioning

        batch.draw(playerTexture,
            centerX - scaledWidth / 2f,      // Center horizontally
            centerY - scaledHeight / 2f + yOffset,  // Center vertically + offset for feet
            scaledWidth, scaledHeight
        );

        // Render performance info
        fpsFont.draw(batch, "FPS: " + currentFPS, 10, Gdx.graphics.getHeight() - 10);
        fpsFont.draw(batch, "Frame Time: " + String.format("%.2f", delta * 1000) + "ms", 10, Gdx.graphics.getHeight() - 30);
        fpsFont.draw(batch, "Zoom: " + (int)cameraDistance, 10, Gdx.graphics.getHeight() - 50);
        // Display tile coordinates with 2 decimal places for precision
        fpsFont.draw(batch, "Tile: (" + String.format("%.2f", player.getPosition().x) + ", " + String.format("%.2f", player.getPosition().y) + ")", 10, Gdx.graphics.getHeight() - 70);

        // Calculate render radius (matches renderVisibleChunks3D calculation) (this is for speed improvements)
        // int renderRadius = (int) Math.ceil(cameraDistance / 100f);
        // renderRadius = Math.max(6, Math.min(renderRadius, 10));
        // int totalInRadius = (renderRadius * 2 + 1) * (renderRadius * 2 + 1);
        // fpsFont.draw(batch, "Chunks: " + chunksRenderedLastFrame + "/" + totalInRadius + " (R:" + renderRadius + ")", 10, Gdx.graphics.getHeight() - 90);
        // fpsFont.draw(batch, "Culled: " + chunksCulledLastFrame + " | Loaded: " + world.getLoadedChunkCount(), 10, Gdx.graphics.getHeight() - 110);

        // Render map editor UI
        mapEditor.render(batch);

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
     * Updates total play time for this session.
     */
    private void updatePlayTime() {
        playTimeMillis = System.currentTimeMillis() - sessionStartTime;
    }

    /**
     * Saves the current game state to the specified slot.
     * Also saves map edits if in a StaticWorld.
     */
    public boolean saveGame(int slot) {
        // Save map edits if we're in a StaticWorld
        if (world instanceof io.github.inherit_this.world.StaticWorld) {
            io.github.inherit_this.world.StaticWorld staticWorld = (io.github.inherit_this.world.StaticWorld) world;
            String mapFilePath = staticWorld.getMapFilePath();
            staticWorld.saveMap(mapFilePath);
            Gdx.app.log("GameScreen", "Map saved to " + mapFilePath);
        }

        // Save player state
        return SaveManager.saveGame(player, characterName, slot, playTimeMillis);
    }

    /**
     * Gets the character name for this game session.
     */
    public String getCharacterName() {
        return characterName;
    }

    /**
     * Gets the player instance (used for loading saved games).
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Adds a breakable object to the game world.
     */
    public void addBreakableObject(BreakableObject object) {
        breakableObjects.add(object);
    }

    /**
     * Switches to a different world type.
     * @param worldType "static" for StaticWorld, "procedural" for ProceduralWorld
     * @param mapPath For static worlds, the path to the map file (can be null for default)
     */
    public void switchWorld(String worldType, String mapPath) {
        // Dispose old world
        if (world != null) {
            world.dispose();
        }

        // Clear breakable objects (they're world-specific)
        breakableObjects.clear();

        // Create new world
        if (worldType.equalsIgnoreCase("static")) {
            String path = (mapPath != null && !mapPath.isEmpty()) ? mapPath : "maps/default_map.json";
            world = new StaticWorld(path);
            Gdx.app.log("GameScreen", "Switched to StaticWorld: " + path);
        } else if (worldType.equalsIgnoreCase("procedural")) {
            world = new ProceduralWorld();
            Gdx.app.log("GameScreen", "Switched to ProceduralWorld");
        } else {
            Gdx.app.error("GameScreen", "Unknown world type: " + worldType);
            return;
        }

        // Update player's world reference
        player = new Player(player.getPosition().x, player.getPosition().y, playerTex, world);

        // Update map editor
        mapEditor.setWorld(world);

        // Preload chunks
        world.preloadChunks(20);
    }

    /**
     * Gets the current world.
     */
    public WorldProvider getWorld() {
        return world;
    }

    /**
     * Updates camera position based on player position and rotation angles.
     */
    private void updateCameraPosition() {
        // Player position is in tiles, convert to pixels for 3D rendering
        float playerX = player.getPosition().x * Constants.TILE_SIZE;
        float playerY = player.getPosition().y * Constants.TILE_SIZE;

        // Calculate camera position using spherical coordinates (Y-up system, Minecraft-style)
        // Convert angles to radians
        float angleRad = (float) Math.toRadians(cameraAngle);
        float tiltRad = (float) Math.toRadians(cameraTilt);

        // Calculate camera offset from player
        // In Y-up: XZ is ground plane, Y is vertical
        float horizontalDist = cameraDistance * (float) Math.cos(tiltRad);
        float offsetX = horizontalDist * (float) Math.sin(angleRad);
        float offsetZ = horizontalDist * (float) Math.cos(angleRad);
        float offsetY = cameraDistance * (float) Math.sin(tiltRad);

        // Position camera relative to player
        camera.position.set(
            playerX + offsetX,
            offsetY,
            playerY + offsetZ
        );

        // Look at player position on the ground to keep world centered
        // Y-up system: XZ are ground plane, Y is height
        camera.lookAt(playerX, 0f, playerY);
        camera.up.set(0, 1, 0); // Y-up: up vector is (0, 1, 0)
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

        // Toggle map editor with F10
        if (Gdx.input.isKeyJustPressed(Input.Keys.F10)) {
            mapEditor.toggle();
            if (mapEditor.isActive()) {
                SoundManager.getInstance().play(SoundType.UI_CLICK, 0.8f);
            }
        }

        // Handle map editor input
        if (mapEditor.isActive()) {
            mapEditor.handleInput();

            // Update hovered tile for preview
            Vector3 groundPosition = getGroundPositionFromMouse();
            if (groundPosition != null) {
                int tileX = (int) Math.floor(groundPosition.x / Constants.TILE_SIZE);
                int tileZ = (int) Math.floor(groundPosition.z / Constants.TILE_SIZE);
                mapEditor.setHoveredTile(tileX, tileZ);

                // Handle map editor clicks
                if (Gdx.input.justTouched()) {
                    mapEditor.handleClick(tileX, tileZ);
                }
            } else {
                mapEditor.clearHoveredTile();
            }
            return; // Skip normal input when editor is active
        }

        // Toggle inventory with 'I' key
        if (!debugConsole.isOpen() && Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            inventoryOpen = !inventoryOpen;
            if (inventoryOpen) {
                SoundManager.getInstance().play(SoundType.UI_CLICK, 0.7f);
            }
        }

        // Handle inventory clicks when open
        if (inventoryOpen && Gdx.input.justTouched()) {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
            inventoryUI.handleClick(mouseX, mouseY);
        }

        // Handle right-click for breaking objects
        if (!debugConsole.isOpen() && !inventoryOpen && Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            handleBreakableObjectClick();
        }

        // Handle hold-to-move (like FATE/Diablo)
        if (!debugConsole.isOpen() && !inventoryOpen && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            // Get mouse position and raycast to ground (returns pixel coordinates)
            Vector3 groundPosition = getGroundPositionFromMouse();
            if (groundPosition != null) {
                // Convert pixel coordinates to tile coordinates
                float tileX = groundPosition.x / Constants.TILE_SIZE;
                float tileZ = groundPosition.z / Constants.TILE_SIZE;
                player.setTargetPosition(tileX, tileZ);
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
     * Handles right-click interactions with breakable objects.
     * Damages/breaks objects and gives loot to the player.
     */
    private void handleBreakableObjectClick() {
        Vector3 groundPosition = getGroundPositionFromMouse();
        if (groundPosition == null) {
            return;
        }

        // Convert pixel coordinates to tile coordinates
        float tileX = groundPosition.x / Constants.TILE_SIZE;
        float tileZ = groundPosition.z / Constants.TILE_SIZE;

        // Check if any breakable object was clicked
        for (int i = breakableObjects.size() - 1; i >= 0; i--) {
            BreakableObject obj = breakableObjects.get(i);
            if (obj.contains(tileX, tileZ)) {
                // Play attack sound
                SoundManager.getInstance().playWithVariation(SoundType.ATTACK_SWING, 0.6f);

                // Damage the object
                boolean destroyed = obj.damage(1);

                if (destroyed) {
                    // Play appropriate break sound based on object type
                    // We can enhance BreakableObject later to store its material type
                    SoundManager.getInstance().playWithVariation(SoundType.OBJECT_BREAK_WOOD, 0.8f);

                    // Generate and give loot to player
                    java.util.List<BreakableObject.LootResult> loot = obj.generateLoot();
                    for (BreakableObject.LootResult result : loot) {
                        if (result.isGold()) {
                            player.getInventory().addGold(result.gold);
                            SoundManager.getInstance().playWithVariation(SoundType.LOOT_GOLD, 0.7f);
                            Gdx.app.log("Loot", "Received " + result.gold + " gold");
                        } else if (result.isItem()) {
                            boolean added = player.getInventory().addItem(result.item, result.quantity);
                            if (added) {
                                SoundManager.getInstance().play(SoundType.LOOT_ITEM, 0.7f);
                                Gdx.app.log("Loot", "Received " + result.quantity + "x " + result.item.getName());
                            } else {
                                Gdx.app.log("Loot", "Inventory full! Could not add " + result.item.getName());
                            }
                        }
                    }

                    // Remove destroyed object
                    breakableObjects.remove(i);
                } else {
                    // Play hit sound for damaged but not destroyed
                    SoundManager.getInstance().playWithVariation(SoundType.ATTACK_HIT, 0.5f);
                    Gdx.app.log("Object", "Damaged object - Health: " + obj.getCurrentHealth() + "/" + obj.getMaxHealth());
                }
                break; // Only interact with one object per click
            }
        }
    }

    /**
     * Renders breakable objects as 2D sprites projected from their 3D world positions.
     */
    private void renderBreakableObjects(SpriteBatch batch) {
        for (BreakableObject obj : breakableObjects) {
            // Convert object's tile position to pixel world position
            float worldX = obj.getPosition().x * Constants.TILE_SIZE;
            float worldZ = obj.getPosition().y * Constants.TILE_SIZE;

            // Project 3D world position to 2D screen position
            Vector3 worldPos = new Vector3(worldX, 0, worldZ);
            Vector3 screenPos = camera.project(worldPos);

            // Check if object is on screen
            if (screenPos.x >= 0 && screenPos.x <= Gdx.graphics.getWidth() &&
                screenPos.y >= 0 && screenPos.y <= Gdx.graphics.getHeight() &&
                screenPos.z >= 0 && screenPos.z <= 1) {

                // Calculate distance-based scale (objects far away appear smaller)
                float distToCamera = camera.position.dst(worldPos);
                float scale = Math.min(1.0f, 400f / distToCamera);

                // Draw the sprite centered at the screen position
                Texture tex = obj.getTexture();
                float width = tex.getWidth() * scale;
                float height = tex.getHeight() * scale;

                batch.draw(tex,
                    screenPos.x - width / 2f,
                    screenPos.y - height / 2f,
                    width, height
                );
            }
        }
    }

    /**
     * Renders visible chunks in 3D with frustum culling.
     * Only renders chunks that are actually visible in the camera's view frustum.
     * This provides massive performance improvement over rendering all chunks in radius.
     */
    private void renderVisibleChunks3D() {
        // Player position is in tiles
        float playerX = player.getPosition().x;
        float playerY = player.getPosition().y;

        // Dynamic render radius based on camera distance
        // More zoom out = render more chunks to fill screen
        // Reduced max from 16 to 10 to prevent FPS drops
        int renderRadius = (int) Math.ceil(cameraDistance / 100f);
        renderRadius = Math.max(6, Math.min(renderRadius, 10)); // Clamp between 6-10 chunks

        // Convert tile position to chunk position (each chunk is CHUNK_SIZE tiles)
        int playerChunkX = (int) Math.floor(playerX / Constants.CHUNK_SIZE);
        int playerChunkY = (int) Math.floor(playerY / Constants.CHUNK_SIZE);

        int chunksRendered = 0;
        int chunksCulled = 0;

        for (int cx = playerChunkX - renderRadius; cx <= playerChunkX + renderRadius; cx++) {
            for (int cy = playerChunkY - renderRadius; cy <= playerChunkY + renderRadius; cy++) {
                // Calculate chunk bounds in world space (Y-up system, Minecraft-style)
                float chunkWorldX = cx * Constants.CHUNK_PIXEL_SIZE;
                float chunkWorldY = cy * Constants.CHUNK_PIXEL_SIZE;
                float chunkSize = Constants.CHUNK_PIXEL_SIZE;

                // Create a simple bounding box for the chunk
                // Center point of the chunk (XZ is ground plane, Y is height)
                float centerX = chunkWorldX + chunkSize / 2f;
                float centerY = 0f; // Chunks are flat on ground (Y=0)
                float centerZ = chunkWorldY + chunkSize / 2f;

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

    /**
     * Renders a preview of the selected tile at the hovered position in the map editor.
     */
    private void renderTilePreview() {
        com.badlogic.gdx.graphics.Texture selectedTexture = mapEditor.getSelectedTileTexture();
        if (selectedTexture == null) return;

        int hoveredX = mapEditor.getHoveredTileX();
        int hoveredY = mapEditor.getHoveredTileY();
        TileLayer selectedLayer = mapEditor.getSelectedLayer();
        int selectedDirection = mapEditor.getSelectedDirection();
        boolean selectedFlipped = mapEditor.getSelectedFlipped();
        int selectedLevel = mapEditor.getSelectedLevel();
        int selectedTextureRotation = mapEditor.getSelectedTextureRotation();

        // Calculate world position for the tile
        float tileWorldX = hoveredX * Constants.TILE_SIZE;
        float tileWorldY = hoveredY * Constants.TILE_SIZE;

        // Get layer Y offset and add level offset (each level is one tile height)
        float baseYOffset = selectedLayer != null ? selectedLayer.getYOffset() : 0f;
        float yOffset = baseYOffset + (selectedLevel * Constants.TILE_SIZE);
        // Add small elevation to preview to make it visually distinct (for non-wall tiles)
        if (selectedLayer != TileLayer.WALL) {
            yOffset += 0.5f;
        }

        // Create temporary tile instance for preview
        TileMesh3D tileMesh = TileMesh3D.getInstance();
        ModelInstance previewInstance;

        // Use wall rendering for WALL layer preview
        if (selectedLayer == TileLayer.WALL) {
            float wallHeight = Constants.TILE_SIZE;
            previewInstance = tileMesh.createWallInstance(
                selectedTexture,
                tileWorldX,
                tileWorldY,
                yOffset,
                selectedDirection,
                wallHeight,
                selectedFlipped,
                selectedTextureRotation
            );
        } else {
            // Use angled tile rendering for other layers
            float angle = 0f;
            previewInstance = tileMesh.createAngledTileInstance(
                selectedTexture,
                tileWorldX,
                tileWorldY,
                yOffset,
                angle,
                selectedDirection
            );
        }

        // Enable blending for semi-transparency effect
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Render the preview
        modelBatch.render(previewInstance, environment);

        // Disable blending after preview
        Gdx.gl.glDisable(GL20.GL_BLEND);
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
        SoundManager.getInstance().dispose();
        inventoryUI.dispose();
        equipmentUI.dispose();
        hotbarUI.dispose();
        mapEditor.dispose();
        world.dispose();
    }
}
