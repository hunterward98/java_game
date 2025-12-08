package io.github.inherit_this.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.inherit_this.world.WorldProvider;

/**
 * Base class for all NPCs (enemies, friendly NPCs, etc.)
 * Provides health, AI behavior, and combat capabilities.
 */
public abstract class NPC extends Entity {

    protected final NPCType type;
    protected final WorldProvider world;
    protected final String name;

    // Stats
    protected int maxHealth;
    protected int currentHealth;
    protected int damage;
    protected float speed;
    protected float detectionRange;  // How far NPC can see player
    protected float attackRange;     // How close to attack

    // AI state
    protected Vector2 targetPosition;
    protected Entity targetEntity;   // Current target (player or another NPC)
    protected NPCState state;
    protected float stateTimer;      // Timer for current state

    // Combat
    protected float attackCooldown;
    protected float timeSinceLastAttack;

    // Loot
    protected int goldDrop;
    protected int xpValue;

    public enum NPCState {
        IDLE,       // Standing still
        WANDER,     // Random movement
        CHASE,      // Pursuing target
        ATTACK,     // In combat
        FLEE,       // Running away
        DEAD        // Defeated
    }

    public NPC(Texture texture, float x, float y, String name, NPCType type, WorldProvider world) {
        super(texture, x, y);
        this.name = name;
        this.type = type;
        this.world = world;
        this.state = NPCState.IDLE;
        this.stateTimer = 0f;
        this.timeSinceLastAttack = 0f;

        // Default stats (override in subclasses)
        this.maxHealth = 100;
        this.currentHealth = maxHealth;
        this.damage = 10;
        this.speed = 3f;  // tiles per second
        this.detectionRange = 8f;  // tiles
        this.attackRange = 1.5f;   // tiles
        this.attackCooldown = 1.0f; // seconds
        this.goldDrop = 10;
        this.xpValue = 25;
    }

    /**
     * Update NPC behavior and AI.
     */
    public void update(float delta, Player player) {
        if (state == NPCState.DEAD) {
            return;
        }

        stateTimer += delta;
        timeSinceLastAttack += delta;

        // Update AI based on NPC type and current state
        updateAI(delta, player);

        // Move toward target position if set
        if (targetPosition != null) {
            moveToward(targetPosition, delta);
        }
    }

    /**
     * AI behavior - override in subclasses for custom behavior.
     */
    protected abstract void updateAI(float delta, Player player);

    /**
     * Move toward a target position.
     */
    protected void moveToward(Vector2 target, float delta) {
        float dx = target.x - position.x;
        float dy = target.y - position.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance > 0.1f) {
            float moveX = (dx / distance) * speed * delta;
            float moveY = (dy / distance) * speed * delta;

            // Simple movement (no collision for now)
            position.x += moveX;
            position.y += moveY;
        } else {
            targetPosition = null;
        }
    }

    /**
     * Take damage from an attack.
     */
    public void takeDamage(int amount, Entity attacker) {
        if (state == NPCState.DEAD) {
            return;
        }

        currentHealth -= amount;

        if (currentHealth <= 0) {
            currentHealth = 0;
            state = NPCState.DEAD;
            onDeath(attacker);
        } else {
            onHit(attacker);
        }
    }

    /**
     * Called when NPC is hit but not killed.
     */
    protected void onHit(Entity attacker) {
        // Become aggressive if not already
        if (type == NPCType.NEUTRAL || type == NPCType.PASSIVE) {
            targetEntity = attacker;
            state = NPCState.CHASE;
        }
    }

    /**
     * Called when NPC dies.
     */
    protected void onDeath(Entity killer) {
        // Drop loot, give XP, etc. - implement in game logic
        System.out.println(name + " has been defeated!");
    }

    /**
     * Attack the target entity.
     */
    public boolean attack(Entity target) {
        if (timeSinceLastAttack >= attackCooldown) {
            timeSinceLastAttack = 0f;
            // Deal damage logic goes here
            return true;
        }
        return false;
    }

    /**
     * Calculate distance to another entity.
     */
    protected float distanceTo(Entity other) {
        float dx = other.getPosition().x - position.x;
        float dy = other.getPosition().y - position.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Check if NPC can see the target.
     */
    protected boolean canSee(Entity target) {
        return distanceTo(target) <= detectionRange;
    }

    /**
     * Check if target is in attack range.
     */
    protected boolean inAttackRange(Entity target) {
        return distanceTo(target) <= attackRange;
    }

    // Getters
    public NPCType getType() { return type; }
    public String getName() { return name; }
    public int getCurrentHealth() { return currentHealth; }
    public int getMaxHealth() { return maxHealth; }
    public NPCState getState() { return state; }
    public boolean isDead() { return state == NPCState.DEAD; }
    public int getGoldDrop() { return goldDrop; }
    public int getXPValue() { return xpValue; }
    public int getDamage() { return damage; }
}
