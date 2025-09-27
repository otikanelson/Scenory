package com.example.scenory.commands;

import com.example.scenory.enums.DrawingTool;
import com.example.scenory.view.components.DrawingCanvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.PixelReader;

import java.util.ArrayList;
import java.util.List;

/**
 * Fixed Stroke Command for drawing operations with proper coordinate handling
 * Handles pen, brush, pencil, and eraser tools with zoom-aware rendering
 */
public class StrokeCommand implements DrawingCommand {

    private final DrawingCanvas canvas;
    private final DrawingTool tool;
    private final Color color;
    private final double strokeWidth;
    private final List<StrokePoint> points;
    private WritableImage beforeImage;
    private boolean isFinished = false;

    // Point class to store coordinates and pressure (for future)
    private static class StrokePoint {
        final double x, y;
        final double pressure; // For future pressure sensitivity

        StrokePoint(double x, double y) {
            this.x = x;
            this.y = y;
            this.pressure = 1.0; // Default pressure
        }

        StrokePoint(double x, double y, double pressure) {
            this.x = x;
            this.y = y;
            this.pressure = pressure;
        }
    }

    public StrokeCommand(DrawingCanvas canvas, DrawingTool tool, Color color, double strokeWidth) {
        this.canvas = canvas;
        this.tool = tool;
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.points = new ArrayList<>();

        // Capture canvas state before drawing
        captureBeforeState();
    }

    /**
     * Add a point to the stroke path (in canvas coordinates)
     */
    public void addPoint(double x, double y) {
        points.add(new StrokePoint(x, y));
    }

    /**
     * Add a point with pressure sensitivity (for future)
     */
    public void addPoint(double x, double y, double pressure) {
        points.add(new StrokePoint(x, y, pressure));
    }

    /**
     * Mark the stroke as finished
     */
    public void finishStroke() {
        isFinished = true;
    }

    @Override
    public void execute() {
        if (points.isEmpty()) {
            return;
        }

        GraphicsContext gc = canvas.getGraphicsContext2D();
        if (gc == null) {
            System.err.println("Graphics context not available for stroke execution");
            return;
        }

        // Save current graphics state
        gc.save();

        try {
            // Set up drawing properties
            setupGraphicsContext(gc);

            // Draw the stroke
            drawStroke(gc);

        } finally {
            // Always restore graphics state
            gc.restore();
        }

        System.out.println("✏️ Executed stroke with " + points.size() + " points using " + tool.getDisplayName());
    }

    @Override
    public void undo() {
        if (beforeImage == null) {
            System.err.println("Cannot undo stroke: no before image captured");
            return;
        }

        GraphicsContext gc = canvas.getGraphicsContext2D();
        if (gc == null) {
            System.err.println("Graphics context not available for stroke undo");
            return;
        }

        // Clear canvas and restore previous state
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.drawImage(beforeImage, 0, 0);

        System.out.println("↶ Undid stroke with " + points.size() + " points");
    }

    @Override
    public String getDescription() {
        if (points.isEmpty()) {
            return tool.getDisplayName() + " stroke (empty)";
        }
        return tool.getDisplayName() + " stroke (" + points.size() + " points)";
    }

    @Override
    public boolean canMergeWith(DrawingCommand other) {
        if (!isFinished || !(other instanceof StrokeCommand)) {
            return false;
        }

        StrokeCommand otherStroke = (StrokeCommand) other;

        // Can merge if same tool, color, and stroke width
        return this.tool == otherStroke.tool &&
                this.color.equals(otherStroke.color) &&
                Math.abs(this.strokeWidth - otherStroke.strokeWidth) < 0.1 &&
                !otherStroke.isFinished;
    }

    @Override
    public void mergeWith(DrawingCommand other) {
        if (!(other instanceof StrokeCommand)) {
            return;
        }

        StrokeCommand otherStroke = (StrokeCommand) other;

        // Add all points from the other stroke
        this.points.addAll(otherStroke.points);

        System.out.println("🔗 Merged strokes: " + this.points.size() + " total points");
    }

    /**
     * Capture the canvas state before drawing
     */
    private void captureBeforeState() {
        try {
            beforeImage = canvas.snapshot(null, null);
        } catch (Exception e) {
            System.err.println("Failed to capture canvas state: " + e.getMessage());
            beforeImage = null;
        }
    }

