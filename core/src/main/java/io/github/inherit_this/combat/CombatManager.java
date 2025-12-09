package io.github.inherit_this.combat;

import com.badlogic.gdx.graphics.Texture;
import io.github.inherit_this.entities.Enemy;
import io.github.inherit_this.entities.NPC;
import io.github.inherit_this.entities.Player;
import io.github.inherit_this.world.WorldProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages NPCs, enemies, and combat logic.
 * Extracted from GameScreen to reduce complexity.
 */
public class CombatManager {
    private final List<NPC> npcs;
    private final Player player;

    public CombatManager(Player player) {
        this.player = player;
        this.npcs = new ArrayList<>();
    }

    /**
     * Update all NPCs (AI, movement, combat).
     */
    public void update(float delta) {
        // Update all NPCs
        for (NPC npc : npcs) {
            npc.update(delta, player);
        }

        // Remove dead NPCs
        npcs.removeIf(NPC::isDead);
    }

    /**
     * Spawn test enemies at specific positions.
     */
    public void spawnTestEnemies(int[] spawnPos, WorldProvider world, Texture enemyTexture) {
        npcs.add(new Enemy(enemyTexture, spawnPos[0] + 100, spawnPos[1], "Skeleton", world));
        npcs.add(new Enemy(enemyTexture, spawnPos[0] - 100, spawnPos[1] + 50, "Zombie", world));
        npcs.add(new Enemy(enemyTexture, spawnPos[0], spawnPos[1] + 150, "Ghost", world));
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
