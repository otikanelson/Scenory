package com.example.scenory.controller;

import com.example.scenory.enums.DrawingTool;
import com.example.scenory.view.components.DrawingCanvas;
import com.example.scenory.view.panels.ToolSelectionPanel;
import javafx.scene.paint.Color;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

import java.util.function.Consumer;

/**
 * Manages drawing tools and canvas interaction
 */
public class ToolManager {

    private final DrawingCanvas canvas;
    private final ToolSelectionPanel toolPanel;
    private final Consumer<String> statusUpdater;

    private DrawingTool currentTool = DrawingTool.PEN;
    private Color currentColor = Color.BLACK;
    private double strokeWidth = 2.0;

    // Thumbnail generation
    private Timeline thumbnailUpdateTimer;
    private Runnable thumbnailUpdateCallback;

    public ToolManager(DrawingCanvas canvas, ToolSelectionPanel toolPanel, Consumer<String> statusUpdater) {
        this.canvas = canvas;
        this.toolPanel = toolPanel;
        this.statusUpdater = statusUpdater;

        setupToolIntegration();
        setupThumbnailGeneration();
    }

    private void setupToolIntegration() {
        if (toolPanel == null) return;

        // Connect tool selection to drawing canvas
        toolPanel.selectedToolProperty().addListener((obs, oldTool, newTool) -> {
            if (canvas != null && newTool != null) {
                canvas.setCurrentTool(newTool);
                currentTool = newTool;
                statusUpdater.accept("🛠️ " + newTool.getDisplayName() + " tool selected");
            }
        });

        // Connect color changes
        toolPanel.selectedColorProperty().addListener((obs, oldColor, newColor) -> {
            if (canvas != null && newColor != null) {
                canvas.setCurrentColor(newColor);
                currentColor = newColor;
                statusUpdater.accept("🎨 Color changed");
            }
        });

        // Connect stroke size changes
        toolPanel.strokeSizeProperty().addListener((obs, oldSize, newSize) -> {
            if (canvas != null && newSize != null) {
                canvas.setStrokeWidth(newSize.doubleValue());
                strokeWidth = newSize.doubleValue();
                statusUpdater.accept("📏 Brush size: " + Math.round(newSize.doubleValue()));
            }
        });

        System.out.println("🔗 Tool integration setup complete");
    }

    private void setupThumbnailGeneration() {
        // Create a timer that triggers thumbnail updates after drawing stops
        thumbnailUpdateTimer = new Timeline(new KeyFrame(Duration.millis(1000), e -> {
            if (thumbnailUpdateCallback != null) {
                thumbnailUpdateCallback.run();
            }
        }));
    }

    public void setThumbnailUpdateCallback(Runnable callback) {
        this.thumbnailUpdateCallback = callback;
    }

    public void triggerThumbnailUpdate() {
        if (thumbnailUpdateTimer != null) {
            thumbnailUpdateTimer.stop();
            thumbnailUpdateTimer.play();
        }
    }

    public void selectTool(DrawingTool tool) {
        if (canvas != null) {
            canvas.setCurrentTool(tool);
            currentTool = tool;

            // Update tool selection panel if available
            if (toolPanel != null) {
                toolPanel.setSelectedTool(tool);
            }

            statusUpdater.accept("🛠️ " + tool.getDisplayName() + " tool selected");
        }
    }

    // Individual tool selection methods
    public void selectPenTool() {
        selectTool(DrawingTool.PEN);
    }

    public void selectBrushTool() {
        selectTool(DrawingTool.BRUSH);
    }

    public void selectEraserTool() {
        selectTool(DrawingTool.ERASER);
    }

    public void selectRectangleTool() {
        selectTool(DrawingTool.RECTANGLE);
        statusUpdater.accept("🔲 Rectangle tool selected - Click and drag to draw");
    }

    public void selectCircleTool() {
        selectTool(DrawingTool.CIRCLE);
        statusUpdater.accept("⭕ Circle tool selected - Click and drag to draw");
    }

    public void selectLineTool() {
        selectTool(DrawingTool.LINE);
        statusUpdater.accept("📏 Line tool selected - Click and drag to draw");
    }

    public void selectTextTool() {
        selectTool(DrawingTool.TEXT);
        statusUpdater.accept("📝 Text tool selected");
    }

    public void clearCanvas() {
        if (canvas != null) {
            canvas.clearCanvasWithUndo();
            statusUpdater.accept("Canvas cleared (can be undone)");
            // Trigger immediate thumbnail update for cleared canvas
            if (thumbnailUpdateCallback != null) {
                thumbnailUpdateCallback.run();
            }
        }
    }

    // Zoom operations
    public void zoomIn() {
        if (canvas != null) {
            canvas.zoomIn();
            statusUpdater.accept("Zoomed in: " + canvas.getZoomPercentage());
        }
    }

    public void zoomOut() {
        if (canvas != null) {
            canvas.zoomOut();
            statusUpdater.accept("Zoomed out: " + canvas.getZoomPercentage());
        }
    }

    public void resetZoom() {
        if (canvas != null) {
            canvas.resetZoom();
            statusUpdater.accept("Zoom reset to actual size");
        }
    }

    public void fitToWindow() {
        if (canvas != null) {
            canvas.fitToWindow();
            statusUpdater.accept("Zoom fit to window");
        }
    }

    // Undo/Redo operations
    public boolean undo() {
        if (canvas != null && canvas.undo()) {
            statusUpdater.accept("↶ " + canvas.getUndoDescription());
            System.out.println("↶ Undo executed");
            // Trigger thumbnail update after undo
            triggerThumbnailUpdate();
            return true;
        } else {
            statusUpdater.accept("Nothing to undo");
            return false;
        }
    }

    public boolean redo() {
        if (canvas != null && canvas.redo()) {
            statusUpdater.accept("↷ " + canvas.getRedoDescription());
            System.out.println("↷ Redo executed");
            // Trigger thumbnail update after redo
            triggerThumbnailUpdate();
            return true;
        } else {
            statusUpdater.accept("Nothing to redo");
            return false;
        }
    }

    // Getters
    public DrawingTool getCurrentTool() {
        return currentTool;
    }

    public Color getCurrentColor() {
        return currentColor;
    }

    public double getStrokeWidth() {
        return strokeWidth;
    }

    public DrawingCanvas getCanvas() {
        return canvas;
    }

    public boolean canUndo() {
        return canvas != null && canvas.canUndo();
    }

    public boolean canRedo() {
        return canvas != null && canvas.canRedo();
    }

    public String getUndoDescription() {
        return canvas != null ? canvas.getUndoDescription() : "Undo";
    }

    public String getRedoDescription() {
        return canvas != null ? canvas.getRedoDescription() : "Redo";
    }
}