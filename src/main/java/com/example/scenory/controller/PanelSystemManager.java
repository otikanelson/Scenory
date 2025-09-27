package com.example.scenory.controller;

import com.example.scenory.view.panels.*;
import com.example.scenory.database.PanelLayoutDAO;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

/**
 * Manages the complex panel system layout and state
 */
public class PanelSystemManager {

    private final BorderPane mainBorderPane;
    private final Consumer<String> statusUpdater;

    // Panel components
    private EnhancedDualPanelGroup leftPanelGroup;
    private SceneConstructor rightSceneConstructor;  // FIXED: Use new simple version
    private ResizablePanelSystem resizableSystem;
    private PanelLayoutDAO.PanelLayout currentLayout;

    // UI component references
    private CheckMenuItem showLeftPanelMenuItem;
    private CheckMenuItem showRightPanelMenuItem;

    public PanelSystemManager(BorderPane mainBorderPane, Consumer<String> statusUpdater) {
        this.mainBorderPane = mainBorderPane;
        this.statusUpdater = statusUpdater;
    }

    public void initializePanelSystem(ToolSelectionPanel toolPanel, VBox fileStructureContent,
                                      VBox sceneConstructorContent, Region canvasArea) {
        System.out.println("🔧 Setting up enhanced dual panel system...");

        // Create EnhancedDualPanelGroup
        leftPanelGroup = new EnhancedDualPanelGroup();
        leftPanelGroup.setToolsContent(toolPanel);
        leftPanelGroup.setStructureContent(fileStructureContent);
        System.out.println("📋 EnhancedDualPanelGroup created with Tools and Structure");

        // FIXED: Create simplified scene constructor panel
        rightSceneConstructor = new SceneConstructor("Scene Panels", sceneConstructorContent);
        System.out.println("🎬 SimpleCollapsedSceneConstructor created");

        // Create resizable panel system
        resizableSystem = new ResizablePanelSystem(mainBorderPane);

        // Replace the main layout panels
        replaceMainLayoutWithEnhancedPanelSystem(canvasArea);

        System.out.println("✅ Enhanced dual panel system initialized");
    }

    private void replaceMainLayoutWithEnhancedPanelSystem(Region canvasArea) {
        if (mainBorderPane == null) {
            System.err.println("❌ mainBorderPane is null - check FXML binding");
            return;
        }

        // Clear existing content
        mainBorderPane.setLeft(null);
        mainBorderPane.setRight(null);

        // Set up resizable panel system with provided canvas area
        if (resizableSystem != null && canvasArea != null) {
            resizableSystem.setComponents(leftPanelGroup, canvasArea, rightSceneConstructor);
            resizableSystem.restorePanelSizes();
        } else {
            System.err.println("❌ Cannot setup resizable system - missing components");
        }

        System.out.println("🔄 Main layout replaced with enhanced panel system");
    }

    public void setupLayoutPersistence() {
        try {
            currentLayout = PanelLayoutDAO.loadLayout("default", "default");
            applyLayout(currentLayout);
        } catch (Exception e) {
            System.err.println("❌ Error loading panel layout: " + e.getMessage());
            currentLayout = createDefaultPanelLayout();
            applyLayout(currentLayout);
        }

        // Listen to panel state changes
        if (leftPanelGroup != null) {
            leftPanelGroup.toolsExpandedProperty().addListener((obs, oldVal, newVal) -> {
                try {
                    saveCurrentLayout();
                } catch (Exception e) {
                    System.err.println("❌ Error saving panel layout: " + e.getMessage());
                }
            });
            leftPanelGroup.structureExpandedProperty().addListener((obs, oldVal, newVal) -> {
                try {
                    saveCurrentLayout();
                } catch (Exception e) {
                    System.err.println("❌ Error saving panel layout: " + e.getMessage());
                }
            });
        }

        if (rightSceneConstructor != null) {
            rightSceneConstructor.collapsedProperty().addListener((obs, oldVal, newVal) -> {
                try {
                    saveCurrentLayout();
                } catch (Exception e) {
                    System.err.println("❌ Error saving panel layout: " + e.getMessage());
                }
            });
        }

        System.out.println("💾 Layout persistence setup complete");
    }

    private PanelLayoutDAO.PanelLayout createDefaultPanelLayout() {
        PanelLayoutDAO.PanelLayout layout = new PanelLayoutDAO.PanelLayout();
        // FIXED: Start with all panels collapsed
        layout.setToolPanelCollapsed(true);
        layout.setFileStructureCollapsed(true);
        layout.setSceneConstructorVisible(true);
        layout.setSceneConstructorPosition("RIGHT");
        layout.setLeftPanelWidth(250.0);
        layout.setRightPanelWidth(300.0);
        return layout;
    }

