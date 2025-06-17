package com.example.scenory.commands;

import com.example.scenory.view.components.DrawingCanvas;
import javafx.scene.image.WritableImage;

/**
 * Command that captures the entire canvas state
 * Used for complex drawing operations
 */
public class CanvasStateCommand implements DrawingCommand {
    private final DrawingCanvas canvas;
    private final WritableImage beforeState;
    private final WritableImage afterState;
    private final String description;

    public CanvasStateCommand(DrawingCanvas canvas, String description) {
        this.canvas = canvas;
        this.description = description;
        this.beforeState = canvas.snapshot(null, null);
        this.afterState = null; // Will be set when command is executed
    }

    // Constructor for when we already have both states
    public CanvasStateCommand(DrawingCanvas canvas, WritableImage beforeState,
                              WritableImage afterState, String description) {
        this.canvas = canvas;
        this.beforeState = beforeState;
        this.afterState = afterState;
        this.description = description;
    }

    @Override
    public void execute() {
        if (afterState != null) {
            restoreCanvasState(afterState);
        }
    }

    @Override
    public void undo() {
        if (beforeState != null) {
            restoreCanvasState(beforeState);
        }
    }

    private void restoreCanvasState(WritableImage state) {
        if (state != null && canvas != null) {
            canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            canvas.getGraphicsContext2D().drawImage(state, 0, 0);
        }
    }

    public void captureAfterState() {
        if (afterState == null) {
            // Capture the state after the operation
            WritableImage newAfterState = canvas.snapshot(null, null);
            // Create new command with both states
            // (We can't modify final fields, so this is handled externally)
        }
    }

    @Override
    public String getDescription() {
        return description;
    }
}