package io.github.inherit_this.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.EnumSet;

/**
 * Central registry for all item definitions in the game.
 * Items are created once and referenced by ID.
 */
public class ItemRegistry {
    private static ItemRegistry instance;
    private final Map<String, Item> items = new HashMap<>();
    private Texture placeholderTexture;

    private ItemRegistry() {
        // Load placeholder texture once
        placeholderTexture = new Texture("items/placeholder.png");
        registerItems();
    }

    public static ItemRegistry getInstance() {
        if (instance == null) {
            instance = new ItemRegistry();
        }
        return instance;
    }

    /**
     * Loads a texture, falling back to placeholder.png if file doesn't exist or size is incorrect.
     * @param path The path to the texture file
     * @param expectedWidth Expected width in grid cells (will be multiplied by 32)
     * @param expectedHeight Expected height in grid cells (will be multiplied by 32)
     */
    private Texture loadTexture(String path, int expectedWidth, int expectedHeight) {
        // Convert grid cells to pixels
        int expectedPixelWidth = expectedWidth * 32;
        int expectedPixelHeight = expectedHeight * 32;
        return loadTexturePixels(path, expectedPixelWidth, expectedPixelHeight);
    }

    /**
     * Loads a texture with exact pixel dimensions, falling back to placeholder.png if file doesn't exist or size is incorrect.
     * @param path The path to the texture file
     * @param expectedPixelWidth Expected width in pixels
     * @param expectedPixelHeight Expected height in pixels
     */
    private Texture loadTexturePixels(String path, int expectedPixelWidth, int expectedPixelHeight) {
        if (Gdx.files.internal(path).exists()) {
            Texture tex = new Texture(path);
            tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

            // Verify texture dimensions match expected size
            if (tex.getWidth() != expectedPixelWidth || tex.getHeight() != expectedPixelHeight) {
                Gdx.app.log("ItemRegistry", "Asset size mismatch for " + path +
                    ": expected " + expectedPixelWidth + "x" + expectedPixelHeight +
                    ", got " + tex.getWidth() + "x" + tex.getHeight() +
                    ". Using placeholder.png");
                tex.dispose();
                return placeholderTexture;
            }

            return tex;
        } else {
            Gdx.app.log("ItemRegistry", "Asset not found: " + path + ", using placeholder.png");
            return placeholderTexture;
        }
    }

    /**
     * Loads a texture without size validation (useful for ground items that don't need grid constraints).
     * Falls back to placeholder.png if file doesn't exist.
     * @param path The path to the texture file
     */
    private Texture loadTextureNoValidation(String path) {
        if (Gdx.files.internal(path).exists()) {
            Texture tex = new Texture(path);
            tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            return tex;
        } else {
            Gdx.app.log("ItemRegistry", "Asset not found: " + path + ", using placeholder.png");
            return placeholderTexture;
        }
    }

