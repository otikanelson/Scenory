package com.example.scenory.model;

import com.example.scenory.database.ProjectDAO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Project {
    private String id;
    private String name;
    private String description;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private List<Scene> scenes;
    private String filePath;

    // Phase 1: Additional project settings
    private int canvasWidth = 800;
    private int canvasHeight = 600;
    private String projectType = "CUSTOM"; // YOUTUBE, FILM, CUSTOM
    private String aspectRatio = "16:9";

    public Project() {
        this.id = UUID.randomUUID().toString();
        this.createdDate = LocalDateTime.now();
        this.modifiedDate = LocalDateTime.now();
        this.scenes = new ArrayList<>();
    }

    // ✨ Database Integration Methods
    /**
     * Save this project to database
     */
    public void save() {
        ProjectDAO.save(this);
    }

    /**
     * Load project from database by ID
     */
    public static Project load(int projectId) {
        return ProjectDAO.load(projectId);
    }

    /**
     * Load all projects from database
     */
    public static List<Project> loadAll() {
        return ProjectDAO.loadAll();
    }

    /**
     * Delete this project from database
     */
    public boolean delete() {
        if (id != null) {
            try {
                return ProjectDAO.delete(Integer.parseInt(id));
            } catch (NumberFormatException e) {
                System.err.println("Invalid project ID for deletion: " + id);
                return false;
            }
        }
        return false;
    }

    // ✨ Utility Methods
    /**
     * Get total panel count across all scenes
     */
    public int getTotalPanelCount() {
        return scenes.stream()
                .mapToInt(scene -> scene.getPanels().size())
                .sum();
    }

    /**
     * Get total estimated duration across all scenes (in seconds)
     */
    public int getTotalEstimatedDuration() {
        return scenes.stream()
                .mapToInt(Scene::getEstimatedDurationSeconds)
                .sum();
    }

    /**
     * Get total video duration for export (sum of all panel display durations)
     */
    public double getTotalVideoDuration() {
        return scenes.stream()
                .flatMap(scene -> scene.getPanels().stream())
                .mapToDouble(panel -> panel.getDisplayDuration() != null ?
                        panel.getDisplayDuration().toSeconds() : 3.0)
                .sum();
    }

    /**
     * Get formatted project statistics
     */
    public String getProjectStats() {
        return String.format("%d scenes, %d panels, %s estimated",
                scenes.size(),
                getTotalPanelCount(),
                formatDuration(getTotalEstimatedDuration()));
    }

    /**
     * Get project readiness for export
     */
    public boolean isReadyForExport() {
        return !scenes.isEmpty() &&
                scenes.stream().allMatch(Scene::isReadyForVideoExport);
    }

    /**
     * Format duration from seconds to MM:SS
     */
    private String formatDuration(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    // Basic Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.modifiedDate = LocalDateTime.now();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.modifiedDate = LocalDateTime.now();
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(LocalDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public List<Scene> getScenes() {
        return scenes;
    }

    public void setScenes(List<Scene> scenes) {
        this.scenes = scenes;
        this.modifiedDate = LocalDateTime.now();
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    // Phase 1: Additional getters and setters
    public int getCanvasWidth() {
        return canvasWidth;
    }

    public void setCanvasWidth(int canvasWidth) {
        this.canvasWidth = canvasWidth;
        this.modifiedDate = LocalDateTime.now();
    }

    public int getCanvasHeight() {
        return canvasHeight;
    }

    public void setCanvasHeight(int canvasHeight) {
        this.canvasHeight = canvasHeight;
        this.modifiedDate = LocalDateTime.now();
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
        this.modifiedDate = LocalDateTime.now();
    }

    public String getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(String aspectRatio) {
        this.aspectRatio = aspectRatio;
        this.modifiedDate = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return name != null ? name : "Unnamed Project";
    }
}