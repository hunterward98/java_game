package io.github.inherit_this.debug;

import io.github.inherit_this.debug.DebugCommand;
import io.github.inherit_this.debug.DebugConsole;
import io.github.inherit_this.entities.Player;

public class TeleportCommand implements DebugCommand {
    private final Player player;

    public TeleportCommand(Player player) {
        this.player = player;
    }

    @Override public String getName() { return "tp"; }
    @Override public String getDescription() { return "Teleport self to tile coords (x, y)"; }

    @Override
    public void execute(String[] args, DebugConsole console) {
        if (args.length != 2) {
            console.log("Usage: tp x y");
            return;
        }
        try {
            int tx = Integer.parseInt(args[0]);
            int ty = Integer.parseInt(args[1]);

            player.setTilePosition(tx, ty);
            console.log("Teleported to tile position (" + tx + ", " + ty + ")");
        } catch (Exception e) {
            console.log("Invalid coordinates. Not integer or out of bounds.");
        }
    }
}
