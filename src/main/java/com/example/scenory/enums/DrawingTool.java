package com.example.scenory.enums;

public enum DrawingTool {
    PEN("Pen"),
    BRUSH("Brush"),
    ERASER("Eraser"),
    RECTANGLE("Rectangle"),
    CIRCLE("Circle"),
    LINE("Line"),
    TEXT("Text");

    private final String displayName;

    DrawingTool(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}