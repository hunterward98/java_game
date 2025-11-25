package io.github.inherit_this.debug;

import io.github.inherit_this.entities.Player;

public class SetLevelCommand implements DebugCommand {
    private final Player player;

    public SetLevelCommand(Player player) {
        this.player = player;
    }

    @Override
    public String getName() {
        return "setlevel";
    }

    @Override
    public String getDescription() {
        return "Set player level: setlevel <level>";
    }

    @Override
    public void execute(String[] args, DebugConsole console) {
        if (args.length < 1) {
            console.log("Usage: setlevel <level>");
            console.log("Current level: " + player.getStats().getLevel());
            return;
        }

        try {
            int level = Integer.parseInt(args[0]);
            if (level < 1 || level > 100) {
                console.log("Level must be between 1 and 100");
                return;
            }

            player.getStats().setLevel(level);
            console.log("Set level to " + player.getStats().getLevel());
            console.log("HP: " + (int)player.getStats().getCurrentHealth() +
                       "/" + (int)player.getStats().getMaxHealth());
        } catch (NumberFormatException e) {
            console.log("Invalid level: " + args[0]);
        }
    }
}
