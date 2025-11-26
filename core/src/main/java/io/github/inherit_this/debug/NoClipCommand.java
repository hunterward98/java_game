package io.github.inherit_this.debug;

import io.github.inherit_this.entities.Player;

public class NoClipCommand implements DebugCommand {
    private final Player player;

    public NoClipCommand(Player player) {
        this.player = player;
    }

    @Override public String getName() { return "noclip"; }
    @Override public String getDescription() { return "Toggles noclip on the player: noclip true|false"; }

    @Override
    public void execute(String[] args, DebugConsole console) {
        if (args.length != 1) {
            console.log("Usage: noclip true|false");
            return;
        }
        boolean val = Boolean.parseBoolean(args[0]);
        player.setNoClip(val);
        console.log("NoClip set to " + val);
    }
}
