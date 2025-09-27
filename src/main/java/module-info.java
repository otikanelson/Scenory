module com.example.scenory {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop; // For BufferedImage and file operations
    requires javafx.swing; // For SwingFXUtils
    requires javafx.web; // For rich text editor WebView - ESSENTIAL!

    // Database requirements
    requires java.sql;
    requires com.zaxxer.hikari;
    requires com.fasterxml.jackson.databind;

    // WebView JavaScript bridge requirement
    requires jdk.jsobject; // For JavaScript-Java communication

    // Open packages for FXML and JSON processing
    opens com.example.scenory to javafx.fxml;
    opens com.example.scenory.controller to javafx.fxml;
    opens com.example.scenory.model to javafx.fxml, com.fasterxml.jackson.databind;
    opens com.example.scenory.database to com.fasterxml.jackson.databind;
    opens com.example.scenory.view.panels to javafx.fxml;
    opens com.example.scenory.view.components to javafx.fxml;
    opens com.example.scenory.view.dialogs to javafx.fxml; // NEW: For rich text modal

    // Export packages
    exports com.example.scenory;
    exports com.example.scenory.model;
    exports com.example.scenory.controller;
    exports com.example.scenory.database;
    exports com.example.scenory.view.panels;
    exports com.example.scenory.view.components;
    exports com.example.scenory.view.dialogs; // NEW: For rich text modal
    exports com.example.scenory.enums;
    exports com.example.scenory.utils;
}