    private void registerItems() {
        // Weapons (1x2 size)
        register(new Item(
            "iron_sword",
            "Iron Sword",
            "A sturdy sword forged from iron.",
            ItemType.WEAPON,
            ItemRarity.COMMON,
            loadTexture("items/iron_sword.png", 1, 2),
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
            loadTexture("items/steel_axe.png", 1, 2),
            1, 2,
            1,
            250
        ));

        // Test weapons with special effects
        register(new Item(
            "mana_draining_sword",
            "Mana Draining Sword",
            "A mystical blade that drains mana from foes.",
            ItemType.WEAPON,
            ItemRarity.RARE,
            loadTexture("items/mana_draining_sword.png", 1, 2),
            1, 2,
            1,
            500,
            2.0f,  // weight
            ItemStats.weaponWithEffects(
                15,    // damage
                100,   // durability
                1.2f,  // attack speed
                EnumSet.of(WeaponEffect.MANA_DRAIN),
                10f,   // mana drain amount
                0f,    // stamina drain amount
                0f     // life steal percent
            ),
            null,  // enchantments
            EquipmentSlot.MAIN_HAND
        ));

        register(new Item(
            "stamina_draining_axe",
            "Stamina Draining Axe",
            "A cursed axe that saps the stamina of its victims.",
            ItemType.WEAPON,
            ItemRarity.RARE,
            loadTexture("items/stamina_draining_axe.png", 1, 2),
            1, 2,
            1,
            550,
            2.5f,  // weight
            ItemStats.weaponWithEffects(
                18,    // damage
                120,   // durability
                0.9f,  // attack speed (slower)
                EnumSet.of(WeaponEffect.STAMINA_DRAIN),
                0f,    // mana drain amount
                15f,   // stamina drain amount
                0f     // life steal percent
            ),
            null,  // enchantments
            EquipmentSlot.MAIN_HAND
        ));

        register(new Item(
            "vampiric_dagger",
            "Vampiric Dagger",
            "A sinister dagger that steals life force from enemies.",
            ItemType.WEAPON,
            ItemRarity.EPIC,
            loadTexture("items/vampiric_dagger.png", 1, 2),
            1, 2,
            1,
            750,
            1.5f,  // weight
            ItemStats.weaponWithEffects(
                12,    // damage (lower but steals life)
                80,    // durability
                1.5f,  // attack speed (fast)
                EnumSet.of(WeaponEffect.LIFE_STEAL),
                0f,    // mana drain amount
                0f,    // stamina drain amount
                0.25f  // life steal percent (25% of damage as healing)
            ),
            null,  // enchantments
            EquipmentSlot.MAIN_HAND
        ));

        register(new Item(
            "cursed_blade",
            "Cursed Blade",
            "A forbidden weapon that drains both mana and stamina.",
            ItemType.WEAPON,
            ItemRarity.LEGENDARY,
            loadTexture("items/cursed_blade.png", 1, 2),
            1, 2,
            1,
            1200,
            3.0f,  // weight
            ItemStats.weaponWithEffects(
                22,    // damage (powerful but heavy cost)
                150,   // durability
                1.0f,  // attack speed (normal)
                EnumSet.of(WeaponEffect.MANA_DRAIN, WeaponEffect.STAMINA_DRAIN),
                8f,    // mana drain amount
                12f,   // stamina drain amount
                0f     // life steal percent
            ),
            null,  // enchantments
            EquipmentSlot.MAIN_HAND
        ));

        // Armor (1x1 size)
        register(new Item(
            "leather_helmet",
            "Leather Helmet",
            "Basic head protection made from leather.",
            ItemType.ARMOR,
            ItemRarity.COMMON,
            loadTexture("items/leather_helmet.png", 1, 1),
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
            loadTexture("items/iron_chestplate.png", 1, 1),
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
            loadTexture("items/health_potion.png", 1, 1),
            1, 1,
            10,
            25
        ));

        register(new Item(
            "bread",
            "Bread",
            "A simple loaf of bread. Restores some stamina.",
            ItemType.CONSUMABLE,
            ItemRarity.COMMON,
            loadTexture("items/bread.png", 1, 1),
            1, 1,
            20,
            5
        ));

        register(new Item(
            "iron_ore",
            "Iron Ore",
            "Raw iron ore. Can be smelted into bars.",
            ItemType.MATERIAL,
            ItemRarity.COMMON,
            loadTexture("items/iron_ore.png", 1, 1),
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
            loadTexture("items/wood.png", 1, 1),
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
            loadTexture("items/gold_ore.png", 1, 1),
            1, 1,
            99,
            50
        ));

        register(new Item(
            "pickaxe",
            "Pickaxe",
            "Used for mining stone and ore.",
            ItemType.TOOL,
            ItemRarity.COMMON,
            loadTexture("items/pickaxe.png", 1, 1),
            1, 1,
            1,
            80
        ));

        register(new Item(
            "fishing_rod",
            "Fishing Rod",
            "Used to catch fish from water.",
            ItemType.TOOL,
            ItemRarity.COMMON,
            loadTexture("items/fishing_rod.png", 1, 2),
            1, 2,
            1,
            60
        ));

        // Currency (1x1 size, highly stackable)
        register(new Item(
            "coins",
            "Coins",
            "Gold coins. The universal currency.",
            ItemType.CURRENCY,
            ItemRarity.COMMON,
            loadTextureNoValidation("objects/coins.png"),  // No size validation - use any size for ground rendering
            1, 1,
            999,  // Highly stackable
            1     // Worth 1 gold each
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
        if (placeholderTexture != null) {
            placeholderTexture.dispose();
        }
        for (Item item : items.values()) {
            // Only dispose non-placeholder textures
            if (item.getIcon() != placeholderTexture) {
                item.getIcon().dispose();
            }
        }
        items.clear();
    }
}
