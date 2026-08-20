package com.example.scenory.tools;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * Square/Rectangle tool for drawing rectangles
 */
public class SquareTool implements Tool {
    
    private double size = 2.0; // Border width
    private double startX;
    private double startY;
    
    @Override
    public String getName() {
        return "Square";
    }
    
    @Override
    public void onMousePressed(MouseEvent event, GraphicsContext gc) {
        startX = event.getX();
        startY = event.getY();
    }
    
    @Override
    public void onMouseDragged(MouseEvent event, GraphicsContext gc) {
        // Visual feedback could be implemented here with a temporary overlay
        // For now, we'll draw on release
    }
    
    @Override
    public void onMouseReleased(MouseEvent event, GraphicsContext gc) {
        double endX = event.getX();
        double endY = event.getY();
        
        // Calculate rectangle bounds
        double x = Math.min(startX, endX);
        double y = Math.min(startY, endY);
        double width = Math.abs(endX - startX);
        double height = Math.abs(endY - startY);
        
        // Draw rectangle
        gc.setLineWidth(size);
        gc.setStroke(Color.BLACK);
        gc.strokeRect(x, y, width, height);
    }
    
    @Override
    public void setSize(double size) {
        this.size = size;
    }
    
    @Override
    public double getSize() {
        return size;
    }
}
