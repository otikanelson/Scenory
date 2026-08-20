package com.example.scenory.tools;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;

/**
 * Eraser tool for removing drawn content
 */
public class EraserTool implements Tool {
    
    private double size = 10.0;
    private double lastX;
    private double lastY;
    
    @Override
    public String getName() {
        return "Eraser";
    }
    
    @Override
    public void onMousePressed(MouseEvent event, GraphicsContext gc) {
        lastX = event.getX();
        lastY = event.getY();
        
        // Erase at the current position
        gc.clearRect(lastX - size/2, lastY - size/2, size, size);
    }
    
    @Override
    public void onMouseDragged(MouseEvent event, GraphicsContext gc) {
        double currentX = event.getX();
        double currentY = event.getY();
        
        // Clear along the path
        gc.clearRect(currentX - size/2, currentY - size/2, size, size);
        
        lastX = currentX;
        lastY = currentY;
    }
    
    @Override
    public void onMouseReleased(MouseEvent event, GraphicsContext gc) {
        // No action needed on release for eraser
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
