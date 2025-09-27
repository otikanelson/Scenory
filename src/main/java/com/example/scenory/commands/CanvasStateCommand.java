package com.example.scenory.commands;

import com.example.scenory.view.components.DrawingCanvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;

/**
 * Fixed Canvas State Command for complex operations
 * Captures and restores complete canvas states for complex multi-step operations
 */
public class CanvasStateCommand implements DrawingCommand {

    private final DrawingCanvas canvas;
    private final String description;
    private WritableImage beforeImage;
    private WritableImage afterImage;
    private boolean isExecuted = false;

    public CanvasStateCommand(DrawingCanvas canvas, String description) {
        this.canvas = canvas;
        this.description = description;

        // Capture the current state as "before"
        captureBeforeState();
    }

    /**
     * Capture the current canvas state as the "after" state and execute
     */
    public void captureAfterStateAndExecute() {
        captureAfterState();
        execute();
    }

    @Override
    public void execute() {
        if (afterImage == null) {
            System.err.println("Cannot execute canvas state command: no after state captured");
            return;
        }

        GraphicsContext gc = canvas.getGraphicsContext2D();
        if (gc == null) {
            System.err.println("Graphics context not available for canvas state execution");
            return;
        }

        // Save current graphics state
        gc.save();

        try {
            // Reset transform to ensure proper restoration
            gc.setTransform(1, 0, 0, 1, 0, 0);

            // Clear canvas first
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

            // Apply the "after" state
            gc.drawImage(afterImage, 0, 0);

        } finally {
            // Always restore graphics state
            gc.restore();
        }

        isExecuted = true;
        System.out.println("🔄 Executed canvas state command: " + description);
    }

    @Override
    public void undo() {
        if (beforeImage == null) {
            System.err.println("Cannot undo canvas state command: no before image captured");
            return;
        }

        GraphicsContext gc = canvas.getGraphicsContext2D();
        if (gc == null) {
            System.err.println("Graphics context not available for canvas state undo");
            return;
        }

        // Save current graphics state
        gc.save();

        try {
            // Reset transform to ensure proper restoration
            gc.setTransform(1, 0, 0, 1, 0, 0);

            // Clear canvas first
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

            // Restore the "before" state
            gc.drawImage(beforeImage, 0, 0);

        } finally {
            // Always restore graphics state
            gc.restore();
        }

        isExecuted = false;
        System.out.println("↶ Undid canvas state command: " + description);
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean canMergeWith(DrawingCommand other) {
        // Canvas state commands generally don't merge with other commands
        return false;
    }

    @Override
    public void mergeWith(DrawingCommand other) {
        // Canvas state commands don't merge
    }

    /**
     * Capture the current canvas state as "before"
     */
    private void captureBeforeState() {
        try {
            beforeImage = canvas.snapshot(null, null);
            System.out.println("📸 Captured before state for: " + description);
        } catch (Exception e) {
            System.err.println("Failed to capture before state: " + e.getMessage());
            beforeImage = null;
        }
    }

    /**
     * Capture the current canvas state as "after"
     */
    private void captureAfterState() {
        try {
            afterImage = canvas.snapshot(null, null);
            System.out.println("📸 Captured after state for: " + description);
        } catch (Exception e) {
            System.err.println("Failed to capture after state: " + e.getMessage());
            afterImage = null;
        }
    }

    /**
     * Manually set the after state (for external operations)
     */
    public void setAfterState(WritableImage image) {
        this.afterImage = image;
        System.out.println("📸 Set after state manually for: " + description);
    }

    /**
     * Check if the command is ready to execute
     */
    public boolean isReadyToExecute() {
        return beforeImage != null && afterImage != null;
    }

    /**
     * Check if the command has been executed
     */
    public boolean isExecuted() {
        return isExecuted;
    }

    /**
     * Get information about the captured states
     */
    public String getStateInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Canvas State Command: ").append(description).append("\n");

        if (beforeImage != null) {
            info.append("Before state: ")
                    .append((int)beforeImage.getWidth()).append("x")
                    .append((int)beforeImage.getHeight()).append(" pixels\n");
        } else {
            info.append("Before state: Not captured\n");
        }

        if (afterImage != null) {
            info.append("After state: ")
                    .append((int)afterImage.getWidth()).append("x")
                    .append((int)afterImage.getHeight()).append(" pixels\n");
        } else {
            info.append("After state: Not captured\n");
        }

        info.append("Executed: ").append(isExecuted);

        return info.toString();
    }

    /**
     * Create a canvas state command for complex operations
     * Usage example:
     * 1. Create command before operation
     * 2. Perform complex drawing operations
     * 3. Call captureAfterStateAndExecute()
     */
    public static CanvasStateCommand createForComplexOperation(DrawingCanvas canvas, String description) {
        return new CanvasStateCommand(canvas, description);
    }

    /**
     * Helper method to execute a complex operation with automatic state management
     */
    public static void executeComplexOperation(DrawingCanvas canvas, String description, Runnable operation) {
        CanvasStateCommand command = new CanvasStateCommand(canvas, description);

        try {
            // Perform the operation
            operation.run();

            // Capture the result and execute the command
            command.captureAfterStateAndExecute();

            // Add to command manager if available
            if (canvas.getCommandManager() != null) {
                canvas.getCommandManager().executeCommand(command);
            }

        } catch (Exception e) {
            System.err.println("Error executing complex operation '" + description + "': " + e.getMessage());
            e.printStackTrace();
        }
    }
}