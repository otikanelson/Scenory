package com.example.scenory.controller;

import com.example.scenory.model.*;
import com.example.scenory.utils.ThumbnailGenerator;
import com.example.scenory.utils.DragAndDropHandler;
import com.example.scenory.view.dialogs.RichTextModal;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Window;
import javafx.util.Duration;

import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Manages all UI updates and interactions
 */
public class UIManager {

    private final ProjectManager projectManager;
    private final PanelNavigator panelNavigator;

    // UI Components (references passed from MainController)
    private TreeView<Object> sceneTreeView;
    private GridPane thumbnailGrid;
    private Label statusLabel;
    private Label canvasSizeLabel;
    private Label zoomLabel;

    // State tracking
    private boolean isUpdatingSelection = false;

    public UIManager(ProjectManager projectManager, PanelNavigator panelNavigator) {
        this.projectManager = projectManager;
        this.panelNavigator = panelNavigator;
    }

    public void setUIComponents(TreeView<Object> sceneTreeView, GridPane thumbnailGrid,
                                Label statusLabel, Label canvasSizeLabel, Label zoomLabel) {
        this.sceneTreeView = sceneTreeView;
        this.thumbnailGrid = thumbnailGrid;
        this.statusLabel = statusLabel;
        this.canvasSizeLabel = canvasSizeLabel;
        this.zoomLabel = zoomLabel;

        setupTreeViewHandlers();
    }

    private void setupTreeViewHandlers() {
        if (sceneTreeView != null) {
            // Setup drag and drop
            DragAndDropHandler.setupTreeViewDragAndDrop(sceneTreeView, this::onDragDropComplete);

            // Tree selection handler
            sceneTreeView.getSelectionModel().selectedItemProperty().addListener(
                    (obs, oldSelection, newSelection) -> {
                        if (isUpdatingSelection) return;

                        if (newSelection != null) {
                            Object selectedItem = newSelection.getValue();
                            if (selectedItem instanceof Scene) {
                                panelNavigator.handleSceneSelection((Scene) selectedItem);
                                refreshAll();
                            } else if (selectedItem instanceof Panel) {
                                panelNavigator.switchToPanel((Panel) selectedItem);
                                refreshAll();
                            }
                        }
                    }
            );
        }
    }

    public void refreshAll() {
        refreshSceneTree();
        updateThumbnailGrid();
        updateSceneInfo();
    }

