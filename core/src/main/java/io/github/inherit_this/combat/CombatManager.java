package io.github.inherit_this.combat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import io.github.inherit_this.entities.Enemy;
import io.github.inherit_this.entities.NPC;
import io.github.inherit_this.entities.Player;
import io.github.inherit_this.particles.ParticleSystem;
import io.github.inherit_this.world.DungeonRoom;
import io.github.inherit_this.world.ProceduralDungeonGenerator;
import io.github.inherit_this.world.WorldProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Manages NPCs, enemies, and combat logic.
 * Extracted from GameScreen to reduce complexity.
 */
public class CombatManager {
    private final List<NPC> npcs;
    private final Player player;
    private final ParticleSystem particleSystem;

    public CombatManager(Player player, ParticleSystem particleSystem) {
        this.player = player;
        this.particleSystem = particleSystem;
        this.npcs = new ArrayList<>();
    }

    /**
     * Update all NPCs (AI, movement, combat).
     */
    public void update(float delta) {
        // Update all NPCs with particle system for combat effects
        for (NPC npc : npcs) {
            npc.update(delta, player, particleSystem);
        }

        // Remove dead NPCs
        npcs.removeIf(NPC::isDead);
    }

    /**
     * Get the particle system for combat effects.
     */
    public ParticleSystem getParticleSystem() {
        return particleSystem;
    }


    /**
     * Spawn enemies throughout a dungeon (rooms and hallways).
     * Spawns 50-120 enemies total, accounting for enemy difficulty.
     * Stronger enemies count as more "difficulty points" so fewer spawn.
     *
     * @param generator The dungeon generator
     * @param dungeonLevel The dungeon level (affects enemy types and scaling)
     * @param playerLevel The player's level (affects enemy scaling)
     * @param world The world provider for enemy AI
     * @param fallbackTexture Texture to use for enemies (typically character.png as placeholder)
     */
    public void spawnDungeonEnemies(ProceduralDungeonGenerator generator, int dungeonLevel,
                                   int playerLevel, WorldProvider world, Texture fallbackTexture) {
        Random random = new Random();

        // Target: 50-120 enemies based on difficulty budget
        // Difficulty budget: 80-150 (accounts for enemy strength)
        int minDifficultyBudget = 80;
        int maxDifficultyBudget = 150;
        int difficultyBudget = minDifficultyBudget + random.nextInt(maxDifficultyBudget - minDifficultyBudget);

        Gdx.app.log("CombatManager", "=== SPAWNING ENEMIES ===");
        Gdx.app.log("CombatManager", "Dungeon Level: " + dungeonLevel + ", Player Level: " + playerLevel);
        Gdx.app.log("CombatManager", "Difficulty Budget: " + difficultyBudget);

        // Texture cache for enemy sprites
        Map<String, Texture> textureCache = new HashMap<>();

        // Collect all valid spawn positions from rooms and hallways
        List<int[]> spawnPositions = new ArrayList<>();

        // Add room positions
        List<DungeonRoom> rooms = generator.getRooms();
        for (DungeonRoom room : rooms) {
            // Sample positions in room (not every tile)
            int samples = Math.max(5, (room.width * room.height) / 20);
            for (int i = 0; i < samples; i++) {
                int margin = 2;
                int rx = (int) room.x + margin + random.nextInt(Math.max(1, room.width - margin * 2));
                int ry = (int) room.y + margin + random.nextInt(Math.max(1, room.height - margin * 2));
                if (!generator.isWall(rx, ry)) {
                    spawnPositions.add(new int[]{rx, ry});
                }
            }
        }

        // Add hallway positions
        int width = generator.getWidthInTiles();
        int height = generator.getHeightInTiles();
        int hallwaySamples = (width * height) / 30; // Sample hallways
        for (int i = 0; i < hallwaySamples; i++) {
            int hx = random.nextInt(width);
            int hy = random.nextInt(height);
            if (!generator.isWall(hx, hy)) {
                // Check if it's not in a room (hallway)
                boolean inRoom = false;
                for (DungeonRoom room : rooms) {
                    if (hx >= room.x && hx < room.x + room.width &&
                        hy >= room.y && hy < room.y + room.height) {
                        inRoom = true;
                        break;
                    }
                }
                if (!inRoom) {
                    spawnPositions.add(new int[]{hx, hy});
                }
            }
        }

        Gdx.app.log("CombatManager", "Found " + spawnPositions.size() + " valid spawn positions");

        // Shuffle spawn positions for variety
        for (int i = spawnPositions.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int[] temp = spawnPositions.get(i);
            spawnPositions.set(i, spawnPositions.get(j));
            spawnPositions.set(j, temp);
        }

        // Spawn enemies until we hit budget or run out of positions
        int totalEnemiesSpawned = 0;
        float currentDifficulty = 0;
        int positionIndex = 0;

        while (currentDifficulty < difficultyBudget && positionIndex < spawnPositions.size() && totalEnemiesSpawned < 120) {
            // Select enemy type
            NPCTemplate template = EnemySpawnTable.selectRandomEnemy(dungeonLevel, random);

            // Calculate enemy difficulty (based on base stats)
            float enemyDifficulty = (template.getBaseHealth() / 50f) + (template.getBaseDamage() / 10f);

            // Don't exceed budget too much
            if (currentDifficulty + enemyDifficulty > difficultyBudget + 10 && totalEnemiesSpawned >= 50) {
                break;
            }

            // Get spawn position
            int[] pos = spawnPositions.get(positionIndex);
            positionIndex++;

            // Scale enemy stats
            NPCScaling.ScaledStats stats = NPCScaling.calculateStats(template, playerLevel, dungeonLevel);

            // Load enemy-specific texture (or use fallback)
            Texture enemyTexture = getEnemyTexture(template.getName(), textureCache, fallbackTexture);

            // Create and spawn the enemy
            Enemy enemy = new Enemy(enemyTexture, pos[0], pos[1], template.getName(), world, stats);
            npcs.add(enemy);
            totalEnemiesSpawned++;
            currentDifficulty += enemyDifficulty;
        }

        Gdx.app.log("CombatManager", "Spawned " + totalEnemiesSpawned + " enemies (difficulty: " + currentDifficulty + "/" + difficultyBudget + ")");
        Gdx.app.log("CombatManager", EnemySpawnTable.getEnemyTypeCount(dungeonLevel) + " enemy types available");
        Gdx.app.log("CombatManager", "=== SPAWN COMPLETE ===");
    }

