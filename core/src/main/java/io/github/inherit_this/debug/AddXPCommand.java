package io.github.inherit_this.debug;

import io.github.inherit_this.entities.Player;

public class AddXPCommand implements DebugCommand {
    private final Player player;

    public AddXPCommand(Player player) {
        this.player = player;
    }

    @Override
    public String getName() {
        return "addxp";
    }

    @Override
    public String getDescription() {
        return "Add XP to player: addxp <amount>";
    }

    @Override
    public void execute(String[] args, DebugConsole console) {
        if (args.length < 1) {
            console.log("Usage: addxp <amount>");
            console.log("Example: addxp 100");
            return;
        }

        try {
            float amount = Float.parseFloat(args[0]);
            int levelsGained = player.getStats().addXP(amount);

            if (levelsGained > 0) {
                console.log("Added " + amount + " XP. Leveled up " + levelsGained + " time(s)!");
                console.log("Now level " + player.getStats().getLevel());
            } else {
                console.log("Added " + amount + " XP.");
            }

            console.log("Current XP: " + (int)player.getStats().getCurrentXP() +
                       "/" + (int)player.getStats().getXPRequiredForNextLevel());
        } catch (NumberFormatException e) {
            console.log("Invalid amount: " + args[0]);
        }
    }
}
