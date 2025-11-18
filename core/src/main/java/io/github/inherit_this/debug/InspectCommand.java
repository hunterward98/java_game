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
        // TODO: implement when other entities can spawn or be interacted with
        // if (args.length != 2) {
        //     console.log("Usage: inspect x y");
        //     return;
        // }
        // try {
        //     int x = Integer.parseInt(args[0]);
        //     int y = Integer.parseInt(args[1]);

        //     Tile tile = world.getTile(x, y);
        //     WorldEntity ent = world.getEntity(x, y);

        //     if (tile == null) {
        //         console.log("No tile at " + x + "," + y);
        //         return;
        //     }

        //     console.log("-- Inspection at (" + x + "," + y + ")");
        //     console.log("Tile: type=" + tile.getType() + ", orient=" + tile.getOrientation() + ", solid=" + tile.isSolid());
        //     if (ent != null) {
        //         console.log("Entity: " + ent.getClass().getSimpleName());
        //     } else {
        //         console.log("Entity: none");
        //     }
        // } catch (NumberFormatException e) {
        //     console.log("Invalid coordinates. Use integers.");
        // }
    }
}
