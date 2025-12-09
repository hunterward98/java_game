package io.github.inherit_this.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import io.github.inherit_this.items.Item;
import io.github.inherit_this.items.ItemRegistry;
import io.github.inherit_this.world.TileTextureManager;
import io.github.inherit_this.world.ModelManager;

/**
 * Factory for creating different types of breakable objects.
 * Now creates 3D models instead of 2D sprites!
 */
public class BreakableObjectFactory {

    private static TileTextureManager textureManager = TileTextureManager.getInstance();
    private static ItemRegistry itemRegistry = ItemRegistry.getInstance();
    private static ModelManager modelManager = ModelManager.getInstance();

    /**
     * Creates a wooden crate that drops wood-related items and small amounts of gold.
     * Now uses a 3D cube model!
     */
    public static BreakableObject createCrate(float x, float y) {
        Model crateModel = modelManager.createCrateModel();
        Texture fallbackTexture = textureManager.getTexture("tiles/stone_1.png");
        BreakableObject crate = new BreakableObject(crateModel, fallbackTexture, x, y, 2, 5, 15);

        // Add loot table
        // 50% chance to drop 1-3 pieces of wood (if wood item exists)
        Item wood = itemRegistry.getItem("wood");
        if (wood != null) {
            crate.addLoot(wood, 1, 3, 0.5f);
        }

        // 25% chance to drop rope (if rope item exists)
        Item rope = itemRegistry.getItem("rope");
        if (rope != null) {
            crate.addLoot(rope, 1, 1, 0.25f);
        }

        return crate;
    }

    /**
     * Creates a clay pot that drops a small amount of gold.
     * Now uses a 3D cylinder model!
     */
    public static BreakableObject createPot(float x, float y) {
        Model potModel = modelManager.createPotModel();
        Texture fallbackTexture = textureManager.getTexture("tiles/stone_2.png");
        BreakableObject pot = new BreakableObject(potModel, fallbackTexture, x, y, 1, 1, 8);

        // Pots have a chance to drop common consumables
        // 20% chance to drop a health potion (if it exists)
        Item healthPotion = itemRegistry.getItem("health_potion");
        if (healthPotion != null) {
            pot.addLoot(healthPotion, 1, 1, 0.2f);
        }

        return pot;
    }

    /**
     * Creates a barrel that drops more valuable items.
     * Now uses a 3D cylinder model!
     */
    public static BreakableObject createBarrel(float x, float y) {
        Model barrelModel = modelManager.createBarrelModel();
        Texture fallbackTexture = textureManager.getTexture("tiles/grass_1.png");
        BreakableObject barrel = new BreakableObject(barrelModel, fallbackTexture, x, y, 3, 10, 25);

        // Add loot table for barrel
        // 30% chance to drop food items
        Item bread = itemRegistry.getItem("bread");
        if (bread != null) {
            barrel.addLoot(bread, 1, 2, 0.3f);
        }

        return barrel;
    }

    /**
     * Creates a treasure chest that requires multiple hits and drops better loot.
     * Now uses a 3D box model!
     */
    public static BreakableObject createChest(float x, float y) {
        Model chestModel = modelManager.createChestModel();
        Texture fallbackTexture = textureManager.getTexture("tiles/grass_2.png");
        BreakableObject chest = new BreakableObject(chestModel, fallbackTexture, x, y, 5, 50, 150);

        // Add valuable loot
        // 70% chance to drop equipment
        Item sword = itemRegistry.getItem("iron_sword");
        if (sword != null) {
            chest.addLoot(sword, 1, 1, 0.3f);
        }

        return chest;
    }
}
