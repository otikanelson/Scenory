package com.example.scenory.model;

import javafx.util.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Panel {
    private String id;
    private String name;
    private int sequenceOrder;
    private String notes;
    private String shotType;
    private String cameraAngle;
    private List<DrawingElement> elements;
    private byte[] thumbnailData;

    // Canvas persistence fields
    private byte[] canvasImageData;  // Stores the full canvas drawing
    private boolean hasDrawingData = false;

    // ✨ NEW: Phase 1 Rich Text Fields
    private String descriptionRichText;  // HTML formatted text
    private String descriptionPlainText; // Plain text fallback

    // ✨ NEW: Phase 1 Visual Customization
    private String canvasBackgroundColor = "#FFFFFF";
    private Duration displayDuration = Duration.seconds(3.0);

    // Phase 2 fields (existing)
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private String dialogue;
    private String action;
    private String cameraMovement;
    private int durationSeconds;
    private boolean isKeyFrame;
    private String transitionType;
    private String audioNotes;

    public Panel() {
        this.id = UUID.randomUUID().toString();
        this.elements = new ArrayList<>();
        this.createdDate = LocalDateTime.now();
        this.modifiedDate = LocalDateTime.now();
        this.isKeyFrame = false;
        this.durationSeconds = 3; // Default 3 seconds
        this.hasDrawingData = false;
    }

    // ===== EXISTING GETTERS AND SETTERS =====

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
        this.modifiedDate = LocalDateTime.now();
    }

    public int getSequenceOrder() { return sequenceOrder; }
    public void setSequenceOrder(int sequenceOrder) { this.sequenceOrder = sequenceOrder; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) {
        this.notes = notes;
        this.modifiedDate = LocalDateTime.now();
    }

    public String getShotType() { return shotType; }
    public void setShotType(String shotType) {
        this.shotType = shotType;
        this.modifiedDate = LocalDateTime.now();
    }

    public String getCameraAngle() { return cameraAngle; }
    public void setCameraAngle(String cameraAngle) {
        this.cameraAngle = cameraAngle;
        this.modifiedDate = LocalDateTime.now();
    }

    public List<DrawingElement> getElements() { return elements; }
    public void setElements(List<DrawingElement> elements) { this.elements = elements; }

    public byte[] getThumbnailData() { return thumbnailData; }
    public void setThumbnailData(byte[] thumbnailData) {
        this.thumbnailData = thumbnailData;
        this.modifiedDate = LocalDateTime.now();
    }

    // Canvas image data getters and setters
    public byte[] getCanvasImageData() { return canvasImageData; }
    public void setCanvasImageData(byte[] canvasImageData) {
        this.canvasImageData = canvasImageData;
        this.hasDrawingData = (canvasImageData != null && canvasImageData.length > 0);
        this.modifiedDate = LocalDateTime.now();
    }

    public boolean hasDrawingData() { return hasDrawingData; }
    public void setHasDrawingData(boolean hasDrawingData) {
        this.hasDrawingData = hasDrawingData;
    }

    // Phase 2 getters and setters
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getModifiedDate() { return modifiedDate; }
    public void setModifiedDate(LocalDateTime modifiedDate) { this.modifiedDate = modifiedDate; }

    public String getDialogue() { return dialogue; }
    public void setDialogue(String dialogue) {
        this.dialogue = dialogue;
        this.modifiedDate = LocalDateTime.now();
    }

    public String getAction() { return action; }
    public void setAction(String action) {
        this.action = action;
        this.modifiedDate = LocalDateTime.now();
    }

    public String getCameraMovement() { return cameraMovement; }
    public void setCameraMovement(String cameraMovement) {
        this.cameraMovement = cameraMovement;
        this.modifiedDate = LocalDateTime.now();
    }

    public int getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
        this.modifiedDate = LocalDateTime.now();
    }

    public boolean isKeyFrame() { return isKeyFrame; }
    public void setKeyFrame(boolean keyFrame) {
        this.isKeyFrame = keyFrame;
        this.modifiedDate = LocalDateTime.now();
    }

    public String getTransitionType() { return transitionType; }
    public void setTransitionType(String transitionType) {
        this.transitionType = transitionType;
        this.modifiedDate = LocalDateTime.now();
    }

    public String getAudioNotes() { return audioNotes; }
    public void setAudioNotes(String audioNotes) {
        this.audioNotes = audioNotes;
        this.modifiedDate = LocalDateTime.now();
    }

    // ===== ✨ NEW: PHASE 1 METHODS =====

    // Rich Text Description Methods
    public String getDescriptionRichText() {
        return descriptionRichText;
    }

    public void setDescriptionRichText(String descriptionRichText) {
        this.descriptionRichText = descriptionRichText;
        this.modifiedDate = LocalDateTime.now();
    }

    public String getDescriptionPlainText() {
        return descriptionPlainText;
    }

    public void setDescriptionPlainText(String descriptionPlainText) {
        this.descriptionPlainText = descriptionPlainText;
        this.modifiedDate = LocalDateTime.now();
    }

    /**
     * Check if panel has rich text description
     */
    public boolean hasRichTextDescription() {
        return descriptionRichText != null && !descriptionRichText.trim().isEmpty();
    }

    // Canvas Background Color Methods
    public String getCanvasBackgroundColor() {
        return canvasBackgroundColor;
    }

    public void setCanvasBackgroundColor(String canvasBackgroundColor) {
        this.canvasBackgroundColor = canvasBackgroundColor;
        this.modifiedDate = LocalDateTime.now();
    }

    // Display Duration Methods (for video export)
    public Duration getDisplayDuration() {
        return displayDuration;
    }

    public void setDisplayDuration(Duration displayDuration) {
        this.displayDuration = displayDuration;
        this.modifiedDate = LocalDateTime.now();
    }

    /**
     * Get formatted duration string for UI display
     */
    public String getFormattedDisplayDuration() {
        if (displayDuration == null) return "3.0s";
        return String.format("%.1fs", displayDuration.toSeconds());
    }

    // Rich Text Editor Integration (will implement in Week 2)
    /**
     * Opens rich text editor modal for this panel's description
     * This method will be implemented when we create RichTextModal
     */
    public void openDescriptionEditor() {
        // TODO: Implement in Week 2 when RichTextModal is created
        System.out.println("📝 Rich text editor not yet implemented for: " + name);
        // RichTextModal modal = new RichTextModal(descriptionRichText, name);
        // modal.showAndWait();
        // Handle result...
    }

    // Database Integration Methods (requires PanelDAO)
    /**
     * Save this panel to database
     */
    public void save(int sceneId) {
        // TODO: Implement when PanelDAO is available
        // PanelDAO.save(this, sceneId);
        System.out.println("💾 Database save not yet implemented for: " + name);
    }

    // ===== UTILITY METHODS =====

    // Existing utility methods
    public boolean hasThumbnail() {
        return thumbnailData != null && thumbnailData.length > 0;
    }

    public String getFormattedDuration() {
        return durationSeconds + "s";
    }

    public boolean hasCanvasData() {
        return canvasImageData != null && canvasImageData.length > 0;
    }

    public String getCanvasDataInfo() {
        if (!hasCanvasData()) {
            return "No drawing data";
        }
        return String.format("Drawing data: %d bytes", canvasImageData.length);
    }

    // ✨ ENHANCED: Updated toString with Phase 1 indicators
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name != null ? name : "Unnamed Panel");

        // Add indicators for Phase 1 features
        if (hasDrawingData()) {
            sb.append(" ✓"); // Has drawing
        }
        if (hasRichTextDescription()) {
            sb.append(" 📝"); // Has description
        }
        if (!canvasBackgroundColor.equals("#FFFFFF")) {
            sb.append(" 🎨"); // Custom background
        }
        if (displayDuration != null && displayDuration.toSeconds() != 3.0) {
            sb.append(" ⏱️"); // Custom timing
        }

        return sb.toString();
    }

    // ✨ ENHANCED: Updated createCopy with Phase 1 fields
    public Panel createCopy() {
        Panel copy = new Panel();

        // Copy basic properties
        copy.setName(this.name + " (Copy)");
        copy.setNotes(this.notes);
        copy.setShotType(this.shotType);
        copy.setCameraAngle(this.cameraAngle);
        copy.setDialogue(this.dialogue);
        copy.setAction(this.action);
        copy.setCameraMovement(this.cameraMovement);
        copy.setDurationSeconds(this.durationSeconds);
        copy.setKeyFrame(this.isKeyFrame);
        copy.setTransitionType(this.transitionType);
        copy.setAudioNotes(this.audioNotes);

        // ✨ NEW: Copy Phase 1 fields
        copy.setDescriptionRichText(this.descriptionRichText);
        copy.setDescriptionPlainText(this.descriptionPlainText);
        copy.setCanvasBackgroundColor(this.canvasBackgroundColor);
        copy.setDisplayDuration(this.displayDuration);

        // Deep copy canvas image data
        if (this.canvasImageData != null && this.canvasImageData.length > 0) {
            byte[] copiedCanvasData = new byte[this.canvasImageData.length];
            System.arraycopy(this.canvasImageData, 0, copiedCanvasData, 0, this.canvasImageData.length);
            copy.setCanvasImageData(copiedCanvasData);
        }

        // Deep copy thumbnail data
        if (this.thumbnailData != null && this.thumbnailData.length > 0) {
            byte[] copiedThumbnailData = new byte[this.thumbnailData.length];
            System.arraycopy(this.thumbnailData, 0, copiedThumbnailData, 0, this.thumbnailData.length);
            copy.setThumbnailData(copiedThumbnailData);
        }

        // Copy drawing elements if any
        if (this.elements != null && !this.elements.isEmpty()) {
            copy.setElements(new ArrayList<>());
            for (DrawingElement element : this.elements) {
                // Note: You'd need to implement a copy method for DrawingElement too
                // For now, we'll skip this as the main drawing data is in canvasImageData
            }
        }

        return copy;
    }

    // ===== PHASE 1 FEATURE INDICATORS =====

    /**
     * Get summary of Phase 1 features for this panel
     */
    public String getPhase1FeatureSummary() {
        StringBuilder summary = new StringBuilder();

        if (hasRichTextDescription()) {
            summary.append("Rich Text Description, ");
        }
        if (!canvasBackgroundColor.equals("#FFFFFF")) {
            summary.append("Custom Background (").append(canvasBackgroundColor).append("), ");
        }
        if (displayDuration != null && displayDuration.toSeconds() != 3.0) {
            summary.append("Custom Timing (").append(getFormattedDisplayDuration()).append("), ");
        }

        if (summary.length() == 0) {
            return "No Phase 1 features";
        }

        // Remove trailing comma and space
        return summary.substring(0, summary.length() - 2);
    }

    /**
     * Check if panel is ready for video export (has all required data)
     */
    public boolean isReadyForVideoExport() {
        return hasDrawingData() || hasRichTextDescription();
    }

    /**
     * Get panel statistics for UI display
     */
    public String getPanelStats() {
        return String.format("Created: %s, Modified: %s, %s",
                createdDate.toLocalDate().toString(),
                modifiedDate.toLocalDate().toString(),
                getPhase1FeatureSummary());
    }
}