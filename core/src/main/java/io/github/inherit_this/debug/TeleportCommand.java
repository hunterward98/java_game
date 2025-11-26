package io.github.inherit_this.debug;

import io.github.inherit_this.entities.Player;

public class TeleportCommand implements DebugCommand {
    private final Player player;

    public TeleportCommand(Player player) {
        this.player = player;
    }

    @Override public String getName() { return "tp"; }
    @Override public String getDescription() { return "Teleport self to tile coords (x, y, [z])"; }

    @Override
    public void execute(String[] args, DebugConsole console) {
        if (args.length < 2 || args.length > 3) {
            console.log("Usage: tp x y [z]");
            return;
        }
        try {
            int tx = Integer.parseInt(args[0]);
            int ty = Integer.parseInt(args[1]);

            player.setTilePosition(tx, ty);

            // Optional Z parameter for debugging player height
            if (args.length == 3) {
                float z = Float.parseFloat(args[2]);
                player.setBillboardZ(z);
                console.log("Teleported to tile position (" + tx + ", " + ty + ") with Z=" + z);
            } else {
                console.log("Teleported to tile position (" + tx + ", " + ty + ")");
            }
        } catch (Exception e) {
            console.log("Invalid coordinates. Not integer or out of bounds.");
        }
    }
}
