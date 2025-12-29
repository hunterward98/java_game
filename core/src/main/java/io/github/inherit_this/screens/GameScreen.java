package io.github.inherit_this.screens;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.inherit_this.Main;
import io.github.inherit_this.items.Item;
import io.github.inherit_this.items.ItemStack;
import io.github.inherit_this.items.EquipmentSlot;
import io.github.inherit_this.items.ItemRegistry;
import io.github.inherit_this.audio.SoundManager;
import io.github.inherit_this.audio.SoundType;
import io.github.inherit_this.ui.EquipmentUI;
import io.github.inherit_this.ui.HotbarUI;
import io.github.inherit_this.ui.InventoryUI;
import io.github.inherit_this.ui.DungeonUI;
import io.github.inherit_this.world.WorldProvider;
import io.github.inherit_this.world.ProceduralWorld;
import io.github.inherit_this.world.StaticWorld;
import io.github.inherit_this.world.PortalRenderer;
import io.github.inherit_this.world.TileTextureManager;
import io.github.inherit_this.world.TileMesh3D;
import io.github.inherit_this.entities.*;
import io.github.inherit_this.util.Constants;
import io.github.inherit_this.util.FontManager;
import io.github.inherit_this.debug.*;
import io.github.inherit_this.save.SaveManager;

