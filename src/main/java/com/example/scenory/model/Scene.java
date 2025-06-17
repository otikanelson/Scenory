package com.example.scenory.model;

import com.example.scenory.database.SceneDAO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Scene {
    private String id;
    private String name;
    private String description;
    private int sequenceOrder;
    private List<Panel> panels;

    // Existing Phase 2 fields (keeping for compatibility)
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private String notes;
    private int estimatedDurationSeconds;
    private String location;
    private String timeOfDay;
    private boolean isCompleted;

    // Phase 1: Additional scene properties
    private String backgroundColor = "#FFFFFF";

    public Scene() {
        this.id = UUID.randomUUID().toString();
        this.panels = new ArrayList<>();
        this.createdDate = LocalDateTime.now();
        this.modifiedDate = LocalDateTime.now();
        this.isCompleted = false;
        this.estimatedDurationSeconds = 0;
    }

    // ✨ Database Integration Methods
    /**
     * Save this scene to database
     */
    public void save(int projectId) {
        SceneDAO.save(this, projectId);
    }

    /**
     * Load scenes by project ID
     */
    public static List<Scene> loadByProject(int projectId) {
        return SceneDAO.loadByProjectId(projectId);
    }

    /**
     * Delete this scene from database
     */
    public boolean delete() {
        if (id != null) {
            try {
                return SceneDAO.delete(Integer.parseInt(id));
            } catch (NumberFormatException e) {
                System.err.println("Invalid scene ID for deletion: " + id);
                return false;
            }
        }
        return false;
    }

    // ✨ Enhanced Utility Methods with Phase 1 Features
    /**
     * Get scene statistics including Phase 1 features
     */
    public String getSceneStats() {
        int panelCount = getPanelCount();
        int panelsWithDrawing = (int) panels.stream()
                .filter(Panel::hasDrawingData)
                .count();
        int panelsWithDescription = (int) panels.stream()
                .filter(Panel::hasRichTextDescription)
                .count();

        return String.format("%d panels (%d drawn, %d described)",
                panelCount, panelsWithDrawing, panelsWithDescription);
    }

    /**
     * Get total video duration for all panels in this scene
     */
    public double getTotalVideoDuration() {
        return panels.stream()
                .mapToDouble(panel -> panel.getDisplayDuration() != null ?
                        panel.getDisplayDuration().toSeconds() : 3.0)
                .sum();
    }

    /**
     * Check if scene is ready for video export
     */
    public boolean isReadyForVideoExport() {
        return !panels.isEmpty() &&
                panels.stream().allMatch(Panel::hasDrawingData);
    }

    /**
     * Get completion percentage based on panels with content
     */
    public double getCompletionPercentage() {
        if (panels.isEmpty()) return 0.0;

        long completedPanels = panels.stream()
                .filter(Panel::hasDrawingData)
                .count();

        return (double) completedPanels / panels.size() * 100.0;
    }

    /**
     * Check if scene has any panels with rich text descriptions
     */
    public boolean hasRichDescriptions() {
        return panels.stream().anyMatch(Panel::hasRichTextDescription);
    }

    /**
     * Check if scene has any panels with custom backgrounds
     */
    public boolean hasCustomBackgrounds() {
        return panels.stream().anyMatch(panel ->
                panel.getCanvasBackgroundColor() != null &&
                        !panel.getCanvasBackgroundColor().equals("#FFFFFF"));
    }

    /**
     * Check if scene has any panels with custom timing
     */
    public boolean hasCustomTiming() {
        return panels.stream().anyMatch(panel ->
                panel.getDisplayDuration() != null &&
                        panel.getDisplayDuration().toSeconds() != 3.0);
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

    public int getSequenceOrder() {
        return sequenceOrder;
    }

    public void setSequenceOrder(int sequenceOrder) {
        this.sequenceOrder = sequenceOrder;
    }

    public List<Panel> getPanels() {
        return panels;
    }

    public void setPanels(List<Panel> panels) {
        this.panels = panels;
    }

    // Phase 2 getters and setters
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
        this.modifiedDate = LocalDateTime.now();
    }

    public int getEstimatedDurationSeconds() {
        return estimatedDurationSeconds;
    }

    public void setEstimatedDurationSeconds(int estimatedDurationSeconds) {
        this.estimatedDurationSeconds = estimatedDurationSeconds;
        this.modifiedDate = LocalDateTime.now();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
        this.modifiedDate = LocalDateTime.now();
    }

    public String getTimeOfDay() {
        return timeOfDay;
    }

    public void setTimeOfDay(String timeOfDay) {
        this.timeOfDay = timeOfDay;
        this.modifiedDate = LocalDateTime.now();
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        this.isCompleted = completed;
        this.modifiedDate = LocalDateTime.now();
    }

    // Phase 1: Background color
    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
        this.modifiedDate = LocalDateTime.now();
    }

    // Utility methods
    public int getPanelCount() {
        return panels != null ? panels.size() : 0;
    }

    public String getFormattedDuration() {
        int minutes = estimatedDurationSeconds / 60;
        int seconds = estimatedDurationSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    public String toString() {
        String baseName = name != null ? name : "Unnamed Scene";
        String stats = String.format(" (%d panels)", getPanelCount());

        // Add completion indicators
        if (isCompleted()) {
            return baseName + " ✅" + stats;
        } else if (isReadyForVideoExport()) {
            return baseName + " 🎬" + stats;
        } else {
            return baseName + stats;
        }
    }
}
