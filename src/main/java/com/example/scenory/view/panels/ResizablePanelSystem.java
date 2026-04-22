package com.example.scenory.view.panels;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;

/**
 * Resizable Panel System - Makes all panels adjustable with drag handles
 * Provides smooth resizing for left and right panels
 */
public class ResizablePanelSystem {

    private BorderPane mainLayout;
    private EnhancedDualPanelGroup leftPanelGroup;
    private CollapsibleSceneConstructor rightSceneConstructor;
    private Region centerCanvas;

    // Resize state
    private boolean isResizingLeft = false;
    private boolean isResizingRight = false;
    private double lastMouseX = 0;

    // Size constraints
    private double minLeftWidth = 50;
    private double maxLeftWidth = 600;
    private double minRightWidth = 60;
    private double maxRightWidth = 500;
    private double minCenterWidth = 400;

    public ResizablePanelSystem(BorderPane mainLayout) {
        this.mainLayout = mainLayout;
        setupResizeHandles();
        System.out.println("🔧 ResizablePanelSystem initialized");
    }

    public void setComponents(EnhancedDualPanelGroup leftPanelGroup,
                              Region centerCanvas,
                              CollapsibleSceneConstructor rightSceneConstructor) {
        this.leftPanelGroup = leftPanelGroup;
        this.centerCanvas = centerCanvas;
        this.rightSceneConstructor = rightSceneConstructor;

        attachResizeHandles();
        setupResizeHandlers();

        System.out.println("🔗 Components attached to ResizablePanelSystem");
    }

    // Call this after the scene is fully loaded to ensure proper layout
    public void finalizeLayout() {
        // Force layout pass
        mainLayout.layout();
        
        // Recalculate dimensions
        recalculatePanelDimensions();
        
        System.out.println("✅ Layout finalized");
    }

    // Force a complete layout recalculation (called on window restore)
    public void forceLayoutRecalculation() {
        if (mainLayout == null) return;
        
        // Invalidate layout for all components
        mainLayout.requestLayout();
        if (leftPanelGroup != null) {
            leftPanelGroup.requestLayout();
        }
        if (rightSceneConstructor != null) {
            rightSceneConstructor.requestLayout();
        }
        if (centerCanvas != null) {
            centerCanvas.requestLayout();
        }
        
        // Recalculate dimensions
        recalculatePanelDimensions();
        
        System.out.println("🔄 Layout recalculation forced");
    }

    private void setupResizeHandles() {
        // Resize handles are no longer needed - BorderPane handles layout automatically
        System.out.println("🔧 ResizablePanelSystem initialized (resize handles disabled)");
    }

    private void attachResizeHandles() {
        // Set left panel directly without wrapper
        if (leftPanelGroup != null) {
            mainLayout.setLeft(leftPanelGroup);
        }

        // Set right panel directly without wrapper
        if (rightSceneConstructor != null) {
            mainLayout.setRight(rightSceneConstructor);
        }

        // Add window resize listener to recalculate panel dimensions
        mainLayout.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null && newScene.getWindow() != null) {
                newScene.getWindow().widthProperty().addListener((obs2, oldVal, newVal) -> {
                    forceLayoutRecalculation();
                });
                newScene.getWindow().heightProperty().addListener((obs2, oldVal, newVal) -> {
                    forceLayoutRecalculation();
                });
            }
        });
    }

    private void setupResizeHandlers() {
        // Resize handlers are no longer needed - BorderPane handles layout automatically
    }

    // Preference saving methods
    private void saveLeftPanelWidth() {
        if (leftPanelGroup != null) {
            // Save to user preferences
            double width = leftPanelGroup.getPrefWidth();
            System.out.println("💾 Saved left panel width: " + width);
            // UserPreferences.getInstance().setDouble("left_panel_width", width);
        }
    }

    private void saveRightPanelWidth() {
        if (rightSceneConstructor != null) {
            // Save to user preferences
            double width = rightSceneConstructor.getPrefWidth();
            System.out.println("💾 Saved right panel width: " + width);
            // UserPreferences.getInstance().setDouble("right_panel_width", width);
        }
    }

    // Public API for setting constraints
    public void setLeftPanelConstraints(double minWidth, double maxWidth) {
        this.minLeftWidth = minWidth;
        this.maxLeftWidth = maxWidth;
    }

    public void setRightPanelConstraints(double minWidth, double maxWidth) {
        this.minRightWidth = minWidth;
        this.maxRightWidth = maxWidth;
    }

    public void setMinCenterWidth(double minWidth) {
        this.minCenterWidth = minWidth;
    }

    // Methods to restore saved sizes
    public void restorePanelSizes() {
        // BorderPane handles layout automatically
        // Just ensure panels have proper preferred sizes
        try {
            double leftWidth = 250; // Default
            double rightWidth = 300; // Default

            if (leftPanelGroup != null) {
                leftPanelGroup.setPrefWidth(leftWidth);
            }

            if (rightSceneConstructor != null) {
                rightSceneConstructor.setPrefWidth(rightWidth);
                rightSceneConstructor.setExpandedWidth(rightWidth);
            }

            System.out.println("📐 Restored panel sizes: left=" + leftWidth + ", right=" + rightWidth);

        } catch (Exception e) {
            System.err.println("❌ Error restoring panel sizes: " + e.getMessage());
        }
    }

    // Debug method
    public void printPanelSizes() {
        double leftWidth = leftPanelGroup != null ? leftPanelGroup.getPrefWidth() : 0;
        double rightWidth = rightSceneConstructor != null ? rightSceneConstructor.getPrefWidth() : 0;
        double centerWidth = mainLayout.getWidth() - leftWidth - rightWidth;

        System.out.println("📐 Panel sizes: Left=" + leftWidth +
                ", Center=" + centerWidth +
                ", Right=" + rightWidth);
    }

    // Recalculate panel dimensions when window is resized
    private void recalculatePanelDimensions() {
        if (mainLayout == null) return;

        double mainHeight = mainLayout.getHeight();

        if (mainHeight <= 0) return;

        // BorderPane automatically manages layout, just ensure heights are set
        if (leftPanelGroup != null) {
            leftPanelGroup.setPrefHeight(mainHeight);
        }

        if (rightSceneConstructor != null) {
            rightSceneConstructor.setPrefHeight(mainHeight);
        }

        System.out.println("📐 Recalculated panel dimensions: height=" + mainHeight);
    }
}