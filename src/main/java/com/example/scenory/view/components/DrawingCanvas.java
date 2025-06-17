package com.example.scenory.view.components;

import com.example.scenory.enums.DrawingTool;
import com.example.scenory.commands.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.image.WritableImage;

/**
 * Enhanced Drawing Canvas with Command System Integration
 * Supports undo/redo, zoom, and all drawing tools
 */
public class DrawingCanvas extends Canvas {

    // =====================================
    // CORE DRAWING FIELDS
    // =====================================
    private GraphicsContext gc;
    private DrawingTool currentTool = DrawingTool.PEN;
    private Color currentColor = Color.BLACK;
    private Color backgroundColor = Color.WHITE;
    private double strokeWidth = 2.0;

    // Drawing state
    private boolean isDrawing = false;
    private double lastX, lastY;

    // =====================================
    // ZOOM FUNCTIONALITY FIELDS
    // =====================================
    private double zoomLevel = 1.0;
    private double minZoom = 0.1;
    private double maxZoom = 5.0;
    private double zoomStep = 1.2;

    // Zoom change listener interface
    public interface ZoomChangeListener {
        void onZoomChanged(double newZoomLevel);
    }

    private ZoomChangeListener zoomChangeListener;

    // =====================================
    // COMMAND SYSTEM INTEGRATION FIELDS
    // =====================================
    private CommandManager commandManager;
    private StrokeCommand currentStrokeCommand;
    private boolean recordCommands = true;

    // Shape drawing state (for rectangle, circle, line tools)
    private double shapeStartX, shapeStartY;
    private boolean isDrawingShape = false;

    // Canvas initialization state
    private boolean isInitialized = false;

