package io.github.inherit_this.debug;

import io.github.inherit_this.entities.Player;

public class SetGoldCommand implements DebugCommand {
    private final Player player;

    public SetGoldCommand(Player player) {
        this.player = player;
    }

    @Override
    public String getName() {
        return "setcoins";
    }

    @Override
    public String getDescription() {
        return "Set player coins: setcoins <amount>";
    }

    @Override
    public void execute(String[] args, DebugConsole console) {
        if (args.length < 1) {
            console.log("Usage: setcoins <amount>");
            console.log("Current coins: " + player.getStats().getCoins());
            return;
        }

        try {
            int amount = Integer.parseInt(args[0]);
            player.getStats().setCoins(amount);
            console.log("Set coins to " + player.getStats().getCoins());
        } catch (NumberFormatException e) {
            console.log("Invalid amount: " + args[0]);
        }
    }
}
