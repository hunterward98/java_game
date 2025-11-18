package io.github.inherit_this.debug;

public interface DebugCommand {
    String getName();
    String getDescription();
    void execute(String[] args, DebugConsole console);
}
