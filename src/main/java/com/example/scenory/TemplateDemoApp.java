package com.example.scenory;

import com.example.scenory.view.templates.ModalTemplate;
import com.example.scenory.view.templates.config.ModalConfig;
import com.example.scenory.view.templates.config.ToolsPanelConfig;
import com.example.scenory.view.templates.config.ScenePanelConfig;
import com.example.scenory.view.templates.config.TimelinePanelConfig;
import com.example.scenory.view.templates.config.StatusPanelConfig;
import com.example.scenory.view.templates.panels.ToolsPanelTemplate;
import com.example.scenory.view.templates.panels.ScenePanelTemplate;
import com.example.scenory.view.templates.panels.TimelinePanelTemplate;
import com.example.scenory.view.templates.panels.StatusPanelTemplate;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 * Demo application to test the UI template components.
 * Run this to see ModalTemplate and ToolsPanelTemplate in action.
 */

public class TemplateDemoApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        
        Label title = new Label("UI Template Components Demo");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        // Button to test ModalTemplate with programmatic content
        Button modalButton = new Button("Test Modal Template");
        modalButton.setOnAction(e -> showModalDemo(primaryStage));
        
        // Button to test ToolsPanelTemplate
        Button toolsPanelButton = new Button("Test Tools Panel Template");
        toolsPanelButton.setOnAction(e -> showToolsPanelDemo());
        
        // Button to test ScenePanelTemplate
        Button scenePanelButton = new Button("Test Scene Panel Template");
        scenePanelButton.setOnAction(e -> showScenePanelDemo());
        
        // Button to test TimelinePanelTemplate
        Button timelinePanelButton = new Button("Test Timeline Panel Template");
        timelinePanelButton.setOnAction(e -> showTimelinePanelDemo());
        
        // Button to test StatusPanelTemplate
        Button statusPanelButton = new Button("Test Status Panel Template");
        statusPanelButton.setOnAction(e -> showStatusPanelDemo());
        
        root.getChildren().addAll(title, modalButton, toolsPanelButton, 
            scenePanelButton, timelinePanelButton, statusPanelButton);
        
        Scene scene = new Scene(root, 400, 400);
        primaryStage.setTitle("Template Demo - All Components");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void showModalDemo(Stage owner) {
        // Create modal content
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.getChildren().addAll(
            new Label("This is a modal created with ModalTemplate!"),
            new Label("It has:"),
            new Label("✓ Automatic stage setup"),
            new Label("✓ CSS stylesheet loading"),
            new Label("✓ Center on screen"),
            new Label("✓ Configurable dimensions")
        );
        
        // Create modal configuration
        ModalConfig config = ModalConfig.builder()
            .title("Modal Template Demo")
            .dimensions(500, 350)
            .minDimensions(400, 300)
            .owner(owner)
            .resizable(true)
            .onClose(v -> System.out.println("Modal closed!"))
            .build();
        
        // Create and show modal
        ModalTemplate modal = ModalTemplate.createWithContent(content, config);
        
        // Add a close button to the content
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> modal.close());
        content.getChildren().add(closeButton);
        
        modal.showAndWait();
    }
    
    private void showToolsPanelDemo() {
        // Create tools panel configuration
        ToolsPanelConfig config = ToolsPanelConfig.builder()
            .toolSpacing(10)
            .showLabels(true)
            .iconSize(32)
            .selectionMode(ToolsPanelConfig.SelectionMode.SINGLE)
            .initialSelection("pencil")
            .build();
        
        // Create tools panel
        ToolsPanelTemplate toolsPanel = new ToolsPanelTemplate(config);
        
        // Add tool items with colored shapes as icons
        Circle pencilIcon = new Circle(16, Color.BLUE);
        toolsPanel.addTool("pencil", pencilIcon, "Pencil", 
            id -> System.out.println("Pencil tool selected: " + id));
        
        Rectangle squareIcon = new Rectangle(24, 24, Color.RED);
        toolsPanel.addTool("square", squareIcon, "Square", 
            id -> System.out.println("Square tool selected: " + id));
        
        Circle circleIcon = new Circle(12, Color.GREEN);
        toolsPanel.addTool("circle", circleIcon, "Circle", 
            id -> System.out.println("Circle tool selected: " + id));
        
        // Add a separator
        toolsPanel.addSeparator();
        
        Rectangle eraserIcon = new Rectangle(20, 20, Color.GRAY);
        toolsPanel.addTool("eraser", eraserIcon, "Eraser", 
            id -> System.out.println("Eraser tool selected: " + id));
        
        // Add status display
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));
        
        Label statusLabel = new Label("Selected tool: " + toolsPanel.getSelectedTool());
        statusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        // Update status when selection changes
        Button checkSelectionButton = new Button("Check Selected Tool");
        checkSelectionButton.setOnAction(e -> {
            String selected = toolsPanel.getSelectedTool();
            statusLabel.setText("Selected tool: " + (selected != null ? selected : "none"));
        });
        
        container.getChildren().addAll(
            new Label("Tools Panel Template Demo"),
            toolsPanel,
            statusLabel,
            checkSelectionButton
        );
        
        // Show in a new stage
        Stage stage = new Stage();
        stage.setTitle("Tools Panel Demo");
        stage.setScene(new Scene(container, 300, 500));
        stage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
    private void showScenePanelDemo() {
        // Create scene panel configuration
        ScenePanelConfig config = ScenePanelConfig.builder()
            .thumbnailDimensions(120, 90)
            .showFrameLabels(true)
            .onFrameSelect(frameId -> System.out.println("Frame selected: " + frameId))
            .onFrameDoubleClick(frameId -> System.out.println("Frame double-clicked: " + frameId))
            .build();
        
        // Create scene panel
        ScenePanelTemplate scenePanel = new ScenePanelTemplate(config);
        
        // Add some sample frames with colored thumbnails
        for (int i = 1; i <= 5; i++) {
            WritableImage thumbnail = new WritableImage(120, 90);
            javafx.scene.SnapshotParameters params = new javafx.scene.SnapshotParameters();
            params.setFill(Color.color(Math.random(), Math.random(), Math.random()));
            Rectangle rect = new Rectangle(120, 90, params.getFill());
            thumbnail = rect.snapshot(params, thumbnail);
            
            scenePanel.addFrame("frame-" + i, thumbnail, "Frame " + i);
        }
        
        // Add action buttons
        scenePanel.addActionButton("Add Frame", new Label("+"), () -> 
            System.out.println("Add frame clicked"));
        scenePanel.addActionButton("Delete Frame", new Label("🗑"), () -> 
            System.out.println("Delete frame clicked"));
        
        // Show in a new stage
        Stage stage = new Stage();
        stage.setTitle("Scene Panel Demo");
        stage.setScene(new Scene(scenePanel, 250, 500));
        stage.show();
    }
    
    private void showTimelinePanelDemo() {
        // Create timeline panel configuration
        TimelinePanelConfig config = TimelinePanelConfig.builder()
            .frameMarkerDimensions(60, 40)
            .showPlaybackButton(true)
            .showAddFrameButton(true)
            .onFrameSelect(frameNum -> System.out.println("Frame selected: " + frameNum))
            .onPlaybackToggle(playing -> System.out.println("Playing: " + playing))
            .build();
        
        // Create timeline panel
        TimelinePanelTemplate timeline = new TimelinePanelTemplate(config);
        
        // Add frame markers
        for (int i = 1; i <= 20; i++) {
            timeline.addFrameMarker(i);
        }
        
        // Set up add frame action
        timeline.setOnAddFrame(() -> {
            int nextFrame = timeline.getFrameMarkers().keySet().stream()
                .max(Integer::compareTo).orElse(0) + 1;
            timeline.addFrameMarker(nextFrame);
            System.out.println("Added frame: " + nextFrame);
        });
        
        // Show in a new stage
        VBox container = new VBox(10);
        container.setPadding(new Insets(10));
        container.getChildren().addAll(
            new Label("Timeline Panel Template Demo"),
            timeline
        );
        
        Stage stage = new Stage();
        stage.setTitle("Timeline Panel Demo");
        stage.setScene(new Scene(container, 600, 150));
        stage.show();
    }
    
    private void showStatusPanelDemo() {
        // Create status panel configuration
        StatusPanelConfig config = StatusPanelConfig.builder()
            .initialZoom(100.0)
            .initialFPS(24)
            .initialFrameInfo(1, 10)
            .showZoom(true)
            .showFPS(true)
            .showFrameInfo(true)
            .build();
        
        // Create status panel
        StatusPanelTemplate statusPanel = new StatusPanelTemplate(config);
        
        // Create controls to update status values
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Status Panel Template Demo");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        Button zoomInButton = new Button("Zoom In");
        zoomInButton.setOnAction(e -> {
            double newZoom = statusPanel.getZoom() + 25;
            statusPanel.setZoom(newZoom);
            System.out.println("Zoom: " + newZoom + "%");
        });
        
        Button zoomOutButton = new Button("Zoom Out");
        zoomOutButton.setOnAction(e -> {
            double newZoom = Math.max(25, statusPanel.getZoom() - 25);
            statusPanel.setZoom(newZoom);
            System.out.println("Zoom: " + newZoom + "%");
        });
        
        Button nextFrameButton = new Button("Next Frame");
        nextFrameButton.setOnAction(e -> {
            int newFrame = Math.min(statusPanel.getTotalFrames(), 
                statusPanel.getCurrentFrame() + 1);
            statusPanel.setFrameInfo(newFrame, statusPanel.getTotalFrames());
            System.out.println("Frame: " + newFrame);
        });
        
        Button changeFPSButton = new Button("Change FPS");
        changeFPSButton.setOnAction(e -> {
            int newFPS = statusPanel.getFPS() == 24 ? 30 : 24;
            statusPanel.setFPS(newFPS);
            System.out.println("FPS: " + newFPS);
        });
        
        container.getChildren().addAll(
            titleLabel,
            statusPanel,
            new Label("Try these controls:"),
            zoomInButton,
            zoomOutButton,
            nextFrameButton,
            changeFPSButton
        );
        
        Stage stage = new Stage();
        stage.setTitle("Status Panel Demo");
        stage.setScene(new Scene(container, 400, 350));
        stage.show();
    }
}
