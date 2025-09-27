package com.example.scenory.view.panels;

import com.example.scenory.enums.DrawingTool;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ToolSelectionPanel extends VBox {

    // Properties for tool state
    private ObjectProperty<DrawingTool> selectedTool = new SimpleObjectProperty<>(DrawingTool.PEN);
    private ObjectProperty<Color> selectedColor = new SimpleObjectProperty<>(Color.BLACK);
    private DoubleProperty strokeSize = new SimpleDoubleProperty(2.0);

    // Tool groups
    private ToggleGroup toolGroup;

    // UI Components
    private VBox toolsContainer;
    private VBox optionsContainer;

    public ToolSelectionPanel() {
        initializeComponent();
        setupToolGroup();
        createToolButtons();
        createToolOptions();

        System.out.println("🔧 ToolSelectionPanel created");
    }

    private void initializeComponent() {
        this.getStyleClass().add("tool-selection-panel");
        this.setSpacing(8);
        this.setPadding(new Insets(8));
        this.setPrefWidth(200);
    }

    private void setupToolGroup() {
        toolGroup = new ToggleGroup();

        // Listen for tool selection changes
        toolGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                DrawingTool tool = (DrawingTool) newToggle.getUserData();
                selectedTool.set(tool);
                System.out.println("🛠️ Tool selected: " + tool.getDisplayName());
            }
        });
    }

    private void createToolButtons() {
        // Tools section header
        Label toolsHeader = new Label("Drawing Tools");
        toolsHeader.getStyleClass().add("tool-section-header");

        toolsContainer = new VBox(4);
        toolsContainer.getStyleClass().add("tools-container");

        // Create tool buttons
        addToolButton("🖊", "Pen", DrawingTool.PEN, true);
        addToolButton("🖌", "Brush", DrawingTool.BRUSH, false);
        addToolButton("✏", "Pencil", DrawingTool.PENCIL, false);

        // Separator
        Separator separator1 = new Separator();
        toolsContainer.getChildren().add(separator1);

        // Modification tools
        addToolButton("🗑", "Eraser", DrawingTool.ERASER, false);
        addToolButton("🪣", "Fill", DrawingTool.FILL, false);

        // Another separator
        Separator separator2 = new Separator();
        toolsContainer.getChildren().add(separator2);

        // Text tools
        addToolButton("T", "Text", DrawingTool.TEXT, false);

        this.getChildren().addAll(toolsHeader, toolsContainer);
    }

    private void addToolButton(String icon, String name, DrawingTool tool, boolean selected) {
        HBox toolRow = new HBox(8);
        toolRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        ToggleButton button = new ToggleButton();
        button.setText(icon + " " + name);
        button.getStyleClass().add("tool-button");
        button.setToggleGroup(toolGroup);
        button.setUserData(tool);
        button.setSelected(selected);
        button.setPrefWidth(160);

        Tooltip.install(button, new Tooltip(name + " tool"));

        toolRow.getChildren().add(button);
        toolsContainer.getChildren().add(toolRow);
    }

    private void createToolOptions() {
        // Tool options section
        Label optionsHeader = new Label("Tool Options");
        optionsHeader.getStyleClass().add("tool-section-header");

        optionsContainer = new VBox(8);
        optionsContainer.getStyleClass().add("tool-options-container");

        // Stroke size
        Label sizeLabel = new Label("Brush Size:");
        sizeLabel.getStyleClass().add("tool-option-label");

        Slider sizeSlider = new Slider(1, 50, 2);
        sizeSlider.getStyleClass().add("tool-option-slider");
        sizeSlider.setShowTickLabels(false);
        sizeSlider.setShowTickMarks(false);
        sizeSlider.setPrefWidth(150);

        Label sizeValueLabel = new Label("2");
        sizeValueLabel.getStyleClass().add("tool-option-value");
        sizeValueLabel.setPrefWidth(30);

        // Bind slider to property
        strokeSize.bind(sizeSlider.valueProperty());
        sizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            sizeValueLabel.setText(String.valueOf(Math.round(newVal.doubleValue())));
        });

        HBox sizeContainer = new HBox(8);
        sizeContainer.getChildren().addAll(sizeSlider, sizeValueLabel);

        // Color picker
        Label colorLabel = new Label("Color:");
        colorLabel.getStyleClass().add("tool-option-label");

        ColorPicker colorPicker = new ColorPicker(Color.BLACK);
        colorPicker.getStyleClass().add("tool-option-color");
        colorPicker.setPrefWidth(150);

        // Bind color picker to property
        selectedColor.bind(colorPicker.valueProperty());
        colorPicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("🎨 Color changed to: " + newVal);
        });

        optionsContainer.getChildren().addAll(
                sizeLabel, sizeContainer,
                colorLabel, colorPicker
        );

        this.getChildren().addAll(optionsHeader, optionsContainer);
    }

    // Public API for accessing tool state
    public DrawingTool getSelectedTool() {
        return selectedTool.get();
    }

    public ObjectProperty<DrawingTool> selectedToolProperty() {
        return selectedTool;
    }

    public Color getSelectedColor() {
        return selectedColor.get();
    }

    public ObjectProperty<Color> selectedColorProperty() {
        return selectedColor;
    }

    public double getStrokeSize() {
        return strokeSize.get();
    }

    public DoubleProperty strokeSizeProperty() {
        return strokeSize;
    }

    public void setSelectedTool(DrawingTool tool) {
        // Find and select the corresponding toggle button
        for (Toggle toggle : toolGroup.getToggles()) {
            if (toggle.getUserData() == tool) {
                toolGroup.selectToggle(toggle);
                break;
            }
        }
    }
}