    public void refreshSceneTree() {
        if (sceneTreeView == null) return;

        isUpdatingSelection = true;
        try {
            TreeItem<Object> rootItem = new TreeItem<>(projectManager.getCurrentProject());
            rootItem.setExpanded(true);

            for (Scene scene : projectManager.getCurrentProject().getScenes()) {
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
            updateSelections();

        } finally {
            isUpdatingSelection = false;
        }
    }

    public void updateThumbnailGrid() {
        if (thumbnailGrid == null || projectManager.getCurrentScene() == null) return;

        thumbnailGrid.getChildren().clear();

        int row = 0;
        for (Panel panel : projectManager.getCurrentScene().getPanels()) {
            Button thumbnailBtn = createThumbnailButton(panel);
            thumbnailGrid.add(thumbnailBtn, 0, row);
            row++;
        }
    }

    private Button createThumbnailButton(Panel panel) {
        VBox thumbnailContainer = new VBox(6);
        thumbnailContainer.getStyleClass().add("enhanced-thumbnail-container");

        // Thumbnail image
        ImageView imageView = createThumbnailImage(panel);

        // Panel title with indicators
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

        // Timing indicator
        Label timingLabel = new Label(panel.getFormattedDisplayDuration());
        timingLabel.getStyleClass().add("timing-indicator");

        thumbnailContainer.getChildren().addAll(imageView, titleContainer, timingLabel);

        Button thumbnailBtn = new Button();
        thumbnailBtn.setGraphic(thumbnailContainer);
        thumbnailBtn.getStyleClass().add("large-thumbnail-button");
        thumbnailBtn.setPrefSize(240, 180);
        thumbnailBtn.setMaxSize(240, 180);
        thumbnailBtn.setMinSize(240, 180);

        // Context menu
        ContextMenu contextMenu = createPanelContextMenu(panel);
        thumbnailBtn.setContextMenu(contextMenu);

        // Click handler
        thumbnailBtn.setOnAction(e -> {
            if (panel != projectManager.getCurrentPanel()) {
                panelNavigator.switchToPanel(panel);
                refreshAll();
            }
        });

        // Selection styling
        if (panel == projectManager.getCurrentPanel()) {
            thumbnailBtn.getStyleClass().add("selected");
            thumbnailBtn.setStyle("-fx-border-color: #bf5700; -fx-border-width: 3; -fx-background-color: #5a5a5a;");
        }

        return thumbnailBtn;
    }

    private ImageView createThumbnailImage(Panel panel) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(220);
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(true);

        // Try to load existing thumbnail first
        if (ThumbnailGenerator.isValidThumbnail(panel.getThumbnailData())) {
            try {
                Image thumbnail = ThumbnailGenerator.bytesToImage(panel.getThumbnailData());
                if (thumbnail != null && !thumbnail.isError()) {
                    imageView.setImage(thumbnail);
                    return imageView;
                }
            } catch (Exception e) {
                System.err.println("Error loading thumbnail for " + panel.getName() + ": " + e.getMessage());
            }
        }

        // If no valid thumbnail, create a default placeholder
        try {
            Image placeholder = createDefaultThumbnailImage(panel);
            imageView.setImage(placeholder);
        } catch (Exception e) {
            System.err.println("Error creating placeholder for " + panel.getName() + ": " + e.getMessage());
            // Set a simple gray background as last resort
            imageView.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-width: 1;");
        }

        return imageView;
    }

    private Image createDefaultThumbnailImage(Panel panel) {
        try {
            // Create a simple placeholder image in JavaFX
            javafx.scene.canvas.Canvas tempCanvas = new javafx.scene.canvas.Canvas(220, 120);
            javafx.scene.canvas.GraphicsContext gc = tempCanvas.getGraphicsContext2D();

            // Light gray background
            gc.setFill(Color.web("#f8f8f8"));
            gc.fillRect(0, 0, 220, 120);

            // Border
            gc.setStroke(Color.web("#dddddd"));
            gc.setLineWidth(1);
            gc.strokeRect(0.5, 0.5, 219, 119);

            // Text
            gc.setFill(Color.web("#999999"));
            gc.setFont(javafx.scene.text.Font.font("Arial", 14));

            String text = panel.hasDrawingData() ? "Loading..." : "Empty Panel";
            javafx.geometry.Bounds textBounds = new javafx.scene.text.Text(text).getBoundsInLocal();
            double textX = (220 - textBounds.getWidth()) / 2;
            double textY = (120 + textBounds.getHeight()) / 2;

            gc.fillText(text, textX, textY);

            // Convert canvas to image
            javafx.scene.image.WritableImage writableImage = new javafx.scene.image.WritableImage(220, 120);
            tempCanvas.snapshot(null, writableImage);

            return writableImage;

        } catch (Exception e) {
            System.err.println("Error creating default thumbnail: " + e.getMessage());
            return null;
        }
    }

