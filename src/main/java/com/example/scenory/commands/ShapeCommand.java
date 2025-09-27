package com.example.scenory.commands;

import com.example.scenory.view.components.DrawingCanvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.image.WritableImage;

/**
 * Fixed Shape Command for drawing geometric shapes with proper coordinate handling
 * Handles rectangle, circle, and line tools with zoom-aware rendering
 */
public class ShapeCommand implements DrawingCommand {

    public enum ShapeType {
        RECTANGLE("Rectangle"),
        CIRCLE("Circle"),
        LINE("Line");

        private final String displayName;

        ShapeType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private final DrawingCanvas canvas;
    private final ShapeType shapeType;
    private final double startX, startY, endX, endY;
    private final Color color;
    private final double strokeWidth;
    private final boolean filled;
    private WritableImage beforeImage;

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

        // Capture canvas state before drawing
        captureBeforeState();
    }

    @Override
    public void execute() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        if (gc == null) {
            System.err.println("Graphics context not available for shape execution");
            return;
        }

        // Save current graphics state
        gc.save();

        try {
            // Set up drawing properties
            setupGraphicsContext(gc);

            // Draw the shape
            drawShape(gc);

        } finally {
            // Always restore graphics state
            gc.restore();
        }

        System.out.println("📐 Executed " + shapeType.getDisplayName() + " from (" +
                startX + "," + startY + ") to (" + endX + "," + endY + ")");
    }

    @Override
    public void undo() {
        if (beforeImage == null) {
            System.err.println("Cannot undo shape: no before image captured");
            return;
        }

        GraphicsContext gc = canvas.getGraphicsContext2D();
        if (gc == null) {
            System.err.println("Graphics context not available for shape undo");
            return;
        }

        // Clear canvas and restore previous state
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.drawImage(beforeImage, 0, 0);

        System.out.println("↶ Undid " + shapeType.getDisplayName());
    }

    @Override
    public String getDescription() {
        return shapeType.getDisplayName() + " (" +
                Math.round(startX) + "," + Math.round(startY) + ") to (" +
                Math.round(endX) + "," + Math.round(endY) + ")";
    }

    @Override
    public boolean canMergeWith(DrawingCommand other) {
        // Shapes generally don't merge with other commands
        return false;
    }

    @Override
    public void mergeWith(DrawingCommand other) {
        // Shapes don't merge
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

        gc.setGlobalBlendMode(javafx.scene.effect.BlendMode.SRC_OVER);
        gc.setStroke(color);
        gc.setFill(color);
        gc.setLineWidth(strokeWidth);
        gc.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
        gc.setLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);
    }

    /**
     * Draw the shape based on its type
     */
    private void drawShape(GraphicsContext gc) {
        switch (shapeType) {
            case RECTANGLE:
                drawRectangle(gc);
                break;
            case CIRCLE:
                drawCircle(gc);
                break;
            case LINE:
                drawLine(gc);
                break;
        }
    }

    /**
     * Draw a rectangle
     */
    private void drawRectangle(GraphicsContext gc) {
        // Calculate rectangle bounds
        double x = Math.min(startX, endX);
        double y = Math.min(startY, endY);
        double width = Math.abs(endX - startX);
        double height = Math.abs(endY - startY);

        if (filled) {
            gc.fillRect(x, y, width, height);
        } else {
            gc.strokeRect(x, y, width, height);
        }
    }

    /**
     * Draw a circle (ellipse)
     */
    private void drawCircle(GraphicsContext gc) {
        // Calculate circle bounds
        double x = Math.min(startX, endX);
        double y = Math.min(startY, endY);
        double width = Math.abs(endX - startX);
        double height = Math.abs(endY - startY);

        if (filled) {
            gc.fillOval(x, y, width, height);
        } else {
            gc.strokeOval(x, y, width, height);
        }
    }

    /**
     * Draw a line
     */
    private void drawLine(GraphicsContext gc) {
        gc.strokeLine(startX, startY, endX, endY);
    }

    // =====================================
    // UTILITY METHODS
    // =====================================

    /**
     * Get the bounding box of this shape
     */
    public javafx.geometry.BoundingBox getBoundingBox() {
        double minX = Math.min(startX, endX);
        double maxX = Math.max(startX, endX);
        double minY = Math.min(startY, endY);
        double maxY = Math.max(startY, endY);

        // Expand by stroke width
        double padding = strokeWidth / 2.0;
        return new javafx.geometry.BoundingBox(
                minX - padding, minY - padding,
                maxX - minX + 2 * padding, maxY - minY + 2 * padding
        );
    }

    /**
     * Get the area of this shape
     */
    public double getArea() {
        double width = Math.abs(endX - startX);
        double height = Math.abs(endY - startY);

        switch (shapeType) {
            case RECTANGLE:
                return width * height;
            case CIRCLE:
                // Approximate as ellipse
                double radiusX = width / 2.0;
                double radiusY = height / 2.0;
                return Math.PI * radiusX * radiusY;
            case LINE:
                return 0.0; // Lines have no area
            default:
                return 0.0;
        }
    }

    /**
     * Get the perimeter of this shape
     */
    public double getPerimeter() {
        double width = Math.abs(endX - startX);
        double height = Math.abs(endY - startY);

        switch (shapeType) {
            case RECTANGLE:
                return 2 * (width + height);
            case CIRCLE:
                // Approximate perimeter of ellipse
                double a = width / 2.0;
                double b = height / 2.0;
                // Ramanujan's approximation
                double h = Math.pow(a - b, 2) / Math.pow(a + b, 2);
                return Math.PI * (a + b) * (1 + (3 * h) / (10 + Math.sqrt(4 - 3 * h)));
            case LINE:
                double dx = endX - startX;
                double dy = endY - startY;
                return Math.sqrt(dx * dx + dy * dy);
            default:
                return 0.0;
        }
    }

    /**
     * Check if a point is inside this shape
     */
    public boolean containsPoint(double x, double y) {
        double minX = Math.min(startX, endX);
        double maxX = Math.max(startX, endX);
        double minY = Math.min(startY, endY);
        double maxY = Math.max(startY, endY);

        switch (shapeType) {
            case RECTANGLE:
                return x >= minX && x <= maxX && y >= minY && y <= maxY;

            case CIRCLE:
                double centerX = (startX + endX) / 2.0;
                double centerY = (startY + endY) / 2.0;
                double radiusX = Math.abs(endX - startX) / 2.0;
                double radiusY = Math.abs(endY - startY) / 2.0;

                double normalizedX = (x - centerX) / radiusX;
                double normalizedY = (y - centerY) / radiusY;
                return (normalizedX * normalizedX + normalizedY * normalizedY) <= 1.0;

            case LINE:
                // Check if point is close to the line (within stroke width)
                double lineLength = getPerimeter();
                if (lineLength == 0) return false;

                // Distance from point to line
                double A = endY - startY;
                double B = startX - endX;
                double C = endX * startY - startX * endY;
                double distance = Math.abs(A * x + B * y + C) / Math.sqrt(A * A + B * B);

                return distance <= strokeWidth / 2.0;

            default:
                return false;
        }
    }

    // =====================================
    // GETTERS
    // =====================================

    public ShapeType getShapeType() {
        return shapeType;
    }

    public double getStartX() {
        return startX;
    }

    public double getStartY() {
        return startY;
    }

    public double getEndX() {
        return endX;
    }

    public double getEndY() {
        return endY;
    }

    public Color getColor() {
        return color;
    }

    public double getStrokeWidth() {
        return strokeWidth;
    }

    public boolean isFilled() {
        return filled;
    }

    public double getWidth() {
        return Math.abs(endX - startX);
    }

    public double getHeight() {
        return Math.abs(endY - startY);
    }
}