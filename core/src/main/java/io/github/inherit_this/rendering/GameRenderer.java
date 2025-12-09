package io.github.inherit_this.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import io.github.inherit_this.combat.CombatManager;
import io.github.inherit_this.entities.BreakableObject;
import io.github.inherit_this.entities.InteractableObject;
import io.github.inherit_this.entities.NPC;
import io.github.inherit_this.entities.Player;
import io.github.inherit_this.input.InputHandler;
import io.github.inherit_this.util.Constants;
import io.github.inherit_this.world.*;

import java.util.List;

/**
 * Handles all 3D rendering for the game including chunks, sprites, and previews.
 * Extracted from GameScreen to reduce complexity.
 */
public class GameRenderer {
    private final PerspectiveCamera camera;
    private final ModelBatch modelBatch;
    private final Environment environment;
    private final Player player;
    private final InputHandler inputHandler;
    private final CombatManager combatManager;

    // References that may change
    private WorldProvider world;
    private MapEditor mapEditor;
    private List<BreakableObject> breakableObjects;
    private List<InteractableObject> interactableObjects;

    // Performance tracking
    private int chunksRenderedLastFrame = 0;
    private int chunksCulledLastFrame = 0;

    public GameRenderer(PerspectiveCamera camera, ModelBatch modelBatch, Environment environment,
                        Player player, InputHandler inputHandler, CombatManager combatManager) {
        this.camera = camera;
        this.modelBatch = modelBatch;
        this.environment = environment;
        this.player = player;
        this.inputHandler = inputHandler;
        this.combatManager = combatManager;
    }

    /**
     * Set the current world provider.
     */
    public void setWorld(WorldProvider world) {
        this.world = world;
    }

    /**
     * Set the map editor reference.
     */
    public void setMapEditor(MapEditor mapEditor) {
        this.mapEditor = mapEditor;
    }

    /**
     * Set the breakable objects list.
     */
    public void setBreakableObjects(List<BreakableObject> breakableObjects) {
        this.breakableObjects = breakableObjects;
    }

    /**
     * Set the interactable objects list.
     */
    public void setInteractableObjects(List<InteractableObject> interactableObjects) {
        this.interactableObjects = interactableObjects;
    }

