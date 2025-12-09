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
import io.github.inherit_this.world.World;
import io.github.inherit_this.world.WorldProvider;
import io.github.inherit_this.world.ProceduralWorld;
import io.github.inherit_this.world.StaticWorld;
import io.github.inherit_this.world.DungeonWorld;
import io.github.inherit_this.world.DungeonManager;
import io.github.inherit_this.world.Portal;
import io.github.inherit_this.world.PortalRenderer;
import io.github.inherit_this.world.Tile;
import io.github.inherit_this.world.TileTextureManager;
import io.github.inherit_this.world.TileMesh3D;
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

        // Initialize input handler
        inputHandler = new io.github.inherit_this.input.InputHandler(camera, player);

        // Initialize inventory, equipment, and hotbar UI
        inventoryUI = new InventoryUI(player.getInventory());
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

        // Initialize combat manager
        combatManager = new io.github.inherit_this.combat.CombatManager(player);

        // Initialize rendering system
        gameRenderer = new io.github.inherit_this.rendering.GameRenderer(
            camera, modelBatch, environment, player, inputHandler, combatManager
        );
        gameRenderer.setWorld(world);
        gameRenderer.setBreakableObjects(breakableObjects);
        gameRenderer.setInteractableObjects(interactableObjects);
        gameRenderer.setMapEditor(mapEditor);

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
        // Clear screen - any void will be sky blue
        ScreenUtils.clear(0.53f, 0.81f, 0.92f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // Track play time
        updatePlayTime();

        updateFPSCounter(delta);

        handleInput();
        inputHandler.checkRotationKeys();
        inputHandler.updateCameraRotation(delta);

        // Fixed time step for consistent game logic
        accumulator += Math.min(delta, 0.25f); // Cap delta to prevent spiral of death

        while (accumulator >= FIXED_TIME_STEP) {
            // Update game logic at fixed rate
            if (!debugConsole.isOpen() && !inventoryOpen) {
                player.update(FIXED_TIME_STEP);
                combatManager.update(FIXED_TIME_STEP);
            }
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

        // Render NPCs (world to screen projection)
        gameRenderer.renderNPCs(batch);

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
                        float tileX = targetEnemy.getPosition().x / Constants.TILE_SIZE;
                        float tileZ = targetEnemy.getPosition().y / Constants.TILE_SIZE;
                        player.setTargetPosition(tileX, tileZ);
                    }
                } else {
                    // No enemy - just move to clicked position
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
