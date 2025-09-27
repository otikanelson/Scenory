package com.example.scenory.controller;

import com.example.scenory.model.Panel;
import com.example.scenory.model.Scene;
import com.example.scenory.utils.CanvasPersistence;
import com.example.scenory.view.components.DrawingCanvas;
import com.example.scenory.commands.CommandManager;

import java.util.function.Consumer;

/**
 * Handles panel navigation and drawing persistence
 */
public class PanelNavigator {

    private final ProjectManager projectManager;
    private final DrawingCanvas canvas;
    private final CommandManager commandManager;
    private final Consumer<String> statusUpdater;
    private final Runnable uiRefresher;

    private boolean isNavigating = false;

    public PanelNavigator(ProjectManager projectManager, DrawingCanvas canvas,
                          CommandManager commandManager, Consumer<String> statusUpdater,
                          Runnable uiRefresher) {
        this.projectManager = projectManager;
        this.canvas = canvas;
        this.commandManager = commandManager;
        this.statusUpdater = statusUpdater;
        this.uiRefresher = uiRefresher;
    }

    public void switchToPanel(Panel panel) {
        if (panel == projectManager.getCurrentPanel() || isNavigating) return;

        isNavigating = true;
        try {
            // Save current panel's drawing before switching
            saveCurrentPanelDrawing();

            // Clear command history when switching panels
            if (commandManager != null) {
                commandManager.clearHistory();
            }

            // Switch to new panel
            projectManager.setCurrentPanel(panel);

            // Update scene if needed (panel might be from different scene)
            Scene panelScene = projectManager.findSceneContainingPanel(panel);
            if (panelScene != null && panelScene != projectManager.getCurrentScene()) {
                projectManager.setCurrentScene(panelScene);
            }

            // Restore the new panel's drawing
            restorePanelDrawing(panel);

            // Update UI
            if (uiRefresher != null) {
                uiRefresher.run();
            }

            statusUpdater.accept("Switched to: " + panel.getName());
            System.out.println("📝 Switched to panel: " + panel.getName() + " (Command history cleared)");

        } finally {
            isNavigating = false;
        }
    }

    public void navigateToPreviousPanel() {
        Scene currentScene = projectManager.getCurrentScene();
        Panel currentPanel = projectManager.getCurrentPanel();

        if (currentScene == null || currentPanel == null) return;

        int currentIndex = currentScene.getPanels().indexOf(currentPanel);
        if (currentIndex > 0) {
            switchToPanel(currentScene.getPanels().get(currentIndex - 1));
        }
    }

    public void navigateToNextPanel() {
        Scene currentScene = projectManager.getCurrentScene();
        Panel currentPanel = projectManager.getCurrentPanel();

        if (currentScene == null || currentPanel == null) return;

        int currentIndex = currentScene.getPanels().indexOf(currentPanel);
        if (currentIndex < currentScene.getPanels().size() - 1) {
            switchToPanel(currentScene.getPanels().get(currentIndex + 1));
        }
    }

    public void handleSceneSelection(Scene scene) {
        if (scene == projectManager.getCurrentScene()) return;

        saveCurrentPanelDrawing();
        projectManager.setCurrentScene(scene);

        if (!scene.getPanels().isEmpty()) {
            switchToPanel(scene.getPanels().get(0));
        } else {
            projectManager.setCurrentPanel(null);
            if (canvas != null) {
                CanvasPersistence.clearCanvas(canvas);
            }
        }

        statusUpdater.accept("Switched to scene: " + scene.getName());
    }

    public void saveCurrentPanelDrawing() {
        Panel currentPanel = projectManager.getCurrentPanel();
        if (currentPanel == null || canvas == null) {
            return;
        }

        try {
            // Always save canvas data first
            byte[] canvasData = CanvasPersistence.saveCanvasToBytes(canvas);

            if (canvasData != null && canvasData.length > 0) {
                currentPanel.setCanvasImageData(canvasData);

                // FIXED: Always generate thumbnail when saving canvas
                byte[] thumbnailData = com.example.scenory.utils.ThumbnailGenerator.generateThumbnail(canvas);
                if (thumbnailData != null && thumbnailData.length > 0) {
                    currentPanel.setThumbnailData(thumbnailData);
                    System.out.println("📸 Thumbnail generated: " + thumbnailData.length + " bytes");
                }

                System.out.println("💾 SAVED: " + currentPanel.getName() + " (" + canvasData.length + " bytes)");
            } else {
                // Even if canvas appears empty, try to generate a thumbnail
                byte[] thumbnailData = com.example.scenory.utils.ThumbnailGenerator.generateThumbnail(canvas);
                if (thumbnailData != null && thumbnailData.length > 0) {
                    currentPanel.setThumbnailData(thumbnailData);
                    System.out.println("📸 Empty canvas thumbnail generated: " + thumbnailData.length + " bytes");
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Error saving panel: " + e.getMessage());
            e.printStackTrace();
            statusUpdater.accept("Error saving drawing");
        }
    }

    public void restorePanelDrawing(Panel panel) {
        if (panel == null || canvas == null) return;

        try {
            if (CanvasPersistence.isValidImageData(panel.getCanvasImageData())) {
                boolean restored = CanvasPersistence.restoreCanvasFromBytes(
                        canvas, panel.getCanvasImageData()
                );

                if (restored) {
                    System.out.println("📂 Restored: " + panel.getName());
                } else {
                    canvas.clearCanvas();
                    System.out.println("⚠️ Failed to restore: " + panel.getName());
                }
            } else {
                canvas.clearCanvas();
                System.out.println("📄 New panel: " + panel.getName());
            }

        } catch (Exception e) {
            canvas.clearCanvas();
            System.err.println("❌ Error restoring panel: " + e.getMessage());
        }
    }

    public boolean isNavigating() {
        return isNavigating;
    }
}