    // =====================================
    // CONSTRUCTOR
    // =====================================
    public DrawingCanvas(double width, double height) {
        super(width, height);

        // Initialize the canvas when it's ready
        this.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null && !isInitialized) {
                initializeCanvasWhenReady();
            }
        });

        // Fallback initialization - try immediately
        initializeCanvasWhenReady();

        setupEventHandlers();
        setupZoomControls();

        System.out.println("🖼️ Canvas initialized: " + width + "x" + height);
    }

    // =====================================
    // INITIALIZATION METHODS
    // =====================================
    private void initializeCanvasWhenReady() {
        if (isInitialized) return;

        try {
            this.gc = getGraphicsContext2D();
            if (this.gc != null) {
                initializeCanvas();
                isInitialized = true;
                System.out.println("✅ Canvas graphics context initialized successfully");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Canvas not ready yet, will initialize later: " + e.getMessage());
            // Will retry when scene property changes
        }
    }

    private void initializeCanvas() {
        if (gc == null) {
            System.out.println("⚠️ Graphics context not available, skipping initialization");
            return;
        }

        // Set initial background
        gc.setFill(backgroundColor);
        gc.fillRect(0, 0, getWidth(), getHeight());

        // Set default drawing properties
        gc.setStroke(currentColor);
        gc.setLineWidth(strokeWidth);
        gc.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
        gc.setLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);
    }

    private void setupEventHandlers() {
        setOnMousePressed(this::handleMousePressed);
        setOnMouseDragged(this::handleMouseDragged);
        setOnMouseReleased(this::handleMouseReleased);
    }

    public void setupZoomControls() {
        // Mouse wheel zoom with Ctrl key
        setOnScroll(event -> {
            if (event.isControlDown()) {
                double deltaY = event.getDeltaY();

                // Get mouse position relative to canvas
                double mouseX = event.getX();
                double mouseY = event.getY();

                if (deltaY > 0) {
                    zoomIn(mouseX, mouseY);
                } else {
                    zoomOut(mouseX, mouseY);
                }
                event.consume();
            }
        });

        // Optional: Add zoom on double-click
        setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && event.isControlDown()) {
                // Double-click with Ctrl to reset zoom
                resetZoom();
                event.consume();
            }
        });

        System.out.println("🔍 Canvas zoom controls initialized (Ctrl+Scroll, Ctrl+Double-click to reset)");
    }

    // =====================================
    // SAFE GRAPHICS CONTEXT ACCESS
    // =====================================
    private GraphicsContext getGC() {
        if (gc == null) {
            initializeCanvasWhenReady();
        }
        return gc;
    }

    // =====================================
    // MOUSE EVENT HANDLERS WITH COMMANDS
    // =====================================
    private void handleMousePressed(MouseEvent event) {
        GraphicsContext currentGC = getGC();
        if (currentGC == null || !isValidCoordinate(event.getX(), event.getY())) {
            return;
        }

        lastX = event.getX();
        lastY = event.getY();
        isDrawing = true;

        switch (currentTool) {
            case PEN:
            case BRUSH:
            case PENCIL:
                startStrokeCommand();
                // Set up drawing mode
                currentGC.setGlobalBlendMode(javafx.scene.effect.BlendMode.SRC_OVER);
                currentGC.setStroke(currentColor);
                currentGC.setLineWidth(strokeWidth);
                currentGC.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
                currentGC.beginPath();
                currentGC.moveTo(lastX, lastY);
                // Draw a small dot for single clicks
                currentGC.lineTo(lastX + 0.1, lastY + 0.1);
                currentGC.stroke();
                break;

            case ERASER:
                startStrokeCommand();
                // Set up eraser mode
                currentGC.setGlobalBlendMode(javafx.scene.effect.BlendMode.SRC_OVER);
                currentGC.setStroke(backgroundColor);
                currentGC.setLineWidth(strokeWidth);
                currentGC.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
                currentGC.beginPath();
                currentGC.moveTo(lastX, lastY);
                // Draw a small dot for single clicks
                currentGC.lineTo(lastX + 0.1, lastY + 0.1);
                currentGC.stroke();
                break;

            case RECTANGLE:
            case CIRCLE:
            case LINE:
                // Shape tools - store start position
                shapeStartX = lastX;
                shapeStartY = lastY;
                isDrawingShape = true;
                break;

            case FILL:
                // Flood fill tool (future implementation)
                break;

            case TEXT:
                // Text tool (future implementation)
                break;
        }
    }

    private void handleMouseDragged(MouseEvent event) {
        GraphicsContext currentGC = getGC();
        if (!isDrawing || currentGC == null || !isValidCoordinate(event.getX(), event.getY())) {
            return;
        }

        double currentX = event.getX();
        double currentY = event.getY();

        switch (currentTool) {
            case PEN:
            case BRUSH:
            case PENCIL:
                // Add point to current stroke command
                if (currentStrokeCommand != null) {
                    currentStrokeCommand.addPoint(currentX, currentY);
                }

                currentGC.setGlobalBlendMode(javafx.scene.effect.BlendMode.SRC_OVER);
                currentGC.setStroke(currentColor);
                currentGC.setLineWidth(strokeWidth);
                currentGC.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
                currentGC.strokeLine(lastX, lastY, currentX, currentY);
                break;

            case ERASER:
                // Add point to current stroke command
                if (currentStrokeCommand != null) {
                    currentStrokeCommand.addPoint(currentX, currentY);
                }

                // Erase by drawing with background color
                currentGC.setGlobalBlendMode(javafx.scene.effect.BlendMode.SRC_OVER);
                currentGC.setStroke(backgroundColor);
                currentGC.setLineWidth(strokeWidth);
                currentGC.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
                currentGC.strokeLine(lastX, lastY, currentX, currentY);
                break;

            case RECTANGLE:
            case CIRCLE:
            case LINE:
                // Shape tools - preview would go here (not implemented yet)
                // For now, just store the current position
                break;
        }

        lastX = currentX;
        lastY = currentY;
    }

    private void handleMouseReleased(MouseEvent event) {
        GraphicsContext currentGC = getGC();
        if (!isDrawing || currentGC == null) return;

        isDrawing = false;
        isDrawingShape = false;

        switch (currentTool) {
            case PEN:
            case BRUSH:
            case PENCIL:
            case ERASER:
                finishStrokeCommand();
                break;

            case RECTANGLE:
                if (isValidCoordinate(event.getX(), event.getY())) {
                    executeShapeCommand(ShapeCommand.ShapeType.RECTANGLE,
                            shapeStartX, shapeStartY, event.getX(), event.getY(), false);
                }
                break;

            case CIRCLE:
                if (isValidCoordinate(event.getX(), event.getY())) {
                    executeShapeCommand(ShapeCommand.ShapeType.CIRCLE,
                            shapeStartX, shapeStartY, event.getX(), event.getY(), false);
                }
                break;

            case LINE:
                if (isValidCoordinate(event.getX(), event.getY())) {
                    executeShapeCommand(ShapeCommand.ShapeType.LINE,
                            shapeStartX, shapeStartY, event.getX(), event.getY(), false);
                }
                break;
        }

        // Reset graphics context to normal state
        if (currentGC != null) {
            currentGC.setGlobalBlendMode(javafx.scene.effect.BlendMode.SRC_OVER);
            currentGC.setStroke(currentColor);
            currentGC.setLineWidth(strokeWidth);
        }
    }

    // =====================================
    // COMMAND SYSTEM METHODS
    // =====================================

    /**
     * Set the command manager for undo/redo functionality
     */
    public void setCommandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
        System.out.println("🔗 Command manager connected to DrawingCanvas");
    }

    /**
     * Enable or disable command recording
     */
    public void setRecordCommands(boolean record) {
        this.recordCommands = record;
    }

    /**
     * Execute a command through the command manager
     */
    private void executeCommand(DrawingCommand command) {
        if (commandManager != null && recordCommands) {
            commandManager.executeCommand(command);
        }
    }

    /**
     * Start a new stroke command for drawing operations
     */
    private void startStrokeCommand() {
        if (commandManager != null && recordCommands) {
            currentStrokeCommand = new StrokeCommand(this, currentTool, currentColor, strokeWidth);
            currentStrokeCommand.addPoint(lastX, lastY);
        }
    }

    /**
     * Finish the current stroke command
     */
    private void finishStrokeCommand() {
        if (currentStrokeCommand != null) {
            currentStrokeCommand.finishStroke();
            executeCommand(currentStrokeCommand);
            currentStrokeCommand = null;
        }
    }

    /**
     * Execute a shape command
     */
    private void executeShapeCommand(ShapeCommand.ShapeType shapeType,
                                     double startX, double startY, double endX, double endY,
                                     boolean filled) {
        ShapeCommand shapeCommand = new ShapeCommand(this, shapeType,
                startX, startY, endX, endY,
                currentColor, strokeWidth, filled);
        executeCommand(shapeCommand);
    }

    // =====================================
    // PUBLIC COMMAND METHODS
    // =====================================

    /**
     * Clear canvas with undo support
     */
    public void clearCanvasWithUndo() {
        ClearCanvasCommand clearCommand = new ClearCanvasCommand(this);
        executeCommand(clearCommand);
    }

    /**
     * Undo last operation
     */
    public boolean undo() {
        if (commandManager != null) {
            return commandManager.undo();
        }
        return false;
    }

    /**
     * Redo last undone operation
     */
    public boolean redo() {
        if (commandManager != null) {
            return commandManager.redo();
        }
        return false;
    }

    /**
     * Check if undo is available
     */
    public boolean canUndo() {
        return commandManager != null && commandManager.canUndo();
    }

    /**
     * Check if redo is available
     */
    public boolean canRedo() {
        return commandManager != null && commandManager.canRedo();
    }

    /**
     * Get command manager for external access
     */
    public CommandManager getCommandManager() {
        return commandManager;
    }

    /**
     * Create a canvas state command (for complex operations)
     */
    public CanvasStateCommand createStateCommand(String description) {
        return new CanvasStateCommand(this, description);
    }

    // =====================================
    // CANVAS MANAGEMENT METHODS
    // =====================================

    /**
     * Clear canvas without undo (internal method)
     */
    public void clearCanvas() {
        GraphicsContext currentGC = getGC();
        if (currentGC == null) return;

        currentGC.clearRect(0, 0, getWidth(), getHeight());
        currentGC.setFill(backgroundColor);
        currentGC.fillRect(0, 0, getWidth(), getHeight());
        System.out.println("🧹 Canvas cleared (no undo)");
    }

    /**
     * Set canvas background color
     */
    public void setCanvasBackgroundColor(Color color) {
        this.backgroundColor = color;
        // Redraw background
        GraphicsContext currentGC = getGC();
        if (currentGC != null) {
            currentGC.save();
            currentGC.setGlobalBlendMode(javafx.scene.effect.BlendMode.SRC_OVER);
            currentGC.setFill(backgroundColor);
            currentGC.fillRect(0, 0, getWidth(), getHeight());
            currentGC.restore();
        }
    }

    // =====================================
    // ZOOM METHODS
    // =====================================

    /**
     * Zoom in centered on a specific point
     */
    public void zoomIn(double centerX, double centerY) {
        double oldZoom = zoomLevel;
        zoomLevel = Math.min(zoomLevel * zoomStep, maxZoom);

        if (zoomLevel != oldZoom) {
            applyZoom(centerX, centerY);
            System.out.println("🔍 Zoomed in to " + Math.round(zoomLevel * 100) + "%");
        }
    }

    /**
     * Zoom out centered on a specific point
     */
    public void zoomOut(double centerX, double centerY) {
        double oldZoom = zoomLevel;
        zoomLevel = Math.max(zoomLevel / zoomStep, minZoom);

        if (zoomLevel != oldZoom) {
            applyZoom(centerX, centerY);
            System.out.println("🔍 Zoomed out to " + Math.round(zoomLevel * 100) + "%");
        }
    }

    /**
     * Zoom in from center of canvas
     */
    public void zoomIn() {
        zoomIn(getWidth() / 2, getHeight() / 2);
    }

    /**
     * Zoom out from center of canvas
     */
    public void zoomOut() {
        zoomOut(getWidth() / 2, getHeight() / 2);
    }

    /**
     * Reset zoom to 100%
     */
    public void resetZoom() {
        if (zoomLevel != 1.0) {
            zoomLevel = 1.0;
            applyZoom(getWidth() / 2, getHeight() / 2);
            System.out.println("🔍 Zoom reset to 100%");
        }
    }

    /**
     * Set specific zoom level
     */
    public void setZoomLevel(double zoom) {
        double oldZoom = zoomLevel;
        zoomLevel = Math.max(minZoom, Math.min(zoom, maxZoom));

        if (zoomLevel != oldZoom) {
            applyZoom(getWidth() / 2, getHeight() / 2);
            System.out.println("🔍 Zoom set to " + Math.round(zoomLevel * 100) + "%");
        }
    }

    /**
     * Fit canvas to available space (if in scroll pane)
     */
    public void fitToWindow() {
        // This would need parent container information
        // For now, reset to 100%
        resetZoom();
    }

    /**
     * Apply zoom transformation
     */
    private void applyZoom(double centerX, double centerY) {
        // Apply scaling transform
        setScaleX(zoomLevel);
        setScaleY(zoomLevel);

        // Fire zoom change event
        fireZoomChanged();
    }

    /**
     * Notify listeners of zoom change
     */
    private void fireZoomChanged() {
        if (zoomChangeListener != null) {
            zoomChangeListener.onZoomChanged(zoomLevel);
        }
    }

    // =====================================
    // TOOL AND PROPERTY SETTERS
    // =====================================

    public void setCurrentTool(DrawingTool tool) {
        // Finish any current stroke when switching tools
        if (currentStrokeCommand != null) {
            finishStrokeCommand();
        }

        this.currentTool = tool;
        System.out.println("🛠️ Tool changed to: " + tool.getDisplayName());
    }

    public void setCurrentColor(Color color) {
        this.currentColor = color;
        GraphicsContext currentGC = getGC();
        if (currentGC != null) {
            currentGC.setStroke(color);
        }
    }

    public void setStrokeWidth(double width) {
        this.strokeWidth = Math.max(0.5, Math.min(width, 50.0)); // Limit stroke width
        GraphicsContext currentGC = getGC();
        if (currentGC != null) {
            currentGC.setLineWidth(this.strokeWidth);
        }
    }

    // =====================================
    // GETTERS
    // =====================================

    public DrawingTool getCurrentTool() {
        return currentTool;
    }

    public Color getCurrentColor() {
        return currentColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public double getStrokeWidth() {
        return strokeWidth;
    }

    public double getZoomLevel() {
        return zoomLevel;
    }

    public String getZoomPercentage() {
        return Math.round(zoomLevel * 100) + "%";
    }

    public double getMinZoom() {
        return minZoom;
    }

    public void setMinZoom(double minZoom) {
        this.minZoom = Math.max(0.01, minZoom);
    }

    public double getMaxZoom() {
        return maxZoom;
    }

    public void setMaxZoom(double maxZoom) {
        this.maxZoom = Math.max(1.0, maxZoom);
    }

    public double getZoomStep() {
        return zoomStep;
    }

    public void setZoomStep(double zoomStep) {
        this.zoomStep = Math.max(1.01, zoomStep);
    }

    public void setZoomChangeListener(ZoomChangeListener listener) {
        this.zoomChangeListener = listener;
    }

    // =====================================
    // ZOOM UTILITY METHODS
    // =====================================

    /**
     * Check if can zoom in further
     */
    public boolean canZoomIn() {
        return zoomLevel < maxZoom;
    }

    /**
     * Check if can zoom out further
     */
    public boolean canZoomOut() {
        return zoomLevel > minZoom;
    }

    /**
     * Get actual canvas size accounting for zoom
     */
    public double getActualWidth() {
        return getWidth() * zoomLevel;
    }

    public double getActualHeight() {
        return getHeight() * zoomLevel;
    }

    // =====================================
    // COMMAND HISTORY METHODS
    // =====================================

    /**
     * Get undo description for UI
     */
    public String getUndoDescription() {
        if (commandManager != null) {
            return commandManager.getUndoDescription();
        }
        return "Undo";
    }

    /**
     * Get redo description for UI
     */
    public String getRedoDescription() {
        if (commandManager != null) {
            return commandManager.getRedoDescription();
        }
        return "Redo";
    }

    /**
     * Clear command history
     */
    public void clearCommandHistory() {
        if (commandManager != null) {
            commandManager.clearHistory();
        }
    }

    /**
     * Print command status for debugging
     */
    public void printCommandStatus() {
        if (commandManager != null) {
            commandManager.printStatus();
        } else {
            System.out.println("❌ No command manager connected");
        }
    }

    // =====================================
    // UTILITY METHODS
    // =====================================

    /**
     * Check if coordinates are within canvas bounds
     */
    private boolean isValidCoordinate(double x, double y) {
        return x >= 0 && x <= getWidth() && y >= 0 && y <= getHeight();
    }

    /**
     * Take a snapshot of the current canvas
     */
    public WritableImage snapshot() {
        return snapshot(null, null);
    }

    /**
     * Get the graphics context for external drawing operations
     */
    public GraphicsContext getGraphicsContext2D() {
        // Override the Canvas method to ensure safe access
        try {
            GraphicsContext context = super.getGraphicsContext2D();
            return context;
        } catch (Exception e) {
            System.out.println("⚠️ Graphics context not available: " + e.getMessage());
            return null;
        }
    }

    /**
     * Resize the canvas
     */
    public void resizeCanvas(double width, double height) {
        setWidth(width);
        setHeight(height);

        // Clear and redraw background
        clearCanvas();

        System.out.println("📐 Canvas resized to: " + width + "x" + height);
    }

    // =====================================
    // DEBUG METHODS
    // =====================================

    /**
     * Print canvas status for debugging
     */
    public void printCanvasStatus() {
        System.out.println("🖼️ Canvas Status:");
        System.out.println("  Size: " + getWidth() + "x" + getHeight());
        System.out.println("  Zoom: " + getZoomPercentage());
        System.out.println("  Tool: " + currentTool.getDisplayName());
        System.out.println("  Color: " + currentColor);
        System.out.println("  Stroke Width: " + strokeWidth);
        System.out.println("  Background: " + backgroundColor);
        System.out.println("  Recording Commands: " + recordCommands);
        System.out.println("  Is Drawing: " + isDrawing);
        System.out.println("  Is Initialized: " + isInitialized);
        System.out.println("  Graphics Context: " + (gc != null ? "Available" : "Not Available"));
    }
}