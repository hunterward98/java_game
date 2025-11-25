package io.github.inherit_this.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import io.github.inherit_this.util.PlaceholderTextureGenerator;
import java.util.HashMap;
import java.util.Map;

/**
 * Central registry for all item definitions in the game.
 * Items are created once and referenced by ID.
 */
public class ItemRegistry {
    private static ItemRegistry instance;
    private final Map<String, Item> items = new HashMap<>();

    private ItemRegistry() {
        registerItems();
    }

    public static ItemRegistry getInstance() {
        if (instance == null) {
            instance = new ItemRegistry();
        }
        return instance;
    }

    /**
     * Loads a texture, falling back to placeholder if file doesn't exist.
     */
    private Texture loadTexture(String itemId, String path) {
        if (Gdx.files.internal(path).exists()) {
            return new Texture(path);
        } else {
            Gdx.app.log("ItemRegistry", "Asset not found: " + path + ", using placeholder");
            return PlaceholderTextureGenerator.getPlaceholderForItem(itemId);
        }
    }

    /**
     * Register all game items here.
     */
    private void registerItems() {
        // Weapons (1x2 size)
        register(new Item(
            "iron_sword",
            "Iron Sword",
            "A sturdy sword forged from iron.",
            ItemType.WEAPON,
            ItemRarity.COMMON,
            loadTexture("iron_sword", "items/iron_sword.png"),
            1, 2, // 1 wide, 2 tall
            1,    // Not stackable
            100   // Worth 100 gold
        ));

        register(new Item(
            "steel_axe",
            "Steel Axe",
            "A heavy axe made of tempered steel.",
            ItemType.WEAPON,
            ItemRarity.UNCOMMON,
            loadTexture("steel_axe", "items/steel_axe.png"),
            1, 2,
            1,
            250
        ));

        // Armor (1x1 size)
        register(new Item(
            "leather_helmet",
            "Leather Helmet",
            "Basic head protection made from leather.",
            ItemType.ARMOR,
            ItemRarity.COMMON,
            loadTexture("leather_helmet", "items/leather_helmet.png"),
            1, 1,
            1,
            50
        ));

        register(new Item(
            "iron_chestplate",
            "Iron Chestplate",
            "Solid iron armor for the torso.",
            ItemType.ARMOR,
            ItemRarity.UNCOMMON,
            loadTexture("iron_chestplate", "items/iron_chestplate.png"),
            1, 1,
            1,
            200
        ));

        // Consumables (1x1 size, stackable)
        register(new Item(
            "health_potion",
            "Health Potion",
            "Restores 50 health points.",
            ItemType.CONSUMABLE,
            ItemRarity.COMMON,
            loadTexture("health_potion", "items/health_potion.png"),
            1, 1,
            10, // Stack up to 10
            25
        ));

        register(new Item(
            "bread",
            "Bread",
            "A simple loaf of bread. Restores some stamina.",
            ItemType.CONSUMABLE,
            ItemRarity.COMMON,
            loadTexture("bread", "items/bread.png"),
            1, 1,
            20,
            5
        ));

        // Materials (1x1 size, highly stackable)
        register(new Item(
            "iron_ore",
            "Iron Ore",
            "Raw iron ore. Can be smelted into bars.",
            ItemType.MATERIAL,
            ItemRarity.COMMON,
            loadTexture("iron_ore", "items/iron_ore.png"),
            1, 1,
            99,
            10
        ));

        register(new Item(
            "wood",
            "Wood",
            "Sturdy wooden planks for crafting.",
            ItemType.MATERIAL,
            ItemRarity.COMMON,
            loadTexture("wood", "items/wood.png"),
            1, 1,
            99,
            5
        ));

        register(new Item(
            "gold_ore",
            "Gold Ore",
            "Precious gold ore. Valuable for crafting.",
            ItemType.MATERIAL,
            ItemRarity.RARE,
            loadTexture("gold_ore", "items/gold_ore.png"),
            1, 1,
            99,
            50
        ));

        // Tools (1x2 size)
        register(new Item(
            "pickaxe",
            "Pickaxe",
            "Used for mining stone and ore.",
            ItemType.TOOL,
            ItemRarity.COMMON,
            loadTexture("pickaxe", "items/pickaxe.png"),
            1, 2,
            1,
            80
        ));

        register(new Item(
            "fishing_rod",
            "Fishing Rod",
            "Used to catch fish from water.",
            ItemType.TOOL,
            ItemRarity.COMMON,
            loadTexture("fishing_rod", "items/fishing_rod.png"),
            1, 2,
            1,
            60
        ));
    }

    /**
     * Register an item in the registry.
     */
    private void register(Item item) {
        items.put(item.getId(), item);
    }

    /**
     * Get an item by its ID.
     */
    public Item getItem(String id) {
        return items.get(id);
    }

    /**
     * Check if an item exists.
     */
    public boolean hasItem(String id) {
        return items.containsKey(id);
    }

    /**
     * Get all registered items.
     */
    public Map<String, Item> getAllItems() {
        return new HashMap<>(items);
    }

    /**
     * Dispose all item textures.
     */
    public void dispose() {
        for (Item item : items.values()) {
            item.getIcon().dispose();
        }
        items.clear();
    }
}
