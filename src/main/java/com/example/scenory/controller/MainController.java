package com.example.scenory.controller;

import com.example.scenory.model.*;
import com.example.scenory.enums.DrawingTool;
import com.example.scenory.view.components.DrawingCanvas;
import com.example.scenory.view.panels.EnhancedDualPanelGroup;
import com.example.scenory.view.panels.ToolSelectionPanel;
import com.example.scenory.commands.CommandManager;
import com.example.scenory.input.KeyboardShortcutManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.input.KeyCode;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Refactored MainController - now much cleaner and focused
 * Delegates responsibilities to specialized managers
 */
public class MainController implements Initializable {

    // =====================================
    // FXML UI Components
    // =====================================
    @FXML private AnchorPane canvasContainer;
    @FXML private StackPane canvasStackPane;
    @FXML private Label statusLabel, canvasSizeLabel, zoomLabel;
    @FXML private BorderPane mainBorderPane;
    @FXML private CheckMenuItem showLeftPanelMenuItem, showRightPanelMenuItem;
    @FXML private MenuItem undoMenuItem, redoMenuItem;

    // =====================================
    // MANAGERS - The core of our refactoring
    // =====================================
    private ProjectManager projectManager;
    private PanelNavigator panelNavigator;
    private UIManager uiManager;
    private ToolManager toolManager;
    private PanelSystemManager panelSystemManager;

    // =====================================
    // CORE COMPONENTS
    // =====================================
    private DrawingCanvas drawingCanvas;
    private CommandManager commandManager;
    private KeyboardShortcutManager shortcutManager;
    private ToolSelectionPanel toolSelectionPanel;

    // UI References
    private TreeView<Object> sceneTreeView;
    private GridPane thumbnailGrid;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("🚀 Initializing Refactored MainController...");

        initializeManagers();
        initializeCanvas();
        initializeCommandSystem();
        initializePanelSystem();
        setupKeyboardShortcuts();