    private void applyLayout(PanelLayoutDAO.PanelLayout layout) {
        if (layout == null) return;

        try {
            // Apply scene constructor state
            if (rightSceneConstructor != null) {
                rightSceneConstructor.setCollapsed(!layout.isSceneConstructorVisible());
            }

            // FIXED: Apply dual panel state - only one at a time
            if (leftPanelGroup != null) {
                if (!layout.isToolPanelCollapsed()) {
                    leftPanelGroup.expandToolsTab();
                } else if (!layout.isFileStructureCollapsed()) {
                    leftPanelGroup.expandStructureTab();
                } else {
                    leftPanelGroup.collapseAll();
                }
            }

            System.out.println("📐 Layout applied successfully");

        } catch (Exception e) {
            System.err.println("❌ Error applying layout: " + e.getMessage());
        }
    }

    private void saveCurrentLayout() {
        if (currentLayout == null) {
            currentLayout = createDefaultPanelLayout();
        }

        try {
            // Save dual panel state
            if (leftPanelGroup != null) {
                currentLayout.setToolPanelCollapsed(!leftPanelGroup.isToolsExpanded());
                currentLayout.setFileStructureCollapsed(!leftPanelGroup.isStructureExpanded());
            }

            // Save scene constructor state
            if (rightSceneConstructor != null) {
                currentLayout.setSceneConstructorVisible(!rightSceneConstructor.isCollapsed());
                currentLayout.setSceneConstructorPosition("RIGHT");
            }

            // Save to database
            PanelLayoutDAO.saveLayout("default", "default", currentLayout);

        } catch (Exception e) {
            System.err.println("❌ Error saving layout: " + e.getMessage());
        }
    }

    // Panel toggle methods
    public void toggleLeftPanel() {
        if (leftPanelGroup != null) {
            leftPanelGroup.collapseAll();

            if (showLeftPanelMenuItem != null) {
                showLeftPanelMenuItem.setSelected(leftPanelGroup.isExpanded());
            }

            statusUpdater.accept(leftPanelGroup.isExpanded() ? "Left panel expanded" : "Left panel collapsed");
        }
    }

    public void toggleRightPanel() {
        if (rightSceneConstructor != null) {
            rightSceneConstructor.toggleCollapse();

            if (showRightPanelMenuItem != null) {
                showRightPanelMenuItem.setSelected(!rightSceneConstructor.isCollapsed());
            }

            statusUpdater.accept(rightSceneConstructor.isCollapsed() ? "Scene constructor collapsed" : "Scene constructor expanded");
        }
    }

    public void showToolsPanel() {
        if (leftPanelGroup != null) {
            // FIXED: Close structure panel first, then open tools
            leftPanelGroup.collapseStructureTab();
            leftPanelGroup.expandToolsTab();
            statusUpdater.accept("Tools panel active");
        }
    }

    public void showStructurePanel() {
        if (leftPanelGroup != null) {
            // FIXED: Close tools panel first, then open structure
            leftPanelGroup.collapseToolsTab();
            leftPanelGroup.expandStructureTab();
            statusUpdater.accept("Structure panel active");
        }
    }

    public String getCurrentPanelState() {
        if (leftPanelGroup != null) {
            String state = leftPanelGroup.getCurrentState();
            System.out.println("📋 Current panel state: " + state);
            return state;
        }
        return "No panels";
    }

    public void setNavigationCallbacks(Runnable onPrevious, Runnable onNext) {
        if (rightSceneConstructor != null) {
            rightSceneConstructor.setOnPreviousPanel(onPrevious);
            rightSceneConstructor.setOnNextPanel(onNext);
        }
    }

    public void setMenuItems(CheckMenuItem showLeftPanelMenuItem, CheckMenuItem showRightPanelMenuItem) {
        this.showLeftPanelMenuItem = showLeftPanelMenuItem;
        this.showRightPanelMenuItem = showRightPanelMenuItem;
    }

    public void updateSceneInfo(String sceneName, String panelName, int panelIndex, int totalPanels) {
        if (rightSceneConstructor != null) {
            rightSceneConstructor.updateSceneInfo(sceneName, panelName, panelIndex, totalPanels);
        }
    }

    // FIXED: Add rich text display method
    public void updateRichTextDisplay(String richText) {
        if (rightSceneConstructor != null) {
            rightSceneConstructor.updateRichTextDisplay(richText);
        }
    }

    // Getters
    public EnhancedDualPanelGroup getLeftPanelGroup() {
        return leftPanelGroup;
    }

    public SceneConstructor getRightSceneConstructor() {  // FIXED: Updated return type
        return rightSceneConstructor;
    }
}
