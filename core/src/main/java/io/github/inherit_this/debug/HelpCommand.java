package io.github.inherit_this.debug;

import com.badlogic.gdx.utils.ObjectMap;

public class HelpCommand implements DebugCommand {
    private final ObjectMap<String, DebugCommand> registry;

    public HelpCommand(ObjectMap<String, DebugCommand> registry) {
        this.registry = registry;
    }

    @Override public String getName() { return "help"; }
    @Override public String getDescription() { return "Lists all commands. Usage: help [command]"; }

    @Override
    public void execute(String[] args, DebugConsole console) {
        if (args.length == 0) {
            console.log("Available Commands:");
            for (DebugCommand c : registry.values()) {
                console.log(c.getName() + " - " + c.getDescription());
            }
            return;
        }

        String target = args[0];
        DebugCommand c = registry.get(target);
        if (c == null) {
            console.log("Unknown command: " + target);
            return;
        }
        console.log(c.getName() + " - " + c.getDescription());
    }
}
