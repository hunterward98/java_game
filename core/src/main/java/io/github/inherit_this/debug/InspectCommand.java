package io.github.inherit_this.debug;

import io.github.inherit_this.debug.DebugCommand;
import io.github.inherit_this.debug.DebugConsole;
import io.github.inherit_this.world.World;
import io.github.inherit_this.world.Tile;
import io.github.inherit_this.entities.*;

public class InspectCommand implements DebugCommand {
    private final World world;

    public InspectCommand(World world) {
        this.world = world;
    }

    @Override public String getName() { return "inspect"; }
    @Override public String getDescription() { return "Shows tile & entity info at location: inspect x y"; }

    @Override
    public void execute(String[] args, DebugConsole console) {
        if (args.length != 2) {
            console.log("Usage: inspect x y");
            return;
        }
        try {
            int x = Integer.parseInt(args[0]);
            int y = Integer.parseInt(args[1]);

            Tile tile = world.getTileAtWorldCoords(x, y);

            if (tile == null) {
                console.log("No tile at world coords (" + x + "," + y + ")");
                return;
            }

            console.log("-- Inspection at world coords (" + x + "," + y + ")");
            console.log("Tile: type=" + tile.getType() + ", solid=" + tile.isSolid());
            // TODO: Add entity inspection when entities system is implemented
            console.log("Entity: none (not yet implemented)");
        } catch (NumberFormatException e) {
            console.log("Invalid coordinates. Use integers.");
        }
    }
}
