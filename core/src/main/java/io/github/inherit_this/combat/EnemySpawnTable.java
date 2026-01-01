package io.github.inherit_this.combat;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Defines which enemy types spawn at different dungeon levels with weighted probabilities.
 * Ensures variety by always having 8+ enemy types available, with weights shifting based on level.
 */
public class EnemySpawnTable {

    /**
     * Get spawn weights for a specific dungeon level.
     * Returns a map of enemy templates to their spawn weights.
     */
    public static Map<NPCTemplate, Integer> getSpawnWeights(int dungeonLevel) {
        Map<NPCTemplate, Integer> weights = new HashMap<>();

        if (dungeonLevel <= 5) {
            // LEVEL 1-5: Early Dungeon
            weights.put(NPCTemplate.SLIME, 25);
            weights.put(NPCTemplate.GOBLIN, 20);
            weights.put(NPCTemplate.SKELETON, 15);
            weights.put(NPCTemplate.WOLF, 15);
            weights.put(NPCTemplate.CURSED_MUSHROOM, 15);
            weights.put(NPCTemplate.ORC, 7);
            weights.put(NPCTemplate.VAMPIRE, 2);
            weights.put(NPCTemplate.TROLL, 1);

        } else if (dungeonLevel <= 15) {
            // LEVEL 6-15: Mid Dungeon - 9 types
            weights.put(NPCTemplate.SLIME, 15);
            weights.put(NPCTemplate.GOBLIN, 15);
            weights.put(NPCTemplate.SKELETON, 15);
            weights.put(NPCTemplate.WOLF, 15);
            weights.put(NPCTemplate.CURSED_MUSHROOM, 10);
            weights.put(NPCTemplate.ORC, 15);
            weights.put(NPCTemplate.VAMPIRE, 8);
            weights.put(NPCTemplate.TROLL, 5);
            weights.put(NPCTemplate.DRAGON, 2);

        } else if (dungeonLevel <= 30) {
            // LEVEL 16-30: Deep Dungeon - 9 types
            weights.put(NPCTemplate.SLIME, 10);
            weights.put(NPCTemplate.GOBLIN, 10);
            weights.put(NPCTemplate.SKELETON, 12);
            weights.put(NPCTemplate.WOLF, 12);
            weights.put(NPCTemplate.CURSED_MUSHROOM, 8);
            weights.put(NPCTemplate.ORC, 15);
            weights.put(NPCTemplate.VAMPIRE, 15);
            weights.put(NPCTemplate.TROLL, 10);
            weights.put(NPCTemplate.DRAGON, 8);

        } else if (dungeonLevel <= 50) {
            // LEVEL 31-50: End Game - 9 types
            weights.put(NPCTemplate.SLIME, 8);
            weights.put(NPCTemplate.GOBLIN, 8);
            weights.put(NPCTemplate.SKELETON, 10);
            weights.put(NPCTemplate.WOLF, 10);
            weights.put(NPCTemplate.CURSED_MUSHROOM, 6);
            weights.put(NPCTemplate.ORC, 12);
            weights.put(NPCTemplate.VAMPIRE, 18);
            weights.put(NPCTemplate.TROLL, 15);
            weights.put(NPCTemplate.DRAGON, 13);

        } else {
            // LEVEL 51+: Post-game - 9 types
            weights.put(NPCTemplate.SLIME, 5);
            weights.put(NPCTemplate.GOBLIN, 5);
            weights.put(NPCTemplate.SKELETON, 8);
            weights.put(NPCTemplate.WOLF, 8);
            weights.put(NPCTemplate.CURSED_MUSHROOM, 4);
            weights.put(NPCTemplate.ORC, 10);
            weights.put(NPCTemplate.VAMPIRE, 20);
            weights.put(NPCTemplate.TROLL, 20);
            weights.put(NPCTemplate.DRAGON, 20);
        }

        return weights;
    }

    /**
     * Select a random enemy template based on dungeon level spawn weights.
     */
    public static NPCTemplate selectRandomEnemy(int dungeonLevel, Random random) {
        Map<NPCTemplate, Integer> weights = getSpawnWeights(dungeonLevel);

        // Calculate total weight
        int totalWeight = 0;
        for (int weight : weights.values()) {
            totalWeight += weight;
        }

        // Select random value
        int randomValue = random.nextInt(totalWeight);

        // Find which enemy was selected
        int currentWeight = 0;
        for (Map.Entry<NPCTemplate, Integer> entry : weights.entrySet()) {
            currentWeight += entry.getValue();
            if (randomValue < currentWeight) {
                return entry.getKey();
            }
        }

        // Fallback (should never happen)
        return NPCTemplate.GOBLIN;
    }

    /**
     * Get the number of enemy types available at a dungeon level.
     */
    public static int getEnemyTypeCount(int dungeonLevel) {
        return getSpawnWeights(dungeonLevel).size();
    }
}
