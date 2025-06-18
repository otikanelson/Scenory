package com.example.scenory.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ModalController {

    @FXML private Label modalTitle;
    @FXML private Label modalContent;
    @FXML private TextArea modalTextArea;
    @FXML private Button closeButton;

    public void initialize() {
        // Setup modal behavior
    }

    @FXML
    private void closeModal() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    public void setTitle(String title) {
        if (modalTitle != null) {
            modalTitle.setText(title);
        }
    }

    public void setContent(String content) {
        if (modalContent != null) {
            modalContent.setText(content);
        }
        if (modalTextArea != null) {
            modalTextArea.setText(content);
        }
    }
}