    /**
     * Renders visible chunks in 3D with frustum culling.
     * Only renders chunks that are actually visible in the camera's view frustum.
     */
    public void renderVisibleChunks3D() {
        // Player position is in tiles
        float playerX = player.getPosition().x;
        float playerY = player.getPosition().y;

        // Dynamic render radius based on camera distance
        int renderRadius = (int) Math.ceil(inputHandler.getCameraDistance() / 100f);
        renderRadius = Math.max(6, Math.min(renderRadius, 10)); // Clamp between 6-10 chunks

        // Convert tile position to chunk position
        int playerChunkX = (int) Math.floor(playerX / Constants.CHUNK_SIZE);
        int playerChunkY = (int) Math.floor(playerY / Constants.CHUNK_SIZE);

        int chunksRendered = 0;
        int chunksCulled = 0;

        for (int cx = playerChunkX - renderRadius; cx <= playerChunkX + renderRadius; cx++) {
            for (int cy = playerChunkY - renderRadius; cy <= playerChunkY + renderRadius; cy++) {
                // Calculate chunk bounds in world space
                float chunkWorldX = cx * Constants.CHUNK_PIXEL_SIZE;
                float chunkWorldY = cy * Constants.CHUNK_PIXEL_SIZE;
                float chunkSize = Constants.CHUNK_PIXEL_SIZE;

                // Center point of the chunk
                float centerX = chunkWorldX + chunkSize / 2f;
                float centerY = 0f;
                float centerZ = chunkWorldY + chunkSize / 2f;

                // Bounding sphere radius
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

    /**
     * Renders a single chunk in 3D using cached ModelInstances.
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
    public void renderTilePreview() {
        if (mapEditor == null || !mapEditor.hasHoveredTile()) {
            return;
        }

        Texture selectedTexture = mapEditor.getSelectedTileTexture();
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

        // Get layer Y offset and add level offset
        float baseYOffset = selectedLayer != null ? selectedLayer.getYOffset() : 0f;
        float yOffset = baseYOffset + (selectedLevel * Constants.TILE_SIZE);
        // Add small elevation to preview to make it visually distinct
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

    /**
     * Renders 3D breakable objects using ModelBatch.
     * Should be called during the ModelBatch rendering phase (between begin/end).
     */
    public void render3DBreakableObjects() {
        if (breakableObjects == null) return;

        for (BreakableObject obj : breakableObjects) {
            // Skip 2D objects
            if (!obj.is3D()) continue;

            // Convert object's tile position to pixel world position
            float worldX = obj.getPosition().x * Constants.TILE_SIZE;
            float worldZ = obj.getPosition().y * Constants.TILE_SIZE;

            // Center the object on its tile and raise it slightly off the ground
            float centerX = worldX + Constants.TILE_SIZE / 2f;
            float centerZ = worldZ + Constants.TILE_SIZE / 2f;
            float yOffset = Constants.TILE_SIZE / 2f; // Center vertically (half the object's height)

            // Create a ModelInstance positioned at the object's location
            ModelInstance instance = new ModelInstance(obj.getModel());
            instance.transform.setToTranslation(centerX, yOffset, centerZ);

            // Render the 3D model
            modelBatch.render(instance, environment);
        }
    }

    /**
     * Renders 3D interactable objects (chests, workbenches, anvils, shrines) using ModelBatch.
     * Should be called during the ModelBatch rendering phase (between begin/end).
     */
    public void render3DInteractableObjects() {
        if (interactableObjects == null) return;

        for (InteractableObject obj : interactableObjects) {
            // Convert object's tile position to pixel world position
            float worldX = obj.getPosition().x * Constants.TILE_SIZE;
            float worldZ = obj.getPosition().y * Constants.TILE_SIZE;

            // Center the object on its tile and raise it slightly off the ground
            float centerX = worldX + Constants.TILE_SIZE / 2f;
            float centerZ = worldZ + Constants.TILE_SIZE / 2f;
            float yOffset = Constants.TILE_SIZE / 2f; // Center vertically

            // Create a ModelInstance positioned at the object's location
            ModelInstance instance = new ModelInstance(obj.getModel());
            instance.transform.setToTranslation(centerX, yOffset, centerZ);

            // Render the 3D model
            modelBatch.render(instance, environment);
        }
    }

    /**
     * Renders 2D breakable objects as sprites projected from their 3D world positions.
     * Should be called during the SpriteBatch rendering phase.
     */
    public void renderBreakableObjects(SpriteBatch batch) {
        if (breakableObjects == null) return;

        for (BreakableObject obj : breakableObjects) {
            // Skip 3D objects (rendered separately)
            if (obj.is3D()) continue;

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

                // Calculate distance-based scale
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
     * Renders NPCs as 2D sprites projected from their 3D world positions.
     */
    public void renderNPCs(SpriteBatch batch) {
        for (NPC npc : combatManager.getAllNPCs()) {
            if (npc.isDead()) {
                continue;
            }

            // Convert NPC's tile position to pixel world position
            float worldX = npc.getPosition().x;
            float worldZ = npc.getPosition().y;

            // Project 3D world position to 2D screen position
            Vector3 worldPos = new Vector3(worldX, 0, worldZ);
            Vector3 screenPos = camera.project(worldPos);

            // Check if NPC is on screen
            if (screenPos.x >= 0 && screenPos.x <= Gdx.graphics.getWidth() &&
                screenPos.y >= 0 && screenPos.y <= Gdx.graphics.getHeight() &&
                screenPos.z >= 0 && screenPos.z <= 1) {

                // Calculate distance-based scale
                float distToCamera = camera.position.dst(worldPos);
                float scale = Math.min(1.0f, 400f / distToCamera);

                // Draw the sprite centered at the screen position
                Texture tex = npc.getTexture();
                float width = tex.getWidth() * scale;
                float height = tex.getHeight() * scale;
                float yOffset = 10f * scale; // Slight offset so sprite feet align with ground

                batch.draw(tex,
                    screenPos.x - width / 2f,
                    screenPos.y - height / 2f + yOffset,
                    width, height
                );

                // TODO: Render health bar above NPC
            }
        }
    }

    // Getters for debug info
    public int getChunksRenderedLastFrame() {
        return chunksRenderedLastFrame;
    }

    public int getChunksCulledLastFrame() {
        return chunksCulledLastFrame;
    }
}
