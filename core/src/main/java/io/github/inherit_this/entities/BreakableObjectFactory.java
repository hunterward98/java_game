package io.github.inherit_this.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import io.github.inherit_this.items.Item;
import io.github.inherit_this.items.ItemRegistry;
import io.github.inherit_this.loot.LootTableGenerator;
import io.github.inherit_this.loot.ScaledLootTable;
import io.github.inherit_this.world.TileTextureManager;
import io.github.inherit_this.world.ModelManager;

/**
 * Factory for creating different types of breakable objects with scaled loot.
 * Loot scales based on player level and dungeon depth.
 */
public class BreakableObjectFactory {

    private static TileTextureManager textureManager = TileTextureManager.getInstance();
    private static ItemRegistry itemRegistry = ItemRegistry.getInstance();
    private static ModelManager modelManager = ModelManager.getInstance();
    private static LootTableGenerator lootGenerator = LootTableGenerator.getInstance();

    /**
     * Creates a wooden crate with scaled loot based on player and dungeon level.
     * @param x X position in tiles
     * @param y Y position in tiles
     * @param playerLevel Current player level (1-100)
     * @param dungeonLevel Current dungeon level (0 if overworld)
     */
    public static BreakableObject createCrate(float x, float y, int playerLevel, int dungeonLevel) {
        Model crateModel = modelManager.createCrateModel();
        Texture fallbackTexture = textureManager.getTexture("tiles/stone_1.png");

        // Generate scaled loot table
        ScaledLootTable lootTable = lootGenerator.generateLootTable(
            playerLevel, dungeonLevel, LootTableGenerator.ObjectType.CRATE
        );

        // Create object with scaled gold and XP
        BreakableObject crate = new BreakableObject(
            crateModel, fallbackTexture, x, y,
            2, lootTable.getGoldMin(), lootTable.getGoldMax()
        );
        crate.setName("Crate");
        crate.setXPReward(lootTable.getXP());

        // Add scaled item drops
        for (ScaledLootTable.ItemDrop drop : lootTable.getItemDrops()) {
            crate.addLoot(drop.getItem(), drop.getMinQuantity(), drop.getMaxQuantity(), drop.getDropChance());
        }

        return crate;
    }

    /**
     * Creates a clay pot with scaled loot based on player and dungeon level.
     * @param x X position in tiles
     * @param y Y position in tiles
     * @param playerLevel Current player level (1-100)
     * @param dungeonLevel Current dungeon level (0 if overworld)
     */
    public static BreakableObject createPot(float x, float y, int playerLevel, int dungeonLevel) {
        Model potModel = modelManager.createPotModel();
        Texture fallbackTexture = textureManager.getTexture("tiles/stone_2.png");

        // Generate scaled loot table
        ScaledLootTable lootTable = lootGenerator.generateLootTable(
            playerLevel, dungeonLevel, LootTableGenerator.ObjectType.POT
        );

        // Create object with scaled gold and XP
        BreakableObject pot = new BreakableObject(
            potModel, fallbackTexture, x, y,
            1, lootTable.getGoldMin(), lootTable.getGoldMax()
        );
        pot.setName("Pot");
        pot.setXPReward(lootTable.getXP());

        // Add scaled item drops
        for (ScaledLootTable.ItemDrop drop : lootTable.getItemDrops()) {
            pot.addLoot(drop.getItem(), drop.getMinQuantity(), drop.getMaxQuantity(), drop.getDropChance());
        }

        return pot;
    }

    /**
     * Creates a barrel with scaled loot based on player and dungeon level.
     * @param x X position in tiles
     * @param y Y position in tiles
     * @param playerLevel Current player level (1-100)
     * @param dungeonLevel Current dungeon level (0 if overworld)
     */
    public static BreakableObject createBarrel(float x, float y, int playerLevel, int dungeonLevel) {
        Model barrelModel = modelManager.createBarrelModel();
        Texture fallbackTexture = textureManager.getTexture("tiles/grass_1.png");

        // Generate scaled loot table
        ScaledLootTable lootTable = lootGenerator.generateLootTable(
            playerLevel, dungeonLevel, LootTableGenerator.ObjectType.BARREL
        );

        // Create object with scaled gold and XP
        BreakableObject barrel = new BreakableObject(
            barrelModel, fallbackTexture, x, y,
            3, lootTable.getGoldMin(), lootTable.getGoldMax()
        );
        barrel.setName("Barrel");
        barrel.setXPReward(lootTable.getXP());

        // Add scaled item drops
        for (ScaledLootTable.ItemDrop drop : lootTable.getItemDrops()) {
            barrel.addLoot(drop.getItem(), drop.getMinQuantity(), drop.getMaxQuantity(), drop.getDropChance());
        }

        return barrel;
    }
}
