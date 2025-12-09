package io.github.inherit_this.debug;

import io.github.inherit_this.entities.*;
import io.github.inherit_this.screens.GameScreen;

/**
 * Debug command to spawn breakable or interactable objects near the player.
 * Usage: spawn <type> [distance]
 * Types: crate, pot, barrel, chest, workbench, anvil, shrine
 */
public class SpawnObjectCommand implements DebugCommand {
    private final Player player;
    private final GameScreen gameScreen;

    public SpawnObjectCommand(Player player, GameScreen gameScreen) {
        this.player = player;
        this.gameScreen = gameScreen;
    }

    @Override
    public String getName() {
        return "spawn";
    }

    @Override
    public String getDescription() {
        return "Spawn an object near player: spawn <type> [distance] [dungeonLevel]";
    }

    @Override
    public void execute(String[] args, DebugConsole console) {
        if (args.length < 1) {
            console.log("Usage: spawn <type> [distance] [dungeonLevel]");
            console.log("Breakable types: crate, pot, barrel");
            console.log("Interactable types: chest, workbench, anvil, shrine");
            console.log("Example: spawn crate 2 (2 tiles away, current dungeon level)");
            console.log("Example: spawn crate 2 50 (2 tiles away, as if dungeon level 50)");
            return;
        }

        String type = args[0].toLowerCase();
        int distance = 2; // Default distance in tiles
        Integer dungeonLevelOverride = null; // null means use current dungeon level

        if (args.length >= 2) {
            try {
                distance = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                console.log("Invalid distance: " + args[1]);
                return;
            }
        }

        if (args.length >= 3) {
            try {
                dungeonLevelOverride = Integer.parseInt(args[2]);
                console.log("Using dungeon level override: " + dungeonLevelOverride);
            } catch (NumberFormatException e) {
                console.log("Invalid dungeon level: " + args[2]);
                return;
            }
        }

        // Calculate spawn position (in front of player)
        float playerX = player.getPosition().x;
        float playerY = player.getPosition().y;
        float spawnX = playerX + distance;
        float spawnY = playerY;

        // Get player level and dungeon level for scaled loot
        int playerLevel = player.getStats().getLevel();
        int dungeonLevel = dungeonLevelOverride != null ? dungeonLevelOverride : gameScreen.getCurrentDungeonLevel();

        // Spawn the object
        switch (type) {
            case "crate":
                gameScreen.addBreakableObject(
                    BreakableObjectFactory.createCrate(spawnX, spawnY, playerLevel, dungeonLevel)
                );
                console.log("Spawned crate at (" + spawnX + ", " + spawnY + ")");
                break;

            case "pot":
                gameScreen.addBreakableObject(
                    BreakableObjectFactory.createPot(spawnX, spawnY, playerLevel, dungeonLevel)
                );
                console.log("Spawned pot at (" + spawnX + ", " + spawnY + ")");
                break;

            case "barrel":
                gameScreen.addBreakableObject(
                    BreakableObjectFactory.createBarrel(spawnX, spawnY, playerLevel, dungeonLevel)
                );
                console.log("Spawned barrel at (" + spawnX + ", " + spawnY + ")");
                break;

            case "chest":
                gameScreen.addInteractableObject(
                    InteractableObjectFactory.createChest(spawnX, spawnY)
                );
                console.log("Spawned chest at (" + spawnX + ", " + spawnY + ")");
                break;

            case "workbench":
                gameScreen.addInteractableObject(
                    InteractableObjectFactory.createWorkbench(spawnX, spawnY)
                );
                console.log("Spawned workbench at (" + spawnX + ", " + spawnY + ")");
                break;

            case "anvil":
                gameScreen.addInteractableObject(
                    InteractableObjectFactory.createAnvil(spawnX, spawnY)
                );
                console.log("Spawned anvil at (" + spawnX + ", " + spawnY + ")");
                break;

            case "shrine":
                gameScreen.addInteractableObject(
                    InteractableObjectFactory.createShrine(spawnX, spawnY)
                );
                console.log("Spawned shrine at (" + spawnX + ", " + spawnY + ")");
                break;

            default:
                console.log("Unknown object type: " + type);
                console.log("Available types: crate, pot, barrel, chest, workbench, anvil, shrine");
                break;
        }
    }
}
