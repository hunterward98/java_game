package io.github.inherit_this.debug;

import io.github.inherit_this.entities.Player;

public class SetGoldCommand implements DebugCommand {
    private final Player player;

    public SetGoldCommand(Player player) {
        this.player = player;
    }

    @Override
    public String getName() {
        return "setgold";
    }

    @Override
    public String getDescription() {
        return "Set player gold: setgold <amount>";
    }

    @Override
    public void execute(String[] args, DebugConsole console) {
        if (args.length < 1) {
            console.log("Usage: setgold <amount>");
            console.log("Current gold: " + player.getStats().getGold());
            return;
        }

        try {
            int amount = Integer.parseInt(args[0]);
            player.getStats().setGold(amount);
            console.log("Set gold to " + player.getStats().getGold());
        } catch (NumberFormatException e) {
            console.log("Invalid amount: " + args[0]);
        }
    }
}
