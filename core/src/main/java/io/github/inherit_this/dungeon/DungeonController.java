package io.github.inherit_this.dungeon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import io.github.inherit_this.audio.SoundManager;
import io.github.inherit_this.audio.SoundType;
import io.github.inherit_this.combat.CombatManager;
import io.github.inherit_this.entities.Player;
import io.github.inherit_this.ui.DungeonUI;
import io.github.inherit_this.world.*;

/**
 * Manages dungeon entry/exit, portals, and dungeon state.
 * Extracted from GameScreen to reduce complexity.
 */
public class DungeonController {
    private final DungeonManager dungeonManager;
    private final DungeonUI dungeonUI;
    private final Player player;
    private final CombatManager combatManager;

    private WorldProvider townWorld;
    private Portal townDungeonEntrance;
    private Portal dungeonTownReturn;

    // References that change with world
    private WorldProvider currentWorld;

    public DungeonController(Player player, DungeonUI dungeonUI, CombatManager combatManager) {
        this.player = player;
        this.dungeonUI = dungeonUI;
        this.combatManager = combatManager;
        this.dungeonManager = DungeonManager.getInstance();
    }

    /**
     * Initialize with town world and create dungeon entrance portal.
     */
    public void initialize(WorldProvider townWorld, float spawnX, float spawnY) {
        this.townWorld = townWorld;
        this.currentWorld = townWorld;

        // Create dungeon entrance portal in town
        townDungeonEntrance = Portal.createDungeonEntrance(
            spawnX + 200,
            spawnY
        );
    }

    /**
     * Check for and handle portal interactions (press E to enter/exit).
     */
    public void handlePortalInteractions() {
        float playerX = player.getPosition().x;
        float playerY = player.getPosition().y;

        if (!dungeonManager.isInDungeon()) {
            // In town - check for dungeon entrance
            if (townDungeonEntrance.isPlayerNear(playerX, playerY, 64f)) {
                dungeonUI.setNearbyPortal(townDungeonEntrance);
                if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                    enterDungeon(1);
                }
            } else {
                dungeonUI.setNearbyPortal(null);
            }
        } else {
            // In dungeon - check for town return
            if (dungeonTownReturn != null &&
                dungeonTownReturn.isPlayerNear(playerX, playerY, 64f)) {
                dungeonUI.setNearbyPortal(dungeonTownReturn);
                if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                    exitDungeon();
                }
            } else {
                dungeonUI.setNearbyPortal(null);
            }
        }
    }

    /**
     * Enter a dungeon at the specified level.
     */
    private void enterDungeon(int level) {
        DungeonWorld dungeon = dungeonManager.enterDungeon(level, player.getPosition().x, player.getPosition().y);

        // Switch world provider
        currentWorld = dungeon;
        player.setWorld(currentWorld);

        // Move player to dungeon spawn
        int[] spawnPos = dungeon.getSpawnPosition();
        player.setPosition(spawnPos[0], spawnPos[1]);

        // Create return portal at spawn
        dungeonTownReturn = Portal.createTownReturn(spawnPos[0] + 64, spawnPos[1]);

        // Clear existing NPCs and spawn test enemies
        combatManager.clearAll();
        Texture enemyTexture = new Texture("character.png");
        enemyTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        combatManager.spawnTestEnemies(spawnPos, currentWorld, enemyTexture);

        // Preload nearby chunks
        dungeon.preloadChunks(3);

        // Play sound
        SoundManager.getInstance().play(SoundType.UI_CLICK, 1.0f);

        Gdx.app.log("DungeonController", "Entered dungeon level " + level + " with " + combatManager.getNPCCount() + " enemies");
    }

    /**
     * Exit dungeon and return to town.
     */
    private void exitDungeon() {
        float[] townPos = dungeonManager.exitToTown();

        // Switch back to town world
        currentWorld = townWorld;
        player.setWorld(currentWorld);

        // Move player to saved town position
        player.setPosition(townPos[0], townPos[1]);

        // Clear dungeon portal
        dungeonTownReturn = null;

        // Clear all NPCs when leaving dungeon
        combatManager.clearAll();

        // Play sound
        SoundManager.getInstance().play(SoundType.UI_CLICK, 1.0f);

        Gdx.app.log("DungeonController", "Returned to town");
    }

    // Getters
    public WorldProvider getCurrentWorld() {
        return currentWorld;
    }

    public void setCurrentWorld(WorldProvider world) {
        this.currentWorld = world;
    }

    public Portal getTownDungeonEntrance() {
        return townDungeonEntrance;
    }

    public Portal getDungeonTownReturn() {
        return dungeonTownReturn;
    }

    public WorldProvider getTownWorld() {
        return townWorld;
    }

    public DungeonManager getDungeonManager() {
        return dungeonManager;
    }
}
