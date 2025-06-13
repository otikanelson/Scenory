module com.example.scenory {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop; // For BufferedImage and file operations
    requires com.fasterxml.jackson.databind; // For JSON handling

    opens com.example.scenory to javafx.fxml;
    opens com.example.scenory.controller to javafx.fxml;
    opens com.example.scenory.model to com.fasterxml.jackson.databind;

    exports com.example.scenory;
}