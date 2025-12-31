package io.github.inherit_this.combat;

/**
 * Template defining base stats for an NPC type.
 * These values are scaled based on player level, dungeon level, and difficulty.
 */
public class NPCTemplate {
    private final String name;
    private final int baseHealth;
    private final int baseDamage;
    private final float baseSpeed;
    private final float detectionRange;
    private final float attackRange;
    private final float attackCooldown;
    private final int baseGold;
    private final int baseXP;
    private final float intelligence;  // 0.0-1.0, affects AI decision making

    public NPCTemplate(String name, int baseHealth, int baseDamage, float baseSpeed,
                      float detectionRange, float attackRange, float attackCooldown,
                      int baseGold, int baseXP, float intelligence) {
        this.name = name;
        this.baseHealth = baseHealth;
        this.baseDamage = baseDamage;
        this.baseSpeed = baseSpeed;
        this.detectionRange = detectionRange;
        this.attackRange = attackRange;
        this.attackCooldown = attackCooldown;
        this.baseGold = baseGold;
        this.baseXP = baseXP;
        this.intelligence = Math.max(0f, Math.min(1f, intelligence));
    }

    // Getters
    public String getName() { return name; }
    public int getBaseHealth() { return baseHealth; }
    public int getBaseDamage() { return baseDamage; }
    public float getBaseSpeed() { return baseSpeed; }
    public float getDetectionRange() { return detectionRange; }
    public float getAttackRange() { return attackRange; }
    public float getAttackCooldown() { return attackCooldown; }
    public int getBaseGold() { return baseGold; }
    public int getBaseXP() { return baseXP; }
    public float getIntelligence() { return intelligence; }

    // Common enemy templates
    public static final NPCTemplate GOBLIN = new NPCTemplate(
        "Goblin", 50, 8, 4f, 8f, 1.2f, 1.2f, 5, 15, 0.3f
    );

    public static final NPCTemplate ORC = new NPCTemplate(
        "Orc", 100, 15, 3f, 10f, 1.5f, 1.5f, 15, 30, 0.4f
    );

    public static final NPCTemplate SKELETON = new NPCTemplate(
        "Skeleton", 60, 10, 3.5f, 12f, 1.3f, 1.0f, 8, 20, 0.5f
    );

    public static final NPCTemplate TROLL = new NPCTemplate(
        "Troll", 200, 25, 2.5f, 6f, 2f, 2.0f, 30, 60, 0.2f
    );

    public static final NPCTemplate DRAGON = new NPCTemplate(
        "Dragon", 500, 50, 5f, 15f, 3f, 3.0f, 100, 200, 0.8f
    );

    public static final NPCTemplate SLIME = new NPCTemplate(
        "Slime", 30, 5, 2f, 5f, 1f, 1.5f, 3, 10, 0.1f
    );

    public static final NPCTemplate WOLF = new NPCTemplate(
        "Wolf", 70, 12, 5f, 12f, 1.2f, 0.8f, 10, 25, 0.6f
    );

    public static final NPCTemplate VAMPIRE = new NPCTemplate(
        "Vampire", 150, 20, 4.5f, 14f, 1.5f, 1.2f, 40, 75, 0.9f
    );

    public static final NPCTemplate CURSED_MUSHROOM = new NPCTemplate(
        "Cursed_Mushroom", 80, 10, 1.5f, 6f, 1.0f, 1.8f, 12, 20, 0.1f
    );

    /**
     * Get all available templates for spawning.
     */
    public static NPCTemplate[] getAllTemplates() {
        return new NPCTemplate[] {
            GOBLIN, ORC, SKELETON, TROLL, DRAGON, SLIME, WOLF, VAMPIRE, CURSED_MUSHROOM
        };
    }

    /**
     * Get template by name (case-insensitive).
     */
    public static NPCTemplate getByName(String name) {
        for (NPCTemplate template : getAllTemplates()) {
            if (template.getName().equalsIgnoreCase(name)) {
                return template;
            }
        }
        return null;
    }
}
