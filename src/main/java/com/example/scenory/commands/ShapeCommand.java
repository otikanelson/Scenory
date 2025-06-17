package com.example.scenory.commands;

import com.example.scenory.view.components.DrawingCanvas;
import javafx.scene.paint.Color;
import javafx.scene.image.WritableImage;

/**
 * Command for drawing shapes (rectangles, circles, lines)
 */
public class ShapeCommand implements DrawingCommand {
    private final DrawingCanvas canvas;
    private final WritableImage beforeState;
    private final ShapeType shapeType;
    private final double startX, startY, endX, endY;
    private final Color color;
    private final double strokeWidth;
    private final boolean filled;

    public enum ShapeType {
        RECTANGLE, CIRCLE, LINE
    }

    public ShapeCommand(DrawingCanvas canvas, ShapeType shapeType,
                        double startX, double startY, double endX, double endY,
                        Color color, double strokeWidth, boolean filled) {
        this.canvas = canvas;
        this.shapeType = shapeType;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.filled = filled;
        this.beforeState = canvas.snapshot(null, null);
    }

    @Override
    public void execute() {
        var gc = canvas.getGraphicsContext2D();
        gc.setStroke(color);
        gc.setFill(color);
        gc.setLineWidth(strokeWidth);

        switch (shapeType) {
            case RECTANGLE:
                double width = Math.abs(endX - startX);
                double height = Math.abs(endY - startY);
                double x = Math.min(startX, endX);
                double y = Math.min(startY, endY);

                if (filled) {
                    gc.fillRect(x, y, width, height);
                } else {
                    gc.strokeRect(x, y, width, height);
                }
                break;

            case CIRCLE:
                double centerX = (startX + endX) / 2;
                double centerY = (startY + endY) / 2;
                double radius = Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2)) / 2;
                double diameter = radius * 2;
                double circleX = centerX - radius;
                double circleY = centerY - radius;

                if (filled) {
                    gc.fillOval(circleX, circleY, diameter, diameter);
                } else {
                    gc.strokeOval(circleX, circleY, diameter, diameter);
                }
                break;

            case LINE:
                gc.strokeLine(startX, startY, endX, endY);
                break;
        }
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
        return "Draw " + shapeType.toString().toLowerCase();
    }
}