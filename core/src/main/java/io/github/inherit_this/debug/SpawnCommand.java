package io.github.inherit_this.debug;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import io.github.inherit_this.combat.CombatManager;
import io.github.inherit_this.combat.Difficulty;
import io.github.inherit_this.combat.NPCScaling;
import io.github.inherit_this.combat.NPCTemplate;
import io.github.inherit_this.entities.Enemy;
import io.github.inherit_this.entities.Player;
import io.github.inherit_this.world.WorldProvider;

/**
 * Debug command to spawn NPCs with scaled stats.
 * Usage: spawn <type> [level] [dungeonLevel] [difficulty]
 */
public class SpawnCommand implements DebugCommand {
    private final Player player;
    private final CombatManager combatManager;
    private final WorldProvider world;
    private final Texture enemyTexture;

    public SpawnCommand(Player player, CombatManager combatManager, WorldProvider world, Texture enemyTexture) {
        this.player = player;
        this.combatManager = combatManager;
        this.world = world;
        this.enemyTexture = enemyTexture;
    }

    @Override
    public String getName() {
        return "spawn";
    }

    @Override
    public String getDescription() {
        return "Spawn NPC: spawn <type> [level] [dungeonLevel] [difficulty]";
    }

    /**
     * Load enemy-specific texture from assets/enemies folder.
     * Falls back to default enemyTexture if not found.
     */
    private Texture loadEnemyTexture(String enemyName) {
        // Convert enemy name to filename format (e.g., "Cursed Mushroom" -> "cursed_mushroom")
        String filename = "enemies/" + enemyName.toLowerCase().replace(" ", "_") + ".png";

        try {
            if (com.badlogic.gdx.Gdx.files.internal(filename).exists()) {
                Texture texture = new Texture(com.badlogic.gdx.Gdx.files.internal(filename));
                texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                return texture;
            }
        } catch (Exception e) {
            com.badlogic.gdx.Gdx.app.log("SpawnCommand", "Failed to load texture: " + filename + ", using default");
        }

        // Fall back to default texture
        return enemyTexture;
    }

    @Override
    public void execute(String[] args, DebugConsole console) {
        if (args.length < 1) {
            console.log("Usage: spawn <type> [level] [dungeonLevel] [difficulty]");
            console.log("Available types:");
            for (NPCTemplate template : NPCTemplate.getAllTemplates()) {
                console.log("  " + template.getName().toLowerCase());
            }
            console.log("Examples:");
            console.log("  spawn goblin - Spawn goblin at player level");
            console.log("  spawn orc 10 - Spawn level 10 orc");
            console.log("  spawn dragon 20 5 hard - Spawn level 20 dragon, dungeon 5, hard mode");
            return;
        }

        // Get template
        String typeName = args[0];
        NPCTemplate template = NPCTemplate.getByName(typeName);
        if (template == null) {
            console.log("Unknown NPC type: " + typeName);
            console.log("Use 'spawn' to see available types");
            return;
        }

        // Parse optional arguments
        int level = player.getStats().getLevel();  // Default to player level
        int dungeonLevel = 0;  // Default to surface
        Difficulty difficulty = Difficulty.NORMAL;  // Default difficulty

        if (args.length >= 2) {
            try {
                level = Integer.parseInt(args[1]);
                level = Math.max(1, Math.min(100, level));  // Clamp 1-100
            } catch (NumberFormatException e) {
                console.log("Invalid level: " + args[1]);
                return;
            }
        }

        if (args.length >= 3) {
            try {
                dungeonLevel = Integer.parseInt(args[2]);
                dungeonLevel = Math.max(0, Math.min(50, dungeonLevel));  // Clamp 0-50
            } catch (NumberFormatException e) {
                console.log("Invalid dungeon level: " + args[2]);
                return;
            }
        }

        if (args.length >= 4) {
            try {
                difficulty = Difficulty.valueOf(args[3].toUpperCase());
            } catch (IllegalArgumentException e) {
                console.log("Invalid difficulty: " + args[3]);
                console.log("Valid: easy, normal, hard");
                return;
            }
        }

        // Calculate scaled stats
        NPCScaling.ScaledStats stats = NPCScaling.calculateStats(template, level, dungeonLevel, difficulty);

        // Load enemy-specific texture or use default
        Texture textureToUse = loadEnemyTexture(template.getName());

        // Spawn near player (random offset 2-4 tiles away)
        float angle = MathUtils.random(0f, 360f);
        float distance = MathUtils.random(2f, 4f);
        float spawnX = player.getPosition().x + MathUtils.cosDeg(angle) * distance;
        float spawnY = player.getPosition().y + MathUtils.sinDeg(angle) * distance;

        // Create and add enemy
        Enemy enemy = new Enemy(textureToUse, spawnX, spawnY, template.getName(), world, stats);
        enemy.setDebugSpawned(true);  // Mark for debug visualization
        combatManager.getAllNPCs().add(enemy);

        console.log(String.format("Spawned %s (Lv%d) with %d HP, %d DMG",
            template.getName(), level, stats.maxHealth, stats.damage));
        console.log(String.format("  Position: (%.1f, %.1f) Player: (%.1f, %.1f) Distance: %.1f",
            spawnX, spawnY, player.getPosition().x, player.getPosition().y, distance));
        console.log(String.format("  Texture: %dx%d", textureToUse.getWidth(), textureToUse.getHeight()));
        if (dungeonLevel > 0) {
            console.log(String.format("  Dungeon level: %d, Difficulty: %s",
                dungeonLevel, difficulty.getDisplayName()));
        }
    }
}
