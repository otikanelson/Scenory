package com.example.scenory.controller;

import com.example.scenory.model.*;
import com.example.scenory.enums.DrawingTool;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private ListView<Panel> panelListView;
    @FXML private AnchorPane canvasContainer;
    @FXML private ScrollPane canvasScrollPane;
    @FXML private ColorPicker colorPicker;
    @FXML private Slider strokeSlider;
    @FXML private ToggleButton penBtn, brushBtn, eraserBtn;
    @FXML private Button newPanelBtn;
    @FXML private Label statusLabel, canvasSizeLabel;

    private Project currentProject;
    private Panel currentPanel;
    private Canvas drawingCanvas;
    private GraphicsContext gc;
    private ObservableList<Panel> panelList;
    private ToggleGroup toolToggleGroup;
    private DrawingTool currentTool = DrawingTool.PEN;

    // Drawing state variables
    private double lastX, lastY;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeProject();
        initializeUI();
        initializeCanvas();
        createFirstPanel();
        setupEventHandlers();
    }

    private void initializeProject() {
        currentProject = new Project();
        currentProject.setName("Untitled Project");

        // Create default scene
        Scene defaultScene = new Scene();
        defaultScene.setName("Scene 1");
        defaultScene.setSequenceOrder(0);
        currentProject.getScenes().add(defaultScene);
    }

    private void initializeUI() {
        panelList = FXCollections.observableArrayList();
        panelListView.setItems(panelList);

        // Setup tool toggle group
        toolToggleGroup = new ToggleGroup();
        penBtn.setToggleGroup(toolToggleGroup);
        brushBtn.setToggleGroup(toolToggleGroup);
        eraserBtn.setToggleGroup(toolToggleGroup);
        penBtn.setSelected(true);

        // Panel list selection handler
        panelListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldPanel, newPanel) -> {
                    if (newPanel != null) {
                        switchToPanel(newPanel);
                    }
                }
        );
    }

    private void initializeCanvas() {
        drawingCanvas = new Canvas(800, 600);
        gc = drawingCanvas.getGraphicsContext2D();

        // Initialize canvas with white background
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, drawingCanvas.getWidth(), drawingCanvas.getHeight());
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.0);

        canvasContainer.getChildren().add(drawingCanvas);

        // Anchor the canvas
        AnchorPane.setTopAnchor(drawingCanvas, 0.0);
        AnchorPane.setLeftAnchor(drawingCanvas, 0.0);

        updateCanvasSizeLabel();
    }

    private void setupEventHandlers() {
        // Canvas mouse events
        drawingCanvas.setOnMousePressed(event -> {
            lastX = event.getX();
            lastY = event.getY();

            if (currentTool == DrawingTool.PEN || currentTool == DrawingTool.BRUSH) {
                gc.beginPath();
                gc.moveTo(lastX, lastY);
                gc.stroke();
            }
        });

        drawingCanvas.setOnMouseDragged(event -> {
            double currentX = event.getX();
            double currentY = event.getY();

            switch (currentTool) {
                case PEN:
                case BRUSH:
                    gc.lineTo(currentX, currentY);
                    gc.stroke();
                    break;
                case ERASER:
                    double strokeWidth = strokeSlider.getValue();
                    gc.clearRect(currentX - strokeWidth/2, currentY - strokeWidth/2,
                            strokeWidth, strokeWidth);
                    break;
            }

            lastX = currentX;
            lastY = currentY;
        });

        // Stroke slider change handler
        strokeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            gc.setLineWidth(newVal.doubleValue());
            statusLabel.setText("Stroke size: " + Math.round(newVal.doubleValue()));
        });
    }

    @FXML
    private void createNewPanel() {
        Panel newPanel = new Panel();
        newPanel.setName("Panel " + (panelList.size() + 1));
        newPanel.setSequenceOrder(panelList.size());

        // Add to current scene (first scene for now)
        currentProject.getScenes().get(0).getPanels().add(newPanel);
        panelList.add(newPanel);

        // Select the new panel
        panelListView.getSelectionModel().select(newPanel);

        statusLabel.setText("Created new panel: " + newPanel.getName());
    }

    private void switchToPanel(Panel panel) {
        currentPanel = panel;
        // Clear the canvas and set white background
        gc.clearRect(0, 0, drawingCanvas.getWidth(), drawingCanvas.getHeight());
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, drawingCanvas.getWidth(), drawingCanvas.getHeight());

        statusLabel.setText("Switched to: " + panel.getName());
    }

    // Tool Selection Methods
    @FXML
    private void selectPenTool() {
        currentTool = DrawingTool.PEN;
        statusLabel.setText("Pen tool selected");
    }

    @FXML
    private void selectBrushTool() {
        currentTool = DrawingTool.BRUSH;
        statusLabel.setText("Brush tool selected");
    }

    @FXML
    private void selectEraserTool() {
        currentTool = DrawingTool.ERASER;
        statusLabel.setText("Eraser tool selected");
    }

    @FXML
    private void colorChanged() {
        Color selectedColor = colorPicker.getValue();
        gc.setStroke(selectedColor);
        statusLabel.setText("Color changed");
    }

    // File Operations
    @FXML
    private void newProject() {
        statusLabel.setText("New project");
    }

    @FXML
    private void openProject() {
        statusLabel.setText("Open project");
    }

    @FXML
    private void saveProject() {
        statusLabel.setText("Save project");
    }

    @FXML
    private void exitApplication() {
        System.exit(0);
    }

    @FXML
    private void undo() {
        statusLabel.setText("Undo");
    }

    @FXML
    private void redo() {
        statusLabel.setText("Redo");
    }

    private void createFirstPanel() {
        createNewPanel();
    }

    private void updateCanvasSizeLabel() {
        canvasSizeLabel.setText(String.format("Canvas: %.0fx%.0f",
                drawingCanvas.getWidth(), drawingCanvas.getHeight()));
    }
}