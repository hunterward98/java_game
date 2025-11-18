package io.github.inherit_this.debug;

import io.github.inherit_this.world.World;

public class RegenWorldCommand implements DebugCommand {
    private final World world;

    public RegenWorldCommand(World world) {
        this.world = world;
    }

    @Override public String getName() { return "regen_world"; }
    @Override public String getDescription() { return "For testing only. Not recommended for use."; }

    @Override
    public void execute(String[] args, DebugConsole console) {
        world.regenerateAll();
        console.log("World regeneration requested.");
    }
}
