package io.github.inherit_this.debug;

import io.github.inherit_this.debug.DebugCommand;
import io.github.inherit_this.debug.DebugConsole;
import io.github.inherit_this.entities.Player;
import io.github.inherit_this.items.Item;
import io.github.inherit_this.items.ItemRegistry;

public class GiveCommand implements DebugCommand {
    private final Player player;

    public GiveCommand(Player player) {
        this.player = player;
    }

    @Override public String getName() { return "give"; }
    @Override public String getDescription() { return "Give item to player: give <item_id> [amount]"; }

    @Override
    public void execute(String[] args, DebugConsole console) {
        if (args.length < 1) {
            console.log("Usage: give <item_id> [amount]");
            console.log("Example: give iron_sword 1");
            console.log("Available items: iron_sword, health_potion, iron_ore, wood, pickaxe, etc.");
            return;
        }

        String itemId = args[0];
        int amount = 1;

        if (args.length >= 2) {
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                console.log("Invalid amount: " + args[1]);
                return;
            }
        }

        Item item = ItemRegistry.getInstance().getItem(itemId);
        if (item == null) {
            console.log("Unknown item: " + itemId);
            console.log("Use 'give' with no args to see available items.");
            return;
        }

        boolean success = player.getInventory().addItem(item, amount);
        if (success) {
            console.log("Gave " + amount + "x " + item.getName() + " to player.");
        } else {
            console.log("Failed to add item. Inventory might be full.");
        }
    }
}
