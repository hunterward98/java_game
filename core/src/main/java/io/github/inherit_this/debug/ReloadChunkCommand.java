package io.github.inherit_this.debug;

import io.github.inherit_this.world.*;
import io.github.inherit_this.entities.*;

public class ReloadChunkCommand implements DebugCommand {
    private final World world;
    private final Player player;

    public ReloadChunkCommand(World world, Player player) {
        this.world = world;
        this.player = player;
    }

    @Override
    public String getName() {
        return "reload_chunk";
    }

    @Override
    public String getDescription() {
        return "Reloads the current chunk";
    }

    @Override
    public void execute(String[] args, DebugConsole console) {
        Chunk c = world.getActiveChunk(0,0);
        if (c == null) {
            console.log("No active chunk to reload.");
            return;
        }
        world.reloadChunk(c.getChunkX(), c.getChunkY());
        console.log("Reloaded chunk (" + c.getChunkX() + ", " + c.getChunkY() + ").");
    }
}
