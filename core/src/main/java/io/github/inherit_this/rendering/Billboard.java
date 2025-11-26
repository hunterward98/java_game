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
 * A billboard sprite that always faces the camera in 3D space.
 * Useful for rendering 2D sprites (like the player) in a 3D world.
 */
public class Billboard {

    private Model model;
    private ModelInstance instance;
    private float width;
    private float height;
    private Vector3 position;

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
     * Renders the billboard, automatically rotating it to face the camera.
     * In Y-up coordinate system: rotates around Y axis (vertical) to always face camera.
     */
    public void render(ModelBatch batch, Camera camera) {
        Vector3 camPos = camera.position;

        // Calculate angle in the XZ plane (horizontal rotation around Y axis)
        float dx = camPos.x - position.x;
        float dz = camPos.z - position.z;
        float angle = (float) Math.toDegrees(Math.atan2(dx, dz));

        // Reset transform and apply position + rotation
        instance.transform.idt();
        instance.transform.setToTranslation(position);
        instance.transform.rotate(0, 1, 0, angle); // Rotate around Y axis (vertical in Y-up)

        batch.render(instance);
    }


    public void dispose() {
        model.dispose();
    }

    public Vector3 getPosition() {
        return position;
    }
}
