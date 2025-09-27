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
 * Clean Drawing Canvas with working touchpad pan and zoom
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
    // ZOOM AND PAN FIELDS
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
    // COMMAND SYSTEM FIELDS
    // =====================================
    private CommandManager commandManager;
    private StrokeCommand currentStrokeCommand;
    private boolean recordCommands = true;

    // Shape drawing state
    private double shapeStartX, shapeStartY;
    private boolean isDrawingShape = false;

    // Canvas initialization state
    private boolean isInitialized = false;

    // =====================================
    // CONSTRUCTOR
    // =====================================
    public DrawingCanvas(double width, double height) {
        super(width, height);

        // Initialize when ready
        this.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null && !isInitialized) {
                initializeCanvasWhenReady();
            }
        });

        initializeCanvasWhenReady();
        setupEventHandlers();

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
                System.out.println("✅ Canvas graphics context initialized");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Canvas not ready yet: " + e.getMessage());
        }
    }

    private void initializeCanvas() {
        if (gc == null) return;

        // Set initial background
        gc.setFill(backgroundColor);
        gc.fillRect(0, 0, getWidth(), getHeight());

        // Set default drawing properties
        gc.setStroke(currentColor);
        gc.setLineWidth(strokeWidth);
        gc.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
        gc.setLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);
    }

    // =====================================
    // SNAPSHOT METHODS (FIXED FOR BLUR ISSUE)
    // =====================================

    /**
     * Get unscaled snapshot for saving - prevents blur on restore
     */
    public WritableImage getUnscaledSnapshot() {
        try {
            // Save current transform state
            double currentScaleX = getScaleX();
            double currentScaleY = getScaleY();
            double currentTranslateX = getTranslateX();
            double currentTranslateY = getTranslateY();

            // Temporarily reset transforms to get clean snapshot
            setScaleX(1.0);
            setScaleY(1.0);
            setTranslateX(0);
            setTranslateY(0);

            // Take snapshot at base resolution
            WritableImage snapshot = snapshot(null, null);

            // Restore original transforms
            setScaleX(currentScaleX);
            setScaleY(currentScaleY);
            setTranslateX(currentTranslateX);
            setTranslateY(currentTranslateY);

            System.out.println("📸 Unscaled snapshot taken: " + (int)snapshot.getWidth() + "x" + (int)snapshot.getHeight());
            return snapshot;

        } catch (Exception e) {
            System.err.println("Error taking unscaled snapshot: " + e.getMessage());
            // Fallback to regular snapshot
            return snapshot(null, null);
        }
    }

    /**
     * Regular snapshot - preserves existing functionality
     */
    public WritableImage snapshot() {
        return snapshot(null, null);
    }

    private void setupEventHandlers() {
        // Only set up drawing event handlers
        setOnMousePressed(this::handleMousePressed);
        setOnMouseDragged(this::handleMouseDragged);
        setOnMouseReleased(this::handleMouseReleased);

        // Set up touchpad pan and zoom
        setupTouchpadControls();
    }

    // =====================================
    // TOUCHPAD PAN AND ZOOM (FIXED)
    // =====================================

    private void setupTouchpadControls() {
        setOnScroll(event -> {
            double deltaX = event.getDeltaX();
            double deltaY = event.getDeltaY();

            if (event.isControlDown()) {
                // Ctrl+scroll = zoom
                if (deltaY > 0) {
                    zoomIn(event.getX(), event.getY());
                } else {
                    zoomOut(event.getX(), event.getY());
                }
            } else {
                // Regular scroll = pan (touchpad two-finger drag)
                handleTouchpadPan(deltaX, deltaY);
            }
            event.consume();
        });

        // Pinch-to-zoom support
        setOnZoom(event -> {
            handlePinchZoom(event.getX(), event.getY(), event.getZoomFactor());
            event.consume();
        });

        System.out.println("🔍 Touchpad controls initialized");
    }

    /**
     * Handle touchpad panning (two-finger drag)
     */
    private void handleTouchpadPan(double deltaX, double deltaY) {
        double currentTranslateX = getTranslateX();
        double currentTranslateY = getTranslateY();

        // Pan with natural direction
        setTranslateX(currentTranslateX + deltaX);
        setTranslateY(currentTranslateY + deltaY);
    }

    /**
     * Handle pinch-to-zoom
     */
    private void handlePinchZoom(double centerX, double centerY, double zoomFactor) {
        double oldZoom = zoomLevel;
        zoomLevel = Math.max(minZoom, Math.min(maxZoom, zoomLevel * zoomFactor));

        if (zoomLevel != oldZoom) {
            // Calculate translation to keep zoom centered
            double scaleFactor = zoomLevel / oldZoom;

            setScaleX(zoomLevel);
            setScaleY(zoomLevel);

            // Adjust translation to zoom at center point
            double newTranslateX = centerX - (centerX - getTranslateX()) * scaleFactor;
            double newTranslateY = centerY - (centerY - getTranslateY()) * scaleFactor;

            setTranslateX(newTranslateX);
            setTranslateY(newTranslateY);

            fireZoomChanged();
        }
    }

    // =====================================
    // MOUSE EVENT HANDLERS (DRAWING ONLY)
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
                currentGC.setStroke(currentColor);
                currentGC.setLineWidth(strokeWidth);
                currentGC.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
                // Draw initial point
                currentGC.strokeLine(lastX, lastY, lastX + 0.1, lastY + 0.1);
                break;

            case ERASER:
                startStrokeCommand();
                currentGC.setStroke(backgroundColor);
                currentGC.setLineWidth(strokeWidth);
                currentGC.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
                currentGC.strokeLine(lastX, lastY, lastX + 0.1, lastY + 0.1);
                break;

            case RECTANGLE:
            case CIRCLE:
            case LINE:
                shapeStartX = lastX;
                shapeStartY = lastY;
                isDrawingShape = true;
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
                if (currentStrokeCommand != null) {
                    currentStrokeCommand.addPoint(currentX, currentY);
                }
                currentGC.strokeLine(lastX, lastY, currentX, currentY);
                break;

            case ERASER:
                if (currentStrokeCommand != null) {
                    currentStrokeCommand.addPoint(currentX, currentY);
                }
                currentGC.strokeLine(lastX, lastY, currentX, currentY);
                break;
        }

        lastX = currentX;
        lastY = currentY;
    }

    private void handleMouseReleased(MouseEvent event) {
        if (!isDrawing) return;

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
    }

    // =====================================
    // ZOOM METHODS
    // =====================================

    public void zoomIn(double centerX, double centerY) {
        double oldZoom = zoomLevel;
        zoomLevel = Math.min(zoomLevel * zoomStep, maxZoom);

        if (zoomLevel != oldZoom) {
            applyZoom(centerX, centerY, oldZoom);
        }
    }

    public void zoomOut(double centerX, double centerY) {
        double oldZoom = zoomLevel;
        zoomLevel = Math.max(zoomLevel / zoomStep, minZoom);

        if (zoomLevel != oldZoom) {
            applyZoom(centerX, centerY, oldZoom);
        }
    }

    private void applyZoom(double centerX, double centerY, double oldZoom) {
        double scaleFactor = zoomLevel / oldZoom;

        setScaleX(zoomLevel);
        setScaleY(zoomLevel);

        // Keep zoom centered on the specified point
        double newTranslateX = centerX - (centerX - getTranslateX()) * scaleFactor;
        double newTranslateY = centerY - (centerY - getTranslateY()) * scaleFactor;

        setTranslateX(newTranslateX);
        setTranslateY(newTranslateY);

        fireZoomChanged();
    }

    public void zoomIn() {
        zoomIn(getWidth() / 2, getHeight() / 2);
    }

    public void zoomOut() {
        zoomOut(getWidth() / 2, getHeight() / 2);
    }

    public void resetZoom() {
        if (zoomLevel != 1.0) {
            zoomLevel = 1.0;
            setScaleX(1.0);
            setScaleY(1.0);
            setTranslateX(0);
            setTranslateY(0);
            fireZoomChanged();
        }
    }

    public void setZoomLevel(double zoom) {
        double oldZoom = zoomLevel;
        zoomLevel = Math.max(minZoom, Math.min(zoom, maxZoom));

        if (zoomLevel != oldZoom) {
            setScaleX(zoomLevel);
            setScaleY(zoomLevel);
            fireZoomChanged();
        }
    }

    public void fitToWindow() {
        resetZoom();
    }

    private void fireZoomChanged() {
        if (zoomChangeListener != null) {
            zoomChangeListener.onZoomChanged(zoomLevel);
        }
    }

    // =====================================
    // GRAPHICS CONTEXT ACCESS
    // =====================================

    private GraphicsContext getGC() {
        if (gc == null) {
            initializeCanvasWhenReady();
        }
        return gc;
    }

    // =====================================
    // COMMAND SYSTEM METHODS
    // =====================================

    public void setCommandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
        System.out.println("🔗 Command manager connected");
    }

    public void setRecordCommands(boolean record) {
        this.recordCommands = record;
    }

    private void executeCommand(DrawingCommand command) {
        if (commandManager != null && recordCommands) {
            commandManager.executeCommand(command);
        }
    }

    private void startStrokeCommand() {
        if (commandManager != null && recordCommands) {
            currentStrokeCommand = new StrokeCommand(this, currentTool, currentColor, strokeWidth);
            currentStrokeCommand.addPoint(lastX, lastY);
        }
    }

    private void finishStrokeCommand() {
        if (currentStrokeCommand != null) {
            currentStrokeCommand.finishStroke();
            executeCommand(currentStrokeCommand);
            currentStrokeCommand = null;
        }
    }

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

    public void clearCanvasWithUndo() {
        ClearCanvasCommand clearCommand = new ClearCanvasCommand(this);
        executeCommand(clearCommand);
    }

    public boolean undo() {
        if (commandManager != null) {
            return commandManager.undo();
        }
        return false;
    }

    public boolean redo() {
        if (commandManager != null) {
            return commandManager.redo();
        }
        return false;
    }

    public boolean canUndo() {
        return commandManager != null && commandManager.canUndo();
    }

    public boolean canRedo() {
        return commandManager != null && commandManager.canRedo();
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    // =====================================
    // CANVAS MANAGEMENT METHODS
    // =====================================

    public void clearCanvas() {
        GraphicsContext currentGC = getGC();
        if (currentGC == null) return;

        currentGC.clearRect(0, 0, getWidth(), getHeight());
        currentGC.setFill(backgroundColor);
        currentGC.fillRect(0, 0, getWidth(), getHeight());
        System.out.println("🧹 Canvas cleared");
    }

    public void setCanvasBackgroundColor(Color color) {
        this.backgroundColor = color;
        GraphicsContext currentGC = getGC();
        if (currentGC != null) {
            currentGC.save();
            currentGC.setFill(backgroundColor);
            currentGC.fillRect(0, 0, getWidth(), getHeight());
            currentGC.restore();
        }
    }

    // =====================================
    // TOOL AND PROPERTY SETTERS
    // =====================================

    public void setCurrentTool(DrawingTool tool) {
        if (currentStrokeCommand != null) {
            finishStrokeCommand();
        }
        this.currentTool = tool;
        System.out.println("🛠️ Tool: " + tool.getDisplayName());
    }

    public void setCurrentColor(Color color) {
        this.currentColor = color;
        GraphicsContext currentGC = getGC();
        if (currentGC != null) {
            currentGC.setStroke(color);
        }
    }

    public void setStrokeWidth(double width) {
        this.strokeWidth = Math.max(0.5, Math.min(width, 50.0));
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

    public boolean canZoomIn() {
        return zoomLevel < maxZoom;
    }

    public boolean canZoomOut() {
        return zoomLevel > minZoom;
    }

    public double getActualWidth() {
        return getWidth() * zoomLevel;
    }

    public double getActualHeight() {
        return getHeight() * zoomLevel;
    }

    // =====================================
    // COMMAND HISTORY METHODS
    // =====================================

    public String getUndoDescription() {
        if (commandManager != null) {
            return commandManager.getUndoDescription();
        }
        return "Undo";
    }

    public String getRedoDescription() {
        if (commandManager != null) {
            return commandManager.getRedoDescription();
        }
        return "Redo";
    }

    public void clearCommandHistory() {
        if (commandManager != null) {
            commandManager.clearHistory();
        }
    }

    // =====================================
    // UTILITY METHODS
    // =====================================

    private boolean isValidCoordinate(double x, double y) {
        return x >= 0 && x <= getWidth() && y >= 0 && y <= getHeight();
    }

    public GraphicsContext getGraphicsContext2D() {
        try {
            return super.getGraphicsContext2D();
        } catch (Exception e) {
            System.out.println("⚠️ Graphics context not available: " + e.getMessage());
            return null;
        }
    }

    public void resizeCanvas(double width, double height) {
        setWidth(width);
        setHeight(height);
        clearCanvas();
        System.out.println("📐 Canvas resized: " + width + "x" + height);
    }

    // =====================================
    // DEBUG METHODS
    // =====================================

    public void printCanvasStatus() {
        System.out.println("🖼️ Canvas Status:");
        System.out.println("  Size: " + getWidth() + "x" + getHeight());
        System.out.println("  Zoom: " + getZoomPercentage());
        System.out.println("  Tool: " + currentTool.getDisplayName());
        System.out.println("  Color: " + currentColor);
        System.out.println("  Stroke Width: " + strokeWidth);
        System.out.println("  Background: " + backgroundColor);
        System.out.println("  Is Drawing: " + isDrawing);
        System.out.println("  Is Initialized: " + isInitialized);
        System.out.println("  Graphics Context: " + (gc != null ? "Available" : "Not Available"));
        System.out.println("  Translate: " + getTranslateX() + ", " + getTranslateY());
        System.out.println("  Scale: " + getScaleX() + ", " + getScaleY());
    }
}