package io.github.inherit_this.entities;

import com.badlogic.gdx.graphics.Texture;
import io.github.inherit_this.world.WorldProvider;

/**
 * Friendly NPC for towns and safe areas.
 * Can be talked to for quests, shops, or dialogue.
 */
public class FriendlyNPC extends NPC {

    private final String dialogue;
    private final NPCRole role;

    public enum NPCRole {
        MERCHANT,    // Sells items
        QUEST_GIVER, // Provides quests
        GUARD,       // Town guard
        VILLAGER     // Generic NPC
    }

    public FriendlyNPC(Texture texture, float x, float y, String name, NPCRole role, String dialogue, WorldProvider world) {
        super(texture, x, y, name, NPCType.FRIENDLY, world);
        this.role = role;
        this.dialogue = dialogue != null ? dialogue : "Greetings, traveler!";

        // Friendly NPCs don't fight
        this.damage = 0;
        this.detectionRange = 3f;  // Only notices player when close
        this.attackRange = 0f;
    }

    @Override
    protected void updateAI(float delta, Player player) {
        // Friendly NPCs just stand idle or wander
        switch (state) {
            case IDLE:
                // Just stand around
                // Could add random idle animations here
                break;

            case WANDER:
                // Optional: implement wandering behavior for villagers
                break;

            default:
                state = NPCState.IDLE;
                break;
        }
    }

    @Override
    public void takeDamage(int amount, Entity attacker) {
        // Friendly NPCs can't be killed (or become hostile if attacked)
        // For now, they just take no damage
        System.out.println(name + " says: \"Why would you attack me?!\"");
    }

    /**
     * Interact with this NPC (talk, trade, etc.)
     */
    public String interact() {
        return dialogue;
    }

    public NPCRole getRole() {
        return role;
    }

    public String getDialogue() {
        return dialogue;
    }
}
