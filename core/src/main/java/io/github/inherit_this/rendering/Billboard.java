package io.github.inherit_this.rendering;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

/**
 * A billboard sprite that can be rotated to face a specific direction in 3D space.
 * Useful for rendering 2D sprites (like the player) in a 3D world with directional facing.
 */
public class Billboard {

    private Model model;
    private ModelInstance instance;
    private float width;
    private float height;
    private Vector3 position;
    private float facingAngle = 0f; // Rotation angle in degrees (0 = facing north/+Z)

    public Billboard(Texture texture, float width, float height) {
        this.width = width;
        this.height = height;
        this.position = new Vector3();

        // Create a vertical quad that will be rotated to face the camera
        ModelBuilder modelBuilder = new ModelBuilder();
        Material material = new Material(
            TextureAttribute.createDiffuse(texture),
            new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        );

        // Create a vertical billboard (stands upright on Y axis)
        modelBuilder.begin();
        modelBuilder.part(
            "billboard",
            GL20.GL_TRIANGLES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.TextureCoordinates,
            material
        ).rect(
            -width / 2, 0, 0, // bottom-left  (x,  y,  z)
            -width / 2, height, 0, // top-left
            width / 2, height, 0, // top-right
            width / 2, 0, 0, // bottom-right
            0, 0, 1 // normal points toward positive Z
        );

        model = modelBuilder.end();

        instance = new ModelInstance(model);
    }

    /**
     * Sets the position of the billboard in world space.
     */
    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
    }

    /**
     * Renders the billboard at its current position and facing angle.
     * In Y-up coordinate system: rotates around Y axis (vertical).
     */
    public void render(ModelBatch batch, Camera camera) {
        // Reset transform and apply position + rotation
        instance.transform.idt();
        instance.transform.setToTranslation(position);
        instance.transform.rotate(0, 1, 0, facingAngle); // Rotate around Y axis to face direction

        batch.render(instance);
    }

    /**
     * Sets the facing angle of the billboard.
     * @param angle Rotation angle in degrees (0 = facing north/+Z, 90 = east/+X, etc.)
     */
    public void setFacingAngle(float angle) {
        this.facingAngle = angle;
    }

    /**
     * Gets the current facing angle of the billboard.
     * @return Rotation angle in degrees
     */
    public float getFacingAngle() {
        return facingAngle;
    }


    public void dispose() {
        model.dispose();
    }

    public Vector3 getPosition() {
        return position;
    }
}
