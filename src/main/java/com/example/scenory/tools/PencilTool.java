package com.example.scenory.tools;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * Pencil tool for drawing freehand lines
 */
public class PencilTool implements Tool {
    
    private double size = 2.0;
    private double lastX;
    private double lastY;
    
    @Override
    public String getName() {
        return "Pencil";
    }
    
    @Override
    public void onMousePressed(MouseEvent event, GraphicsContext gc) {
        lastX = event.getX();
        lastY = event.getY();
        
        // Draw a single point
        gc.setLineWidth(size);
        gc.setStroke(Color.BLACK);
        gc.strokeOval(lastX - size/2, lastY - size/2, size, size);
    }
    
    @Override
    public void onMouseDragged(MouseEvent event, GraphicsContext gc) {
        double currentX = event.getX();
        double currentY = event.getY();
        
        // Draw line from last position to current position
        gc.setLineWidth(size);
        gc.setStroke(Color.BLACK);
        gc.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
        gc.strokeLine(lastX, lastY, currentX, currentY);
        
        lastX = currentX;
        lastY = currentY;
    }
    
    @Override
    public void onMouseReleased(MouseEvent event, GraphicsContext gc) {
        // No action needed on release for pencil
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
