package io.github.inherit_this.debug;

import io.github.inherit_this.world.WorldProvider;
import io.github.inherit_this.world.ProceduralWorld;

public class RegenWorldCommand implements DebugCommand {
    private final WorldProvider world;

    public RegenWorldCommand(WorldProvider world) {
        this.world = world;
    }

    @Override public String getName() { return "regen_world"; }
    @Override public String getDescription() { return "Regenerates procedural world (only works for ProceduralWorld)"; }

    @Override
    public void execute(String[] args, DebugConsole console) {
        if (world instanceof ProceduralWorld) {
            ((ProceduralWorld) world).regenerateAll();
            console.log("World regeneration requested.");
        } else {
            console.log("This command only works with ProceduralWorld (dungeons), not static worlds (town).");
        }
    }
}
