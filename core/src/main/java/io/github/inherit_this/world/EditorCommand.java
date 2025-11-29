package io.github.inherit_this.world;

/**
 * Command pattern interface for map editor actions.
 * Supports undo/redo functionality.
 */
public interface EditorCommand {

    /**
     * Executes the command (places tile/object).
     */
    void execute();

    /**
     * Undoes the command (removes what was placed).
     */
    void undo();

    /**
     * Redoes the command (places again after undo).
     */
    void redo();
}
