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
    private Map<String, Model> wallModels;  // Cache for walls with different UV rotations (key: texture + rotation)

    private TileMesh3D() {
        modelBuilder = new ModelBuilder();
        tileModels = new HashMap<>();
        wallModels = new HashMap<>();
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
     * Gets or creates a wall model with rotated UVs.
     * @param texture The texture to use
     * @param textureRotation Rotation in 90° increments (0-3: 0°, 90°, 180°, 270°)
     */
    public Model getWallModel(Texture texture, int textureRotation) {
        String key = texture.toString() + "_" + textureRotation;
        if (!wallModels.containsKey(key)) {
            wallModels.put(key, createWallPlane(texture, textureRotation));
        }
        return wallModels.get(key);
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
     * @param flipped Whether to flip the texture horizontally
     * @param textureRotation Texture rotation in 90° increments (0-3: 0°, 90°, 180°, 270°)
     */
    public ModelInstance createWallInstance(Texture texture, float worldX, float worldY,
                                           float yOffset, int direction, float wallHeight, boolean flipped, int textureRotation) {
        Model model = getWallModel(texture, textureRotation);  // Use wall model with rotated UVs
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

        // Rotate around Y axis based on direction to face the correct way (must happen BEFORE X rotation)
        // Each direction needs specific rotation to orient the wall texture properly
        // North (0): 0° - wall faces south from north edge
        // East (1): 90° - wall faces west from east edge
        // South (2): 180° - wall faces north from south edge
        // West (3): 270° - wall faces east from west edge
        float yRotation = direction * 90f;
        if (yRotation > 0) {
            instance.transform.rotate(0, 1, 0, yRotation);
        }

        // Rotate 90 degrees around X axis to make it vertical (must happen AFTER Y rotation)
        instance.transform.rotate(1, 0, 0, 90f);

        // Scale vertically to match wall height
        instance.transform.scale(1f, wallHeight / tileSize, 1f);

        // Flip texture horizontally if requested (useful for seeing interior walls)
        if (flipped) {
            instance.transform.scale(-1f, 1f, 1f);
        }

        return instance;
    }

    /**
     * Creates a flat horizontal plane mesh with the given texture.
     * The plane lies flat on the XZ plane (horizontal ground).
     */
    private Model createTilePlane(Texture texture) {
        Material material = new Material(
            TextureAttribute.createDiffuse(texture),
            new com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute(
                com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA,
                com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA
            )
        );

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

    /**
     * Creates a flat horizontal plane mesh with rotated UVs for walls.
     * This makes the texture appear correctly oriented when the mesh is rotated vertical.
     * @param texture The texture to apply
     * @param textureRotation Additional UV rotation in 90° increments (0-3: 0°, 90°, 180°, 270°)
     */
    private Model createWallPlane(Texture texture, int textureRotation) {
        Material material = new Material(
            TextureAttribute.createDiffuse(texture),
            new com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute(
                com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA,
                com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA
            )
        );

        float size = Constants.TILE_SIZE;
        float halfSize = size / 2f;

        // Create a horizontal plane with rotated UV coordinates
        // When this plane is rotated 90° around X to make it vertical,
        // the texture will appear correctly oriented
        modelBuilder.begin();

        // Build mesh manually to control UV coordinates
        com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo v1 = new com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo();
        com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo v2 = new com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo();
        com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo v3 = new com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo();
        com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo v4 = new com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo();

        // Base UV coordinates (already rotated 90° to fix wall orientation)
        float u1, v1_uv, u2, v2_uv, u3, v3_uv, u4, v4_uv;

        // Apply additional texture rotation by rotating UVs
        switch (textureRotation) {
            case 0: // No additional rotation
                u1 = 0; v1_uv = 1;  // bottom-left
                u2 = 1; v2_uv = 1;  // top-left
                u3 = 1; v3_uv = 0;  // top-right
                u4 = 0; v4_uv = 0;  // bottom-right
                break;
            case 1: // 90° clockwise
                u1 = 0; v1_uv = 0;  // bottom-left
                u2 = 0; v2_uv = 1;  // top-left
                u3 = 1; v3_uv = 1;  // top-right
                u4 = 1; v4_uv = 0;  // bottom-right
                break;
            case 2: // 180°
                u1 = 1; v1_uv = 0;  // bottom-left
                u2 = 0; v2_uv = 0;  // top-left
                u3 = 0; v3_uv = 1;  // top-right
                u4 = 1; v4_uv = 1;  // bottom-right
                break;
            case 3: // 270° clockwise
                u1 = 1; v1_uv = 1;  // bottom-left
                u2 = 1; v2_uv = 0;  // top-left
                u3 = 0; v3_uv = 0;  // top-right
                u4 = 0; v4_uv = 1;  // bottom-right
                break;
            default:
                u1 = 0; v1_uv = 1;
                u2 = 1; v2_uv = 1;
                u3 = 1; v3_uv = 0;
                u4 = 0; v4_uv = 0;
                break;
        }

        // Set positions with calculated UV coordinates
        v1.setPos(-halfSize, 0, -halfSize).setNor(0, 1, 0).setUV(u1, v1_uv);  // bottom-left
        v2.setPos(-halfSize, 0, halfSize).setNor(0, 1, 0).setUV(u2, v2_uv);   // top-left
        v3.setPos(halfSize, 0, halfSize).setNor(0, 1, 0).setUV(u3, v3_uv);    // top-right
        v4.setPos(halfSize, 0, -halfSize).setNor(0, 1, 0).setUV(u4, v4_uv);   // bottom-right

        com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder builder = modelBuilder.part(
            "wall",
            GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates,
            material
        );

        // Create two triangles for the quad (counter-clockwise winding)
        builder.rect(v1, v2, v3, v4);

        return modelBuilder.end();
    }

    public void dispose() {
        for (Model model : tileModels.values()) {
            model.dispose();
        }
        tileModels.clear();
        for (Model model : wallModels.values()) {
            model.dispose();
        }
        wallModels.clear();
    }
}
