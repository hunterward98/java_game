package io.github.inherit_this.debug;

import io.github.inherit_this.entities.Player;
import io.github.inherit_this.util.Constants;
import io.github.inherit_this.world.DungeonConfig;
import io.github.inherit_this.world.DungeonWorld;
import io.github.inherit_this.world.DungeonManager;

/**
 * Debug command to test procedural dungeon generation at different levels and themes.
 * Usage: testdungeon <level> [procedural|room|maze]
 */
public class TestDungeonCommand implements DebugCommand {
    private final Player player;
    private final DungeonManager dungeonManager;
    private final WorldSwitcher worldSwitcher;

    /**
     * Interface for switching the game world (implemented by GameScreen or similar).
     */
    public interface WorldSwitcher {
        void switchToDungeon(DungeonWorld dungeon);
    }

    public TestDungeonCommand(Player player, DungeonManager dungeonManager, WorldSwitcher worldSwitcher) {
        this.player = player;
        this.dungeonManager = dungeonManager;
        this.worldSwitcher = worldSwitcher;
    }

    @Override
    public String getName() {
        return "testdungeon";
    }

    @Override
    public String getDescription() {
        return "Test procedural dungeon generation at specified level";
    }

    @Override
    public void execute(String[] args, DebugConsole console) {
        if (args.length < 1) {
            console.log("Usage: testdungeon <level>");
            console.log("Examples:");
            console.log("  testdungeon 1   - Test level 1 with random theme");
            console.log("  testdungeon 5   - Test level 5 with random theme");
            console.log("  testdungeon 11  - Test level 11 with random theme");
            console.log("");
            console.log("Available themes (randomly selected per dungeon):");
            console.log("  - Ancient Crypt (marble_tile floors, dark_brick walls)");
            console.log("  - Dark Depths (dark_granite floors, dark_brick walls)");
            console.log("");
            console.log("Use 'seed' command to view/change your dungeon seed");
            return;
        }

        try {
            // Parse dungeon level
            int level = Integer.parseInt(args[0]);
            if (level < 1 || level > 100) {
                console.log("Invalid level. Must be between 1 and 100.");
                return;
            }

            // Clear existing dungeons to force regeneration
            dungeonManager.clearDungeons();

            // Create dungeon using player's seed
            long playerSeed = player.getStats().getDungeonSeed();
            long seed = playerSeed + level * 1000;  // Same formula as DungeonManager

            // Use procedural generation (always)
            DungeonConfig config = DungeonConfig.createProcedural(seed, level);

            // Create the dungeon world
            DungeonWorld dungeon = new DungeonWorld(config);

            // Get spawn position and teleport player
            int[] spawnPos = dungeon.getSpawnPosition();

            // Convert pixel coordinates to tile coordinates (Player uses tiles internally)
            int spawnTileX = spawnPos[0] / Constants.TILE_SIZE;
            int spawnTileY = spawnPos[1] / Constants.TILE_SIZE;

            player.setTilePosition(spawnTileX, spawnTileY);
            player.setWorld(dungeon);

            // Switch the game world
            worldSwitcher.switchToDungeon(dungeon);

            // Log success
            String themeName = config.getTheme().getName();
            console.log("Entered level " + level + " dungeon (Theme: " + themeName + ")");

        } catch (NumberFormatException e) {
            console.log("Invalid level number: " + args[0]);
        } catch (Exception e) {
            console.log("Error creating test dungeon: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
