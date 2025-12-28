package io.github.inherit_this.debug;

import io.github.inherit_this.entities.Player;
import io.github.inherit_this.world.DungeonManager;

/**
 * Debug command to view or set the player's dungeon seed.
 * The seed determines dungeon generation - same seed = same dungeons.
 */
public class SeedCommand implements DebugCommand {
    private final Player player;
    private final DungeonManager dungeonManager;

    public SeedCommand(Player player, DungeonManager dungeonManager) {
        this.player = player;
        this.dungeonManager = dungeonManager;
    }

    @Override
    public String getName() {
        return "seed";
    }

    @Override
    public String getDescription() {
        return "View or set the dungeon generation seed";
    }

    @Override
    public void execute(String[] args, DebugConsole console) {
        if (args.length == 0) {
            // Display current seed
            long currentSeed = player.getStats().getDungeonSeed();
            console.log("Current dungeon seed: " + currentSeed);
            console.log("This seed determines all dungeon layouts for this character.");
            console.log("Usage: seed <new_seed>  - Set a new seed (clears cached dungeons)");
            console.log("       seed random      - Generate a random seed");
            return;
        }

        try {
            long newSeed;

            if (args[0].equalsIgnoreCase("random") || args[0].equalsIgnoreCase("rand")) {
                // Generate random seed
                newSeed = System.nanoTime();
                console.log("Generated random seed: " + newSeed);
            } else {
                // Parse seed from argument
                newSeed = Long.parseLong(args[0]);
            }

            // Update player's seed
            player.getStats().setDungeonSeed(newSeed);

            // Update dungeon manager and clear cached dungeons
            dungeonManager.setPlayerSeed(newSeed);

            console.log("Dungeon seed updated to: " + newSeed);
            console.log("All cached dungeons have been cleared.");
            console.log("New dungeons will generate with this seed.");

        } catch (NumberFormatException e) {
            console.log("Invalid seed. Must be a number or 'random'.");
            console.log("Example: seed 123456789");
        } catch (Exception e) {
            console.log("Error setting seed: " + e.getMessage());
        }
    }
}
