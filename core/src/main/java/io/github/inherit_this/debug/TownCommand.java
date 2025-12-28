package io.github.inherit_this.debug;

import io.github.inherit_this.entities.Player;
import io.github.inherit_this.screens.GameScreen;
import io.github.inherit_this.world.DungeonManager;

/**
 * Debug command to teleport player back to town.
 * Exits any active dungeon and returns to the default town map.
 */
public class TownCommand implements DebugCommand {
    private final GameScreen gameScreen;
    private final Player player;

    public TownCommand(GameScreen gameScreen, Player player) {
        this.gameScreen = gameScreen;
        this.player = player;
    }

    @Override
    public String getName() {
        return "town";
    }

    @Override
    public String getDescription() {
        return "Teleport back to town";
    }

    @Override
    public void execute(String[] args, DebugConsole console) {
        try {
            DungeonManager dungeonManager = DungeonManager.getInstance();

            // Get town return position (or use default if not set)
            float[] townPos = dungeonManager.exitToTown();

            // Switch to default town map
            gameScreen.switchWorld("static", "maps/default_map.json");

            // Teleport player to town position
            if (townPos[0] != 0 || townPos[1] != 0) {
                player.setPosition(townPos[0], townPos[1]);
                console.log("Returned to town at position (" + (int)townPos[0] + ", " + (int)townPos[1] + ")");
            } else {
                // Default spawn position if no town position was saved
                console.log("Returned to town (default spawn)");
            }

        } catch (Exception e) {
            console.log("Error returning to town: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
