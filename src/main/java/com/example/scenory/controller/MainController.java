package com.example.scenory.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.example.scenory.model.Project;
import com.example.scenory.tools.EraserTool;
import com.example.scenory.tools.PencilTool;
import com.example.scenory.tools.SquareTool;
import com.example.scenory.tools.Tool;
import com.example.scenory.view.templates.config.ToolsPanelConfig;
import com.example.scenory.view.templates.config.ScenePanelConfig;
import com.example.scenory.view.templates.config.TimelinePanelConfig;
import com.example.scenory.view.templates.config.StatusPanelConfig;
import com.example.scenory.view.templates.panels.ToolsPanelTemplate;
import com.example.scenory.view.templates.panels.ScenePanelTemplate;
import com.example.scenory.view.templates.panels.TimelinePanelTemplate;
import com.example.scenory.view.templates.panels.StatusPanelTemplate;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;

public class MainController implements Initializable {

    @FXML private StackPane canvasStackPane;
    @FXML private AnchorPane canvasContainer;
    @FXML private VBox toolsPanelContainer;
    @FXML private VBox scenePanelContainer;
    @FXML private HBox timelinePanelContainer;
    @FXML private HBox statusPanelContainer;
    @FXML private Label statusLabel;
    @FXML private Label canvasSizeLabel;
    @FXML private Label zoomLabel;

    private Project currentProject;
    private Canvas canvas;
    private GraphicsContext gc;
    
    // Template components
    private ToolsPanelTemplate toolsPanel;
    private ScenePanelTemplate scenePanel;
    private TimelinePanelTemplate timelinePanel;
    private StatusPanelTemplate statusPanel;
    
    // Tool management
    private Tool currentTool;
    
    // Drawing state
    private double lastX, lastY;
    private boolean isDrawing = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("🚀 Initializing MainController...");
        
        // Initialize with pencil tool as default
        currentTool = new PencilTool();
        
        // Initialize all panels
        initializeToolsPanel();
        initializeScenePanel();
        initializeTimelinePanel();
        initializeStatusPanel();
        
        initializeCanvas();
        
        if (statusLabel != null) {
            statusLabel.setText("Ready to create amazing storyboards - Pencil selected");
        }
        
