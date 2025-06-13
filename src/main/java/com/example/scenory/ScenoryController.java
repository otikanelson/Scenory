package com.example.scenory;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ScenoryController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}