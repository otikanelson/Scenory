package com.example.scenory.utils;

import com.example.scenory.controller.MainController;
import com.example.scenory.model.*;
import javafx.scene.control.*;
import javafx.scene.input.*;

public class DragAndDropHandler {

    public static void setupTreeViewDragAndDrop(TreeView<Object> treeView, Runnable onDropComplete) {

        treeView.setCellFactory(tv -> {
            TreeCell<Object> cell = new TreeCell<Object>() {
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                        setStyle("");
                    } else if (item instanceof Scene) {
                        Scene scene = (Scene) item;
                        setText("📁 " + scene.getName() + " (" + scene.getPanels().size() + " panels)");
                        setStyle("-fx-font-weight: bold;");
                    } else if (item instanceof Panel) {
                        Panel panel = (Panel) item;
                        String indicator = panel.hasDrawingData() ? " ✓" : "";
                        setText("🎬 " + panel.getName() + indicator);
                        setStyle("-fx-padding: 0 0 0 20;");
                    } else if (item instanceof Project) {
                        Project project = (Project) item;
                        setText("📚 " + project.getName());
                        setStyle("-fx-font-weight: bold;");
                    }
                }
            };

            // Drag detection
            cell.setOnDragDetected(event -> {
                if (cell.getItem() instanceof Panel && !cell.isEmpty()) {
                    Panel panel = (Panel) cell.getItem();

                    Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.putString("SCENORY_PANEL:" + panel.getId());
                    db.setContent(content);

                    cell.setStyle(cell.getStyle() + "; -fx-background-color: #0078d4; -fx-opacity: 0.7;");
                    event.consume();
                }
            });

            // Drag over with visual feedback
            cell.setOnDragOver(event -> {
                if (event.getGestureSource() != cell &&
                        event.getDragboard().hasString() &&
                        event.getDragboard().getString().startsWith("SCENORY_PANEL:")) {

                    Object targetItem = cell.getItem();

                    if (targetItem instanceof Scene || targetItem instanceof Panel) {
                        event.acceptTransferModes(TransferMode.MOVE);

                        String currentStyle = cell.getStyle();
                        if (targetItem instanceof Panel) {
                            double cellHeight = cell.getHeight();
                            double mouseY = event.getY();
                            boolean dropInUpperHalf = mouseY < cellHeight / 2;

                            if (dropInUpperHalf) {
                                if (!currentStyle.contains("border-top")) {
                                    cell.setStyle(currentStyle + "; -fx-border-top: 3px solid #28a745;");
                                }
                            } else {
                                if (!currentStyle.contains("border-bottom")) {
                                    cell.setStyle(currentStyle + "; -fx-border-bottom: 3px solid #28a745;");
                                }
                            }
                        } else {
                            if (!currentStyle.contains("background-color: #28a745")) {
                                cell.setStyle(currentStyle + "; -fx-background-color: #28a745; -fx-opacity: 0.5;");
                            }
                        }
                    }
                }
                event.consume();
            });

            // Remove visual feedback on drag exit
            cell.setOnDragExited(event -> {
                String style = cell.getStyle();
                style = style.replaceAll("; -fx-background-color: #28a745; -fx-opacity: 0\\.5", "");
                style = style.replaceAll("; -fx-border-top: 3px solid #28a745", "");
                style = style.replaceAll("; -fx-border-bottom: 3px solid #28a745", "");
                cell.setStyle(style);
                event.consume();
            });

            // Handle drop
            cell.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                boolean success = false;

                if (db.hasString() && db.getString().startsWith("SCENORY_PANEL:")) {
                    String panelId = db.getString().substring(14);
                    success = performPanelDrop(treeView, cell, panelId, event.getY());

                    if (success && onDropComplete != null) {
                        onDropComplete.run();
                    }
                }

                event.setDropCompleted(success);
                event.consume();
            });

            // Clean up visual feedback when drag is done
            cell.setOnDragDone(event -> {
                String style = cell.getStyle();
                style = style.replaceAll("; -fx-background-color: #0078d4; -fx-opacity: 0\\.7", "");
                style = style.replaceAll("; -fx-border-top: 3px solid #28a745", "");
                style = style.replaceAll("; -fx-border-bottom: 3px solid #28a745", "");
                cell.setStyle(style);
                event.consume();
            });

            return cell;
        });
    }

    private static boolean performPanelDrop(TreeView<Object> treeView, TreeCell<Object> targetCell, String panelId, double mouseY) {
        try {
            Panel draggedPanel = findPanelById(treeView, panelId);
            if (draggedPanel == null) return false;

            Scene sourceScene = findSceneContainingPanel(treeView, draggedPanel);
            if (sourceScene == null) return false;

            Object targetItem = targetCell.getItem();
            Scene targetScene = null;
            int targetIndex = 0;

            if (targetItem instanceof Scene) {
                targetScene = (Scene) targetItem;
                targetIndex = targetScene.getPanels().size();
            } else if (targetItem instanceof Panel) {
                Panel targetPanel = (Panel) targetItem;
                targetScene = findSceneContainingPanel(treeView, targetPanel);

                if (targetScene != null) {
                    int targetPanelIndex = targetScene.getPanels().indexOf(targetPanel);
                    double cellHeight = targetCell.getHeight();
                    boolean dropInUpperHalf = mouseY < cellHeight / 2;

                    if (dropInUpperHalf) {
                        targetIndex = targetPanelIndex;
                    } else {
                        targetIndex = targetPanelIndex + 1;
                    }
                }
            }

            if (targetScene == null) return false;

            int currentIndex = sourceScene.getPanels().indexOf(draggedPanel);

            // Prevent dropping on same position
            if (targetScene == sourceScene && currentIndex == targetIndex) {
                return false;
            }

            // Prevent redundant position changes
            if (targetScene == sourceScene) {
                if ((currentIndex == targetIndex - 1 && targetIndex > currentIndex) ||
                        (currentIndex == targetIndex && targetIndex < currentIndex)) {
                    return false;
                }
            }

            // Remove from source
            boolean removed = sourceScene.getPanels().remove(draggedPanel);
            if (!removed) return false;

            // Adjust target index if moving within same scene
            if (targetScene == sourceScene && targetIndex > currentIndex) {
                targetIndex--;
            }

            // Validate target index
            if (targetIndex < 0) targetIndex = 0;
            if (targetIndex > targetScene.getPanels().size()) {
                targetIndex = targetScene.getPanels().size();
            }

            // Add to target position
            try {
                targetScene.getPanels().add(targetIndex, draggedPanel);
            } catch (IndexOutOfBoundsException e) {
                targetScene.getPanels().add(draggedPanel);
            }

            // Update sequence numbers
            updateSequenceNumbers(sourceScene);
            if (targetScene != sourceScene) {
                updateSequenceNumbers(targetScene);
            }

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    private static Scene findSceneContainingPanel(TreeView<Object> treeView, Panel panel) {
        TreeItem<Object> root = treeView.getRoot();
        if (root == null) return null;

        if (root.getValue() instanceof Project) {
            Project project = (Project) root.getValue();
            for (Scene scene : project.getScenes()) {
                if (scene.getPanels().contains(panel)) {
                    return scene;
                }
            }
        }

        return null;
    }

    private static Panel findPanelById(TreeView<Object> treeView, String panelId) {
        TreeItem<Object> root = treeView.getRoot();
        if (root == null) return null;

        if (root.getValue() instanceof Project) {
            Project project = (Project) root.getValue();
            for (Scene scene : project.getScenes()) {
                for (Panel panel : scene.getPanels()) {
                    if (panel.getId().equals(panelId)) {
                        return panel;
                    }
                }
            }
        }

        return null;
    }

    public static void setupPanelContextMenu(TreeView<Object> treeView, MainController controller) {
        treeView.setContextMenu(null); // Clear existing context menu

        treeView.setOnContextMenuRequested(event -> {
            TreeItem<Object> selectedItem = treeView.getSelectionModel().getSelectedItem();
            if (selectedItem != null && selectedItem.getValue() instanceof Panel) {
                Panel panel = (Panel) selectedItem.getValue();

                ContextMenu contextMenu = new ContextMenu();

                // Duplicate menu item
                MenuItem duplicateItem = new MenuItem("Duplicate Panel");
                duplicateItem.setOnAction(e -> controller.duplicateSpecificPanel(panel));

                // Delete menu item
                MenuItem deleteItem = new MenuItem("Delete Panel");
                deleteItem.setOnAction(e -> {
                    // Set as current panel and delete
                    // You might need to add a method to select and delete
                });

                // Properties menu item
                MenuItem propertiesItem = new MenuItem("Panel Properties");
                propertiesItem.setOnAction(e -> {
                    // Open properties dialog
                });

                contextMenu.getItems().addAll(duplicateItem, deleteItem,
                        new SeparatorMenuItem(), propertiesItem);

                contextMenu.show(treeView, event.getScreenX(), event.getScreenY());
            }
            event.consume();
        });
    }

    private static void updateSequenceNumbers(Scene scene) {
        for (int i = 0; i < scene.getPanels().size(); i++) {
            scene.getPanels().get(i).setSequenceOrder(i);
        }
    }
}