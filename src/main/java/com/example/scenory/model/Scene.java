package com.example.scenory.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Scene {
    private String id;
    private String name;
    private String description;
    private int sequenceOrder;
    private List<Panel> panels;

    public Scene() {
        this.id = UUID.randomUUID().toString();
        this.panels = new ArrayList<>();
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}