        System.out.println("✅ MainController initialized successfully");
    }
    
    private void initializeToolsPanel() {
        // Create tools panel configuration
        ToolsPanelConfig config = ToolsPanelConfig.builder()
            .toolSpacing(8)
            .showLabels(false)
            .iconSize(24)
            .selectionMode(ToolsPanelConfig.SelectionMode.SINGLE)
            .initialSelection("pencil")
            .styleClasses("tools-sidebar")
            .build();
        
        // Create tools panel
        toolsPanel = new ToolsPanelTemplate(config);
        toolsPanel.setAlignment(Pos.TOP_CENTER);
        toolsPanel.setPadding(new Insets(10, 6, 10, 6));
        
        // Create tool icons
        SVGPath pencilIcon = new SVGPath();
        pencilIcon.setContent("M2,14 L2,11 L11,2 L14,5 L5,14 Z");
        pencilIcon.getStyleClass().add("tool-icon-glyph");
        
        Rectangle eraserIcon = new Rectangle(14, 10);
        eraserIcon.setArcWidth(2);
        eraserIcon.setArcHeight(2);
        eraserIcon.getStyleClass().add("tool-icon-glyph-outline");
        
        Rectangle squareIcon = new Rectangle(12, 12);
        squareIcon.getStyleClass().add("tool-icon-glyph-outline");
        
        Circle circleIcon = new Circle(6);
        circleIcon.getStyleClass().add("tool-icon-glyph-outline");
        
        // Add tools to panel
        toolsPanel.addTool("pencil", pencilIcon, "Pencil", this::selectToolById);
        toolsPanel.addTool("eraser", eraserIcon, "Eraser", this::selectToolById);
        toolsPanel.addTool("square", squareIcon, "Square", this::selectToolById);
        toolsPanel.addTool("circle", circleIcon, "Circle", this::selectToolById);
        
        // Add the tools panel to the container
        if (toolsPanelContainer != null) {
            toolsPanelContainer.getChildren().add(toolsPanel);
            System.out.println("✅ Tools panel initialized with ToolsPanelTemplate");
        }
    }
    
    private void selectToolById(String toolId) {
        switch (toolId) {
            case "pencil":
                selectPencilTool();
                break;
            case "eraser":
                selectEraserTool();
                break;
            case "square":
                selectSquareTool();
                break;
            case "circle":
                selectCircleTool();
                break;
            default:
                System.out.println("⚠️ Unknown tool: " + toolId);
        }
    }
    
    private void initializeScenePanel() {
        // Create scene panel configuration
        ScenePanelConfig config = ScenePanelConfig.builder()
            .thumbnailDimensions(200, 150)
            .showFrameLabels(true)
            .enableLayerMode(false)
            .onFrameSelect(frameId -> {
                System.out.println("📸 Frame selected: " + frameId);
                if (statusLabel != null) {
                    statusLabel.setText("Frame: " + frameId);
                }
            })
            .onFrameDoubleClick(frameId -> System.out.println("📸 Frame double-clicked: " + frameId))
            .styleClasses("scene-sidebar")
            .build();
        
        // Create scene panel
        scenePanel = new ScenePanelTemplate(config);
        
        // Add some sample frames
        for (int i = 1; i <= 5; i++) {
            WritableImage thumbnail = createSampleThumbnail();
            scenePanel.addFrame("frame-" + i, thumbnail, "Frame " + i);
        }
        
        // Add action buttons
        scenePanel.addActionButton("Add Frame", new Label("+"), () -> {
            System.out.println("➕ Add frame clicked");
            // Count frames by checking the internal map size via public getter
            int nextFrame = 1;
            while (scenePanel.getSelectedFrame() != null || nextFrame <= 100) {
                String testId = "frame-" + nextFrame;
                // Just create a new frame - ScenePanelTemplate will handle duplicates
                WritableImage thumbnail = createSampleThumbnail();
                scenePanel.addFrame(testId, thumbnail, "Frame " + nextFrame);
                break;
            }
        });
        
        scenePanel.addActionButton("Delete Frame", new Label("🗑"), () -> {
            String selected = scenePanel.getSelectedFrame();
            if (selected != null) {
                System.out.println("🗑 Delete frame: " + selected);
                scenePanel.removeFrame(selected);
            }
        });
        
        // Add to container
        if (scenePanelContainer != null) {
            scenePanelContainer.getChildren().add(scenePanel);
            System.out.println("✅ Scene panel initialized");
        }
    }
    
    private void initializeTimelinePanel() {
        // Create timeline panel configuration
        TimelinePanelConfig config = TimelinePanelConfig.builder()
            .frameMarkerDimensions(60, 50)
            .showPlaybackButton(true)
            .showAddFrameButton(true)
            .onFrameSelect(frameNum -> {
                System.out.println("🎬 Timeline frame selected: " + frameNum);
                if (statusLabel != null) {
                    statusLabel.setText("Timeline: Frame " + frameNum);
                }
            })
            .onPlaybackToggle(playing -> {
                System.out.println(playing ? "▶️ Playing" : "⏸️ Paused");
                if (statusLabel != null) {
                    statusLabel.setText(playing ? "Playing animation" : "Paused");
                }
            })
            .styleClasses("timeline-container")
            .build();
        
        // Create timeline panel
        timelinePanel = new TimelinePanelTemplate(config);
        
        // Add frame markers (matching scene panel frames)
        for (int i = 1; i <= 5; i++) {
            timelinePanel.addFrameMarker(i);
        }
        
        // Set up add frame action
        timelinePanel.setOnAddFrame(() -> {
            int nextFrame = timelinePanel.getFrameMarkers().keySet().stream()
                .max(Integer::compareTo).orElse(0) + 1;
            timelinePanel.addFrameMarker(nextFrame);
            System.out.println("➕ Added timeline frame: " + nextFrame);
        });
        
        // Add to container
        if (timelinePanelContainer != null) {
            timelinePanelContainer.getChildren().add(timelinePanel);
            System.out.println("✅ Timeline panel initialized");
        }
    }
    
    private void initializeStatusPanel() {
        // Create status panel configuration
        StatusPanelConfig config = StatusPanelConfig.builder()
            .initialZoom(100.0)
            .initialFPS(30)
            .initialFrameInfo(1, 5)
            .showZoom(true)
            .showFPS(true)
            .showFrameInfo(true)
            .styleClasses("status-bar")
            .build();
        
        // Create status panel
        statusPanel = new StatusPanelTemplate(config);
        
        // Add to container
        if (statusPanelContainer != null) {
            statusPanelContainer.getChildren().add(statusPanel);
            System.out.println("✅ Status panel initialized");
        }
    }
    
    private WritableImage createSampleThumbnail() {
        WritableImage thumbnail = new WritableImage(200, 150);
        javafx.scene.SnapshotParameters params = new javafx.scene.SnapshotParameters();
        params.setFill(Color.color(0.9, 0.9, 0.9));
        Rectangle rect = new Rectangle(200, 150, params.getFill());
        return rect.snapshot(params, thumbnail);
    }
    
    private void initializeCanvas() {
        // Create canvas with smaller size to leave room for panels
        canvas = new Canvas(840, 580);
        gc = canvas.getGraphicsContext2D();
        
        // Set default drawing properties
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
        
        // Clear canvas with white background
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, 840, 580);
        
        // Add canvas to container
        if (canvasContainer != null) {
            canvasContainer.getChildren().add(canvas);
            
            // Set up mouse event handlers for drawing
            canvas.setOnMousePressed(this::handleMousePressed);
            canvas.setOnMouseDragged(this::handleMouseDragged);
            canvas.setOnMouseReleased(this::handleMouseReleased);
            
            System.out.println("🎨 Canvas initialized: 840x580");
        }
    }
    
    private void handleMousePressed(MouseEvent event) {
        if (currentTool != null) {
            currentTool.onMousePressed(event, gc);
        }
        
        if (statusLabel != null) {
            statusLabel.setText("Drawing with " + (currentTool != null ? currentTool.getName() : "tool") + "...");
        }
    }
    
    private void handleMouseDragged(MouseEvent event) {
        if (currentTool != null) {
            currentTool.onMouseDragged(event, gc);
        }
    }
    
    private void handleMouseReleased(MouseEvent event) {
        if (currentTool != null) {
            currentTool.onMouseReleased(event, gc);
        }
        
        if (statusLabel != null) {
            statusLabel.setText((currentTool != null ? currentTool.getName() : "Tool") + " selected");
        }
    }

    public void loadProject(Project project) {
        this.currentProject = project;
        System.out.println("📂 Loading project: " + project.getName());
        
        if (statusLabel != null) {
            statusLabel.setText("Project: " + project.getName());
        }
    }

    // Placeholder methods to prevent compilation errors
    @FXML private void newProject() { }
    @FXML private void openProject() { }
    @FXML private void saveProject() { }
    @FXML private void saveProjectAs() { }
    @FXML private void exportPDF() { }
    @FXML private void exportImages() { }
    @FXML private void backToWelcome() { }
    @FXML private void exitApplication() { System.exit(0); }
    @FXML private void undo() { }
    @FXML private void redo() { }
    @FXML private void clearCanvas() { 
        if (gc != null) {
            gc.setFill(Color.WHITE);
            gc.fillRect(0, 0, 840, 580);
        }
    }
    @FXML private void createNewScene() { }
    @FXML private void createNewPanel() { }
    @FXML private void duplicatePanel() { }
    @FXML private void editPanelDescription() { }
    
    // Tool selection methods
    @FXML private void selectPencilTool() {
        currentTool = new PencilTool();
        if (statusLabel != null) {
            statusLabel.setText("Pencil tool selected");
        }
        System.out.println("✏️ Pencil tool selected");
    }
    
    @FXML private void selectEraserTool() {
        currentTool = new EraserTool();
        if (statusLabel != null) {
            statusLabel.setText("Eraser tool selected");
        }
        System.out.println("🧹 Eraser tool selected");
    }
    
    @FXML private void selectSquareTool() {
        currentTool = new SquareTool();
        if (statusLabel != null) {
            statusLabel.setText("Square tool selected");
        }
        System.out.println("⬜ Square tool selected");
    }
    
    // Legacy tool methods (kept for compatibility)
    @FXML private void selectPenTool() { selectPencilTool(); }
    @FXML private void selectBrushTool() { selectPencilTool(); }
    @FXML private void selectRectangleTool() { selectSquareTool(); }
    @FXML private void selectCircleTool() { }
    @FXML private void selectLineTool() { }
    @FXML private void selectTextTool() { }
    @FXML private void zoomIn() { }
    @FXML private void zoomOut() { }
    @FXML private void fitToWindow() { }
    @FXML private void actualSize() { }
    @FXML private void toggleLeftPanel() { }
    @FXML private void toggleRightPanel() { }
    @FXML private void showQuickStart() { }
    @FXML private void showShortcuts() { }
    @FXML private void showDebugInfo() { }
    @FXML private void showAbout() { }
    
    // Method called by DragAndDropHandler
    public void duplicateSpecificPanel(com.example.scenory.model.Panel panel) {
        System.out.println("📋 Duplicate panel requested: " + (panel != null ? panel.getName() : "null"));
    }
    
    // Navigation methods
    @FXML private void previousPanel() {
        System.out.println("◀ Previous panel");
    }
    
    @FXML private void nextPanel() {
        System.out.println("▶ Next panel");
    }
}
