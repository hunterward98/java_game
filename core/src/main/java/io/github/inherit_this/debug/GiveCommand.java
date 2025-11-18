package io.github.inherit_this.debug;

import io.github.inherit_this.debug.DebugCommand;
import io.github.inherit_this.debug.DebugConsole;
import io.github.inherit_this.entities.Player;

public class GiveCommand implements DebugCommand {
    private final Player player;

    public GiveCommand(Player player) {
        this.player = player;
    }

    @Override public String getName() { return "give"; }
    @Override public String getDescription() { return "Give item to player: give itemName amount"; }

    @Override
    public void execute(String[] args, DebugConsole console) {
        new Exception("Items are not implemented.");
        // if (args.length < 2) {
        //     console.log("Usage: give itemName amount");
        //     return;
        // }
        // String itemName = args[0];
        // int amount = 1;
        // try {
        //     amount = Integer.parseInt(args[1]);
        // } catch (NumberFormatException ignored) {}

        // player.getInventory().add(itemName, amount);
        // console.log("Gave " + amount + "x " + itemName + " to player.");
    }
}
