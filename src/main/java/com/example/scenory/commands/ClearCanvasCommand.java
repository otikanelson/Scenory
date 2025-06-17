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
            canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            canvas.getGraphicsContext2D().drawImage(beforeState, 0, 0);
        }
    }

    @Override
    public String getDescription() {
        return "Clear Canvas";
    }
}