package io.github.inherit_this.world;

/**
 * Represents a portal that allows teleportation between town and dungeons.
 * - Town portal: Enters dungeon level 1
 * - Dungeon portal: Returns to town
 */
public class Portal {

    public enum PortalType {
        DUNGEON_ENTRANCE,  // In town, leads to dungeon level 1
        TOWN_RETURN        // In dungeon, returns to town
    }

    private final PortalType type;
    private final float worldX;
    private final float worldY;
    private final int targetLevel;  // For DUNGEON_ENTRANCE, which level to enter

    /**
     * Create a dungeon entrance portal (in town).
     */
    public static Portal createDungeonEntrance(float worldX, float worldY) {
        return new Portal(PortalType.DUNGEON_ENTRANCE, worldX, worldY, 1);
    }

    /**
     * Create a town return portal (in dungeon).
     */
    public static Portal createTownReturn(float worldX, float worldY) {
        return new Portal(PortalType.TOWN_RETURN, worldX, worldY, 0);
    }

    private Portal(PortalType type, float worldX, float worldY, int targetLevel) {
        this.type = type;
        this.worldX = worldX;
        this.worldY = worldY;
        this.targetLevel = targetLevel;
    }

    /**
     * Check if the player is near enough to interact with this portal.
     */
    public boolean isPlayerNear(float playerX, float playerY, float interactionRange) {
        float dx = playerX - worldX;
        float dy = playerY - worldY;
        float distanceSquared = dx * dx + dy * dy;
        return distanceSquared <= interactionRange * interactionRange;
    }

    public PortalType getType() {
        return type;
    }

    public float getWorldX() {
        return worldX;
    }

    public float getWorldY() {
        return worldY;
    }

    public int getTargetLevel() {
        return targetLevel;
    }

    /**
     * Get display text for interaction prompt.
     */
    public String getInteractionText() {
        switch (type) {
            case DUNGEON_ENTRANCE:
                return "Press E to enter dungeon";
            case TOWN_RETURN:
                return "Press E to return to town";
            default:
                return "Press E to use portal";
        }
    }
}
