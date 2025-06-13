package com.example.scenory.model;

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

    public Panel() {
        this.id = UUID.randomUUID().toString();
        this.elements = new ArrayList<>();
    }

    // Getters and Setters
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
    }

    public int getSequenceOrder() {
        return sequenceOrder;
    }

    public void setSequenceOrder(int sequenceOrder) {
        this.sequenceOrder = sequenceOrder;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getShotType() {
        return shotType;
    }

    public void setShotType(String shotType) {
        this.shotType = shotType;
    }

    public String getCameraAngle() {
        return cameraAngle;
    }

    public void setCameraAngle(String cameraAngle) {
        this.cameraAngle = cameraAngle;
    }

    public List<DrawingElement> getElements() {
        return elements;
    }

    public void setElements(List<DrawingElement> elements) {
        this.elements = elements;
    }

    public byte[] getThumbnailData() {
        return thumbnailData;
    }

    public void setThumbnailData(byte[] thumbnailData) {
        this.thumbnailData = thumbnailData;
    }

    // Override toString for ListView display
    @Override
    public String toString() {
        return name != null ? name : "Unnamed Panel";
    }
}
