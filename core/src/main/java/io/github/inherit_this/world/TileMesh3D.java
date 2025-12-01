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
     * Creates an angled ModelInstance for walls and roofs.
     * @param texture The texture to use
     * @param worldX World X position in pixels
     * @param worldY World Y position in pixels (Z in 3D space)
     * @param yOffset Height offset (Y in 3D space)
     * @param angle Rotation angle in degrees (0=flat, 45=roof, 90=wall)
     * @param direction Direction facing (0=North, 1=East, 2=South, 3=West)
     */
    public ModelInstance createAngledTileInstance(Texture texture, float worldX, float worldY,
                                                   float yOffset, float angle, int direction) {
        Model model = getTileModel(texture);
        ModelInstance instance = new ModelInstance(model);

        // Center position
        float centerX = worldX + Constants.TILE_SIZE / 2f;
        float centerZ = worldY + Constants.TILE_SIZE / 2f;

        // Set initial position
        instance.transform.setToTranslation(centerX, yOffset, centerZ);

        // Apply rotation based on angle (tilt for walls/roofs)
        if (angle > 0f) {
            // Rotate around X axis for tilt (0=flat, 45=angled roof, 90=vertical wall)
            instance.transform.rotate(1, 0, 0, angle);
        }

        // Apply rotation based on direction (which way the tile faces)
        if (direction > 0) {
            // Rotate around Y axis: 0=North, 1=East, 2=South, 3=West
            instance.transform.rotate(0, 1, 0, direction * 90f);
        }

        return instance;
    }

    /**
     * Creates a wall instance positioned at the edge of a tile.
     * @param texture The texture to use
     * @param worldX World X position in pixels (tile position)
     * @param worldY World Y position in pixels (tile position, Z in 3D space)
     * @param yOffset Height offset (Y in 3D space)
     * @param direction Direction of wall (0=North edge, 1=East edge, 2=South edge, 3=West edge)
     * @param wallHeight Height of the wall in world units
     */
    public ModelInstance createWallInstance(Texture texture, float worldX, float worldY,
                                           float yOffset, int direction, float wallHeight) {
        Model model = getTileModel(texture);
        ModelInstance instance = new ModelInstance(model);

        float tileSize = Constants.TILE_SIZE;
        float halfSize = tileSize / 2f;

        // Calculate position based on direction (which edge of the tile)
        float posX = worldX + halfSize;
        float posZ = worldY + halfSize;
        float posY = yOffset + wallHeight / 2f;  // Center vertically

        // Offset to the edge based on direction
        switch (direction) {
            case 0: // North edge (positive Z direction)
                posZ += halfSize;
                break;
            case 1: // East edge (positive X direction)
                posX += halfSize;
                break;
            case 2: // South edge (negative Z direction)
                posZ -= halfSize;
                break;
            case 3: // West edge (negative X direction)
                posX -= halfSize;
                break;
        }

        // Set position
        instance.transform.setToTranslation(posX, posY, posZ);

        // Rotate 90 degrees around X axis to make it vertical
        instance.transform.rotate(1, 0, 0, 90f);

        // Rotate around Y axis based on direction to face the correct way
        // Each direction needs specific rotation to orient the wall texture properly
        // North (0): 0° - wall faces south from north edge
        // East (1): 90° - wall faces west from east edge
        // South (2): 180° - wall faces north from south edge
        // West (3): 270° - wall faces east from west edge
        float yRotation = direction * 90f;
        if (yRotation > 0) {
            instance.transform.rotate(0, 1, 0, yRotation);
        }

        // Scale vertically to match wall height
        instance.transform.scale(1f, wallHeight / tileSize, 1f);

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
