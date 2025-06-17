package com.example.scenory.controller;

import com.example.scenory.model.Project;
import com.example.scenory.model.Scene;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class WelcomeController implements Initializable {

    @FXML private VBox recentProjectsContainer;

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

        addToRecentProjects(project);
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

        addToRecentProjects(project);
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

            addToRecentProjects(project);
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
            addToRecentProjects(project);
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
            Label emptyLabel = new Label("No recent projects yet. Create your first project above!");
            emptyLabel.getStyleClass().add("empty-state-text");
            recentProjectsContainer.getChildren().add(emptyLabel);
        } else {
            for (RecentProject recent : recentProjects) {
                HBox projectItem = createRecentProjectItem(recent);
                recentProjectsContainer.getChildren().add(projectItem);
            }
        }
    }

    private HBox createRecentProjectItem(RecentProject recent) {
        HBox item = new HBox(16);
        item.getStyleClass().add("recent-project-item");

        // Project info
        VBox info = new VBox(4);

        Label nameLabel = new Label(recent.name);
        nameLabel.getStyleClass().add("recent-project-name");

        Label pathLabel = new Label(recent.path);
        pathLabel.getStyleClass().add("recent-project-path");

        Label dateLabel = new Label("Modified: " + recent.lastModified.format(
                DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")
        ));
        dateLabel.getStyleClass().add("recent-project-date");

        info.getChildren().addAll(nameLabel, pathLabel, dateLabel);

        // Spacer
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        // Open button
        Button openButton = new Button("Open");
        openButton.getStyleClass().addAll("recent-project-button", "primary-button");
        openButton.setOnAction(e -> openRecentProject(recent));

        item.getChildren().addAll(info, spacer, openButton);
        return item;
    }

    private void openRecentProject(RecentProject recent) {
        System.out.println("🔄 Opening recent project: " + recent.name);

        // TODO: Load actual project data
        Project project = createNewProject(recent.name, "recent");
        launchMainApplication(project);
    }

    private void addToRecentProjects(Project project) {
        RecentProject recent = new RecentProject();
        recent.name = project.getName();
        recent.path = "~/Documents/Scenory/" + project.getName() + ".scenory";
        recent.lastModified = LocalDateTime.now();
        recent.type = "storyboard";

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
        // TODO: Implement project browser
        showInfo("Project Browser", "Project browser coming soon!");
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

            // Get the main controller and pass the project
            MainController mainController = loader.getController();
            mainController.loadProject(project);

            // Replace current scene with main application
            Stage stage = (Stage) recentProjectsContainer.getScene().getWindow();
            stage.getScene().setRoot(mainView);
            stage.setTitle("Scenory - " + project.getName());

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
        showInfo("Settings", "Settings panel coming soon!");
    }

    @FXML
    private void openTutorials() {
        System.out.println("📖 Opening tutorials...");
        showInfo("Tutorials", "Interactive tutorials coming soon!");
    }

    @FXML
    private void openTips() {
        System.out.println("💡 Opening tips...");
        showInfo("Tips & Tricks", "Pro tips and workflow guides coming soon!");
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
        showInfo("Help", "Help documentation coming soon!\n\n" +
                "For now, try creating a new project above.");
    }

    // ===============================
    // HELPER METHODS
    // ===============================

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