    /**
     * Set up graphics context for drawing
     */
    private void setupGraphicsContext(GraphicsContext gc) {
        // Reset any existing transforms - we work in screen coordinates
        gc.setTransform(1, 0, 0, 1, 0, 0);

        switch (tool) {
            case PEN:
            case BRUSH:
            case PENCIL:
                gc.setGlobalBlendMode(javafx.scene.effect.BlendMode.SRC_OVER);
                gc.setStroke(color);
                gc.setFill(color);
                break;

            case ERASER:
                gc.setGlobalBlendMode(javafx.scene.effect.BlendMode.SRC_OVER);
                gc.setStroke(canvas.getBackgroundColor());
                gc.setFill(canvas.getBackgroundColor());
                break;

            default:
                gc.setGlobalBlendMode(javafx.scene.effect.BlendMode.SRC_OVER);
                gc.setStroke(color);
                gc.setFill(color);
                break;
        }

        // Set line properties
        gc.setLineWidth(strokeWidth);
        gc.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
        gc.setLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);
    }

    /**
     * Draw the stroke path
     */
    private void drawStroke(GraphicsContext gc) {
        if (points.size() == 1) {
            // Single point - draw a dot
            StrokePoint point = points.get(0);
            drawDot(gc, point.x, point.y);
        } else if (points.size() >= 2) {
            // Multiple points - draw connected lines
            drawConnectedPath(gc);
        }
    }

    /**
     * Draw a single dot for single-point strokes
     */
    private void drawDot(GraphicsContext gc, double x, double y) {
        double radius = strokeWidth / 2.0;

        switch (tool) {
            case BRUSH:
                // Soft brush effect
                gc.fillOval(x - radius, y - radius, strokeWidth, strokeWidth);
                break;

            case PEN:
            case PENCIL:
            case ERASER:
            default:
                // Hard edge dot
                gc.fillOval(x - radius, y - radius, strokeWidth, strokeWidth);
                break;
        }
    }

    /**
     * Draw connected path for multi-point strokes
     */
    private void drawConnectedPath(GraphicsContext gc) {
        switch (tool) {
            case BRUSH:
                drawBrushPath(gc);
                break;

            case PEN:
            case PENCIL:
            case ERASER:
            default:
                drawLinePath(gc);
                break;
        }
    }

    /**
     * Draw a simple line path
     */
    private void drawLinePath(GraphicsContext gc) {
        gc.beginPath();

        StrokePoint firstPoint = points.get(0);
        gc.moveTo(firstPoint.x, firstPoint.y);

        for (int i = 1; i < points.size(); i++) {
            StrokePoint point = points.get(i);
            gc.lineTo(point.x, point.y);
        }

        gc.stroke();
    }

    /**
     * Draw a brush path with variable width (future enhancement)
     */
    private void drawBrushPath(GraphicsContext gc) {
        // For now, draw as regular line path
        // Future: implement pressure-sensitive brush with variable width
        drawLinePath(gc);

        // Future implementation could include:
        // - Pressure sensitivity
        // - Brush texture
        // - Variable opacity
        // - Brush shape variations
    }

    /**
     * Apply smoothing to the stroke path (future enhancement)
     */
    private List<StrokePoint> smoothPath(List<StrokePoint> originalPoints) {
        if (originalPoints.size() < 3) {
            return originalPoints;
        }

        // Simple smoothing algorithm - can be enhanced
        List<StrokePoint> smoothed = new ArrayList<>();
        smoothed.add(originalPoints.get(0)); // Keep first point

        for (int i = 1; i < originalPoints.size() - 1; i++) {
            StrokePoint prev = originalPoints.get(i - 1);
            StrokePoint curr = originalPoints.get(i);
            StrokePoint next = originalPoints.get(i + 1);

            // Simple average smoothing
            double smoothX = (prev.x + curr.x + next.x) / 3.0;
            double smoothY = (prev.y + curr.y + next.y) / 3.0;

            smoothed.add(new StrokePoint(smoothX, smoothY, curr.pressure));
        }

        smoothed.add(originalPoints.get(originalPoints.size() - 1)); // Keep last point
        return smoothed;
    }

    // =====================================
    // UTILITY METHODS
    // =====================================

    /**
     * Get the bounding box of this stroke
     */
    public javafx.geometry.BoundingBox getBoundingBox() {
        if (points.isEmpty()) {
            return new javafx.geometry.BoundingBox(0, 0, 0, 0);
        }

        double minX = points.get(0).x;
        double maxX = points.get(0).x;
        double minY = points.get(0).y;
        double maxY = points.get(0).y;

        for (StrokePoint point : points) {
            minX = Math.min(minX, point.x);
            maxX = Math.max(maxX, point.x);
            minY = Math.min(minY, point.y);
            maxY = Math.max(maxY, point.y);
        }

        // Expand by stroke width
        double padding = strokeWidth / 2.0;
        return new javafx.geometry.BoundingBox(
                minX - padding, minY - padding,
                maxX - minX + 2 * padding, maxY - minY + 2 * padding
        );
    }

    /**
     * Get the number of points in this stroke
     */
    public int getPointCount() {
        return points.size();
    }

    /**
     * Get the total length of this stroke
     */
    public double getStrokeLength() {
        if (points.size() < 2) {
            return 0.0;
        }

        double totalLength = 0.0;
        for (int i = 1; i < points.size(); i++) {
            StrokePoint prev = points.get(i - 1);
            StrokePoint curr = points.get(i);

            double dx = curr.x - prev.x;
            double dy = curr.y - prev.y;
            totalLength += Math.sqrt(dx * dx + dy * dy);
        }

        return totalLength;
    }

    /**
     * Check if this stroke is finished
     */
    public boolean isFinished() {
        return isFinished;
    }

    /**
     * Get the tool used for this stroke
     */
    public DrawingTool getTool() {
        return tool;
    }

    /**
     * Get the color used for this stroke
     */
    public Color getColor() {
        return color;
    }

    /**
     * Get the stroke width
     */
    public double getStrokeWidth() {
        return strokeWidth;
    }
}