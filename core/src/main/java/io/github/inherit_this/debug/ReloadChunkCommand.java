package io.github.inherit_this.debug;

import io.github.inherit_this.world.*;
import io.github.inherit_this.entities.*;

public class ReloadChunkCommand implements DebugCommand {
    private final WorldProvider world;
    private final Player player;

    public ReloadChunkCommand(WorldProvider world, Player player) {
        this.world = world;
        this.player = player;
    }

    @Override
    public String getName() {
        return "reload_chunk";
    }

    @Override
    public String getDescription() {
        return "Reloads the current chunk (only works for ProceduralWorld)";
    }

    @Override
    public void execute(String[] args, DebugConsole console) {
        if (!(world instanceof ProceduralWorld)) {
            console.log("This command only works with ProceduralWorld (dungeons), not static worlds (town).");
            return;
        }

        Chunk c = world.getActiveChunk(0,0);
        if (c == null) {
            console.log("No active chunk to reload.");
            return;
        }
        ((ProceduralWorld) world).reloadChunk(c.getChunkX(), c.getChunkY());
        console.log("Reloaded chunk (" + c.getChunkX() + ", " + c.getChunkY() + ").");
    }
}
