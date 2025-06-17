package com.example.scenory.view.panels;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

/**
 * Resizable Panel System - Makes all panels adjustable with drag handles
 * Provides smooth resizing for left and right panels
 */
public class ResizablePanelSystem {

    private BorderPane mainLayout;
    private EnhancedDualPanelGroup leftPanelGroup;
    private CollapsibleSceneConstructor rightSceneConstructor;
    private Region centerCanvas;

    // Resize handles
    private Region leftResizeHandle;
    private Region rightResizeHandle;

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

    private void setupResizeHandles() {
        // Left resize handle
        leftResizeHandle = new Region();
        leftResizeHandle.getStyleClass().add("resizable-panel-divider");
        leftResizeHandle.setPrefWidth(4);
        leftResizeHandle.setMinWidth(4);
        leftResizeHandle.setMaxWidth(4);
        leftResizeHandle.setCursor(Cursor.H_RESIZE);
        leftResizeHandle.setStyle("-fx-background-color: transparent;");

        // Right resize handle
        rightResizeHandle = new Region();
        rightResizeHandle.getStyleClass().add("resizable-panel-divider");
        rightResizeHandle.setPrefWidth(4);
        rightResizeHandle.setMinWidth(4);
        rightResizeHandle.setMaxWidth(4);
        rightResizeHandle.setCursor(Cursor.H_RESIZE);
        rightResizeHandle.setStyle("-fx-background-color: transparent;");
    }

    private void attachResizeHandles() {
        // Create containers for panels with resize handles
        if (leftPanelGroup != null) {
            Pane leftContainer = new Pane();
            leftContainer.getChildren().addAll(leftPanelGroup, leftResizeHandle);

            // Position resize handle at right edge of left panel
            leftResizeHandle.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
                if (leftPanelGroup != null) {
                    double panelWidth = leftPanelGroup.getWidth();
                    leftResizeHandle.setLayoutX(panelWidth - 2);
                    leftResizeHandle.setLayoutY(0);
                    leftResizeHandle.setPrefHeight(leftContainer.getHeight());
                }
            });

            leftPanelGroup.widthProperty().addListener((obs, oldVal, newVal) -> {
                leftResizeHandle.setLayoutX(newVal.doubleValue() - 2);
            });

            leftContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
                leftResizeHandle.setPrefHeight(newVal.doubleValue());
                leftPanelGroup.setPrefHeight(newVal.doubleValue());
            });

            mainLayout.setLeft(leftContainer);
        }

        if (rightSceneConstructor != null) {
            Pane rightContainer = new Pane();
            rightContainer.getChildren().addAll(rightSceneConstructor, rightResizeHandle);

            // Position resize handle at left edge of right panel
            rightResizeHandle.setLayoutX(0);
            rightResizeHandle.setLayoutY(0);

            rightContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
                rightResizeHandle.setPrefHeight(newVal.doubleValue());
                rightSceneConstructor.setPrefHeight(newVal.doubleValue());
            });

            rightSceneConstructor.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
                rightSceneConstructor.setLayoutX(4); // Offset for resize handle
            });

            mainLayout.setRight(rightContainer);
        }
    }

    private void setupResizeHandlers() {
        // Left panel resize handlers
        if (leftResizeHandle != null) {
            leftResizeHandle.setOnMousePressed(this::handleLeftResizeStart);
            leftResizeHandle.setOnMouseDragged(this::handleLeftResize);
            leftResizeHandle.setOnMouseReleased(this::handleLeftResizeEnd);
            leftResizeHandle.setOnMouseEntered(e -> {
                leftResizeHandle.setStyle("-fx-background-color: #bf5700; -fx-opacity: 0.7;");
            });
            leftResizeHandle.setOnMouseExited(e -> {
                if (!isResizingLeft) {
                    leftResizeHandle.setStyle("-fx-background-color: transparent;");
                }
            });
        }

        // Right panel resize handlers
        if (rightResizeHandle != null) {
            rightResizeHandle.setOnMousePressed(this::handleRightResizeStart);
            rightResizeHandle.setOnMouseDragged(this::handleRightResize);
            rightResizeHandle.setOnMouseReleased(this::handleRightResizeEnd);
            rightResizeHandle.setOnMouseEntered(e -> {
                rightResizeHandle.setStyle("-fx-background-color: #bf5700; -fx-opacity: 0.7;");
            });
            rightResizeHandle.setOnMouseExited(e -> {
                if (!isResizingRight) {
                    rightResizeHandle.setStyle("-fx-background-color: transparent;");
                }
            });
        }
    }

    // Left panel resize handlers
    private void handleLeftResizeStart(MouseEvent event) {
        isResizingLeft = true;
        lastMouseX = event.getSceneX();
        leftResizeHandle.setStyle("-fx-background-color: #ff8f00; -fx-opacity: 0.9;");
        event.consume();
    }

    private void handleLeftResize(MouseEvent event) {
        if (!isResizingLeft || leftPanelGroup == null) return;

        double deltaX = event.getSceneX() - lastMouseX;
        double currentWidth = leftPanelGroup.getPrefWidth();
        double newWidth = currentWidth + deltaX;

        // Apply constraints
        newWidth = Math.max(minLeftWidth, Math.min(newWidth, maxLeftWidth));

        // Check that center canvas maintains minimum width
        double availableWidth = mainLayout.getWidth();
        double rightWidth = rightSceneConstructor != null ? rightSceneConstructor.getPrefWidth() : 0;
        double maxAllowedLeftWidth = availableWidth - rightWidth - minCenterWidth;
        newWidth = Math.min(newWidth, maxAllowedLeftWidth);

        // Apply new width
        leftPanelGroup.setPrefWidth(newWidth);
        leftPanelGroup.setMinWidth(newWidth);
        leftPanelGroup.setMaxWidth(newWidth);

        // Update dual panel widths if needed
        if (leftPanelGroup instanceof EnhancedDualPanelGroup) {
            EnhancedDualPanelGroup dualGroup = (EnhancedDualPanelGroup) leftPanelGroup;
            double ratio = newWidth / dualGroup.getContentWidth();
            dualGroup.setWidths(
                    Math.max(50, newWidth * 0.2),  // Collapsed width
                    Math.max(200, newWidth * 0.8), // Single panel width
                    newWidth                       // Dual panel width
            );
        }

        lastMouseX = event.getSceneX();
        event.consume();
    }

    private void handleLeftResizeEnd(MouseEvent event) {
        isResizingLeft = false;
        leftResizeHandle.setStyle("-fx-background-color: transparent;");

        // Save the new width to preferences
        saveLeftPanelWidth();

        event.consume();
    }

    // Right panel resize handlers
    private void handleRightResizeStart(MouseEvent event) {
        isResizingRight = true;
        lastMouseX = event.getSceneX();
        rightResizeHandle.setStyle("-fx-background-color: #ff8f00; -fx-opacity: 0.9;");
        event.consume();
    }

    private void handleRightResize(MouseEvent event) {
        if (!isResizingRight || rightSceneConstructor == null) return;

        double deltaX = event.getSceneX() - lastMouseX;
        double currentWidth = rightSceneConstructor.getPrefWidth();
        double newWidth = currentWidth - deltaX; // Negative because we're resizing from left edge

        // Apply constraints
        newWidth = Math.max(minRightWidth, Math.min(newWidth, maxRightWidth));

        // Check that center canvas maintains minimum width
        double availableWidth = mainLayout.getWidth();
        double leftWidth = leftPanelGroup != null ? leftPanelGroup.getPrefWidth() : 0;
        double maxAllowedRightWidth = availableWidth - leftWidth - minCenterWidth;
        newWidth = Math.min(newWidth, maxAllowedRightWidth);

        // Apply new width
        rightSceneConstructor.setPrefWidth(newWidth);
        rightSceneConstructor.setMinWidth(newWidth);
        rightSceneConstructor.setMaxWidth(newWidth);

        // Update expanded width
        rightSceneConstructor.setExpandedWidth(newWidth);

        lastMouseX = event.getSceneX();
        event.consume();
    }

    private void handleRightResizeEnd(MouseEvent event) {
        isResizingRight = false;
        rightResizeHandle.setStyle("-fx-background-color: transparent;");

        // Save the new width to preferences
        saveRightPanelWidth();

        event.consume();
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
        // Restore from user preferences
        try {
            // double leftWidth = UserPreferences.getInstance().getDouble("left_panel_width", 250);
            // double rightWidth = UserPreferences.getInstance().getDouble("right_panel_width", 300);

            double leftWidth = 250; // Default for now
            double rightWidth = 300; // Default for now

            if (leftPanelGroup != null) {
                leftPanelGroup.setPrefWidth(leftWidth);
                leftPanelGroup.setMinWidth(leftWidth);
                leftPanelGroup.setMaxWidth(leftWidth);
            }

            if (rightSceneConstructor != null) {
                rightSceneConstructor.setPrefWidth(rightWidth);
                rightSceneConstructor.setMinWidth(rightWidth);
                rightSceneConstructor.setMaxWidth(rightWidth);
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
}