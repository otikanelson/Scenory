package com.example.scenory.controller;

import com.example.scenory.model.*;
import com.example.scenory.utils.CanvasPersistence;
import com.example.scenory.view.components.DrawingCanvas;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Handles all project-related operations
 */
public class ProjectManager {

    private Project currentProject;
    private Scene currentScene;
    private Panel currentPanel;

    // Callbacks for UI updates
    private Consumer<String> statusUpdater;
    private Runnable uiRefresher;

    public ProjectManager(Consumer<String> statusUpdater, Runnable uiRefresher) {
        this.statusUpdater = statusUpdater;
        this.uiRefresher = uiRefresher;
        initializeDefaultProject();
    }

    private void initializeDefaultProject() {
        currentProject = new Project();
        currentProject.setName("Untitled Project");

        // Create default scene
        Scene defaultScene = new Scene();
        defaultScene.setName("Scene 1");
        defaultScene.setSequenceOrder(0);
        currentProject.getScenes().add(defaultScene);
        currentScene = defaultScene;

        System.out.println("📁 Project initialized with default scene");
    }

    public void loadProject(Project project) {
        System.out.println("📂 Loading project: " + project.getName());

        currentProject = project;

        // Set current scene to first scene or create one if none exists
        if (!project.getScenes().isEmpty()) {
            currentScene = project.getScenes().get(0);
        } else {
            Scene defaultScene = new Scene();
            defaultScene.setName("Scene 1");
            defaultScene.setSequenceOrder(0);
            project.getScenes().add(defaultScene);
            currentScene = defaultScene;
        }

        currentPanel = null;

        if (uiRefresher != null) {
            uiRefresher.run();
        }

        statusUpdater.accept("✅ Loaded project: " + project.getName());
        System.out.println("✅ Project loaded successfully: " + project.getName());
    }

    public void createNewScene() {
        Scene newScene = new Scene();
        newScene.setName("Scene " + (currentProject.getScenes().size() + 1));
        newScene.setSequenceOrder(currentProject.getScenes().size());

        currentProject.getScenes().add(newScene);
        currentScene = newScene;
        currentPanel = null;

        if (uiRefresher != null) {
            uiRefresher.run();
        }

        statusUpdater.accept("Created new scene: " + newScene.getName());
    }

    public boolean deleteCurrentScene() {
        if (currentScene == null || currentProject.getScenes().size() <= 1) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cannot Delete Scene");
            alert.setHeaderText("Cannot delete the last scene");
            alert.setContentText("A project must have at least one scene.");
            alert.showAndWait();
            return false;
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

            if (uiRefresher != null) {
                uiRefresher.run();
            }

            statusUpdater.accept("Scene deleted");
            return true;
        }

