package com.example.scenory.commands;

/**
 * Command Pattern Interface for Undo/Redo System
 * All drawing operations implement this interface
 */
public interface DrawingCommand {
    /**
     * Execute the command (perform the drawing action)
     */
    void execute();

    /**
     * Undo the command (reverse the drawing action)
     */
    void undo();

    /**
     * Get description of the command for UI display
     */
    String getDescription();

    /**
     * Check if this command can be merged with another command
     * (useful for continuous drawing strokes)
     */
    default boolean canMergeWith(DrawingCommand other) {
        return false;
    }

    /**
     * Merge this command with another command
     */
    default void mergeWith(DrawingCommand other) {
        // Default implementation does nothing
    }
}
