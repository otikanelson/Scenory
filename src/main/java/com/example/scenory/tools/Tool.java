package com.example.scenory.tools;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;

/**
 * Base interface for all drawing tools
 */
public interface Tool {
    
    /**
     * Get the name of the tool
     */
    String getName();
    
    /**
     * Handle mouse pressed event
     */
    void onMousePressed(MouseEvent event, GraphicsContext gc);
    
    /**
     * Handle mouse dragged event
     */
    void onMouseDragged(MouseEvent event, GraphicsContext gc);
    
    /**
     * Handle mouse released event
     */
    void onMouseReleased(MouseEvent event, GraphicsContext gc);
    
    /**
     * Set the tool size/width
     */
    void setSize(double size);
    
    /**
     * Get the current tool size/width
     */
    double getSize();
}
