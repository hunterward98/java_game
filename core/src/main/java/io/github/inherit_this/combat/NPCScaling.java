package io.github.inherit_this.combat;

/**
 * Calculates scaled NPC stats based on template, player level, dungeon level, and difficulty.
 * Provides dynamic challenge scaling for enemies.
 */
public class NPCScaling {

    /**
     * Scaled stats result.
     */
    public static class ScaledStats {
        public final int maxHealth;
        public final int damage;
        public final float speed;
        public final float detectionRange;
        public final float attackRange;
        public final float attackCooldown;
        public final int coinsDrop;
        public final int xpValue;
        public final float intelligence;

        public ScaledStats(int maxHealth, int damage, float speed, float detectionRange,
                          float attackRange, float attackCooldown, int coinsDrop, int xpValue,
                          float intelligence) {
            this.maxHealth = maxHealth;
            this.damage = damage;
            this.speed = speed;
            this.detectionRange = detectionRange;
            this.attackRange = attackRange;
            this.attackCooldown = attackCooldown;
            this.coinsDrop = coinsDrop;
            this.xpValue = xpValue;
            this.intelligence = intelligence;
        }
    }

    /**
     * Calculate scaled stats for an NPC.
     *
     * @param template The base NPC template
     * @param playerLevel Current player level (1-100)
     * @param dungeonLevel Dungeon depth (0 = surface, higher = deeper)
     * @param difficulty Game difficulty setting
     * @return Scaled stats
     */
    public static ScaledStats calculateStats(NPCTemplate template, int playerLevel,
                                             int dungeonLevel, Difficulty difficulty) {
        // Level scaling: stats increase 10% per player level
        float levelMultiplier = 1.0f + (playerLevel - 1) * 0.10f;

        // Dungeon scaling: stats increase 5% per dungeon level
        float dungeonMultiplier = 1.0f + dungeonLevel * 0.05f;

        // Combined scaling before difficulty
        float combinedMultiplier = levelMultiplier * dungeonMultiplier;

        // Health scaling: scales heavily with levels
        int scaledHealth = Math.round(template.getBaseHealth() * combinedMultiplier * difficulty.getHealthMultiplier());

        // Damage scaling: scales moderately with levels
        int scaledDamage = Math.round(template.getBaseDamage() * combinedMultiplier * difficulty.getDamageMultiplier());

        // Speed: slight increase with dungeonlevel, affected by difficulty AI speed
        float scaledSpeed = template.getBaseSpeed() * (1.0f + dungeonLevel * 0.02f) * difficulty.getAiSpeedMultiplier();

        // Detection range: increases slightly with dungeon level (smarter enemies)
        float scaledDetection = template.getDetectionRange() * (1.0f + dungeonLevel * 0.03f);

        // Attack range stays mostly the same
        float scaledAttackRange = template.getAttackRange();

        // Attack cooldown: decreases (faster attacks) with levels and difficulty
        float scaledCooldown = template.getAttackCooldown() / (1.0f + playerLevel * 0.01f) / difficulty.getAiSpeedMultiplier();
        scaledCooldown = Math.max(0.3f, scaledCooldown); // Min cooldown of 0.3s

        // Coins and XP scale with levels and difficulty
        int scaledCoins = Math.round(template.getBaseGold() * levelMultiplier * difficulty.getLootMultiplier());
        int scaledXP = Math.round(template.getBaseXP() * levelMultiplier);

        // Intelligence increases slightly with dungeon level and difficulty
        float scaledIntelligence = Math.min(1.0f, template.getIntelligence() +
            dungeonLevel * 0.02f +
            (difficulty == Difficulty.HARD ? 0.1f : difficulty == Difficulty.EASY ? -0.1f : 0f));

        return new ScaledStats(
            scaledHealth,
            scaledDamage,
            scaledSpeed,
            scaledDetection,
            scaledAttackRange,
            scaledCooldown,
            scaledCoins,
            scaledXP,
            scaledIntelligence
        );
    }

    /**
     * Calculate stats using current game state.
     * For when difficulty setting is not yet implemented, defaults to NORMAL.
     *
     * @param template The base NPC template
     * @param playerLevel Current player level
     * @param dungeonLevel Dungeon depth
     * @return Scaled stats using NORMAL difficulty
     */
    public static ScaledStats calculateStats(NPCTemplate template, int playerLevel, int dungeonLevel) {
        return calculateStats(template, playerLevel, dungeonLevel, Difficulty.NORMAL);
    }
}
