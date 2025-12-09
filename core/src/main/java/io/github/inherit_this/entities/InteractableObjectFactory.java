package io.github.inherit_this.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import io.github.inherit_this.world.TileTextureManager;
import io.github.inherit_this.world.ModelManager;

/**
 * Factory for creating different types of interactable objects.
 * These objects use E key interaction instead of being breakable.
 */
public class InteractableObjectFactory {

    private static TileTextureManager textureManager = TileTextureManager.getInstance();
    private static ModelManager modelManager = ModelManager.getInstance();

    /**
     * Creates a storage chest that the player can open.
     */
    public static InteractableObject createChest(float x, float y) {
        Model chestModel = modelManager.createChestModel();
        Texture fallbackTexture = textureManager.getTexture("tiles/grass_2.png");
        return new InteractableObject(chestModel, fallbackTexture, x, y, InteractableType.CHEST);
    }

    /**
     * Creates a workbench for crafting items.
     */
    public static InteractableObject createWorkbench(float x, float y) {
        Model workbenchModel = modelManager.createWorkbenchModel();
        Texture fallbackTexture = textureManager.getTexture("tiles/wood_wall.png");
        return new InteractableObject(workbenchModel, fallbackTexture, x, y, InteractableType.WORKBENCH);
    }

    /**
     * Creates an anvil for repairing and upgrading equipment.
     */
    public static InteractableObject createAnvil(float x, float y) {
        Model anvilModel = modelManager.createAnvilModel();
        Texture fallbackTexture = textureManager.getTexture("tiles/stone_1.png");
        return new InteractableObject(anvilModel, fallbackTexture, x, y, InteractableType.ANVIL);
    }

    /**
     * Creates a shrine/altar for blessings and buffs.
     */
    public static InteractableObject createShrine(float x, float y) {
        Model shrineModel = modelManager.createShrineModel();
        Texture fallbackTexture = textureManager.getTexture("tiles/mossy_stone_1.png");
        return new InteractableObject(shrineModel, fallbackTexture, x, y, InteractableType.SHRINE);
    }
}
