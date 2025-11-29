package io.github.inherit_this.debug;

import io.github.inherit_this.screens.GameScreen;

public class SwitchWorldCommand implements DebugCommand {
    private final GameScreen gameScreen;

    public SwitchWorldCommand(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    @Override
    public String getName() {
        return "switchworld";
    }

    @Override
    public String getDescription() {
        return "Switch between world types (static/procedural)";
    }

    @Override
    public void execute(String[] args, DebugConsole console) {
        if (args.length < 1) {
            console.log("Usage: switchworld <static|procedural> [mapPath]");
            console.log("Examples:");
            console.log("  switchworld static");
            console.log("  switchworld static maps/my_custom_map.json");
            console.log("  switchworld procedural");
            return;
        }

        String worldType = args[0].toLowerCase();
        String mapPath = args.length > 1 ? args[1] : null;

        if (!worldType.equals("static") && !worldType.equals("procedural")) {
            console.log("Invalid world type. Use 'static' or 'procedural'.");
            return;
        }

        try {
            gameScreen.switchWorld(worldType, mapPath);

            if (worldType.equals("static")) {
                String path = (mapPath != null) ? mapPath : "maps/default_map.json";
                console.log("Switched to static world: " + path);
            } else {
                console.log("Switched to procedural world");
            }
        } catch (Exception e) {
            console.log("Error switching world: " + e.getMessage());
        }
    }
}