        System.out.println("✅ Refactored MainController initialized successfully");
    }

    // =====================================
    // INITIALIZATION METHODS
    // =====================================

    private void initializeManagers() {
        // Create project manager with callbacks
        projectManager = new ProjectManager(
                this::updateStatus,
                this::refreshAllUI
        );

        // Panel system manager
        panelSystemManager = new PanelSystemManager(mainBorderPane, this::updateStatus);

        System.out.println("📋 Managers initialized");
    }

    private void initializeCanvas() {
        drawingCanvas = new DrawingCanvas(800, 600);

        // Set up zoom change listener
        drawingCanvas.setZoomChangeListener(newZoomLevel -> {
            if (zoomLabel != null) {
                zoomLabel.setText("🔍 " + Math.round(newZoomLevel * 100) + "%");
            }
        });

        // Add canvas to container
        if (canvasContainer != null) {
            canvasContainer.getChildren().clear();
            canvasContainer.getChildren().add(drawingCanvas);
            centerCanvas();

            // MOVE SCROLL EVENTS TO CONTAINER LEVEL
            setupContainerScrollEvents();
        }

        updateCanvasSizeLabel();
        System.out.println("🖼️ Canvas initialized: 800x600");
    }

    private void setupContainerScrollEvents() {
        // Set scroll events on the container, not the canvas
        canvasContainer.setOnScroll(event -> {
            double deltaX = event.getDeltaX();
            double deltaY = event.getDeltaY();

            if (event.isControlDown()) {
                // Ctrl+scroll = zoom
                if (deltaY > 0) {
                    drawingCanvas.zoomIn(event.getX(), event.getY());
                } else {
                    drawingCanvas.zoomOut(event.getX(), event.getY());
                }
            } else {
                // Regular scroll = pan
                double currentTranslateX = drawingCanvas.getTranslateX();
                double currentTranslateY = drawingCanvas.getTranslateY();

                drawingCanvas.setTranslateX(currentTranslateX + deltaX);
                drawingCanvas.setTranslateY(currentTranslateY + deltaY);
            }
            event.consume();
        });

        // Pinch-to-zoom on container
        canvasContainer.setOnZoom(event -> {
            double zoomFactor = event.getZoomFactor();
            double centerX = event.getX();
            double centerY = event.getY();

            double currentZoom = drawingCanvas.getZoomLevel();
            double newZoom = currentZoom * zoomFactor;
            drawingCanvas.setZoomLevel(newZoom);

            event.consume();
        });
    }

    // In DrawingCanvas.java - REMOVE setupZoomControls() method or make it empty:
    public void setupZoomControls() {
        // Events now handled by parent container
        System.out.println("🔍 Zoom controls delegated to parent container");
    }

    private void initializeCommandSystem() {
        commandManager = new CommandManager();
        commandManager.setMaxHistorySize(100);
        commandManager.setMergeConsecutiveStrokes(true);

        // Connect command manager to canvas
        drawingCanvas.setCommandManager(commandManager);

        // Setup undo/redo menu binding
        setupUndoRedoMenuBinding();

        System.out.println("🔧 Command system initialized");
    }

    private void initializePanelSystem() {
        // Create tool selection panel
        toolSelectionPanel = new ToolSelectionPanel();

        // Create file structure content
        VBox fileStructureContent = createFileStructureContent();

        // Create scene constructor content with thumbnail grid
        VBox sceneConstructorContent = createSceneConstructorContent();

        // Initialize panel system
        panelSystemManager.initializePanelSystem(
                toolSelectionPanel,
                fileStructureContent,
                sceneConstructorContent,
                canvasStackPane  // Pass the canvas area as Region
        );

        // Setup navigation callbacks
        panelSystemManager.setNavigationCallbacks(
                this::previousPanel,
                this::nextPanel
        );

        // Set menu items for panel visibility
        panelSystemManager.setMenuItems(showLeftPanelMenuItem, showRightPanelMenuItem);

        // Setup layout persistence
        panelSystemManager.setupLayoutPersistence();

        // Create remaining managers now that we have all components
        createRemainingManagers();

        System.out.println("🎛️ Panel system initialized");
    }

    private void createRemainingManagers() {
        // Create panel navigator
        panelNavigator = new PanelNavigator(
                projectManager,
                drawingCanvas,
                commandManager,
                this::updateStatus,
                this::refreshAllUI
        );

        // Create tool manager
        toolManager = new ToolManager(
                drawingCanvas,
                toolSelectionPanel,
                this::updateStatus
        );

        // FIXED: Set up thumbnail update callback
        toolManager.setThumbnailUpdateCallback(() -> {
            // Save current panel drawing (which generates thumbnails)
            panelNavigator.saveCurrentPanelDrawing();
            // Refresh UI to show updated thumbnails
            refreshAllUI();
        });

        // Create UI manager
        uiManager = new UIManager(projectManager, panelNavigator);
        uiManager.setUIComponents(sceneTreeView, thumbnailGrid, statusLabel, canvasSizeLabel, zoomLabel);

        // FIXED: Set up enhanced dual panel behavior (only one at a time)
        setupMutuallyExclusivePanels();

        System.out.println("🔗 All managers created and connected");
    }

    private void setupMutuallyExclusivePanels() {
        EnhancedDualPanelGroup leftPanelGroup = panelSystemManager.getLeftPanelGroup();
        if (leftPanelGroup != null) {
            // Override the click handlers to ensure mutual exclusivity
            leftPanelGroup.toolsExpandedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal && leftPanelGroup.isStructureExpanded()) {
                    // If tools is being expanded and structure is open, close structure
                    leftPanelGroup.collapseStructureTab();
                }
            });

            leftPanelGroup.structureExpandedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal && leftPanelGroup.isToolsExpanded()) {
                    // If structure is being expanded and tools is open, close tools
                    leftPanelGroup.collapseToolsTab();
                }
            });
        }
    }

    private VBox createFileStructureContent() {
        VBox content = new VBox(8);
        content.getStyleClass().add("file-structure-content");

        // Create tree view
        sceneTreeView = new TreeView<>();
        sceneTreeView.getStyleClass().add("resizable-tree-view");

        // Header with buttons
        HBox header = createFileStructureHeader();

        ScrollPane treeScrollPane = new ScrollPane(sceneTreeView);
        treeScrollPane.setFitToWidth(true);
        treeScrollPane.getStyleClass().add("invisible-scroll-pane");
        VBox.setVgrow(treeScrollPane, Priority.ALWAYS);

        content.getChildren().addAll(header, treeScrollPane);
        return content;
    }

    private HBox createFileStructureHeader() {
        HBox header = new HBox(6);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label headerLabel = new Label("📋 Project Structure");
        headerLabel.getStyleClass().add("section-header");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Management buttons
        Button addSceneButton = new Button("+");
        addSceneButton.getStyleClass().addAll("mini-button", "add-button");
        addSceneButton.setOnAction(e -> createNewScene());
        Tooltip.install(addSceneButton, new Tooltip("Add Scene"));

        Button deleteSceneButton = new Button("−");
        deleteSceneButton.getStyleClass().addAll("mini-button", "delete-button");
        deleteSceneButton.setOnAction(e -> deleteCurrentScene());
        Tooltip.install(deleteSceneButton, new Tooltip("Delete Scene"));

        Button addPanelButton = new Button("📄");
        addPanelButton.getStyleClass().addAll("mini-button", "add-button");
        addPanelButton.setOnAction(e -> createNewPanel());
        Tooltip.install(addPanelButton, new Tooltip("Add Panel"));

        Button deletePanelButton = new Button("🗑");
        deletePanelButton.getStyleClass().addAll("mini-button", "delete-button");
        deletePanelButton.setOnAction(e -> deleteCurrentPanel());
        Tooltip.install(deletePanelButton, new Tooltip("Delete Panel"));

        header.getChildren().addAll(headerLabel, spacer, addSceneButton, deleteSceneButton,
                new Separator(javafx.geometry.Orientation.VERTICAL),
                addPanelButton, deletePanelButton);

        return header;
    }

    private VBox createSceneConstructorContent() {
        VBox content = new VBox(8);
        content.getStyleClass().add("scene-constructor-content");

        // Create and store reference to thumbnail grid
        thumbnailGrid = new GridPane();
        thumbnailGrid.setHgap(8);
        thumbnailGrid.setVgap(8);

        VBox thumbnailContainer = new VBox(8);
        thumbnailContainer.getStyleClass().add("thumbnail-container");
        thumbnailContainer.getChildren().add(thumbnailGrid);

        content.getChildren().add(thumbnailContainer);
        return content;
    }

    private void setupUndoRedoMenuBinding() {
        if (commandManager == null) return;

        // Bind menu items to command manager properties
        if (undoMenuItem != null) {
            undoMenuItem.disableProperty().bind(commandManager.canUndoProperty().not());
            undoMenuItem.textProperty().bind(commandManager.undoDescriptionProperty());
        }

        if (redoMenuItem != null) {
            redoMenuItem.disableProperty().bind(commandManager.canRedoProperty().not());
            redoMenuItem.textProperty().bind(commandManager.redoDescriptionProperty());
        }

        System.out.println("🔗 Undo/Redo menu items bound to command manager");
    }

    private void setupKeyboardShortcuts() {
        if (statusLabel == null || statusLabel.getScene() == null) {
            // Try again after a short delay
            Timeline delayedSetup = new Timeline(new KeyFrame(Duration.millis(100), e -> {
                if (statusLabel.getScene() != null) {
                    setupKeyboardShortcutsInternal();
                }
            }));
            delayedSetup.play();
            return;
        }

        setupKeyboardShortcutsInternal();
    }

    private void setupKeyboardShortcutsInternal() {
        shortcutManager = new KeyboardShortcutManager(statusLabel.getScene());

        // Create callback implementation
        KeyboardShortcutManager.ShortcutCallbacks callbacks = new KeyboardShortcutManager.ShortcutCallbacks() {
            @Override public void undo() { toolManager.undo(); }
            @Override public void redo() { toolManager.redo(); }

            @Override public void selectPenTool() { toolManager.selectPenTool(); }
            @Override public void selectBrushTool() { toolManager.selectBrushTool(); }
            @Override public void selectEraserTool() { toolManager.selectEraserTool(); }
            @Override public void selectRectangleTool() { toolManager.selectRectangleTool(); }
            @Override public void selectCircleTool() { toolManager.selectCircleTool(); }
            @Override public void selectLineTool() { toolManager.selectLineTool(); }
            @Override public void selectTextTool() { toolManager.selectTextTool(); }

            @Override public void newProject() { MainController.this.newProject(); }
            @Override public void openProject() { MainController.this.openProject(); }
            @Override public void saveProject() { MainController.this.saveProject(); }

            @Override public void previousPanel() { MainController.this.previousPanel(); }
            @Override public void nextPanel() { MainController.this.nextPanel(); }

            @Override public void newPanel() { MainController.this.createNewPanel(); }
            @Override public void duplicatePanel() { MainController.this.duplicatePanel(); }
            @Override public void deletePanel() { MainController.this.deleteCurrentPanel(); }

            @Override public void togglePanels() { panelSystemManager.toggleLeftPanel(); }
            @Override public void toggleToolsPanel() { panelSystemManager.showToolsPanel(); }
            @Override public void toggleStructurePanel() { panelSystemManager.showStructurePanel(); }

            @Override public void zoomIn() { toolManager.zoomIn(); }
            @Override public void zoomOut() { toolManager.zoomOut(); }
            @Override public void resetZoom() { toolManager.resetZoom(); }
        };

        shortcutManager.setupDefaultShortcuts(callbacks);

        // Add rich text editor shortcut
        shortcutManager.registerCtrlShortcut(KeyCode.E, () -> {
            if (projectManager.getCurrentPanel() != null) {
                editPanelDescription();
            }
        });

        System.out.println("⌨️ Keyboard shortcuts initialized");
    }

    private void centerCanvas() {
        if (canvasContainer != null && drawingCanvas != null) {
            double containerWidth = canvasContainer.getWidth() > 0 ? canvasContainer.getWidth() : 800;
            double containerHeight = canvasContainer.getHeight() > 0 ? canvasContainer.getHeight() : 600;

            double centerX = Math.max(0, (containerWidth - drawingCanvas.getWidth()) / 2);
            double centerY = Math.max(0, (containerHeight - drawingCanvas.getHeight()) / 2);

            AnchorPane.setTopAnchor(drawingCanvas, centerY);
            AnchorPane.setLeftAnchor(drawingCanvas, centerX);

            // Listen for container size changes to re-center
            canvasContainer.widthProperty().addListener((obs, oldVal, newVal) -> {
                double newCenterX = Math.max(0, (newVal.doubleValue() - drawingCanvas.getWidth()) / 2);
                AnchorPane.setLeftAnchor(drawingCanvas, newCenterX);
            });

            canvasContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
                double newCenterY = Math.max(0, (newVal.doubleValue() - drawingCanvas.getHeight()) / 2);
                AnchorPane.setTopAnchor(drawingCanvas, newCenterY);
            });
        }
    }

    // =====================================
    // PUBLIC API METHODS (called from managers)
    // =====================================

    public void loadProject(Project project) {
        panelNavigator.saveCurrentPanelDrawing();

        // Clear command history when loading new project
        if (commandManager != null) {
            commandManager.clearHistory();
        }

        projectManager.loadProject(project);

        // If scene has panels, load the first one
        Scene currentScene = projectManager.getCurrentScene();
        if (currentScene != null && !currentScene.getPanels().isEmpty()) {
            panelNavigator.switchToPanel(currentScene.getPanels().get(0));
        } else {
            createNewPanel();
        }

        updateWindowTitle();
    }

    // =====================================
    // MENU ACTION HANDLERS (delegated to managers)
    // =====================================

    @FXML private void undo() { toolManager.undo(); }
    @FXML private void redo() { toolManager.redo(); }

    @FXML private void selectPenTool() { toolManager.selectPenTool(); }
    @FXML private void selectBrushTool() { toolManager.selectBrushTool(); }
    @FXML private void selectEraserTool() { toolManager.selectEraserTool(); }
    @FXML private void selectRectangleTool() { toolManager.selectRectangleTool(); }
    @FXML private void selectCircleTool() { toolManager.selectCircleTool(); }
    @FXML private void selectLineTool() { toolManager.selectLineTool(); }
    @FXML private void selectTextTool() { toolManager.selectTextTool(); }

    @FXML private void clearCanvas() { toolManager.clearCanvas(); }

    @FXML private void zoomIn() { toolManager.zoomIn(); }
    @FXML private void zoomOut() { toolManager.zoomOut(); }
    @FXML private void fitToWindow() { toolManager.fitToWindow(); }
    @FXML private void actualSize() { toolManager.resetZoom(); }

    @FXML private void toggleLeftPanel() { panelSystemManager.toggleLeftPanel(); }
    @FXML private void toggleRightPanel() { panelSystemManager.toggleRightPanel(); }

    // =====================================
    // PROJECT/SCENE/PANEL OPERATIONS (delegated to managers)
    // =====================================

    @FXML
    private void createNewScene() {
        panelNavigator.saveCurrentPanelDrawing();
        projectManager.createNewScene();

        if (commandManager != null) {
            commandManager.clearHistory();
        }

        refreshAllUI();
    }

    @FXML
    private void deleteCurrentScene() {
        if (projectManager.deleteCurrentScene()) {
            if (commandManager != null) {
                commandManager.clearHistory();
            }
            refreshAllUI();
        }
    }

    @FXML
    private void createNewPanel() {
        Panel newPanel = projectManager.createNewPanel();
        if (newPanel != null) {
            panelNavigator.switchToPanel(newPanel);
            refreshAllUI();
        }
    }

    @FXML
    private void deleteCurrentPanel() {
        Panel currentPanel = projectManager.getCurrentPanel();
        if (currentPanel != null && projectManager.deletePanel(currentPanel)) {
            refreshAllUI();
        }
    }

    @FXML
    private void duplicatePanel() {
        Panel currentPanel = projectManager.getCurrentPanel();
        if (currentPanel != null) {
            Panel duplicated = projectManager.duplicatePanel(currentPanel);
            if (duplicated != null) {
                panelNavigator.switchToPanel(duplicated);
                refreshAllUI();
            }
        }
    }

    @FXML
    private void editPanelDescription() {
        Panel currentPanel = projectManager.getCurrentPanel();
        if (currentPanel != null) {
            // The UIManager handles rich text editing
            uiManager.refreshAll(); // This will trigger the rich text editor through context menu
        }
    }

    private void previousPanel() {
        panelNavigator.navigateToPreviousPanel();
        refreshAllUI();
    }

    private void nextPanel() {
        panelNavigator.navigateToNextPanel();
        refreshAllUI();
    }

    // =====================================
    // UI UPDATE METHODS (delegated to UIManager)
    // =====================================

    private void refreshAllUI() {
        if (uiManager != null) {
            uiManager.refreshAll();
        }
        updateSceneInfo();
    }

    private void updateSceneInfo() {
        Scene currentScene = projectManager.getCurrentScene();
        Panel currentPanel = projectManager.getCurrentPanel();

        if (currentScene != null && panelSystemManager != null) {
            String sceneName = currentScene.getName();
            String panelName = currentPanel != null ? currentPanel.getName() : "No Panel";
            int panelIndex = currentPanel != null ? currentScene.getPanels().indexOf(currentPanel) : -1;
            int totalPanels = currentScene.getPanels().size();

            panelSystemManager.updateSceneInfo(sceneName, panelName, panelIndex, totalPanels);

            // FIXED: Update rich text display
            if (currentPanel != null) {
                String richText = currentPanel.getDescriptionPlainText();
                panelSystemManager.updateRichTextDisplay(richText);
            }
        }
    }

    private void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }

    private void updateCanvasSizeLabel() {
        if (canvasSizeLabel != null && drawingCanvas != null) {
            canvasSizeLabel.setText(String.format("📐 Canvas: %.0fx%.0f",
                    drawingCanvas.getWidth(), drawingCanvas.getHeight()));
        }
    }

    private void updateWindowTitle() {
        try {
            if (projectManager.getCurrentProject() != null && statusLabel != null) {
                Stage stage = (Stage) statusLabel.getScene().getWindow();
                stage.setTitle("Scenory - " + projectManager.getCurrentProject().getName());
            }
        } catch (Exception e) {
            // Ignore title update errors
        }
    }

    // =====================================
    // NAVIGATION METHODS
    // =====================================

    @FXML
    private void backToWelcome() {
        panelNavigator.saveCurrentPanelDrawing();
        Stage stage = (Stage) statusLabel.getScene().getWindow();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/scenory/welcome-view.fxml"));
            Parent welcomeView = loader.load();

            stage.getScene().setRoot(welcomeView);
            stage.setTitle("Scenory - Professional Storyboard Creator");

            System.out.println("🏠 Returned to welcome screen");

        } catch (IOException e) {
            System.err.println("❌ Failed to return to welcome screen: " + e.getMessage());
            e.printStackTrace();
            updateStatus("❌ Error navigating to welcome screen");
        }
    }

    @FXML private void newProject() { backToWelcome(); }
    @FXML private void openProject() { updateStatus("Open project - TODO"); }
    @FXML private void saveProject() { updateStatus("Project saved"); }
    @FXML private void saveProjectAs() { updateStatus("Save as - TODO"); }
    @FXML private void exportPDF() { updateStatus("Exporting to PDF..."); }
    @FXML private void exportImages() { updateStatus("Exporting images..."); }
    @FXML private void exitApplication() { System.exit(0); }

    // =====================================
    // INFO/DEBUG METHODS
    // =====================================

    @FXML
    private void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Scenory");
        alert.setHeaderText("Scenory - Professional Storyboarding Tool");
        alert.setContentText("Version 1.0\n\n" +
                "Refactored with clean architecture and manager pattern\n" +
                "Built with JavaFX");
        alert.showAndWait();
    }

    @FXML
    private void showDebugInfo() {
        System.out.println("\n=== SCENORY DEBUG INFO ===");
        System.out.println("Tool Manager: " + (toolManager != null ? "✅" : "❌"));
        System.out.println("Project Manager: " + (projectManager != null ? "✅" : "❌"));
        System.out.println("Panel Navigator: " + (panelNavigator != null ? "✅" : "❌"));
        System.out.println("UI Manager: " + (uiManager != null ? "✅" : "❌"));
        System.out.println("Panel System Manager: " + (panelSystemManager != null ? "✅" : "❌"));
        System.out.println("Current Tool: " + (toolManager != null ? toolManager.getCurrentTool() : "Unknown"));
        System.out.println("Current Panel: " + (projectManager != null && projectManager.getCurrentPanel() != null ?
                projectManager.getCurrentPanel().getName() : "None"));
        System.out.println("Current Scene: " + (projectManager != null && projectManager.getCurrentScene() != null ?
                projectManager.getCurrentScene().getName() : "None"));
        System.out.println("Panel State: " + (panelSystemManager != null ?
                panelSystemManager.getCurrentPanelState() : "Unknown"));
        System.out.println("========================\n");
    }

    @FXML private void showQuickStart() { showInfo("Quick Start", "Quick start guide coming soon!"); }
    @FXML private void showShortcuts() { showInfo("Shortcuts", "Keyboard shortcuts guide coming soon!"); }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // =====================================
    // BACKWARDS COMPATIBILITY METHODS
    // =====================================

    /**
     * For backwards compatibility with existing code
     */
    public void duplicateSpecificPanel(Panel panel) {
        if (panel != null) {
            Panel duplicated = projectManager.duplicatePanel(panel);
            if (duplicated != null) {
                refreshAllUI();
            }
        }
    }

    /**
     * Get current project for external access
     */
    public Project getCurrentProject() {
        return projectManager != null ? projectManager.getCurrentProject() : null;
    }

    /**
     * Get current panel for external access
     */
    public Panel getCurrentPanel() {
        return projectManager != null ? projectManager.getCurrentPanel() : null;
    }
}