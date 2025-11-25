package io.github.inherit_this.debug;

import io.github.inherit_this.entities.Player;

public class ClearInventoryCommand implements DebugCommand {
    private final Player player;

    public ClearInventoryCommand(Player player) {
        this.player = player;
    }

    @Override
    public String getName() {
        return "clearinv";
    }

    @Override
    public String getDescription() {
        return "Clear all items from inventory";
    }

    @Override
    public void execute(String[] args, DebugConsole console) {
        player.getInventory().clear();
        console.log("Inventory cleared");
    }
}
