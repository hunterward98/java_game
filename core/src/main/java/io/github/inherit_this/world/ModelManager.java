package io.github.inherit_this.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import io.github.inherit_this.util.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages loading, caching, and creation of 3D models for objects in the game.
 * Similar to TileTextureManager but for 3D models.
 */
public class ModelManager {
    private static ModelManager instance;

    private ModelBuilder modelBuilder;
    private AssetManager assetManager;
    private Map<String, Model> proceduralModels; // Cache for procedurally generated models
    private Map<String, Model> loadedModels;     // Cache for loaded model files

    private ModelManager() {
        this.modelBuilder = new ModelBuilder();
        this.assetManager = new AssetManager();
        this.proceduralModels = new HashMap<>();
        this.loadedModels = new HashMap<>();
    }

    public static ModelManager getInstance() {
        if (instance == null) {
            instance = new ModelManager();
        }
        return instance;
    }

    /**
     * Gets or creates a simple cube model.
     * @param width Width in world units
     * @param height Height in world units
     * @param depth Depth in world units
     * @param texture Texture to apply to all faces
     * @return Cached or newly created model
     */
    public Model getCubeModel(float width, float height, float depth, Texture texture) {
        String key = "cube_" + width + "_" + height + "_" + depth + "_" + texture.toString();

        if (!proceduralModels.containsKey(key)) {
            proceduralModels.put(key, createCube(width, height, depth, texture));
        }

        return proceduralModels.get(key);
    }

    /**
     * Gets or creates a simple box model with different dimensions.
     * Useful for crates, barrels, chests, etc.
     */
    public Model getBoxModel(String name, float width, float height, float depth, Texture texture) {
        String key = "box_" + name;

        if (!proceduralModels.containsKey(key)) {
            proceduralModels.put(key, createCube(width, height, depth, texture));
        }

        return proceduralModels.get(key);
    }

    /**
     * Creates a textured cube model.
     */
    private Model createCube(float width, float height, float depth, Texture texture) {
        // Set texture filtering for pixel-perfect rendering
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        Material material = new Material(
            TextureAttribute.createDiffuse(texture),
            ColorAttribute.createSpecular(1, 1, 1, 1)
        );

        long attributes = VertexAttributes.Usage.Position |
                         VertexAttributes.Usage.Normal |
                         VertexAttributes.Usage.TextureCoordinates;

        return modelBuilder.createBox(width, height, depth, material, attributes);
    }

    /**
     * Gets or creates a cylinder model (useful for pots, barrels).
     */
    public Model getCylinderModel(String name, float width, float height, float depth, Texture texture, int divisions) {
        String key = "cylinder_" + name;

        if (!proceduralModels.containsKey(key)) {
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

            Material material = new Material(
                TextureAttribute.createDiffuse(texture),
                ColorAttribute.createSpecular(1, 1, 1, 1)
            );

            long attributes = VertexAttributes.Usage.Position |
                             VertexAttributes.Usage.Normal |
                             VertexAttributes.Usage.TextureCoordinates;

            Model model = modelBuilder.createCylinder(width, height, depth, divisions, material, attributes);
            proceduralModels.put(key, model);
        }

        return proceduralModels.get(key);
    }

    /**
     * Loads a 3D model from a file (OBJ, G3D, etc).
     * @param modelPath Path to the model file in assets
     * @return Loaded model, or null if loading fails
     */
    public Model loadModel(String modelPath) {
        if (loadedModels.containsKey(modelPath)) {
            return loadedModels.get(modelPath);
        }

        try {
            // Queue the model for loading
            assetManager.load(modelPath, Model.class);
            assetManager.finishLoading(); // Block until loaded (for simplicity)

            Model model = assetManager.get(modelPath, Model.class);
            loadedModels.put(modelPath, model);

            Gdx.app.log("ModelManager", "Loaded model: " + modelPath);
            return model;
        } catch (Exception e) {
            Gdx.app.error("ModelManager", "Failed to load model: " + modelPath, e);
            return null;
        }
    }

    /**
     * Creates a simple crate model (cube with crate texture).
     */
    public Model createCrateModel() {
        Texture crateTexture = new Texture("breakable/crate.png");
        return getBoxModel("crate",
            Constants.TILE_SIZE * 0.8f,  // Slightly smaller than a tile
            Constants.TILE_SIZE * 0.8f,
            Constants.TILE_SIZE * 0.8f,
            crateTexture
        );
    }

    /**
     * Creates a simple pot model (cylinder).
     */
    public Model createPotModel() {
        Texture potTexture = new Texture("breakable/pot.png");
        return getCylinderModel("pot",
            Constants.TILE_SIZE * 0.5f,  // Width (diameter)
            Constants.TILE_SIZE * 0.6f,  // Height
            Constants.TILE_SIZE * 0.5f,  // Depth (diameter)
            potTexture,
            16  // Number of divisions (smoother = more divisions)
        );
    }

    /**
     * Creates a simple barrel model (cylinder, taller than pot).
     */
    public Model createBarrelModel() {
        Texture barrelTexture = new Texture("breakable/barrel.png");
        return getCylinderModel("barrel",
            Constants.TILE_SIZE * 0.6f,  // Width
            Constants.TILE_SIZE * 0.9f,  // Height (taller than pot)
            Constants.TILE_SIZE * 0.6f,  // Depth
            barrelTexture,
            16
        );
    }

    /**
     * Creates a simple chest model (rectangular box).
     * Used for interactable storage chests.
     */
    public Model createChestModel() {
        Texture chestTexture = new Texture("breakable/chest.png");
        return getBoxModel("chest",
            Constants.TILE_SIZE * 0.9f,  // Width (wider)
            Constants.TILE_SIZE * 0.7f,  // Height (shorter)
            Constants.TILE_SIZE * 0.6f,  // Depth
            chestTexture
        );
    }

    /**
     * Creates a workbench model (flat-topped table).
     */
    public Model createWorkbenchModel() {
        Texture workbenchTexture = new Texture("interactable/workbench.png");
        return getBoxModel("workbench",
            Constants.TILE_SIZE * 1.0f,  // Width (full tile)
            Constants.TILE_SIZE * 0.8f,  // Height (table height)
            Constants.TILE_SIZE * 1.0f,  // Depth (full tile)
            workbenchTexture
        );
    }

    /**
     * Creates an anvil model (heavy, squat metalworking tool).
     */
    public Model createAnvilModel() {
        Texture anvilTexture = new Texture("interactable/anvil.png");
        return getBoxModel("anvil",
            Constants.TILE_SIZE * 0.7f,  // Width
            Constants.TILE_SIZE * 0.6f,  // Height (squat)
            Constants.TILE_SIZE * 0.5f,  // Depth
            anvilTexture
        );
    }

    /**
     * Creates a shrine/altar model (tall, ornate structure).
     */
    public Model createShrineModel() {
        Texture shrineTexture = new Texture("interactable/shrine.png");
        return getBoxModel("shrine",
            Constants.TILE_SIZE * 0.9f,  // Width
            Constants.TILE_SIZE * 1.2f,  // Height (taller than most objects)
            Constants.TILE_SIZE * 0.9f,  // Depth
            shrineTexture
        );
    }

    /**
     * Disposes of all loaded models and clears caches.
     */
    public void dispose() {
        // Dispose procedural models
        for (Model model : proceduralModels.values()) {
            model.dispose();
        }
        proceduralModels.clear();

        // Asset manager handles disposal of loaded models
        assetManager.dispose();
        loadedModels.clear();

        Gdx.app.log("ModelManager", "Disposed all models");
    }
}