    private ContextMenu createPanelContextMenu(Panel panel) {
        ContextMenu contextMenu = new ContextMenu();

        // Rename Panel
        MenuItem renameItem = new MenuItem("Rename Panel");
        renameItem.setGraphic(new Label("✏️"));
        renameItem.setOnAction(e -> showRenamePanelDialog(panel));

        // Edit Description (Rich Text)
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
        duplicateItem.setOnAction(e -> {
            projectManager.duplicatePanel(panel);
            refreshAll();
        });

        MenuItem deleteItem = new MenuItem("Delete Panel");
        deleteItem.setGraphic(new Label("🗑️"));
        deleteItem.setOnAction(e -> {
            if (projectManager.deletePanel(panel)) {
                refreshAll();
            }
        });

        MenuItem clearItem = new MenuItem("Clear Content");
        clearItem.setGraphic(new Label("🧹"));
        clearItem.setOnAction(e -> {
            projectManager.clearPanelContent(panel, null); // Canvas will be handled elsewhere
            refreshAll();
        });

        contextMenu.getItems().addAll(duplicateItem, deleteItem, clearItem);

        return contextMenu;
    }

    public void updateSceneInfo() {
        // This would update scene info labels, zoom info, etc.
        // Implementation depends on your specific UI components
    }

    private void updateSelections() {
        isUpdatingSelection = true;
        try {
            if (sceneTreeView != null && projectManager.getCurrentPanel() != null) {
                TreeItem<Object> panelItem = findPanelTreeItem(sceneTreeView.getRoot(), projectManager.getCurrentPanel());
                if (panelItem != null) {
                    sceneTreeView.getSelectionModel().select(panelItem);
                }
            }
        } finally {
            isUpdatingSelection = false;
        }
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

    // Dialog methods
    private void showRenamePanelDialog(Panel panel) {
        TextInputDialog dialog = new TextInputDialog(panel.getName());
        dialog.setTitle("Rename Panel");
        dialog.setHeaderText("Enter new name for panel:");
        dialog.setContentText("Name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                panel.setName(name.trim());
                refreshAll();
                updateStatus("Panel renamed: " + name);
            }
        });
    }

    private void openRichTextEditor(Panel panel) {
        try {
            Window parentWindow = statusLabel.getScene().getWindow();
            RichTextModal.openForPanel(panel, parentWindow, (content) -> {
                refreshAll();
                updateStatus("✅ Panel description updated: " + panel.getName());
            });
        } catch (Exception e) {
            System.err.println("❌ Failed to open rich text editor: " + e.getMessage());
            showSimpleTextInputFallback(panel);
        }
    }

    private void showSimpleTextInputFallback(Panel panel) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Edit Panel Description");
        dialog.setHeaderText("Edit description for: " + panel.getName());

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        TextArea textArea = new TextArea();
        textArea.setText(panel.getDescriptionPlainText() != null ? panel.getDescriptionPlainText() : "");
        textArea.setPromptText("Enter panel description here...");
        textArea.setPrefRowCount(8);
        textArea.setPrefColumnCount(50);
        textArea.setWrapText(true);

        VBox container = new VBox(10);
        container.getChildren().addAll(new Label("Description:"), textArea);
        container.setPrefWidth(500);

        dialog.getDialogPane().setContent(container);
        javafx.application.Platform.runLater(() -> textArea.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return textArea.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(description -> {
            panel.setDescriptionPlainText(description);
            panel.setDescriptionRichText(description);
            refreshAll();
            updateStatus("✅ Panel description updated (fallback mode): " + panel.getName());
        });
    }

    private void showTimingDialog(Panel panel) {
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
                    refreshAll();
                    updateStatus("Panel timing updated: " + panel.getFormattedDisplayDuration());
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
            refreshAll();
            updateStatus("Panel background updated");
        }
    }

    private void onDragDropComplete() {
        panelNavigator.saveCurrentPanelDrawing();
        refreshAll();
        updateStatus("Panel reordered successfully");
    }

    // Utility methods
    public void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }

    public void updateCanvasSizeLabel(double width, double height) {
        if (canvasSizeLabel != null) {
            canvasSizeLabel.setText(String.format("📐 Canvas: %.0fx%.0f", width, height));
        }
    }

    public void updateZoomLabel(double zoomLevel) {
        if (zoomLabel != null) {
            zoomLabel.setText("🔍 " + Math.round(zoomLevel * 100) + "%");
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}