        return false;
    }

    public Panel createNewPanel() {
        if (currentScene == null) {
            statusUpdater.accept("No scene selected");
            return null;
        }

        Panel newPanel = new Panel();
        newPanel.setName("Panel " + (currentScene.getPanels().size() + 1));
        newPanel.setSequenceOrder(currentScene.getPanels().size());

        // FIXED: Generate a placeholder thumbnail for new panels
        try {
            // Create a simple placeholder thumbnail for empty panels
            byte[] placeholderThumbnail = createPlaceholderThumbnail();
            if (placeholderThumbnail != null) {
                newPanel.setThumbnailData(placeholderThumbnail);
                System.out.println("📸 Placeholder thumbnail created for new panel");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Could not create placeholder thumbnail: " + e.getMessage());
        }

        currentScene.getPanels().add(newPanel);
        currentPanel = newPanel;

        if (uiRefresher != null) {
            uiRefresher.run();
        }

        statusUpdater.accept("Created new panel: " + newPanel.getName());
        return newPanel;
    }

    private byte[] createPlaceholderThumbnail() {
        try {
            // Create a simple 120x90 placeholder image
            java.awt.image.BufferedImage placeholder = new java.awt.image.BufferedImage(120, 90, java.awt.image.BufferedImage.TYPE_INT_ARGB);
            java.awt.Graphics2D g2d = placeholder.createGraphics();

            // Enable antialiasing
            g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING, java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Light gray background
            g2d.setColor(new java.awt.Color(240, 240, 240));
            g2d.fillRect(0, 0, 120, 90);

            // Darker border
            g2d.setColor(new java.awt.Color(200, 200, 200));
            g2d.drawRect(0, 0, 119, 89);

            // "Empty" text
            g2d.setColor(new java.awt.Color(150, 150, 150));
            g2d.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));

            String text = "Empty Panel";
            java.awt.FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getHeight();

            int x = (120 - textWidth) / 2;
            int y = (90 + textHeight) / 2 - fm.getDescent();

            g2d.drawString(text, x, y);
            g2d.dispose();

            // Convert to byte array
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            javax.imageio.ImageIO.write(placeholder, "PNG", baos);
            return baos.toByteArray();

        } catch (Exception e) {
            System.err.println("Error creating placeholder thumbnail: " + e.getMessage());
            return null;
        }
    }

    public boolean deletePanel(Panel panelToDelete) {
        if (panelToDelete == null) {
            statusUpdater.accept("No panel selected");
            return false;
        }

        Scene scene = findSceneContainingPanel(panelToDelete);
        if (scene == null) {
            statusUpdater.accept("Cannot find scene for panel");
            return false;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Panel");
        alert.setHeaderText("Delete Panel: " + panelToDelete.getName());
        alert.setContentText("Are you sure you want to delete this panel?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String deletedPanelName = panelToDelete.getName();
            int deletedIndex = scene.getPanels().indexOf(panelToDelete);

            scene.getPanels().remove(panelToDelete);

            // Handle current panel selection
            if (panelToDelete == currentPanel) {
                if (!scene.getPanels().isEmpty()) {
                    int newIndex = Math.min(deletedIndex, scene.getPanels().size() - 1);
                    currentPanel = scene.getPanels().get(newIndex);
                } else {
                    currentPanel = createNewPanel();
                    return true; // Early return since createNewPanel handles UI refresh
                }
            }

            if (uiRefresher != null) {
                uiRefresher.run();
            }

            statusUpdater.accept("Deleted panel: " + deletedPanelName);
            return true;
        }

        return false;
    }

    public Panel duplicatePanel(Panel originalPanel) {
        if (originalPanel == null) {
            statusUpdater.accept("No panel selected to duplicate");
            return null;
        }

        try {
            Panel duplicatedPanel = originalPanel.createCopy();

            Scene targetScene = findSceneContainingPanel(originalPanel);
            if (targetScene == null) {
                statusUpdater.accept("Cannot find scene for panel");
                return null;
            }

            int originalIndex = targetScene.getPanels().indexOf(originalPanel);
            int insertIndex = originalIndex + 1;

            String baseName = originalPanel.getName();
            if (baseName.endsWith(" (Copy)")) {
                baseName = baseName.substring(0, baseName.length() - 7);
            }

            String newName = generateUniquePanelName(targetScene, baseName);
            duplicatedPanel.setName(newName);
            duplicatedPanel.setSequenceOrder(insertIndex);

            targetScene.getPanels().add(insertIndex, duplicatedPanel);

            // Update sequence orders for panels after the inserted one
            for (int i = insertIndex + 1; i < targetScene.getPanels().size(); i++) {
                targetScene.getPanels().get(i).setSequenceOrder(i);
            }

            if (targetScene != currentScene) {
                currentScene = targetScene;
            }

            currentPanel = duplicatedPanel;

            if (uiRefresher != null) {
                uiRefresher.run();
            }

            statusUpdater.accept("Panel duplicated: " + duplicatedPanel.getName());
            return duplicatedPanel;

        } catch (Exception e) {
            System.err.println("❌ Error duplicating panel: " + e.getMessage());
            e.printStackTrace();
            statusUpdater.accept("Error duplicating panel");
            return null;
        }
    }

    public void clearPanelContent(Panel panel, DrawingCanvas canvas) {
        if (panel == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Clear Panel");
        alert.setHeaderText("Clear " + panel.getName() + "?");
        alert.setContentText("This will permanently delete all drawing on this panel.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            panel.setCanvasImageData(null);
            panel.setThumbnailData(null);

            if (panel == currentPanel && canvas != null) {
                CanvasPersistence.clearCanvas(canvas);
            }

            if (uiRefresher != null) {
                uiRefresher.run();
            }

            statusUpdater.accept("Panel cleared: " + panel.getName());
        }
    }

    public void navigateToWelcomeScreen(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/scenory/welcome-view.fxml"));
            Parent welcomeView = loader.load();

            stage.getScene().setRoot(welcomeView);
            stage.setTitle("Scenory - Professional Storyboard Creator");

            System.out.println("🏠 Returned to welcome screen");

        } catch (IOException e) {
            System.err.println("❌ Failed to return to welcome screen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Helper methods
    public Scene findSceneContainingPanel(Panel panel) {
        for (Scene scene : currentProject.getScenes()) {
            if (scene.getPanels().contains(panel)) {
                return scene;
            }
        }
        return null;
    }

    private String generateUniquePanelName(Scene scene, String baseName) {
        String candidateName = baseName + " (Copy)";
        int counter = 1;

        while (isPanelNameTaken(scene, candidateName)) {
            counter++;
            candidateName = baseName + " (Copy " + counter + ")";
        }

        return candidateName;
    }

    private boolean isPanelNameTaken(Scene scene, String name) {
        return scene.getPanels().stream()
                .anyMatch(panel -> panel.getName().equals(name));
    }

    // Getters and setters
    public Project getCurrentProject() { return currentProject; }
    public Scene getCurrentScene() { return currentScene; }
    public Panel getCurrentPanel() { return currentPanel; }

    public void setCurrentScene(Scene scene) {
        this.currentScene = scene;
    }

    public void setCurrentPanel(Panel panel) {
        this.currentPanel = panel;
    }
}