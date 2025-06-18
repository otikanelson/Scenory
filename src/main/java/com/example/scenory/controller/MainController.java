package com.example.scenory.controller;

import com.example.scenory.model.*;
import com.example.scenory.enums.DrawingTool;
import com.example.scenory.utils.DragAndDropHandler;
import com.example.scenory.utils.ThumbnailGenerator;
import com.example.scenory.utils.CanvasPersistence;
import com.example.scenory.view.components.DrawingCanvas;
import com.example.scenory.view.panels.*;
import com.example.scenory.view.dialogs.RichTextModalController;
import com.example.scenory.database.PanelLayoutDAO;
import com.example.scenory.commands.*;
import com.example.scenory.input.KeyboardShortcutManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.collections.ObservableList;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Optional;

public class MainController implements Initializable {

    // =====================================
    // FXML UI Components
    // =====================================
    @FXML private AnchorPane canvasContainer;
    @FXML private StackPane canvasStackPane;
    @FXML private ScrollPane canvasScrollPane;
    @FXML private Label statusLabel, canvasSizeLabel, zoomLabel;

    // Main layout components
    @FXML private BorderPane mainBorderPane;

    // Menu items for panel visibility and undo/redo
    @FXML private CheckMenuItem showLeftPanelMenuItem, showRightPanelMenuItem;
    @FXML private MenuItem undoMenuItem, redoMenuItem;

    // =====================================
    // ENHANCED PANEL SYSTEM COMPONENTS - CORRECTED
    // =====================================
    private EnhancedDualPanelGroup leftPanelGroup;
    private ToolSelectionPanel toolSelectionPanel;
    private CollapsibleSceneConstructor rightSceneConstructor;
    private ResizablePanelSystem resizableSystem;
    private PanelLayoutDAO.PanelLayout currentLayout;

    // Tree view for structure tab
    private TreeView<Object> sceneTreeView;

    // Thumbnail grid reference (stored for easy access)
    private GridPane thumbnailGrid;

    // =====================================
    // COMMAND SYSTEM COMPONENTS
    // =====================================
    private CommandManager commandManager;
    private KeyboardShortcutManager shortcutManager;

    // =====================================
    // APPLICATION STATE
    // =====================================
    private Project currentProject;
    private com.example.scenory.model.Scene currentScene; // FIXED: Fully qualified name
    private Panel currentPanel;
    private DrawingCanvas drawingCanvas;
    private GraphicsContext gc;
    private ObservableList<Panel> panelList;
    private DrawingTool currentTool = DrawingTool.PEN;

    // Drawing State
    private double lastX, lastY;
    private double zoomLevel = 1.0;
    private boolean autoGenerateThumbnails = true;
    private Timeline thumbnailUpdateTimer;

    // Persistence Control
    private boolean isUpdatingSelection = false;
    private boolean autoSaveEnabled = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("🚀 Initializing Enhanced MainController with Rich Text Editor...");

        initializeProject();
        initializeCommandSystem();
        initializeEnhancedPanelSystem();
        initializeCanvas();
        setupSceneManagement();
        createFirstPanel();
        setupEventHandlers();
        setupToolIntegration();
        setupLayoutPersistence();
        setupKeyboardShortcuts();

