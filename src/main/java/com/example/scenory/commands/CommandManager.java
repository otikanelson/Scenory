package com.example.scenory.commands;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.Stack;

/**
 * Command Manager for Undo/Redo System
 * Manages command execution, undo/redo stacks, and state
 */
public class CommandManager {

    // Command stacks
    private final Stack<DrawingCommand> undoStack = new Stack<>();
    private final Stack<DrawingCommand> redoStack = new Stack<>();

    // Properties for UI binding
    private final BooleanProperty canUndo = new SimpleBooleanProperty(false);
    private final BooleanProperty canRedo = new SimpleBooleanProperty(false);
    private final StringProperty undoDescription = new SimpleStringProperty("Undo");
    private final StringProperty redoDescription = new SimpleStringProperty("Redo");

    // Configuration
    private int maxHistorySize = 50; // Limit memory usage
    private boolean mergeConsecutiveStrokes = true;

    // State tracking
    private DrawingCommand currentStroke = null; // For merging strokes

    /**
     * Execute a command and add it to the undo stack
     */
    public void executeCommand(DrawingCommand command) {
        // Try to merge with current stroke if possible
        if (mergeConsecutiveStrokes && currentStroke != null &&
                currentStroke.canMergeWith(command)) {
            currentStroke.mergeWith(command);
            updateProperties();
            return;
        }

        // Execute the command
        command.execute();

        // Add to undo stack
        undoStack.push(command);

        // Clear redo stack (new command invalidates redo history)
        redoStack.clear();

        // Update current stroke for potential merging
        if (command instanceof StrokeCommand) {
            currentStroke = command;
        } else {
            currentStroke = null;
        }

        // Limit stack size to prevent memory issues
        trimUndoStack();

        // Update UI properties
        updateProperties();

        System.out.println("✅ Executed: " + command.getDescription() +
                " (Undo stack: " + undoStack.size() + ")");
    }

    /**
     * Undo the last command
     */
    public boolean undo() {
        if (undoStack.isEmpty()) {
            return false;
        }

        DrawingCommand command = undoStack.pop();
        command.undo();
        redoStack.push(command);

        // Clear current stroke since we're undoing
        currentStroke = null;

        updateProperties();

        System.out.println("↶ Undid: " + command.getDescription() +
                " (Undo: " + undoStack.size() + ", Redo: " + redoStack.size() + ")");

        return true;
    }

    /**
     * Redo the last undone command
     */
    public boolean redo() {
        if (redoStack.isEmpty()) {
            return false;
        }

        DrawingCommand command = redoStack.pop();
        command.execute();
        undoStack.push(command);

        // Clear current stroke since we're redoing
        currentStroke = null;

        updateProperties();

        System.out.println("↷ Redid: " + command.getDescription() +
                " (Undo: " + undoStack.size() + ", Redo: " + redoStack.size() + ")");

        return true;
    }

    /**
     * Clear all command history
     */
    public void clearHistory() {
        undoStack.clear();
        redoStack.clear();
        currentStroke = null;
        updateProperties();

        System.out.println("🧹 Command history cleared");
    }

    /**
     * Finish the current stroke (prevents further merging)
     */
    public void finishCurrentStroke() {
        if (currentStroke instanceof StrokeCommand) {
            ((StrokeCommand) currentStroke).finishStroke();
        }
        currentStroke = null;
    }

    /**
     * Get the description of the next undo operation
     */
    public String getUndoDescription() {
        if (undoStack.isEmpty()) {
            return "Undo";
        }
        return "Undo " + undoStack.peek().getDescription();
    }

    /**
     * Get the description of the next redo operation
     */
    public String getRedoDescription() {
        if (redoStack.isEmpty()) {
            return "Redo";
        }
        return "Redo " + redoStack.peek().getDescription();
    }

    /**
     * Update UI binding properties
     */
    private void updateProperties() {
        canUndo.set(!undoStack.isEmpty());
        canRedo.set(!redoStack.isEmpty());
        undoDescription.set(getUndoDescription());
        redoDescription.set(getRedoDescription());
    }

    /**
     * Trim undo stack to prevent memory issues
     */
    private void trimUndoStack() {
        while (undoStack.size() > maxHistorySize) {
            undoStack.remove(0); // Remove oldest command
        }
    }

    // =====================================
    // PROPERTIES FOR UI BINDING
    // =====================================

    public BooleanProperty canUndoProperty() {
        return canUndo;
    }

    public BooleanProperty canRedoProperty() {
        return canRedo;
    }

    public StringProperty undoDescriptionProperty() {
        return undoDescription;
    }

    public StringProperty redoDescriptionProperty() {
        return redoDescription;
    }

    // =====================================
    // CONFIGURATION METHODS
    // =====================================

    public void setMaxHistorySize(int size) {
        this.maxHistorySize = Math.max(1, size);
        trimUndoStack();
    }

    public int getMaxHistorySize() {
        return maxHistorySize;
    }

    public void setMergeConsecutiveStrokes(boolean merge) {
        this.mergeConsecutiveStrokes = merge;
    }

    public boolean isMergeConsecutiveStrokes() {
        return mergeConsecutiveStrokes;
    }

    // =====================================
    // STATUS METHODS
    // =====================================

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    public int getUndoStackSize() {
        return undoStack.size();
    }

    public int getRedoStackSize() {
        return redoStack.size();
    }

    public void printStatus() {
        System.out.println("📊 Command Manager Status:");
        System.out.println("  Undo Stack: " + undoStack.size() + " commands");
        System.out.println("  Redo Stack: " + redoStack.size() + " commands");
        System.out.println("  Can Undo: " + canUndo());
        System.out.println("  Can Redo: " + canRedo());
        System.out.println("  Max History: " + maxHistorySize);
        System.out.println("  Merge Strokes: " + mergeConsecutiveStrokes);
    }
}