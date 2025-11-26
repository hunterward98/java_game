package io.github.inherit_this.world;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import io.github.inherit_this.util.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Creates and manages 3D plane meshes for tiles.
 * Each tile type gets a reusable model with its texture applied.
 */
public class TileMesh3D {

    private static TileMesh3D instance;
    private ModelBuilder modelBuilder;
    private Map<Texture, Model> tileModels;

    private TileMesh3D() {
        modelBuilder = new ModelBuilder();
        tileModels = new HashMap<>();
    }

    public static TileMesh3D getInstance() {
        if (instance == null) {
            instance = new TileMesh3D();
        }
        return instance;
    }

    /**
     * Gets or creates a 3D plane model for a tile texture.
     * Models are cached and reused for performance.
     */
    public Model getTileModel(Texture texture) {
        if (!tileModels.containsKey(texture)) {
            tileModels.put(texture, createTilePlane(texture));
        }
        return tileModels.get(texture);
    }

    /**
     * Creates a ModelInstance positioned at specific world coordinates.
     */
    public ModelInstance createTileInstance(Texture texture, float worldX, float worldY, float worldZ) {
        Model model = getTileModel(texture);
        ModelInstance instance = new ModelInstance(model);

        // Position the tile in world space
        // Center the tile at the given coordinates
        instance.transform.setToTranslation(
            worldX + Constants.TILE_SIZE / 2f,
            worldZ,  // Height (z in 3D becomes y in our world coords)
            worldY + Constants.TILE_SIZE / 2f
        );

        return instance;
    }

    /**
     * Creates a flat horizontal plane mesh with the given texture.
     * The plane lies flat on the XZ plane (horizontal ground).
     */
    private Model createTilePlane(Texture texture) {
        Material material = new Material(TextureAttribute.createDiffuse(texture));

        float size = Constants.TILE_SIZE;
        float halfSize = size / 2f;

        // Create a horizontal plane (flat on XZ, facing up in Y)
        // Using createRect with appropriate vertex positions
        modelBuilder.begin();

        modelBuilder.part(
            "tile",
            GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates,
            material
        ).rect(
            // Four corners of the tile plane (counter-clockwise from bottom-left)
            -halfSize, 0, -halfSize,  // bottom-left
            -halfSize, 0, halfSize,   // top-left
            halfSize, 0, halfSize,    // top-right
            halfSize, 0, -halfSize,   // bottom-right
            0, 1, 0                    // normal pointing up
        );

        return modelBuilder.end();
    }

    public void dispose() {
        for (Model model : tileModels.values()) {
            model.dispose();
        }
        tileModels.clear();
    }
}
