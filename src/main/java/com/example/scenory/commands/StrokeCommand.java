package com.example.scenory.commands;

import com.example.scenory.view.components.DrawingCanvas;
import javafx.scene.paint.Color;
import javafx.scene.image.WritableImage;
import com.example.scenory.enums.DrawingTool;
import java.util.ArrayList;
import java.util.List;

/**
 * Command for drawing strokes (can be merged for continuous drawing)
 */
public class StrokeCommand implements DrawingCommand {
    private final DrawingCanvas canvas;
    private final WritableImage beforeState;
    private WritableImage afterState;
    private final List<StrokePoint> strokePoints;
    private final DrawingTool tool;
    private final Color color;
    private final double strokeWidth;
    private boolean executed = false;
    private boolean justFinished = false; // Track if stroke was just drawn live

    public StrokeCommand(DrawingCanvas canvas, DrawingTool tool, Color color, double strokeWidth) {
        this.canvas = canvas;
        this.tool = tool;
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.strokePoints = new ArrayList<>();
        this.beforeState = canvas.snapshot(null, null);
    }

    public void addPoint(double x, double y) {
        strokePoints.add(new StrokePoint(x, y));
    }

    public void finishStroke() {
        if (!executed) {
            this.afterState = canvas.snapshot(null, null);
            executed = true;
            justFinished = true; // Mark that this stroke was just drawn live
        }
    }

    @Override
    public void execute() {
        // Don't re-execute if the stroke was just drawn live
        // Only execute for redo operations
        if (afterState != null && !justFinished) {
            // Restore the after-state
            var gc = canvas.getGraphicsContext2D();
            gc.setFill(canvas.getBackgroundColor());
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.drawImage(afterState, 0, 0);
        } else if (afterState == null) {
            // Re-draw the stroke (shouldn't happen in normal flow)
            redrawStroke();
        }
        // Mark that we're past the initial execution
        justFinished = false;
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

    private void redrawStroke() {
        if (strokePoints.isEmpty()) return;

        var gc = canvas.getGraphicsContext2D();
        gc.setStroke(color);
        gc.setLineWidth(strokeWidth);

        gc.beginPath();
        StrokePoint firstPoint = strokePoints.get(0);
        gc.moveTo(firstPoint.x, firstPoint.y);

        for (int i = 1; i < strokePoints.size(); i++) {
            StrokePoint point = strokePoints.get(i);
            gc.lineTo(point.x, point.y);
        }

        gc.stroke();
    }

    @Override
    public boolean canMergeWith(DrawingCommand other) {
        if (!(other instanceof StrokeCommand)) return false;

        StrokeCommand otherStroke = (StrokeCommand) other;
        return this.tool == otherStroke.tool &&
                this.color.equals(otherStroke.color) &&
                this.strokeWidth == otherStroke.strokeWidth &&
                !this.executed; // Only merge if current stroke isn't finished
    }

    @Override
    public void mergeWith(DrawingCommand other) {
        if (other instanceof StrokeCommand) {
            StrokeCommand otherStroke = (StrokeCommand) other;
            this.strokePoints.addAll(otherStroke.strokePoints);
        }
    }

    @Override
    public String getDescription() {
        return tool.getDisplayName() + " Stroke";
    }

    // Helper class for stroke points
    private static class StrokePoint {
        final double x, y;

        StrokePoint(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
}