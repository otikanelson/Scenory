package com.example.scenory.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import com.example.scenory.model.Project;
import com.example.scenory.model.Scene;
import com.example.scenory.view.templates.ModalTemplate;
import com.example.scenory.view.templates.config.ModalConfig;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class WelcomeController implements Initializable {

    @FXML private HBox recentProjectsContainer;

    // Recent projects storage (in a real app, this would be persisted)
    private List<RecentProject> recentProjects = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("🏠 Welcome screen initialized");
        loadRecentProjects();
        updateRecentProjectsDisplay();
    }

    // ===============================
    // PROJECT CREATION METHODS
    // ===============================

    @FXML
    private void createYouTubeProject() {
        System.out.println("📺 Creating YouTube Animation project...");

        Project project = createNewProject("YouTube Animation", "youtube");
        // Set YouTube-specific settings
        // - 16:9 aspect ratio
        // - 30fps timeline
        // - Standard YouTube scene duration

        addToRecentProjects(project, "youtube");
        launchMainApplication(project);
    }

    @FXML
    private void createFilmProject() {
        System.out.println("🎭 Creating Film Storyboard project...");

        Project project = createNewProject("Film Storyboard", "film");
        // Set Film-specific settings
        // - 2.35:1 or 1.85:1 aspect ratio
        // - 24fps timeline
        // - Longer scene durations

        addToRecentProjects(project, "film");
        launchMainApplication(project);
    }

    @FXML
    private void createCustomProject() {
        System.out.println("⚙️ Creating Custom project...");

        // Show custom project dialog
        Optional<ProjectSettings> settings = showCustomProjectDialog();
        if (settings.isPresent()) {
            Project project = createNewProject(settings.get().name, "custom");
            // Apply custom settings

            addToRecentProjects(project, "custom");
            launchMainApplication(project);
        }
    }

    @FXML
    private void openExistingProject() {
        System.out.println("📂 Opening existing project...");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Scenory Project");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Scenory Projects", "*.scenory")
        );

        Stage stage = (Stage) recentProjectsContainer.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            // TODO: Load project from file
            System.out.println("Loading project: " + selectedFile.getName());

            // For now, create a dummy project
            Project project = createNewProject(selectedFile.getName().replace(".scenory", ""), "loaded");
            addToRecentProjects(project, "loaded");
            launchMainApplication(project);
        }
    }

    // ===============================
    // RECENT PROJECTS METHODS
    // ===============================

    private void loadRecentProjects() {
        // TODO: Load from preferences/file
        // For now, create some dummy recent projects for demonstration
        if (recentProjects.isEmpty()) {
            // This would normally load from saved preferences
            System.out.println("📋 No recent projects found");
        }
    }

    private void updateRecentProjectsDisplay() {
        recentProjectsContainer.getChildren().clear();

        if (recentProjects.isEmpty()) {
            VBox empty = new VBox(6);
            empty.getStyleClass().add("empty-state");
            empty.setAlignment(Pos.CENTER);

            Label title = new Label("No recent projects yet");
            title.getStyleClass().add("empty-state-title");
            Label subtitle = new Label("Start your first storyboard above");
            subtitle.getStyleClass().add("empty-state-subtitle");

            empty.getChildren().addAll(title, subtitle);
            recentProjectsContainer.getChildren().add(empty);
        } else {
            for (RecentProject recent : recentProjects) {
                VBox card = createRecentProjectCard(recent);
                recentProjectsContainer.getChildren().add(card);
            }
        }
    }

    /**
     * Builds a flat recent-project card: a neutral thumbnail block with a
     * small icon, title, and last-modified date below - matches the
     * template card styling so the whole page reads as one consistent
     * flat, uncluttered system (no tags, no overlays, no color-coding).
     */
    private VBox createRecentProjectCard(RecentProject recent) {
        VBox card = new VBox();
        card.getStyleClass().add("recent-card");
        card.setOnMouseClicked(e -> openRecentProject(recent));

        StackPane thumb = new StackPane();
        thumb.getStyleClass().add("recent-card-thumb");
        Label icon = new Label(iconForType(recent.type));
        icon.getStyleClass().add("recent-card-icon");
        thumb.getChildren().add(icon);

        VBox info = new VBox(2);
        info.setPadding(new Insets(10, 14, 12, 14));
        Label nameLabel = new Label(recent.name);
        nameLabel.getStyleClass().add("recent-card-title");
        nameLabel.setWrapText(true);
        Label dateLabel = new Label(recent.lastModified.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        dateLabel.getStyleClass().add("recent-card-date");
        info.getChildren().addAll(nameLabel, dateLabel);

        card.getChildren().addAll(thumb, info);
        return card;
    }

    /** Maps a project type to a small neutral icon for the thumbnail block. */
    private String iconForType(String type) {
        if (type == null) return "🗂️";
        switch (type) {
            case "youtube":
                return "📺";
            case "film":
                return "🎬";
            case "custom":
                return "⚙️";
            default:
                return "🗂️";
        }
    }

    private void openRecentProject(RecentProject recent) {
        System.out.println("🔄 Opening recent project: " + recent.name);

        // TODO: Load actual project data
        Project project = createNewProject(recent.name, "recent");
        launchMainApplication(project);
    }

    private void addToRecentProjects(Project project, String type) {
        RecentProject recent = new RecentProject();
        recent.name = project.getName();
        recent.path = "~/Documents/Scenory/" + project.getName() + ".scenory";
        recent.lastModified = LocalDateTime.now();
        recent.type = type;

        // Add to beginning of list
        recentProjects.add(0, recent);

        // Keep only last 5 projects
        if (recentProjects.size() > 5) {
            recentProjects = recentProjects.subList(0, 5);
        }

        updateRecentProjectsDisplay();
    }

    @FXML
    private void browseAllProjects() {
        System.out.println("📁 Browse all projects...");
        showInfoModal("Project Browser", "Project browser coming soon!");
    }

    // ===============================
    // UTILITY METHODS
    // ===============================

    private Project createNewProject(String name, String type) {
        Project project = new Project();
        project.setName(name);
        project.setDescription("Created from " + type + " template");

        // Create default scene
        Scene defaultScene = new Scene();
        defaultScene.setName("Scene 1");
        defaultScene.setSequenceOrder(0);
        project.getScenes().add(defaultScene);

        System.out.println("✅ Created project: " + name);
        return project;
    }

    private void launchMainApplication(Project project) {
        try {
            System.out.println("🚀 Launching main application with project: " + project.getName());

            // Load the main application FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/scenory/main-view.fxml"));
            Parent mainView = loader.load();

            // Create NEW scene
            Stage stage = (Stage) recentProjectsContainer.getScene().getWindow();
            javafx.scene.Scene newScene = new javafx.scene.Scene(mainView);
            
            // Load CSS stylesheet
            try {
                String cssFile = getClass().getResource("/com/example/scenory/styles.css").toExternalForm();
                newScene.getStylesheets().clear();
                newScene.getStylesheets().add(cssFile);
                System.out.println("✅ CSS loaded for main view: " + cssFile);
            } catch (Exception e) {
                System.err.println("⚠️ CSS file not found for main view");
            }
            
            // Store maximized state before Scene transition to preserve valid window bounds
            boolean wasMaximized = stage.isMaximized();
            if (wasMaximized) {
                stage.setMaximized(false);  // Temporarily restore to normal
            }
            
            // Set the scene
            stage.setScene(newScene);
            stage.setTitle("Scenory - " + project.getName());
            
            // Re-maximize if the window was maximized before
            if (wasMaximized) {
                stage.setMaximized(true);
            }
            
            // Get the controller and load the project
            MainController mainController = loader.getController();
            mainController.loadProject(project);

        } catch (IOException e) {
            System.err.println("❌ Failed to launch main application: " + e.getMessage());
            e.printStackTrace();
            showError("Launch Error", "Failed to open the main application.");
        }
    }

    private Optional<ProjectSettings> showCustomProjectDialog() {
        TextInputDialog dialog = new TextInputDialog("My Storyboard");
        dialog.setTitle("New Custom Project");
        dialog.setHeaderText("Create Custom Project");
        dialog.setContentText("Project Name:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            ProjectSettings settings = new ProjectSettings();
            settings.name = result.get().trim();
            settings.aspectRatio = "16:9"; // Default
            settings.fps = 30; // Default
            return Optional.of(settings);
        }

        return Optional.empty();
    }

    // ===============================
    // NAVIGATION METHODS
    // ===============================

    @FXML
    private void openSettings() {
        System.out.println("⚙️ Opening settings...");
        showInfoModal("Settings", "Settings panel coming soon!");
    }

    @FXML
    private void openTutorials() {
        System.out.println("📖 Opening tutorials...");
        showInfoModal("Tutorials", "Interactive tutorials coming soon!");
    }

    @FXML
    private void openTips() {
        System.out.println("💡 Opening tips...");
        showInfoModal("Tips & Tricks", "Pro tips and workflow guides coming soon!");
    }

    @FXML
    private void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Scenory");
        alert.setHeaderText("Scenory - Professional Storyboarding Tool");
        alert.setContentText("Version 1.0\n\n" +
                "A modern storyboarding application for animators,\n" +
                "filmmakers, and content creators.\n\n" +
                "Built with JavaFX");
        alert.showAndWait();
    }

    @FXML
    private void showHelp() {
        showInfoModal("Help", "Help documentation coming soon!\n\nFor now, try creating a new project above.");
    }

    // ===============================
    // HELPER METHODS
    // ===============================

    private void showInfoModal(String title, String message) {
        // Create modal content
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER_LEFT);
        
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-font-size: 14px;");
        
        Button okButton = new Button("OK");
        okButton.setStyle("-fx-min-width: 80px;");
        okButton.setDefaultButton(true);
        
        content.getChildren().addAll(messageLabel, okButton);
        
        // Get current stage as owner
        Stage owner = (Stage) recentProjectsContainer.getScene().getWindow();
        
        // Create modal configuration
        ModalConfig config = ModalConfig.builder()
            .title(title)
            .dimensions(400, 200)
            .minDimensions(350, 150)
            .owner(owner)
            .resizable(false)
            .build();
        
        // Create and show modal
        ModalTemplate modal = ModalTemplate.createWithContent(content, config);
        okButton.setOnAction(e -> modal.close());
        
        modal.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ===============================
    // DATA CLASSES
    // ===============================

    private static class RecentProject {
        String name;
        String path;
        LocalDateTime lastModified;
        String type;
    }

    private static class ProjectSettings {
        String name;
        String aspectRatio;
        int fps;
    }
}