        System.out.println("✅ Enhanced MainController with Rich Text Editor initialized successfully");
    }

    // =====================================
    // ENHANCED INITIALIZATION METHODS
    // =====================================

    private void initializeProject() {
        currentProject = new Project();
        currentProject.setName("Untitled Project");

        // Create default scene
        com.example.scenory.model.Scene defaultScene = new com.example.scenory.model.Scene(); // FIXED
        defaultScene.setName("Scene 1");
        defaultScene.setSequenceOrder(0);
        currentProject.getScenes().add(defaultScene);
        currentScene = defaultScene;

        System.out.println("📁 Project initialized with default scene");
    }

    private void initializeCommandSystem() {
        System.out.println("🔧 Setting up command system...");

        // Create command manager
        commandManager = new CommandManager();
        commandManager.setMaxHistorySize(100);
        commandManager.setMergeConsecutiveStrokes(true);

        System.out.println("✅ Command system initialized");
    }

    private void initializeEnhancedPanelSystem() {
        System.out.println("🔧 Setting up enhanced dual panel system...");

        // Create tool selection panel content
        toolSelectionPanel = new ToolSelectionPanel();
        System.out.println("🛠️ ToolSelectionPanel created");

        // Create file structure content (using existing tree view)
        VBox fileStructureContent = createFileStructureContent();

        // Create EnhancedDualPanelGroup
        leftPanelGroup = new EnhancedDualPanelGroup();
        leftPanelGroup.setToolsContent(toolSelectionPanel);
        leftPanelGroup.setStructureContent(fileStructureContent);
        System.out.println("📋 EnhancedDualPanelGroup created with Tools and Structure");

        // Create enhanced scene constructor panel with navigation at top
        VBox sceneConstructorContent = createSceneConstructorContentWithReference();
        rightSceneConstructor = new CollapsibleSceneConstructor("Scene Panels", sceneConstructorContent);

        // Set up navigation callbacks
        rightSceneConstructor.setOnPreviousPanel(this::previousPanel);
        rightSceneConstructor.setOnNextPanel(this::nextPanel);
        System.out.println("🎬 Enhanced CollapsibleSceneConstructor created");

        // Create resizable panel system
        resizableSystem = new ResizablePanelSystem(mainBorderPane);

        // Replace the main layout panels
        replaceMainLayoutWithEnhancedPanelSystem();

        System.out.println("✅ Enhanced dual panel system initialized");
    }

    private VBox createFileStructureContent() {
        VBox content = new VBox(8);
        content.getStyleClass().add("file-structure-content");

        // Header with management buttons
        HBox header = new HBox(6);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label headerLabel = new Label("📋 Project Structure");
        headerLabel.getStyleClass().add("section-header");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Scene management buttons
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

        // Tree view in scroll pane
        if (sceneTreeView == null) {
            sceneTreeView = new TreeView<>();
            sceneTreeView.getStyleClass().add("resizable-tree-view");
        }

        ScrollPane treeScrollPane = new ScrollPane(sceneTreeView);
        treeScrollPane.setFitToWidth(true);
        treeScrollPane.getStyleClass().add("invisible-scroll-pane");
        VBox.setVgrow(treeScrollPane, Priority.ALWAYS);

        content.getChildren().addAll(header, treeScrollPane);
        return content;
    }

    private VBox createSceneConstructorContentWithReference() {
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

    private void replaceMainLayoutWithEnhancedPanelSystem() {
        if (mainBorderPane == null) {
            System.err.println("❌ mainBorderPane is null - check FXML binding");
            return;
        }

        // Clear existing content
        mainBorderPane.setLeft(null);
        mainBorderPane.setRight(null);

        // Set up resizable panel system
        resizableSystem.setComponents(leftPanelGroup, canvasStackPane, rightSceneConstructor);

        // Restore saved panel sizes
        resizableSystem.restorePanelSizes();

        System.out.println("🔄 Main layout replaced with enhanced panel system");
    }

    private void initializeCanvas() {
        drawingCanvas = new DrawingCanvas(800, 600);

        // Connect command manager to canvas
        drawingCanvas.setCommandManager(commandManager);

        // Set up zoom change listener to update zoom label
        drawingCanvas.setZoomChangeListener(newZoomLevel -> {
            if (zoomLabel != null) {
                zoomLabel.setText("🔍 " + Math.round(newZoomLevel * 100) + "%");
            }
            zoomLevel = newZoomLevel;
        });

        // Add canvas to the container and center it
        if (canvasContainer != null) {
            canvasContainer.getChildren().clear();
            canvasContainer.getChildren().add(drawingCanvas);
            centerCanvas();
        }

        // Setup undo/redo menu binding
        setupUndoRedoMenuBinding();

        updateCanvasSizeLabel();
        System.out.println("🖼️ Enhanced canvas initialized with command system: 800x600");
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
            System.err.println("❌ Cannot setup keyboard shortcuts - scene not available yet");
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
            @Override public void undo() { MainController.this.undo(); }
            @Override public void redo() { MainController.this.redo(); }

            @Override public void selectPenTool() { selectTool(DrawingTool.PEN); }
            @Override public void selectBrushTool() { selectTool(DrawingTool.BRUSH); }
            @Override public void selectEraserTool() { selectTool(DrawingTool.ERASER); }
            @Override public void selectRectangleTool() { selectTool(DrawingTool.RECTANGLE); }
            @Override public void selectCircleTool() { selectTool(DrawingTool.CIRCLE); }
            @Override public void selectLineTool() { selectTool(DrawingTool.LINE); }
            @Override public void selectTextTool() { selectTool(DrawingTool.TEXT); }

            @Override public void newProject() { MainController.this.newProject(); }
            @Override public void openProject() { MainController.this.openProject(); }
            @Override public void saveProject() { MainController.this.saveProject(); }

            @Override public void previousPanel() { MainController.this.previousPanel(); }
            @Override public void nextPanel() { MainController.this.nextPanel(); }

            @Override public void newPanel() { MainController.this.createNewPanel(); }
            @Override public void duplicatePanel() { MainController.this.duplicatePanel(); }
            @Override public void deletePanel() { MainController.this.deleteCurrentPanel(); }

            @Override public void togglePanels() { MainController.this.toggleLeftPanel(); }
            @Override public void toggleToolsPanel() {
                if (leftPanelGroup != null) {
                    leftPanelGroup.expandToolsTab();
                }
            }
            @Override public void toggleStructurePanel() {
                if (leftPanelGroup != null) {
                    leftPanelGroup.expandStructureTab();
                }
            }

            @Override public void zoomIn() { MainController.this.zoomIn(); }
            @Override public void zoomOut() { MainController.this.zoomOut(); }
            @Override public void resetZoom() { MainController.this.actualSize(); }
        };

        shortcutManager.setupDefaultShortcuts(callbacks);

        // Add rich text editor shortcut
        shortcutManager.registerCtrlShortcut(KeyCode.E, () -> {
            if (currentPanel != null) {
                openRichTextEditor(currentPanel);
            }
        });

        System.out.println("⌨️ Keyboard shortcuts initialized with Rich Text Editor (Ctrl+E)");
    }

    private void centerCanvas() {
        if (canvasContainer != null && drawingCanvas != null) {
            // Calculate center position
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
    // RICH TEXT EDITOR INTEGRATION
    // =====================================

    /**
     * Open rich text editor for a panel's description
     */
    /**
     * FIXED: Open rich text editor for a panel's description
     */
    private void openRichTextEditor(Panel panel) {
        try {
            System.out.println("📝 Opening simplified rich text editor for: " + panel.getName());

            // FIXED: Load the Rich Text Modal FXML with correct path
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/scenory/view/dialogs/RichTextModal.fxml"));
            Parent richTextModal = loader.load();

            // Get the controller
            RichTextModalController controller = loader.getController();

            // Create a new stage for the modal
            Stage modalStage = new Stage();
            modalStage.setTitle("Edit Panel Description - " + panel.getName());
            modalStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            modalStage.initOwner(statusLabel.getScene().getWindow());

            // Set up the scene - FIXED: Use javafx.scene.Scene
            javafx.scene.Scene modalScene = new javafx.scene.Scene(richTextModal, 700, 500);

            // Add CSS styling
            try {
                String cssFile = getClass().getResource("/com/example/scenory/styles.css").toExternalForm();
                modalScene.getStylesheets().add(cssFile);
            } catch (Exception e) {
                System.out.println("⚠️ Could not load CSS for rich text modal");
            }

            modalStage.setScene(modalScene);
            modalStage.setResizable(true);
            modalStage.setMinWidth(600);
            modalStage.setMinHeight(400);

            // Center the modal
            modalStage.centerOnScreen();

            // Set up the controller with panel data and save callback
            controller.openForPanel(panel, this::onRichTextSaved);

            // Show the modal
            modalStage.showAndWait();

        } catch (IOException e) {
            System.err.println("❌ Failed to open rich text editor: " + e.getMessage());
            e.printStackTrace();

            // FALLBACK: Show simple text input dialog
            showSimpleTextInputFallback(panel);
        } catch (Exception e) {
            System.err.println("❌ Unexpected error opening rich text editor: " + e.getMessage());
            e.printStackTrace();
            showError("Rich Text Editor Error", "Failed to open the rich text editor: " + e.getMessage());
        }
    }

    /**
     * Fallback simple text input when rich text modal fails
     */
    private void showSimpleTextInputFallback(Panel panel) {
        // Create a custom dialog with TextArea instead of TextField
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Edit Panel Description");
        dialog.setHeaderText("Edit description for: " + panel.getName());

        // Set up dialog buttons
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        // Create TextArea for multiline input
        TextArea textArea = new TextArea();
        textArea.setText(panel.getDescriptionPlainText() != null ? panel.getDescriptionPlainText() : "");
        textArea.setPromptText("Enter panel description here...");
        textArea.setPrefRowCount(8);
        textArea.setPrefColumnCount(50);
        textArea.setWrapText(true);

        // Style the TextArea
        textArea.setStyle(
                "-fx-font-family: 'Inter', 'Segoe UI', Arial, sans-serif; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 8px;"
        );

        // Create container
        VBox container = new VBox(10);
        container.getChildren().addAll(
                new Label("Description:"),
                textArea
        );
        container.setPrefWidth(500);

        dialog.getDialogPane().setContent(container);

        // Focus on text area
        javafx.application.Platform.runLater(() -> textArea.requestFocus());

        // Set result converter
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return textArea.getText();
            }
            return null;
        });

        // Show dialog and handle result
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(description -> {
            panel.setDescriptionPlainText(description);
            panel.setDescriptionRichText(description);
            onRichTextSaved(description);
            statusLabel.setText("✅ Panel description updated (fallback mode): " + panel.getName());
        });
    }
    /**
     * Callback when rich text is saved
     */
    private void onRichTextSaved(String richTextContent) {
        System.out.println("💾 Rich text content saved: " + richTextContent.length() + " characters");

        // Update UI to reflect changes
        updateThumbnailGrid(); // This will show the rich text indicator (📝)
        updateSceneInfo();

        // Mark panel as modified
        if (currentPanel != null) {
            statusLabel.setText("✅ Panel description updated: " + currentPanel.getName());
        }
    }

    /**
     * Enhanced menu action for rich text editing
     */
    @FXML
    private void editPanelDescription() {
        if (currentPanel != null) {
            openRichTextEditor(currentPanel);
        } else {
            statusLabel.setText("No panel selected");
        }
    }

    // =====================================
    // ENHANCED UNDO/REDO METHODS
    // =====================================

    @FXML
    private void undo() {
        if (drawingCanvas != null && drawingCanvas.undo()) {
            statusLabel.setText("↶ " + commandManager.getUndoDescription());
            System.out.println("↶ Undo executed");
        } else {
            statusLabel.setText("Nothing to undo");
        }
    }

    @FXML
    private void redo() {
        if (drawingCanvas != null && drawingCanvas.redo()) {
            statusLabel.setText("↷ " + commandManager.getRedoDescription());
            System.out.println("↷ Redo executed");
        } else {
            statusLabel.setText("Nothing to redo");
        }
    }

    // =====================================
    // TOOL INTEGRATION METHODS
    // =====================================

    private void setupToolIntegration() {
        if (toolSelectionPanel == null) return;

        // Connect tool selection to drawing canvas
        toolSelectionPanel.selectedToolProperty().addListener((obs, oldTool, newTool) -> {
            if (drawingCanvas != null && newTool != null) {
                drawingCanvas.setCurrentTool(newTool);
                currentTool = newTool;
                statusLabel.setText("🛠️ " + newTool.getDisplayName() + " tool selected");
            }
        });

        // Connect color changes
        toolSelectionPanel.selectedColorProperty().addListener((obs, oldColor, newColor) -> {
            if (drawingCanvas != null && newColor != null) {
                drawingCanvas.setCurrentColor(newColor);
                statusLabel.setText("🎨 Color changed");
            }
        });

        // Connect stroke size changes
        toolSelectionPanel.strokeSizeProperty().addListener((obs, oldSize, newSize) -> {
            if (drawingCanvas != null && newSize != null) {
                drawingCanvas.setStrokeWidth(newSize.doubleValue());
                statusLabel.setText("📏 Brush size: " + Math.round(newSize.doubleValue()));
            }
        });

        System.out.println("🔗 Enhanced tool integration with command system setup complete");
    }

    // =====================================
    // LAYOUT PERSISTENCE METHODS
    // =====================================

    private void setupLayoutPersistence() {
        // Load saved layout (with error handling)
        try {
            currentLayout = PanelLayoutDAO.loadLayout("default", "default");
            applyLayout(currentLayout);
        } catch (Exception e) {
            System.err.println("❌ Error loading panel layout: " + e.getMessage());
            currentLayout = createDefaultPanelLayout();
            applyLayout(currentLayout);
        }

        // Listen to the correct properties for EnhancedDualPanelGroup (with error handling)
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
        // Create a default layout when database is not available
        PanelLayoutDAO.PanelLayout layout = new PanelLayoutDAO.PanelLayout();
        layout.setToolPanelCollapsed(false);
        layout.setFileStructureCollapsed(false);
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

            // Apply dual panel state using correct methods
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
            // Save dual panel state using correct properties
            if (leftPanelGroup != null) {
                currentLayout.setToolPanelCollapsed(!leftPanelGroup.isToolsExpanded());
                currentLayout.setFileStructureCollapsed(!leftPanelGroup.isStructureExpanded());
            }

            // Save scene constructor state
            if (rightSceneConstructor != null) {
                currentLayout.setSceneConstructorVisible(!rightSceneConstructor.isCollapsed());
                currentLayout.setSceneConstructorPosition("RIGHT");
            }

            // Save to database (with error handling)
            PanelLayoutDAO.saveLayout("default", "default", currentLayout);

        } catch (Exception e) {
            System.err.println("❌ Error saving layout: " + e.getMessage());
            // Continue without saving - app should still work
        }
    }


    // =====================================
    // ENHANCED PANEL TOGGLE METHODS
    // =====================================

    @FXML
    private void toggleLeftPanel() {
        if (leftPanelGroup != null) {
            leftPanelGroup.collapseAll();

            if (showLeftPanelMenuItem != null) {
                showLeftPanelMenuItem.setSelected(leftPanelGroup.isExpanded());
            }

            statusLabel.setText(leftPanelGroup.isExpanded() ? "Left panel expanded" : "Left panel collapsed");
        }
    }

    @FXML
    private void toggleRightPanel() {
        if (rightSceneConstructor != null) {
            rightSceneConstructor.toggleCollapse();

            if (showRightPanelMenuItem != null) {
                showRightPanelMenuItem.setSelected(!rightSceneConstructor.isCollapsed());
            }

            statusLabel.setText(rightSceneConstructor.isCollapsed() ? "Scene constructor collapsed" : "Scene constructor expanded");
        }
    }

    public void showToolsPanel() {
        if (leftPanelGroup != null) {
            leftPanelGroup.expandToolsTab();
            statusLabel.setText("Tools panel active");
        }
    }

    public void showStructurePanel() {
        if (leftPanelGroup != null) {
            leftPanelGroup.expandStructureTab();
            statusLabel.setText("Structure panel active");
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

    // =====================================
    // TOOL SELECTION METHODS
    // =====================================

    private void selectTool(DrawingTool tool) {
        if (drawingCanvas != null) {
            drawingCanvas.setCurrentTool(tool);
            currentTool = tool;

            // Update tool selection panel if available
            if (toolSelectionPanel != null) {
                toolSelectionPanel.setSelectedTool(tool);
            }

            statusLabel.setText("🛠️ " + tool.getDisplayName() + " tool selected");
        }
    }

    // Individual tool selection methods for menu actions
    @FXML private void selectPenTool() { selectTool(DrawingTool.PEN); }
    @FXML private void selectBrushTool() { selectTool(DrawingTool.BRUSH); }
    @FXML private void selectEraserTool() { selectTool(DrawingTool.ERASER); }

    @FXML
    private void selectRectangleTool() {
        selectTool(DrawingTool.RECTANGLE);
        statusLabel.setText("🔲 Rectangle tool selected - Click and drag to draw");
    }

    @FXML
    private void selectCircleTool() {
        selectTool(DrawingTool.CIRCLE);
        statusLabel.setText("⭕ Circle tool selected - Click and drag to draw");
    }

    @FXML
    private void selectLineTool() {
        selectTool(DrawingTool.LINE);
        statusLabel.setText("📏 Line tool selected - Click and drag to draw");
    }

    @FXML
    private void selectTextTool() {
        selectTool(DrawingTool.TEXT);
        statusLabel.setText("📝 Text tool selected");
    }

    // =====================================
    // ENHANCED CLEAR CANVAS
    // =====================================

    public void clearCurrentPanelWithUndo() {
        if (drawingCanvas != null) {
            drawingCanvas.clearCanvasWithUndo();
            statusLabel.setText("Canvas cleared (can be undone)");
        }
    }

    @FXML
    private void clearCanvas() {
        clearCurrentPanelWithUndo();
    }

    // =====================================
    // PROJECT LOADING WITH COMMAND HISTORY
    // =====================================

    public void loadProject(Project project) {
        System.out.println("📂 Loading project: " + project.getName());

        try {
            // Save any current work first
            if (currentPanel != null) {
                saveCurrentPanelDrawing();
            }

            // Clear command history when loading new project
            if (commandManager != null) {
                commandManager.clearHistory();
            }

            // Load the new project
            currentProject = project;

            // Set current scene to first scene or create one if none exists
            if (!project.getScenes().isEmpty()) {
                currentScene = project.getScenes().get(0);
            } else {
                // Create default scene if project has none
                com.example.scenory.model.Scene defaultScene = new com.example.scenory.model.Scene(); // FIXED
                defaultScene.setName("Scene 1");
                defaultScene.setSequenceOrder(0);
                project.getScenes().add(defaultScene);
                currentScene = defaultScene;
            }

            // Clear current panel selection
            currentPanel = null;

            // Refresh all UI components
            refreshSceneTree();
            refreshPanelList();
            updateSceneInfo();
            updateThumbnailGrid();

            // Clear canvas
            if (drawingCanvas != null) {
                CanvasPersistence.clearCanvas(drawingCanvas);
            }

            // If scene has panels, load the first one
            if (!currentScene.getPanels().isEmpty()) {
                switchToPanelInternal(currentScene.getPanels().get(0));
            } else {
                // Create first panel if scene is empty
                createNewPanel();
            }

            // Update window title
            updateWindowTitle();

            statusLabel.setText("✅ Loaded project: " + project.getName());
            System.out.println("✅ Project loaded successfully with fresh command history: " + project.getName());

        } catch (Exception e) {
            System.err.println("❌ Error loading project: " + e.getMessage());
            e.printStackTrace();
            statusLabel.setText("❌ Error loading project");
        }
    }

    private void updateWindowTitle() {
        try {
            if (currentProject != null && statusLabel != null) {
                javafx.stage.Stage stage = (javafx.stage.Stage) statusLabel.getScene().getWindow();
                stage.setTitle("Scenory - " + currentProject.getName());
            }
        } catch (Exception e) {
            // Ignore title update errors
        }
    }

    private void setupSceneManagement() {
        refreshSceneTree();
        updateSceneInfo();

        // Setup drag and drop
        if (sceneTreeView != null) {
            DragAndDropHandler.setupTreeViewDragAndDrop(sceneTreeView, this::onDragDropComplete);
        }

        // Tree selection handler with proper saving
        if (sceneTreeView != null) {
            sceneTreeView.getSelectionModel().selectedItemProperty().addListener(
                    (obs, oldSelection, newSelection) -> {
                        if (isUpdatingSelection) return; // Prevent feedback loops

                        if (newSelection != null) {
                            Object selectedItem = newSelection.getValue();
                            if (selectedItem instanceof com.example.scenory.model.Scene) { // FIXED
                                handleSceneSelection((com.example.scenory.model.Scene) selectedItem);
                            } else if (selectedItem instanceof Panel) {
                                handlePanelSelection((Panel) selectedItem);
                            }
                        }
                    }
            );
        }

        updateThumbnailGrid();
        System.out.println("🌳 Scene management setup complete");
    }

    private void createFirstPanel() {
        createNewPanel();
    }

    private void setupEventHandlers() {
        System.out.println("🖱️ Enhanced event handlers setup complete");
    }

    // =====================================
    // PANEL SWITCHING WITH COMMAND HISTORY
    // =====================================

    private void handleSceneSelection(com.example.scenory.model.Scene scene) { // FIXED
        if (scene == currentScene) return;

        saveCurrentPanelDrawing();
        currentScene = scene;
        refreshPanelList();
        updateSceneInfo();
        updateThumbnailGrid();

        if (!scene.getPanels().isEmpty()) {
            switchToPanelInternal(scene.getPanels().get(0));
        } else {
            currentPanel = null;
            CanvasPersistence.clearCanvas(drawingCanvas);
        }

        statusLabel.setText("Switched to scene: " + scene.getName());
    }

    private void handlePanelSelection(Panel panel) {
        if (panel == currentPanel) return;
        switchToPanelInternal(panel);
    }

    private void switchToPanelInternal(Panel panel) {
        if (panel == currentPanel) return;

        // Save current panel's drawing before switching
        saveCurrentPanelDrawing();

        // Clear command history when switching panels
        if (commandManager != null) {
            commandManager.clearHistory();
        }

        // Switch to new panel
        currentPanel = panel;

        // Update scene if needed (panel might be from different scene)
        com.example.scenory.model.Scene panelScene = findSceneContainingPanel(panel); // FIXED
        if (panelScene != null && panelScene != currentScene) {
            currentScene = panelScene;
            refreshPanelList();
        }

        // Restore the new panel's drawing
        restorePanelDrawing(panel);

        // Update UI
        updateSceneInfo();
        updateThumbnailGrid();
        updateSelections();

        statusLabel.setText("Switched to: " + panel.getName());
        System.out.println("📝 Switched to panel: " + panel.getName() + " (Command history cleared)");
    }

    private void saveCurrentPanelDrawing() {
        if (currentPanel == null || drawingCanvas == null) {
            return;
        }

        try {
            byte[] canvasData = CanvasPersistence.saveCanvasToBytes(drawingCanvas);

            if (canvasData != null && canvasData.length > 0) {
                currentPanel.setCanvasImageData(canvasData);

                // Also update thumbnail
                if (autoGenerateThumbnails) {
                    byte[] thumbnailData = ThumbnailGenerator.generateThumbnail(drawingCanvas);
                    currentPanel.setThumbnailData(thumbnailData);
                }

                System.out.println("💾 SAVED: " + currentPanel.getName() + " (" + canvasData.length + " bytes)");
            }

        } catch (Exception e) {
            System.err.println("❌ Error saving panel: " + e.getMessage());
            e.printStackTrace();
            statusLabel.setText("Error saving drawing");
        }
    }

    private void restorePanelDrawing(Panel panel) {
        if (panel == null || drawingCanvas == null) return;

        try {
            if (CanvasPersistence.isValidImageData(panel.getCanvasImageData())) {
                boolean restored = CanvasPersistence.restoreCanvasFromBytes(
                        drawingCanvas, panel.getCanvasImageData()
                );

                if (restored) {
                    System.out.println("📂 Restored: " + panel.getName());
                } else {
                    drawingCanvas.clearCanvas();
                    System.out.println("⚠️ Failed to restore: " + panel.getName());
                }
            } else {
                drawingCanvas.clearCanvas();
                System.out.println("📄 New panel: " + panel.getName());
            }

        } catch (Exception e) {
            drawingCanvas.clearCanvas();
            System.err.println("❌ Error restoring panel: " + e.getMessage());
        }
    }

    // =====================================
    // UI UPDATE METHODS
    // =====================================

    private void refreshSceneTree() {
        if (sceneTreeView == null) return;

        isUpdatingSelection = true;
        try {
            TreeItem<Object> rootItem = new TreeItem<>(currentProject);
            rootItem.setExpanded(true);

            for (com.example.scenory.model.Scene scene : currentProject.getScenes()) { // FIXED
                TreeItem<Object> sceneItem = new TreeItem<>(scene);
                sceneItem.setExpanded(true);

                for (Panel panel : scene.getPanels()) {
                    TreeItem<Object> panelItem = new TreeItem<>(panel);
                    sceneItem.getChildren().add(panelItem);
                }

                rootItem.getChildren().add(sceneItem);
            }

            sceneTreeView.setRoot(rootItem);
            sceneTreeView.setShowRoot(false);
        } finally {
            isUpdatingSelection = false;
        }
    }

    private void refreshPanelList() {
        if (currentScene != null && panelList != null) {
            panelList.clear();
            panelList.addAll(currentScene.getPanels());
        }
    }

    private void updateSceneInfo() {
        if (currentScene != null && rightSceneConstructor != null) {
            String sceneName = currentScene.getName();
            String panelName = currentPanel != null ? currentPanel.getName() : "No Panel";
            int panelIndex = currentPanel != null ? currentScene.getPanels().indexOf(currentPanel) : -1;
            int totalPanels = currentScene.getPanels().size();

            // Update the scene constructor header info
            rightSceneConstructor.updateSceneInfo(sceneName, panelName, panelIndex, totalPanels);
        }

        if (zoomLabel != null) {
            zoomLabel.setText("🔍 " + Math.round(zoomLevel * 100) + "%");
        }
    }

    private void updateThumbnailGrid() {
        if (thumbnailGrid != null && currentScene != null) {
            updateThumbnailGridInternal(thumbnailGrid);
        }
    }

    private void updateThumbnailGridInternal(GridPane thumbnailGrid) {
        if (thumbnailGrid == null || currentScene == null) return;

        thumbnailGrid.getChildren().clear();

        int row = 0;
        for (Panel panel : currentScene.getPanels()) {
            Button thumbnailBtn = createLargeThumbnailButton(panel);
            thumbnailGrid.add(thumbnailBtn, 0, row);
            row++;
        }
    }

    private Button createLargeThumbnailButton(Panel panel) {
        VBox thumbnailContainer = new VBox(6);
        thumbnailContainer.getStyleClass().add("enhanced-thumbnail-container");

        // Thumbnail image
        ImageView imageView = createThumbnailImage(panel);

        // Panel title with rich text indicator
        HBox titleContainer = new HBox(4);
        Label titleLabel = new Label(panel.getName());
        titleLabel.getStyleClass().add("thumbnail-title");

        // Rich text indicator
        if (panel.hasRichTextDescription()) {
            Label richTextIndicator = new Label("📝");
            richTextIndicator.getStyleClass().add("rich-text-indicator");
            titleContainer.getChildren().addAll(titleLabel, richTextIndicator);
        } else {
            titleContainer.getChildren().add(titleLabel);
        }

        // Background color indicator
        if (panel.getCanvasBackgroundColor() != null && !panel.getCanvasBackgroundColor().equals("#FFFFFF")) {
            javafx.scene.shape.Rectangle colorIndicator = new javafx.scene.shape.Rectangle(12, 12);
            colorIndicator.setFill(Color.web(panel.getCanvasBackgroundColor()));
            colorIndicator.getStyleClass().add("color-indicator");
            titleContainer.getChildren().add(colorIndicator);
        }

        // Timing indicator (moved to separate line to avoid overlap)
        Label timingLabel = new Label(panel.getFormattedDisplayDuration());
        timingLabel.getStyleClass().add("timing-indicator");

        thumbnailContainer.getChildren().addAll(imageView, titleContainer, timingLabel);

        Button thumbnailBtn = new Button();
        thumbnailBtn.setGraphic(thumbnailContainer);
        thumbnailBtn.getStyleClass().add("large-thumbnail-button");
        thumbnailBtn.setPrefSize(240, 180);
        thumbnailBtn.setMaxSize(240, 180);
        thumbnailBtn.setMinSize(240, 180);

        // Enhanced context menu with rich text editor
        ContextMenu contextMenu = createEnhancedPanelContextMenu(panel);
        thumbnailBtn.setContextMenu(contextMenu);

        // Click handler
        thumbnailBtn.setOnAction(e -> {
            if (panel != currentPanel) {
                handlePanelSelection(panel);
            }
        });

        // Selection styling
        if (panel == currentPanel) {
            thumbnailBtn.getStyleClass().add("selected");
            thumbnailBtn.setStyle("-fx-border-color: #bf5700; -fx-border-width: 3; -fx-background-color: #5a5a5a;");
        }

        return thumbnailBtn;
    }

    private ImageView createThumbnailImage(Panel panel) {
        if (ThumbnailGenerator.isValidThumbnail(panel.getThumbnailData())) {
            try {
                Image thumbnail = ThumbnailGenerator.bytesToImage(panel.getThumbnailData());
                if (thumbnail != null) {
                    ImageView imageView = new ImageView(thumbnail);
                    imageView.setFitWidth(220);
                    imageView.setFitHeight(120);
                    imageView.setPreserveRatio(true);
                    return imageView;
                }
            } catch (Exception e) {
                System.err.println("Error loading thumbnail: " + e.getMessage());
            }
        }

        // Default placeholder
        ImageView placeholder = new ImageView();
        placeholder.setFitWidth(220);
        placeholder.setFitHeight(120);
        return placeholder;
    }

    /**
     * Create enhanced panel context menu with rich text editor
     */
    private ContextMenu createEnhancedPanelContextMenu(Panel panel) {
        ContextMenu contextMenu = new ContextMenu();

        // Rename Panel
        MenuItem renameItem = new MenuItem("Rename Panel");
        renameItem.setGraphic(new Label("✏️"));
        renameItem.setOnAction(e -> showRenamePanelDialog(panel));

        // Edit Description (Rich Text) - NOW FUNCTIONAL!
        MenuItem editDescItem = new MenuItem("Edit Description");
        editDescItem.setGraphic(new Label("📝"));
        editDescItem.setOnAction(e -> openRichTextEditor(panel));

        // Set Panel Timing
        MenuItem timingItem = new MenuItem("Set Timing");
        timingItem.setGraphic(new Label("⏱️"));
        timingItem.setOnAction(e -> showTimingDialog(panel));

        // Change Canvas Background
        MenuItem backgroundItem = new MenuItem("Change Background");
        backgroundItem.setGraphic(new Label("🎨"));
        backgroundItem.setOnAction(e -> showBackgroundColorDialog(panel));

        contextMenu.getItems().addAll(
                renameItem, editDescItem, new SeparatorMenuItem(),
                timingItem, backgroundItem, new SeparatorMenuItem()
        );

        // Standard operations
        MenuItem duplicateItem = new MenuItem("Duplicate Panel");
        duplicateItem.setGraphic(new Label("📋"));
        duplicateItem.setOnAction(e -> duplicatePanel(panel));

        MenuItem deleteItem = new MenuItem("Delete Panel");
        deleteItem.setGraphic(new Label("🗑️"));
        deleteItem.setOnAction(e -> deletePanel(panel));

        MenuItem clearItem = new MenuItem("Clear Content");
        clearItem.setGraphic(new Label("🧹"));
        clearItem.setOnAction(e -> clearPanelContent(panel));

        contextMenu.getItems().addAll(duplicateItem, deleteItem, clearItem);

        return contextMenu;
    }

    private void updateSelections() {
        isUpdatingSelection = true;
        try {
            if (sceneTreeView != null && currentPanel != null) {
                TreeItem<Object> panelItem = findPanelTreeItem(sceneTreeView.getRoot(), currentPanel);
                if (panelItem != null) {
                    sceneTreeView.getSelectionModel().select(panelItem);
                }
            }
        } finally {
            isUpdatingSelection = false;
        }
    }

    // =====================================
    // HELPER METHODS
    // =====================================

    private com.example.scenory.model.Scene findSceneContainingPanel(Panel panel) { // FIXED
        for (com.example.scenory.model.Scene scene : currentProject.getScenes()) {
            if (scene.getPanels().contains(panel)) {
                return scene;
            }
        }
        return null;
    }

    private TreeItem<Object> findPanelTreeItem(TreeItem<Object> root, Panel targetPanel) {
        if (root == null) return null;

        if (root.getValue() instanceof Panel) {
            Panel panel = (Panel) root.getValue();
            if (panel.getId().equals(targetPanel.getId())) {
                return root;
            }
        }

        for (TreeItem<Object> child : root.getChildren()) {
            TreeItem<Object> found = findPanelTreeItem(child, targetPanel);
            if (found != null) {
                return found;
            }
        }

        return null;
    }

    private void updateCanvasSizeLabel() {
        if (canvasSizeLabel != null) {
            canvasSizeLabel.setText(String.format("📐 Canvas: %.0fx%.0f",
                    drawingCanvas.getWidth(), drawingCanvas.getHeight()));
        }
    }

    private void onDragDropComplete() {
        saveCurrentPanelDrawing();
        refreshSceneTree();
        updateSceneInfo();
        updateThumbnailGrid();
        statusLabel.setText("Panel reordered successfully");
    }

    // =====================================
    // SCENE/PANEL CREATION METHODS
    // =====================================

    @FXML
    private void createNewScene() {
        saveCurrentPanelDrawing();

        com.example.scenory.model.Scene newScene = new com.example.scenory.model.Scene(); // FIXED
        newScene.setName("Scene " + (currentProject.getScenes().size() + 1));
        newScene.setSequenceOrder(currentProject.getScenes().size());

        currentProject.getScenes().add(newScene);
        currentScene = newScene;
        currentPanel = null;

        if (drawingCanvas != null) {
            CanvasPersistence.clearCanvas(drawingCanvas);
        }

        // Clear command history for new scene
        if (commandManager != null) {
            commandManager.clearHistory();
        }

        refreshSceneTree();
        refreshPanelList();
        updateSceneInfo();
        updateThumbnailGrid();

        statusLabel.setText("Created new scene: " + newScene.getName());
    }

    @FXML
    private void deleteCurrentScene() {
        if (currentScene == null || currentProject.getScenes().size() <= 1) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cannot Delete Scene");
            alert.setHeaderText("Cannot delete the last scene");
            alert.setContentText("A project must have at least one scene.");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Scene");
        alert.setHeaderText("Delete Scene: " + currentScene.getName());
        alert.setContentText("Are you sure you want to delete this scene and all its panels?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            currentProject.getScenes().remove(currentScene);
            currentScene = currentProject.getScenes().get(0);
            currentPanel = null;

            CanvasPersistence.clearCanvas(drawingCanvas);

            // Clear command history
            if (commandManager != null) {
                commandManager.clearHistory();
            }

            refreshSceneTree();
            refreshPanelList();
            updateSceneInfo();
            updateThumbnailGrid();

            statusLabel.setText("Scene deleted");
        }
    }

    @FXML
    private void createNewPanel() {
        if (currentScene == null) {
            statusLabel.setText("No scene selected");
            return;
        }

        saveCurrentPanelDrawing();

        Panel newPanel = new Panel();
        newPanel.setName("Panel " + (currentScene.getPanels().size() + 1));
        newPanel.setSequenceOrder(currentScene.getPanels().size());

        currentScene.getPanels().add(newPanel);
        refreshPanelList();

        switchToPanelInternal(newPanel);

        refreshSceneTree();
        updateSceneInfo();
        updateThumbnailGrid();

        statusLabel.setText("Created new panel: " + newPanel.getName());
    }

    @FXML
    private void deleteCurrentPanel() {
        if (currentPanel == null || currentScene == null) {
            statusLabel.setText("No panel selected");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Panel");
        alert.setHeaderText("Delete Panel: " + currentPanel.getName());
        alert.setContentText("Are you sure you want to delete this panel?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String deletedPanelName = currentPanel.getName();
            int deletedIndex = currentScene.getPanels().indexOf(currentPanel);

            currentScene.getPanels().remove(currentPanel);

            // Switch to adjacent panel or create new one if none exist
            if (!currentScene.getPanels().isEmpty()) {
                int newIndex = Math.min(deletedIndex, currentScene.getPanels().size() - 1);
                switchToPanelInternal(currentScene.getPanels().get(newIndex));
            } else {
                // No panels left, create a new one
                createNewPanel();
                return;
            }

            refreshSceneTree();
            refreshPanelList();
            updateSceneInfo();
            updateThumbnailGrid();

            statusLabel.setText("Deleted panel: " + deletedPanelName);
        }
    }

    // =====================================
    // PANEL OPERATIONS
    // =====================================

    @FXML
    private void duplicatePanel() {
        if (currentPanel == null || currentScene == null) {
            statusLabel.setText("No panel selected to duplicate");
            return;
        }

        duplicatePanelInternal(currentPanel, true);
    }

    private void duplicatePanelInternal(Panel originalPanel, boolean switchToNew) {
        try {
            // Save current drawing before duplicating
            if (currentPanel == originalPanel) {
                saveCurrentPanelDrawing();
            }

            // Create a copy of the panel
            Panel duplicatedPanel = originalPanel.createCopy();

            // Find the scene containing the original panel
            com.example.scenory.model.Scene targetScene = findSceneContainingPanel(originalPanel); // FIXED
            if (targetScene == null) {
                statusLabel.setText("Cannot find scene for panel");
                return;
            }

            // Find insertion position (right after the original panel)
            int originalIndex = targetScene.getPanels().indexOf(originalPanel);
            int insertIndex = originalIndex + 1;

            // Generate unique name
            String baseName = originalPanel.getName();
            if (baseName.endsWith(" (Copy)")) {
                baseName = baseName.substring(0, baseName.length() - 7);
            }

            String newName = generateUniquePanelName(targetScene, baseName);
            duplicatedPanel.setName(newName);

            // Set sequence order
            duplicatedPanel.setSequenceOrder(insertIndex);

            // Insert the duplicated panel
            targetScene.getPanels().add(insertIndex, duplicatedPanel);

            // Update sequence orders for panels after the inserted one
            for (int i = insertIndex + 1; i < targetScene.getPanels().size(); i++) {
                targetScene.getPanels().get(i).setSequenceOrder(i);
            }

            // Update current scene if different
            if (targetScene != currentScene) {
                currentScene = targetScene;
            }

            // Switch to the new panel if requested
            if (switchToNew) {
                switchToPanelInternal(duplicatedPanel);
            }

            // Refresh UI
            refreshSceneTree();
            refreshPanelList();
            updateSceneInfo();
            updateThumbnailGrid();

            statusLabel.setText("Panel duplicated: " + duplicatedPanel.getName());

        } catch (Exception e) {
            System.err.println("❌ Error duplicating panel: " + e.getMessage());
            e.printStackTrace();
            statusLabel.setText("Error duplicating panel");
        }
    }

    private String generateUniquePanelName(com.example.scenory.model.Scene scene, String baseName) { // FIXED
        String candidateName = baseName + " (Copy)";
        int counter = 1;

        while (isPanelNameTaken(scene, candidateName)) {
            counter++;
            candidateName = baseName + " (Copy " + counter + ")";
        }

        return candidateName;
    }

    private boolean isPanelNameTaken(com.example.scenory.model.Scene scene, String name) { // FIXED
        return scene.getPanels().stream()
                .anyMatch(panel -> panel.getName().equals(name));
    }

    public void duplicateSpecificPanel(Panel panel) {
        if (panel != null) {
            duplicatePanelInternal(panel, false);
        }
    }

    private void duplicatePanel(Panel panel) {
        duplicatePanelInternal(panel, true);
    }

    private void deletePanel(Panel panel) {
        if (panel == currentPanel) {
            deleteCurrentPanel();
        } else {
            // Delete specific panel
            com.example.scenory.model.Scene scene = findSceneContainingPanel(panel); // FIXED
            if (scene != null) {
                scene.getPanels().remove(panel);
                refreshSceneTree();
                updateThumbnailGrid();
                statusLabel.setText("Panel deleted: " + panel.getName());
            }
        }
    }

    private void clearPanelContent(Panel panel) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Clear Panel");
        alert.setHeaderText("Clear " + panel.getName() + "?");
        alert.setContentText("This will permanently delete all drawing on this panel.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            panel.setCanvasImageData(null);
            panel.setThumbnailData(null);

            if (panel == currentPanel && drawingCanvas != null) {
                CanvasPersistence.clearCanvas(drawingCanvas);

                // Clear command history since content is cleared
                if (commandManager != null) {
                    commandManager.clearHistory();
                }
            }

            updateThumbnailGrid();
            statusLabel.setText("Panel cleared: " + panel.getName());
        }
    }

    // =====================================
    // DIALOG METHODS
    // =====================================

    private void showRenamePanelDialog(Panel panel) {
        TextInputDialog dialog = new TextInputDialog(panel.getName());
        dialog.setTitle("Rename Panel");
        dialog.setHeaderText("Enter new name for panel:");
        dialog.setContentText("Name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                panel.setName(name.trim());
                refreshSceneTree();
                updateThumbnailGrid();
                updateSceneInfo();
                statusLabel.setText("Panel renamed: " + name);
            }
        });
    }

    private void showTimingDialog(Panel panel) {
        // Simple timing dialog for now
        TextInputDialog dialog = new TextInputDialog(String.valueOf(panel.getDisplayDuration().toSeconds()));
        dialog.setTitle("Panel Timing");
        dialog.setHeaderText("Set display duration for: " + panel.getName());
        dialog.setContentText("Duration (seconds):");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(durationStr -> {
            try {
                double seconds = Double.parseDouble(durationStr);
                if (seconds > 0 && seconds <= 30) {
                    panel.setDisplayDuration(Duration.seconds(seconds));
                    updateThumbnailGrid();
                    statusLabel.setText("Panel timing updated: " + panel.getFormattedDisplayDuration());
                } else {
                    showError("Invalid Duration", "Duration must be between 0.1 and 30 seconds.");
                }
            } catch (NumberFormatException e) {
                showError("Invalid Input", "Please enter a valid number.");
            }
        });
    }

    private void showBackgroundColorDialog(Panel panel) {
        ColorPicker colorPicker = new ColorPicker(Color.WHITE);
        if (panel.getCanvasBackgroundColor() != null) {
            colorPicker.setValue(Color.web(panel.getCanvasBackgroundColor()));
        }

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Panel Background Color");
        dialog.setHeaderText("Choose background color for: " + panel.getName());
        dialog.getDialogPane().setContent(new VBox(8,
                new Label("Background Color:"), colorPicker));

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Color selectedColor = colorPicker.getValue();
            panel.setCanvasBackgroundColor(selectedColor.toString());

            // Update canvas if this is current panel
            if (panel == currentPanel && drawingCanvas != null) {
                drawingCanvas.setCanvasBackgroundColor(selectedColor);
            }

            updateThumbnailGrid();
            statusLabel.setText("Panel background updated");
        }
    }

    // =====================================
    // NAVIGATION METHODS (called by scene constructor)
    // =====================================

    @FXML
    private void previousPanel() {
        if (currentScene == null || currentPanel == null) return;

        int currentIndex = currentScene.getPanels().indexOf(currentPanel);
        if (currentIndex > 0) {
            switchToPanelInternal(currentScene.getPanels().get(currentIndex - 1));
        }
    }

    @FXML
    private void nextPanel() {
        if (currentScene == null || currentPanel == null) return;

        int currentIndex = currentScene.getPanels().indexOf(currentPanel);
        if (currentIndex < currentScene.getPanels().size() - 1) {
            switchToPanelInternal(currentScene.getPanels().get(currentIndex + 1));
        }
    }

    // =====================================
    // ZOOM MENU METHODS
    // =====================================

    @FXML
    private void zoomIn() {
        if (drawingCanvas != null) {
            drawingCanvas.zoomIn();
            statusLabel.setText("Zoomed in: " + drawingCanvas.getZoomPercentage());
        }
    }

    @FXML
    private void zoomOut() {
        if (drawingCanvas != null) {
            drawingCanvas.zoomOut();
            statusLabel.setText("Zoomed out: " + drawingCanvas.getZoomPercentage());
        }
    }

    @FXML
    private void fitToWindow() {
        if (drawingCanvas != null) {
            drawingCanvas.fitToWindow();
            statusLabel.setText("Zoom fit to window");
        }
    }

    @FXML
    private void actualSize() {
        if (drawingCanvas != null) {
            drawingCanvas.resetZoom();
            statusLabel.setText("Zoom reset to actual size");
        }
    }

    // =====================================
    // MENU ACTION HANDLERS
    // =====================================

    @FXML
    private void backToWelcome() {
        try {
            saveCurrentPanelDrawing();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/scenory/welcome-view.fxml"));
            Parent welcomeView = loader.load();

            Stage stage = (Stage) statusLabel.getScene().getWindow();
            stage.getScene().setRoot(welcomeView);
            stage.setTitle("Scenory - Professional Storyboard Creator");

            System.out.println("🏠 Returned to welcome screen");

        } catch (IOException e) {
            System.err.println("❌ Failed to return to welcome screen: " + e.getMessage());
            e.printStackTrace();
            showError("Navigation Error", "Failed to return to welcome screen.");
        }
    }

    @FXML private void newProject() { backToWelcome(); }
    @FXML private void openProject() { statusLabel.setText("Open project - TODO"); }
    @FXML private void saveProject() { statusLabel.setText("Project saved"); }
    @FXML private void saveProjectAs() { statusLabel.setText("Save as - TODO"); }
    @FXML private void exportPDF() { statusLabel.setText("Exporting to PDF..."); }
    @FXML private void exportImages() { statusLabel.setText("Exporting images..."); }
    @FXML private void exitApplication() { System.exit(0); }
    @FXML private void duplicateScene() { statusLabel.setText("Duplicate scene"); }
    @FXML private void editSceneProperties() { statusLabel.setText("Edit scene properties"); }
    @FXML private void editPanelProperties() { statusLabel.setText("Edit panel properties"); }
    @FXML private void showQuickStart() { showInfo("Quick Start", "Quick start guide coming soon!"); }
    @FXML private void showShortcuts() { showInfo("Shortcuts", "Keyboard shortcuts guide coming soon!"); }

    @FXML
    private void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Scenory");
        alert.setHeaderText("Scenory - Professional Storyboarding Tool");
        alert.setContentText("Version 1.0\n\n" +
                "Enhanced with Rich Text Editor, undo/redo system and keyboard shortcuts\n" +
                "Built with JavaFX");
        alert.showAndWait();
    }

    // =====================================
    // DEBUG AND STATUS METHODS
    // =====================================

    @FXML
    private void showDebugInfo() {
        System.out.println("\n=== SCENORY DEBUG INFO ===");
        printCommandStatus();
        if (shortcutManager != null) {
            shortcutManager.printShortcuts();
        }
        System.out.println("Current Tool: " + currentTool);
        System.out.println("Current Panel: " + (currentPanel != null ? currentPanel.getName() : "None"));
        System.out.println("Current Scene: " + (currentScene != null ? currentScene.getName() : "None"));
        System.out.println("Zoom Level: " + Math.round(zoomLevel * 100) + "%");
        System.out.println("Panel State: " + getCurrentPanelState());
        System.out.println("Rich Text Enabled: true");
        System.out.println("========================\n");
    }

    public void printCommandStatus() {
        if (commandManager != null) {
            commandManager.printStatus();
        }
        if (drawingCanvas != null) {
            drawingCanvas.printCommandStatus();
        }
    }

    // =====================================
    // HELPER METHODS
    // =====================================

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}