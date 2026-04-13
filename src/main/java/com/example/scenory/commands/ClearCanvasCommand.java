package com.example.scenory.commands;

import com.example.scenory.view.components.DrawingCanvas;
import javafx.scene.image.WritableImage;

/**
 * Command for clearing the entire canvas
 */
public class ClearCanvasCommand implements DrawingCommand {
    private final DrawingCanvas canvas;
    private final WritableImage beforeState;

    public ClearCanvasCommand(DrawingCanvas canvas) {
        this.canvas = canvas;
        this.beforeState = canvas.snapshot(null, null);
    }

    @Override
    public void execute() {
        canvas.clearCanvas();
    }

    @Override
    public void undo() {
        if (beforeState != null) {
            var gc = canvas.getGraphicsContext2D();
            gc.setFill(canvas.getBackgroundColor());
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.drawImage(beforeState, 0, 0);
        }
    }

    @Override
    public String getDescription() {
        return "Clear Canvas";
    }
}