import java.util.List;
import java.util.Random;

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

    // Input handling
    private io.github.inherit_this.input.InputHandler inputHandler;

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

    // Dungeon system
    private io.github.inherit_this.dungeon.DungeonController dungeonController;
    private DungeonUI dungeonUI;
    private PortalRenderer portalRenderer;

    // Map editor
    private io.github.inherit_this.world.MapEditor mapEditor;

    // Breakable objects
    private java.util.List<BreakableObject> breakableObjects;

    // Interactable objects (chests, workbenches, anvils, shrines)
    private java.util.List<InteractableObject> interactableObjects;
    private InteractableObject nearbyInteractable;

    // Dropped items (loot on the ground)
    private java.util.List<io.github.inherit_this.entities.DroppedItem> droppedItems;

    // Particle system for visual effects
    private io.github.inherit_this.particles.ParticleSystem particleSystem;

    // Combat manager (handles NPCs and enemies)
    private io.github.inherit_this.combat.CombatManager combatManager;

    // Rendering system
    private io.github.inherit_this.rendering.GameRenderer gameRenderer;

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

        // Initialize dungeon manager with player's seed for consistent dungeon generation
        io.github.inherit_this.world.DungeonManager.getInstance().setPlayerSeed(player.getStats().getDungeonSeed());

        // Initialize input handler
        inputHandler = new io.github.inherit_this.input.InputHandler(camera, player);

        // Initialize inventory, equipment, and hotbar UI
        inventoryUI = new InventoryUI(player.getInventory(), player.getStats());
        equipmentUI = new EquipmentUI(player.getEquipment());
        hotbarUI = new HotbarUI(player.getInventory(), player.getStats());

        // Initialize dungeon system
        dungeonUI = new DungeonUI();
        dungeonController = new io.github.inherit_this.dungeon.DungeonController(player, dungeonUI, combatManager);
        dungeonController.initialize((StaticWorld) world, spawnX, spawnY);

        debugConsole = new DebugConsole();
        debugConsole.registerCommand(new HelpCommand(debugConsole.getCommands()));

        // Player commands
        debugConsole.registerCommand(new NoClipCommand(player));
        debugConsole.registerCommand(new TeleportCommand(player));
        debugConsole.registerCommand(new SetHealthCommand(player));
        // NOTE: DrainCommand requires particleSystem - registered after initialization (see line ~220)
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
        debugConsole.registerCommand(new TownCommand(this, player));
        debugConsole.registerCommand(new TestDungeonCommand(
            player,
            io.github.inherit_this.world.DungeonManager.getInstance(),
            this::switchToDungeon
        ));
        debugConsole.registerCommand(new SeedCommand(
            player,
            io.github.inherit_this.world.DungeonManager.getInstance()
        ));

        // Object spawning command
        debugConsole.registerCommand(new SpawnObjectCommand(player, this));

        // Set up input handling with scroll wheel support
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(inputHandler.getScrollProcessor());
        inputMultiplexer.addProcessor(debugConsole);
        Gdx.input.setInputProcessor(inputMultiplexer);

        // Initialize FPS font (use default libGDX font for now)
        fpsFont = new BitmapFont();

        // Initialize map editor
        mapEditor = new io.github.inherit_this.world.MapEditor(fpsFont);
        mapEditor.setWorld(world);

        // Initialize breakable objects list
        breakableObjects = new java.util.ArrayList<>();

        // Initialize interactable objects list
        interactableObjects = new java.util.ArrayList<>();

        // Initialize dropped items list
        droppedItems = new java.util.ArrayList<>();

        // Initialize particle system
        particleSystem = new io.github.inherit_this.particles.ParticleSystem(camera);

        // Set breakable objects on player for collision detection
        player.setBreakableObjects(breakableObjects);

        // Set particle system on player for combat effects
        player.setParticleSystem(particleSystem);

        // Initialize combat manager with particle system
        combatManager = new io.github.inherit_this.combat.CombatManager(player, particleSystem);

        // Register commands that require late initialization
        debugConsole.registerCommand(new io.github.inherit_this.debug.DrainCommand(player, particleSystem));

        // Load enemy texture for spawn command (reuse player texture for now)
        com.badlogic.gdx.graphics.Texture enemyTexture = new com.badlogic.gdx.graphics.Texture("character.png");
        enemyTexture.setFilter(com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest,
                              com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest);
        debugConsole.registerCommand(new io.github.inherit_this.debug.SpawnCommand(
            player, combatManager, world, enemyTexture));

        // Initialize rendering system
        gameRenderer = new io.github.inherit_this.rendering.GameRenderer(
            camera, modelBatch, environment, player, inputHandler, combatManager
        );
        gameRenderer.setWorld(world);
        gameRenderer.setBreakableObjects(breakableObjects);
        gameRenderer.setInteractableObjects(interactableObjects);
        gameRenderer.setDroppedItems(droppedItems);
        gameRenderer.setMapEditor(mapEditor);

        // Set up map editor object placement callback
        mapEditor.setObjectPlacementCallback((objectType, tileX, tileY) -> {
            int playerLevel = player.getStats().getLevel();
            int dungeonLevel = getCurrentDungeonLevel();
            BreakableObject obj = null;
            switch (objectType) {
                case "Crate":
                    obj = BreakableObjectFactory.createCrate(tileX, tileY, playerLevel, dungeonLevel);
                    break;
                case "Pot":
                    obj = BreakableObjectFactory.createPot(tileX, tileY, playerLevel, dungeonLevel);
                    break;
                case "Barrel":
                    obj = BreakableObjectFactory.createBarrel(tileX, tileY, playerLevel, dungeonLevel);
                    break;
            }
            if (obj != null) {
                addBreakableObject(obj);
            }
        });

        // Spawn persistent town objects (these regenerate on world reload/return from dungeon)
        // TEMPORARILY DISABLED FOR DEBUGGING
        // spawnTownObjects();

        // Add test interactable objects (press E to interact)
        interactableObjects.add(InteractableObjectFactory.createChest(0, 5));
        interactableObjects.add(InteractableObjectFactory.createWorkbench(-3, 0));
        interactableObjects.add(InteractableObjectFactory.createAnvil(3, 0));
        interactableObjects.add(InteractableObjectFactory.createShrine(0, -5));

        // Enable VSync to prevent screen tearing
        Gdx.graphics.setVSync(true);
    }

    @Override
    public void render(float delta) {
        // Clear screen - sky blue for overworld, dark for dungeons
        boolean inDungeon = world instanceof io.github.inherit_this.world.DungeonWorld;

        if (inDungeon) {
            // Dark background for dungeons
            ScreenUtils.clear(0.05f, 0.05f, 0.08f, 1f);
        } else {
            // Sky blue for overworld
            ScreenUtils.clear(0.53f, 0.81f, 0.92f, 1f);
        }
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // Update fog based on location
        updateFog(inDungeon);

        // Track play time
        updatePlayTime();

        updateFPSCounter(delta);

        handleInput();

        // Only allow camera rotation when console is closed
        if (!debugConsole.isOpen()) {
            inputHandler.checkRotationKeys();
            inputHandler.updateCameraRotation(delta);
        }

        // Fixed time step for consistent game logic
        accumulator += Math.min(delta, 0.25f); // Cap delta to prevent spiral of death

        while (accumulator >= FIXED_TIME_STEP) {
            // Update game logic at fixed rate
            if (!debugConsole.isOpen() && !inventoryOpen) {
                player.update(FIXED_TIME_STEP);
                combatManager.update(FIXED_TIME_STEP);
            }
            // Always update particles (even when paused)
            particleSystem.update(FIXED_TIME_STEP);
            accumulator -= FIXED_TIME_STEP;
        }

        inputHandler.updateCameraPosition();

        // Enable depth testing for 3D rendering
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

        // Render 3D world (player will be rendered as 2D overlay later)
        modelBatch.begin(camera);
        gameRenderer.renderVisibleChunks3D();

        // Render 3D breakable objects (chests, barrels, etc)
        gameRenderer.render3DBreakableObjects();

        // Render 3D interactable objects (workbenches, anvils, shrines, chests)
        gameRenderer.render3DInteractableObjects();

        // Render map editor tile preview
        if (mapEditor.isActive() && mapEditor.hasHoveredTile()) {
            gameRenderer.renderTilePreview();
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

        // Render dungeon UI (level display and portal prompts)
        dungeonUI.render();

        // Render breakable objects (world to screen projection)
        gameRenderer.renderBreakableObjects(batch);

        // Update hovered NPC for tooltips
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        gameRenderer.updateHoveredNPC(mouseX, mouseY);

        // Render NPCs (world to screen projection)
        gameRenderer.renderNPCs(batch);

        // Render NPC tooltips (if hovering over an NPC)
        gameRenderer.renderNPCTooltip(batch, mouseX, mouseY);

        // Render dropped items (coins, loot)
        gameRenderer.renderDroppedItems(batch);

        // Render player sprite with perspective scaling
        // Scale inversely with camera distance for proper perspective
        // Base scale multiplied by 1.15 to make player 15% larger relative to tiles
        // At MIN distance (200): scale = 1.73x (closer = bigger)
        // At default (300): scale = 1.15x (normal)
        // At MAX distance (400): scale = 0.86x (farther = smaller)
        float scale = (300f / inputHandler.getCameraDistance()) * 1.15f;

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
        fpsFont.draw(batch, "Zoom: " + (int)inputHandler.getCameraDistance(), 10, Gdx.graphics.getHeight() - 50);
        // Display tile coordinates with 2 decimal places for precision
        fpsFont.draw(batch, "Tile: (" + String.format("%.2f", player.getPosition().x) + ", " + String.format("%.2f", player.getPosition().y) + ")", 10, Gdx.graphics.getHeight() - 70);

        // Calculate render radius (matches renderVisibleChunks3D calculation) (this is for speed improvements)
        // int renderRadius = (int) Math.ceil(inputHandler.getCameraDistance() / 100f);
        // renderRadius = Math.max(6, Math.min(renderRadius, 10));
        // int totalInRadius = (renderRadius * 2 + 1) * (renderRadius * 2 + 1);
        // fpsFont.draw(batch, "Chunks: " + chunksRenderedLastFrame + "/" + totalInRadius + " (R:" + renderRadius + ")", 10, Gdx.graphics.getHeight() - 90);
        // fpsFont.draw(batch, "Culled: " + chunksCulledLastFrame + " | Loaded: " + world.getLoadedChunkCount(), 10, Gdx.graphics.getHeight() - 110);

        // Render map editor UI
        mapEditor.render(batch);

        // Render breakable object tooltip if hovering
        if (!debugConsole.isOpen() && !inventoryOpen) {
            renderBreakableObjectTooltip(batch);
            renderDroppedItemTooltip(batch);
        }

        // Render particle effects (projected from 3D world to 2D screen, like player)
        particleSystem.render(batch);

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
     * Updates fog rendering based on whether the player is in a dungeon.
     * Dungeons get atmospheric fog to soften distant/unloaded areas.
     * Uses LibGDX's shader-based fog (ColorAttribute.Fog).
     */
    private void updateFog(boolean inDungeon) {
        if (inDungeon) {
            // Add dark fog for dungeons if not already present
            if (!environment.has(ColorAttribute.Fog)) {
                // Dark gray fog that matches the dungeon atmosphere
                // The fog color should match the clear color for seamless blending
                // This creates atmospheric depth and hides distant unloaded areas
                environment.set(new ColorAttribute(ColorAttribute.Fog, 0.05f, 0.05f, 0.08f, 1f));
            }
        } else {
            // Remove fog for overworld
            if (environment.has(ColorAttribute.Fog)) {
                environment.remove(ColorAttribute.Fog);
            }
        }
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

        // Clear breakable objects and dropped items (they're world-specific)
        breakableObjects.clear();
        droppedItems.clear();

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
        player.setBreakableObjects(breakableObjects);
        player.setParticleSystem(particleSystem);

        // Update map editor and renderer
        mapEditor.setWorld(world);
        gameRenderer.setWorld(world);

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
     * Switches to a dungeon world (for debug testing).
     * @param dungeon The dungeon world to switch to
     */
    public void switchToDungeon(io.github.inherit_this.world.DungeonWorld dungeon) {
        // Dispose old world if it's not cached in dungeon manager
        if (world != null && !io.github.inherit_this.world.DungeonManager.getInstance().isInDungeon()) {
            world.dispose();
        }

        // Clear breakable objects and dropped items (they're world-specific)
        breakableObjects.clear();
        droppedItems.clear();

        // Set new world
        world = dungeon;

        // Update dungeonController so it doesn't override us every frame
        dungeonController.setCurrentWorld(world);

        // Update player's world reference
        player.setWorld(world);

        // Update map editor and renderer
        mapEditor.setWorld(world);
        gameRenderer.setWorld(world);

        // Preload chunks
        world.preloadChunks(20);

        // Spawn loot in dungeon based on room loot values
        spawnDungeonLoot(dungeon);

        Gdx.app.log("GameScreen", "Switched to dungeon level " + dungeon.getConfig().getDungeonLevel());
    }

    /**
     * Spawns loot (coins and items) in dungeon rooms and hallways based on loot values.
     * Higher level dungeons have more valuable loot (more coins per location) rather than more items.
     */
    private void spawnDungeonLoot(io.github.inherit_this.world.DungeonWorld dungeon) {
        io.github.inherit_this.world.DungeonGenerator generator = dungeon.getGenerator();

        // Check if it's a procedural generator (has rooms)
        if (!(generator instanceof io.github.inherit_this.world.ProceduralDungeonGenerator)) {
            return;
        }

        io.github.inherit_this.world.ProceduralDungeonGenerator procGen =
            (io.github.inherit_this.world.ProceduralDungeonGenerator) generator;
        List<io.github.inherit_this.world.DungeonRoom> rooms = procGen.getRooms();

        // Get coins item from registry
        io.github.inherit_this.items.Item coins = io.github.inherit_this.items.ItemRegistry.getInstance().getItem("coins");
        if (coins == null) {
            Gdx.app.error("GameScreen", "Could not find coins item for loot spawning!");
            return;
        }

        Random random = new Random();
        int dungeonLevel = dungeon.getConfig().getDungeonLevel();
        int playerLevel = player.getStats().getLevel();
        int totalLootSpawned = 0;

        // Spawn loot in rooms based on lootValue
        // Reduced spawn points by 80%, but each stack contains multiple coins
        for (io.github.inherit_this.world.DungeonRoom room : rooms) {
            int lootValue = room.lootValue;

            // Reduced spawn points by 80% (multiply by 0.2)
            int baseSpawnPoints = Math.max(1, lootValue / 2 + random.nextInt(Math.max(1, lootValue / 2)));
            int numSpawnPoints = Math.max(1, (int)(baseSpawnPoints * 0.2));

            for (int i = 0; i < numSpawnPoints; i++) {
                // Find a random floor tile within the room (avoiding edges)
                int margin = 2; // Stay away from walls
                int rx = (int) room.x + margin + random.nextInt(Math.max(1, room.width - margin * 2));
                int ry = (int) room.y + margin + random.nextInt(Math.max(1, room.height - margin * 2));

                // Make sure it's not a wall
                if (!generator.isWall(rx, ry)) {
                    // Calculate coin stack quantity based on formula:
                    // min = 5 * dungeonLevel + playerLevel
                    // max = 6 * dungeonLevel + 3 * playerLevel
                    int minCoins = 5 * dungeonLevel + playerLevel;
                    int maxCoins = 6 * dungeonLevel + 3 * playerLevel;
                    int coinQuantity = minCoins + random.nextInt(Math.max(1, maxCoins - minCoins + 1));

                    // Spawn a single coin stack at this location
                    io.github.inherit_this.entities.DroppedItem droppedItem =
                        new io.github.inherit_this.entities.DroppedItem(coins, rx, ry, coinQuantity);
                    droppedItems.add(droppedItem);
                    totalLootSpawned += coinQuantity;
                }
            }
        }

        // Spawn scattered loot in hallways (less dense than rooms but still visible)
        // Reduced hallway samples by 80% (multiply by 0.2)
        int width = generator.getWidthInTiles();
        int height = generator.getHeightInTiles();
        int baseHallwaySamples = (width * height) / 15; // Check about 6-7% of tiles
        int hallwaySamples = Math.max(1, (int)(baseHallwaySamples * 0.2)); // Reduced by 80%

        for (int i = 0; i < hallwaySamples; i++) {
            int x = 10 + random.nextInt(width - 20);  // Stay away from borders
            int y = 10 + random.nextInt(height - 20);

            // Check if it's a floor tile (not wall) and not in any room
            if (!generator.isWall(x, y) && !isInAnyRoom(rooms, x, y)) {
                // Hallways have about half the coin value of rooms
                int minCoins = (5 * dungeonLevel + playerLevel) / 2;
                int maxCoins = (6 * dungeonLevel + 3 * playerLevel) / 2;
                int coinQuantity = minCoins + random.nextInt(Math.max(1, maxCoins - minCoins + 1));

                // Spawn a single coin stack at this location
                io.github.inherit_this.entities.DroppedItem droppedItem =
                    new io.github.inherit_this.entities.DroppedItem(coins, x, y, coinQuantity);
                droppedItems.add(droppedItem);
                totalLootSpawned += coinQuantity;
            }
        }

        // Spawn breakable objects (pots and crates) in rooms
        int objectsSpawned = spawnBreakableObjectsInRooms(rooms, generator, dungeonLevel, random);

        Gdx.app.log("GameScreen", "Spawned " + totalLootSpawned + " coins and " + objectsSpawned + " breakable objects in dungeon (level " + dungeonLevel + ")");
    }

    /**
     * Check if a tile position is inside any room.
     */
    private boolean isInAnyRoom(List<io.github.inherit_this.world.DungeonRoom> rooms, int x, int y) {
        for (io.github.inherit_this.world.DungeonRoom room : rooms) {
            if (x >= room.x && x < room.x + room.width &&
                y >= room.y && y < room.y + room.height) {
                return true;
            }
        }
        return false;
    }

    /**
     * Spawns breakable objects (pots and crates) in dungeon rooms.
     * Number of objects scales with room loot value, difficulty scales with dungeon level.
     * @return Total number of objects spawned
     */
    private int spawnBreakableObjectsInRooms(List<io.github.inherit_this.world.DungeonRoom> rooms,
                                              io.github.inherit_this.world.DungeonGenerator generator,
                                              int dungeonLevel, Random random) {
        int objectsSpawned = 0;
        int playerLevel = player.getStats().getLevel(); // Get current player level for loot scaling

        for (io.github.inherit_this.world.DungeonRoom room : rooms) {
            int lootValue = room.lootValue;

            // Number of breakable objects based on room loot value
            // Small rooms (lootValue ~3): 1-2 objects
            // Medium rooms (lootValue ~5): 2-3 objects
            // Large rooms (lootValue ~8): 3-5 objects
            int numObjects = Math.max(1, lootValue / 3 + random.nextInt(Math.max(1, lootValue / 3)));

            for (int i = 0; i < numObjects; i++) {
                // Find a random floor tile within the room (avoiding edges)
                int margin = 2; // Stay away from walls
                int rx = (int) room.x + margin + random.nextInt(Math.max(1, room.width - margin * 2));
                int ry = (int) room.y + margin + random.nextInt(Math.max(1, room.height - margin * 2));

                // Make sure it's not a wall
                if (!generator.isWall(rx, ry)) {
                    // Randomly choose between pot (70%) and crate (30%)
                    // Pots are more common but have less loot
                    BreakableObject obj;
                    if (random.nextFloat() < 0.7f) {
                        obj = BreakableObjectFactory.createPot(rx, ry, playerLevel, dungeonLevel);
                    } else {
                        obj = BreakableObjectFactory.createCrate(rx, ry, playerLevel, dungeonLevel);
                    }

                    breakableObjects.add(obj);
                    objectsSpawned++;
                }
            }
        }

        return objectsSpawned;
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
            Vector3 groundPosition = inputHandler.getGroundPositionFromMouse();
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

        // Handle dungeon portal interactions
        dungeonController.handlePortalInteractions();
        world = dungeonController.getCurrentWorld();  // Sync world after portal use
        mapEditor.setWorld(world);
        gameRenderer.setWorld(world);

        // Handle left-click to pickup dropped items
        if (!debugConsole.isOpen() && !inventoryOpen && Gdx.input.justTouched() &&
            Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            handleItemPickup();
        }

        // Update nearby interactable object
        updateNearbyInteractable();

        // Handle E key to interact with nearby objects
        if (!debugConsole.isOpen() && !inventoryOpen && Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            handleInteractableInteraction();
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

        // Handle right-click to equip items from inventory
        if (inventoryOpen && Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
            ItemStack stackToEquip = inventoryUI.handleRightClick(mouseX, mouseY);
            if (stackToEquip != null && stackToEquip.getItem().isEquippable()) {
                Item previousItem = equipmentUI.equipItem(stackToEquip.getItem());
                if (previousItem != null) {
                    inventoryUI.getInventory().addItem(previousItem, 1);
                }
                SoundManager.getInstance().play(SoundType.UI_CLICK, 0.8f);
            }
        }

        // Handle equipment slot clicks to unequip
        if (inventoryOpen && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
            EquipmentSlot clickedSlot = equipmentUI.handleClick(mouseX, mouseY);
            if (clickedSlot != null) {
                Item unequippedItem = equipmentUI.unequipSlot(clickedSlot);
                if (unequippedItem != null) {
                    boolean added = inventoryUI.getInventory().addItem(unequippedItem, 1);
                    if (!added) {
                        equipmentUI.equipItem(unequippedItem);
                    } else {
                        SoundManager.getInstance().play(SoundType.UI_CLICK, 0.8f);
                    }
                }
            }
        }

        // Handle right-click for breaking objects
        if (!debugConsole.isOpen() && !inventoryOpen && Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            handleBreakableObjectClick();
        }

        // Handle hold-to-move and combat (FATE-style)
        if (!debugConsole.isOpen() && !inventoryOpen && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            // Get mouse position and raycast to ground (returns pixel coordinates)
            Vector3 groundPosition = inputHandler.getGroundPositionFromMouse();
            if (groundPosition != null) {
                // Check if clicking on/near an enemy
                NPC targetEnemy = combatManager.findNearestNPC(groundPosition.x, groundPosition.z, 32f);

                if (targetEnemy != null && !targetEnemy.isDead()) {
                    // Enemy found - attack if in range
                    if (player.isInAttackRange(targetEnemy)) {
                        // In range - attack the enemy
                        player.attack(targetEnemy);
                        player.stopMoving();  // Stop moving while attacking
                    } else {
                        // Not in range - move toward enemy to get into range
                        // Enemy position is already in tiles
                        player.setTargetPosition(targetEnemy.getPosition().x, targetEnemy.getPosition().y);
                    }
                } else {
                    // No enemy - just move to clicked position
                    // groundPosition is in pixels, convert to tiles
                    float tileX = groundPosition.x / Constants.TILE_SIZE;
                    float tileZ = groundPosition.z / Constants.TILE_SIZE;
                    player.setTargetPosition(tileX, tileZ);
                }
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
     * Gets the current dungeon level (returns 0 if in overworld).
     */
    public int getCurrentDungeonLevel() {
        if (dungeonController.getDungeonManager().isInDungeon()) {
            io.github.inherit_this.world.DungeonWorld dungeon = dungeonController.getDungeonManager().getCurrentDungeon();
            if (dungeon != null) {
                return dungeon.getConfig().getDungeonLevel();
            }
        }
        return 0; // Overworld
    }

    /**
     * Adds an interactable object to the world.
     */
    public void addInteractableObject(InteractableObject obj) {
        interactableObjects.add(obj);
    }

    /**
     * Spawns persistent breakable objects in town that regenerate when player
     * reloads the game or returns from a dungeon. These objects scale with player level only.
     */
    private void spawnTownObjects() {
        // Only spawn in overworld/town (not in dungeons)
        if (getCurrentDungeonLevel() > 0) {
            return;
        }

        // Clear existing breakable objects to prevent duplicates
        breakableObjects.clear();

        int playerLevel = player.getStats().getLevel();
        int townDungeonLevel = 0; // Town objects use player level only

        // Spawn persistent objects around town spawn point
        // These positions should be adjusted to match your town layout

        // Area 1: Near spawn (training area)
        addBreakableObject(BreakableObjectFactory.createCrate(2, 2, playerLevel, townDungeonLevel));
        addBreakableObject(BreakableObjectFactory.createPot(4, 2, playerLevel, townDungeonLevel));
        addBreakableObject(BreakableObjectFactory.createBarrel(-2, 3, playerLevel, townDungeonLevel));

        // Area 2: Resource gathering area
        addBreakableObject(BreakableObjectFactory.createCrate(8, -5, playerLevel, townDungeonLevel));
        addBreakableObject(BreakableObjectFactory.createCrate(10, -5, playerLevel, townDungeonLevel));
        addBreakableObject(BreakableObjectFactory.createBarrel(9, -7, playerLevel, townDungeonLevel));

        // Area 3: Storage area
        addBreakableObject(BreakableObjectFactory.createPot(-8, 5, playerLevel, townDungeonLevel));
        addBreakableObject(BreakableObjectFactory.createPot(-6, 5, playerLevel, townDungeonLevel));
        addBreakableObject(BreakableObjectFactory.createCrate(-7, 7, playerLevel, townDungeonLevel));
    }

    /**
     * Regenerates town objects (called when returning from dungeon or reloading).
     */
    public void regenerateTownObjects() {
        spawnTownObjects();
    }

    /**
     * Renders a tooltip for the breakable object under the mouse cursor.
     */
    private void renderBreakableObjectTooltip(SpriteBatch batch) {
        // Use object-level mouse detection for better alignment with raised 3D models
        Vector3 objectPosition = inputHandler.getObjectPositionFromMouse();
        if (objectPosition == null) {
            return;
        }

        // Convert pixel coordinates to tile coordinates
        float tileX = objectPosition.x / Constants.TILE_SIZE;
        float tileZ = objectPosition.z / Constants.TILE_SIZE;

        // Find hovered breakable object
        BreakableObject hoveredObject = null;
        for (BreakableObject obj : breakableObjects) {
            if (!obj.isDestroyed() && obj.contains(tileX, tileZ)) {
                hoveredObject = obj;
                break;
            }
        }

        if (hoveredObject != null) {
            // Use the game's tooltip font for consistency
            BitmapFont tooltipFont = FontManager.getInstance().getTooltipFont();

            // Draw tooltip text with simple background
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

            String text = hoveredObject.getName() + " [" + hoveredObject.getCurrentHealth() + "/" + hoveredObject.getMaxHealth() + " HP]";

            // Position tooltip near cursor
            float bgX = mouseX + 15;
            float bgY = mouseY + 5;

            // Draw text with black outline for visibility
            tooltipFont.setColor(0, 0, 0, 1);
            tooltipFont.draw(batch, text, bgX - 1, bgY - 1);
            tooltipFont.draw(batch, text, bgX + 1, bgY - 1);
            tooltipFont.draw(batch, text, bgX - 1, bgY + 1);
            tooltipFont.draw(batch, text, bgX + 1, bgY + 1);

            // Draw main text in white
            tooltipFont.setColor(1, 1, 1, 1);
            tooltipFont.draw(batch, text, bgX, bgY);
        }
    }

    /**
     * Renders a tooltip for dropped items (coins) when player is near.
     */
    private void renderDroppedItemTooltip(SpriteBatch batch) {
        float playerX = player.getPosition().x;
        float playerY = player.getPosition().y;
        float pickupRange = 2.0f; // Same range as pickup

        // Find nearest dropped item
        io.github.inherit_this.entities.DroppedItem nearestItem = null;
        float nearestDist = pickupRange;

        for (io.github.inherit_this.entities.DroppedItem item : droppedItems) {
            if (item.isPickedUp()) continue;

            float dist = item.getPosition().dst(playerX, playerY);
            if (dist < nearestDist) {
                nearestDist = dist;
                nearestItem = item;
            }
        }

        // Show tooltip for nearest item if it's currency (coins)
        if (nearestItem != null && nearestItem.getItem().getType() == io.github.inherit_this.items.ItemType.CURRENCY) {
            BitmapFont tooltipFont = FontManager.getInstance().getTooltipFont();
            String text = "Coins " + nearestItem.getQuantity();

            // Position tooltip at center of screen (above player)
            float textX = Gdx.graphics.getWidth() / 2f;
            float textY = Gdx.graphics.getHeight() / 2f + 100;

            // Center the text
            com.badlogic.gdx.graphics.g2d.GlyphLayout layout = new com.badlogic.gdx.graphics.g2d.GlyphLayout(tooltipFont, text);
            float bgX = textX - layout.width / 2;
            float bgY = textY - layout.height / 2;

            // Draw text shadow (black outline)
            tooltipFont.setColor(0, 0, 0, 1);
            tooltipFont.draw(batch, text, bgX - 1, bgY - 1);
            tooltipFont.draw(batch, text, bgX + 1, bgY - 1);
            tooltipFont.draw(batch, text, bgX - 1, bgY + 1);
            tooltipFont.draw(batch, text, bgX + 1, bgY + 1);

            // Draw main text in gold/yellow color for coins
            tooltipFont.setColor(0.9f, 0.8f, 0.2f, 1);
            tooltipFont.draw(batch, text, bgX, bgY);
        }
    }

    /**
     * Handles right-click interactions with breakable objects.
     * Damages/breaks objects and gives loot to the player.
     */
    private void handleBreakableObjectClick() {
        Vector3 groundPosition = inputHandler.getGroundPositionFromMouse();
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

                    // Spawn break particles
                    float pixelX = (obj.getPosition().x + 0.5f) * Constants.TILE_SIZE;
                    float pixelZ = (obj.getPosition().y + 0.5f) * Constants.TILE_SIZE; // Tile Y maps to world Z
                    float pixelY = Constants.TILE_SIZE / 2f; // Start particles at object height
                    particleSystem.createBreakEffect(
                        obj,     // Pass the object to determine material type
                        pixelX,
                        pixelY,
                        pixelZ,
                        12,      // particle count
                        100f,    // min speed (pixels/sec)
                        250f     // max speed (pixels/sec)
                    );

                    // Generate and give loot to player
                    java.util.List<BreakableObject.LootResult> loot = obj.generateLoot();
                    for (BreakableObject.LootResult result : loot) {
                        if (result.isCoins()) {
                            player.getStats().addCoins(result.coins);
                            SoundManager.getInstance().playWithVariation(SoundType.LOOT_GOLD, 0.7f);
                            Gdx.app.log("Loot", "Received " + result.coins + " coins");
                        } else if (result.isXP()) {
                            int levelsGained = player.getStats().addXP(result.xp);
                            if (levelsGained > 0) {
                                SoundManager.getInstance().play(SoundType.UI_CLICK, 1.0f);
                                Gdx.app.log("Level", "LEVEL UP! Now level " + player.getStats().getLevel());
                            }
                            Gdx.app.log("Loot", "Received " + result.xp + " XP");
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
     * Updates the nearby interactable object reference.
     * Checks if player is within interaction range of any interactable object.
     */
    private void updateNearbyInteractable() {
        nearbyInteractable = null;
        float playerX = player.getPosition().x;
        float playerY = player.getPosition().y;
        float interactDistance = 1.5f; // tiles

        for (InteractableObject obj : interactableObjects) {
            if (obj.isPlayerNear(playerX, playerY, interactDistance)) {
                nearbyInteractable = obj;
                break; // Only track the first nearby object
            }
        }
    }

    /**
     * Handles interaction with the nearby interactable object when E is pressed.
     * Opens appropriate UI based on the object type.
     */
    private void handleInteractableInteraction() {
        if (nearbyInteractable == null) {
            return;
        }

        // Trigger interaction
        boolean success = nearbyInteractable.interact();

        if (success) {
            // Play interaction sound
            SoundManager.getInstance().play(SoundType.UI_CLICK, 0.8f);

            // Open appropriate UI based on type
            switch (nearbyInteractable.getType()) {
                case CHEST:
                    // TODO: Open chest storage UI
                    Gdx.app.log("Interaction", "Opened chest!");
                    break;
                case WORKBENCH:
                    // TODO: Open crafting UI
                    Gdx.app.log("Interaction", "Opened workbench!");
                    break;
                case ANVIL:
                    // TODO: Open repair/upgrade UI
                    Gdx.app.log("Interaction", "Opened anvil!");
                    break;
                case SHRINE:
                    // TODO: Open blessing/buff UI
                    Gdx.app.log("Interaction", "Activated shrine!");
                    break;
            }
        }
    }

    /**
     * Handles picking up dropped items when the player left-clicks near them.
     * Coins are automatically added to gold, other items go to inventory.
     */
    private void handleItemPickup() {
        float playerX = player.getPosition().x;
        float playerY = player.getPosition().y;
        float pickupRange = 2.0f; // Can pick up items within 2 tiles

        // Find the nearest item in range
        DroppedItem nearestItem = null;
        float nearestDistance = Float.MAX_VALUE;

        for (DroppedItem item : droppedItems) {
            if (item.isPickedUp()) continue;

            if (item.isInRange(playerX, playerY, pickupRange)) {
                float dx = playerX - item.getPosition().x;
                float dy = playerY - item.getPosition().y;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);

                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearestItem = item;
                }
            }
        }

        // Pick up the nearest item
        if (nearestItem != null) {
            // Check if it's currency (coins) - add to coin counter instead of inventory
            if (nearestItem.getItem().getType() == io.github.inherit_this.items.ItemType.CURRENCY) {
                int coinAmount = nearestItem.getQuantity();
                nearestItem.pickup(); // Mark as picked up
                player.getStats().addCoins(coinAmount);
                SoundManager.getInstance().play(SoundType.INVENTORY_PICKUP, 0.6f);
                Gdx.app.log("Pickup", "Picked up " + coinAmount + " coins (Total: " + player.getStats().getCoins() + ")");
            } else {
                // Other items go to inventory
                boolean added = inventoryUI.getInventory().addItem(nearestItem.getItem(), 1);

                if (added) {
                    nearestItem.pickup(); // Mark as picked up
                    SoundManager.getInstance().play(SoundType.INVENTORY_PICKUP, 0.7f);
                    Gdx.app.log("Pickup", "Picked up " + nearestItem.getItem().getName());
                } else {
                    // Inventory full
                    Gdx.app.log("Pickup", "Inventory full! Cannot pick up " + nearestItem.getItem().getName());
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
        dungeonUI.updateCamera();
    }

    @Override
    public void dispose() {
        playerTex.dispose();
        player.dispose();
        modelBatch.dispose();
        fpsFont.dispose();
        particleSystem.dispose();
        TileMesh3D.getInstance().dispose();
        TileTextureManager.getInstance().dispose();
        io.github.inherit_this.world.ModelManager.getInstance().dispose();
        ItemRegistry.getInstance().dispose();
        FontManager.getInstance().dispose();
        SoundManager.getInstance().dispose();
        inventoryUI.dispose();
        equipmentUI.dispose();
        hotbarUI.dispose();
        dungeonUI.dispose();
        dungeonController.getDungeonManager().dispose();
        mapEditor.dispose();
        world.dispose();
    }
}
