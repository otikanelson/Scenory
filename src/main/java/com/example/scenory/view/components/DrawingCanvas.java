package com.example.scenory.view.components;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import com.example.scenory.enums.DrawingTool;

public class DrawingCanvas extends Canvas {
    private GraphicsContext gc;
    private DrawingTool currentTool = DrawingTool.PEN;
    private Color currentColor = Color.BLACK;
    private double strokeWidth = 2.0;
    private double lastX, lastY;

    public DrawingCanvas(double width, double height) {
        super(width, height);
        this.gc = getGraphicsContext2D();
        setupEventHandlers();
        initializeCanvas();
    }

    private void initializeCanvas() {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, getWidth(), getHeight());
        gc.setStroke(currentColor);
        gc.setLineWidth(strokeWidth);
    }

    private void setupEventHandlers() {
        setOnMousePressed(this::handleMousePressed);
        setOnMouseDragged(this::handleMouseDragged);
        setOnMouseReleased(this::handleMouseReleased);
    }

    private void handleMousePressed(MouseEvent event) {
        lastX = event.getX();
        lastY = event.getY();

        if (currentTool == DrawingTool.PEN || currentTool == DrawingTool.BRUSH) {
            gc.beginPath();
            gc.moveTo(lastX, lastY);
            gc.stroke();
        }
    }

    private void handleMouseDragged(MouseEvent event) {
        double currentX = event.getX();
        double currentY = event.getY();

        switch (currentTool) {
            case PEN:
            case BRUSH:
                gc.lineTo(currentX, currentY);
                gc.stroke();
                break;
            case ERASER:
                gc.clearRect(currentX - strokeWidth/2, currentY - strokeWidth/2,
                        strokeWidth, strokeWidth);
                break;
        }

        lastX = currentX;
        lastY = currentY;
    }

    private void handleMouseReleased(MouseEvent event) {
        // Finalize drawing operation
    }

    // Tool and property setters
    public void setCurrentTool(DrawingTool tool) { this.currentTool = tool; }
    public void setCurrentColor(Color color) {
        this.currentColor = color;
        gc.setStroke(color);
    }
    public void setStrokeWidth(double width) {
        this.strokeWidth = width;
        gc.setLineWidth(width);
    }
}