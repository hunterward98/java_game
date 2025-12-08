package io.github.inherit_this.entities;

/**
 * Types of NPCs and their behavior toward the player.
 */
public enum NPCType {
    HOSTILE,   // Attacks player on sight
    FRIENDLY,  // Can be talked to, provides quests/shops
    NEUTRAL,   // Ignores player unless attacked
    PASSIVE    // Flees from player and combat
}
