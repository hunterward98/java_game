package io.github.inherit_this.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import io.github.inherit_this.world.WorldProvider;

/**
 * Hostile NPC that attacks the player on sight.
 * Implements aggressive AI with chase and attack behaviors.
 */
public class Enemy extends NPC {

    private float wanderChangeTimer = 0f;
    private static final float WANDER_CHANGE_INTERVAL = 3f; // Change direction every 3 seconds

    public Enemy(Texture texture, float x, float y, String name, WorldProvider world) {
        super(texture, x, y, name, NPCType.HOSTILE, world);

        // Enemy-specific stat adjustments
        this.detectionRange = 10f;  // Can see player from 10 tiles away
        this.attackRange = 1.2f;    // Melee range
        this.speed = 4f;            // Slightly slower than player
    }

    @Override
    protected void updateAI(float delta, Player player) {
        switch (state) {
            case IDLE:
                // Idle enemies look for the player
                if (canSee(player)) {
                    targetEntity = player;
                    state = NPCState.CHASE;
                    stateTimer = 0f;
                } else {
                    // Randomly start wandering
                    wanderChangeTimer += delta;
                    if (wanderChangeTimer >= WANDER_CHANGE_INTERVAL) {
                        if (MathUtils.random() < 0.3f) {
                            state = NPCState.WANDER;
                            stateTimer = 0f;
                        }
                        wanderChangeTimer = 0f;
                    }
                }
                break;

            case WANDER:
                wanderChangeTimer += delta;

                // Set random target position
                if (targetPosition == null || wanderChangeTimer >= WANDER_CHANGE_INTERVAL) {
                    float wanderRadius = 5f;
                    float randomAngle = MathUtils.random(0f, 360f);
                    targetPosition = new Vector2(
                        position.x + MathUtils.cosDeg(randomAngle) * wanderRadius,
                        position.y + MathUtils.sinDeg(randomAngle) * wanderRadius
                    );
                    wanderChangeTimer = 0f;
                }

                // Check for player while wandering
                if (canSee(player)) {
                    targetEntity = player;
                    state = NPCState.CHASE;
                    targetPosition = null;
                    stateTimer = 0f;
                }

                // Return to idle after wandering for a while
                if (stateTimer > 10f) {
                    state = NPCState.IDLE;
                    targetPosition = null;
                    stateTimer = 0f;
                }
                break;

            case CHASE:
                if (targetEntity != null) {
                    // Chase the target
                    targetPosition = new Vector2(targetEntity.getPosition());

                    // Check if in attack range
                    if (inAttackRange(targetEntity)) {
                        state = NPCState.ATTACK;
                        targetPosition = null;
                        stateTimer = 0f;
                    }

                    // Stop chasing if target is too far
                    if (!canSee(targetEntity)) {
                        state = NPCState.IDLE;
                        targetEntity = null;
                        targetPosition = null;
                        stateTimer = 0f;
                    }
                } else {
                    state = NPCState.IDLE;
                    stateTimer = 0f;
                }
                break;

            case ATTACK:
                if (targetEntity != null) {
                    // Face target and attack
                    if (inAttackRange(targetEntity)) {
                        if (attack(targetEntity)) {
                            // Attack successful - deal damage
                            if (targetEntity instanceof Player) {
                                ((Player) targetEntity).getStats().takeDamage(damage);
                            }
                        }
                    } else {
                        // Target moved out of range, chase again
                        state = NPCState.CHASE;
                        stateTimer = 0f;
                    }

                    // Check if target is still visible
                    if (!canSee(targetEntity)) {
                        state = NPCState.IDLE;
                        targetEntity = null;
                        stateTimer = 0f;
                    }
                } else {
                    state = NPCState.IDLE;
                    stateTimer = 0f;
                }
                break;

            case FLEE:
                // Run away from target
                if (targetEntity != null) {
                    Vector2 awayDir = new Vector2(
                        position.x - targetEntity.getPosition().x,
                        position.y - targetEntity.getPosition().y
                    ).nor();

                    targetPosition = new Vector2(
                        position.x + awayDir.x * 10f,
                        position.y + awayDir.y * 10f
                    );

                    // Stop fleeing after getting far enough
                    if (!canSee(targetEntity)) {
                        state = NPCState.IDLE;
                        targetPosition = null;
                        stateTimer = 0f;
                    }
                }
                break;

            case DEAD:
                // Do nothing when dead
                break;
        }
    }

    @Override
    protected void onDeath(Entity killer) {
        super.onDeath(killer);

        // Give XP and gold to player
        if (killer instanceof Player) {
            Player player = (Player) killer;
            player.getStats().addXP(xpValue);
            player.getInventory().addGold(goldDrop);
        }
    }
}
