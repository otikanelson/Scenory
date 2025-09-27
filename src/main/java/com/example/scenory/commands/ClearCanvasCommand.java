package com.example.scenory.commands;

import com.example.scenory.view.components.DrawingCanvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;

/**
 * Fixed Clear Canvas Command with proper state management
 * Handles clearing the entire canvas with undo support
 */
public class ClearCanvasCommand implements DrawingCommand {

    private final DrawingCanvas canvas;
    private WritableImage beforeImage;

    public ClearCanvasCommand(DrawingCanvas canvas) {
        this.canvas = canvas;

        // Capture canvas state before clearing
        captureBeforeState();
    }

    @Override
    public void execute() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        if (gc == null) {
            System.err.println("Graphics context not available for clear canvas");
            return;
        }

        // Save current graphics state
        gc.save();

        try {
            // Reset transform to ensure we clear the entire canvas
            gc.setTransform(1, 0, 0, 1, 0, 0);

            // Clear the entire canvas
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

            // Fill with background color
            gc.setFill(canvas.getBackgroundColor());
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        } finally {
            // Always restore graphics state
            gc.restore();
        }

        System.out.println("🧹 Canvas cleared and filled with background color");
    }

    @Override
    public void undo() {
        if (beforeImage == null) {
            System.err.println("Cannot undo clear canvas: no before image captured");
            return;
        }

        GraphicsContext gc = canvas.getGraphicsContext2D();
        if (gc == null) {
            System.err.println("Graphics context not available for clear canvas undo");
            return;
        }

        // Save current graphics state
        gc.save();

        try {
            // Reset transform to ensure proper restoration
            gc.setTransform(1, 0, 0, 1, 0, 0);

            // Clear canvas first
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

            // Restore previous state
            gc.drawImage(beforeImage, 0, 0);

        } finally {
            // Always restore graphics state
            gc.restore();
        }

        System.out.println("↶ Undid canvas clear - restored previous content");
    }

    @Override
    public String getDescription() {
        return "Clear Canvas";
    }

    @Override
    public boolean canMergeWith(DrawingCommand other) {
        // Clear canvas commands should not merge with other commands
        return false;
    }

    @Override
    public void mergeWith(DrawingCommand other) {
        // Clear canvas commands don't merge
    }

    /**
     * Capture the canvas state before clearing
     */
    private void captureBeforeState() {
        try {
            beforeImage = canvas.snapshot(null, null);
            System.out.println("📸 Captured canvas state before clear (" +
                    beforeImage.getWidth() + "x" + beforeImage.getHeight() + ")");
        } catch (Exception e) {
            System.err.println("Failed to capture canvas state before clear: " + e.getMessage());
            beforeImage = null;
        }
    }

    /**
     * Check if the undo operation is available
     */
    public boolean canUndo() {
        return beforeImage != null;
    }

    /**
     * Get information about the captured state
     */
    public String getBeforeStateInfo() {
        if (beforeImage == null) {
            return "No state captured";
        }
        return String.format("Captured state: %.0fx%.0f pixels",
                beforeImage.getWidth(), beforeImage.getHeight());
    }
}