    /**
     * Load enemy texture from cache or file system.
     * Tries to load from "enemies/{enemyName}.png", falls back to provided texture if not found.
     */
    private Texture getEnemyTexture(String enemyName, Map<String, Texture> cache, Texture fallback) {
        // Check cache first
        if (cache.containsKey(enemyName)) {
            return cache.get(enemyName);
        }

        // Try to load enemy-specific texture
        String texturePath = "enemies/" + enemyName.toLowerCase() + ".png";
        Texture texture;

        try {
            texture = new Texture(texturePath);
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            Gdx.app.log("CombatManager", "Loaded texture for " + enemyName + " from " + texturePath);
        } catch (Exception e) {
            // Fall back to provided texture if specific texture not found
            texture = fallback;
            Gdx.app.log("CombatManager", "Using fallback texture for " + enemyName + " (no texture at " + texturePath + ")");
        }

        // Cache the result
        cache.put(enemyName, texture);
        return texture;
    }

    /**
     * Find the nearest NPC to a world position within a maximum distance.
     */
    public NPC findNearestNPC(float worldX, float worldZ, float maxDistance) {
        NPC nearest = null;
        float nearestDist = Float.MAX_VALUE;

        for (NPC npc : npcs) {
            if (npc.isDead()) continue;

            float dx = npc.getPosition().x - worldX;
            float dy = npc.getPosition().y - worldZ;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);

            if (dist < nearestDist && dist <= maxDistance) {
                nearest = npc;
                nearestDist = dist;
            }
        }

        return nearest;
    }

    /**
     * Clear all NPCs (e.g., when exiting dungeon).
     */
    public void clearAll() {
        npcs.clear();
    }

    /**
     * Get all active NPCs.
     */
    public List<NPC> getAllNPCs() {
        return npcs;
    }

    /**
     * Get count of active NPCs.
     */
    public int getNPCCount() {
        return npcs.size();
    }
}
