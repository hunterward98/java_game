package io.github.inherit_this.debug;

import io.github.inherit_this.entities.Player;

public class SetHealthCommand implements DebugCommand {
    private final Player player;

    public SetHealthCommand(Player player) {
        this.player = player;
    }

    @Override
    public String getName() {
        return "sethealth";
    }

    @Override
    public String getDescription() {
        return "Set player health: sethealth <amount>";
    }

    @Override
    public void execute(String[] args, DebugConsole console) {
        if (args.length < 1) {
            console.log("Usage: sethealth <amount>");
            console.log("Current health: " + (int)player.getStats().getCurrentHealth() +
                       "/" + (int)player.getStats().getMaxHealth());
            return;
        }

        try {
            float amount = Float.parseFloat(args[0]);
            player.getStats().setHealth(amount);
            console.log("Set health to " + (int)player.getStats().getCurrentHealth() +
                       "/" + (int)player.getStats().getMaxHealth());
        } catch (NumberFormatException e) {
            console.log("Invalid amount: " + args[0]);
        }
    }
}
